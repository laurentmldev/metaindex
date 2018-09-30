package metaindex.data.element;

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
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunityComplexData;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.metadata.IMetadata;
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
public interface IElementHandle extends IElementContents,IElementFunctions, IAccessControledData, ICommunityComplexData {

	public List<IDatasetHandle> getDatasets();
	public IDatasetHandle getDataset(int datasetId);
	public void addDataset(IDatasetHandle d) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException ;
	public Map<Integer, IDatasetHandle> getDatasetsMap();
	public IMetadataHandle getMetadata(int metadataId);
	public void addAssociation(ICatalogHandle catalog) throws DataAccessErrorException, DataAccessConstraintException;
	public void removeAssociation(ICatalogHandle catalog) throws DataAccessErrorException, DataAccessConstraintException;
	
	/**
	 * 
	 * @param catalog
	 * @return position of this element in the given catalog. 0 if it is not found in the catalog.
	 */
	public int indexIn(ICatalogHandle catalog) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException;	
	public IElementHandle getTemplateRefElement() throws DataReferenceErrorException ;
		
	public void moveMetadata(IMetadataHandle movedMetadata,int newDatasetId, int newCol, int newPos) throws DataReferenceErrorException ;
	
	public IMetadataHandle createMetadata(Integer datasetId,String metadataName, String metadataComment, 
									Integer columnNb, Integer positionNb, Integer termId) 
						throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException ;
	

}
