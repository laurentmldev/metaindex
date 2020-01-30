package metaindex.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.commons.globals.Globals;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.dbinterface.CreateIndexIntoEsDbStmt.IndexAlreadyExistException;
import metaindex.data.term.CatalogTerm;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;
import metaindex.websockets.commons.AMxWSController;
import metaindex.websockets.users.WsControllerUser.COMMUNITY_MODIF_TYPE;
import toolbox.exceptions.DataProcessException;

@Controller
public class WsControllerCatalog extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerCatalog.class);
	
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
	    		if (requestMsg.getCatalogId().equals(0) 
	    				|| requestMsg.getCatalogId().equals(curCatalog.getId())) {
	    			answeredList.add(new WsMsgCatalogDetails_answer(curCatalog,user));	  
	    		}
			}    
    		sendListToUser(user.getName(),"/queue/catalogs",answeredList, /* Base64 compression */ true); 
	    	
    	} catch (IOException e) 
    	{
    		log.error("Unable to process get_catalogs from '"+user.getName()+"' : "+e.getMessage());
    		e.printStackTrace();
    	}
    }
    
    @MessageMapping("/create_catalog")
    @SubscribeMapping ( "/user/queue/created_catalog")
    public void handleCreateCatalogRequest(SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgCreateCatalog_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	    	
    	WsMsgCreateCatalog_answer answer = new WsMsgCreateCatalog_answer(requestMsg);
    	
    	if (!this.userHasAdminAccess(user)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
			return;         		
    	}
    	
    	try {    		
    		ICatalog c  = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogName());
	    	if (c==null) {
	    		c=new Catalog();
	    		c.setName(requestMsg.getCatalogName());
	    		Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getCreateIntoDbStmt(user,c).execute();
	    		if (!result) {
	    			answer.setRejectMessage("Unable to create catalog definition into SQL db");
	    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
	    			return;
	    		}
	    		Globals.Get().getCatalogsMgr().loadFromDb();	    		
	    		c  = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogName());
	    		user.setUserCatalogAccessRights(c.getId(), USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN);
	    		result = Globals.Get().getDatabasesMgr().getUserProfileDbInterface().getSetUserAccessRightsIntoDbStmt(user, c).execute();	    		
	    		
	    		if (!result) {
	    			answer.setRejectMessage("Unable to give access rights to user");
	    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
	    		}
	    		
	    	} else {
	    		user.sendGuiWarningMessage("Reusing existing definition for catalog '"+c.getName()+"'");
	    	}
	    	c  = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogName());
	    	
	    	try {
		    	Boolean result = Globals.Get().getDatabasesMgr().getCatalogContentsDbInterface().getCreateIndexIntoDbStmt(c).execute();
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
			
			c.loadStatsFromDb();	    	
			c.loadMappingFromDb();	
			c.loadVocabulariesFromDb();
			c.setDbIndexFound(true);	    	
					
			answer.setIsSuccess(true);
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);
	    	user.setCurrentCatalog(c.getId());
	    	user.notifyCatalogContentsChanged(COMMUNITY_MODIF_TYPE.COMMUNITY_LIST, 1);
	    	
    	} catch (Exception e) 
    	{
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process create_catalog from '"+user.getName()+"' : "+e.getMessage());
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_catalog", answer);    		
    		e.printStackTrace();
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
    		c.acquireLock();
	    	requestMsg.setName(c.getName());
	    	Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getUpdateIntoDbStmt(user, requestMsg).execute();
	    	if (!result) {
	    		answer.setRejectMessage("Unable to update catalog custom parameters");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
    			return;
    		}
	    	c.loadCustomParamsFromdb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process customize_catalog from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_customized", answer);
    		e.printStackTrace();
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
    	
    	if (!this.userHasAdminAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
			return;         		
    	}
    	
    	try {   
    		c.acquireLock();
	    	
    		// Delete SQL definition
	    	Boolean result = Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getDeleteFromDbStmt(user, c).execute();
	    	if (!result) {
	    		answer.setRejectMessage("Unable to delete catalog definition");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    			return;
    		}
	    	
	    	Globals.Get().getCatalogsMgr().removeCatalog(c.getId());
	    	
	    	// Delete ES index
	    	result = Globals.Get().getDatabasesMgr().getCatalogContentsDbInterface().getDeleteFromDbStmt(user, c).execute();
	    	if (!result) {
	    		answer.setRejectMessage("Unable to delete catalog contents");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    			return;
    		}
	    	
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
	    	
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		Globals.Get().getCatalogsMgr().removeCatalog(c.getId());	    	
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process delete_catalog '"+c.getName()+"' by '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_catalog", answer);
    		e.printStackTrace();
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
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process update_catalog_lexic from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/catalog_lexic_updated", answer);
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	    	
    }

    
}
