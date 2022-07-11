package metaindex.app.control.catalogdrive;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.security.Security;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.ServerAuthenticationManager;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.apache.sshd.sftp.server.AbstractSftpEventListenerAdapter;
import org.apache.sshd.sftp.server.FileHandle;
import org.apache.sshd.sftp.server.Handle;
import org.apache.sshd.sftp.server.SftpEventListener;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import toolbox.exceptions.DataProcessException;
import metaindex.data.userprofile.IUserProfileData;


public class SftpCatalogsDrive implements ICatalogsDrive {
	
	private Log log = LogFactory.getLog(SftpCatalogsDrive.class);
	
	private static final String QUOTAS_WARNING_FILE_NAME = "---!!! QUOTAS EXCEEDED - Please remove some file or upgrade your plan  !!!---";
	
	private SshServer _server=null;
	private Boolean _isStarted = false;
	private Semaphore _serverLock = new Semaphore(1,true);
	private Integer _port=0;
	
	public void start() {
		try {
			_serverLock.acquire();
			// possibly need semaphore if more intensive start/stop cycles
			if (_isStarted) {
				_serverLock.release();
				return; 
			}
			_isStarted=true;	
			// not clear for now why/if we need this tempo
			// server instance is sometimes null when running this code
			// while it should be running since we executed init() method.
			Thread.sleep(1000);
			if (_server==null) {
				log.error("SFTP server not instanciated (yet), unable to start it.");
			}
			_server.start();
			log.info("Catalogs drive accessible at SFTP port '"+getPort()+"'");
			_serverLock.release();
		} catch (InterruptedException | IOException| NullPointerException e) {
			e.printStackTrace();
			_serverLock.release();
		}
		
	}
	public void stop() {
		try {
			_serverLock.acquire();
			
			if (_server==null) { return; }
			try { _server.stop();}		
			catch (Throwable t) {
				log.error("Unable to stop Drive SFTP server : "+t.getMessage());
				t.printStackTrace();
			}
			_server=null;
			_isStarted=false;
			_serverLock.release();
		} catch (InterruptedException e) {
			_server=null;
			_serverLock.release();
		}
	}	
			
	public Integer getPort() {
		if (_server==null) { return -1; }
		return _server.getPort();
	}
	
	private void updateQuotasWarningFile(ICatalog c) {
		// create a file showing that catalog is over-quotas
    	try {						
			if (!c.checkQuotasDriveOk()) {
				OutputStream quotasWarningFile = new FileOutputStream(
						c.getLocalFsFilesPath()+"/"+QUOTAS_WARNING_FILE_NAME);
				quotasWarningFile.close();
			} else {
				File quotasFile = new File(c.getLocalFsFilesPath()+"/"+QUOTAS_WARNING_FILE_NAME);
				if (quotasFile.exists()) { quotasFile.delete(); }
			}
		} catch (IOException e) {}// well what to do ... ?
	}
	
	// user drive is made of a symlink to each catalog with write or admin access 
	private FileSystemFactory buildUserDriveFs(IUserProfileData p) {
		
		try {
			File userdriveFs = new File(p.getLocalFsFilesPath());
			
			// user access might have changed between 2 connections, so we refresh it			 
			if (userdriveFs.exists()) {
				// On MacOSX (at least) corresponding folders on catalogs are changed to RO when
				// executing this command. That's why we invoke c.setLocalFsFilesPathPosixRights()
				// few lines below to reset it.
				FileUtils.deleteDirectory(userdriveFs); 
			}
			if (!userdriveFs.mkdirs()) { log.error("unable to create drive folder for user : "+userdriveFs); }
			
			Integer nbCatalogs=0;
			for (ICatalog c : p.getAccessibleCatalogs()) {
				USER_CATALOG_ACCESSRIGHTS accesRights=p.getUserCatalogAccessRights(c.getId());
				if (accesRights.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT)
			    		|| accesRights.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)) {
					
					File catalogDriveLink = new File(p.getLocalFsFilesPath()+"/"+c.getName());
					Files.createSymbolicLink(
							catalogDriveLink.toPath(), 
							new File(c.getLocalFsFilesPath()).toPath());

					// see comment up there about 'FileUtils.deleteDirectory(userdriveFs);'
					// need to force folder to proper access rights.
					c.setLocalFsFilesPathPosixRights(); 
					
					updateQuotasWarningFile(c);
					nbCatalogs++;
		    	}
			}
			
			if (nbCatalogs==0) { return null; }
			return new VirtualFileSystemFactory(userdriveFs.toPath());
		} catch (IOException e) {
			log.error("Unable to create user drive folder : "+e.getMessage());
			return null;
		}
    	
	}
	private void checkCatalogAccess(ICatalog c, IUserProfileData u, Boolean checkWriteAccess) throws IOException {
		if (c==null) { throw new IOException("Catalog does not exist (anymore?)"); }
		if (u==null) { throw new IOException("Unknown user"); }
		if (!u.isEnabled()) { throw new IOException("User not allowed to connect"); }
		USER_CATALOG_ACCESSRIGHTS access = u.getUserCatalogAccessRights(c.getId());
		
		if (!access.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)
				&& !access.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT)) {			
			if (checkWriteAccess || !access.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_READ)) {
				throw new IOException("User not allowed to access contents"); 
			}
		}
	}
	
 	public SftpCatalogsDrive(Integer catalogsSftpDrivePort) {
 		_port=catalogsSftpDrivePort;
 		init();
 	}
 	public void init() {
 		Security.addProvider(new BouncyCastleProvider());
 		
 		// ensure socket is available
 		try {
			ServerSocket s = new ServerSocket(_port);
			s.close(); 		
		} catch (IOException e) {
			log.error("Could not open drive port '"+_port+"' for catalogs SFTP access  : "+e.getMessage());
			return; 
		} 		
		
		
		_server = SshServer.setUpDefaultServer();
		  _server.setPort(_port);
		  
		  // a file where to store host RSA keys so that it can be reused, avoiding 
		  // user to always accept a new key at each server restart
		  SimpleGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider(
				  new File(Globals.GetMxProperty("mx.drive.sslKeyFile")).toPath());				  	
		  keyPairProvider.setAlgorithm("RSA");		  
		  _server.setKeyPairProvider(keyPairProvider);
		  
		  org.apache.sshd.core.CoreModuleProperties.WELCOME_BANNER.set(
				  _server,
				  "Welcome to MetaindeX Drive\n");
		  
		  _server.setPasswordAuthenticator(new PasswordAuthenticator() {

			    @Override
			    public boolean authenticate(String userName, String providedClearPasswd, ServerSession s) {
			    	
			    	IUserProfileData p = Globals.Get().getUsersMgr().getUserByName(userName);
			    	// unknown user
			    	if (p==null) { return false; }
			    	
			    	// forbid access to disabled users
			    	if (!p.isEnabled()) { return false; }
			    	
			    	// wrong password
			    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			    	Boolean passwdOk = bCryptPasswordEncoder.matches(providedClearPasswd,p.getPassword());
			    	if (!passwdOk) { return false; }
			    	
					FileSystemFactory fs = buildUserDriveFs(p);
					if (fs==null) { return false; }			    						    	
			       _server.setFileSystemFactory(fs);
					  
			    	return true;
			    }
			  });
		  
		  
		  ScpCommandFactory commandFactory = new ScpCommandFactory();
		  _server.setCommandFactory(commandFactory);
		  	
	       SftpSubsystemFactory sftpSubsystemFactory = new SftpSubsystemFactory();
	       // add listeners to prevent user to upload data if disc quota exceeded
	       SftpEventListener sftpEventsListener = new AbstractSftpEventListenerAdapter() {
	    	   
	    	   private Semaphore _normalizationLock = new Semaphore(1,true);
	    	   
	    	   /// store files names to be normalized
	    	   /// @see closing
	    	   private Map<String,List<File> > pendingFilesToBeNormalizedPerPath = new ConcurrentHashMap<>(); 

	    	   
	    	   /**
	    	    * Need to do file name normalization. If not, files with apparently same name can have potentially
	    	    * different binary structure of file name (utf-8 decomposition or not for special characters).
	    	    * Then when user request it by a URI, file is not found while it is apparently present in corresponding folder ...
	    	    * it is because filename binary representation does not match URI decoded by java.
	    	    * 
	    	    * The "closed" method from SftpEventListener would be a good place to rename a file once it has been
	    	    * finished to be written, but this method is apparently not invoked by Apache Mina server.
	    	    * Waiting for a better solution, we try to mimic it using "closing" method instead, where the
	    	    * parent folder is closed after files have been fully written.
	    	    * We use this as a trigger to perform file names normalization.
	    	 * @throws IOException 
	    	    */
	    	   @Override
	    	    public void closing(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
	    	       
	    		   super.closing(session,remoteHandle,localHandle);
	    		   File closingFile = localHandle.getFile().toFile(); 
	    		   if (closingFile.isFile()) {
	    			   try {
		    			   _normalizationLock.acquire();
		    			   String parentPath = closingFile.getParent(); 
		    			   if (!pendingFilesToBeNormalizedPerPath.containsKey(parentPath)) {
		    				   pendingFilesToBeNormalizedPerPath.put(parentPath,new CopyOnWriteArrayList<>());
		    			   }
		    			   
		    			   pendingFilesToBeNormalizedPerPath.get(parentPath).add(closingFile);
	    			   } catch (InterruptedException e) { e.printStackTrace(); }
    			   
	    			   _normalizationLock.release();
	    			   
	    		   } else if (closingFile.isDirectory()) {
	    			   try {
		    			   _normalizationLock.acquire();
		    			   if (!pendingFilesToBeNormalizedPerPath.containsKey(closingFile.getPath())) {
		    				   _normalizationLock.release();
		    				   return;
		    			   }
		    			   for (File f : pendingFilesToBeNormalizedPerPath.get(closingFile.getPath())) {
		    				   String curFileName =f.getPath();
		    	    		   String normalizedFileName=Normalizer.normalize(curFileName, Globals.LOCAL_USERDATA_NORMALIZATION_FORM);	    	    		  	   
	    	    			   try {
									Files.move(new File(curFileName).toPath(), new File(normalizedFileName).toPath());
								} catch (IOException e) {
									log.error("Unable to normalize name of uploaded file '"+curFileName+"' : "+e.getMessage());
								}
		    	    		   
		    			   }
		    			   pendingFilesToBeNormalizedPerPath.get(closingFile.getPath()).clear();
	    			   } catch (InterruptedException e) { e.printStackTrace(); }
	    			   _normalizationLock.release();
	    		   }
	    		   
	    		   	
	    	    }
	    	  
	    	  
	    	    @Override
	    	    public void reading(
	    	            ServerSession session, String remoteHandle, FileHandle localHandle,
	    	            long offset, byte[] data, int dataOffset, int dataLen)
	    	            throws IOException {
	    	    	super.reading(session,remoteHandle,localHandle,offset,data,dataOffset,dataLen);	    		   
	    		   	String pathArray[]=localHandle.toString().split("/");
					if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
					String catalogName=pathArray[1];
					ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
					IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
					checkCatalogAccess(c,u,false);					
	    	    }

	    	    
	    	   @Override
	    	    public void writing(ServerSession session, String remoteHandle, FileHandle localHandle,
	    	            								long offset, byte[] data, int dataOffset, int dataLenBytes)
	    	            																		throws IOException {	    		   
	    		   	super.writing(session,remoteHandle,localHandle,offset,data,dataOffset,dataLenBytes);	    		   
	    		   	String pathArray[]=localHandle.toString().split("/");
					if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
					String catalogName=pathArray[1];
					ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
					IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
					checkCatalogAccess(c,u,true);
					if (!c.checkQuotasDriveOk()) { throw new IOException("Catalog out of quota, operation forbidded"); }					
	    	    }	    	   

	    	    @Override
	    	    public void creating(ServerSession session, Path path, Map<String, ?> attrs)
	    	            throws IOException {
	    	    	
	    	    	super.creating(session,path,attrs);	    	    	
	    	    	String pathArray[]=path.toString().split("/");
		    		if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
		    		String catalogName=pathArray[1];
		    		ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
		    		IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
		    		checkCatalogAccess(c,u,true);
		    		if (!c.checkQuotasDriveOk()) { throw new IOException("Catalog out of quota, operation forbidded"); }
	    	    }

	    	   
	    	    @Override
	    	    public void removing(ServerSession session, Path path, boolean isDirectory)
	    	            throws IOException {
	    	    	super.removing(session, path,isDirectory);
	    	    	String pathArray[]=path.toString().split("/");
					if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
					String catalogName=pathArray[1];
					ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
					IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
					checkCatalogAccess(c,u,true);
	    	    }
	    	    
	    	    @Override
	    	    public void removed(ServerSession session, Path path, boolean isDirectory, Throwable thrown)
	    	            throws IOException {
	    	    	super.removed(session, path,isDirectory,thrown);
	    	    	String pathArray[]=path.toString().split("/");
					if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
					String catalogName=pathArray[1];
					ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
					updateQuotasWarningFile(c);
	    	    }
	    	 
	    	    @Override
	    	    public void moving(ServerSession session, Path srcPath, Path dstPath, Collection<CopyOption> opts)
	    	            throws IOException {
	    	    	super.moving(session,srcPath,dstPath,opts);
	    	    	if (srcPath.toString().split("/").length<3
	    	    	 || dstPath.toString().split("/").length<3) { throw new IOException("Forbidden to modify root folder"); }
		    		
	    	    	IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
					String srcCatalogName=srcPath.toString().split("/")[1];
	    	    	String dstCatalogName=dstPath.toString().split("/")[1];
					ICatalog cSrc = Globals.Get().getCatalogsMgr().getCatalog(srcCatalogName);
					ICatalog cDst = Globals.Get().getCatalogsMgr().getCatalog(dstCatalogName);
					checkCatalogAccess(cSrc,u,true);
					checkCatalogAccess(cDst,u,true);
					if (!cDst.checkQuotasDriveOk()) { throw new IOException("Catalog out of quota, operation forbidded"); }
	    	    }
	    	    
	    	    @Override
	    	    public void linking(ServerSession session, Path source, Path target, boolean symLink)
	    	            throws IOException {
	    	        super.linking(session, source, target,symLink);
	    	        throw new IOException("Operation forbidden"); 
	    	    }
	    	    
	    	    @Override
	    	    public void modifyingAttributes(ServerSession session, Path path, Map<String, ?> attrs)
	    	            throws IOException {
	    	    	super.modifyingAttributes(session, path, attrs);
	    	    	String pathArray[]=path.toString().split("/");
					if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
					String catalogName=pathArray[1];
					ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
					IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
					checkCatalogAccess(c,u,true);
	    	    }
	    	    
	       };
	   
	      sftpSubsystemFactory.addSftpEventListener(sftpEventsListener);
	    
		  List<SubsystemFactory> namedFactoryList = new ArrayList<SubsystemFactory>();
		  namedFactoryList.add(sftpSubsystemFactory);
		  _server.setSubsystemFactories(namedFactoryList);
	
	}
}