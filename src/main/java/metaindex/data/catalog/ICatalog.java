package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;
import java.util.Map;

import org.apache.ftpserver.ftplet.FtpException;

import metaindex.data.filter.IFilter;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.TermVocabularySet;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IIdentifiable;
import toolbox.utils.ILockable;

/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalog extends IIdentifiable<Integer>,ILockable,ICatalogCustomParams {

	
	public void setId(Integer id);
	public void setName(String shortname);
		
	/// get UserId for whom created it
	public Integer getCreatorId();
	public void setCreatorId(Integer creatorId);
		
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
	
	public void startServices() throws DataProcessException;
	
	// Quotas Management
	public Long getQuotaNbDocs();
	public void setQuotaNbDocs(Long maxNbDocs);
	public Integer getQuotaFtpDiscSpaceBytes();
	public void setQuotaFtpDiscSpaceBytes(Integer maxFtpSpaceBytes);		
	public Boolean checkQuotasNbDocsOk();
	public Boolean checkQuotasDisckSpaceOk();
	public Long getDiscSpaceUseBytes();
	
	// Files access
	public String getLocalFsFilesPath();
	public Integer getFtpPort(IUserProfileData user);
	public String getFilesBaseUrl();	
	
	// Perspectives
	public Map<String,ICatalogPerspective> getPerspectives();	
	public void updateCatalogPerspectives(List<ICatalogPerspective> perspectivesFromSqlDb) throws DataProcessException;
	
	
	// Vocabulary
	public void updateCatalogVocabularies(List<CatalogVocabularySet> vocabulariesFromSqlDb) throws DataProcessException ;
	
	// User
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
	
	/**
	 * Walk through all 'Relation' terms and build a map with full ist
	 * of parent/child names. This is used when adding a new Relation term,
	 * since ElasticSearch only supports one single relation field per index
	 * listing all the relations.
	 * @return
	 */
	public Map<String,String> getTermsRelationsDefinitions();
	
	

	
}
