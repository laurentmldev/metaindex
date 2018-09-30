package metaindex.data.element;

import java.util.List;
import java.util.Map;
import java.util.Set;

import metaindex.data.IBufferizedData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

/**
 * Element data interface
 * @author laurent
 *
 */
public interface IElementFunctions{


	public void clearDatasets();
	public boolean isTemplateLoadError();
	public Integer getCommunityId() ;
	public void setCommunityId(int communityID) ;
	public String getTemplateRefElementName();
	/** get the number of elements using this one as a template. */
	public int getNbReferencingElements();
	public int getNbDatasets();
	public boolean isModifyOverridenTemplate();
	public String getSearchText();
	/** Say if this element is still valid in the community */	
	public boolean isOutDated();
	public Set<Integer> getDatasetIds();
	
	/**
	 * Dump contents as log info
	 * @param depthStr string to put as prefix
	 */
	public void dump(String depthStr);
	
	
}
