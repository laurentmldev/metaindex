package metaindex.data.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import metaindex.data.IGenericMetaindexData;
import metaindex.data.IObservable;
import metaindex.data.community.ICommunityAccessing;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalog extends IObservable<ICatalogHandle>, ICommunityAccessing, ICatalogContents,ICommunitySubdata {

	/**
	 * Return a clone of this element
	 * @return
	 */
	public abstract ICatalog clone() throws CloneNotSupportedException;

	public void addStaticElement (IUserProfileData activeUser, int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException;
	public void removeStaticElement (IUserProfileData activeUser, int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException;
	void removeStaticElements(IUserProfileData activeUser, List<IElement> elementsToRemove)
											throws DataAccessErrorException, DataAccessConstraintException;
	public IElement getElement(int elementId) throws DataAccessErrorException;
	public List<IElement> getElements();
	public Map<Integer, IElement> getElementsMap();
	public List<IElementHandle> getUserElements(IUserProfileData user);
	
	/** 
	 * Get list of metadatas actually involved in this catalog
	 * @return
	 */
	public Map<Integer, IMetadata> getMetadatas();
	public Map<Integer, IMetadataHandle> getUserMetadatas(IUserProfileData activeUser);
	/** 
	 * Get list of datasets actually involved in this catalog
	 * @return
	 */
	public Map<Integer, IDataset> getDatasets();
	public Map<Integer, IDatasetHandle> getUserDatasets(IUserProfileData activeUser);
	
	public List<Integer> getStaticElementsList(IUserProfileData activeUser);
	public List<Integer> getDynamicElementsList(IUserProfileData activeUser);
	

}
