package metaindex.data.dataset;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IBufferizedData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Bean storing Metadata characteristic information
 * @author laurent
 *
 */
public interface IDatasetContents{
  	
	
	public String getName();
	public void setName(String name);
	public Integer getElementId();
	public void setElementId(Integer elId);
	public String getComment();
	public void setComment(String comment);
	public Integer getDatasetId();
	public void setDatasetId(Integer datasetId);
	public Integer getLayoutNbColumns();
	public void setLayoutNbColumns(Integer nbColumns);
	public Integer getLayoutPosition();
	public void setLayoutPosition(Integer position);
	public boolean isLayoutAlwaysExpand();
	public void setLayoutAlwaysExpand(Boolean alwaysExpand);
	public boolean isLayoutDoDisplayName();
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName);

}
