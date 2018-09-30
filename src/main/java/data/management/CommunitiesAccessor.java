package metaindex.data.management;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADBAccessedData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class CommunitiesAccessor extends ADBAccessedData implements ICommunitiesAccessor {

	private static Log log = LogFactory.getLog(CommunitiesAccessor.class);
	
	private Integer communitiesLastId=0;
	private Integer catalogsLastId=0;
	private Integer elementsLastId=0;
	private Integer datasetsLastId=0;
	private Integer metadataLastId=0;
	private Integer termsLastId=0;
	
	// Map : idName / Community object
	static Map<String,ICommunity> instanciatedCommunities = new HashMap<String, ICommunity>();
	
	static public ADataAccessFactory getDataAccessors() {
		return singleton_myself.getDataAccess();
	}
	static CommunitiesAccessor singleton_myself = new CommunitiesAccessor();
	
	static public synchronized Integer getNewCommunityId() {	
		if (singleton_myself.communitiesLastId==0) { 
			singleton_myself.communitiesLastId = singleton_myself.getCommunityDBAccessor().getNextId(); 
		} else singleton_myself.communitiesLastId++;
		return 	singleton_myself.communitiesLastId;
	}
	static synchronized public Integer getNewCatalogId() {	
		if (singleton_myself.catalogsLastId==0) { 
			singleton_myself.catalogsLastId = singleton_myself.getCatalogDBAccessor().getNextId(); 
		} else singleton_myself.catalogsLastId++;
		return 	singleton_myself.catalogsLastId;
	}
	static synchronized public Integer getNewElementId() {	
		if (singleton_myself.elementsLastId==0) { 			
			singleton_myself.elementsLastId = singleton_myself.getElementDBAccessor().getNextId();			
		} else { singleton_myself.elementsLastId++; }
		return 	singleton_myself.elementsLastId;
	}
	static synchronized public Integer getNewDatasetId() {	
		if (singleton_myself.datasetsLastId==0) { 
			singleton_myself.datasetsLastId = singleton_myself.getDatasetDBAccessor().getNextId(); 
		} else singleton_myself.datasetsLastId++;
		return 	singleton_myself.datasetsLastId;
	}
	static synchronized public Integer getNewMetadataId() {	
		if (singleton_myself.metadataLastId==0) { 
			singleton_myself.metadataLastId = singleton_myself.getMetadataDBAccessor().getNextId(); 
		} else singleton_myself.metadataLastId++;
		return 	singleton_myself.metadataLastId;
	}
	static synchronized public Integer getNewTermId() {	
		if (singleton_myself.termsLastId==0) { 
			singleton_myself.termsLastId = singleton_myself.getCommunityTermsDBAccessor().getNextId(); 
		} else singleton_myself.termsLastId++;
		return 	singleton_myself.termsLastId;
	}	
	private CommunitiesAccessor() {
		super(ADataAccessFactory.getDataAccessImplFactory(
				ADataAccessFactory.DATA_ACCESS_IMPL_DB_METAINDEX));	
		
		
	} 
	
	/**
	 * Get community already found in the list, or if not try to load it from DB
	 * @param idName
	 * @return updated community. Create community if not already exists.
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public static ICommunity getOrLoadCommunity(IUserProfileData activeUser, String idName) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		if (idName.length()==0) {
			throw new DataReferenceErrorException("IdName is empty, unable to retrieve corresponding community.");
		}
		
		if (!instanciatedCommunities.containsKey(idName)) { 		
			ICommunity newCommunityInstance = new Community(idName);
			newCommunityInstance.update(activeUser);
			addCommunity(newCommunityInstance);	
			newCommunityInstance.startServices();
		}
		return instanciatedCommunities.get(idName);
	}
	
	public static void addCommunity(ICommunity c) throws DataAccessConstraintException {
		if (instanciatedCommunities.containsKey(c.getIdName())) {
			throw new DataAccessConstraintException("Community '"+c.getIdName()+"' already registered, unable to add it again.");			
		}
		if (c.getCommunityId()>singleton_myself.communitiesLastId) { singleton_myself.communitiesLastId=c.getCommunityId(); }
		instanciatedCommunities.put(c.getIdName(), c);		
	}
	
	/**
	 * Retrieve required community if found null otherwise.
	 * @param idName
	 * @return searched community if found, throw exception otherwise. 
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public static ICommunity getCommunity(String idName) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		
		if (idName.length()==0) {
			throw new DataReferenceErrorException("IdName is empty, unable to retrieve corresponding community.");
		}		
		
		if (! instanciatedCommunities.containsKey(idName)) {			
			throw new DataReferenceErrorException("Unable to properly get Community '"+idName);			
		}
		ICommunity c = instanciatedCommunities.get(idName);
		return c;
	}
	
	public static boolean communityExists(String idName) {		
		return instanciatedCommunities.containsKey(idName);
	}

	/**
	 * Retrieve corresponding community object if exists.
	 * @param searchedCommunityId
	 * @return searched community if found, null otherwise
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public static ICommunity getCommunity(Integer searchedCommunityId) 
		throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
			
			Iterator<ICommunity> it = instanciatedCommunities.values().iterator();
			
			while (it.hasNext()) {
				ICommunity c = it.next();
				if (c.getCommunityId().equals(searchedCommunityId)) { return c; }
			}
			throw new DataReferenceErrorException("Community ID '"+searchedCommunityId+"' not found.");
	}
	
	/**
	 * Empty currently loaded communities (useful for UTests essentially)
	 */
	public static void reset() {
		instanciatedCommunities.clear();
		CatalogsAccessor.clear();
		// Load communities available in DB using a default Admin user
		IUserProfileData adminUser = new UserProfileData();
		adminUser.setUsername(AMetaindexBean.ADMIN_USER_NAME);
		List<ICommunity> communitiesInDb = singleton_myself.getCommunityDBAccessor().loadAllDataFromDB(adminUser);
		Iterator<ICommunity> it = communitiesInDb.iterator();
		while (it.hasNext()) { addCommunity(it.next()); }
			
	}
		
}
