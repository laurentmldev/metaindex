package metaindex.data.beans;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.transaction.PlatformTransactionManager;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import metaindex.data.IBufferizedData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.CommunityVocabularySet;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.UsersAccessor;
import metaindex.data.metadata.beans.BeanElementUpdateMetadataProcess;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;

//import metaindex.dataAccess.DataAccessFactory;

public abstract class AMetaindexBean extends ActionSupport  {
	
	private static final long serialVersionUID = 1084021485104376111L;
	private Log log = LogFactory.getLog(AMetaindexBean.class);
	
	public static final String ADMIN_USER_NAME="root";
	
	protected static final String USERPROFILE_SESSION_ATT="user_profile";
	protected static final String SELECTED_COMMUNITY_SESSION_ATT="selected_community";
	protected static final String SELECTED_ELEMENT_SESSION_ATT="selected_element";
	protected static final String SELECTED_CATALOG_SESSION_ATT="selected_catalog";
	protected static final String GUITHEME_SESSION_ATT="selected_guitheme";
	
	private static final String VALIDATION_SESSION_ID="VALID_SESSION_ID_135246";
		
	private IUserProfileData userProfileData=null;

	public boolean isUserLogged() { return this.getLoggedUserProfile().getUsername().length()>0; }
    
  	public void prepare() throws Exception {
  		
  		HttpServletRequest request=ServletActionContext.getRequest(); 
  		
  		// it test user has been set, then we use it
  		if (userProfileData==null || userProfileData.getSessionId()!=VALIDATION_SESSION_ID) {
  			
  			userProfileData=null;
  	  		
  			// ATTENTION operation not thread safe
  			String sessionId=request.getSession().getId();
			
			if (UsersAccessor.userExists(sessionId)) { userProfileData = UsersAccessor.getUserProfileData(sessionId); }		
			else 
			{ 
				userProfileData = new UserProfileData();
				userProfileData.setSessionId(sessionId);
				UsersAccessor.addUser(userProfileData);
			}
  		} 
  		else { log.error("Using found test user to perform operation "+this.getClass().getName()); }
  		
		// for proper language traduction
		userProfileData.setCurrentActionSupport(this);
		
		// the logged user ask to refresh data of its own account from DB
		if (getLoggedUserProfile().isLoggedIn()) {
			getLoggedUserProfile().update(getLoggedUserProfile());
			
			setSessionLanguage(getLoggedUserProfile().getGuiLanguageShort(), ActionContext.getContext());
			setSessionGuiTheme(getLoggedUserProfile().getGuiThemeShort(),request);
			
			if (this.getLoggedUserProfile().getSelectedCommunity()!=null) {
				request.getSession().setAttribute(SELECTED_COMMUNITY_SESSION_ATT, this.getLoggedUserProfile().getSelectedCommunity());
			}
		}
  	}
  	
  	protected void setSessionGuiTheme(String themeShort, HttpServletRequest request) {
  		request.getSession().setAttribute(GUITHEME_SESSION_ATT, themeShort);
  	}
  	
  	protected void setSessionLanguage(String languageShort, ActionContext ctx) {
  		if (ctx != null) { ctx.setLocale( new Locale(getLoggedUserProfile().getGuiLanguageShort())); }
  	}
  	
  	/// for validation tests only
  	public void setValidationActiveUser(String testUserName) {
  		
  		try { userProfileData = UsersAccessor.getUserProfileData(VALIDATION_SESSION_ID); } 
		catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) 
		{
	  		userProfileData = new UserProfileData();
			userProfileData.setSessionId(VALIDATION_SESSION_ID);
			UsersAccessor.addUser(userProfileData);
			IUserProfileData adminUser = new UserProfileData();
			adminUser.setUsername(ADMIN_USER_NAME);				
			userProfileData.logIn(adminUser, testUserName);				
		} 
  	}
  	
	static public enum BeanProcessResult { 	BeanProcess_SUCCESS, 
											BeanProcess_ERROR,
											BeanProcess_DBERROR,
											BeanProcess_ConstraintERROR
											};									
	


	public IUserProfileData getLoggedUserProfile() {
		if (!userProfileData.isLoggedIn()
				&& ServletActionContext.getRequest()!=null 
				&& ServletActionContext.getRequest().getUserPrincipal()!=null) {
			
			// detect if log-in operation have been just performed
			// ie if user principal is defined.
			try {	
				String username = ServletActionContext.getRequest().getUserPrincipal().getName();
				
				IUserProfileData adminUser = new UserProfileData();
				adminUser.setUsername(ADMIN_USER_NAME);
				userProfileData.logIn(adminUser, username);
				
			} catch (DataAccessErrorException | DataAccessConstraintException e) {
				e.printStackTrace();
				return null;
			}
		}
		return userProfileData;
	}
	
	public ICommunityHandle getSelectedCommunity() {	
		return userProfileData.getSelectedCommunity();
	}
	public void setSelectedCommunity(ICommunityHandle c) {
		userProfileData.setSelectedCommunity(c);
	}	
	public ICatalogHandle getSelectedCatalog() {
		return userProfileData.getSelectedCommunity().getSelectedCatalog();
	}
	
	public IElementHandle getSelectedElement() {
		return userProfileData.getSelectedCommunity().getSelectedCatalog().getSelectedElement();
	}	
	
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
