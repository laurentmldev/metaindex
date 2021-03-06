package metaindex.app.control.websockets.users;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.users.messages.*;
import metaindex.app.control.websockets.users.messages.WsMsgUserSessionStatus_answer.SESSION_STATUS;
import metaindex.app.control.websockets.users.messages.WsUserGuiMessageText.MESSAGE_CRITICITY;
import metaindex.app.control.websockets.catalogs.messages.WsMsgCatalogContentsChanged_answer;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.periodic.statistics.catalog.SetUserCustoCatalogMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.app.periodic.statistics.user.LoginUserMxStat;
import metaindex.app.periodic.statistics.user.SetPrefUserMxStat;
import metaindex.app.periodic.statistics.user.UpdatePlanPaymentMxStat;
import metaindex.app.periodic.statistics.user.UpdatePlanRequestMxStat;
import metaindex.data.catalog.CatalogChatMsg;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogChatMsg;
import metaindex.data.commons.globals.plans.IPlan;
import metaindex.data.commons.globals.plans.IPlansManager;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.payment.IPaymentInterface;
import toolbox.utils.payment.IPaymentInterface.PAYMENT_METHOD;
import toolbox.utils.payment.PaypalPaymentInterface;
import toolbox.utils.payment.SandboxPaymentInterface;



@Controller
public class WsControllerUser extends AMxWSController {
	

	private Log log = LogFactory.getLog(WsControllerUser.class);
		
	
public static final Integer NB_DAYS_PLAN_DISCOUNT=182;// approx. half a year
	public static final Integer NB_TRIES_FOR_PAYMENT_CONFIRMATION=5;
	
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
	
	private static List<WsMsgUpdateUserPlan_answer> _awaitingPaymentPlanUpdates = new java.util.concurrent.CopyOnWriteArrayList<>();
	
	
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
    		e.printStackTrace();
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.register_request"));
    	}
    }
 
		
	
    @MessageMapping("/users_profiles")
    @SubscribeMapping ("/user/queue/users_profiles")
    public void handleUserProfileRequest(SimpMessageHeaderAccessor headerAccessor, 
    										WsMsgUserProfile_request requestMsg) throws Exception {
    	
    	WsMsgUserProfile_answer answer = new WsMsgUserProfile_answer();
    	answer.setRequestId(requestMsg.getRequestId());
    	IUserProfileData user = getUserProfile(headerAccessor);
    	if (user==null || !user.isLoggedIn() || user.getRole()==USER_ROLE.ROLE_OBSERVER) { return; }
    	
    	try {
    		
    		for (Integer userId : requestMsg.getUsersIds()) {
	    		IUserProfileData requestedUser = Globals.Get().getUsersMgr().getUserById(userId);
	    		if (requestedUser==null) {
	    			answer.setRejectMessage(user.getText("Profile.server.noSuchUser",userId.toString()));
	    			answer.setIsSuccess(false);
	    			messageSender.convertAndSendToUser(	headerAccessor.getUser().getName(), 
	    					"/queue/users_profiles", answer); 
	    			return;
	    		}    		
	    		answer.addUser(requestedUser);
    		}
    		answer.setIsSuccess(true);
    		messageSender.convertAndSendToUser(	headerAccessor.getUser().getName(), 
					"/queue/users_profiles", answer);
    		
    		Globals.GetStatsMgr().handleStatItem(new LoginUserMxStat(user));
	    	
    	} catch (DataProcessException e) 
    	{
    		log.error("Unable to retrieve users profiles : "+e);  
    		e.printStackTrace();
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
    			answer.setRejectMessage(user.getText("Profile.plans.wrongUser"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
    			return;
    		}
    		
    		ICatalog c = user.getCurrentCatalog();
    		if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    			answer.setRejectMessage(user.getText("Profile.plans.wrongUser"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
    			return;
    		}
    		
    		IUserProfileData tmpUserData = new UserProfileData();
    		tmpUserData.setId(user.getId());
    		tmpUserData.setUserCatalogKibanaIFrameHtml(c.getId(), requestMsg.getKibanaIFrame());
    		
    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getSetUserCatalogCustomizationIntoDbStmt(tmpUserData, c).execute();
    		if (result==false) {
    			answer.setRejectMessage(user.getText("Profile.server.userUpdateRefused"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
    			return;
    		}
    		user.setUserCatalogKibanaIFrameHtml(c.getId(), requestMsg.getKibanaIFrame());
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);			
	    	
    		Globals.GetStatsMgr().handleStatItem(new SetUserCustoCatalogMxStat(user,c));
    		
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage(user.getText("Profile.server.userUpdateRefused"));
    		log.error("unable to update user custom data : "+e.getMessage());
    		e.printStackTrace();
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_catalogcusto_set", answer);
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
    			answer.setRejectMessage(user.getText("Profile.plans.wrongUser"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
    			return;
    		}
    		
    		IUserProfileData tmpUserData = new UserProfileData();
    		tmpUserData.setGuiThemeId(requestMsg.getThemeId());
    		tmpUserData.setGuiLanguageId(requestMsg.getLanguageId());
    		tmpUserData.setNickname(requestMsg.getNickName());
    		tmpUserData.setName(user.getName());
    		tmpUserData.setId(user.getId());
    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getUpdateUserProfileIntoDbStmt(tmpUserData).execute();
    		
    		if (result==false) {
    			answer.setRejectMessage(user.getText("Profile.server.userUpdateRefused"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
    			return;
    		}
    		
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);			
	    	
    		Globals.GetStatsMgr().handleStatItem(new SetPrefUserMxStat(user));
    		
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage(user.getText("Profile.server.userUpdateRefused"));
    		log.error("unable to update user preferences data : "+e.getMessage());
    		e.printStackTrace();
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/user_preferences_set", answer);
			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.set_user_preferences"));
			return;    		
    	}
    }
   

	public static enum BREAKDOWN_ENTRY_TYPE { product, tax, discount, totalht, total };
	
	public class PlanBreakdownEntry {		
		public String title;
		public Double cost;
		public BREAKDOWN_ENTRY_TYPE type;
		public String unit="";
		
		public PlanBreakdownEntry(String title,Double cost,BREAKDOWN_ENTRY_TYPE type,String unit) {
			this.title=title;
			this.cost=cost;
			this.type=type;
			this.unit=unit;
		}
	};
	
	private Double round2digits(Double value) {
		return Math.ceil(value*100)/100;
	}
    private Boolean buildPlanUpdateCheckoutDetails(IUserProfileData u, WsMsgUpdateUserPlan_request requestMsg,WsMsgUpdateUserPlan_answer answerMsg) {
		
    	
    	Integer requestedPlanId = requestMsg.getPlanId();
    	
    	Double totalYearlyCost=0.0;
    	Double totalYearlyCostHT=0.0;
    	
    	IPlan curPlan = u.getPlan();
    	if (curPlan==null) {
    		curPlan=Globals.Get().getPlansMgr().getDefaultPlan(u.getCategory());
    		if (curPlan==null) {
    			log.error("No default plan found for category '"+u.getCategory()+"'. Unable to build plan update checkout details");
    			return false;
    		}
		}
    	Double curPlanCost = curPlan.getYearlyCostEuros().doubleValue();
				
		IPlan newPlan = Globals.Get().getPlansMgr().getPlan(requestedPlanId);
		if (newPlan==null) {
			log.error("User "+u.getName()+" requested update to unknown plan id '"+requestedPlanId+"'");
			return false;
		}
		Double newPlanCost = round2digits(newPlan.getYearlyCostEuros().doubleValue());
		List<PlanBreakdownEntry> breakDownList= new ArrayList<>();
		
		// new plan cost
		breakDownList.add(new PlanBreakdownEntry(
						"Plan '"+newPlan.getName()+"' 1 "+u.getText("Profile.plans.year"),
						newPlanCost,
						BREAKDOWN_ENTRY_TYPE.product,
						"€ "+u.getText("Profile.plans.taxExcluded")));
		totalYearlyCostHT+=newPlanCost;
		
		// discount if any
		Date now = new Date();
		Long curPlanRemainingDays = (u.getPlanEndDate().getTime() - now.getTime())/(1000*3600*24) ;
		Double discountValue=0.0;
		if (curPlan.getAvailableForPurchase()==true 
				&& curPlanCost<=newPlanCost && curPlanRemainingDays>=NB_DAYS_PLAN_DISCOUNT) {
			discountValue=round2digits(curPlanCost/2);
			breakDownList.add(new PlanBreakdownEntry(
					u.getText("Profile.plans.discount") + " -50% "
								+u.getText("Profile.plans.currentPlan")
								+" <span style='color:black'>"+curPlan.getName()+"</span>",
					-discountValue,
					BREAKDOWN_ENTRY_TYPE.discount,
					"€"));
			totalYearlyCostHT-=discountValue;
		}
		// totalHT		
		breakDownList.add(new PlanBreakdownEntry(
				"Total "+u.getText("Profile.plans.taxExcluded"),
				totalYearlyCostHT,
				BREAKDOWN_ENTRY_TYPE.totalht,
				"€"));
		
		// tax
		Double taxRate = Double.valueOf(Globals.GetMxProperty("mx.payment.taxrate"));
		Double taxCost = round2digits((taxRate/100)*totalYearlyCostHT); 
		breakDownList.add(new PlanBreakdownEntry(
				u.getText("Profile.plans.tax")+" "+taxRate+"%",
				taxCost,
				BREAKDOWN_ENTRY_TYPE.tax,
				"€"));
		
		// grand total with taxes
		totalYearlyCost=totalYearlyCostHT+taxCost;
		breakDownList.add(new PlanBreakdownEntry(
				"Total",
				totalYearlyCost,
				BREAKDOWN_ENTRY_TYPE.total,
				"€"));
		
		answerMsg.setTotalCost(totalYearlyCost);
		answerMsg.setBreakdownEntries(breakDownList);
		
		return true;
	}
    
    /*
     		Browser          				Server               	Payment Service (Paypal,...)
     			
     	    
     		   -----ask payment details--------> 
     		   <----send back breakdown details-
     		   
     		   ---------------------------process payment------------->
     		   <------------------------transaction confirmation-------
     		   
     		   
     		   -----------notify payment----->
     		   								  --------confirm payment-->
     		   								  <----payment confirmed----
     		   								  
     		   							[update user plan]
     		   							
     		   	<--------notify plan update---
     
     */
    
    
    @MessageMapping("/update_plan_request")
    @SubscribeMapping ("/user/queue/update_plan_answer")
    public void handleUserPlanUpdateRequest(SimpMessageHeaderAccessor headerAccessor, 
    									WsMsgUpdateUserPlan_request requestMsg) throws Exception {
    	
    	WsMsgUpdateUserPlan_answer answer = new WsMsgUpdateUserPlan_answer(requestMsg);
		IUserProfileData user = getUserProfile(headerAccessor);
				
    	try {
    		if (!requestMsg.getUserId().equals(user.getId())) {
    			answer.setRejectMessage(user.getText("Profile.plans.wrongUser"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_answer", answer);
    			return;
    		}
    		if (!user.isLoggedIn() || !user.isEnabled()) {
    			answer.setRejectMessage(user.getText("Profile.plans.userNotLoggedIn"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_answer", answer);
    			return;
    		}
    		
    		DateFormat timestampFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
    		String transactionId = requestMsg.getUserId()+"-"+requestMsg.getPlanId()+"-"+timestampFormat.format(new Date());
    		answer.setTransactionId(transactionId);
    		if (buildPlanUpdateCheckoutDetails(user,requestMsg,answer)==false) {
    			answer.setRejectMessage(user.getText("Profile.plans.couldNotComputeTransaction"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_answer", answer);
    			return;
    		}
    		
    		answer.setIsSuccess(true);
    		_awaitingPaymentPlanUpdates.add(answer);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_answer", answer);			
	    	
    		Globals.GetStatsMgr().handleStatItem(new UpdatePlanRequestMxStat(user,
    																transactionId,
    																user.getPlanId(),
    																requestMsg.getPlanId(),
    																answer.getTotalCost()));
    		
    	} catch (DataProcessException e) 
    	{
    		answer.setRejectMessage(user.getText("Profile.plans.couldNotComputeTransaction"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_answer", answer);
			log.error("unable to prepare user plan purchase : "+e.getMessage());
			e.printStackTrace();
			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_plan_request"));
			return;    		
    	}
    }

    
    @MessageMapping("/update_plan_confirm_payment_request")
    @SubscribeMapping ("/user/queue/update_plan_confirm_payment_answer")
    public void handleUserPlanUpdateRequest(SimpMessageHeaderAccessor headerAccessor, 
    									WsMsgUpdateUserPlanPaymentConfirm_request requestMsg) throws Exception {
    	
    	WsMsgUpdateUserPlanPaymentConfirm_answer answer = new WsMsgUpdateUserPlanPaymentConfirm_answer(requestMsg);
		IUserProfileData user = getUserProfile(headerAccessor);
				
    	try {
    		if (!requestMsg.getUserId().equals(user.getId())) {
    			answer.setRejectMessage(user.getText("Profile.plans.wrongUser"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
    			return;
    		}
    		if (!user.isLoggedIn() || !user.isEnabled()) {
    			answer.setRejectMessage(user.getText("Profile.plans.userNotLoggedIn"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
    			return;
    		}
    		
    		String transactionId = requestMsg.getTransactionId();
    		WsMsgUpdateUserPlan_answer awaitingTransactionData = _awaitingPaymentPlanUpdates.stream()
					.filter(p -> p.getTransactionId().equals(transactionId))
					.findFirst()
					.orElse(null);
    		if (awaitingTransactionData==null) {
    			answer.setRejectMessage(user.getText("Profile.plans.noSuchTransactionAwaiting"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
    			return;
    		}    	
    		
    		String paymentEntry = 
    				   "transactionId="+requestMsg.getTransactionId()
    				+"\tuserId="+user.getId()
    				+"\tmethod="+requestMsg.getPaymentMethod()
    				+"\tcost="+requestMsg.getTotalCost()
    				+"\tplanId="+requestMsg.getPlanId()
    				;
    		
    		IPaymentInterface pi = null;
    		if (requestMsg.getPaymentMethod()==PAYMENT_METHOD.paypal) 
    		{ 
    			pi = new PaypalPaymentInterface(Globals.GetMxProperty("mx.payment.login"),
    						 							   Globals.GetMxProperty("mx.payment.password")); 
    		}
    		else if (Globals.Get().isDevMode() && requestMsg.getPaymentMethod()==PAYMENT_METHOD.sandbox) {
    			pi = new SandboxPaymentInterface(); 
    		}
    		else {
    			PaymentLogging.logger.error(paymentEntry+"\t"+"status=NOT_CONFIRMED");
    			answer.setRejectMessage(user.getText("Profile.plans.paymentMethodUnsupported",requestMsg.getPaymentMethod().toString()));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
    			
    			user.sendEmailCCiAdmin(  user.getText("Profile.plans.email.purchaseNotConfirmed.title",requestMsg.getPaymentMethod().toString()), 
										user.getText("Profile.plans.email.purchaseNotConfirmed.body",user.getNickname(),
														Globals.Get().getWebAppBaseUrl(),
														requestMsg.getTransactionId()));

    			return;
    		}
    		
    		// given time for payment to be available in paypal DB
    		// so retry several time
    		Boolean paymentConfirmed = false;
    		for (Integer nbTry=0;nbTry<NB_TRIES_FOR_PAYMENT_CONFIRMATION;nbTry++) {
    			
    			if (nbTry>0) { Thread.sleep(3000); }
    			paymentConfirmed = pi.confirmPayment(
        				requestMsg.getTransactionId(), 
        				awaitingTransactionData.getTotalCost(),
        				requestMsg.getPaymentDetails());
    			
    			if (paymentConfirmed) { break; }
    			if (nbTry<NB_TRIES_FOR_PAYMENT_CONFIRMATION) {user.sendGuiInfoMessage(user.getText("Profile.plans.noPaymentReceivedYet")); }
    			       		
    		}
    		    		    		
    		if (!paymentConfirmed) {
    			PaymentLogging.logger.error(paymentEntry+"\t"+"status=NOT_CONFIRMED");
    			answer.setRejectMessage(user.getText("Profile.plans.noPaymentReceived"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
    			
    			user.sendEmailCCiAdmin(  user.getText("Profile.plans.email.purchaseNotConfirmed.title",requestMsg.getPaymentMethod().toString()), 
										user.getText("Profile.plans.email.purchaseNotConfirmed.body",user.getNickname(),
																	Globals.Get().getWebAppBaseUrl(),
																	requestMsg.getTransactionId()));

    			return;
    		}
    		
    		IPlan plan = Globals.Get().getPlansMgr().getPlan(requestMsg.getPlanId());
    		if (plan==null) {
    			
    			PaymentLogging.logger.error(paymentEntry+"\t"+"status=UNKNOWN_PLAN_ID");
    			log.error("User '"+user.getName()+"' purchased unknown plan '"+requestMsg.getPlanId()+"' (transactionId="+requestMsg.getTransactionId()+")");
    			
    			user.sendEmailCCiAdmin( user.getText("Profile.plans.email.purchaseNotFinalized.title",requestMsg.getPaymentMethod().toString()), 
									   user.getText("Profile.plans.email.purchaseNotFinalized.body",user.getNickname(),requestMsg.getTransactionId()));
    			
    			return;
    		}
    		

    		// update user data with new plan    		
    		try {
    			user.setPlanId(requestMsg.getPlanId());
    			user.setPlanStartDate(new Date());
    			//  set end-date one year later
    			Integer newPlanDurationYear = 1;
    			Calendar cal = new GregorianCalendar();
    			cal.setTime(user.getPlanStartDate());
    			cal.add(Calendar.YEAR,newPlanDurationYear);
    			cal.add(Calendar.DAY_OF_MONTH,-1);
    			user.setPlanEndDate(cal.getTime());       			
	    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getCreateOrUpdatePlanIntoDbStmt(user).execute();    		
	    		if (result==false) {
	    			PaymentLogging.logger.error(paymentEntry+"\t"+"status=SQL_UPDATE_REFUSED");
	    			answer.setRejectMessage(user.getText("Profile.plans.paymentOkButDBError"));
	    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
	    			
	    			user.sendEmailCCiAdmin( user.getText("Profile.plans.email.purchaseNotAppliedYet.title",requestMsg.getPaymentMethod().toString()), 
	    									user.getText("Profile.plans.email.purchaseNotAppliedYet.body",user.getNickname(),requestMsg.getTransactionId()));
	    			return;
	    		}
    		} catch (Throwable t) {
    			PaymentLogging.logger.error(paymentEntry+"\t"+"status=EXCEPTION "+t.getMessage());
    			answer.setRejectMessage(user.getText("Profile.plans.paymentOkButServerError"));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
    			
    			user.sendEmailCCiAdmin( user.getText("Profile.plans.email.purchaseNotAppliedYet.title",requestMsg.getPaymentMethod().toString()), 
						user.getText("Profile.plans.email.purchaseNotAppliedYet.body",user.getNickname(),requestMsg.getTransactionId()));
    			
    			return;
    		}
    		
    		PaymentLogging.logger.info(paymentEntry+"\t"+"status=SUCCESS");
    		
    		user.loadFullUserData();
    		
    		
    		answer.setIsSuccess(true);
    		_awaitingPaymentPlanUpdates.remove(awaitingTransactionData);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);			
	    	
    		Globals.GetStatsMgr().handleStatItem(new UpdatePlanPaymentMxStat(user,
    																transactionId,
    																user.getPlanId(),
    																requestMsg.getPlanId(),
    																awaitingTransactionData.getTotalCost()));
    		
    		user.sendEmailCCiAdmin( user.getText("Profile.plans.email.purchaseOk.title",plan.getName()), 
					user.getText("Profile.plans.email.purchaseOk.body",user.getNickname(),
																	   plan.getName(),
																	   requestMsg.getTotalCost().toString(),
																	   user.getPlanEndDate().toString(),
																	   requestMsg.getTransactionId()));
    		

    	} catch (Throwable e) {
    		answer.setRejectMessage(user.getText("Profile.plans.planUpdateFailed"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/update_plan_confirm_payment_answer", answer);
			log.error("Unable to finalize plan purchase : "+e.getMessage());
			e.printStackTrace();
			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_plan_confirm_payment_answer"));
			
			user.sendEmailCCiAdmin( user.getText("Profile.plans.email.purchaseNotFinalized.title",requestMsg.getPaymentMethod().toString()), 
					   user.getText("Profile.plans.email.purchaseNotFinalized.body",user.getNickname(),requestMsg.getTransactionId()));
    	}
    }

    
   
    @SendTo ("/user/queue/gui_messaging_chat")
    public void sendUserGuiChatMessage(	IUserProfileData user, ICatalog c, ICatalogChatMsg msg) throws Exception {

    		List<ICatalogChatMsg> messagesList = new ArrayList<>();
    		messagesList.add(msg);
    		messageSender.convertAndSendToUser(user.getName(), 
					"/queue/gui_messaging_chat", new WsUserGuiChatMessage(c,messagesList));
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
    

    @SendTo( "/user/queue/session_status")
    public void sendSessionStatusExpired(IUserProfileData user) throws Exception {
    	WsMsgUserSessionStatus_answer msg = new WsMsgUserSessionStatus_answer(SESSION_STATUS.EXPIRED);       	
		this.messageSender.convertAndSendToUser(user.getName(),
									"/queue/session_status", msg);        	
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
