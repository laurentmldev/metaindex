package metaindex.data.catalog;

import java.util.Date;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;
import java.util.Map;

import metaindex.app.control.catalogdrive.SftpCatalogsDrive;
import metaindex.app.control.catalogdrive.ICatalogsDrive;
import metaindex.data.filter.IFilter;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPeriodicProcess;
import toolbox.utils.IIdentifiable;
import toolbox.utils.ILockable;

/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalog extends IIdentifiable<Integer>,ILockable,ICatalogCustomParams,IPeriodicProcess,ICatalogChat {

	/** get a string displaying Catalog current infos (for logs) */
	public String getDetailsStr();
	
	public void setId(Integer id);
	public void setName(String shortname);
	
	/** some constraints might lead to disable a catalog,
	 *  for example if quotas have not been respected for a while */
	public Boolean isEnabled();
	
	public Date getLastUpdate();
	public void setLastUpdate(Date timetampDate);
		
	/// users
	// get UserId for whom created it
	public Integer getOwnerId();
	public void setOwnerId(Integer ownerId);
	/** return all users having an 'access right' entry for this catalog, even if this right is NONE */
	public List<IUserProfileData> getUsers() throws DataProcessException;
	
	/// flag saying whether DB instance could be done for this catalog
	public Boolean isDbIndexFound(); 
	public void setDbIndexFound(Boolean dbIndexFound);		
	
	// Terms
	public Map<String,ICatalogTerm> getTerms();
	public void clearTerms() throws DataProcessException;	
	/**
	 * Update terms list of the catalog so that each ElasticSearch Mapping has a corresponding term:
	 * 	- when a Term def is found in SQL DB and an ElasticSearch mapping matchs its name, then use it
	 *  - when not, then add a default Term for each ES mapping
	 * @param termsFromSqlDb
	 * @throws ESDataProcessException
	 */
	public void updateTermsApplicativeInfo(List<ICatalogTerm> termsFromSqlDb) throws DataProcessException;
	public void loadTermsFromDb() throws DataProcessException;
	
	public void stopServices() throws DataProcessException;
	
	// Misc
	public String getTimeFieldRawName();
	
	// Quotas Management
	public Long getQuotaNbDocs();
	public Long getQuotaDriveBytes();		
	public Boolean checkQuotasNbDocsOk();
	public Boolean checkQuotasDriveOk();
	public Long getDriveUseBytes();
	
	// Files access
	public String getLocalFsFilesPath();
	public String getFilesBaseUrl();
	
	// Perspectives
	public Map<String,ICatalogPerspective> getPerspectives();	
	public void updateCatalogPerspectives(List<ICatalogPerspective> perspectivesFromSqlDb) throws DataProcessException;
	
	
	// Vocabulary
	public void updateCatalogVocabularies(List<CatalogVocabularySet> vocabulariesFromSqlDb) throws DataProcessException ;
	
	// User
	public Integer getNbLoggedUsers();
	public void enter(IUserProfileData p) throws DataProcessException ;
	public void quit(IUserProfileData p) throws DataProcessException;
	/// invoked when catalog is deleted
	public void kickOutAllUsers(IUserProfileData activeUser) throws DataProcessException;
	
	// Filters
	public List<IFilter> getFilters();
	public IFilter getFilter(Integer filterId) throws DataProcessException;	
	public IFilter getFilter(String filterName) throws DataProcessException;	
	public void addFilter(IFilter c) throws DataProcessException,DataProcessException;
	public void clearFilters() throws DataProcessException;
	
	public CatalogVocabularySet getVocabulary(Integer guiLanguageId);
	public CatalogVocabularySet getVocabulary(String guiLanguageShortName);
	public Map<String,CatalogVocabularySet> getVocabularies();
	public void setVocabulary(String vocShortName,CatalogVocabularySet voc);
	public void setVocabulary(Integer guiLanguageId, CatalogVocabularySet voc);
	
	public void setThumbnailUrl(String thumbnailUrl);
	public String getThumbnailUrl();
	
	/** get the number of documents */
	public Long getNbDocuments();
	public void setNbDocuments(Long nbDocs);
	
	/** about managed fields mapping 
	 * @throws DataProcessException */
	public void updateTermsMapping(Map<String,RAW_DATATYPE> m,Map<String,Map<String,Object>> properties) throws DataProcessException;
	
	/* Database management shortcuts */
	/**
	 * Update custom params from SQL db
	 * @throws DataProcessException
	 */
	public void loadCustomParamsFromdb() throws DataProcessException;
	/**
	 * Update statistics for ElasticSearch index corresponding to this catalog
	 * @throws DataProcessException
	 */
	public void loadStatsFromDb() throws DataProcessException;
	/**
	 * Update list ofElasticSearch mappings
	 * @throws DataProcessException
	 */
	public void loadMappingFromDb() throws DataProcessException;
	/**
	 * Update perspectives definition from SQL Db contents
	 * @throws DataProcessException
	 */
	public void loadPerspectivesFromdb() throws DataProcessException;
	
	/**
	 * Update vocabularies definition from SQL Db contents
	 * @throws DataProcessException
	 */
	public void loadVocabulariesFromDb() throws DataProcessException;
	
	/**
	 * Load vocabulary of all registered terms
	 */
	public void loadTermsVocabularyFromDb() throws DataProcessException;
	
	/**
	 * 
	 * @param term
	 * @param guiLanguageId
	 * @param translation
	 * @throws DataProcessException
	 */
	public void updateTermVocabulary(List<TermVocabularySet> data) throws DataProcessException;
	
	
	
}
