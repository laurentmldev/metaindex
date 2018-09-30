package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class AllElementsCatalog extends VirtualCatalog {

	private Log log = LogFactory.getLog(AllElementsCatalog.class);
	
	public static final int ALL_ELEMENTS_CATALOG_ID = 0;
	private final String SEARCH_QUERY = "*";
	public AllElementsCatalog(Community myCommunity) {
		super(myCommunity);
		
	}
	
	@Override
	public String getSearchQuery() {
		return SEARCH_QUERY;
	}

	@Override
  	public int getCatalogId() {
		return ALL_ELEMENTS_CATALOG_ID;
	}

}
