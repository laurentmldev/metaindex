package metaindex.app.beans;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.app.control.websockets.users.PaymentLogging;
import metaindex.data.commons.globals.plans.IPlansManager;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * @author Laurent ML
 */
public class BeanSignupConfirmEmail extends BeanSignupSendEmail {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanSignupConfirmEmail.class);

  	
	@Override
  	public void prepare() throws Exception { 
		super.prepare();
		
		HttpServletRequest request=ServletActionContext.getRequest();
		String idStr = request.getParameter("requestId");
		Long idLong = new Long(idStr);
		this.setRequestId(idLong);
		
		String email=request.getParameter("email");
		this.setEmail(email);
		
	}
	/**
	 * Synchronized to ensure that it is not possible that 2 persons 
	 * create at the same time an account with same email
	 */
	@Override
  	synchronized public String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			
			BeanSignupSendEmail.AwaitingAccount a = getAwaitingAccount(getEmail(),getRequestId());
			
			if (a==null) { 
				return BeanProcessResult.BeanProcess_ERROR.toString();
			}
			
			clearAwaitingAccount(getEmail());
			
			// creating SQL entry for new user
			// default password is dummy, will have to be reset by user
			IUserProfileData u = new UserProfileData();
			u.setName(a.email);
			u.setNickname(a.properties.get("nickname").toString());
			
    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
    														.getCreateUserIntoSqlDbStmt(u).execute();	    		    		
    		if (!result) {
    			log.error("unable to create new user '"+a.email+"'");
    			return BeanProcessResult.BeanProcess_ERROR.toString();    			
    		}
    		// retrieve user_id from SQL db
    		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
    				.getPopulateUserProfileIdFromDbStmt(u)
    				.execute();
    		
    		// assign 'USER' role
    		result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
    							.getCreateorUpdateUserRoleIntoSqlDbStmt(u,USER_ROLE.ROLE_USER).execute();	    		    		
    		if (!result) {
    			log.error("unable to assign role to new user '"+a.email+"'");
    			return BeanProcessResult.BeanProcess_ERROR.toString();    			
    		}
    		   
    		// assign default plan to user
    		u.setPlanId(IPlansManager.DEFAULT_PLAN_ID);
			u.setPlanStartDate(new Date());
			//  set end-date one year later
			Integer newPlanDurationYear = 1;
			Calendar cal = new GregorianCalendar();
			cal.setTime(u.getPlanStartDate());
			cal.add(Calendar.YEAR,newPlanDurationYear);
			cal.add(Calendar.DAY_OF_MONTH,-1);
			u.setPlanEndDate(cal.getTime());       			
    		result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getCreateOrUpdatePlanIntoDbStmt(u).execute();    		
    		if (result==false) {
    			log.error("unable to assign default plan to new user '"+a.email+"'");
    			return BeanProcessResult.BeanProcess_ERROR.toString();
    		}
    		
    		// prepare next step : reset password
    		BeanResetPwd.SignalComingUserPasswdReset(u.getId());
    		
		} catch (Throwable e) {
  			e.printStackTrace();
  			return BeanProcessResult.BeanProcess_ERROR.toString();
  		} 
		
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}

}  
