package metaindex.data.metadata;

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
import metaindex.data.community.ICommunityTermData;
import metaindex.data.community.ICommunityTermHandle;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.metadata.specialized.MetadataHandle_Image;
import metaindex.data.metadata.specialized.MetadataHandle_LongText;
import metaindex.data.metadata.specialized.MetadataHandle_TinyText;
import metaindex.data.metadata.specialized.MetadataHandle_WebLink;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
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
public interface IMetadataHandle extends IMetadataContents,IMetadataFunctions, IAccessControledData, ICommunityComplexData {
	
  	public ICommunityTermHandle getTerm() ;
  	
  	public IDatasetHandle getParentDataset();
  	public void setParentDataset(IDatasetHandle dataset) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException ;
  	
  	
  	  	
}
