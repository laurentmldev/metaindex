package metaindex.app.beans;

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
public class BeanContactUs extends BeanProfile {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanContactUs.class);
	
	private String _email = "";
	private String _msg = "";
	private String _topic = "";
	private String _origin = "";
	
	/**
	 * Synchronized to allow temporisation to be effective in case of floading attack
	 */
	@Override
  	public synchronized String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to process contact-form message");								
			}
			if (Globals.GetMxProperty("mx.runmode").equals(Globals.MX_RUNMODE_STANDALONE)) {
				return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
			}
			
			if (getMsg().length()==0 || getEmail().length()==0) {
				log.error("Empty msg or email, skipped");
				return BeanProcessResult.BeanProcess_ERROR.toString();
			}
			Boolean isAUser=false;
			// get from current user session
			IUserProfileData activeUser = Globals.Get().getUsersMgr().getUserByName(getEmail());
			if (activeUser!=null) {
				isAUser=true;
			}
						
			try {
				
				// try to limit hardcore overflow. 
				// That might work since this method in synchronized
				Thread.sleep(2000);
				
				if (isAUser) {
					Globals.Get().sendEmail(
							Globals.GetMxProperty("mx.mailer.admin_recipient"),
							"A message from user '"+activeUser.getNickname()+"' for "+getTopic(),
							 "From <b>"+activeUser.getName()+"</b> ("+activeUser.getNickname()+")<br/><br/>"
							+getMsg()
							+"<br/><hr/>"
							);
				} else {
					Globals.Get().sendEmail(
							Globals.GetMxProperty("mx.mailer.admin_recipient"),
							"A message from a visitor '"+getEmail()+"' for "+getTopic(),
							"Message : <br/><hr/><br/>"+getMsg()+"<br/><hr/>"
							);
				}
			
			} catch (DataProcessException e) {
				log.error(e.getMessage());							
				return BeanProcessResult.BeanProcess_ERROR.toString();
			}
					
			
		} catch (Throwable e) {
  			e.printStackTrace();		  			  			  			  			
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String email) {
		// don't why there is a ', ' appended at the end when received from form 
		this._email = email.replaceAll(",\\s*$", "");
	}

	public String getMsg() {
		return _msg;
	}

	public void setMsg(String msg) {
		this._msg = msg;
	}

	public String getTopic() {
		return _topic;
	}

	public void setTopic(String _topic) {
		this._topic = _topic;
	}

	public String getOrigin() {
		return _origin;
	}

	public void setOrigin(String _origin) {
		this._origin = _origin;
	}
	
}  
