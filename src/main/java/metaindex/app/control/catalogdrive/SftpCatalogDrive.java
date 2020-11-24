package metaindex.app.control.catalogdrive;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.common.PropertyResolverUtils;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.scp.ScpHelper;
import org.apache.sshd.common.util.GenericUtils;
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
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;


public class SftpCatalogDrive implements ICatalogDrive {
	
	private Log log = LogFactory.getLog(SftpCatalogDrive.class);
	
	private static final String QUOTAS_WARNING_FILE_NAME = "---!!! QUOTAS EXCEEDED - Please remove some file or upgrade your plan  !!!---";
	
	private ICatalog _catalog=null;
	
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
			log.info("Drive for catalog '"+_catalog.getName()+"' accessible at SFTP port '"+getPort()+"'");
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
 	public SftpCatalogDrive(ICatalog c) {
 		
 		_catalog=c;
 		
 		Security.addProvider(new BouncyCastleProvider());
 		
 		// try to find a port available for drive server of this catalog
 		Integer drivePortRangeLow = new Integer(Globals.GetMxProperty("mx.drive.port.range_low"));
 		Integer drivePortRangeHigh = new Integer(Globals.GetMxProperty("mx.drive.port.range_high")); 		

 		Integer catalogDrivePort = _catalog.getDrivePort(); 		
 		
 		if (catalogDrivePort<drivePortRangeLow || catalogDrivePort>drivePortRangeHigh) {
 			log.error("Drive port '"+catalogDrivePort+"' for catalog "+c.getName()
 				+" is out of range "+drivePortRangeLow+"-"+drivePortRangeHigh
 				+", unable to start SFTP userdata access.");
 			return;
 		}
		// ensure socket is available
 		try {
			ServerSocket s = new ServerSocket(catalogDrivePort);
			s.close(); 		
		} catch (IOException e) {
			log.error("Drive port '"+catalogDrivePort+"' for catalog "+c.getName()
				+" could not be opened, unable to start SFTP userdata access : "+e.getMessage());
			return; 
		} 		
		
		
		_server = SshServer.setUpDefaultServer();
		  _server.setPort(_catalog.getDrivePort());
		  
		  // a file where to store host RSA keys so that it can be reused, avoiding 
		  // user to always accept a new key at each server restart
		  SimpleGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider(
				  new File(Globals.GetMxProperty("mx.drive.sslKeyFile")).toPath());				  	
		  keyPairProvider.setAlgorithm("RSA");		  
		  _server.setKeyPairProvider(keyPairProvider);
		  
		  PropertyResolverUtils.updateProperty(_server,
				  	ServerAuthenticationManager.WELCOME_BANNER,
				  	"Welcome to MetaindeX Catalog ### "+_catalog.getName()+" ###");
		  
		  _server.setPasswordAuthenticator(new PasswordAuthenticator() {

			    @Override
			    public boolean authenticate(String userName, String providedClearPasswd, ServerSession s) {
			    	
			    	IUserProfileData p = Globals.Get().getUsersMgr().getUserByName(userName);
			    	// unknown user
			    	if (p==null) { return false; }
			    	
			    	// wrong password
			    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			    	Boolean passwdOk = bCryptPasswordEncoder.matches(providedClearPasswd,p.getPassword());
			    	if (!passwdOk) { return false; }
			    	
			    	USER_CATALOG_ACCESSRIGHTS accesRights=p.getUserCatalogAccessRights(_catalog.getId());
			    	
			    	// ensure user has write access to the catalog
			    	if (!accesRights.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT)
			    		&& !accesRights.equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)) {
			    		return false;
			    	}
			    		
			    	// create a file showing the name of opened catalog
			    	// help user to be sure of what catalog is he viewing
			    	try {
						OutputStream catalogNameFile = new FileOutputStream(
								_catalog.getLocalFsFilesPath()+"/"+"---### MetaindeX Catalog ["+_catalog.getName()+"] ###---");
						catalogNameFile.close();
						
						if (!_catalog.checkQuotasDriveOk()) {
							OutputStream quotasWarningFile = new FileOutputStream(
									_catalog.getLocalFsFilesPath()+"/"+QUOTAS_WARNING_FILE_NAME);
							quotasWarningFile.close();
						} else {
							File quotasFile = new File(_catalog.getLocalFsFilesPath()+"/"+QUOTAS_WARNING_FILE_NAME);
							if (quotasFile.exists()) { quotasFile.delete(); }
						}
					} catch (IOException e) {/* do nothing special */}
					
			    	ScpCommandFactory commandFactory = new ScpCommandFactory() {
			    		 @Override
		    		    public boolean isSupportedCommand(String command) {
		    		        if (GenericUtils.isEmpty(command)) {
		    		            return false;
		    		        }

		    		        return command.startsWith(ScpHelper.SCP_COMMAND_PREFIX);
		    		    }
			    	};
					_server.setCommandFactory(commandFactory);
					  
			    	return true;
			    }
			  });
		  
		  
		  ScpCommandFactory commandFactory = new ScpCommandFactory();
		  _server.setCommandFactory(commandFactory);

		  VirtualFileSystemFactory vfs = 
	    			new VirtualFileSystemFactory(new File(_catalog.getLocalFsFilesPath()).toPath());			    	
	       _server.setFileSystemFactory(vfs);
			
	       SftpSubsystemFactory sftpSubsystemFactory = new SftpSubsystemFactory();
	       // add listeners to prevent user to upload data if disc quota exceeded
	       SftpEventListener sftpEventsListener = new AbstractSftpEventListenerAdapter() {
	    	   
	    	   @Override
	    	    public void writing(ServerSession session, String remoteHandle, FileHandle localHandle,
	    	            								long offset, byte[] data, int dataOffset, int dataLenBytes)
	    	            																		throws IOException {
	    		   super.writing(session,remoteHandle,localHandle,offset,data,dataOffset,dataLenBytes);
	    	       if (!_catalog.checkQuotasDriveOk()) {
	    	    	 String userName = session.getUsername();
	    	    	 IUserProfileData p = Globals.Get().getUsersMgr().getUserByName(userName);
	    	    	 session.disconnect(11, p.getText("Drive.quotaExceeded", _catalog.getName()));	    	    	    	    	 
	    	       }
	    	    }	    	   

	    	    @Override
	    	    public void creating(ServerSession session, Path path, Map<String, ?> attrs)
	    	            throws IOException {
	    	    	super.creating(session,path,attrs);
	    	    	if (!_catalog.checkQuotasDriveOk()) {
	    	    	 String userName = session.getUsername();
	    	    	 IUserProfileData p = Globals.Get().getUsersMgr().getUserByName(userName);
	    	    	 session.disconnect(11, p.getText("Drive.quotaExceeded", _catalog.getName()));
	    	    	 throw new IOException( p.getText("Drive.quotaExceeded", _catalog.getName()));
	    	        }	    	        
	    	    }
	    	    
	    	    @Override
	    	    public void removed(ServerSession session, Path path, boolean isDirectory, Throwable thrown)
	    	            throws IOException {
	    	    	super.removed(session,path,isDirectory,thrown);
	    	    	
	    	    	if (_catalog.checkQuotasDriveOk()) {
		    	    	File quotasFile = new File(_catalog.getLocalFsFilesPath()+"/"+QUOTAS_WARNING_FILE_NAME);
						if (quotasFile.exists()) { quotasFile.delete(); }
	    	    	}
	    	    }

	       };
	   
	      sftpSubsystemFactory.addSftpEventListener(sftpEventsListener);
	    
		  List<SubsystemFactory> namedFactoryList = new ArrayList<SubsystemFactory>();
		  namedFactoryList.add(sftpSubsystemFactory);
		  _server.setSubsystemFactories(namedFactoryList);
	
	}
}