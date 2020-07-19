package metaindex.app.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.app.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * Common Bean for all the 'Profile' JSP pages (profile,createProfile, editProfile).
 * @author Laurent ML
 */
public class BeanSignup extends AMetaindexBean {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanSignup.class);

	// 12h to confirm account
	private static final Integer MAX_ACCOUNT_CONFIRMATION_DELAY_SEC=43200;
	
	private String _email = "";
	private String _nickname = "";
	
	class AwaitingAccount {
		
		public String email="";
		public String nickname="";
		public Date requestDate=new Date();
		public Long randomRequestId=new Random().nextLong();
		
		public AwaitingAccount(String newEmail,String newNickname) throws DataProcessException {
			email=newEmail;
			nickname=newNickname;
		}
		
	}
	
	private static Map<String,AwaitingAccount> _awaitingAccountsByEmail=new ConcurrentHashMap<>();
	
	protected static AwaitingAccount GetAwaitingAccount(String email,Long requestId) {
		AwaitingAccount a = _awaitingAccountsByEmail.get(email);
		if (a==null) { return null; }
		if (!a.randomRequestId.equals(requestId)) { return null; }
		return a;
	}
	
	protected static void ClearAwaitingAccount(String email) {
		_awaitingAccountsByEmail.remove(email);		
	}
	
	/**
	 * Synchronized to ensure that it is not possible to create together an account with same email
	 */
	@Override
  	synchronized public String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			
			// clean too old requests
			Date now = new Date();			
			for (AwaitingAccount awaitingAccount : _awaitingAccountsByEmail.values()) {
				Date creationDate = awaitingAccount.requestDate;
				if (now.toInstant().getEpochSecond()
							-creationDate.toInstant().getEpochSecond()
							> MAX_ACCOUNT_CONFIRMATION_DELAY_SEC) {
					_awaitingAccountsByEmail.remove(awaitingAccount.randomRequestId);
				}
			}
			
			//HttpServletRequest request=ServletActionContext.getRequest();
			
			// check that there isn't already a user with same email
			IUserProfileData existingUser = Globals.Get().getUsersMgr().getUserByName(this.getEmail());
			if (existingUser!=null) {
				return "emailalreadyinuse";
			}

			// check if there isn't already such an account waiting for confirmation
			// TODO: maybe in this case re-send a confirmation email?
			
			
			if (_awaitingAccountsByEmail.containsKey(getEmail())) {
				return "newemailalreadywaiting";
			}
			
			AwaitingAccount newAccountWaitingForEmailConfirmation = new AwaitingAccount(getEmail(),getNickname());
			_awaitingAccountsByEmail.put(newAccountWaitingForEmailConfirmation.email, 
										 newAccountWaitingForEmailConfirmation);
			
			String msgBodyHtml="<center>"
					+"<h3>Hello "+this.getNickname()+",</h3><br/><br/>"
					+"Thank you very much for your registration to MetaindeX. We hope you'll enjoy using it very much.<br/><br/>"
					+"Please click "
					+"<a href=\"https://metaindex.fr:8443/metaindex/signup_confirmemail?"
						+"email="
							+newAccountWaitingForEmailConfirmation.email
						+"&requestId="
							+newAccountWaitingForEmailConfirmation.randomRequestId+"\" >Here</a>"
					+" to finalize your account and you'll be done yeehaaa!<br/><br/>"					
					;
			
			try {
				Globals.Get().sendEmail(getEmail(), 
					"Account Confirmation", 
					msgBodyHtml);				
			} catch (DataProcessException e) {
				// tmp			
				log.info("### sent email with req id "+newAccountWaitingForEmailConfirmation.randomRequestId);
			
				/*
				_awaitingAccountsByEmail.remove(newAccountWaitingForEmailConfirmation.email);
				log.error(e.getMessage());	
				TOTO: send admin email			
				return BeanProcessResult.BeanProcess_ERROR.toString();
				 */
			}
					
			
		} catch (Throwable e) {
  			e.printStackTrace();		  			  			  			  			
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
	public void setEmail(String email) {
		_email=email;
	}
	public String getEmail() {
		return _email;
	}
	
	public void setNickname(String nickname) {
		_nickname=nickname;
	}
	public String getNickname() {
		return _nickname;
	}
}  
