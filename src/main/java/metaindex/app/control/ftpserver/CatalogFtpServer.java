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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.DefaultFtplet;
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
	
	private class MxFtplet extends DefaultFtplet {

		@Override
		public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply)
				throws FtpException, IOException {
			
			// for operation uploading data, check quotas
			if (request.getCommand().equals("APPE")
					|| request.getCommand().equals("MKD")
					|| request.getCommand().equals("STOR")
					|| request.getCommand().equals("STOU")
					|| request.getCommand().equals("XMKD")
			   ) {
				
				// quota exceeded, operation forbiden
				if (!_catalog.checkQuotasDisckSpaceOk()) {

					session.write(new DefaultFtpReply(552, 
							"Disk quota exceeded for catalog '"+_catalog.getName()+"'"));
					
					
					// TODO if operation was file upload, delete the file which is too big for
					// remaining quota
					return FtpletResult.SKIP;
				}
			}
			
			return FtpletResult.DEFAULT;
		}
	

		@Override
		public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {
			
			//log.error("#### Ftplet beforeCommand from '"+arg0.getUser()+"' : '"+arg1.getCommand()+"' / '"+arg1.getArgument()+"'");
			
			try { 
				
				// allowed anonymous commands
				if (session.getUser()==null && (
						   request.getCommand().equals("AUTH")
						|| request.getCommand().equals("USER")
						|| request.getCommand().equals("PASS")
					)) {
					
					return FtpletResult.DEFAULT;
				}
				else {
					String name = session.getUser().getName();
					IUserProfileData user = Globals.Get().getUsersMgr().getUserByName(session.getUser().getName());
					if (user==null
							|| !user.isLoggedIn()
							|| user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.NONE
							// no ftp access for Read-Only users
							|| user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_READ) {
						
						session.write(new DefaultFtpReply(550, 
								"Unauthorized user '"+name+"' for catalog '"+_catalog.getName()+"'"));
						return FtpletResult.DISCONNECT;
					}
					/*
					log.info("User:"+user.getName()
						+" Rights:"+user.getUserCatalogAccessRights(_catalog.getId()).toString()
						+" Cmd:"+arg1.getCommand()+" -> "+arg1.getArgument());
					*/
					// Read-only operations, we separate them from edit operations for clarity
					if (	(user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT
								|| user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)
							&&
							(
									request.getCommand().equals("OPTS")
									|| request.getCommand().equals("FEAT")
									|| request.getCommand().equals("PASV")
									|| request.getCommand().equals("MDTM")
									|| request.getCommand().equals("PBSZ")
									|| request.getCommand().equals("PROT")
									|| request.getCommand().equals("TYPE")
									|| request.getCommand().equals("EPSV")
									|| request.getCommand().equals("CWD")
									|| request.getCommand().equals("LIST")
									|| request.getCommand().equals("CWD")
									|| request.getCommand().equals("MLSD")
									|| request.getCommand().equals("MLST")
									|| request.getCommand().equals("NLST")
									|| request.getCommand().equals("NOOP")
									|| request.getCommand().equals("PWD")
									|| request.getCommand().equals("QUIT")
									|| request.getCommand().equals("REIN")
									|| request.getCommand().equals("SIZE")
									|| request.getCommand().equals("STAT")
									|| request.getCommand().equals("SYST")
									|| request.getCommand().equals("XPWD")
									|| request.getCommand().equals("MLST")	
									|| request.getCommand().equals("REST")
							)) {
						return FtpletResult.DEFAULT;
					}
					
					// Edit operations
					if (	(user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT
								|| user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN) 
							&&
							(
									request.getCommand().equals("APPE")
									|| request.getCommand().equals("CDUP")
									|| request.getCommand().equals("MKD")
									|| request.getCommand().equals("RETR")
									|| request.getCommand().equals("RNFR")
									|| request.getCommand().equals("RNTO")
									|| request.getCommand().equals("STOR")
									|| request.getCommand().equals("STOU")
									|| request.getCommand().equals("XCUP")
									|| request.getCommand().equals("XMKD")
									
									
																									
							)) {
						
						// for operation uploading data, check quotas
						if (request.getCommand().equals("APPE")
								|| request.getCommand().equals("MKD")
								|| request.getCommand().equals("STOR")
								|| request.getCommand().equals("STOU")
								|| request.getCommand().equals("XMKD")
						   ) {
							
							// quota exceeded, operation forbiden
							if (!_catalog.checkQuotasDisckSpaceOk()) {
								session.write(new DefaultFtpReply(552, 
										"Quota exceeded for catalog '"+_catalog.getName()+"',"
																		+" please delete some files or ask your system administrator"
																		+" to increase quota allocated to catalog."));
								return FtpletResult.SKIP;
							}
						}
						
						return FtpletResult.DEFAULT;
					}
					
					// Admin operations
					if (	(user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN) 
							&&
							(
									request.getCommand().equals("DELE")
									|| request.getCommand().equals("RMD")
									|| request.getCommand().equals("XRMD")																																		
							)) {
						
						
						return FtpletResult.DEFAULT;
					}
					
				}
			} catch (Exception e) { 
				e.printStackTrace();
				
			}
			session.write(new DefaultFtpReply(502, "Operation not implemented"));
			return FtpletResult.SKIP;			
		}

	}
	private Log log = LogFactory.getLog(CatalogFtpServer.class);
	
	private static final Integer FTPSERVER_TIMEOUT_SEC=3000;
	private ICatalog _catalog=null;
	
	private FtpServerFactory _serverFactory=null;
	private FtpServer _server=null;
	private Boolean _isStarted = false;
	
	public void setUser(IUserProfileData p,Boolean enabled) {
		
		BaseUser newUser = new BaseUser();
        newUser.setName(p.getName());
        newUser.setPassword(p.getPassword());
                
        // set (and create if needed) local-system folder storing ftp files
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
		// possibly need semaphore if more intensive start/stop cycles
		if (_isStarted) { return; }
		_isStarted=true;
		if (_server==null) { return; }
		if (_server.isSuspended()) { _server.resume(); return;}		
		
		_server.start();
		log.info("FTP server for catalog '"+_catalog.getName()+"' accessible at port '"+getPort()+"'");		
	}
	public void stop() throws FtpException  {
		if (_server==null) { return; }
		_server.stop();
		_isStarted=false;
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
 		
 		Integer catalogFtpPort = ftpPortRangeLow; 		
 		Boolean isPortValid=false;
 		
		while (!isPortValid && catalogFtpPort<=ftpPortRangeHigh) {			
			try {
 				ServerSocket s = new ServerSocket(catalogFtpPort);
 				isPortValid=true;
 				s.close();
 				break;
 			} catch (IOException e) { isPortValid=false; } 		
			catalogFtpPort++;
			
		} 				
		
		_catalog=c;
		_serverFactory = new FtpServerFactory();
		ListenerFactory listenersFactory = new ListenerFactory();
		DataConnectionConfigurationFactory dataConnectionsFactory = new DataConnectionConfigurationFactory();
		if (!isPortValid) {
			log.error("No available FTP port could be retrieved for FTP server of catalog "+c.getName()+" within range "
						+ftpPortRangeLow+"-"+ftpPortRangeHigh);
			return;
		}
		
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
		// tmp props file created on the fly, does not exist in the project initial conf
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
		
		_serverFactory.setFtplets(Collections.singletonMap("MxFtplet", new MxFtplet()));
		_server= _serverFactory.createServer();
		
		
	}
}