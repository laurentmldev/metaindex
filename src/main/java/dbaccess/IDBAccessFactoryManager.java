package metaindex.dbaccess;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.IBufferizedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public interface IDBAccessFactoryManager  {

	
	public void setDataAccess(ADataAccessFactory dataAccess) throws DataAccessErrorException;
	
	/**
	 * Get data accessors factory.
	 * @return data access factory allowing bean to access their data
	 */
	public ADataAccessFactory getDataAccess();
	
 

	
	
}
