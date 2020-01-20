package metaindex.websockets.users;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.websockets.commons.AMxWSController;
import metaindex.websockets.filters.WsMsgCreateFilter_answer;
import metaindex.websockets.catalogs.WsMsgCatalogContentsChanged_answer;
import metaindex.websockets.users.WsMsgRegisterUser_answer;
import metaindex.websockets.users.WsMsgRegisterUser_request;
import metaindex.websockets.users.WsUserGuiMessageText.MESSAGE_CRITICITY;
import toolbox.exceptions.DataProcessException;

@Controller
public class WsControllerUser extends AMxWSController {
	
	
	private Log log = LogFactory.getLog(WsControllerUser.class);
		
	// list to be coherent with metaindex.js API equivalent
	public enum COMMUNITY_MODIF_TYPE { 	COMMUNITY_LIST, 
										COMMUNITY_DEFINITION,
										FIELD_VALUE, 
										FIELDS_LIST, 
										FIELD_DEFINITION,
									    ITEMS_LIST
										};
	
	public static WsControllerUser UsersWsController = null;
	
	@Autowired
	public WsControllerUser(SimpMessageSendingOperations messageSender) {
		super(messageSender);	
		UsersWsController=this;
	}		
		
	
    @MessageMapping("/register_request")
    @SubscribeMapping ("/user/queue/register_ack")
    public void handleUserRegisterRequest(SimpMessageHeaderAccessor headerAccessor, 
    												WsMsgRegisterUser_request requestMsg) throws Exception {
    	
    	try {
    		IUserProfileData user = getUserProfile(headerAccessor);
    		Boolean registrationStatus=user.isLoggedIn();
    		
    		user.setWebsocketSessionId(headerAccessor.getSessionId());
    		messageSender.convertAndSendToUser(	headerAccessor.getUser().getName(), 
					"/queue/register_ack", 
					new WsMsgRegisterUser_answer(registrationStatus));
	    	
    	} catch (DataProcessException e) 
    	{
    		log.error("Unable to register '"+headerAccessor.getUser().getName()+"' on websockets API : "+e);    		
    	}
    }
   
    @SendTo ("/user/queue/gui_messaging")
    public void sendUserGuiMessageText(	IUserProfileData user, 
    									MESSAGE_CRITICITY  level, 
    									String message,
    									List<String> details) throws Exception {

    		messageSender.convertAndSendToUser(user.getName(), 
					"/queue/gui_messaging", 
					new WsUserGuiMessageText(level, message, details));
    }
    

    @MessageMapping("/set_user_catalogcusto")
    @SubscribeMapping ("/user/queue/user_catalogcusto_set")
    public void handleUserSetCatalogCustomizationRequest(SimpMessageHeaderAccessor headerAccessor, 
    										WsMsgUserSetCatalogCustomization_request requestMsg) throws Exception {
    	
    	WsMsgUserSetCatalogCustomization_answer answer = new WsMsgUserSetCatalogCustomization_answer(requestMsg);
		IUserProfileData user = getUserProfile(headerAccessor);
				
    	try {
    		if (!requestMsg.getUserId().equals(user.getId())) {
    			answer.setRejectMessage("User ID does not match currently logged user, sorry.");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
    			return;
    		}
    		
    		ICatalog c = user.getCurrentCatalog();
    		if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    			answer.setRejectMessage("User ID does not match currently logged user, sorry.");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
    			return;
    		}
    		
    		IUserProfileData tmpUserData = new UserProfileData();
    		tmpUserData.setId(user.getId());
    		tmpUserData.setUserCatalogKibanaIFrameHtml(c.getId(), requestMsg.getKibanaIFrame());
    		
    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileDbInterface().getSetUserCatalogCustomizationIntoDbStmt(tmpUserData, c).execute();
    		if (result==false) {
    			answer.setRejectMessage("Update operation refused, sorry.");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
    			return;
    		}
    		user.setUserCatalogKibanaIFrameHtml(c.getId(), requestMsg.getKibanaIFrame());
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);			
	    	
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage("Update operation failed : "+e.getMessage());
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
			e.printStackTrace();
			return;    		
    	}
    }
   
    
    @MessageMapping("/set_user_preferences")
    @SubscribeMapping ("/user/queue/user_preferences_set")
    public void handleUserSetPreferencesRequest(SimpMessageHeaderAccessor headerAccessor, 
    												WsMsgUserSetPreferences_request requestMsg) throws Exception {
    	
    	WsMsgUserSetPreferences_answer answer = new WsMsgUserSetPreferences_answer(requestMsg);
		IUserProfileData user = getUserProfile(headerAccessor);
				
    	try {
    		if (!requestMsg.getUserId().equals(user.getId())) {
    			answer.setRejectMessage("User ID does not match currently logged user, sorry.");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
    			return;
    		}
    		
    		IUserProfileData tmpUserData = new UserProfileData();
    		tmpUserData.setGuiThemeId(requestMsg.getThemeId());
    		tmpUserData.setGuiLanguageId(requestMsg.getLanguageId());
    		tmpUserData.setNickname(requestMsg.getNickName());
    		tmpUserData.setName(user.getName());
    		tmpUserData.setId(user.getId());
    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileDbInterface().getUpdateIntoDbStmt(tmpUserData).execute();
    		
    		if (result==false) {
    			answer.setRejectMessage("Update operation refused, sorry.");
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
    			return;
    		}
    		
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);			
	    	
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage("Update operation failed : "+e.getMessage());
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
			e.printStackTrace();
			return;    		
    	}
    }
   
    
    @SendTo ("/user/queue/gui_messaging_progress")
    public void sendUserGuiMessageProgress(	IUserProfileData user, 
    											Integer processingId, 
    											String message, 
    											Float pourcentage,
    											Boolean active) throws Exception {

    		messageSender.convertAndSendToUser(user.getName(), 
					"/queue/gui_messaging_progress", 
					new WsUserGuiMessageProgress(processingId,message,pourcentage,active));
    }
    
    

    @SendTo("/queue/catalog_contents_changed")
    public void sendBroadCastCatalogContentsChanged(IUserProfileData user, 
    												 COMMUNITY_MODIF_TYPE modifType, 
    												 Integer nbImpactedItems) throws Exception {

    		messageSender.convertAndSend(
					"/queue/catalog_contents_changed", 
					new WsMsgCatalogContentsChanged_answer(user.getCurrentCatalog().getName(),
															user.getNickname(),
															nbImpactedItems,
															modifType));
    }
    
    
    
}
