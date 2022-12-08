package metaindex.app.beans;



/*
                      GNU GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

 See full version of LICENSE in <https://fsf.org/>
 
 */

import java.util.Locale;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.filter.IFilter;
import metaindex.data.commons.globals.plans.IPlan;
import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.app.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import toolbox.exceptions.DataProcessException;


public abstract class AMetaindexBean extends ActionSupport implements Preparable  {

	static public enum BeanProcessResult { 	
		BeanProcess_SUCCESS, 
		BeanProcess_ERROR,
		BeanProcess_DBERROR,
		BeanProcess_ConstraintERROR
	};									
	
	protected static final String CURRENT_COMMUNITY_SESSION_ATT="current_catalog";
	protected static final String GUITHEME_SESSION_ATT="current_guitheme";
	protected static final String GUILANGUAGE_SESSION_ATT="current_guilanguage";

	private static final long serialVersionUID = 1084021485104376111L;
	private Log log = LogFactory.getLog(AMetaindexBean.class);
	private IUserProfileData _userProfileData=null;
	private String _latestSessionLanguage="EN";

	public boolean isUserLogged() { return this.getCurrentUserProfile().getName().length()>0; }
    
	// standalone user forbidden in non standalone mode
	public boolean isStandaloneModeAuthorized(IUserProfileData u) {
		if (u!=null && u.getName().equals(Globals.GetMxProperty("mx.standalone.login"))) {
			return false;
		} else { return true; }
	}
	@Override
  	public void prepare() throws Exception {
		
		
		
		try { 
			
			// app initialization is done here in order to ensure that all necessary beans 
			// are available.
			// (there is probably more elegant callback or annotation way to do that).
			// Draw back is that we need a first connection for application to be totally initialized.
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					Globals.Get().init();
					Globals.Get().setApplicationStatus(APPLICATION_STATUS.RUNNING);								
			}
			
			HttpServletRequest request=ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			
			// handle URL parameters if any
			if (request.getParameter("language")!=null) { 
				setSessionLanguage(request.getParameter("language"),request,ActionContext.getContext()); 
			}
					  		
  			// if LOGGED-IN user : try to get user profile by name if already logged in
  			if (request.getUserPrincipal()!=null) {
  				String userName=request.getUserPrincipal().getName();
  				_userProfileData=Globals.Get().getUsersMgr().getExistingUserByName(userName);	 
  				if (_userProfileData==null) {
  					_userProfileData=Globals.Get().getUsersMgr().getUserByHttpSessionId(session.getId());
  					_userProfileData.setName(userName);
  							  				
  				} 
  				assert(_userProfileData!=null);
  				_userProfileData.setRemoteAddress(request.getRemoteAddr());
  				_userProfileData.setHttpSession(session);
				Globals.Get().getUsersMgr().registerUser(_userProfileData);
				
				// (re)load all user data from DB
				// this seems acceptable since user stays quite long on the same page,
				// (several operations are done via websockets, from a single page)
				//
				// That might start to be quite heavy if somesay amount of users and catalogs
				// increase significantly ...
				//
				// if removed, then an explicit loadFullUserData() might be helpful at login time at least,
				// so that user can see the modifs once a backup restore has been performed in the background.
				if (getCurrentUserProfile()==null) {
					log.error("Unable to retrieve data for user name '"+userName+"'");
				} else { getCurrentUserProfile().loadFullUserData(); }
  				
				
  				// check to detect multiple parallel users disabled for now
  				// not robust enough
  				/*
  				if (_userProfileData.getRemoteAddress().length()==0) {
  					_userProfileData.setRemoteAddress(request.getRemoteAddr());	  					
  				}
  				else if (!_userProfileData.getRemoteAddress().equals(request.getRemoteAddr())) {  
  					
  					log.warn("#### several computers with the same account "+_userProfileData.getName()+":\n"
  							+"			   current="+_userProfileData.getRemoteAddress()
  										+" newRequest="+request.getRemoteAddr()+"\n"
  						);
  					String userMsg="Hum it seems that somebody else just tried to use your login credentials, it has been blocked. "
  											+"Please contact your system administrator";
  	  				_userProfileData.sendGuiWarningMessage(userMsg);  	  				
  	  				_userProfileData.sendEmail("[METAINDEX] Your account is maybe being used by somebody else",userMsg);
  	  				_userProfileData=null;
  	  				return;
  				}
  				*/
  					  				
  			} 
  			
  			// if anonymous session : try to get user profile by session id
  			else {
  				_userProfileData=Globals.Get().getUsersMgr().getUserByHttpSessionId(session.getId());
  				if (_userProfileData==null) {
  					_userProfileData=new UserProfileData();
  					_userProfileData.setHttpSession(session);	  
  					Globals.Get().getUsersMgr().registerAnonymousUser(_userProfileData);
  				}
  			}
	  					
			// set user language and guitheme
			if (getCurrentUserProfile()!=null && Globals.Get().getApplicationStatus().equals(APPLICATION_STATUS.RUNNING) && getCurrentUserProfile().isLoggedIn()) {
				
				setSessionLanguage(	getCurrentUserProfile().getGuiLanguage().getShortname(),request,
									ActionContext.getContext());
				
				setSessionGuiTheme(getCurrentUserProfile().getGuiTheme().getShortname(),request);
				
				if (this.getCurrentUserProfile().getCurrentCatalog()!=null) {
							request.getSession(false).setAttribute(CURRENT_COMMUNITY_SESSION_ATT, 
							this.getCurrentUserProfile().getCurrentCatalog());
				}
			} else {
				if (session.getAttribute(GUILANGUAGE_SESSION_ATT)!=null) {
					setSessionLanguage(	session.getAttribute(GUILANGUAGE_SESSION_ATT).toString(),
										request,
										ActionContext.getContext());
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
  		request.getSession(false).setAttribute(GUITHEME_SESSION_ATT, themeShort);
  	}
  	
  	protected void setSessionLanguage(String languageShort, HttpServletRequest request, ActionContext ctx) {
  		request.getSession(false).setAttribute(GUILANGUAGE_SESSION_ATT, languageShort);
  		if (ctx != null) { 
  			ctx.setLocale( new Locale(languageShort));   			
  		}
  		
  		_latestSessionLanguage=languageShort;
  	}
  	
  	protected String getSessionLanguage(HttpServletRequest request) {
  		if (request.getSession(false)==null || request.getSession(false).getAttribute(GUILANGUAGE_SESSION_ATT)==null) {
  			return Globals.Get().getGuiLanguagesMgr().getGuiLanguage(UserProfileData.DEFAULT_LANG_ID).getShortname();
  		}
  		return request.getSession(false).getAttribute(GUILANGUAGE_SESSION_ATT).toString();
  		
  	}
  	
  	public String getCurrentLanguage() {
  		
  		if (_userProfileData!=null && _userProfileData.isLoggedIn()) {
  			return _userProfileData.getGuiLanguageShortname();
  		}
  		
  		return _latestSessionLanguage.toUpperCase();
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
	// might be needed for proper URL
	public String getPaymentLogin() {
		return Globals.GetMxProperty("mx.payment.login");
	}
	public String getWebAppBaseUrl() {
		return Globals.Get().getWebAppBaseUrl();
	}
	public ICatalog getCurrentCatalog() {	
		return _userProfileData.getCurrentCatalog();
	}
	public void setCurrentCatalog(ICatalog c) {
		_userProfileData.setCurrentCatalog(c.getId());
	}	
	
	/* Get Definition of plans available and matching current user category */
	public Collection<IPlan> getPlansList() {
		Collection<IPlan> result = new ArrayList<IPlan>();
		for (IPlan p : Globals.Get().getPlansMgr().getPlans()) {
			if (p.getId().equals(this.getCurrentUserProfile().getPlanId())
					|| p.getCategory().equals(CATEGORY.ALL)
					|| p.getCategory().equals(this.getCurrentUserProfile().getCategory())) {
				result.add(p);
			}
		}
		return result;
	}
	
	public Integer getCurrentDocumentId() {		
		return _userProfileData.getCurrentDocumentId();
	}	
	
	public String getMxVersion() { return Globals.GetMxProperty("mx.version"); }
	public String getMxFooterInfo() {return Globals.GetMxProperty("mx.footer.info"); } 
	
	public String getMxRole() {	return _userProfileData.getRole().toString(); }
	public Boolean getMxDevMode() {	return Globals.Get().isDevMode(); }
	public String getMxRunMode() {	return Globals.GetMxProperty("mx.runmode"); }
	public String getMxStandaloneLogin() {	return Globals.GetMxProperty("mx.standalone.login"); }
	public String getMxStandalonePassword() { return Globals.GetMxProperty("mx.standalone.password"); }
	
	public String getMxAppStatus() { return Globals.Get().getApplicationStatus().toString(); }
	public String getMxDriveHostPath() { return Globals.GetMxProperty("mx.drive.standalone.hostPath"); }
	
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
