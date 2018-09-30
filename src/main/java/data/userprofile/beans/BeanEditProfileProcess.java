package metaindex.data.userprofile.beans;  

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;


public class BeanEditProfileProcess extends BeanProfile {  
  
	private static final long serialVersionUID = -2616542792633700713L;
	private Log log = LogFactory.getLog(BeanEditProfileProcess.class);
	
	@Override
	public String execute()
	{  
		try 
		{ 
			// update database using data sent by the user 
			this.getLoggedUserProfile().commit(this.getLoggedUserProfile());
			// Refresh bean data from Database for display
			// No difference if the update was successful or not, 
			// we want to show the user a GUI with data as it's in the DB 
			this.getLoggedUserProfile().update(this.getLoggedUserProfile());
		}
		catch (DataAccessErrorException|DataReferenceErrorException e) {

				log.error("Error while udating data for user '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
				return BeanProcessResult.BeanProcess_DBERROR.toString(); 
		} catch (DataAccessConstraintException e) {

			log.error("Constraint Error while udating data for user '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}
		
		// refresh Session guitheme variable and language config
		setSessionLanguage(getLoggedUserProfile().getGuiLanguageShort(), ActionContext.getContext());
		setSessionGuiTheme(getLoggedUserProfile().getGuiThemeShort(),ServletActionContext.getRequest());
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();				
	}

	
}  
