package metaindex.data.userprofile;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.configuration2.PropertiesConfiguration;

import metaindex.data.filter.IFilter;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.commons.globals.guitheme.GuiThemesManager;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import metaindex.data.commons.globals.plans.IPlan;
import metaindex.data.commons.globals.plans.IPlansManager;
import metaindex.app.Globals;
import metaindex.app.control.websockets.catalogs.messages.WsMsgCatalogDetails_answer;
import metaindex.app.control.websockets.catalogs.messages.WsMsgCatalogShortDetails_answer;
import metaindex.app.control.websockets.users.WsControllerUser;
import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.app.control.websockets.users.messages.WsUserGuiMessageText.MESSAGE_CRITICITY;
import metaindex.app.periodic.db.UserProfilePeriodicDbReloader;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogChatMsg;
import toolbox.exceptions.DataAccessException;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.PeriodicProcessMonitor;
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
	
	public final static Integer DEFAULT_LANG_ID=1;
	public final static Integer DEFAULT_GUITHEME_ID=1;
	

	private String _remoteAddr="";			
	private HttpSession _httpSession = null;
	private String _wsSessionId = "";
	private Integer _userId = 0;
	private String _useremail = "";
	private String _cryptedPassword = "";	
	private String _nickname = "";
	private boolean _isLoggedIn = false;
	private USER_ROLE _role=USER_ROLE.ROLE_OBSERVER;
	private CATEGORY _category=CATEGORY.STUDENT_SEARCHER;
	
	private Integer _guiLanguageId = DEFAULT_LANG_ID;
	private Integer _guiThemeId = DEFAULT_GUITHEME_ID;
	
	private Integer _curNbCatalogsCreated = 0;
	private ICatalog _selectedCatalog=null;
	private Integer _currentFilterId=IFilter.ALL_ITEMS_CATALOG_ID;

	private Date _itemsLastChangeDate=new Date(0);
	private Map<Integer,Integer> _currentDocumentIdByFilterId = new java.util.concurrent.ConcurrentHashMap<>();
	private Map<Integer,USER_CATALOG_ACCESSRIGHTS> _catalogsAccessRights = new java.util.concurrent.ConcurrentHashMap<>();
	
	private Semaphore _userProfileLock = new Semaphore(1,true);
	public void acquireLock() throws InterruptedException { _userProfileLock.acquire(); }
	public void releaseLock() { _userProfileLock.release(); }
	
	// Catalogs Customization
	private Map<Integer,String> _catalogsKibanaIFrameHtml= new java.util.concurrent.ConcurrentHashMap<>();
	
	// processing tasks by id
	private Map<Integer,IProcessingTask> _runningProcessingTasks = new ConcurrentHashMap<Integer,IProcessingTask>();

	// used to detect inactive users 
	// not implemented yet in framework, need to check manually in DB
	private Date _lastLoginDate=new Date(0);
	
	// for auto-refresh processing
	private Date _lastUpdate=new Date(0);
	
	private Integer _autoRefreshPeriodSec=Catalog.AUTOREFRESH_PERIOD_SEC;
	
	private PeriodicProcessMonitor _dbAutoRefreshProcessing=new UserProfilePeriodicDbReloader(this);
	
	private Boolean _enabled=false;
	
	// plan info
	private Integer _planId;// the real reference info is plan id
	private IPlan _cachedCurPlan; // just cache the object corresponding to planId
	private Date _planStartDate;
	private Date _planEndDate;
	private Integer _planNbQuotaWarnings=0;
	
	 

	public UserProfileData() {
		// auto refresh shall be active even if user is not logged in
		// because this user might own catalogs used by others
		// so changes in DB (typically plan or enabled-flag) shall 
		// be detected even if the user itself is not logged-in
		_dbAutoRefreshProcessing.start();
		
		// will load corresponding translations map
		this.setGuiLanguageId(DEFAULT_LANG_ID);
	}
	public Integer getPlanId() {
		return _planId; 
	}
	public void setPlanId(Integer planId) { _planId=planId; }

	/**
	 * if assigned plan is out of date, return default plan id as fallback
	 */
	public IPlan getPlan() {
		Integer activePlanId=getPlanId();
		Date now=new Date();		
		if (now.after(getPlanEndDate())) { 
			activePlanId=Globals.Get().getPlansMgr().getDefaultPlan(this.getCategory()).getId(); 
		}
		
		// update cached current plan object if needed
		if (_cachedCurPlan==null || !_cachedCurPlan.getId().equals(activePlanId)) {
			_cachedCurPlan = Globals.Get().getPlansMgr().getPlan(activePlanId);
		}
				
		return _cachedCurPlan;
	}
	private String formatDate(Date date) {
		DateFormat userDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (this.getGuiLanguage().getShortname().equals("FR")) { userDateFormat = new SimpleDateFormat("dd/MM/yyyy"); }
		return userDateFormat.format(date); 
	}
	@Override
	public Date getPlanStartDate() { return _planStartDate; }
	@Override
	public String getPlanStartDateStr() { return formatDate(_planStartDate); }
	@Override
	public void setPlanStartDate(Date planStartDate) { _planStartDate=planStartDate; }
	@Override
	public Date getPlanEndDate() { return _planEndDate; }
	@Override
	public String getPlanEndDateStr() { return formatDate(_planEndDate); }
	@Override
	public void setPlanEndDate(Date planEndDate) { _planEndDate=planEndDate; }
	
	@Override
	public Integer getId() {
		return _userId;
	}
	
	@Override
	public void setId(Integer id) { _userId=id; }
	
	@Override
	public void logIn() throws DataProcessException {
		if (this.getHttpSessionId().length()==0) {
			throw new DataProcessException("SessionId not set for user. Unable to login.");
		}
		try {
			this.acquireLock();
			if (this._isLoggedIn==false) {
				this._isLoggedIn=true;
				this.setLastLoginDate(new Date());
		    	Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getUpdateUserLastLoginDateIntoDbStmt(this).execute();
				Boolean result =Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().createOrUpdateCatalogStatisticsUser(this);
		    	if (!result) {
		    		log.error("unable to update user details in statistics environment.");
	    		}
		    	log.info("User '"+this.getName()+"' logged-in");
		    	log.info(this.getDetailsStr());
			}
	    	this.releaseLock();
		} catch(InterruptedException e) {
			this.releaseLock();
			throw new DataProcessException("Unable to perform user login : "+e.getMessage(),e);
		}
		
		if (!Globals.Get().getActiveUsersList().contains(this)) { Globals.Get().getActiveUsersList().add(this); }
 		
	}
	
	@Override
	public void logOut() throws DataProcessException {
		try {
			this.acquireLock();
			// exit from current catalog if any
			if (this.getCurrentCatalog()!=null) {
				this.getCurrentCatalog().quit(this);
			}
			this._isLoggedIn=false;
			//log.info("User '"+this.getName()+"' logged-out");
			this.releaseLock();
		} catch(InterruptedException e) {
			this.releaseLock();
			throw new DataProcessException("Unable to perform user logout : "+e.getMessage(),e);
		}
		
		if (Globals.Get().getActiveUsersList().contains(this)) { Globals.Get().getActiveUsersList().remove(this); }
	}
	
	@Override
	public String getStatisticsUrl() {
		String url=Globals.GetMxProperty("mx.kibana.protocol")+"://"
								+Globals.GetMxProperty("mx.kibana.host");
										
		if (Globals.GetMxProperty("mx.kibana.port").length()>0) {
			url+=":"+Globals.GetMxProperty("mx.kibana.port");
		}
		
		url+="/metaindex/kibana/s/"+this.getCurrentCatalog().getName();
		return url; 
	}
	@Override
	public String getStatisticsDiscoverUrl() { 
		String url = getStatisticsUrl()+Globals.GetMxProperty("mx.kibana.urlparams");
		
		return url;
	}
	
	@Override
	public boolean isLoggedIn() { return _isLoggedIn && _httpSession!=null; }
	
	@Override
	public HttpSession getHttpSession() { return _httpSession; };
	
	@Override
	public String getHttpSessionId() { 
		if (_httpSession==null) { return ""; }
		return _httpSession.getId();
	}
	@Override
	public void setHttpSession(HttpSession session) {
		if (_httpSession!=null && _httpSession!=session) {
			try { _httpSession.invalidate(); }
			catch(IllegalStateException e) {
				// session already invalidated
			}
		}
		_httpSession = session; 
	}
	
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
	
	@Override 
	public Integer getCurNbCatalogsCreated() {
		return _curNbCatalogsCreated;
	}
	public void setCurNbCatalogsCreated(Integer nbCats) {
		_curNbCatalogsCreated=nbCats;
	}
	
    /** Retrieve (encrypted) password. 
     *  Only used for unit test 
     */
	@Override
    public String getPassword() {
		return _cryptedPassword; 
	}
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
		try { 
			_curLanguageTranslations = Globals.Get().getProperties("global_"+langShortName.toLowerCase());
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
		
		try {
			this.acquireLock();
			if (catalogId==null) { _selectedCatalog=null; }
			else { _selectedCatalog=Globals.Get().getCatalogsMgr().getCatalog(catalogId); }		
			// select default filter when entering a catalog
			this.setCurrentFilter(IFilter.ALL_ITEMS_CATALOG_ID);
		} catch (InterruptedException e) {
			_selectedCatalog=null;
			log.error("For "+this.getName()+" while setting current catalog Id to '"+catalogId+"' : "+e.getMessage(),e);
			e.printStackTrace();
		}
		this.releaseLock();

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
		// keep tracks of currently running processing tasks for admin monitoring
		Globals.Get().getActiveProcessingTasks().add(processingTask);
		_runningProcessingTasks.put(processingTask.getId(), processingTask);			
	}	
	@Override
	public void removeProccessingTask(Integer processingTaskId) {
		// keep tracks of currently running processing tasks for admin monitoring
		Globals.Get().getActiveProcessingTasks().remove(_runningProcessingTasks.get(processingTaskId));
		_runningProcessingTasks.remove(processingTaskId);		
	}
	@Override
	public void notifyChange(IProcessingTask processingTask) {
		if (processingTask.isTerminated()) {
			removeProccessingTask(processingTask.getId());								
		}
	}

	@Override 
	public String getText(String textid, String... params) {
		if (_curLanguageTranslations==null) {
			log.error("trying to get text from undefined language properties.");
			return textid;
		}
		String propertyStr = _curLanguageTranslations.getString(textid);
		if (propertyStr==null) { return textid; }
		
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
		if (!this.isEnabled()) { return USER_CATALOG_ACCESSRIGHTS.NONE; }
		
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
		if (role==null) { _catalogsAccessRights.remove(catalogId); }
		else { _catalogsAccessRights.put(catalogId, role); }		
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
	
	@Override
	public void sendEmail(String subject, String body) throws DataProcessException {
		Globals.Get().sendEmail(this.getName(), subject, body);
	}
	
	@Override
	public void sendEmailCCiAdmin(String subject, String body) throws DataProcessException {
		Globals.Get().sendEmail(this.getName(),/*Cc*/"",/*Cci*/Globals.GetMxProperty("mx.mailer.admin_recipient"),subject, body);
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
	public void sendGuiChatMessage(ICatalog c, ICatalogChatMsg msg) {
		try {
			WsControllerUser.UsersWsController.sendUserGuiChatMessage(this,c,msg);
		} catch (Exception e) {
			log.error("Unable to send chat msg to user '"+this.getName()+"' : "+e.getMessage());
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
	
	@Override
	public void notifyCatalogContentsChanged(CATALOG_MODIF_TYPE modifType, Long nbImpactedItems) {
		try {
			WsControllerUser.UsersWsController.sendBroadCastCatalogContentsChanged(this,modifType,nbImpactedItems);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void notifyCatalogContentsChanged(CATALOG_MODIF_TYPE modifType, String impactedItemName, String impactDetails) {
		try {
			WsControllerUser.UsersWsController.sendBroadCastCatalogContentsChanged(this,modifType,impactedItemName,impactDetails);
		} catch (Exception e) {
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

	@Override
	public Date getLastUpdate() {
		return _lastUpdate;
	}
	@Override 
	public void setLastUpdate(Date newDate) { 
		_lastUpdate=newDate; 
	}
	@Override 
	public void setLastUpdateIfNewer(Date newDate) { 
		if (newDate.getTime()>_lastUpdate.getTime()) {
			_lastUpdate=newDate; 
		}
	}

	@Override
	public Boolean shallBeProcessed(Date testedUpdateDate) {
		return this.getLastUpdate().before(testedUpdateDate);
	}

	// ----- helpers about user profile
	public void loadFullUserData() throws DataProcessException {
		
		// load user data from DB
		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
				.getPopulateUserProfileFromDbStmt(this)
				.execute();
		
		// load nb catalogs created
		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
			.getCountUserCatalogsInDbStmt(this)
			.execute();
			
		// load user roles data from DB
		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
				.getPopulateAccessRightsFromDbStmt(this)
				.execute();
		
		// load user custos
		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
				.getPopulateCatalogCustomizationFromDbStmt(this)
				.execute();
		
		// update Kibana/ELK rights
		Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface()
				.createOrUpdateCatalogStatisticsUser(this);
    	
		// refresh and propagate data where needed
		doPeriodicProcess();
	}

	
	@Override
	public void doPeriodicProcess() throws DataProcessException {
		
		// avoid periodic process on dummy or empty profile objects
		if (getName().length()==0) { return; }
		
		Long prevChangeTime = this.getLastUpdate().getTime();
		Boolean onlyIfDbcontentsUpdated=true;
		List<IUserProfileData> list = new ArrayList<>();
		list.add(this);		
		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getPopulateUserProfileFromDbStmt(list,onlyIfDbcontentsUpdated).execute();
		Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getPopulateAccessRightsFromDbStmt(list,onlyIfDbcontentsUpdated).execute();
		
		// detect if contents actually changed
		if (this.getLastUpdate().getTime() > prevChangeTime) { 
			// catalogs access rights might have changed
			// so we update roles in Kibana env. and Drive server
			
			//log.error("### updates detected in DB on "+this+" : "+this.getLastUpdate().getTime()+" > "+prevChangeTime);
			Boolean result =Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().createOrUpdateCatalogStatisticsUser(this);
	    	if (!result) {
	    		log.error("unable to update user details in statistics environment.");
    		}
	    		
	    	log.info(this.getDetailsStr());
	    			
		}
		
		// check also for HttpSession timeout
		// not very clear to do it here together with DB-changes monitoring, but convenient 
		checkHttpSessionTimeout();
 			 
	}
	private static final Integer SESSION_EXPIRED_NOTIFY_TRESHOLD_SEC=15;
	private void checkHttpSessionTimeout() {
		if (getHttpSession()!=null) {
			try {
				long lastActivationDate = getHttpSession().getLastAccessedTime();
				long currTime = System.currentTimeMillis();
				long expiryDelayMs = lastActivationDate + this.getHttpSession().getMaxInactiveInterval()*1000 - currTime;
				
				if (expiryDelayMs<SESSION_EXPIRED_NOTIFY_TRESHOLD_SEC*1000) {
					try {
						WsControllerUser.UsersWsController.sendSessionStatusExpired(this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			// session invalid
			} catch (IllegalStateException e) {
				// nothing special to do there, session is not valid anyway
			}
		}
	}

	@Override
	public Integer getPeriodicProcessPeriodSec() {
		return _autoRefreshPeriodSec;
	}
	
	@Override
	public Boolean isEnabled() { return _enabled; }
	
	@Override
	public void setEnabled(Boolean enabled) 
	{ 
		_enabled=enabled;
		if (_enabled==false && this.isLoggedIn()) {
			try { this.logOut(); }
			catch(DataProcessException e) {
				log.error("Error while log-out on setEnabled(false) for "+this.getName()+" : "+e.getMessage());
			}
		}
	}
	
	@Override
	public String getDetailsStr() {
		String str = "'"+this.getName()+"' :"
				+"\n\t- id: "+this.getId()
				+"\n\t- nickname: "+this.getNickname()
				+"\n\t- enabled: "+this.isEnabled()
				+"\n\t- lastUpdate: "+this.getLastUpdate().getTime()
				+"\n\t- role: "+this.getRole().toString()
				+"\n\t- language: "+this.getGuiLanguageShortname()
				+"\n\t- theme: "+this.getGuiThemeShortname()
				+"\n\t- plan: "+Globals.Get().getPlansMgr().getPlan(getPlanId()).getName()+" (id:"+getPlanId()+")"
						+" from "+this.getPlanStartDate()+" until "+this.getPlanEndDate()
				// if defined plan is outdated, fallback into a basic default plan
				+(getPlan().getId().equals(getPlanId()) ? "" :
					"\n\t\t\t Plan OUTDATED. Active plan:"+this.getPlan().getName()+" (id:"+this.getPlan().getId()+")")
				+"\n\t\t- quotaCatalogsCreated: "+this.getPlan().getQuotaCatalogsCreated()
				+"\n\t\t- quotaNbDocsPerCatalog: "+this.getPlan().getQuotaNbDocsPerCatalog()
				+"\n\t\t- quotaDrivePerCatalog: "+this.getPlan().getQuotaDriveMBytesPerCatalog()+"MB"
				+"\n\t- planNbQuotasWarnings: "+this.getPlanNbQuotaWarnings()
				+"\n\t- catalogs rights: ";
		if (this.getRole() == USER_ROLE.ROLE_ADMIN) {
			str+=USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN.toString()+" for all";
		} else {								
			Integer nbAccessibleCatalogs=0;
			for (ICatalog c : Globals.Get().getCatalogsMgr().getCatalogsList()) {
				if (this.getUserCatalogAccessRights(c.getId())!=USER_CATALOG_ACCESSRIGHTS.NONE) {
					nbAccessibleCatalogs++;
					str +="\n\t\t \""+c.getName()+"\" : "+this.getUserCatalogAccessRights(c.getId()).toString();				
				}
			}
			
			if (nbAccessibleCatalogs==0) { str+=USER_CATALOG_ACCESSRIGHTS.NONE.toString()+" for all"; }
		}
		return str;
	}
	@Override
	public String getRemoteAddress() {
		return _remoteAddr;
	}
	@Override
	public void setRemoteAddress(String addr) { _remoteAddr=addr; }
	@Override
	public Integer getPlanNbQuotaWarnings() {
		return _planNbQuotaWarnings;
	}
	@Override
	public void setPlanNbQuotaWarnings(Integer nbWarnings) {
		_planNbQuotaWarnings=nbWarnings;		
	}
	@Override
	public List<ICatalog> getOwnedCatalogs() {
		return Globals.Get().getCatalogsMgr().getOwnedCatalogsList(getId());
	}
	@Override
	public List<ICatalog> getAccessibleCatalogs() {
		List<ICatalog> catalogs = Globals.Get().getCatalogsMgr().getCatalogsList();
		List<ICatalog> result = new ArrayList<>();
		Iterator<ICatalog> it = catalogs.iterator();
		while (it.hasNext()) {
    		ICatalog curCatalog = it.next();
    		if (this.getUserCatalogAccessRights(curCatalog.getId())!=USER_CATALOG_ACCESSRIGHTS.NONE)
			{ 
    			result.add(curCatalog);
			}
		}
		return result; 
	}
	@Override
	public CATEGORY getCategory() {
		return _category;
	}
	@Override
	public void setCategory(CATEGORY c) {
		_category=c;
	}
	@Override
	public String getLocalFsFilesPath() {
		return Globals.Get().getUserdataFsPathUsers()+"/"+getId();
	}
	@Override
	public Date getLastLoginDate() {
		return _lastLoginDate;
	}
	@Override
	public void setLastLoginDate(Date date) {
		_lastLoginDate=date;		
	}
	
}
