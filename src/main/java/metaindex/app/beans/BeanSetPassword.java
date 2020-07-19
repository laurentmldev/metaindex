package metaindex.app.beans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * Common Bean for all the 'Profile' JSP pages (profile,createProfile, editProfile).
 * @author Laurent ML
 */
public class BeanSetPassword extends BeanSignup {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanSignupConfirmEmail.class);

	private Long _requestId=null;
	private String _clearPassword="";
  	
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
			
			IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(this.getEmail());
			if (u==null ) {
    			log.error("unable to reset password for user '"+getEmail()+"', no such user.");
    			return BeanProcessResult.BeanProcess_ERROR.toString();				
			}
			
			u.setName(getEmail());
			Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
					.getPopulateUserProfileIdFromDbStmt(u).execute();	    		    		
			
			u.setPasswordAndEncrypt(getClearPassword());
			
    		Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
    												.getUpdatePassswordIntoDbStmt(u).execute();	    		    		
    		if (!result) {
    			log.error("unable to update user '"+u.getName()+"' password.");
    			return BeanProcessResult.BeanProcess_ERROR.toString();    			
    		}
    			
		} catch (Throwable e) {
  			e.printStackTrace();
  			return BeanProcessResult.BeanProcess_ERROR.toString();
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
	public void setRequestId(Long requestId) {
		_requestId=requestId;
	}
	public Long getRequestId() {
		return _requestId;
	}

	public String getClearPassword() {
		return _clearPassword;
	}

	public void setClearPassword(String _clearPassword) {
		this._clearPassword = _clearPassword;
	}
}  
