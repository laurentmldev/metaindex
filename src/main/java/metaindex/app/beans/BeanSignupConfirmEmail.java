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

import javax.servlet.http.HttpServletRequest;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.commons.globals.plans.IPlansManager;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
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
			
			// try to limit hardcore overflow. 
			// That might work since this method in synchronized
			Thread.sleep(2000);
			
			
			// if user was already created, we just forward to password page
			// this happens if user clicked several times on the link
			// (or if outlook rewrites the link to check security ... :/ )
			IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(a.email);
			if (u!=null) {
				BeanResetPwd.SignalComingUserPasswdReset(u.getId());
				return BeanProcessResult.BeanProcess_SUCCESS.toString();
			}
			
			// creating SQL entry for new user
			// default password is dummy, will have to be reset by user
			u = new UserProfileData();
			u.setName(a.email);
			u.setNickname(a.properties.get("nickname").toString());
			u.setGuiLanguageId((Integer)a.properties.get("guilanguageid"));
			u.setCategory((CATEGORY)a.properties.get("category"));
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
    		u.setPlanId(Globals.Get().getPlansMgr().getDefaultPlan(u.getCategory()).getId());
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
    		
    		Globals.Get().sendEmail(Globals.GetMxProperty("mx.mailer.admin_recipient"), "New user registered : "+u.getNickname(), 
    						"Dear and beloved administrator,<br/><br/>"
    						+"A new user named <b>'"+u.getNickname()+"'</b> just created an account in MetaindeX.<br/><br/>"
    						+"Please take care of him/her!");
    		
		} catch (Throwable e) {
  			e.printStackTrace();
  			return BeanProcessResult.BeanProcess_ERROR.toString();
  		} 
		
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}

}  
