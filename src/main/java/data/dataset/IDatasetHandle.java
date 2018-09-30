package metaindex.data.dataset;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IAccessControledData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunityComplexData;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;
import metaindex.data.AAccessControledData;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface IDatasetHandle extends  IDatasetContents,IDatasetFunctions,IAccessControledData, ICommunityComplexData {


	public IMetadataHandle getMetadata(int metadataId) throws DataAccessErrorException; 
	public List<List<IMetadataHandle> > getColumnsMetadata();
	public Map<Integer, IMetadataHandle> getMetadatasMap();
	
	public void addMetadata(IMetadataHandle newChild) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException;	
	
	
	public List<IMetadataHandle> getMetadata(); 
	public IElementHandle getParentElementData();
	public void setParentElementData(IElementHandle e) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException;
	
	
}
