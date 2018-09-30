package metaindex.dbaccess;

import java.util.Hashtable;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.RowMapper;

import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/**
 * Generic SQL abstraction class for updates, queries and transactions.
 * @author laurent
 *
 */
public interface ISQLDataAccessor extends IDataAccessAware {
	
	/**
	 * Execute all the given SQL statements in a unique SQL transaction
	 * @param sqlStatements the list of SQL statements to execute
	 * @throws DataAccessErrorException if problem occured while executing SQL statements
	 */
	public abstract void executeSqlTransaction(List<String> sqlStatements) throws DataAccessErrorException,DataAccessConstraintException;
	
	/**
	 * Perform an SQL update of data executing the given SQL statement
	 * @param sqlUpdateCmd the SQL update statement
	 * @throws DataAccessErrorException if problem occured while executing SQL statements
	 */
	public abstract void updateSqlData(String sqlUpdateCmd) throws DataAccessErrorException,DataAccessConstraintException;

	
	/**
	 * Perform an SQL insert of a single row executing the given SQL statement, and retrieving the corresponding generated ID
	 * @param sqlInsertCmd the SQL insert statement
	 * @return id the ID of the inserted row
	 * @throws DataAccessErrorException if problem occured while executing SQL statements
	 */
	public abstract int insertSqlRow(String sqlInsertCmd) throws DataAccessErrorException,DataAccessConstraintException;
	
	/**
	 * Perform given SQL query and return the result in the required data object
	 * @param sqlQueryCmd the SQL query
	 * @param mapper the mapper for putting the result of the SQL query into the data object
	 * @return the (generic) data object to be filled in by the mapper
	 * @throws DataAccessErrorException if problem occured while executing SQL statements
	 */
	public abstract < T > List<T> querySqlData(String sqlQueryCmd, RowMapper<T> mapper) throws DataAccessErrorException;
	
	/**
	 * Retrieve Integer list as a result of the given query
	 * @param sqlQueryCmd
	 * @return List of Integers, empty list if no result found
	 * @throws DataAccessErrorException if the query could not be executed, or the result not converted to a list of Integers.
	 */
	public abstract  List<Integer> queryForIntegerList(String sqlQueryCmd) throws DataAccessErrorException;
	
	/**
	 * Retrieve String list as a result of the given query
	 * @param sqlQueryCmd
	 * @return List of Integers, empty list if no result found
	 * @throws DataAccessErrorException if the query could not be executed, or the result not converted to a list of Strings.
	 */
	public abstract  List<String> queryForStringList(String sqlQueryCmd) throws DataAccessErrorException;

	/**
	 * Execute the given SQL script
	 * @param sqlScript script file loaded, typically "new ClassPathResource('path/to/script.sql')"
	 * @throws DataAccessErrorException if any SQL error occured
	 */
	public abstract void executeSqlScript(Resource sqlScript) throws DataAccessErrorException;
	
	/**
	 * Execute the given SQL script, replacing each given key by its corresponding value
	 * @param sqlScript script file loaded, typically "new ClassPathResource('path/to/script.sql')"
	 * @throws DataAccessErrorException if any SQL error occured
	 */
	public abstract void executeSqlParameterizedScript(Resource sqlScript, Hashtable<String,String> keymap) 
														throws DataAccessErrorException,DataAccessConstraintException;
	
}
