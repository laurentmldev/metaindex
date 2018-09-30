package metaindex.data.userprofile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.dbaccess.accessors.AGuiLanguagesAccessor;
import metaindex.dbaccess.accessors.AGuiThemesAccessor;
import metaindex.dbaccess.accessors.AUserProfileAccessor;

/**
 * Java object container for users DB table.
 * Retrieve also String info of corresponding foreign keys (guilanguage and guitheme).
 * @author Laurent ML
 */
public class UserProfileData extends AGenericMetaindexData<IUserProfileData> implements IUserProfileData
{
	private Log log = LogFactory.getLog(UserProfileData.class);
	
	private ActionSupport currentAction=null;
	
	private boolean issync=false;
	public final static int DEFAULT_LANG_ID=1;
	public final static int DEFAULT_GUITHEME_ID=1;
	
	private String sessionId = "";
	private Integer userId = 0;
	private String username = "";
	private String email = "";	
	private boolean isLoggedIn = false;
	
	/** only used for unit test */
	private String password="";
	
	private GuiLanguageData guilanguageobj = null;
	private GuiThemeData guithemeobj = null;
	
	private ICommunityHandle selectedCommunity=null;
	
	public void logIn(IUserProfileData activeUser, String username) throws DataAccessConstraintException {
		if (this.getSessionId()=="") {
			throw new DataAccessConstraintException("SessionId not set for user. Unable to log-in him.");
		}
		this.setUsername(username);
		this.update(activeUser);
		this.isLoggedIn=true;
	}
	
	// used essentially for websockets usage
	public void logIn() throws DataAccessConstraintException {
		if (this.getSessionId()=="") {
			throw new DataAccessConstraintException("SessionId not set for user. Unable to log-in him.");
		}
		this.isLoggedIn=true;
	}

	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		checkCompliantWithDBSmallString("field 'username' of userprofile '"+this.getUsername()+"'",this.getUsername());
		checkCompliantWithDBSmallString("field 'email' of userprofile '"+this.getUsername()+"'",this.getEmail());
	}
	
	@Override
	public void setCurrentActionSupport(ActionSupport a) { this.currentAction=a; }
	
	@Override
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	@Override
	public String getSessionId() { return sessionId; };
	
	@Override
	public void setSessionId(String sId) 
	{ 
		sessionId = sId;		
	}
	
	@Override
	public boolean isEnabled() {
		return isLoggedIn();
	}
	@Override
	public String getUsername() { return username; }
	@Override
	public void setUsername(String username) { this.username = username; }
	@Override
	public String getEmail() { return email; }
	@Override
    public void setEmail(String email) { this.email = email; }  

    /** Retrieve (encrypted) password. 
     *  Only used for unit test 
     */
	@Override
    public String getPassword() { return password; }
    /** Encrypt and store password */
	@Override
    public void setPasswordAndEncrypt(String password) 
    { 
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    	this.password = bCryptPasswordEncoder.encode(password); 
	}  
	@Override
    public String getGuiLanguage() { return guilanguageobj.getName(); }
	@Override
    public String getGuiLanguageShort() { return guilanguageobj.getShortName(); }
	@Override
    public int getGuiLanguageId() { return guilanguageobj.getId(); }
	@Override
    public void setGuiLanguageId(int guilanguageid) { 
		List<GuiLanguageData> languages = getGuiLanguages();
		Iterator<GuiLanguageData> it = languages.iterator();
		while (it.hasNext()) {
			GuiLanguageData cur = it.next();
			if (cur.getId()== guilanguageid) {
				guilanguageobj = cur;
			}
		}
		if (languages.size()>0 && guilanguageobj==null)  { 
			guilanguageobj = languages.get(0);
			log.warn("Language id '"+guilanguageid+"' unknown, language '"+guilanguageobj.getName()+"' set by default.");
		} 
	}      
	@Override
    public String getGuiTheme() { return guithemeobj.getName(); }
	@Override
    public String getGuiThemeShort() { return guithemeobj.getShortName(); }
	@Override
    public int getGuiThemeId() { return guithemeobj.getId(); }
	@Override
    public void setGuiThemeId(int guithemeid) {
		List<GuiThemeData> themes = getGuiThemes();
		Iterator<GuiThemeData> it = themes.iterator();
		while (it.hasNext()) {
			GuiThemeData cur = it.next();
			if (cur.getId()== guithemeid) {
				guithemeobj = cur;
			}
		}
		if (themes.size()>0 && guithemeobj==null)  { 
			guithemeobj = themes.get(0);
			log.warn("Language id '"+guithemeid+"' unknown, language '"+guithemeobj.getName()+"' set by default.");
		}		
	}      
	@Override
    public List<GuiLanguageData> getGuiLanguages() {
		this.getGuiLanguagesDBAccessor();
		return this.getGuiLanguagesDBAccessor().getGuiLanguagesList(); 
	}
	@Override
	public List<GuiThemeData> getGuiThemes() { return this.getGuiThemesDBAccessor().getGuiThemesList(); }
	
	public UserProfileData() {
		super(ADataAccessFactory.getDataAccessImplFactory(
				ADataAccessFactory.DATA_ACCESS_IMPL_DB_METAINDEX));	
		this.setGuiLanguageId(DEFAULT_LANG_ID);
		this.setGuiThemeId(DEFAULT_GUITHEME_ID);		
	}
  	
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException  {	
		this.getUserProfileDBAccessor().createIntoDB(activeUser, this);
	}
	
	@Override
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		try {		
			this.getUserProfileDBAccessor().refreshFromDB(activeUser,this);
		}
		catch(DataAccessErrorException e)
		{
			log.error("DB error while retrieving Profile data for user '"+this.getUsername()+"'");
			throw e;
		} catch (Exception e) {
			log.error("DB error while decoding Profile data for user '"+this.getUsername()+"'");
			throw new DataAccessErrorException("DB error while decoding Profile data for user '"
															+this.getUsername()+"' : "+e.getMessage());
		}
		issync=true;
	}
	
	/**
	 * Update data of this instance into DB.
	 */
	@Override
	public void commit(IUserProfileData activeUser) 
				throws DataAccessErrorException,DataAccessConstraintException {
		try { this.getUserProfileDBAccessor().storeIntoDB(activeUser,this); } 
			catch (DataAccessErrorException e) 
			{ 
				log.error(e.getCause().getMessage());
				throw e;
			}	
			issync=false;
	}
	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getUserProfileDBAccessor().deleteFromDB(activeUser, this);
		issync=false;
	}
	@Override
	public ICommunityHandle getSelectedCommunity() {
		return selectedCommunity;
	}
	@Override
	public void setSelectedCommunity(ICommunityHandle communityData) {
		
		selectedCommunity=communityData;	
		
		if (selectedCommunity.getCatalogs().size()>0 && selectedCommunity.getSelectedCatalog()==null) {
			try {
				this.selectedCommunity.setSelectedCatalog(selectedCommunity.getCatalogs().get(0));				
			} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
				log.error("Unable to select default catalog/element for newly selected community "+this.getSelectedCommunity().getIdName());
				e.printStackTrace();
			}
		}
	}
	@Override
	public ICatalogHandle getSelectedCatalog() {
		if (getSelectedCommunity()!=null) {
			return getSelectedCommunity().getSelectedCatalog();
		} else { return null; }
	}
	@Override
	public IElementHandle getSelectedElement() {		
		if (getSelectedCommunity()==null || getSelectedCommunity().getSelectedCatalog()==null) { return null; }
		return getSelectedCommunity().getSelectedCatalog().getSelectedElement();
	}
	
	@Override
	public void invalidate() {
		issync=false;		
	}
	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.commit(activeUser);		
	}
	@Override
	public void updateFull(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.update(activeUser);		
	}

	@Override
	public boolean isSynchronized() {
		return issync;
	}

	@Override
	public IUserProfileData clone() throws CloneNotSupportedException {
		log.error("Clone not implemented for UserProfileData");
		return null;
	}
	
	@Override
	public void quitSelectedCommunity() {
		if (this.getSelectedCommunity()!=null) {
			this.getSelectedCommunity().quit();
		}
	}
	

	/**
	 * Retrieve all the communities the given user belongs to.
	 * @param activeUsername
	 * @return
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	@Override
	public List<ICommunityHandle> getUserCommunities(IUserProfileData profile) 
				throws DataAccessErrorException, DataAccessConstraintException {
		
			return this.getCommunityDBAccessor().loadUserAssociatedData(this,profile);
	}

	/**
	 * TODO make it static
	 * @return
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	@Override
	public List<ICommunityHandle> getAllCommunities() 
				throws DataAccessErrorException, DataAccessConstraintException {
		
		List<ICommunityHandle> result= new ArrayList<ICommunityHandle>();
		List<ICommunity> communities= getCommunityDBAccessor().loadAllDataFromDB(this);
		Iterator<ICommunity> it = communities.iterator();
		while(it.hasNext()) {
			ICommunityHandle newc = new CommunityHandle(this, it.next());
			if (newc.isReadableByUser()) {
				result.add(newc);
			}		
		}
		return result;
	}	
	
	@Override
	public void populateFromJson(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException {
		throw new UnableToPopulateException("method not implemented");
		
	}

	@Override
	public String getText(String textId) {
		if (this.currentAction==null) {
			log.warn("User '"+this.getUsername()+"' has no current ActionSupport set.");
			return textId; 
		}
		else { return this.currentAction.getText(textId); }
		
	}

	@Override
	public Integer getUserId() {
		return this.userId;
	}
	
	@Override
	public void setUserId(Integer id) {
		this.userId=id;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		// nothing		
	}
}
