package metaindex.app.control.ftpserver;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpletResult;

import metaindex.app.Globals;
import metaindex.app.periodic.statistics.catalog.FtpUploadMxStat;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;

import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;


public class MxFtplet extends DefaultFtplet {
	
	private Log log = LogFactory.getLog(MxFtplet.class);
	private ICatalog _catalog;
	
	public MxFtplet(ICatalog c) {
		_catalog=c;
	}
    
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
			
			// quota exceeded, operation canceled (and file deleted)
			if (!_catalog.checkQuotasDisckSpaceOk()) {

				session.write(new DefaultFtpReply(FtpReply.REPLY_552_REQUESTED_FILE_ACTION_ABORTED_EXCEEDED_STORAGE, 
						"Disk quota exceeded for catalog '"+_catalog.getName()+"'"));
				
				FileSystemView fsview = session.getFileSystemView();
				FtpFile cwd = fsview.getWorkingDirectory();
				String absoluteFsFilePath=_catalog.getLocalFsFilesPath()+cwd.getAbsolutePath()+"/"+request.getArgument();
				File userdataFile = new File(absoluteFsFilePath);
				if (userdataFile.exists()) {
					if (!userdataFile.delete()) {
						log.error("unable to delete local userdata content : "+absoluteFsFilePath);
					}
				}

				return FtpletResult.DISCONNECT;
			}
		}
		
		
		return super.afterCommand(session,request,reply);
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
				
				return super.beforeCommand(session,request);
			}
			else {
				String name = session.getUser().getName();
				IUserProfileData user = Globals.Get().getUsersMgr().getUserByName(session.getUser().getName());
				if (user==null
						|| user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.NONE
						// no ftp access for Read-Only users
						|| user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_READ
						// user or catalog disabled
						|| user.isEnabled()==false
						|| _catalog.isEnabled()==false) {
					
					session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, 
							"Unauthorized user '"+name+"' for catalog '"+_catalog.getName()+"'"));
					return FtpletResult.SKIP;
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
					return super.beforeCommand(session,request);
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
							session.write(new DefaultFtpReply(FtpReply.REPLY_552_REQUESTED_FILE_ACTION_ABORTED_EXCEEDED_STORAGE, 
									"Quota exceeded for catalog '"+_catalog.getName()+"',"
																	+" please delete some files or ask your system administrator"
																	+" to increase quota allocated to catalog."));
							return FtpletResult.SKIP;
						}
					}
					
					if (request.getCommand().equals("APPE")
							|| request.getCommand().equals("STOR")
							|| request.getCommand().equals("STOU")) {
						Globals.GetStatsMgr().handleStatItem(new FtpUploadMxStat(user,_catalog));
					}
					
					return super.beforeCommand(session,request);
				}
				
				// Admin operations
				if (	(user.getUserCatalogAccessRights(_catalog.getId())==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN) 
						&&
						(
								request.getCommand().equals("DELE")
								|| request.getCommand().equals("RMD")
								|| request.getCommand().equals("XRMD")																																		
						)) {
					
					
					return super.beforeCommand(session,request);
				}
				
			}
		} catch (Exception e) { 
			e.printStackTrace();
			
		}
		session.write(new DefaultFtpReply(FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN, "Operation not implemented"));
		return FtpletResult.SKIP;			
	}

}
