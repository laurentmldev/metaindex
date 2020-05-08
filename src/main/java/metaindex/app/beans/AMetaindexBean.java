package metaindex.app.beans;


/*
                      GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

 See full version of LICENSE in <https://fsf.org/>
 
 */
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.filter.IFilter;
import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataAccessException;
import toolbox.exceptions.DataProcessException;


public abstract class AMetaindexBean extends ActionSupport implements Preparable  {

	static public enum BeanProcessResult { 	
		BeanProcess_SUCCESS, 
		BeanProcess_ERROR,
		BeanProcess_DBERROR,
		BeanProcess_ConstraintERROR
	};									
	
	protected static final String USERPROFILE_SESSION_ATT="user_profile";
	protected static final String CURRENT_COMMUNITY_SESSION_ATT="current_catalog";
	protected static final String CURRENT_DOCUMENT_SESSION_ATT="current_document_id";
	protected static final String CURRENT_CATALOG_SESSION_ATT="current_filter";
	protected static final String GUITHEME_SESSION_ATT="current_guitheme";

	private static final long serialVersionUID = 1084021485104376111L;
	private Log log = LogFactory.getLog(AMetaindexBean.class);
	private IUserProfileData _userProfileData=null;

	public boolean isUserLogged() { return this.getCurrentUserProfile().getName().length()>0; }
    
	@Override
  	public void prepare() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					Globals.Get().init();
					Globals.Get().setApplicationStatus(APPLICATION_STATUS.RUNNING);								
			}
			
			HttpServletRequest request=ServletActionContext.getRequest();
			
	  		if (_userProfileData==null) {
	  			String sessionId=request.getSession().getId();
	  			
	  			// if LOGGED-IN user : try to get user profile by name if already logged in
	  			if (request.getUserPrincipal()!=null) {
	  				String userName=request.getUserPrincipal().getName();
	  				_userProfileData=Globals.Get().getUsersMgr().getUserByName(userName);	 
	  				if (_userProfileData==null) {
	  					log.error("Could not retrieve data for user '"+userName+"', unable to log-in properly.");
	  					_userProfileData=null;
	  					return;
	  				}
	  				if (_userProfileData.getRemoteAddress().length()==0) {
	  					_userProfileData.setRemoteAddress(request.getRemoteAddr());	  					
	  				}
	  				else if (!_userProfileData.getRemoteAddress().equals(request.getRemoteAddr())) {  
	  					
  	  					log.warn("#### several computers with the same account "+_userProfileData.getName()+":\n"
  	  							+"			   current="+_userProfileData.getRemoteAddress()
  	  										+" newRequest="+request.getRemoteAddr()+"\n"
  	  						);
  	  					String userMsg="Hum it seems that somebody else just tried to use your login credentials, it has been blocked.";
	  	  				_userProfileData.sendGuiWarningMessage(userMsg);  	  				
	  	  				_userProfileData.sendEmail("[METAINDEX] Your account is being used",userMsg);
	  	  				_userProfileData=null;
	  	  				return;
  	  				}
	  					  				
	  			} 
	  			
	  			// if anonymous session : try to get user profile by session id
	  			if (_userProfileData==null) {
	  				_userProfileData=Globals.Get().getUsersMgr().getUserByHttpSessionId(sessionId);
	  			}
	  			
	  			// full new user
				if (_userProfileData==null) { 
					_userProfileData = new UserProfileData();					
				}
											
				if (_userProfileData.getHttpSessionId().length()==0) {
  					_userProfileData.setHttpSessionId(sessionId);
  					Globals.Get().getUsersMgr().registerUser(_userProfileData);  						  				
  				} 
	  		} 
	  		else { log.error("Using found test user to perform operation "+this.getClass().getName()); }

			
			// refresh data of its own account from DB
			if (getCurrentUserProfile()!=null && !getMxStatus().equals("MAINTENANCE") && getCurrentUserProfile().isLoggedIn()) {
				// load all user data from DB
				Globals.Get().getDatabasesMgr().loadFullUserData(getCurrentUserProfile());				
				
				setSessionLanguage(	getCurrentUserProfile().getGuiLanguage().getShortname(),
									ActionContext.getContext());
				
				setSessionGuiTheme(getCurrentUserProfile().getGuiTheme().getShortname(),request);
				
				if (this.getCurrentUserProfile().getCurrentCatalog()!=null) {
							request.getSession().setAttribute(CURRENT_COMMUNITY_SESSION_ATT, 
							this.getCurrentUserProfile().getCurrentCatalog());
				}
			}			
			
		} catch (Throwable e) {
  			Globals.Get().setApplicationStatus(APPLICATION_STATUS.FAILURE);
  			log.error("Unable to load application data : "+e.getMessage());
  			e.printStackTrace();
  			getCurrentUserProfile().setGuiLanguageId(1);
  			getCurrentUserProfile().setGuiThemeId(1);
  			Globals.Get().getCatalogsMgr().getCatalogsList().clear();  			  			  			  			
  		} 
		
  	}
  	
  	protected void setSessionGuiTheme(String themeShort, HttpServletRequest request) {
  		request.getSession().setAttribute(GUITHEME_SESSION_ATT, themeShort);
  	}
  	
  	protected void setSessionLanguage(String languageShort, ActionContext ctx) {
  		if (ctx != null) { 
  			String curLangShortname = getCurrentUserProfile().getGuiLanguage().getShortname();
  			ctx.setLocale( new Locale(curLangShortname));   			
  		}
  	}
  	

	public IUserProfileData getCurrentUserProfile() {
		if (_userProfileData!=null && !_userProfileData.isLoggedIn()
				&& ServletActionContext.getRequest()!=null 
				&& ServletActionContext.getRequest().getUserPrincipal()!=null) {
			
				String username = ServletActionContext.getRequest().getUserPrincipal().getName();				
				_userProfileData.setName(username);
				try {
					_userProfileData.logIn();
				} catch (DataProcessException e) {
					e.printStackTrace();
					return null;
				}
		}
		return _userProfileData;
	}
	
	public ICatalog getCurrentCatalog() {	
		return _userProfileData.getCurrentCatalog();
	}
	public void setCurrentCatalog(ICatalog c) {
		_userProfileData.setCurrentCatalog(c.getId());
	}	
	public IFilter getCurrentFilter() {
		return _userProfileData.getCurrentFilter();
	}
	
	public Integer getCurrentDocumentId() {		
		return _userProfileData.getCurrentDocumentId();
	}	
	
	public String getMxVersion() {	return Globals.GetMxProperty("mx.version"); }
	public String getMxStatus() {	return Globals.GetMxProperty("mx.status"); }
	public String getMxRole() {	return _userProfileData.getRole().toString(); }
	
	/** TODO temporary for avoiding Struts auto populate error on form submit for those fields */
	public void setSubmit(String dummy){}
	/** TODO temporary for avoiding Struts auto populate error on form submit for those fields */
	public void set_csrf(String dummy){}
	/** TODO temporary for avoiding Struts auto populate error on form submit for those fields */
	public void setSecure(String dummy){}
	/** TODO temporary for avoiding Struts auto populate error on form submit for those fields */
	public void setLogout(String dummy){}
	/** TODO temporary for avoiding Struts auto populate error on form submit for those fields */
	public void setError(String error){}
}
