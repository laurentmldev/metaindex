package metaindex.dbaccess.accessors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.community.Community;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.*;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/**
 * We don't want to inherit from AMetaindexDBAccessor since UserProfileData is quite special :
 * it is itself a userData, so AMetainxDBAccessor relationship as a 'associatedData' with UserProfileData (i.e. itself)
 * is not relevant here.
 * 
 * @author laurent
 *
 */
public abstract class AUserProfileAccessor extends AJdbcDataAccessor  {

	public AUserProfileAccessor(ADataAccessFactory accessorsFactory, DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}


	/**
	 * Insert new entries corresponding to the given data object
	 * @param dataObject
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract void createIntoDB(IUserProfileData activeUser, IUserProfileData dataObject)
		throws DataAccessErrorException,DataAccessConstraintException;
	
	/**
	 * Execute the SQL sequence returned by the prepareStoreIntoDB method of this object
	 * (within an SQL transaction).
	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObject the data object to use as a source for populating database
	 * It's "isFullyPopulated()" method should return true in order to ensure that provided 
	 * data will be complete enough to build the DB request.
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void storeIntoDB(IUserProfileData activeUser, IUserProfileData dataObject)
				throws DataAccessErrorException,DataAccessConstraintException
	{
		List<String> updateSqlSequence = prepareStoreIntoDB(activeUser,dataObject);
		executeSqlSequence(updateSqlSequence);
	}

	
	/**
 	 * Prepare SQL statement for storing current object into DB, i.e. build the
 	 * corresponding DB query and return it without executing it.
 	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObject the data object to use as a source for populating database
	 * It's "isFullyPopulated()" method should return true in order to ensure that provided 
	 * data will be complete enough to build the DB request.
	 * @return the SQL statement to execute for storing this object into DB 
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract List<String> prepareStoreIntoDB(IUserProfileData activeUser, IUserProfileData dataObject)
			throws DataAccessErrorException,DataAccessConstraintException;

	/**
	 * Update the given data object with the corresponding DB data. The mandatory fields (keys)
	 * depend on each T type (to be specified in each corresponding implementation comments).
	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObject the data object to use as a source for building database request 
	 * and as a container for retrieved data. 
	 * It's "isIdentified()" method should return true in order to ensure that provided 
	 * data will be enough to build the DB request.
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract void refreshFromDB(IUserProfileData activeUser, IUserProfileData dataObject)
			throws DataAccessErrorException,DataAccessConstraintException;

	/**
	 * Execute the SQL sequence returned by the prepareDeleteFromDB method of this object
	 * (within an SQL transaction).
	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObject the data object to delete
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void deleteFromDB(IUserProfileData activeUser, IUserProfileData dataObject)
				throws DataAccessErrorException,DataAccessConstraintException
	{
		List<String> deleteSqlSequence = prepareDeleteFromDB(activeUser,dataObject);
		executeSqlSequence(deleteSqlSequence);
	}

	
	/**
	 * Return sequence of SQL statements to execute to delete given object and any other related data if necessary within a transaction
	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObject the data object to delete
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract List<String> prepareDeleteFromDB(IUserProfileData activeUser, IUserProfileData dataObject)
			throws DataAccessErrorException,DataAccessConstraintException;

	
	/**
	 * Return all the T objects found on DB
	 * @param the user (used for potential access rights)
	 * @return the list of found objects
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract List<IUserProfileData> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException;	
	
}
