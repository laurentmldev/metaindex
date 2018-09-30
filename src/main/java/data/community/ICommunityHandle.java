package metaindex.data.community;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import metaindex.data.AAccessControledData;
import metaindex.data.IAccessControledData;
import metaindex.data.IBufferizedData;
import metaindex.data.IBufferizedDataHandle;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.test.TestCommunity;
import metaindex.websockets.AMetaindexWSServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface ICommunityHandle extends IAccessControledData,IGenericCommunity {

	public ICatalogHandle getSelectedCatalog();
	public IElementHandle getSelectedElement();
	public void setSelectedCatalog(ICatalogHandle c) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException;
	public CommunityVocabularySet getVocabulary();
	public List<ICommunityHandle> getCommunities();
	public void joinCommunity() throws DataAccessErrorException, DataAccessConstraintException;
	public Map<String,ICommunityTermHandle> getTermsMap();
	
	public void enter();
	public void quit();
	
	
	public List<IElementHandle> getElements();
	
	public ICatalogHandle getCatalog(int catalogId) throws DataAccessErrorException;
	public IElementHandle getElement(Integer elementId) throws DataAccessErrorException;	
	public void joinCommunity(IUserProfileData userToAdd) throws DataAccessErrorException, DataAccessConstraintException;
	public void deleteElements(IUserProfileData activeUser, List<IElementHandle> el) 
							throws DataAccessConstraintException, DataAccessErrorException, DataReferenceErrorException ;
	/** Return updated elements 
	 * @throws DataReferenceErrorException 
	 * @throws DataAccessConstraintException */
	public void createFullElements(IUserProfileData activeUser, List<IElement> elts) throws DataAccessConstraintException, DataReferenceErrorException;
	public AMetaindexWSServer getCatalogServer();
	public AMetaindexWSServer getChatServer();
	public AMetaindexWSServer getElementDataServer();
	/**
	 * Retrieve list of elements using given term
	 * @param idName
	 * @return Element Ids 
	 */
	public List<Integer> getElementsByTerm(Integer termId);
	
	public ICommunityTermHandle getTermData(String termIdName);
	public ICommunityTermHandle getTermDataById(int termId);
	
	
	public List<IElementHandle> getTemplateElements();	
	public Map<Integer,IElementHandle> getElementsMap();
	
	public IUserProfileData getLoggedUserProfile(String sessionId);
	
	public void addTerm(ICommunityTermHandle newTerm) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException ;
	public List<ICommunityTermHandle> getTerms();
	public List<ICatalogHandle> getCatalogs();
}
