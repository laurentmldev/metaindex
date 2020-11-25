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
import org.apache.struts2.ServletActionContext;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import metaindex.data.userprofile.UserProfileData;
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
	private CATEGORY _category = CATEGORY.STUDENT_SEARCHER;
	
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
			IUserProfileData tmpUser = new UserProfileData();
			String languageShortname =getSessionLanguage(ServletActionContext.getRequest());
			if (languageShortname!=null) {
				IGuiLanguage lang = Globals.Get().getGuiLanguagesMgr().getGuiLanguage(languageShortname.toUpperCase());
				Integer guiLanguageId =lang.getId();
				tmpUser.setGuiLanguageId(guiLanguageId);
			}
			
			AwaitingAccount newAccountWaitingForEmailConfirmation = new AwaitingAccount(getEmail());
			newAccountWaitingForEmailConfirmation.properties.put("nickname",getNickname());
			newAccountWaitingForEmailConfirmation.properties.put("guilanguageid",tmpUser.getGuiLanguageId());
			newAccountWaitingForEmailConfirmation.properties.put("category",getCategory());
			addAwaitingAccount(newAccountWaitingForEmailConfirmation);
			
			String confirmationLink=Globals.Get().getWebAppBaseUrl()+"/signup_confirmemail?" 
					+"email="+newAccountWaitingForEmailConfirmation.email
					+"&requestId="  
					+newAccountWaitingForEmailConfirmation.randomRequestId;
			try {

				// try to limit hardcore overflow. 
				// That might work since this method in synchronized
				Thread.sleep(2000);
				
				Globals.Get().sendEmail(getEmail(), 
						tmpUser.getText("signup.registration.email.title"),
						tmpUser.getText("signup.registration.email.body",getNickname(),confirmationLink,
								Globals.GetMxProperty("mx.drive.sftp.port")));				
			
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

	public CATEGORY getCategory() {
		return _category;
	}

	public void setCategory(CATEGORY _category) {
		this._category = _category;
	}
}  
