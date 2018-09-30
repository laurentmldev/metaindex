package metaindex.data.element;

import java.util.List;
import java.util.Map;
import java.util.Set;

import metaindex.data.IBufferizedData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.metadata.specialized.IMetadata_Image;
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
public interface IElementContents {

	public Integer getElementId() ;
	public void setElementId(Integer elementId);
	public String getComment();
	public void setComment(String comment) ;
	public boolean isTemplate() ;
	public void setTemplate(Boolean isTemplate) ;
	public Integer getTemplateRefElementId();
	public void setTemplateRefElementId(Integer refId);	
	public String getName() ;
	public void setName(String name) ;
	public String getThumbnailUrl() ;
	public void setThumbnailUrl(String url) ;
	public boolean hasThumbnail();		

}
