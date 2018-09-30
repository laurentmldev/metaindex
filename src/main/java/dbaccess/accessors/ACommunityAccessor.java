package metaindex.dbaccess.accessors;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.*;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;

public abstract class ACommunityAccessor extends AMetaindexAccessor<ICommunity> 
									implements IAssociatedData<ICommunity,IUserProfileData,ICommunityHandle>,
									IDataAccessAware {

	public ACommunityAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
				super(accessorsFactory,dataSource,txManager);
	}

	
}
