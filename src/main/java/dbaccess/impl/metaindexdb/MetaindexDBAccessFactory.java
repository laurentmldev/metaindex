package metaindex.dbaccess.impl.metaindexdb;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.dbaccess.ADataAccessFactory;
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

public class MetaindexDBAccessFactory extends ADataAccessFactory {

	public int getSmallStringMaxDBLength() { return 200; }
	public int getLongStringMaxDBLength() { return 4080; }
	
	public AGuiLanguagesAccessor getNewGuiLanguagesAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException {
		return new metaindex.dbaccess.impl.metaindexdb.DBGuiLanguagesAccessor(this,dataSource,txManager);
	}
	public AGuiThemesAccessor getNewGuiThemesAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException {
		return new metaindex.dbaccess.impl.metaindexdb.DBGuiThemesAccessor(this,dataSource,txManager);
	}
	public AUserProfileAccessor getNewUsersAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException
	{
		return new metaindex.dbaccess.impl.metaindexdb.DBUsersAccessor(this,dataSource,txManager);
	}
	public ACommunityAccessor getNewCommunitiesAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException {
		return new metaindex.dbaccess.impl.metaindexdb.DBCommunitiesAccessor(this,dataSource,txManager);
	}
	public ACommunityTermsAccessor getNewCommunityTermsAccessor(DataSource dataSource,PlatformTransactionManager txManager) 
			throws DataAccessErrorException,DataAccessConnectException {
		return new DBCommunityTermsAccessor(this,dataSource,txManager);
	}	
	public AElementAccessor getNewElementsAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException {
		return new DBElementAccessor(this,dataSource,txManager);
	}
	
	public ADatasetAccessor getNewDatasetsAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException {
		return new DBDatasetAccessor(this,dataSource,txManager);
	}
	public AMetadataAccessor getNewMetadatasAccessor(DataSource dataSource,PlatformTransactionManager txManager)
			throws DataAccessErrorException,DataAccessConnectException {
		return new DBMetadataAccessor(this,dataSource,txManager);
	}
	@Override
	public ACatalogAccessor getNewCatalogsAccessor(DataSource dataSource, PlatformTransactionManager txManager)
			throws DataAccessErrorException, DataAccessConnectException {
		return new DBCatalogAccessor(this,dataSource,txManager);
	}
}
