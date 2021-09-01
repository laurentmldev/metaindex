package metaindex.app.control.websockets.admin;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>
*/


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.admin.messages.WsMsgGetMonitoringInfo_answer;
import metaindex.app.control.websockets.admin.messages.WsMsgGetMonitoringInfo_request;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.data.userprofile.IUserProfileData;


@Controller
public class WsControllerAdmin extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerAdmin.class);
	
	
	@Autowired
	public WsControllerAdmin(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
		
    @MessageMapping("/admin_get_monitoring_info")
    @SubscribeMapping ( "/user/queue/admin_monitoring_info")
    public void handleMonitoringInfoRequest(SimpMessageHeaderAccessor headerAccessor, 
    		WsMsgGetMonitoringInfo_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	
    	WsMsgGetMonitoringInfo_answer answer = new WsMsgGetMonitoringInfo_answer(requestMsg);
	
		
		if (!this.userHasAdminRole(user)) { 
			
			answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/admin_monitoring_info", answer);
			return;
    	}
    	answer.setNbActiveUsers(Globals.Get().getActiveUsersList().size());
    	answer.setNbRunningProcessingTasks(Globals.Get().getActiveProcessingTasks().size());
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/admin_monitoring_info", answer);
		return;
    		
	    
    }


    
}
