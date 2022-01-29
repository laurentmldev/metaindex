package metaindex.data.catalog;



/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.filter.Filter;
import metaindex.data.filter.IFilter;
import metaindex.app.Globals;
import metaindex.app.control.catalogdrive.SftpCatalogsDrive;
import metaindex.app.control.catalogdrive.ICatalogsDrive;
import metaindex.app.periodic.db.CatalogPeriodicDbReloader;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.PeriodicProcessMonitor;
import toolbox.utils.StreamHandler;
import toolbox.utils.filetools.FileSystemUtils;

public class Catalog implements ICatalog {


	public static final Integer AUTOREFRESH_PERIOD_SEC=5;
	public static final Long DEFAULT_QUOTA_NBDOCS = 200L;
	public static final Long DEFAULT_QUOTA_DRIVE_BYTES = 0L;
	public static final Long CHAT_MESSAGES_KEEPALIVE_MS = 2592000000L;//30 days	
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private List<ICatalogChatMsg> _chatMessages = new CopyOnWriteArrayList<>();
	private Semaphore _chatLock = new Semaphore(1,true);
	
	private PeriodicProcessMonitor _dbAutoRefreshProcessing=null;
	private Semaphore _catalogLock = new Semaphore(1,true);
	public void acquireLock() throws InterruptedException {
		_catalogLock.acquire(); 
	}
	public void releaseLock() {
		_catalogLock.release(); 
	}
	
	// flag saying if initial config has been done
	// (avoid doig it each time and don't want to do it 
	// at start up for all catalogs because it is too long
	private Boolean _firstEnterConfig=true;
	
	private Integer _autoRefreshPeriodSec=AUTOREFRESH_PERIOD_SEC;
	private Date _lastUpdate=new Date(0);
	
	private Boolean _dbIndexFound=false;
	
	
	// from SQL DB
	private Integer _id=0;
	private String _shortname="";
	private Integer _ownerId=0;
	private IUserProfileData _curOwner=null;
	private String _thumbnailUrl="";	
	private List<String> _itemNameFieldsList=new ArrayList<>();
	private String _itemThumbnailUrlField="";
	private String _urlPrefix="";
	private String _perspectiveMatchField="";
	private Integer _timeFieldTermId = null;
	
	// from ElasticSearch DB
	private Long _nbDocuments=0L;
	private Long _indexDiskUsageMB=0L;
	
	private List<IFilter> _filters = new java.util.concurrent.CopyOnWriteArrayList<>();
	private Semaphore _filtersLock = new Semaphore(1,true);
	
	private Map<Integer,IUserProfileData> _loggedUsersIds = new java.util.concurrent.ConcurrentHashMap<>();
	private Semaphore _loggedUsersLock = new Semaphore(1,true);
	private Map<String,CatalogVocabularySet> _vocabularies = new java.util.concurrent.ConcurrentHashMap<>();
	private Semaphore _vocabulariesLock = new Semaphore(1,true);	
	
	private Map<String,ICatalogTerm> _terms = new java.util.concurrent.ConcurrentHashMap<>();
	private Semaphore _termsLock = new Semaphore(1,true);
	
	private Map<String,ICatalogPerspective> _perspectives = new java.util.concurrent.ConcurrentHashMap<>();
	private Semaphore _perspectivesLock = new Semaphore(1,true);
	
	private ICatalogsDrive _driveServer=null;
	
	
	
	public Catalog() {}
	public Catalog(ICatalogCustomParams ref) {
		this.setId(ref.getId());
		this.setName(ref.getName());
		this.setItemNameFields(ref.getItemNameFields());
		this.setItemThumbnailUrlField(ref.getItemsUrlPrefix());
		this.setItemsUrlPrefix(ref.getItemsUrlPrefix());
		this.setPerspectiveMatchField(ref.getPerspectiveMatchField());
	}
	
	@Override
	public String getDetailsStr() {
		return "'"+this.getName()+"' :"
				+"\n\t- id: "+this.getId()
				+"\n\t- creator_id: "+this.getOwnerId()
				+"\n\t- quotaNbDocs: "+this.getQuotaNbDocs()
				+"\n\t- quotaDriveMBytes: "+this.getQuotaDriveMBytes()+"MB"
				+"\n\t- Nb Logged users:\t"+this.getNbLoggedUsers();
		

	}
	/*
	@Override
	public ICatalogsDrive getDriveServer() { return _driveServer; };
	
	private void startDrive() throws DataProcessException {
		if (getDriveServer()==null) { _driveServer=new SftpCatalogsDrive(this); }
		try { 
			getDriveServer().start();
		}
		catch (Exception e) { 
			throw new DataProcessException
				("Unable to start Drive for catalog "+this.getName()+" : "+e.getMessage(),e); 
		}
	}
	private void stopDrive() throws DataProcessException {
		if (getDriveServer()==null) { return; }
		try { getDriveServer().stop(); }
		catch (Exception e) { 
			throw new DataProcessException
				("Unable to stop Drive for catalog "+this.getName()+" : "+e.getMessage(),e); 
		}
		_driveServer=null;
	}
	*/
	private void startServices() throws DataProcessException {
		        	
		// issue #136: /!\ Processing in startServices shall be thread safe, because called out of a semaphore
		// lock in 'enter()' method.
		
		File catalogdataFs = new File(getLocalFsFilesPath());
		if (!catalogdataFs.exists()) {
			if (!catalogdataFs.mkdirs()) {
				log.error("unable to create data folder for catalog : "+getLocalFsFilesPath());
			}
		}
		else {
			log.info("created drive folder : "+getLocalFsFilesPath());
		}
		
		_dbAutoRefreshProcessing=new CatalogPeriodicDbReloader(this);
		_dbAutoRefreshProcessing.start();		
		
		log.info("configuring Kibana Space for catalog "+this.getName()+"... ");
		if (!(Globals.Get().getDatabasesMgr()
				.getCatalogManagementDbInterface()
				.setSpaceTimezone(this,"Zulu")
				
				&& Globals.Get().getDatabasesMgr()
				.getCatalogManagementDbInterface()
				.setSpaceDayOfWeek(this, "Monday")
				
				&& Globals.Get().getDatabasesMgr()
				.getCatalogManagementDbInterface()
				.setKibanaNumberFormat(this, "en", "00.[000]")
				
				&& Globals.Get().getDatabasesMgr()
				.getCatalogManagementDbInterface()
				.setSpaceLandingUrl(this, "/app/discover")
				
				&& Globals.Get().getDatabasesMgr()
				.getCatalogManagementDbInterface()
				.setKibanaQueryLanguage(this, "Lucene")
							
				)) {
			throw new DataProcessException("Unable to configure Kibana advanced settings.");
		}
		
		log.info("done configuring Kibana for catalog "+this.getName());
		
	}
	@Override 
	public void stopServices() throws DataProcessException {
		    
		IUserProfileData tmpAdminUserData = new UserProfileData();
		tmpAdminUserData.setName("admin-service");
		tmpAdminUserData.setNickname("admin-service");
		kickOutAllUsers(tmpAdminUserData);		
		
		_dbAutoRefreshProcessing.stopMonitoring();
		_dbAutoRefreshProcessing=null;
		
	}
	
	@Override
	public Integer getId() { return _id; }
	@Override
	public void setId(Integer id) { _id=id; }
	
	@Override
	public String getName() { return _shortname; }
	@Override	
	public void setName(String shortname) { _shortname = shortname; }
	
	@Override	
	public Integer getOwnerId() { return _ownerId; }
	@Override
	public void setOwnerId(Integer creatorId) { _ownerId=creatorId; }
	
	public IUserProfileData getOwner() {
		if (_curOwner!=null && _curOwner.getId().equals(getOwnerId())) { return _curOwner; }
		_curOwner = Globals.Get().getUsersMgr().getUserById(getOwnerId());
		return _curOwner;
	}
	
	public List<IUserProfileData> getUsers() throws DataProcessException {
		
		// first retrieve ids of users having access rights defined for this catalog or being admin
		List<Integer> usersIds = new ArrayList<>();
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface()
			.getCatalogUsersIdsStmt(this).execute(new StreamHandler<Integer>(usersIds));
		
		List<IUserProfileData> result = new ArrayList<>();
				
		for (Integer userId : usersIds) {
			IUserProfileData u = Globals.Get().getUsersMgr().getUserById(userId);
			if (u==null) {
				log.error("Unable to retrieve details of user '"+userId+"' for catalog '"+this.getName()+"'");
				continue;
			}
			result.add(u);
		}
		return result;
	}
	@Override
	/**
	 * current policy consist in considering a catalog disabled if its
	 * owner is disabled.
	 */
	public Boolean isEnabled() {
		IUserProfileData u = getOwner();
		if (u!=null) { return u.isEnabled(); }
		log.warn("isEnabled: unable to get owner of catalog "+getName()+", returned disabled by default.");
		return false;
	}
	
	@Override
	public String getLocalFsFilesPath() {
		return Globals.Get().getUserdataFsPathCatalogs()+"/"+this.getName();
	}
	@Override
	public String getFilesBaseUrl() {
		return Globals.Get().getWebAppBaseUrl()+(Globals.LOCAL_USERDATA_PATH_SUFFIX+"/"+this.getName());
	}
	
	@Override
	public Integer getNbLoggedUsers() { return _loggedUsersIds.size(); }
	
	@Override
	public void enter(IUserProfileData p) throws DataProcessException {
		try {
			
			_loggedUsersLock.acquire();			
			
						
			if (!_loggedUsersIds.containsKey(p.getId()) ) {
				_loggedUsersIds.put(p.getId(),p);
				log.info("Catalog "+this.getName()+" added user '"
							+p.getId()+"' sessionId="+p.getHttpSessionId()
							+". Total: "+_loggedUsersIds.size()+" users.");
			}
			
			_loggedUsersLock.release();
			
			// /!\ Attention: issue #136: startServices takes some time to execute (because of Kibana space config)
			// and so shall be called OUT of the _loggedUsersLock
			// if not, if user click 'rapidly' on 2 catalogs, and 'enter catalog' rapidly on the second
			// it might actually enter into the first one.
			//
			// Processing in startServices are thread safe.
			//
			if (_firstEnterConfig==true) {
				_firstEnterConfig=false;
				startServices();				
			}			
			
			
		} catch (Exception e) { _loggedUsersLock.release(); e.printStackTrace(); }
		
	}

	@Override
	public void quit(IUserProfileData p) throws DataProcessException {
		try {
			_loggedUsersLock.acquire();
			if (_loggedUsersIds.containsKey(p.getId())) {
				_loggedUsersIds.remove(p.getId());
				// we don't want Drive connections to stop if user wants
				// to go to another catalog while uploading files,
				// so we keep its connection active
				// _driveServer.setUser(p, false);
			}
			_loggedUsersLock.release();	
		} catch (Exception e) {
			_loggedUsersLock.release();
			e.printStackTrace();
		}

		//log.error("### user "+p.getName()+" ("+p+") quits "+this.getName());
	}
	
	@Override 
	public void kickOutAllUsers(IUserProfileData activeUser) throws DataProcessException {
		for (Integer userId : _loggedUsersIds.keySet()) {
			IUserProfileData u = Globals.Get().getUsersMgr().getUserById(userId);
			u.setCurrentCatalog(0);
			this.quit(u);			
			u.sendGuiWarningMessage(
						u.getText("Catalogs.exitedWarning",this.getName(),activeUser.getNickname()));
		}
	}
	
	@Override
	public List<IFilter> getBuiltinFilters(IUserProfileData u) {
		List<IFilter> builtinFilters = new ArrayList<>();
		Filter myModifsFilter = new Filter();
		myModifsFilter.setId(CATALOG_BUILTIN_FILTER_ID__MY_MODIFS);
		myModifsFilter.setName(u.getText("Header.search.savequery.builtin.myModifs"));
		myModifsFilter.setQuery(ICatalogTerm.MX_TERM_LASTMODIF_USERID+":"+u.getId());
		myModifsFilter.setIsBuiltin(true);
		builtinFilters.add(myModifsFilter);
		
		Filter myLastMinuteModifsFilter = new Filter();
		myLastMinuteModifsFilter.setId(CATALOG_BUILTIN_FILTER_ID__MY_LASTMINUTE_MODIFS);
		myLastMinuteModifsFilter.setName(u.getText("Header.search.savequery.builtin.myRecentModifs"));
		myLastMinuteModifsFilter.setQuery(ICatalogTerm.MX_TERM_LASTMODIF_USERID+":"+u.getId()
								+" AND "+ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP+":[now-1m TO *]");
		myLastMinuteModifsFilter.setIsBuiltin(true);
		builtinFilters.add(myLastMinuteModifsFilter);
		
		return builtinFilters;
	}
	@Override
	public List<IFilter> getCatalogFilters() {
		return _filters;
	}
	@Override
	public List<IFilter> getFilters(IUserProfileData u) {
		List<IFilter> allFilters =  getBuiltinFilters(u);
		allFilters.addAll(getCatalogFilters());
		return allFilters;
	}
	@Override
	public IFilter getFilter(IUserProfileData u,Integer filterId) throws DataProcessException {
		return getFilters(u).stream()
					.filter(c -> c.getId().equals(filterId))
					.findFirst()
					.orElse(null);
	}
	@Override
	public IFilter getFilter(IUserProfileData u,String filterName) throws DataProcessException {
		return getFilters(u).stream()
					.filter(c -> c.getName().equals(filterName))
					.findFirst()
					.orElse(null);
	}

	@Override
	public void addFilter(IFilter filter) throws DataProcessException {
		try {
			_filtersLock.acquire();
			if (!_filters.contains(filter)) { _filters.add(filter); }
			_filtersLock.release();
		} catch (InterruptedException e) {
			_filtersLock.release();
			throw new DataProcessException(e.getMessage());
		}		
	}
	
	@Override
	public void clearFilters() throws DataProcessException {
		try {
			_filtersLock.acquire();
			_filters.clear();
			_filtersLock.release();
		} catch (InterruptedException e) {
			_filtersLock.release();
			throw new DataProcessException(e.getMessage());
		}
	}

	@Override
	public CatalogVocabularySet getVocabulary(Integer guiLanguageId) {
		
		IGuiLanguage lang = Globals.Get().getGuiLanguagesMgr().getGuiLanguage(guiLanguageId);
		if (lang==null) {
			log.error("unknown language id '"+guiLanguageId+"'.");
			return null;
		}

		return _vocabularies.get(lang.getShortname());
		
	}
	@Override
	public CatalogVocabularySet getVocabulary(String guiLanguageShortName) {
		return _vocabularies.get(guiLanguageShortName); 
	}
	@Override
	public Map<String,CatalogVocabularySet> getVocabularies() {
		return _vocabularies;
	}
	@Override
	public void setVocabulary(String guiLanguageId,CatalogVocabularySet voc) {
		_vocabularies.put(guiLanguageId,voc);
	}
	@Override
	public void setVocabulary(Integer guiLanguageId,CatalogVocabularySet voc) {
		setVocabulary(Globals.Get().getGuiLanguagesMgr().getGuiLanguage(guiLanguageId).getShortname(),voc);
	}
	@Override
	public void setThumbnailUrl(String thumbnailUrl) {
		_thumbnailUrl=thumbnailUrl;
		
	}
	@Override
	public String getThumbnailUrl() {
		return _thumbnailUrl;
	}
	
	@Override
	public Long getNbDocuments() {
		return _nbDocuments;
	}
	@Override
	public void setNbDocuments(Long _nbDocuments) {
		this._nbDocuments = _nbDocuments;
	}
	
	@Override
	public String getTimeFieldRawName() {
		if (getTimeFieldTermId()==null || getTimeFieldTermId().equals(0)) { return ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP; }
		
		for (ICatalogTerm t : this.getTerms().values()) {
			if (t.getId().equals(getTimeFieldTermId())) {
				return t.getRawFieldName();
			}
		}
		log.warn("Catalog '"+this.getName()+"' uses term id '"+getTimeFieldTermId()
					+"' as timefield, but no such term found on the catalog, using default.");
		
		return ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP; 
	}
	@Override
	public Integer getTimeFieldTermId() { return _timeFieldTermId; }	
	@Override
	public void setTimeFieldTermId(Integer tId) { _timeFieldTermId=tId; }	
	
	@Override
	/**
	 * This function is called at Catalog loading, importing fields mapping definition from ElasticSearch,
	 * storing in with corresponding default terms.
	 * Applicative type of terms is set just after when loading applicative data from SQL DB
	 * @see updateTermsApplicativeInfo
	 */
	public void updateTermsMapping(Map<String,RAW_DATATYPE> m,Map<String,Map<String,Object>> properties)
		throws DataProcessException {
		
		try {
			_termsLock.acquire();
			
			for (String termName : m.keySet()) {
				
				RAW_DATATYPE rawtype = m.get(termName);
				ICatalogTerm term = this.getTerms().get(termName);
				// if term already loaded, we ensure given raw datatype matches
				if (term!=null && term.getRawDatatype()!=rawtype) {
					throw new DataProcessException("Updated term '"+termName+"' raw datatype '"+rawtype
									+"' mismatch with currently loaded term : "+term.getRawDatatype()+"'");
				}
				// otherwise we create the term
				else {
					// check term name
					if (!ICatalogTerm.CheckTermNameSyntax(termName)) {
						log.warn("Catalog '"+this.getName()+"': term '"
									+termName+"' (from ElasticSearch) does not respect syntax restrictions.");
					}
					term = ICatalogTerm.BuildCatalogTerm(rawtype);
					term.setCatalogId(this.getId());
					term.setName(termName);
					this.getTerms().put(termName, term); 
					
				}
				term.setMappingProperties(properties.get(termName));
				
				_termsLock.release();
			}	
			
		} catch (InterruptedException e) { _termsLock.release();  throw new DataProcessException(e.getMessage()); }
		
	}
	
	@Override
	public List<String> getItemNameFields() {
		return _itemNameFieldsList;
	}
	@Override
	public void setItemNameFields(List<String> fieldnames) {
		_itemNameFieldsList = fieldnames;		
	}
	@Override
	public String getItemThumbnailUrlField() {
		return _itemThumbnailUrlField;
	}
	@Override
	public void setItemThumbnailUrlField(String fieldname) {
		_itemThumbnailUrlField=fieldname;		
	}
	@Override
	public String getItemsUrlPrefix() {
		if (_urlPrefix.length()==0) { return getFilesBaseUrl(); }
		return _urlPrefix;
	}
	@Override
	public void setItemsUrlPrefix(String urlPrefix) {
		_urlPrefix=urlPrefix;		
	}
	
	@Override
	public String getPerspectiveMatchField() {
		return _perspectiveMatchField;
	}
	
	@Override
	public void setPerspectiveMatchField(String fieldName) {
		_perspectiveMatchField=fieldName;
	}
	
	@Override
	public Map<String,ICatalogTerm> getTerms() { 
		return _terms; 
	}
	
	@Override
	public void clearTerms() throws DataProcessException {
		_terms.clear();
	}
	
	@Override
	public Map<String, ICatalogPerspective> getPerspectives() {
		return _perspectives;
	}
	
	@Override
	public void loadStatsFromDb() throws DataProcessException {
		List<ICatalog> list = new ArrayList<ICatalog>();
		list.add(this);
		Globals.Get().getDatabasesMgr().getCatalogContentsDbInterface().getLoadStatsFromDocsDbStmt(list).execute();
		
	}
	@Override
	public void loadMappingFromDb() throws DataProcessException {
		List<ICatalog> list = new ArrayList<ICatalog>();
		list.add(this);
		Globals.Get().getDatabasesMgr().getCatalogContentsDbInterface().getLoadMappingFromDocsDbStmt(list).execute();
	}
	@Override
	public void loadCustomParamsFromdb() throws DataProcessException {
		List<ICatalog> list = new ArrayList<ICatalog>();
		list.add(this);
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(list).execute();
	}
	@Override 
	public void loadPerspectivesFromdb() throws DataProcessException {
		List<ICatalog> list = new ArrayList<ICatalog>();
		list.add(this);
		Globals.Get().getDatabasesMgr().getPerspectivesDbInterface().getLoadFromDbStmt(this).execute();
	}
	@Override
	public void loadTermsFromDb() throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(this);
		Globals.Get().getDatabasesMgr().getTermsDbInterface().getPopulateTermsFromDbStmt(list).execute();
	}
	@Override 
	 /** 
	 * This function is called at Catalog loading, after importing fields mapping definition from ElasticSearch : 
	 * here terms loaded with default info are updated with applicative information from SQL DB
	 * Applicative type of terms is set just after when loading applicative data from SQL DB
	 * @see updateTermsMapping
	 */
	public void updateTermsApplicativeInfo(List<ICatalogTerm> termsFromDb) throws DataProcessException {
		
			try {
				_termsLock.acquire();
			
				for (ICatalogTerm term : termsFromDb) {
					ICatalogTerm existingTermObj = this.getTerms().get(term.getRawFieldName());
					
					if (existingTermObj==null) {
						log.warn("Raw field '"+term.getRawFieldName()+"' not defined, required by term '"
																+term.getName()+"' in catalog "+this.getName());
						continue;
					}
					
					// ensure raw datatype matchs
					if (ICatalogTerm.getRawDatatype(term.getDatatype())!=existingTermObj.getRawDatatype()) {
						throw new DataProcessException("Datatype mismatch between loaded term '"+term.getName()+"' and existing mapping : "
									+term.getDatatype()+" is not compatible with raw datatype "+existingTermObj.getRawDatatype());
					}
					
					// raw field and term have same name					
					existingTermObj.setId(term.getId());
					existingTermObj.setDatatype(term.getDatatype());
					existingTermObj.setEnumsList(term.getEnumsList());
					existingTermObj.setIsMultiEnum(term.getIsMultiEnum());
				}
				
				_termsLock.release();
				
			} catch (InterruptedException e) { _termsLock.release(); throw new DataProcessException(e.getMessage()); }
	}
	
	@Override
	public void updateCatalogPerspectives(List<ICatalogPerspective> perspectivesFromSqlDb)
			throws DataProcessException {
		
		try {
			_perspectivesLock.acquire();
			_perspectives.clear();
			for (ICatalogPerspective p : perspectivesFromSqlDb) {
				if (p!=null) {
					_perspectives.put(p.getName(), p);
				}
			}			
			_perspectivesLock.release();
			
		} catch (InterruptedException e) { _perspectivesLock.release(); throw new DataProcessException(e.getMessage()); }
		
	}
	
	@Override
	public void updateCatalogVocabularies(List<CatalogVocabularySet> vocabulariesFromSqlDb)
			throws DataProcessException {
		
		try {
			_vocabulariesLock.acquire();
			_vocabularies.clear();
			for (CatalogVocabularySet v : vocabulariesFromSqlDb) {
				if (v!=null) { _vocabularies.put(v.getGuiLanguageShortName(),v); }
			}			
			_vocabulariesLock.release();
			
		} catch (InterruptedException e) { _vocabulariesLock.release(); throw new DataProcessException(e.getMessage()); }
		
	}
	
	@Override
	public void loadVocabulariesFromDb() throws DataProcessException {
		
		try {
			_vocabulariesLock.acquire();
			this._vocabularies.clear();
			List<ICatalog> list = new ArrayList<>();
			list.add(this);
			Globals.Get().getDatabasesMgr().getCatalogVocDbInterface().getLoadFromDbStmt(this).execute();
			
			for (IGuiLanguage curLang : Globals.Get().getGuiLanguagesMgr().getGuiLanguages()) {
				if (_vocabularies.get(curLang.getShortname())==null) {
					CatalogVocabularySet defaultCatalogLexic = CatalogVocabularySet.getDefaultLanguage(curLang.getShortname());
					defaultCatalogLexic.setCatalogId(this.getId());
					_vocabularies.put(curLang.getShortname(), defaultCatalogLexic);
				}
			}
			_vocabulariesLock.release();
		} catch (InterruptedException e) { _vocabulariesLock.release(); throw new DataProcessException(e.getMessage()); }
		
	}
	
	
	@Override
	public void loadTermsVocabularyFromDb() throws DataProcessException {		
		try {
			_termsLock.acquire();
			if (this.getTerms().size()>0) {
				Globals.Get().getDatabasesMgr().getTermsDbInterface().getPopulateVocabularyFromDbStmt(this.getTerms().values()).execute();
			}
			_termsLock.release();
			
		} catch (InterruptedException e) { _termsLock.release(); throw new DataProcessException(e.getMessage()); }		
	}
	
	@Override
	public void updateTermVocabulary(List<TermVocabularySet> data) throws DataProcessException {		
		try {
			
			_termsLock.acquire();
			
			// update in database
			Globals.Get().getDatabasesMgr().getTermsDbInterface().getCreateOrUpdateVocabularyIntoDbStmt(data).execute();
			
			// update loaded terms contents
			for (TermVocabularySet voc : data) {
				ICatalogTerm term = this.getTerms().values().stream()
						.filter(t -> t.getId().equals(voc.getId())).findFirst().orElse(null);
						
				if (term==null) {
					throw new DataProcessException("Given term translations refers to a temr not found in community : "+voc.getName());
				}
				
				term.setVocabulary(Globals.Get().getGuiLanguagesMgr().getGuiLanguage(voc.getGuiLanguageId()).getShortname(), voc);				
			}			
			
			_termsLock.release();

		} catch (InterruptedException e) { _termsLock.release(); throw new DataProcessException(e.getMessage()); }		
	}
	
	@Override
	public Boolean isDbIndexFound() { return _dbIndexFound; }
	@Override
	public void setDbIndexFound(Boolean dbIndexFound) { _dbIndexFound=dbIndexFound; }
	
	@Override
	public Long getQuotaNbDocs() {
		IUserProfileData curOwner = getOwner();
		if (curOwner==null) { return DEFAULT_QUOTA_NBDOCS; }
		return curOwner.getPlan().getQuotaNbDocsPerCatalog();
	}
	@Override
	public Long getQuotaDriveMBytes() {
		IUserProfileData curOwner = getOwner();
		if (curOwner==null) { return DEFAULT_QUOTA_DRIVE_BYTES; }
		return curOwner.getPlan().getQuotaDriveMBytesPerCatalog();
	}
	
	@Override
	public Boolean checkQuotasNbDocsOk() {
		return this.getNbDocuments()<this.getQuotaNbDocs();		
	}

	@Override
	public Long getDriveUseMBytes() {
		return getCatalogFilesDiskUseMBytes() + getELKIndexDiskUseMBytes();
	}
	public Long getCatalogFilesDiskUseMBytes() {
		try {
			Long usedDiskSpace = FileSystemUtils.GetTotalSizeBytes(this.getLocalFsFilesPath()+"/");
			return usedDiskSpace/1000000;
		} catch (IOException e) {
			log.error("Unable to retrieve used drive usage for catalog '"+this.getName()+"' at "+this.getLocalFsFilesPath()+" : "+e.getMessage());
			//e.printStackTrace();
			return -1L;
		}
		
	}
	@Override
	public Long getELKIndexDiskUseMBytes() {
		return _indexDiskUsageMB;
	}
	@Override
	public void setELKIndexDiskUseMBytes(Long diskUsageMB) {
		_indexDiskUsageMB=diskUsageMB;
	}
	
	@Override
	public Boolean checkQuotasDriveOk() {
		return getDriveUseMBytes()<this.getQuotaDriveMBytes();
	}

	@Override
	public Date getLastUpdate() {
		return _lastUpdate;
	}
	
	@Override
	public void setLastUpdate(Date modifDate) {
		_lastUpdate=modifDate;		
	}
	
	@Override
	public void doPeriodicProcess() throws DataProcessException {
		Date prevCurDate = this.getLastUpdate();
		Boolean onlyIfDbcontentsUpdated=true;
		List<ICatalog> list = new ArrayList<>();
		list.add(this);		
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(list,onlyIfDbcontentsUpdated).execute();
		
		// detect if contents actually changed
		if (this.getLastUpdate().after(prevCurDate)) { log.info(this.getDetailsStr()); }
		
		// clean chat messages hwich are too old
		cleanChatOlderMessages();
	}
	@Override 
	public Boolean shallBeProcessed(Date testedUpdateDate) { 
		return this.getLastUpdate().before(testedUpdateDate); 
	}

	@Override
	public Integer getPeriodicProcessPeriodSec() { return _autoRefreshPeriodSec; }
	@Override
	public List<ICatalogChatMsg> getChatHistory() {
		return _chatMessages;
	}
	/* removing messages too old*/
	private void cleanChatOlderMessages() {
		try {
			_chatLock.acquire();
		List<ICatalogChatMsg> purgedMessages = new java.util.concurrent.CopyOnWriteArrayList<>();
		Long nowMs = new Date().getTime();
		
		for (ICatalogChatMsg msg : getChatHistory()) {
			if (nowMs-msg.getTimestamp().getTime()<=CHAT_MESSAGES_KEEPALIVE_MS) {
				purgedMessages.add(msg);
			}
		}		
		_chatMessages=purgedMessages;
		_chatLock.release();
		
		} catch (InterruptedException e) {
			e.printStackTrace();
			_chatLock.release();
			_chatMessages.clear();
		}
		
	}
	@Override
	public void postMessage(IUserProfileData postingUser,ICatalogChatMsg msg) {
				
		try {			
			_chatLock.acquire();
			List<IUserProfileData> users = this.getUsers();
			for (IUserProfileData user : users) {
				if (user.getId().equals(postingUser.getId())) { continue; }
				user.sendGuiChatMessage(this, msg);
			}
			// the sender is the last one to have the msg sent to him, so that he knows 
			// more or less that everybody got the message
			postingUser.sendGuiChatMessage(this, msg);
			_chatMessages.add(msg);
			_chatLock.release();
		} catch (DataProcessException | InterruptedException e) {
			log.error("unable to post chat message");
			e.printStackTrace();
			_chatLock.release();
		}
		
	}


	
}
