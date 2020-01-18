package metaindex.websockets.perspectives;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.commons.globals.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.commons.AMxWSController;

@Controller
public class WsControllerPerspective extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerPerspective.class);
	
	@Autowired
	public WsControllerPerspective(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
    @MessageMapping("/update_perspective")
    @SubscribeMapping ( "/user/queue/perspective_updated")
    public void handleCreateOrUpdatePerspectiveRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgUpdatePerspective_request requestMsg) throws Exception {

    	WsMsgUpdatePerspective_answer answer = new WsMsgUpdatePerspective_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_updated", answer);
    		return;
    	}
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	try {   
    		c.acquireLock();
	    	
	    	Boolean result = Globals.Get().getDatabasesMgr().getPerspectivesDbInterface().getUpdatePerspectiveIntoDbStmt(user, c, requestMsg.getJsonDef()).execute();	    	
	    	if (!result) {
	    		answer.setRejectMessage("Unable to update perspective");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_updated", answer);
    			return;
    		}
	    	c.loadPerspectivesFromdb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_updated", answer);
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process update_perspective from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_updated", answer);
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	
    }

    

    @MessageMapping("/delete_perspective")
    @SubscribeMapping ( "/user/queue/perspective_deleted")
    public void handleDeletePerspectiveRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgDeletePerspective_request requestMsg) throws Exception {

    	WsMsgDeletePerspective_answer answer = new WsMsgDeletePerspective_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_deleted", answer);
    		return;
    	}
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	try {   
    		c.acquireLock();
	    	
	    	Boolean result = Globals.Get().getDatabasesMgr().getPerspectivesDbInterface().getDeletePerspectiveFromDbStmt(user, c, requestMsg.getPerspectiveId()).execute();	    	
	    	if (!result) {
	    		answer.setRejectMessage("Unable to delete perspective");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_deleted", answer);
    			return;
    		}
	    	c.loadPerspectivesFromdb();
	    	answer.setIsSuccess(true);    	
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_deleted", answer);
	    	c.releaseLock();
    	} catch (Exception e) 
    	{    		
    		answer.setIsSuccess(false);  
    		answer.setRejectMessage("Unable to process delete_perspective from '"+user.getName()+"' : "+e.getMessage());
	    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/perspective_deleted", answer);
    		e.printStackTrace();
    		c.releaseLock();
    	}
    	
    }

    
    

    
}
