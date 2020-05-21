package metaindex.app.control.ftpserver;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;


public class CatalogFtpServer {
	
	private Log log = LogFactory.getLog(CatalogFtpServer.class);
	
	private static final Integer FTPSERVER_TIMEOUT_SEC=3000;
	private ICatalog _catalog=null;
	
	private FtpServerFactory _serverFactory=null;
	private FtpServer _server=null;
	private Boolean _isStarted = false;
	private Semaphore _serverLock = new Semaphore(1,true);
	
	public void setUser(IUserProfileData p,Boolean enabled) {
		
		BaseUser newUser = new BaseUser();
        newUser.setName(p.getName());
        newUser.setPassword(p.getPassword());
                
        // set (and create if needed) local-system folder storing userdata files
        File directory = new File(_catalog.getLocalFsFilesPath());
        if (! directory.exists()){ 
        	log.error("Userdata folder does not exist, unable to allow FTP connection : "+_catalog.getLocalFsFilesPath());
        	newUser.setEnabled(false);
        	return;
        }
        newUser.setHomeDirectory(_catalog.getLocalFsFilesPath());
                
        List<Authority> authorities = new ArrayList<Authority>();
        if (p.getUserCatalogAccessRights(_catalog.getId()).equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT)
        	|| p.getUserCatalogAccessRights(_catalog.getId()).equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN) ){
        		authorities.add(new WritePermission());
        		newUser.setEnabled(enabled);
    	} else {
    		newUser.setEnabled(false);
    		
    	}
        newUser.setAuthorities(authorities);
        //Save the user to the user list on the file-system
        try { 
        	log.debug("Activated user "+p.getName());
        	_serverFactory.getUserManager().save(newUser); 
        } catch (FtpException e) { e.printStackTrace(); }
                
	}
		
	public void start() throws FtpException  {
		try {
			_serverLock.acquire();
			// possibly need semaphore if more intensive start/stop cycles
			if (_isStarted) {
				_serverLock.release();
				return; 
			}
			_isStarted=true;
			if (_server==null) {
				_serverLock.release();
				return; 
			}
			if (_server.isSuspended()) {
				_server.resume(); 
				_serverLock.release();
				return;
			}		
			
			_server.start();
			log.info("FTP server for catalog '"+_catalog.getName()+"' accessible at port '"+getPort()+"'");
			_serverLock.release();
		} catch (InterruptedException e) {
			_server=null;
			_serverLock.release();
		}
		
	}
	public void stop() throws FtpException  {
		try {
			_serverLock.acquire();
			
			if (_server==null) { return; }
			try { _server.stop(); } 
			catch (UnsupportedOperationException e) {
				// this throws an UnsupportedOperationException
				// don't know yet why. Possibly mis-use
				// of FtpServer/Ftplet life cycle
				//
				// Possible Memory leak here
				log.warn("ignored exception while stoping FTP server");
			}			
			catch (Throwable t) {
				log.error("Unable to stop FTP server : "+t.getMessage());
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
		return _serverFactory.getListener("default").getPort();
	}
 	public CatalogFtpServer(ICatalog c) {

 		java.util.logging.Logger jul = java.util.logging.Logger.getLogger("org.apache.ftpserver.listener.nio.FtpLoggingFilter");
	    jul.setLevel(java.util.logging.Level.SEVERE);
	    
 		// try to find a port available for FTP server of this catalog
 		Integer ftpPortRangeLow = new Integer(Globals.GetMxProperty("mx.ftp.port.range_low"));
 		Integer ftpPortRangeHigh = new Integer(Globals.GetMxProperty("mx.ftp.port.range_high")); 		
 		
 		String ftpPassivePortRangeLow = Globals.GetMxProperty("mx.ftp.passive.range_low");
 		String ftpPassivePortRangeHigh = Globals.GetMxProperty("mx.ftp.passive.range_high"); 		
 		
 		Integer catalogFtpPort = c.getFtpPort(); 		
 		
 		if (catalogFtpPort<ftpPortRangeLow || catalogFtpPort>ftpPortRangeHigh) {
 			log.error("FTP port '"+catalogFtpPort+"' for catalog "+c.getName()
 				+" is out of range "+ftpPortRangeLow+"-"+ftpPortRangeHigh
 				+", unable to start FTP userdata access.");
 			return;
 		}
		// ensure socket is available
		try {
			ServerSocket s = new ServerSocket(catalogFtpPort);
			s.close(); 		
		} catch (IOException e) {
			log.error("FTP port '"+catalogFtpPort+"' for catalog "+c.getName()
				+" could not be opened, unable to start FTP userdata access : "+e.getMessage());
			return; 
		} 		
		
		_catalog=c;
		_serverFactory = new FtpServerFactory();
		ListenerFactory listenersFactory = new ListenerFactory();
		DataConnectionConfigurationFactory dataConnectionsFactory = new DataConnectionConfigurationFactory();
		
		listenersFactory.setPort(catalogFtpPort);
		listenersFactory.setIdleTimeout(FTPSERVER_TIMEOUT_SEC);
		dataConnectionsFactory.setPassivePorts(ftpPassivePortRangeLow+"-"+ftpPassivePortRangeHigh);
		
		SslConfigurationFactory ssl = new SslConfigurationFactory();
	    ssl.setKeystoreFile(new File(Globals.GetMxProperty("mx.ssl.keystore.file")));
		ssl.setKeystorePassword(Globals.GetMxProperty("mx.ssl.keystore.password"));				
	    listenersFactory.setSslConfiguration(ssl.createSslConfiguration());
	    listenersFactory.setDataConnectionConfiguration(dataConnectionsFactory.createDataConnectionConfiguration());
	    
	    Listener mxListener = listenersFactory.createListener();
		_serverFactory.addListener( "default", mxListener );
		
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		// props file created on the fly, does not exist in the project initial conf
		// storing local data
	    userManagerFactory.setFile(new File("ftpusers.properties"));
		userManagerFactory.setPasswordEncryptor(new PasswordEncryptor() {

            @Override
            public String encrypt(String password) {
            	// password already encrypted in DB
            	return password; 		                
            }

            @Override
            public boolean matches(String passwordToCheck, String storedPassword) {
            	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            	return bCryptPasswordEncoder.matches(passwordToCheck, storedPassword);		            	
            }
        });
		
		UserManager um = userManagerFactory.createUserManager();
		_serverFactory.setUserManager(um);		
		
		_serverFactory.setFtplets(Collections.singletonMap("MxFtplet", new MxFtplet(_catalog)));
		_server= _serverFactory.createServer();

		
	}
}