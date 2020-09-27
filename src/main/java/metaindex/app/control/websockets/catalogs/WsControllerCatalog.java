package metaindex.app.control.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>
*/

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.catalogs.messages.*;
import metaindex.app.control.websockets.catalogs.messages.WsMsgCatalogUsers_answer.GuiCatalogUser;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.app.periodic.statistics.catalog.CreateCatalogMxStat;
import metaindex.app.periodic.statistics.catalog.DeleteCatalogMxStat;
import metaindex.app.periodic.statistics.catalog.SetCustoCatalogMxStat;
import metaindex.app.periodic.statistics.catalog.UpdateLexicCatalogMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.dbinterface.CreateIndexIntoEsDbStmt.IndexAlreadyExistException;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.ICatalogUser;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.utils.StrTools;
import toolbox.utils.StreamHandler;

@Controller
public class WsControllerCatalog extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerCatalog.class);
	
	private static Semaphore _GlobalCreateCatalogLock = new Semaphore(1,true);
	
	@Autowired
	public WsControllerCatalog(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
		
    @MessageMapping("/get_catalogs")
    @SubscribeMapping ( "/user/queue/catalogs")
    public void handleCatalogsRequest(SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgGetCatalogs_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	
		
    	try {
    		List<ICatalog> catalogs = Globals.Get().getCatalogsMgr().getCatalogsList();
    		List<WsMsgCatalogDetails_answer> answeredList = new ArrayList<WsMsgCatalogDetails_answer>();
    		
    		if (!this.userHasReadAccess(user)) { 
    			sendListToUser(user.getName(),"/queue/catalogs",answeredList, /* Base64 compression */ true);     
    			return;
        	}
        	
    		Iterator<ICatalog> it = catalogs.iterator();
    		while (it.hasNext()) {
	    		ICatalog curCatalog = it.next();
	    		// request catalogId=0 == retrieve all catalogs not accessible to the user
	    		if (requestMsg.getCatalogId().equals(0) 
	    				|| requestMsg.getCatalogId().equals(curCatalog.getId())) {
	    			if (user.getUserCatalogAccessRights(curCatalog.getId())!=USER_CATALOG_ACCESSRIGHTS.NONE)
	    			{ 
	    				WsMsgCatalogDetails_answer curCatAnswer = new WsMsgCatalogDetails_answer(curCatalog,user);
	    				curCatAnswer.setRequestId(requestMsg.getRequestId());
	    				curCatAnswer.setIsSuccess(true);
	    				answeredList.add(curCatAnswer);
	    			} else if (!requestMsg.getCatalogId().equals(0)) {
	    				user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));
	    				return;
	    			}
	    		}
	    		// request catalogId=-1 == retrieve all catalogs not accessible to the user
	    		else if (requestMsg.getCatalogId().equals(-1)) {
	    			if (user.getUserCatalogAccessRights(curCatalog.getId())==USER_CATALOG_ACCESSRIGHTS.NONE)
	    			{ 
	    				WsMsgCatalogDetails_answer curCatAnswer = new WsMsgCatalogShortDetails_answer(curCatalog,user);
	    				curCatAnswer.setRequestId(requestMsg.getRequestId());
	    				curCatAnswer.setIsSuccess(true);
	    				answeredList.add(curCatAnswer);
	    			}
	    		}
			}    
    		sendListToUser(user.getName(),"/queue/catalogs",answeredList, /* Base64 compression */ true); 
	    	
    	} catch (IOException e) 
    	{
    		log.error("Unable to process get_catalogs from '"+user.getName()+"' : "+e.getMessage());
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.get_catalogs"));
    		e.printStackTrace();
    	}
    }

    /**
		catalog users are retrieved separatly from catalog itself,
		because it requires more complex operations and potentially
		several DB accesses
	*/
    @MessageMapping("/get_catalog_users")
    @SubscribeMapping ( "/user/queue/catalog_users")
    public void handleCatalogUsersRequest(SimpMessageHeaderAccessor headerAccessor, 
    							WsMsgCatalogUsers_request requestMsg) throws Exception {

    	WsMsgCatalogUsers_answer answer = new WsMsgCatalogUsers_answer(requestMsg); 
    	IUserProfileData activeUser = getUserProfile(headerAccessor);	
    	ICatalog c = activeUser.getCurrentCatalog();
		if (c==null) {
			answer.setRejectMessage("Current user catalog does not match request");
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_users", answer);
			return;
		}
		if (!this.userHasWriteAccess(activeUser)) { 
			answer.setRejectMessage(activeUser.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_users", answer);
			return;
    	}
		
		// user shall be catalog admin to access details
		// of people using it (i.e. nickname, *email* and access rights ...)
		if (!activeUser.getUserCatalogAccessRights(c.getId()).equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)) {
			answer.setRejectMessage(activeUser.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_users", answer);
			return;
		}
		
		// first retrieve ids of users having access rights defined for this catalog
		List<Integer> usersIds = new ArrayList<>();
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface()
			.getCatalogUsersIdsStmt(c).execute(new StreamHandler<Integer>(usersIds));
		
		List<GuiCatalogUser> result = new ArrayList<>();
				
		for (Integer userId : usersIds) {
			IUserProfileData u = Globals.Get().getUsersMgr().getUserById(userId);
			if (u==null) {
				log.error("Unable to retrieve details of user '"+u.getId()+"' for catalog '"+c.getName()+"'");
				continue;
			}
			// skip one-self and  owner of the catalog
			if (u.getId().equals(c.getOwnerId())) { continue; }
			if (u.getId().equals(activeUser.getId())) { continue; }
			
			// ignore disabled users
			if (u.isEnabled()==false) { continue; }
			USER_CATALOG_ACCESSRIGHTS access = u.getUserCatalogAccessRights(c.getId());
			GuiCatalogUser userGuiDescr = new GuiCatalogUser();
			userGuiDescr.setName(u.getName());
			userGuiDescr.setNickname(u.getNickname());
			userGuiDescr.setId(u.getId());
			userGuiDescr.setCatalogId(c.getId());
			userGuiDescr.setCatalogAccessRights(access);
			result.add(userGuiDescr);
		}
		
		answer.setIsSuccess(true);
		answer.setUsers(result);
		
		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
												"/queue/catalog_users", 
												answer);
		
    }


    @MessageMapping("/set_catalog_user_access")
    @SubscribeMapping ( "/user/queue/catalog_user_access")
    public void handleCatalogSetUserAcccessRequest(SimpMessageHeaderAccessor headerAccessor, 
    								WsMsgSetCatalogUserAccess_request requestMsg) throws Exception {

    	WsMsgSetCatalogUserAccess_answer answer = new WsMsgSetCatalogUserAccess_answer(requestMsg); 
    	IUserProfileData activeUser = getUserProfile(headerAccessor);	
    	IUserProfileData targetUser = Globals.Get().getUsersMgr().getUserById(requestMsg.getUserId());
    	if (activeUser==null) {
    		log.error("No user matching hearderAccessor in WS set_catalog_user_access request, ingored.");
    		return; 
    	}
    	if (targetUser==null) {
    		log.error("No such target user matching requested WS operation set_catalog_user_access, ingored.");
    		return; 
    	}
    	ICatalog c = activeUser.getCurrentCatalog();
		if (c==null) {
			answer.setRejectMessage("Current user has no catalog currently open");
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_user_access", answer);
			return;
		}
		if (!c.getId().equals(requestMsg.getCatalogId())) {
			answer.setRejectMessage(activeUser.getText("Current user catalog does not match request"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_user_access", answer);
			return;
		}
		if (!this.userHasWriteAccess(activeUser)) { 
			answer.setRejectMessage(activeUser.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_user_access", answer);
			return;
    	}
		
		// user shall be catalog admin to access details
		// of people using it (i.e. nickname, *email* and access rights ...)
		if (!activeUser.getUserCatalogAccessRights(c.getId()).equals(USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN)) {
			answer.setRejectMessage(activeUser.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_user_access", answer);
			return;
		}
		
		// cannot change status of owner of the catalog
		if (targetUser.getId().equals(c.getOwnerId())) {
			answer.setRejectMessage(activeUser.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_user_access", answer);
			return;
		}
		
		Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface()
			.getSetCatalogUserAccessStmt(c,targetUser,requestMsg.getAccessRights()).execute();
		if (result==false) {
			answer.setRejectMessage("Unable to give access rights to users");
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_user_access", answer);
			return;
		}
		
		answer.setIsSuccess(true);				
		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
												"/queue/catalog_user_access", 
												answer);
		
		if (targetUser.isLoggedIn()) {
			targetUser.sendGuiInfoMessage(targetUser.getText("Catalogs.users.catalogAccessRightsChanged",
														c.getName(),requestMsg.getAccessRights().toString()));
		}
		
		targetUser.sendEmail(targetUser.getText("Catalogs.users.catalogAccessRightsChanged.email.title",c.getName()), 
							 targetUser.getText("Catalogs.users.catalogAccessRightsChanged.email.body",
									 targetUser.getNickname(),
									 c.getName(),
									 requestMsg.getAccessRights().toString()));
		
    }
    
    private Integer findAvailableFtpPort(Integer portRangeStart,Integer portRangeEnd) {
    	
    	Integer curPort=portRangeStart;
    	while (curPort<=portRangeEnd) {
            try {
                    ServerSocket s = new ServerSocket(curPort);
                    s.close();
                    return curPort;
            } catch (IOException e) {
            	curPort++;
            }            
    	}
    	
    	return null;
    }
    
    @MessageMapping("/create_catalog")
    @SubscribeMapping ( "/user/queue/created_catalog")
    public void handleCreateCatalogRequest(SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgCreateCatalog_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	    	
    	WsMsgCreateCatalog_answer answer = new WsMsgCreateCatalog_answer(requestMsg);
    	
    	if (!this.userHasWriteAccess(user)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
			return;         		
    	}
    	
    	try {    		

    		// need to ensure that a unique request for a free port and catalog name will be done at a time
            // for the whole server
            _GlobalCreateCatalogLock.acquire();
            
    		// check there is no such catalog already existing
    		ICatalog c  = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogName());
	    	if (c!=null) {
	    		answer.setRejectMessage(user.getText("Catalogs.catalogNameAlreadyUsed",c.getName()));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			_GlobalCreateCatalogLock.release();
    			return;
	    	}
    		
    		c=new Catalog();
    		c.setName(requestMsg.getCatalogName());
    		
    		Integer ftpPortRangeLow = new Integer(Globals.GetMxProperty("mx.ftp.port.range_low"));
            Integer ftpPortRangeHigh = new Integer(Globals.GetMxProperty("mx.ftp.port.range_high"));
            
            Integer availablePort = findAvailableFtpPort(ftpPortRangeLow,ftpPortRangeHigh);
            if (availablePort==null) {
            	answer.setRejectMessage("Unable to create catalog definition, no available port for userdata access");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			_GlobalCreateCatalogLock.release();
    			return;
            }
            c.setFtpPort(availablePort);
            
    		Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getCreateIntoDefDbStmt(user,c).execute();
    		if (!result) {
    			answer.setRejectMessage("Unable to create catalog definition into SQL db");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			_GlobalCreateCatalogLock.release();
    			return;
    		}
    		Globals.Get().getCatalogsMgr().loadFromDb();	    		
    		c  = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogName());
    		user.setUserCatalogAccessRights(c.getId(), USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN);
    		result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getSetUserAccessRightsIntoDbStmt(user, c).execute();	    		
    		
    		if (!result) {
    			answer.setRejectMessage("Unable to give access rights to user");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			_GlobalCreateCatalogLock.release();
    			return;
    		}
    		
    		// during loading of catalog from Db, services are started including FTP server
    		// so the port is taken and we can release the lock
    		_GlobalCreateCatalogLock.release();
	        
	    	c  = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogName());
	    	
	    	try {
	    		
	    		// create index
		    	result = Globals.Get().getDatabasesMgr().getCatalogContentsDbInterface().getCreateIndexIntoDocsDbStmt(c).execute();
		    	if (!result) {
		    		answer.setRejectMessage("Unable to create catalog index");
	    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
	    			return;
	    		}
		    			    	
	    	} catch(IndexAlreadyExistException e) {
	    		user.sendGuiWarningMessage("Reusing existing contents for catalog '"+c.getName()+"'");
	    	}
	    		 
	    	// create default terms
	    	// LastModif date
	    	ICatalogTerm newTermLastModifDate = ICatalogTerm.BuildCatalogTerm(ICatalogTerm.TERM_DATATYPE.DATE);
	    	newTermLastModifDate.setCatalogId(c.getId());
	    	newTermLastModifDate.setName(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP);
	    	
	    	ICatalogTerm newTermLastModifUser = ICatalogTerm.BuildCatalogTerm(ICatalogTerm.TERM_DATATYPE.INTEGER);
	    	newTermLastModifUser.setCatalogId(c.getId());
	    	newTermLastModifUser.setName(ICatalogTerm.MX_TERM_LASTMODIF_USERID);
	    	
	    	List<ICatalogTerm> mxFields = new ArrayList<>();
	    	mxFields.add(newTermLastModifDate);
	    	mxFields.add(newTermLastModifUser);
	    	
			Boolean isSuccess = Globals.Get().getDatabasesMgr().getTermsDbInterface()
					.createIntoESDbStmt(c,mxFields).execute();						    				
			if (!isSuccess) {
				answer.setIsSuccess(false);  
	    		answer.setRejectMessage("Unable to process create_catalog from '"+user.getName()+"' : unable to create default internal fields");
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);    		
	    		return;   
			}
			
			// create default voc in DB
	    	CatalogVocabularySet voc = c.getVocabulary(user.getGuiLanguageShortname());    	
	    	if (voc==null) {
	    		log.error("No '"+user.getGuiLanguageShortname()
	    						+"' vocabulary for new catalog "+c.getName()+", unable to customize the catalog name");

	    	} else {
		    	try {   
		    		c.acquireLock();
		    		CatalogVocabularySet vocClone=new CatalogVocabularySet(voc);
		    		vocClone.setName(StrTools.Capitalize(c.getName().replaceAll("_", " ")));		    		
			    	Globals.Get().getDatabasesMgr().getCatalogVocDbInterface().getWriteIntoDbStmt(vocClone).execute();
			    	c.releaseLock();
		    	} catch (Exception e) 
		    	{    		
		    		e.printStackTrace();
		    		c.releaseLock();
		    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_catalog.create_vocabulary"));
		    	}
	    	}
	    	
			c.loadStatsFromDb();	    	
			c.loadMappingFromDb();	
			c.loadVocabulariesFromDb();
			c.setDbIndexFound(true);
			// update user stats info, typically nb of catalogs created
			IUserProfileData creator = Globals.Get().getUsersMgr().getUserById(c.getOwnerId());
			if (creator!=null) { 
				Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
					.getCountUserCatalogsInDbStmt(creator)
					.execute();
			}
			
			// create Kibana space dedicated to this catalog
	    	result =Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().createStatisticsSpace(user, c);
	    	if (!result) {
	    		answer.setRejectMessage("Unable to create catalog statistics space");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			return;
    		}
	    	user.sendGuiInfoMessage("Created Kibana space for "+c.getName());
	    	// create index-pattern for catalog's space
	    	result =Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().createStatisticsIndexPattern(user, c);
	    	if (!result) {
	    		answer.setRejectMessage("Unable to create statistics index-pattern");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			return;
    		}
	    	user.sendGuiInfoMessage("Created Kibana index-pattern for "+c.getName());
	    	
	    	// create RO and W roles
	    	result =Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().createCatalogStatisticsRoles(user, c);
	    	if (!result) {
	    		answer.setRejectMessage("Unable to create catalog statistics roles");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
    			return;
    		}
	    	user.sendGuiInfoMessage("Created Kibana roles for accessing "+c.getName());
	    	// assign W role to active user is done by the monitoring task over user contents in SQL DB
	    	// @see UserProfileData.doPeriodicProcess() function
	    	
			answer.setIsSuccess(true);
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
	    	user.setCurrentCatalog(c.getId());
	    	user.notifyCatalogContentsChanged(CATALOG_MODIF_TYPE.CATALOGS_LIST, user.getCatalogVocabulary().getName(),c.getName());
	    	Globals.GetStatsMgr().handleStatItem(new CreateCatalogMxStat(user,c));	    	
	    	
    	} catch (Exception e) 
    	{
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process create_catalog from '"+user.getName()+"' : "+e.getMessage());
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);    
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_catalog"));
    		e.printStackTrace();
    		_GlobalCreateCatalogLock.release();
    	}
    }
    
    @MessageMapping("/select_catalog")
    @SubscribeMapping ( "/user/queue/catalog_selected")
    public void handleSelectCatalogRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgSelectCatalog_request requestMsg) throws Exception {

    	WsMsgSelectCatalog_answer answer = new WsMsgSelectCatalog_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogId());
    	    	
    	if (c==null) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_selected", answer);
    		return;
    	}
    	if (!this.userHasReadAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_selected", answer);
			return;         		
    	}
    	
    	if (user.getCurrentCatalog()!=null && !c.getId().equals(user.getCurrentCatalog().getId())) {
			user.getCurrentCatalog().quit(user);
		}
    	
    	user.setCurrentCatalog(c.getId());    	
    	c.enter(user);
    	answer.setIsSuccess(true);
    	
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_selected", answer);
        	
    }


    @MessageMapping("/customize_catalog")
    @SubscribeMapping ( "/user/queue/catalog_customized")
    public void handleCustomiseCatalogRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgCustomizeCatalog_request requestMsg) throws Exception {

    	WsMsgCustomizeCatalog_answer answer = new WsMsgCustomizeCatalog_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	// FTP port is not available for user custo for now... maybe later
    	requestMsg.setFtpPort(c.getFtpPort());
    	
    	if (c==null || !c.getId().equals(requestMsg.getId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
    		return;
    	}
    	
    	if (!this.userHasAdminAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
			return;         		
    	}
    	try {   
    		Boolean updateKibanaTimeField = !c.getTimeFieldTermId().equals(requestMsg.getTimeFieldTermId());
    		
    		// update catalog contents itself
    		c.acquireLock();
	    	requestMsg.setName(c.getName());	    	
	    	Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getUpdateIntoDefDbStmt(user, requestMsg).execute();
	    	if (!result) {
	    		answer.setRejectMessage("Unable to update catalog custom parameters");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
    			c.releaseLock();
    			return;
    		}
	    	c.loadCustomParamsFromdb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
	    	Globals.GetStatsMgr().handleStatItem(new SetCustoCatalogMxStat(user,c));
	    	c.releaseLock();
	    	
	    	// update Kibana timafield if needed
	    	if (updateKibanaTimeField==true) {
	    		result =Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().updateStatisticsTimeField(user, c);
		    	if (!result) {		    		
		    		user.sendGuiWarningMessage("Unable to update Kibana timefield to '"+c.getTimeFieldRawName()+"'");
	    			return;
	    		} else {
	    			user.sendGuiSuccessMessage(user.getText("Catalogs.overview.lastUpdateTimestamp.updated", c.getTimeFieldRawName(),c.getName()));
	    		}
	    	}
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process customize_catalog from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
	    	Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.customize_catalog"));
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	
    }


    @MessageMapping("/delete_catalog")
    @SubscribeMapping ( "/user/queue/deleted_catalog")
    public void handleDeleteCatalogRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgDeleteCatalog_request requestMsg) throws Exception {

    	WsMsgDeleteCatalog_answer answer = new WsMsgDeleteCatalog_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    		return;
    	}
    	// only catalog owner can delete it
    	if (!c.getOwnerId().equals(user.getId())) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
			return;         		
    	}
    	
    	try {   
    		c.acquireLock();
	    	
    		c.stopServices();
    		
    		// Delete SQL definition
	    	Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getDeleteFromDefDbStmt(user, c).execute();
	    	if (!result) {
	    		answer.setRejectMessage("Unable to delete catalog definition");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_catalog.delete_sql_def"));
    			c.releaseLock();
    			return;
    		}
	    	Globals.Get().getCatalogsMgr().removeCatalog(c.getId());
	    	
	    	// update user stats info, typically nb of catalogs created
			IUserProfileData creator = Globals.Get().getUsersMgr().getUserById(c.getOwnerId());
			if (creator!=null) { 
				Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
					.getCountUserCatalogsInDbStmt(creator)
					.execute();
			}
			
	    	// Try to delete ES index (if any)
	    	try {
		    	result = Globals.Get().getDatabasesMgr().getCatalogContentsDbInterface().getDeleteFromDocsDbStmt(user, c).execute();
		    	if (!result) {
		    		answer.setRejectMessage("Unable to delete catalog documents contents");
	    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
	    			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_catalog.delete_documents"));
	    			c.releaseLock();
	    			return;
	    		}
	    	} catch (Exception e) {    		
	    		// not so nice to detect it with a string search 
	    		if (!e.getMessage().contains("index_not_found_exception")) {	    			    			    	
		    		log.error("Unable to delete index '"+c.getName()+"' : "+e.getMessage());
		    		e.printStackTrace();
		    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_catalog.elasticsearch"));
	    		}
	    		// else the index was not in ES, but this is not a problem for the "delete catalog" operation
	    		// so we can silently ignore it
	    	}
	    		    	
	    	// Delete catalog config from Kibana config
	    	Boolean kResult1 = Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().deleteCatalogStatisticsRoles(user, c);
	    	if (!kResult1) {
	    		answer.setRejectMessage("Unable to delete statistics roles for '"+c.getName()+"'");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    		}
	    	Boolean kResult2 = Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().deleteStatisticsSpace(user, c);
	    	if (!kResult2) {
	    		answer.setRejectMessage("Unable to delete statistics spaces for '"+c.getName()+"'");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    		}
	    	
			
	    	// no need to explicitly delete index pattern, because it has defined as part of the space,
	    	// and has been deleted by Kibana when deleting the space itself	    	
	    	user.setCurrentCatalog(0);
	    	user.setUserCatalogAccessRights(c.getId(), null);
	    	answer.setIsSuccess(kResult1&&kResult2);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
	    	Globals.GetStatsMgr().handleStatItem(new DeleteCatalogMxStat(user,c));
	    	c.releaseLock();
	    	

			
    	} catch (Exception e) {    					    	
	    		answer.setIsSuccess(false);  
	    		answer.setRejectMessage("Unable to process delete_catalog '"+c.getName()+"' by '"+user.getName()+"'");
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
	    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_catalog.sql"));
	    		e.printStackTrace();
	    		c.releaseLock();
    		   		
    	}    	
    }


    @MessageMapping("/join_catalog")
    @SubscribeMapping ( "/user/queue/join_catalog")
    public void handleJoinCatalogRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgJoinCatalog_request requestMsg) throws Exception {

    	WsMsgJoinCatalog_answer answer = new WsMsgJoinCatalog_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	
    	if (user.isEnabled()==false || !user.isLoggedIn()) {
    		answer.setRejectMessage("Current user is not allowed to send a join-catalog request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
    		return;
    	}
    	
    	ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogId());
    	if (c==null) {
    		answer.setRejectMessage("No such catalog, unable to process join-catalog request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
    		return;
    	}

		if (!c.isEnabled()) {
			answer.setRejectMessage("catalog is disabled, unable to process join-catalog request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
    		return;
		}
		
    	if (user.getUserCatalogAccessRights(c.getId())!=USER_CATALOG_ACCESSRIGHTS.NONE) {
    		answer.setRejectMessage("user is already registered to this catalog, join-catalog request ignored");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
    		return;
    	}
    	
		// send notification email to catalog owner
		IUserProfileData catalogOwner = Globals.Get().getUsersMgr().getUserById(c.getOwnerId());
		if (catalogOwner==null) {
			answer.setRejectMessage("catalog has no valid owner user, unable to process join-catalog request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
    		return;
		}
    	try {   
    		
    		// assign a 'NONE' access rights to this usert for the catalog
    		// os that calalog admins can then see it in the users list and 
    		// give hil proper access rights
    		Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface()
    				.getSetCatalogUserAccessStmt(c,user,USER_CATALOG_ACCESSRIGHTS.NONE).execute();
			if (result==false) {
				answer.setRejectMessage("Unable to give join given catalog.");
				this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
				return;
			}
			try {
				catalogOwner.sendEmail(
						catalogOwner.getText("Catalogs.users.email.joinRequest.title",
																	user.getNickname(),
																	c.getName()),
						catalogOwner.getText("Catalogs.users.email.joinRequest.body",
								catalogOwner.getNickname(),
								user.getNickname(),
								c.getName()));
			} catch (Throwable e) {
				answer.setRejectMessage("Unable to notify admins to give you access rights to this catalog.");
				this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
				return;
			}
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
	    			
    	} catch (Exception e) {    					    	
	    		answer.setIsSuccess(false);  
	    		answer.setRejectMessage("Unable to process join_catalog request");
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/join_catalog", answer);
	    		e.printStackTrace();
	    		c.releaseLock();
    		   		
    	}    	
    }

    @MessageMapping("/update_catalog_lexic")
    @SubscribeMapping ( "/user/queue/catalog_lexic_updated")
    public void handleUpdateCatalogLexicRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgUpdateCatalogLexic_request requestMsg) throws Exception {
    	
    	WsMsgUpdateCatalogLexic_answer answer = new WsMsgUpdateCatalogLexic_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
    		return;
    	}
    	
    	if (!this.userHasAdminAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
			return;         		
    	}
    	
    	CatalogVocabularySet voc = c.getVocabulary(requestMsg.getLangShortName());    	
    	if (voc==null) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current catalog does not have any vocabulary for given language '"+requestMsg.getLangShortName()+"'");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
    		return;
    	}
    	try {   
    		c.acquireLock();
    		CatalogVocabularySet vocClone=new CatalogVocabularySet(voc);
    		vocClone.setVocabularyEntry(requestMsg.getEntryName(), requestMsg.getEntryTranslation());
    		
	    	Boolean result = Globals.Get().getDatabasesMgr().getCatalogVocDbInterface().getWriteIntoDbStmt(vocClone).execute();
	    	if (!result) {
	    		answer.setRejectMessage("Unable to update catalog vocabulary");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
    			c.releaseLock();
    			return;
    		}
	    	c.loadVocabulariesFromDb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
	    	Globals.GetStatsMgr().handleStatItem(new UpdateLexicCatalogMxStat(user,c));
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process update_catalog_lexic from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
	    	Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_catalog_lexic"));
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	    	
    }

    
}
