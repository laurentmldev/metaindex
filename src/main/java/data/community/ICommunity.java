package metaindex.data.community;

import java.util.List;
import java.util.Map;

import metaindex.data.IBufferizedData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.IMultiLanguageData;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.websockets.AMetaindexWSServer;
import metaindex.websockets.ThumbnailsServer;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface ICommunity extends IGenericCommunity, IMultiLanguageData<CommunityVocabularySet>, ICommunityAccessing, ICommunitySubdata,IBufferizedData {

	/**
	 * Start community services, typically web socket servers
	 */
	public void startServices();
	public void enter(IUserProfileData p);
	public void quit(IUserProfileData p);
	
	public List<IElement> getElements();
	public Map<Integer,IElement> getElementsMap();
	public ICatalog getCatalog(int catalogId) throws DataAccessErrorException;
	public List<IElement> getTemplateElements();
	public IElement getElement(Integer elementId) throws DataAccessErrorException ;
	public IDataset getDataset(Integer datasetId) throws DataAccessErrorException ;
	public IMetadata getMetadata(Integer metadataId) throws DataAccessErrorException ;
	public void addElement(IUserProfileData activeUser, IElement el) throws DataAccessConstraintException;
	public void removeElement(IUserProfileData activeUser, IElement el) throws DataAccessConstraintException;
	/** Return updated elements 
	 * @throws DataReferenceErrorException 
	 * @throws DataAccessConstraintException */
	public void createFullAllElements(IUserProfileData activeUser, List<IElement> elts) throws DataAccessConstraintException, DataReferenceErrorException;
	public void deleteElements(IUserProfileData activeUser, List<IElement> el) 
									throws DataAccessConstraintException, DataAccessErrorException, DataReferenceErrorException ;
	public void addCatalog(ICatalog cat) throws DataAccessConstraintException;
	public IUserProfileData getLoggedUserProfile(String sessionId);
	
	public List<IElementHandle> getUserElements(IUserProfileData user);
	public CommunityVocabularySet getVocabulary(IUserProfileData activeUser);
	public ICatalogHandle getUserCatalog(IUserProfileData user,int catalogId) throws DataAccessErrorException;
	public IElementHandle getUserElement(IUserProfileData user,Integer elementId) throws DataAccessErrorException;	
	public void joinCommunity(IUserProfileData activeUser, IUserProfileData userToAdd) throws DataAccessErrorException, DataAccessConstraintException;

	public AMetaindexWSServer getCatalogServer();
	public AMetaindexWSServer getElementDataServer();
	public AMetaindexWSServer getChatServer();
	public ICommunityTerm getTermData(String termIdName);
	public ICommunityTerm getTermDataById(int termId);
	
	public void addTerm(ICommunityTerm newTerm);
	public List<ICommunityTerm> getTerms();
	public List<ICatalog> getCatalogs();
	
	/**
	 * Retrieve list of elements using given term
	 * @param idName
	 * @return Element Ids 
	 */
	public List<Integer> getElementsIdsByTerm(Integer termId);
}
