package metaindex.app.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.app.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

    
/** 
 * @author Laurent ML
 */
public class BeanResetPwd extends BeanResetPwdSendEmail {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanSignupConfirmEmail.class);

	private String _clearPassword="";
	
	private static List<Integer> _expectedPasswordReset = new CopyOnWriteArrayList<>();
  	
	public static void SignalComingUserPasswdReset(Integer uid) { 
		if (!_expectedPasswordReset.contains(uid)) {
			_expectedPasswordReset.add(uid);
		}
	}
	/**
	 * Synchronized to ensure that it is not possible that 2 persons 
	 * create at the same time an account with same email
	 */
	@Override
  	public String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			if (Globals.isInStandaloneMode()) {
				return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
			}
			
			
			IUserProfileData u = Globals.Get().getUsersMgr().getUserByName(this.getEmail());
			if (u==null ) {
    			log.error("unable to reset password for user '"+getEmail()+"', no such user.");
    			return BeanProcessResult.BeanProcess_ERROR.toString();				
			}
			
			if (!_expectedPasswordReset.contains(u.getId())) {
				log.error("unexpected operation : reset password for user '"+getEmail()+"'.");
    			return BeanProcessResult.BeanProcess_ERROR.toString();
			}
			_expectedPasswordReset.remove(u.getId());
			
			u.setName(getEmail());
			
			if (!isStandaloneModeAuthorized(u)) {
				log.error("Standaloe user trying to be used in non-standalone mode");
				return BeanProcessResult.BeanProcess_ERROR.toString();
			}
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
		
		clearAwaitingAccount(getEmail());
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
	public String getClearPassword() {
		return _clearPassword;
	}

	public void setClearPassword(String _clearPassword) {
		this._clearPassword = _clearPassword;
	}
}  
