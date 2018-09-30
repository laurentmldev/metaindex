package metaindex.data.userprofile.beans;  

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

public class BeanCreateProfileProcess extends BeanProfile {  
  
	private Log log = LogFactory.getLog(BeanCreateProfileProcess.class);
	private static final long serialVersionUID = 5554322670053423884L;
	private String clearPassword = "";
	private String acceptConditions;
		
	@Override
	public String execute()
	{  
		
		if (checkPassword(clearPassword) != 0) 
		{ 
			addActionError("Sorry, password too simple, try to be more creative please!");
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();	
		}
		
		// prepare Username field from session
		try {
			// we have filled the user data with all necessary information (from User form)
			this.getLoggedUserProfile().create(this.getLoggedUserProfile());
		}
		catch (DataAccessErrorException e) {
			addActionError("An error occured while create profile for user '"+this.getLoggedUserProfile().getUsername()+"'");
			log.error("An error occured while create profile for user '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
			return BeanProcessResult.BeanProcess_ERROR.toString();
		}
		catch (DataAccessConstraintException e) {
			addActionError("Duplicate data for new user profile '"+this.getLoggedUserProfile().getUsername()+"'");
			log.error("Duplicate data for new user profile '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}
		
		addActionMessage(getText("createProfile.success"));							
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getLoggedUserProfile().update(this.getLoggedUserProfile()) ;}
		catch (Exception e)
		{ 
			log.error("Unable to refresh data for new user profile '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString(); 
	}


	// needed for input fields validation
	public String getPassword(String password) {
		return clearPassword;
	}
	public void setPassword(String password)
	{
		this.clearPassword = password;
		this.setPasswordAndEncrypt(password); 
	}
	
	//public boolean getAcceptConditions(){ return acceptConditions; }
	//public void setAcceptConditions(boolean acceptConditions){ this.acceptConditions = acceptConditions; }
	public String getAcceptConditions(){ return acceptConditions; }
	public void setAcceptConditions(String acceptConditions){ this.acceptConditions = acceptConditions; }
	  
	protected int checkPassword(String password) {
		log.warn("No Password Complexity Check. TO DO !");
		return 0;
	}
	
	 
}  
