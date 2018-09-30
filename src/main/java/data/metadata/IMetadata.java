package metaindex.data.metadata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IBufferizedData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunityAccessing;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.dataset.IDataset;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_LongText;
import metaindex.data.metadata.specialized.IMetadata_Number;
import metaindex.data.metadata.specialized.IMetadata_TinyText;
import metaindex.data.metadata.specialized.IMetadata_WebLink;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public interface IMetadata extends ICommunityAccessing, ICommunitySubdata, IMetadataContents, IMetadataFunctions {

	public static class PositionComparator implements Comparator<IMetadata> {

		@Override
		public int compare(IMetadata o1, IMetadata o2) {
			if (o1.getLayoutPosition()<o2.getLayoutPosition()) { return -1; }
			if (o1.getLayoutPosition()>o2.getLayoutPosition()) { return 1; }
			return 0;
		}
		
	}
	
  	public ICommunityTerm getTerm() ;
  	
  	public IDataset getParentDataset();
  	public void setParentDataset(IDataset dataset);
  	

}
