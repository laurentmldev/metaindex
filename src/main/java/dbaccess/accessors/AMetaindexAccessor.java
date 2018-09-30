package metaindex.dbaccess.accessors;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.ContextLoader;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IBufferizedData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.element.IElement;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AJdbcDataAccessor;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDBAccessorsUser;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.impl.metaindexdb.MetaindexDBAccessFactory;
import metaindex.data.AAccessControledData;

public abstract class AMetaindexAccessor <T extends IGenericMetaindexData> extends AJdbcDataAccessor 
														implements IDBAccessorsUser,IDBAccessedData,IDBAccessFactoryManager {

	
	private static Log log = LogFactory.getLog(AMetaindexAccessor.class);

	protected abstract class ADBBatchOperator {
		public abstract List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException;
		public abstract void populateStatements(IUserProfileData activeUser, T dataObject, List<PreparedStatement> stmts) throws SQLException;
	};
	
	public AMetaindexAccessor(ADataAccessFactory accessorsFactory, DataSource dataSource, 
							PlatformTransactionManager transactionManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,transactionManager);
		
	}

	public abstract String getTableName();
		
	public Integer getNextId() {
		// this will return the last used ids in DB
		String sql="SELECT Auto_increment FROM information_schema.tables WHERE table_name='"+getTableName()+"'";
		List<Integer> res = this.queryForIntegerList(sql);		
		Integer result=0;
		Iterator<Integer> it = res.iterator();
		while (it.hasNext()) { 
			Integer cur = it.next();
			if (cur > result) { result = cur; } 
		}
		return result+1;
	}
	
	/**
	 * Build a batch of DB commands based on a given buikdStatement and fillSatement functions.
	 * @param activeUser
	 * @param dataObjects
	 * @param getStatementDefFunc
	 * @param getStatementContentsFunc
	 */
	public void executeBatchDBOperations(IUserProfileData activeUser, List<T> dataObjects, ADBBatchOperator dbOp) 
																				throws DataAccessConstraintException {
		
		Connection con=null;
		List<String> populateErrorsMessages=new ArrayList<String>();
		
		try {			
			con = this.getDataSource().getConnection();
			con.setAutoCommit(false);
			
			// retrieve statements definition
			List<PreparedStatement> statements = dbOp.createStatements(activeUser.getSelectedCommunity(),con);
			
			// populate statements for each object
			Iterator<T> it = dataObjects.iterator();
			int idxEl=0;
			while (it.hasNext()) {
				idxEl++;
				T curDataObj=it.next();
				try { dbOp.populateStatements(activeUser, curDataObj, statements); }
				catch (Exception e) {
					populateErrorsMessages.add("Error on element "+idxEl+" of the list : "+e.getMessage());
				}
			}
			if (populateErrorsMessages.size()>0) {
				throw new DataAccessConstraintException(populateErrorsMessages);
			}
			// execute statements in the order they have been declared by the dbOperator
			Iterator<PreparedStatement> itStmts = statements.iterator();
			while (itStmts.hasNext()) {
				PreparedStatement statement = itStmts.next();
				int[] res = statement.executeBatch();
				for (int i=0;i<res.length;i++) {
					int curRes=res[i];
					if (curRes==Statement.EXECUTE_FAILED) {
						con.rollback();					
						throw new DataAccessErrorException("Unable to execute batch command #"+i+" : "+statement);
					}
				}
			}
			
			// commit db
			con.commit();
			con.setAutoCommit(true);
			
			// TODO : elements storeIntoDB?
		} catch (SQLException e) {
			e.printStackTrace();
			if (con!=null) {
				try { con.rollback(); } 
				catch (SQLException e1) { e1.printStackTrace(); }
			}
			throw new DataAccessErrorException(e);
		} 
	}
	
	public abstract ADBBatchOperator getCreateIntoDBOperator();
	public abstract ADBBatchOperator getStoreIntoDBOperator();
	public abstract ADBBatchOperator getDeleteFromDBOperator();
	
	
	/**
	 * Insert new entries corresponding to the given data object
	 * @param dataObject
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void createIntoDB(IUserProfileData activeUser, T dataObject)
		throws DataAccessErrorException,DataAccessConstraintException {
		List<T> list = new ArrayList<T>();
		list.add(dataObject);
		createIntoDB(activeUser, list);		
	}

	public void createFullIntoDB(IUserProfileData activeUser, List<T> dataObjects) {
		this.createIntoDB(activeUser, dataObjects);
	}
	
	/**
	 * Insert new entries corresponding to the given data objects list
	 * @param dataObject
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void createIntoDB(IUserProfileData activeUser, List<T> dataObjects)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		if (dataObjects.size()==0) { return; }
		executeBatchDBOperations(activeUser, dataObjects,this.getCreateIntoDBOperator());
		
	}
	
	
	/**
	 * Insert new entries corresponding to the given data object
	 * @param dataObject
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void storeIntoDB(IUserProfileData activeUser, T dataObject)
		throws DataAccessErrorException,DataAccessConstraintException {
		List<T> list = new ArrayList<T>();
		list.add(dataObject);
		storeIntoDB(activeUser, list);		
	}

	
	/**
	 * Insert new entries corresponding to the given data objects list
	 * @param dataObject
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void storeIntoDB(IUserProfileData activeUser, List<T> dataObjects)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		executeBatchDBOperations(activeUser, dataObjects,this.getStoreIntoDBOperator());		
	}

	/**
	 * Remove from DB informations related to this element (but not subelements)
	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObject the data object to delete
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void deleteFromDB(IUserProfileData activeUser, T dataObject)
				throws DataAccessErrorException,DataAccessConstraintException
	{
		List<T> list = new ArrayList<T>();
		list.add(dataObject);
		deleteFromDB(activeUser, list);
	}

	/**
	 * Remove from DB informations related to those elements (but not subelements)
	 * @param activeUserName the username to use for potential access rights checking
	 * @param dataObjects the data objects to delete
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public void deleteFromDB(IUserProfileData activeUser, List<T> dataObjects)
				throws DataAccessErrorException,DataAccessConstraintException
	{
		executeBatchDBOperations(activeUser, dataObjects,this.getDeleteFromDBOperator());
	}
	

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
	public abstract void refreshFromDB(IUserProfileData activeUser, T dataObject)
			throws DataAccessErrorException,DataAccessConstraintException;


	
	/**
	 * Return all the T objects found on DB
	 * @param the user (used for potential access rights)
	 * @return the list of found objects
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract List<T> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException;	
	
	


}
