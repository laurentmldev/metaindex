package metaindex.data.catalog;

import java.io.File;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.ftplet.FtpException;

import metaindex.data.filter.IFilter;
import metaindex.data.commons.globals.Globals;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.PeriodicProcessMonitor;
import toolbox.utils.FileSystemUtils;

public class Catalog implements ICatalog {

	public static final Integer AUTOREFRESH_PERIOD_SEC=5;

	public static final Long DEFAULT_QUOTA_NBDOCS = 200L;
	public static final Integer DEFAULT_QUOTA_DISCSPACEBYTES = 0;
	private Log log = LogFactory.getLog(Catalog.class);
	
	private PeriodicProcessMonitor _dbAutoRefreshProcessing=new PeriodicProcessMonitor(this);
	private Semaphore _catalogLock = new Semaphore(1,true);
	public void acquireLock() throws InterruptedException { _catalogLock.acquire(); }
	public void releaseLock() { _catalogLock.release(); }
	
	private Boolean _dbIndexFound=false;
	
	private Integer _autoRefreshPeriodSec=AUTOREFRESH_PERIOD_SEC;
	
	// from SQL DB
	private Integer _id=0;
	private String _shortname="";
	private Integer _creatorId=0;
	private String _thumbnailUrl="";	
	private List<String> _itemNameFieldsList=new ArrayList<>();
	private String _itemThumbnailUrlField="";
	private String _urlPrefix="";
	private String _perspectiveMatchField="";
	private Date _lastUpdate=new Date(0);
	
	// Quota data
	private Long _quotaNbDocs=DEFAULT_QUOTA_NBDOCS;
	private Integer _quotaFtpDiskSpaceBytes=DEFAULT_QUOTA_DISCSPACEBYTES;
	
	// from ElasticSearch DB
	private Long _nbDocuments=0L;
	
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
	
	private CatalogFtpServer _ftpServer=null;
	
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
				+"\n\t- creator_id: "+this.getCreatorId()
				+"\n\t- quotaNbDocs: "+this.getQuotaNbDocs()
				+"\n\t- quotaFtpDiscSpaceBytes: "+this.getQuotaFtpDiscSpaceBytes()+" Bytes"
				+"\n\t- Nb Logged users:\t"+this.getNbLoggedUsers();

	}
	@Override 
	public void startServices() throws DataProcessException {
		_ftpServer=new CatalogFtpServer(this);
		try { _ftpServer.start(); }
		catch (FtpException e) { 
			throw new DataProcessException
				("Unable to start FTP server for catalog "+this.getName()+" : "+e.getMessage(),e); 
		}
		_dbAutoRefreshProcessing.start();
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
	public Integer getCreatorId() { return _creatorId; }
	@Override
	public void setCreatorId(Integer creatorId) { _creatorId=creatorId; }
	
	@Override 
	public Integer getFtpPort(IUserProfileData user) {
		if (_ftpServer==null) { return -1; }
		return _ftpServer.getPort();
	}
	@Override
	public String getLocalFsFilesPath() {
		return Globals.Get().getWebappsFsPath()+"/data/"+this.getName();
	}
	@Override
	public String getFilesBaseUrl() {
		return Globals.Get().getAppBaseUrl()+"/data/"+this.getName()+"/";
	}
	
	@Override
	public Integer getNbLoggedUsers() { return _loggedUsersIds.size(); }
	
	@Override
	public void enter(IUserProfileData p) throws DataProcessException {
		try {
			_loggedUsersLock.acquire();		
			Globals.Get().getWebappsFsPath();
			Globals.Get().getAppBaseUrl();
			if (_ftpServer!=null) { _ftpServer.setUser(p, true); }
			if (!_loggedUsersIds.containsKey(p.getId()) ) {
				_loggedUsersIds.put(p.getId(),p);
				log.info("Catalog "+this.getName()+" added user '"+p.getName()+"' sessionId="+p.getHttpSessionId()
																			+". Total: "+_loggedUsersIds.size()+" users.");
			}
			_loggedUsersLock.release();
		} catch (Exception e) { _loggedUsersLock.release(); e.printStackTrace(); }
		
	}

	@Override
	public void quit(IUserProfileData p) throws DataProcessException {
		try {
			_loggedUsersLock.acquire();
			if (_loggedUsersIds.containsKey(p.getId())) {
				_loggedUsersIds.remove(p.getId());
				// we don't want FTP connections to stop if user wants
				// to go to another catalog while uploading files,
				// so we keep its connection active
				// _ftpServer.setUser(p, false);
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
			if (_ftpServer!=null) { _ftpServer.setUser(u, false); }
			u.sendGuiWarningMessage("You have been exited from catalog '"+this.getName()
										+"' after an action of '"+activeUser.getNickname()+"'");
		}
	}
	
	@Override
	public List<IFilter> getFilters() {
		return _filters;
	}
	
	@Override
	public IFilter getFilter(Integer filterId) throws DataProcessException {
		return _filters.stream()
					.filter(c -> c.getId().equals(filterId))
					.findFirst()
					.orElse(null);
	}
	@Override
	public IFilter getFilter(String filterName) throws DataProcessException {
		return _filters.stream()
					.filter(c -> c.getName().equals(filterName))
					.findFirst()
					.orElse(null);
	}

	@Override
	public void addFilter(IFilter cat) throws DataProcessException {
		try {
			_filtersLock.acquire();
			if (!_filters.contains(cat)) { _filters.add(cat); }
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
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getTermsRelationsDefinitions() {
		ICatalogTerm mxRelationsTerm = this.getTerms().get(ICatalogTerm.MX_FIELD_RELATIONS);
		if (mxRelationsTerm==null) { return new HashMap<String,String>(); }
		
		// 'relations' comes from ElasticSearch field contents 
		if (!mxRelationsTerm.getMappingProperties().containsKey("relations")) { return new HashMap<String,String>(); }
		
		return (Map<String,String>)mxRelationsTerm.getMappingProperties().get("relations");
	}
	
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
	public void doPeriodicProcess() throws DataProcessException {
		Date prevCurDate = this.getLastUpdate();
		Boolean onlyIfDbcontentsUpdated=true;
		List<ICatalog> list = new ArrayList<>();
		list.add(this);		
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(list,onlyIfDbcontentsUpdated).execute();
		
		// detect if contents actually changed
		if (this.getLastUpdate().after(prevCurDate)) { log.info(this.getDetailsStr()); }
		
	}
	@Override 
	public Boolean shallBeProcessed(Date testedUpdateDate) { 
		return this.getLastUpdate().before(testedUpdateDate); 
	}
	
	@Override
	public Integer getPeriodicProcessPeriodSec() { return _autoRefreshPeriodSec; }
	
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
					
					// if raw field and term have same name, use same 'term' data-structure
					if (existingTermObj.getName().equals(term.getName())) {
						existingTermObj.setId(term.getId());
						existingTermObj.setDatatype(term.getDatatype());
						existingTermObj.setEnumsList(term.getEnumsList());
						existingTermObj.setIsMultiEnum(term.getIsMultiEnum());
					// if not, add the new term in the list
					// This case only occurs for 'RELATION' terms, for which the raw field is unique and shared
					// among all applicative terms because of ElasticSearch constraint (only 1 single 'join' field per index)
					} else {
						term.setCatalogId(this.getId());
						this.getTerms().put(term.getName(), term);
					}
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
		return _quotaNbDocs;
	}
	@Override
	public void setQuotaNbDocs(Long maxNbDocs) {
		_quotaNbDocs=maxNbDocs;
	}
	@Override
	public Integer getQuotaFtpDiscSpaceBytes() {
		return _quotaFtpDiskSpaceBytes;
	}
	@Override
	public void setQuotaFtpDiscSpaceBytes(Integer maxFtpSpaceBytes) {
		_quotaFtpDiskSpaceBytes=maxFtpSpaceBytes;
	}
	
	@Override
	public Boolean checkQuotasNbDocsOk() {
		return this.getNbDocuments()<this.getQuotaNbDocs();		
	}
	@Override
	public Long getDiscSpaceUseBytes() {
		try {
			// set (and create if needed) local-system folder storing ftp files
	        File directory = new File(this.getLocalFsFilesPath());
	        if (! directory.exists()){ directory.mkdir(); }
	        
			Long usedDiskSpace = FileSystemUtils.getTotalSizeBytes(this.getLocalFsFilesPath());
			return usedDiskSpace;
		} catch (IOException e) {
			log.error("Unable to retrieve used disc usage for catalog '"+this.getName()+"' at "+this.getLocalFsFilesPath()+" : "+e.getMessage());
			//e.printStackTrace();
			return -1L;
		}
		
	}
	@Override
	public Boolean checkQuotasDisckSpaceOk() {
		return getDiscSpaceUseBytes()<this.getQuotaFtpDiscSpaceBytes();
	}
	@Override
	public Date getLastUpdate() {
		return _lastUpdate;
	}
	@Override
	public void setLastUpdate(Date modifDate) {
		_lastUpdate=modifDate;		
	}

	
}
