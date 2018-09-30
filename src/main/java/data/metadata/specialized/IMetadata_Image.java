package metaindex.data.metadata.specialized;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataContents;
import metaindex.data.metadata.MetadataHandle;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Bean storing community data
 * @author laurent
 *
 */
 public interface IMetadata_Image extends IMetadata_Specialized {
	
	
	public String getImageUrl();
	public void setImageUrl(String imageLink);
	
	public boolean isThumbnail();
	public void setThumbnail(boolean val);
	
	public void setBorderSize(Long borderSize);
	public Long getBorderSize();
	
	public void setBorderColor(String color);
	public String getBorderColor();

	public String getSearchText();
}
