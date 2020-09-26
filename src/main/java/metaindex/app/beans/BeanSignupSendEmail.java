package metaindex.app.beans;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * Common Bean for all the 'Profile' JSP pages (profile,createProfile, editProfile).
 * @author Laurent ML
 */
public class BeanSignupSendEmail extends ABeanEmailConfirmedAction {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanSignupSendEmail.class);

	private static Map<String,AwaitingAccount> _awaitingAccountsByEmail=new ConcurrentHashMap<>();
	
	protected Map<String,AwaitingAccount>  getAwaitingAccountsByEmailMap() { return _awaitingAccountsByEmail; }
	
	private String _nickname = "";
	
	/**
	 * Synchronized to ensure that it is not possible to create together an account with same email
	 */
	@Override
  	synchronized public String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			
			clearOldRequests();
			//HttpServletRequest request=ServletActionContext.getRequest();
			
			// check that there isn't already a user with same email
			IUserProfileData existingUser = Globals.Get().getUsersMgr().getUserByName(this.getEmail());
			if (existingUser!=null) {
				return "emailalreadyinuse";
			}

			// check if there isn't already such an account waiting for confirmation
			// TODO: maybe in this case re-send a confirmation email?
			
			
			if (awaitingAccountExists(getEmail())) {
				return "newemailalreadywaiting";
			}
			
			AwaitingAccount newAccountWaitingForEmailConfirmation = new AwaitingAccount(getEmail());
			newAccountWaitingForEmailConfirmation.properties.put("nickname",getNickname());
			addAwaitingAccount(newAccountWaitingForEmailConfirmation);
			
			String confirmationLink="https://metaindex.fr:8443/metaindex/signup_confirmemail?" 
					+"email="+newAccountWaitingForEmailConfirmation.email
					+"&requestId="  
					+newAccountWaitingForEmailConfirmation.randomRequestId;
			String msgBodyHtml="<center>"
					+"<h3>Hello "+this.getNickname()+",</h3><br/><br/>"
					+"Thank you very much for your registration to MetaindeX. We hope you'll enjoy using it very much.<br/><br/>"
					+"Please click "
					+"<a href=\""+confirmationLink+"\" >Here</a>"
					+" to finalize your account and you'll be done yeehaaa!<br/><br/>"					
					;
			
			try {

				Globals.Get().sendEmail(getEmail(), 
					"Account Confirmation", 
					msgBodyHtml);				
			
			} catch (DataProcessException e) {
				getAwaitingAccountsByEmailMap().remove(newAccountWaitingForEmailConfirmation.email);
				log.error(e.getMessage());							
				return BeanProcessResult.BeanProcess_ERROR.toString();				
			}
					
			
		} catch (Throwable e) {
  			e.printStackTrace();		  			  			  			  			
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
	public void setNickname(String nickname) {
		_nickname=nickname;
	}
	public String getNickname() {
		return _nickname;
	}
}  