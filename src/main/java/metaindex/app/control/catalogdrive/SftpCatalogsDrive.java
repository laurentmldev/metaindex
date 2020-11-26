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
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.apache.sshd.server.subsystem.sftp.AbstractSftpEventListenerAdapter;
import org.apache.sshd.server.subsystem.sftp.FileHandle;
import org.apache.sshd.server.subsystem.sftp.SftpEventListener;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;


public class SftpCatalogsDrive implements ICatalogsDrive {
	
	private Log log = LogFactory.getLog(SftpCatalogsDrive.class);
	
	private static final String QUOTAS_WARNING_FILE_NAME = "---!!! QUOTAS EXCEEDED - Please remove some file or upgrade your plan  !!!---";
	
	private SshServer _server=null;
	private Boolean _isStarted = false;
	private Semaphore _serverLock = new Semaphore(1,true);
	
	public void start() {
		try {
			_serverLock.acquire();
			// possibly need semaphore if more intensive start/stop cycles
			if (_isStarted) {
				_serverLock.release();
				return; 
			}
			_isStarted=true;		
			_server.start();
			log.info("Catalogs drive accessible at SFTP port '"+getPort()+"'");
			_serverLock.release();
		} catch (InterruptedException | IOException e) {
			//_server=null;
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
	
	
	// user drive is made of a symlink to each catalog with write or admin access 
	private FileSystemFactory buildUserDriveFs(IUserProfileData p) {
		
		try {
			File userdriveFs = new File(p.getLocalFsFilesPath());
			
			// user access might have changed between 2 connections, so we refresh it
			if (userdriveFs.exists()) { FileUtils.deleteDirectory(userdriveFs); }
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
	private void checkCatalogModifyDataAllowed(ICatalog c, IUserProfileData u) throws IOException {
		if (c==null) { throw new IOException("Catalog does not exist (anymore?)"); }
		if (u==null) { throw new IOException("Unknown user"); }
		if (!u.isEnabled()) { throw new IOException("User not allowed to connect"); }
		USER_CATALOG_ACCESSRIGHTS access = u.getUserCatalogAccessRights(c.getId());
		if (!access.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)
				&& !access.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT)) {
			throw new IOException("User not allowed to access contents"); 
		}		
	}
 	public SftpCatalogsDrive(Integer catalogsSftpDrivePort) {
 		
 		Security.addProvider(new BouncyCastleProvider());
 		
 		// ensure socket is available
 		try {
			ServerSocket s = new ServerSocket(catalogsSftpDrivePort);
			s.close(); 		
		} catch (IOException e) {
			log.error("Could not open drive port '"+catalogsSftpDrivePort+"' for catalogs SFTP access  : "+e.getMessage());
			return; 
		} 		
		
		
		_server = SshServer.setUpDefaultServer();
		  _server.setPort(catalogsSftpDrivePort);
		  
		  // a file where to store host RSA keys so that it can be reused, avoiding 
		  // user to always accept a new key at each server restart
		  SimpleGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider(
				  new File(Globals.GetMxProperty("mx.drive.sslKeyFile")).toPath());				  	
		  keyPairProvider.setAlgorithm("RSA");		  
		  _server.setKeyPairProvider(keyPairProvider);
		  
		  PropertyResolverUtils.updateProperty(_server,
				  	ServerAuthenticationManager.WELCOME_BANNER,
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
					checkCatalogModifyDataAllowed(c,u);
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
		    		checkCatalogModifyDataAllowed(c,u);
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
					checkCatalogModifyDataAllowed(c,u);
	    	    }
	    	    
	    	    @Override
	    	    public void removed(ServerSession session, Path path, boolean isDirectory, Throwable thrown)
	    	            throws IOException {
	    	    	super.removed(session, path,isDirectory,thrown);
	    	    	String pathArray[]=path.toString().split("/");
					if (pathArray.length<3) { throw new IOException("Forbidden to modify root folder"); }
					String catalogName=pathArray[1];
					ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catalogName);
					IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(session.getUsername());
					checkCatalogModifyDataAllowed(c,u);
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
					checkCatalogModifyDataAllowed(cSrc,u);
					checkCatalogModifyDataAllowed(cDst,u);
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
					checkCatalogModifyDataAllowed(c,u);
	    	    }
	    	    
	       };
	   
	      sftpSubsystemFactory.addSftpEventListener(sftpEventsListener);
	    
		  List<SubsystemFactory> namedFactoryList = new ArrayList<SubsystemFactory>();
		  namedFactoryList.add(sftpSubsystemFactory);
		  _server.setSubsystemFactories(namedFactoryList);
	
	}
}