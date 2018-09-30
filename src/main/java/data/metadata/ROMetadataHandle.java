package metaindex.data.metadata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.metadata.specialized.Metadata_TinyText;
import metaindex.data.metadata.specialized.Metadata_WebLink;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AccessRightException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;
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
public class ROMetadataHandle extends MetadataHandle {

	

	public ROMetadataHandle(IMetadata refData) {
		super(new UserProfileData(), refData);
	
	}

	public void checkWritableByUser() throws AccessRightException { throw new AccessRightException("Read-Only implementation of Metadata handle."); }
	public void checkManageableByUser() throws AccessRightException { throw new AccessRightException("Read-Only implementation of Metadata handle."); }
	public void checkReadableByUser() throws AccessRightException {  }

}
