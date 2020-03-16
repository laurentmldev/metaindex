package metaindex.app.control.websockets.users;

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

import metaindex.app.control.websockets.catalogs.WsMsgCatalogContentsChanged_answer;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.control.websockets.users.WsMsgRegisterUser_answer;
import metaindex.app.control.websockets.users.WsMsgRegisterUser_request;
import metaindex.app.control.websockets.users.WsUserGuiMessageText.MESSAGE_CRITICITY;
import metaindex.app.periodic.statistics.catalog.SetUserCustoCatalogMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.app.periodic.statistics.user.LoginUserMxStat;
import metaindex.app.periodic.statistics.user.SetPrefUserMxStat;
import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;

@Controller
public class WsControllerUser extends AMxWSController {
	
	
	private Log log = LogFactory.getLog(WsControllerUser.class);
		
	// list to be coherent with metaindex.js API equivalent
	public enum CATALOG_MODIF_TYPE { 	CATALOGS_LIST, 
										CATALOG_DEFINITION,
										FIELD_VALUE, 
										FIELDS_LIST, 
										FIELD_DEFINITION,
									    DOCS_LIST
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
    	
    	IUserProfileData user = getUserProfile(headerAccessor);
    	try {
    		
    		Boolean registrationStatus=user.isLoggedIn();
    		
    		user.setWebsocketSessionId(headerAccessor.getSessionId());
    		messageSender.convertAndSendToUser(	headerAccessor.getUser().getName(), 
					"/queue/register_ack", 
					new WsMsgRegisterUser_answer(registrationStatus));
    		
    		Globals.GetStatsMgr().handleStatItem(new LoginUserMxStat(user));
	    	
    	} catch (DataProcessException e) 
    	{
    		log.error("Unable to register '"+headerAccessor.getUser().getName()+"' on websockets API : "+e);  
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.register_request"));
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
	    	
    		Globals.GetStatsMgr().handleStatItem(new SetUserCustoCatalogMxStat(user,c));
    		
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage("Update operation failed : "+e.getMessage());
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
			e.printStackTrace();
			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.set_user_catalogcusto"));
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
	    	
    		Globals.GetStatsMgr().handleStatItem(new SetPrefUserMxStat(user));
    		
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage("Update operation failed : "+e.getMessage());
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
			e.printStackTrace();
			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.set_user_preferences"));
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
    												 CATALOG_MODIF_TYPE modifType, 
    												 Long nbImpactedItems) throws Exception {

    		messageSender.convertAndSend(
					"/queue/catalog_contents_changed", 
					new WsMsgCatalogContentsChanged_answer(user.getCurrentCatalog().getName(),
															user.getNickname(),
															nbImpactedItems,
															modifType));
    }
    
    @SendTo("/queue/catalog_contents_changed")
    public void sendBroadCastCatalogContentsChanged(IUserProfileData user, 
    												 CATALOG_MODIF_TYPE modifType, 
    												 String impactedItemName,
    												 String impactDetails) throws Exception {

    		messageSender.convertAndSend(
					"/queue/catalog_contents_changed", 
					new WsMsgCatalogContentsChanged_answer(user.getCurrentCatalog().getName(),
															user.getNickname(),
															impactedItemName,
															impactDetails,
															modifType));
    }    
    
}
