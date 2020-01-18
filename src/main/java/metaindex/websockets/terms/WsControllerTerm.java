package metaindex.websockets.terms;

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

import metaindex.data.commons.globals.Globals;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.CatalogTerm;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.catalogs.WsMsgUpdateCatalogLexic_answer;
import metaindex.websockets.catalogs.WsMsgUpdateCatalogLexic_request;
import metaindex.websockets.commons.AMxWSController;
import metaindex.websockets.users.WsControllerUser.COMMUNITY_MODIF_TYPE;
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
    			sendTermUpdateErrorAnswer(user,this.messageSender,answer,"No such catalog");
    			return;
    		}
    		if (!this.userHasWriteAccess(user,c)) { 
        		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
    			return;         		
        	}
    		user.setCurrentCatalog(c.getId());
    		
    		ICatalogTerm existingTerm = c.getTerms().get(requestMsg.getTermName());
    		if (existingTerm==null) {
    			sendTermUpdateErrorAnswer(user,this.messageSender,answer,"No such term");
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
    			sendTermUpdateErrorAnswer(user,this.messageSender,answer,"Server operation refused");
    			return;   
    		}
    		else {
    			c.loadTermsFromDb();
    			answer.setIsSuccess(true);
        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
        												"/queue/updated_term", 
    													getRawString(answer)); 
        		user.notifyCatalogContentsChanged(COMMUNITY_MODIF_TYPE.FIELD_DEFINITION, 1);
    		}
    	} catch (Exception e) 
    	{
    		answer.setIsSuccess(false);
    		String errorMessage=e.getMessage();
    		if (errorMessage==null) { errorMessage="???"; }
    		
    		sendTermUpdateErrorAnswer(user,this.messageSender,answer,errorMessage);
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
    		answer.setRejectMessage("term '"+requestMsg.getTermName()+"' already exist in catalog.");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);
    		return;
    	}
    	term = ICatalogTerm.BuildCatalogTerm(requestMsg.getTermDatatype());
    	term.setCatalogId(requestMsg.getCatalogId());
    	term.setName(requestMsg.getTermName());   
    	// if type is "relation", adding necessary complementary information which will be expected by DB procressing
    	if (requestMsg.getTermDatatype()==TERM_DATATYPE.RELATION) {
	    	Map<String,String> relationsMap = new HashMap<>();
	    	if (!requestMsg.getComplementaryInfoMap().containsKey("parent")
	    			|| !requestMsg.getComplementaryInfoMap().containsKey("child")) {
	    		answer.setRejectMessage("term '"+requestMsg.getTermName()+"' missing parent/child relation names.");
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);
	    		return;
	    	}
	    		
	    	relationsMap.put(requestMsg.getComplementaryInfoMap().get("parent"),requestMsg.getComplementaryInfoMap().get("child"));
	    	// mapping properties contains details of the relations names from ElasticSearch, here we populate here manually
	    	// to have consistent state of our newly built term before database processing
	    	Map<String,Object> mappingProperties = new HashMap<>();
	    	mappingProperties.put("relations", relationsMap);
	    	term.setMappingProperties(mappingProperties);
	    	// should be done after set properties, because there is a consistency check inside the setEnumsList method
	    	List<String> enumsList = new ArrayList<>();
	    	enumsList.add(requestMsg.getComplementaryInfoMap().get("parent"));
	    	enumsList.add(requestMsg.getComplementaryInfoMap().get("child"));	    	
	    	term.setEnumsList(enumsList);
    	}
    	try {
    		Boolean result = Globals.Get().getDatabasesMgr().getTermsDbInterface()
    					.createIntoDbStmt(c,term).execute();
    		
    		if (result==false) {
        		answer.setRejectMessage("unable to create term '"+requestMsg.getTermName()+"'.");
        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);	
        		return;
        	}
    		
    		answer.setIsSuccess(true);
    		
    	} catch (ESDataProcessException e) {
    		e.printStackTrace();
    		user.sendGuiErrorMessage("Sorry unable to create term : "+e.getMessage());
    		return;
    	}
    	catch (SQLDataProcessException e) {
    		// most probably term description already exist
    		answer.setRejectMessage("term description failed, try to refresh you page please : "+e.getMessage());
    		// no return here, we still want to refresh contents (field could still be added into ES db)
    	}
    	
    	// refresh terms list in catalog
    	c.acquireLock();
    	c.clearTerms();
    	c.loadMappingFromDb();
    	c.loadTermsFromDb();
    	c.releaseLock();
    	  
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_term", answer);
    	user.notifyCatalogContentsChanged(COMMUNITY_MODIF_TYPE.FIELDS_LIST, 1);
        	
    }
    
    /**
     * NOT YET FUNCTIONAL
     * @param headerAccessor
     * @param requestMsg
     * @throws Exception
     */
    @MessageMapping("/delete_term")
    @SubscribeMapping ( "/user/queue/deleted_term")
    public void handleDeleteTermRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgDeleteTerm_request requestMsg) throws Exception {

    	WsMsgDeleteTerm_answer answer = new WsMsgDeleteTerm_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(requestMsg.getCatalogId());
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	user.setCurrentCatalog(c.getId());
    	ICatalogTerm term = c.getTerms().get(requestMsg.getTermName());
    	if (term==null) {
    		user.sendGuiErrorMessage("Sorry no such term '"+requestMsg.getTermName()+"' in catalog, please refresh your page.");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_term", answer);
    	}
    	
    	Boolean result = Globals.Get().getDatabasesMgr().getTermsDbInterface().deleteFromDbStmt(c,term).execute();
    	
    	// refresh filters list in catalog
    	c.acquireLock();
    	c.clearFilters();
    	Globals.Get().getDatabasesMgr().getFiltersDbInterface().getLoadFromDbStmt(c).execute();
    	c.releaseLock();
    	
    	if (result==false) {
    		user.sendGuiErrorMessage("Sorry unable to delete term '"+requestMsg.getTermName()+"'.");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_term", answer);	
    		return;
    	}
    	
    	answer.setIsSuccess(true);  
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_term", answer);
    	user.notifyCatalogContentsChanged(COMMUNITY_MODIF_TYPE.FIELDS_LIST, 1);
        	
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
    		answer.setRejectMessage("Current user catalog does not match request");
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
    		answer.setRejectMessage("Current user catalog does contain requested term");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
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
    	    		answer.setRejectMessage("Unable to create term description prior to uptading its lexic");
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
	    		answer.setRejectMessage("Unable to update term translation");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
    			c.releaseLock();
    			return;
    		}
	    	c.loadTermsVocabularyFromDb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process term_lexic_updated from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/term_lexic_updated", answer);
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	    	
    }
    
    
}
