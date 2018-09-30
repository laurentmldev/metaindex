package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import metaindex.data.AAccessControledData;
import metaindex.data.IAccessControledData;
import metaindex.data.IBufferizedData;
import metaindex.data.IObserver;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IJsonEncodableHandle;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.test.TestCommunity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface ICatalogHandle extends ICatalogContents,IAccessControledData, IObserver<ICatalog>,IJsonEncodableHandle {

	public IElementHandle getSelectedElement(); 
	public void setSelectedElement(int elementId) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException;


	public IElementHandle nextElement() throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException;
	public void unsetSelectedElement();
	
	public void addStaticElement (int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException;
	public void removeStaticElement (int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException;
	public IElementHandle getElement(int elementId) throws DataAccessErrorException;
	public List<IElementHandle> getElements();
	
	/** 
	 * Get list of metadatas actually involved in this catalog
	 * @return
	 */
	public Map<Integer, IMetadataHandle> getMetadatas();
	/** 
	 * Get list of datasets actually involved in this catalog
	 * @return
	 */
	public Map<Integer, IDatasetHandle> getDatasets();
	
	public List<Integer> getStaticElementsList();
	public List<Integer> getDynamicElementsList();
}
