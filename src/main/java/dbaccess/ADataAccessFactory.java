package metaindex.dbaccess;


import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.AGenericMetaindexData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AGuiLanguagesAccessor;
import metaindex.dbaccess.accessors.AGuiThemesAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AUserProfileAccessor;


public abstract class ADataAccessFactory  {

	public abstract int getSmallStringMaxDBLength();
	public abstract int getLongStringMaxDBLength();
	
	private static Log log = LogFactory.getLog(AGenericMetaindexData.class);
	public static final String DATA_ACCESS_IMPL_DB_METAINDEX = "metaindexdb";
	
	public static ADataAccessFactory getDataAccessImplFactory(String dbImplId) {
		if (dbImplId.equals(DATA_ACCESS_IMPL_DB_METAINDEX)) 
		{ 
			return new metaindex.dbaccess.impl.metaindexdb.MetaindexDBAccessFactory(); 
		}
		else { 
			log.error("Unknown Metaindex DB implementation : '"+dbImplId+"'");
			return null;
		}
	}
	
	public abstract AGuiLanguagesAccessor getNewGuiLanguagesAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract AGuiThemesAccessor getNewGuiThemesAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException; 
	public abstract AUserProfileAccessor  getNewUsersAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract ACommunityAccessor getNewCommunitiesAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract ACommunityTermsAccessor getNewCommunityTermsAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract ACatalogAccessor getNewCatalogsAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract AElementAccessor getNewElementsAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract ADatasetAccessor getNewDatasetsAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException;
	public abstract AMetadataAccessor getNewMetadatasAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException;

}
