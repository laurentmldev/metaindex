package metaindex.dbaccess;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
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

public abstract class ADBAccessedData extends AJsonEncodable
		implements IDBAccessedData,IDBAccessFactoryManager,IDataAccessAware,IDBAccessorsUser {
	
	//private static Log log = LogFactory.getLog(BeanData.class);
	private static final String DB_DATASOURCE = "dataSource";
	private static final String DB_TXMANAGER = "txManager";
	private static ADataAccessFactory dataAccessFactory;
	private static DataSource globaldatasource;
	private static PlatformTransactionManager globalTxManager;
	private DataSource datasource;
	private PlatformTransactionManager txManager;
	private static Log log = LogFactory.getLog(ADBAccessedData.class);
	
	public ADBAccessedData(ADataAccessFactory dbAccessorsFactory) {
		setDataAccess(dbAccessorsFactory);
	}
	
	
	@Override
	public void setDataAccess(ADataAccessFactory dataAccess) throws DataAccessErrorException {
			dataAccessFactory = dataAccess;		
	}
	
	/**
	 * Get data accessors factory.
	 * @return data access factory allowing bean to access their data
	 */
	@Override
	public ADataAccessFactory getDataAccess() { return dataAccessFactory; }
	
    /**
     * Get datasource object of this bean.
     * If not set, try to retrieve one using the 'DB_DATASOURCE' bean from current Web Application context (Spring)
     * 								
     * @return datasource used by this bean
     */
	@Override
	public DataSource getDataSource() {
		if (datasource==null) {
			// when Unit Test, we have set a Global data source to use
			if (globaldatasource!=null) { datasource = globaldatasource; }
			// production case, we retrieve the Spring bean data source defined in the applicationContext.xml file
			else { datasource = (DataSource)ContextLoader.getCurrentWebApplicationContext().getBean(DB_DATASOURCE); }
		}
		return datasource;
	}
	

	/**
	 * @param ds the data source to be used
	 * @throws BeanDataException if a datasource is already set for this bean
	 */
	@Override
	public void setDataSource(DataSource ds) throws BeanDataException {
		if (datasource != null) {
				throw new BeanDataException("Trying to set datasource '"+ds.toString()
						+"' while bean already has one : '"+datasource.toString()+"'");
		}
		datasource=ds;
	}

	
    /**
     * Get DB transaction manager
     * If not set, try to retrieve one using the 'DB_TXMANAGER' bean from current Web Application context (Spring)
     * 								
     * @return txManager used by this bean
     */
	@Override
	public PlatformTransactionManager getTxManager() {
		if (txManager==null) {
			// when Unit Test, we have set a Global data source to use
			if (globalTxManager!=null) { txManager = globalTxManager; }
			// production case, we retrieve the Spring bean data source defined in the applicationContext.xml file
			else { txManager = (PlatformTransactionManager)ContextLoader.getCurrentWebApplicationContext().getBean(DB_TXMANAGER); }
		}
		return txManager;
	}
	
	/**
	 * (re)set the global datasource to be used by any bean.
	 * We (re)set also the global Transaction Manager
	 * Used for unit tests purpose only.
	 * @param globalds the new global data source to be used
	 * @throws BeanDataException if a global datasource is already set for this bean
	 */
	public static void setGlobalDataSource(DataSource globalds)  {
		globaldatasource=globalds;
		globalTxManager=new DataSourceTransactionManager(globaldatasource);
	}
	

	@Override
	public ACommunityAccessor getCommunityDBAccessor() {
		try {  return getDataAccess().getNewCommunitiesAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ACatalogAccessor getCatalogDBAccessor() {
		try {  return getDataAccess().getNewCatalogsAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public ACommunityTermsAccessor getCommunityTermsDBAccessor() {
		try {  return getDataAccess().getNewCommunityTermsAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AElementAccessor getElementDBAccessor() {
		try {  return getDataAccess().getNewElementsAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ADatasetAccessor getDatasetDBAccessor() {
		try {  return getDataAccess().getNewDatasetsAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AMetadataAccessor getMetadataDBAccessor() {
		try {  return getDataAccess().getNewMetadatasAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	@Override
	public AGuiLanguagesAccessor getGuiLanguagesDBAccessor() {
		try {  return getDataAccess().getNewGuiLanguagesAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AGuiThemesAccessor getGuiThemesDBAccessor() {
		try {  return getDataAccess().getNewGuiThemesAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AUserProfileAccessor getUserProfileDBAccessor() {
		try {  return getDataAccess().getNewUsersAccessor(getDataSource(),getTxManager()); }
		catch (DataAccessErrorException|DataAccessConnectException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String checkCompliantWithDBSmallString(String objName, String testStr) throws DataAccessConstraintException {
		Integer maxLength=this.getDataAccess().getSmallStringMaxDBLength();
		Integer actualLength=testStr.length();
		if (actualLength>maxLength) {
			String msg ="While populating "+objName+" : Contents too long for DB "
								+"'small string' (max length is "+maxLength+" characters) : '"+testStr+"'";
			throw new DataAccessConstraintException(msg);
		}
		return testStr;
	}

	public String checkCompliantWithDBLongString(String objName, String testStr) throws DataAccessConstraintException {
		Integer maxLength=this.getDataAccess().getLongStringMaxDBLength();
		Integer actualLength=testStr.length();
		if (actualLength>maxLength) {
			String msg ="While populating "+objName+" : Contents too long for DB "
								+"'small string' (max length is "+maxLength+" characters) : '"+testStr+"'";
			throw new DataAccessConstraintException(msg);
		}
		return testStr;	
	}
}
