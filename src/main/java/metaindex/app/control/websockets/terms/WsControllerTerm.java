package metaindex.app.control.websockets.terms;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.terms.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.app.periodic.statistics.terms.CreateTermMxStat;
import metaindex.app.periodic.statistics.terms.UpdateLexicTermMxStat;
import metaindex.app.periodic.statistics.terms.UpdateTermMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.CatalogTerm;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.elasticsearch.ESDataProcessException;
import toolbox.database.sql.SQLDataProcessException;

@Controller
public class WsControllerTerm extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerTerm.class);
	
	@Autowired
	public WsControllerTerm(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
	private void sendTermUpdateErrorAnswer(IUserProfileData user, 
								 SimpMessageSendingOperations messageSender, 
								 WsMsgUpdateTerm_answer answer, 
								 String errText) 
						throws MessagingException, IOException {
		answer.setIsSuccess(false);
		String errMsg=user.getText("Catalogs.server.unableToUpdateTerm",
						answer.getTermName(),
						answer.getTermType(),
						errText);
		
		answer.setRejectMessage(errMsg);
		this.messageSender.convertAndSendToUser(user.getNickname(),
												"/queue/updated_term", 
												getRawString(answer));
	}
	
    @MessageMapping("/update_term")
    @SubscribeMapping ( "/user/queue/updated_term")
    public void handleUpdateFieldTypeRequest(SimpMessageHeaderAccessor headerAccessor, 
    		WsMsgUpdateTerm_request requestMsg) throws Exception {

    
    	IUserProfileData user = getUserProfile(headerAccessor);			
    	WsMsgUpdateTerm_answer answer = new WsMsgUpdateTerm_answer(requestMsg);
    	
    	try {
    		ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogId());
    		if (c==null) {
    			sendTermUpdateErrorAnswer(user,this.messageSender,answer,user.getText("Catalogs.catalogUnknown"));
    			return;
    		}
    		if (!this.userHasWriteAccess(user,c)) { 
        		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
    			return;         		
        	}
    		user.setCurrentCatalog(c.getId());
    		
    		ICatalogTerm existingTerm = c.getTerms().get(requestMsg.getTermName());
    		if (existingTerm==null) {
    			sendTermUpdateErrorAnswer(user,this.messageSender,answer,user.getText("Catalogs.field.noSuchField",requestMsg.getTermName()));
    			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_term.no_such_term"));
    			return;    			
    		}
    		
    		ICatalogTerm newTermDef = ICatalogTerm.BuildCatalogTerm(requestMsg.getTermType()); 
    		newTermDef.setCatalogId(c.getId());
    		newTermDef.setName(existingTerm.getName());
    		newTermDef.setEnumsList(requestMsg.getTermEnumsList());
    		newTermDef.setIsMultiEnum(requestMsg.getTermIsMultiEnum());
    		
    		Boolean isSuccess = Globals.Get().getDatabasesMgr().getTermsDbInterface()
    						.getUpdateIntoDbStmt(newTermDef).execute();
							    		
    		
    		if (!isSuccess) {
    			sendTermUpdateErrorAnswer(user,this.messageSender,answer,user.getText("Catalogs.field.updateTermOperationRefused",existingTerm.getName()));
    			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_term.refused_by_server"));
    			return;   
    		}
    		else {
    			c.loadTermsFromDb();
    			answer.setIsSuccess(true);
        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
        												"/queue/updated_term", 
    													getRawString(answer)); 
        		user.notifyCatalogContentsChanged(CATALOG_MODIF_TYPE.FIELD_DEFINITION, 1L);
        		Globals.GetStatsMgr().handleStatItem(new UpdateTermMxStat(user,c,newTermDef.getName()));
    		}
    	} catch (Exception e) 
    	{
    		answer.setIsSuccess(false);
    		String errorMessage=e.getMessage();
    		if (errorMessage==null) { errorMessage="???"; }
    		
    		sendTermUpdateErrorAnswer(user,this.messageSender,answer,errorMessage);
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_term.server_error"));
			e.printStackTrace();
    	}
    	
    }
    
    @MessageMapping("/create_term")
    @SubscribeMapping ( "/user/queue/created_term")
    public void handleCreateTermRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgCreateTerm_request requestMsg) throws Exception {

    	WsMsgCreateTerm_answer answer = new WsMsgCreateTerm_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogId());
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	user.setCurrentCatalog(c.getId());
    	
    	ICatalogTerm term = c.getTerms().get(requestMsg.getTermName());
    	if (term!=null) {    		
    		answer.setRejectMessage(user.getText("Catalogs.field.alreadyExistInCatalog",requestMsg.getTermName()));
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.already_exists"));
    		return;
    	}
    	term = ICatalogTerm.BuildCatalogTerm(requestMsg.getTermDatatype());
    	term.setCatalogId(requestMsg.getCatalogId());
    	term.setName(requestMsg.getTermName());
    	
    	// default isMultiEnum=True for images
    	if (requestMsg.getTermDatatype().equals(TERM_DATATYPE.IMAGE_URL)) {
    		term.setIsMultiEnum(true);
    	}
    	try {
    		Boolean result = Globals.Get().getDatabasesMgr().getTermsDbInterface()
    					.createIntoDbStmt(c,term).execute();
    		
    		if (result==false) {
        		answer.setRejectMessage(user.getText("Catalogs.field.unableToCreateTerm"));
        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);	
        		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.refused_by_server"));
        		return;
        	}
    		
    		answer.setIsSuccess(true);
    		
    	} catch (ESDataProcessException e) {
    		log.error("Unable to create new term (ELK exception) : "+e.getMessage());
    		e.printStackTrace();
    		user.sendGuiErrorMessage(user.getText("Catalogs.field.unableToCreateTerm"));
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.elasticsearch"));
    		return;
    	}
    	catch (SQLDataProcessException e) {
    		// most probably term description already exist
    		answer.setRejectMessage(user.getText("Catalogs.field.unableToCreateTerm")+" : "+e.getMessage());
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.sql"));
    		// no return here, we still want to refresh contents (field could still be added into ES db)
    	}
    	
    	// refresh terms list in catalog
    	// might not be required if several terms are created in a row    		    
    	try {
	    	c.acquireLock();
	    	c.clearTerms();
	    	c.loadMappingFromDb();
	    	c.loadTermsFromDb();
	    	c.releaseLock();
    	} catch (Throwable t) {
    		c.releaseLock();
    		throw t;
    	}
	
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);
    	user.notifyCatalogContentsChanged(CATALOG_MODIF_TYPE.FIELDS_LIST, 1L);
    	Globals.GetStatsMgr().handleStatItem(new CreateTermMxStat(user,c,term.getName()));
    	
		// Refresh Kibana index-pattern so that new term is available for statistics
    	Boolean rst = Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().refreshStatisticsIndexPattern(user, c);
    	if (rst==false) {
    		answer.setRejectMessage(user.getText("Catalogs.field.unableToCreateTermInKibana",term.getName()));
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.kibana"));
    	}
		
    }
    
    @MessageMapping("/update_term_lexic")
    @SubscribeMapping ( "/user/queue/term_lexic_updated")
    public void handleUpdateTermLexicRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgUpdateTermLexic_request requestMsg) throws Exception {

    	WsMsgUpdateTermLexic_answer answer = new WsMsgUpdateTermLexic_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage(user.getText("Profile.server.fieldLexicUpdateRefused"));
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
    		return;
    	}
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	ICatalogTerm t =c.getTerms().get(requestMsg.getTermName());
    	if (t==null) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage(user.getText("Catalogs.field.noSuchField"));
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_term_lexic.no_such_term"));
    		return;
    	}
    	
    	TermVocabularySet voc = new TermVocabularySet();
    	voc.setGuiLanguageId(Globals.Get().getGuiLanguagesMgr().getGuiLanguage(requestMsg.getLangShortName()).getId());    	
    	voc.setId(t.getId());
    	voc.setName(requestMsg.getEntryTranslation());
    	
    	try {   
    		c.acquireLock();
    		
    		// if term is not defined in MX DB but only in EL DB, we need to create it, so that its
        	// vocabulary can point to it
        	if (t.getId()==0) {
        		Boolean result = Globals.Get().getDatabasesMgr().getTermsDbInterface().createIntoDbStmt(c, t).execute();
    	    	if (!result) {
    	    		answer.setRejectMessage(user.getText("Catalogs.field.updateTermOperationRefused",t.getName())+" (1)");
        			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
        			c.releaseLock();
        			return;
        		}
    	    	c.loadTermsFromDb();
    	    	t =c.getTerms().get(requestMsg.getTermName());
    	    	voc.setId(t.getId());
        	}
        	
	    	Boolean result = Globals.Get().getDatabasesMgr().getTermsDbInterface().getCreateOrUpdateVocabularyIntoDbStmt(voc).execute();
	    	if (!result) {
	    		answer.setRejectMessage(user.getText("Catalogs.field.updateTermOperationRefused",t.getName())+" (2)");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
    			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_term_lexic.refused_by_server"));
    			c.releaseLock();
    			return;
    		}
	    	c.loadTermsVocabularyFromDb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
	    	Globals.GetStatsMgr().handleStatItem(new UpdateLexicTermMxStat(user,c,t.getName()));
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		log.error("Error while updating term lexic : "+e.getMessage());
    		e.printStackTrace();
    		answer.setRejectMessage(user.getText("Catalogs.field.updateTermOperationRefused",t.getName())+" (3)");
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
	    	Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_term_lexic.server_error"));
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	    	
    }
    
    
}
