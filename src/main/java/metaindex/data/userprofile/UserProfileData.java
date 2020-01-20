package metaindex.data.userprofile;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.configuration2.PropertiesConfiguration;

import metaindex.data.filter.IFilter;
import metaindex.data.commons.globals.Globals;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.commons.globals.guitheme.GuiThemesManager;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.websockets.users.WsControllerUser;
import metaindex.websockets.users.WsControllerUser.COMMUNITY_MODIF_TYPE;
import metaindex.websockets.users.WsUserGuiMessageText.MESSAGE_CRITICITY;
import toolbox.exceptions.DataAccessException;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IProcessingTask;


/**
 * Java object container for users DB table.
 * Retrieve also String info of corresponding foreign keys (guilanguage and guitheme).
 * @author Laurent ML
 */
public class UserProfileData implements IUserProfileData
{
	private Log log = LogFactory.getLog(UserProfileData.class);
	
	private PropertiesConfiguration _curLanguageTranslations=null;
	
	public final static int DEFAULT_LANG_ID=1;
	public final static int DEFAULT_GUITHEME_ID=1;
	
	private String _httpSessionId = "";
	private String _wsSessionId = "";
	private Integer _userId = 0;
	private String _useremail = "";
	private String _cryptedPassword = "";
	private String _nickname = "";
	private boolean _isLoggedIn = false;
	private USER_ROLE _role=USER_ROLE.ROLE_OBSERVER;
	
	private Integer _guiLanguageId = DEFAULT_LANG_ID;
	private Integer _guiThemeId = DEFAULT_GUITHEME_ID;
	
	private ICatalog _selectedCatalog=null;
	private Integer _currentFilterId=IFilter.ALL_ITEMS_CATALOG_ID;

	private Date _itemsLastChangeDate=new Date(0);
	private Map<Integer,Integer> _currentDocumentIdByFilterId = new java.util.concurrent.ConcurrentHashMap<>();
	private Map<Integer,USER_CATALOG_ACCESSRIGHTS> _catalogsAccessRights = new java.util.concurrent.ConcurrentHashMap<>();
	
	// Catalogs Custo
	private Map<Integer,String> _catalogsKibanaIFrameHtml= new java.util.concurrent.ConcurrentHashMap<>();
	
	// processing tasks by id
	private Map<Integer,IProcessingTask> _runningProcessingTasks = new ConcurrentHashMap<Integer,IProcessingTask>();

	@Override
	public Integer getId() {
		return _userId;
	}
	
	@Override
	public void setId(Integer id) { _userId=id; }
	
	@Override
	public void logIn(IUserProfileData activeUser, String useremail) throws DataProcessException {
		if (this.getHttpSessionId().length()==0) {
			throw new DataProcessException("SessionId not set for user. Unable to log-in him.");
		}
		this.setName(useremail);
		this._isLoggedIn=true;
	}
	
	@Override
	/// used essentially for websockets usage
	public void logIn() throws DataProcessException {
		if (this.getHttpSessionId().length()==0) {
			throw new DataProcessException("SessionId not set for user. Unable to log-in him.");
		}
		this._isLoggedIn=true;
	}
	
	@Override
	public String getStatisticsUrl() { 
		return Globals.GetMxProperty("mx.kibana.protocol")+"://"
				+Globals.GetMxProperty("mx.kibana.host")+":"+Globals.GetMxProperty("mx.kibana.port")
				+"/app/kibana"; 
	}
	@Override
	public String getStatisticsDiscoverUrl() { 
		return getStatisticsUrl()+"#/discover"
				+Globals.GetMxProperty("mx.kibana.urlparams"); 
	}
	
	@Override
	public boolean isLoggedIn() { return _isLoggedIn; }
	
	@Override
	public String getHttpSessionId() { return _httpSessionId; };
	
	@Override
	public void setHttpSessionId(String sId) { _httpSessionId = sId; }
	
	@Override
	public String getWebsocketSessionId() { return _wsSessionId; };
	
	@Override
	public void setWebsocketSessionId(String sId) { _wsSessionId = sId; }
	
	@Override
	public String getName() { return _useremail; }
	@Override
	public void setName(String username) { this._useremail = username; }
	@Override
	public String getNickname() { return _nickname; }
	public void setNickname(String nickname) { this._nickname = nickname; }
	
	
    /** Retrieve (encrypted) password. 
     *  Only used for unit test 
     */
	@Override
    public String getPassword() { return _cryptedPassword; }
    /** Encrypt and store password */
	@Override
    public void setPasswordAndEncrypt(String clearPassword) 
    { 
    	BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    	this._cryptedPassword = bCryptPasswordEncoder.encode(clearPassword); 
	}  
	public void setEncryptedPassword(String password) {
		this._cryptedPassword = password;
	}
	
	//------ Gui Language
	@Override
	public Integer getGuiLanguageId() { return _guiLanguageId; }
	
	@Override 
	public String getGuiLanguageShortname() {
		return Globals.Get().getGuiLanguagesMgr().getGuiLanguage(getGuiLanguageId()).getShortname();
	}
	
	@Override
	public void setGuiLanguageId(Integer guiLanguageId) { 
		_guiLanguageId=guiLanguageId;
		String langShortName = getGuiLanguage().getShortname();
		try { _curLanguageTranslations = Globals.Get().getProperties("global_"+langShortName.toLowerCase());
		} catch (DataAccessException cex)
		{
		    log.error("Unable to load translations for language key '"+langShortName+"', using English.");
		    try { _curLanguageTranslations = Globals.Get().getProperties("global_en"); }
		    catch (DataAccessException e) { e.printStackTrace(); }
		}
	}      
	@Override
	public IGuiLanguage getGuiLanguage() {
		return Globals.Get().getGuiLanguagesMgr().getGuiLanguage(getGuiLanguageId());
	}
	@Override
	public Collection<IGuiLanguage> getGuiLanguages() { return Globals.Get().getGuiLanguagesMgr().getGuiLanguages(); }
	
	//------ Gui Theme	
	@Override
	public Integer getGuiThemeId() { return _guiThemeId; }
	@Override 
	public String getGuiThemeShortname() {
		try {
			String themeShortname = Globals.Get().getGuiThemesMgr().getGuiTheme(getGuiThemeId()).getShortname();
			if (themeShortname==null || themeShortname.length()==0) { return GuiThemesManager.DEFAULT_GUITHEME_SHORTNAME; }
			else { return themeShortname; }
		} catch (Exception e) { return GuiThemesManager.DEFAULT_GUITHEME_SHORTNAME; }
	}
	
	@Override
	public void setGuiThemeId(Integer guiThemeId) { _guiThemeId=guiThemeId; }      
	@Override
	public IGuiTheme getGuiTheme() {
		return Globals.Get().getGuiThemesMgr().getGuiTheme(getGuiThemeId());
	}
	@Override
	public Collection<IGuiTheme> getGuiThemes() { return Globals.Get().getGuiThemesMgr().getGuiThemes(); }
	
	@Override
	public ICatalog getCurrentCatalog() {
		return _selectedCatalog;
	}
	@Override
	public void setCurrentCatalog(Integer catalogId) {
		
		_selectedCatalog=Globals.Get().getCatalogsMgr().getCatalog(catalogId);
		
		// select default filter when entering a catalog
		this.setCurrentFilter(IFilter.ALL_ITEMS_CATALOG_ID);					
	}
	
	@Override
	public CatalogVocabularySet getCatalogVocabulary() {
		if (getCurrentCatalog()==null) { return new CatalogVocabularySet(); }
		return getCurrentCatalog().getVocabulary(this.getGuiLanguageId());
	}
	@Override
	public IFilter getCurrentFilter() {
		if (getCurrentCatalog()!=null) {
			try {
				return getCurrentCatalog().getFilter(_currentFilterId);
			} catch (DataProcessException e) {
				e.printStackTrace();
				return null;
			}
		} else { return null; }
	}
	
	@Override
	public void setCurrentFilter(Integer filterId) {
		_currentFilterId=filterId;
	}
	
	@Override
	public Integer getCurrentDocumentId() {		
		return _currentDocumentIdByFilterId.get(_currentFilterId);
	}
	
	@Override
	public void setCurrentDocumentId(Integer docId) {
		_currentDocumentIdByFilterId.put(_currentFilterId, docId);
	}
	
	@Override
	public void quitCurrentCatalog() throws DataProcessException {
		if (this.getCurrentCatalog()!=null) {
			this.getCurrentCatalog().quit(this);
		}
	}
	
	@Override
	public IProcessingTask getProcessingTask(Integer processingTaskId) {
		return _runningProcessingTasks.get(processingTaskId);
	}
	@Override
	public void addProcessingTask(IProcessingTask processingTask) {
		_runningProcessingTasks.put(processingTask.getId(), processingTask);		
	}	
	@Override
	public void removeProccessingTask(Integer processingTaskId) {
		_runningProcessingTasks.remove(processingTaskId);
	}
	@Override
	public void notifyChange(IProcessingTask processingTask) {
		//WSControllersAccessor.getWSController().sendProgressingTaskProgress(processingTask);
		if (processingTask.isTerminated()) {
			_runningProcessingTasks.remove(processingTask.getId());								
		}
	}

	@Override 
	public String getText(String textid, String... params) {
		if (_curLanguageTranslations==null) {
			log.error("trying to get Ttext from undefined language properties.");
			return textid;
		}
		String propertyStr = _curLanguageTranslations.getString(textid);
		if (propertyStr==null) { return textid+" "+params; }
		// replacement of inner params if any
		for (Integer i=0;i<params.length;i++) {
			String curParamStr=params[i];
			if (curParamStr==null) { curParamStr="?"; }
			propertyStr=propertyStr.replaceFirst("\\{"+i+"\\}", curParamStr);			
		}
		return propertyStr;
	}
	@Override
	public List<Integer> getUserCatalogsIds() {
		List<Integer> catalogIds = new ArrayList<Integer>();
		Iterator<Integer> it = _catalogsAccessRights.keySet().iterator();
		while (it.hasNext()) {
			catalogIds.add(it.next());
		}
		return catalogIds;
	}

	@Override
	public void clearUserCatalogsIds() {
		_catalogsAccessRights.clear();
		
	}

	@Override
	public USER_ROLE getRole() { return _role; }
	@Override
	public void setRole(USER_ROLE role) { _role=role; }
	@Override
	public USER_CATALOG_ACCESSRIGHTS getUserCatalogAccessRights(Integer catalogId)
	{
		// User role overrides user catalog access rights
		if (this.getRole()==USER_ROLE.ROLE_ADMIN) { return USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN; }
		
		if (this.getRole()==USER_ROLE.ROLE_USER) {
			if (_catalogsAccessRights.containsKey(catalogId)) { 
				return _catalogsAccessRights.get(catalogId); 
			}
			else { return USER_CATALOG_ACCESSRIGHTS.NONE; }
		}
		
		if (this.getRole()==USER_ROLE.ROLE_OBSERVER) {
			if (_catalogsAccessRights.containsKey(catalogId)) { 
				return USER_CATALOG_ACCESSRIGHTS.CATALOG_READ; 
			}
			else { return USER_CATALOG_ACCESSRIGHTS.NONE; }
		}
		
		else { return USER_CATALOG_ACCESSRIGHTS.NONE; }
	}
	@Override
	public void setUserCatalogAccessRights(Integer catalogId, USER_CATALOG_ACCESSRIGHTS role) {
		_catalogsAccessRights.put(catalogId, role);		
	}
	
	@Override
	public void setUserCatalogKibanaIFrameHtml(Integer catalogId,String iFrameKibanaString) { 
		_catalogsKibanaIFrameHtml.put(catalogId, iFrameKibanaString); 
	}
		
	@Override
	public String getUserCatalogKibanaIFrameHtml(Integer catalogId) { 
		return _catalogsKibanaIFrameHtml.get(catalogId); 
	}
	
	@Override
	public String getCurrentCatalogKibanaIFrameHtml() { 
		if (this.getCurrentCatalog()==null) { return ""; }
		if (getUserCatalogKibanaIFrameHtml(this.getCurrentCatalog().getId())==null) { return ""; }
		return getUserCatalogKibanaIFrameHtml(this.getCurrentCatalog().getId());
	}
	private void sendGuiMessage(MESSAGE_CRITICITY level, String msg, List<String> details) {
		try {
			WsControllerUser.UsersWsController.sendUserGuiMessageText(this,level,msg,details);
		} catch (Exception e) {
			log.error("Unable to send user '"+this.getName()+"' "+level.toString()+"message '"+msg+"'" + e.getMessage());
			e.printStackTrace();
		}
		
	}
	@Override
	public void sendGuiErrorMessage(String msg) { sendGuiMessage(MESSAGE_CRITICITY.ERROR,msg, new ArrayList<String>()); }
	@Override
	public void sendGuiErrorMessage(String msg,List<String> details) {sendGuiMessage(MESSAGE_CRITICITY.ERROR,msg,details); } 
	@Override
	public void sendGuiInfoMessage(String msg) { sendGuiMessage(MESSAGE_CRITICITY.INFO,msg, new ArrayList<String>()); }
	@Override
	public void sendGuiWarningMessage(String msg) { sendGuiMessage(MESSAGE_CRITICITY.WARNING,msg, new ArrayList<String>()); }
	@Override
	public void sendGuiSuccessMessage(String msg) { sendGuiMessage(MESSAGE_CRITICITY.SUCCESS,msg, new ArrayList<String>()); }
	
	public void sendGuiProgressMessage(Integer procId, String msg, Float pourcentage,Boolean active) {
		try {
			WsControllerUser.UsersWsController.sendUserGuiMessageProgress(this,procId,msg,pourcentage,active);
		} catch (Exception e) {
			//log.error("Unable to send user '"+this.getName()+"' "+level.toString()+"message '"+msg+"'" + e.getMessage());
			e.printStackTrace();
		}
	}
	public void sendGuiProgressMessage(Integer procId, String msg, Float pourcentage) {
		sendGuiProgressMessage(procId,msg,pourcentage,true /*processing is active*/);		
	}
	
	public void notifyCatalogContentsChanged(COMMUNITY_MODIF_TYPE modifType, Integer nbImpactedItems) {
		try {
			WsControllerUser.UsersWsController.sendBroadCastCatalogContentsChanged(this,modifType,nbImpactedItems);
		} catch (Exception e) {
			//log.error("Unable to send user '"+this.getName()+"' "+level.toString()+"message '"+msg+"'" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public Date getItemsLastChangeDate() {
		return _itemsLastChangeDate;
	}
	@Override
	public void setItemsLastChangeDate(Date itemsLastChangeDate) {
		this._itemsLastChangeDate = itemsLastChangeDate;
	}

	
}
