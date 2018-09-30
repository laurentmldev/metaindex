package metaindex.data.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import metaindex.data.IBufferizedData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.IMultiLanguageData;
import metaindex.data.IObservable;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/**
 * Bean storing catalog data
 * @author laurent
 *
 */
public interface ICatalogContents  
  {

	public int getCatalogId();
	public void setCatalogId(int catalogID);

	/**
	 * Dump contents as log info
	 * @param depthStr string to put as prefix
	 */
	public void dump(String depthStr);
	
	public Integer getCommunityId();
	public void setCommunityId(int communityID);

	public String getName();
	public void setName(String name);
	
	public String getComment();
	public void setComment(String comment);
	
	public String getSearchQuery();
	public void setSearchQuery(String searchQuery);

	/**
	 * We deduce that a catalog is dynamic if its search query is not empty
	 */
	public boolean isDynamic();
	public boolean isVirtual();	
	public int getElementsCount();
	public boolean isStaticElement(int elementId);
	public int getNbDynamicElements();
	public int getNbStaticElements();
	
	
}
