package metaindex.dbaccess;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.ContextLoader;

import metaindex.data.IBufferizedData;
import metaindex.data.community.Community;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDBAccessorsUser;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;

public abstract class AGenericDBData extends ADBAccessedData implements IBufferizedData {
	
	private Log log = LogFactory.getLog(AGenericDBData.class);
		

	public AGenericDBData(ADataAccessFactory accessorsFactory)  {
		super(accessorsFactory);
		
	}
	
}

