package metaindex.dbaccess.accessors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.community.Community;
import metaindex.data.community.CommunityDatatype;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.ICommunityTermData;
import metaindex.dbaccess.*;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;

public abstract class ACommunityTermsAccessor extends AMetaindexAccessor<ICommunityTerm> 
									implements IAssociatedData<ICommunityTerm,Community,CommunityTermHandle>,IDataAccessAware {

	
	public ACommunityTermsAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
							PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}
	
}
