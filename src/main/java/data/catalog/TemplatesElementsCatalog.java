package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.userprofile.IUserProfileData;
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
public class TemplatesElementsCatalog extends VirtualCatalog {

	private Log log = LogFactory.getLog(TemplatesElementsCatalog.class);
	
	public static final int TEMPLATES_ELEMENTS_CATALOG_ID = -2;
	
	public TemplatesElementsCatalog(Community myCommunity) {
		super(myCommunity);
		
	}
	
	@Override
	public String getSearchQuery() {
		return "*";
	}

	@Override
  	public int getCatalogId() {
		return TEMPLATES_ELEMENTS_CATALOG_ID;
	}
	
	@Override
	public List<Integer> getStaticElementsList(IUserProfileData activeUser) {
		return new ArrayList<Integer>();
	}
	@Override
	public List<Integer> getDynamicElementsList(IUserProfileData activeUser) {
		return this.getCatalogDBAccessor().getTemplateElementsIds(activeUser, this.getCommunityData());
	}
	
	

}
