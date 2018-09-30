package metaindex.dbaccess.accessors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public abstract class AMetadataAccessor extends AMetaindexAccessor<IMetadata> 
									implements IAssociatedData<IMetadata,IDataset,IMetadataHandle>{

	
	public AMetadataAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
			super(accessorsFactory,dataSource,txManager);		
	}

	public abstract String getImageDatatypeName();
	public abstract String getNumberDatatypeName();
	public abstract String getWebLinkDatatypeName();
	public abstract String getTinyTextDatatypeName();
	public abstract String getLongTextDatatypeName();
	
	public abstract void loadMetadatasFromDB(IUserProfileData activeUser, IDataset parentDataset) 
										throws DataAccessErrorException,DataAccessConstraintException;
	
}
