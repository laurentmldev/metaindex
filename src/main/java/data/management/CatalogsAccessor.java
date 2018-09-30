package metaindex.data.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class CatalogsAccessor implements ICatalogsAccessor {

	private static Log log = LogFactory.getLog(CatalogsAccessor.class);
	
	// keep track of instanciated UserCatalogs
	static Map<Integer, Map<Integer, List<ICatalogHandle> > > communityCatalogsHandle = new HashMap<Integer, Map<Integer, List<ICatalogHandle> > >();
	
	static CatalogsAccessor singleton_myself = new CatalogsAccessor();
	
	private CatalogsAccessor() {} 
	
	static public Map<Integer,List<ICatalogHandle> > getCommunityCatalogHandles(Integer communityId) {
		if (communityCatalogsHandle.get(communityId)==null) { 
			communityCatalogsHandle.put(communityId, new HashMap<Integer,List<ICatalogHandle> >());			
		}
		return communityCatalogsHandle.get(communityId);
	}
	
	static public List<ICatalogHandle> getCatalogHandles(Integer communityId, Integer catalogId) {
		Map<Integer,List<ICatalogHandle> > communityCatalogHandles = getCommunityCatalogHandles(communityId);
		if (communityCatalogHandles.get(catalogId)==null) {
			communityCatalogHandles.put(catalogId, new ArrayList<ICatalogHandle>());
		}
		return communityCatalogHandles.get(catalogId);
	}

	static public void addCatalogHandle(ICatalogHandle c) {
		List<ICatalogHandle> handlesList = getCatalogHandles(c.getCommunityId(),c.getCatalogId());
		if (!handlesList.contains(c)) {
			handlesList.add(c);
		}
	}
	
	static public void deleteCatalogHandle(ICatalogHandle c) {
		getCatalogHandles(c.getCommunityId(),c.getCatalogId()).remove(c);		
	}
	/**
	 * Empty currently loaded communities (useful for UTests essentially)
	 */
	public static void clear() {
		communityCatalogsHandle.clear();
	}
	public static void clear(ICatalog c) {
		getCatalogHandles(c.getCommunityId(),c.getCatalogId()).clear();
	}	

}
