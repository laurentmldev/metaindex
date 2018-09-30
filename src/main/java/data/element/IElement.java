package metaindex.data.element;

import java.util.List;
import java.util.Map;
import java.util.Set;

import metaindex.data.IBufferizedData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.community.ICommunityAccessing;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.dataset.IDataset;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

/**
 * Element data interface
 * @author laurent
 *
 */
public interface IElement extends ICommunityAccessing,IElementContents,IElementFunctions,ICommunitySubdata,
								  IBufferizedData, IDataAccessAware,IDBAccessedData,IDBAccessFactoryManager {
	
	
	/** To be called when this element is not available anymore in the community */
	public void setOutDated();
	/** get datasets of this element, including implicit datasets from template */
	public List<IDataset> getDatasets();
	/** get datasets directly belonging to this element (i.e. without 'template' abstraction) */
	public List<IDataset> getLocalDatasets();
	public IDataset getDataset(int datasetId);
	public void addDataset(IDataset d);
	public Map<Integer, IDataset> getDatasetsMap();
	public IMetadata getMetadata(int metadataId);
	//public List<String> prepareCreateSequence(IUserProfileData activeUser);
	public void addAssociation(IUserProfileData activeUser, ICatalogContents catalog) throws DataAccessErrorException, DataAccessConstraintException;
	public void removeAssociation(IUserProfileData activeUser, ICatalogContents catalog) throws DataAccessErrorException, DataAccessConstraintException;
	/**
	 * 
	 * @param catalog
	 * @return position of this element in the given catalog. 0 if it is not found in the catalog.
	 */
	public int indexIn(ICatalogContents catalog) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException ;	
	public IElement getTemplateRefElement() throws DataReferenceErrorException ;
	
	/**
	 * Move the given metadata to the specified position
	 * and move all the  subsequent metadatas too if necessary
	 * @param activeUser
	 * @param movedMetadata
	 * @param newDatasetId
	 * @param newCol
	 * @param newPos
	 * @throws DataReferenceErrorException
	 */		
	public void moveMetadata(IUserProfileData activeUser, IMetadata movedMetadata,int newDatasetId, int newCol, int newPos) throws DataReferenceErrorException ;
	
	/**
	 * Create a new metadaa in this element, handling the case of creating a metadata coresponding to the template element.
	 * @param activeUser
	 * @param datasetId
	 * @param metadataName
	 * @param metadataComment
	 * @param columnNb
	 * @param positionNb
	 * @param termId
	 * @return
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public IMetadata addMetadata(IUserProfileData activeUser, Integer datasetId,
			String metadataName, String metadataComment, 
			Integer columnNb, Integer positionNb, Integer termId) 
						throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException ;
	
}
