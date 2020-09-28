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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * @author Laurent ML
 */
public class BeanResetPwdSendEmail extends ABeanEmailConfirmedAction {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanResetPwdSendEmail.class);
	

	// list is different between signup and reset password
	private static Map<String,AwaitingAccount> _awaitingAccountsByEmail=new ConcurrentHashMap<>();
	@Override
	protected Map<String,AwaitingAccount>  getAwaitingAccountsByEmailMap() { return _awaitingAccountsByEmail; }
	
	@Override
  	public void prepare() throws Exception { 
		super.prepare();
		
		HttpServletRequest request=ServletActionContext.getRequest();
		
		String email=request.getParameter("email");
		if (email!=null && email.length()>0) {
			this.setEmail(email);
		}
		
	}
	
	/**
	 * Synchronized to allow temporisation to be effective in case of floading attack
	 */
	@Override
  	public synchronized String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			
			// try to limit hardcore overflow. 
			// That might work since this method in synchronized
			Thread.sleep(2000);

			// get from current user session
			IUserProfileData activeUser = Globals.Get().getUsersMgr().getUserByName(getEmail());
			if (activeUser==null) {
				return "invaliduserid";
			}
			
			clearOldRequests();
			
			if (awaitingAccountExists(getEmail())) {
				return "alreadywaiting";
			}
			
			AwaitingAccount accountWaitingForEmailConfirmation = new AwaitingAccount(getEmail());
			addAwaitingAccount(accountWaitingForEmailConfirmation);
			
			String confirmationLink="https://metaindex.fr:8443/metaindex/resetpwd_confirmemail?" 
					+"email="+accountWaitingForEmailConfirmation.email
					+"&requestId="  
					+accountWaitingForEmailConfirmation.randomRequestId;
						
			try {
				
				activeUser.sendEmail( 
						activeUser.getText("passwordreset.email.title"),
						activeUser.getText("passwordreset.email.body",activeUser.getNickname(),confirmationLink)
						);
			
			} catch (DataProcessException e) {
				getAwaitingAccountsByEmailMap().remove(accountWaitingForEmailConfirmation.email);
				log.error(e.getMessage());							
				return BeanProcessResult.BeanProcess_ERROR.toString();
			}
					
			
		} catch (Throwable e) {
  			e.printStackTrace();		  			  			  			  			
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
