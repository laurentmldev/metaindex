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

import javax.servlet.http.HttpServletRequest;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * @author Laurent ML
 */
public class BeanResetPwdConfirmEmail extends BeanResetPwdSendEmail {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanResetPwdConfirmEmail.class);
	
  	
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
	 * Ensure that a request has been seen before
	 */
	@Override
  	public String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			
			BeanResetPwdSendEmail.AwaitingAccount a = getAwaitingAccount(getEmail(),getRequestId());
			
			if (a==null) {
				return BeanProcessResult.BeanProcess_ERROR.toString();
			}
			
			clearAwaitingAccount(getEmail());
			
			IUserProfileData activeUser = Globals.Get().getUsersMgr().getUserByName(getEmail());
			if (activeUser==null) {
				return "invaliduserid";
			}
			
			// prepare next step : reset password
    		BeanResetPwd.SignalComingUserPasswdReset(activeUser.getId());
    		
			
		} catch (Throwable e) {
  			e.printStackTrace();
  			return BeanProcessResult.BeanProcess_ERROR.toString();
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
