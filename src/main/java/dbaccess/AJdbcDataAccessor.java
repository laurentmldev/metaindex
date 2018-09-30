package metaindex.dbaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public abstract class AJdbcDataAccessor extends ADBAccessedData implements ISQLDataAccessor {
	
		private final JdbcTemplate jdbcTemplate;
		private final TransactionTemplate txTemplate;
		
		
		private static Log log = LogFactory.getLog(AJdbcDataAccessor.class);
	
	/**
	 * Escape tricky characters before storing them into DB
	 * @param stringToEscape the string to be escaped
	 * @return the escaped string
	 */
	static public String escape(String stringToEscape) {
		if (stringToEscape==null) { stringToEscape=""; }
		String result=stringToEscape;
		result=result.replaceAll("'", "@Apostrophe@");
		result=result.replaceAll("’", "@Apostrophe@");
		result=result.replaceAll(";", "@SemiColumn@");
		return result;
	}
	/**
	 * Unescape tricky characters before storing them into DB
	 * @param stringToEscape the string to be escaped
	 * @see escape
	 * @return the escaped string
	 */
	static public String unescape(String stringToEscape) {
		String result=stringToEscape;
		result=result.replaceAll("@Apostrophe@", "’");
		result=result.replaceAll("@SemiColumn@",";");
		return result;
	}
	
	public AJdbcDataAccessor(ADataAccessFactory accessorsFactory, DataSource dataSource, 
							PlatformTransactionManager transactionManager) throws DataAccessConnectException
	{
		super(accessorsFactory);
		 try {
			  jdbcTemplate = new JdbcTemplate(getDataSource());
			  txTemplate=new TransactionTemplate(getTxManager());
		 } catch (Exception e) {
			 	throw new DataAccessConnectException(e); }
	} 
	
	/**
	 * Execute all the given SQL statements as JDBC Transaction
	 * @TODO for now just perform a stupid sequence of SQL statements. Still need to implement real SQL transaction.
	 */
	public void executeSqlTransaction(final List<String> sqlStatements) throws DataAccessErrorException, DataAccessConstraintException
	{

			txTemplate.execute(new TransactionCallback<Object>() {

		      // the code in this method executes in a transactional context
		      public Object doInTransaction(TransactionStatus status) {
		    	  executeSqlSequence(sqlStatements);
		    	  return null;
		      }
		    });		
	}
	/**
	 * Execute all the given SQL statements without transaction.
	 * Use 'executeSqlTransaction' method for transactionned SQL sequence.
	 */
	public void executeSqlSequence(List<String> sqlStatements) throws DataAccessErrorException, DataAccessConstraintException
	{
		try 
		{
			Iterator<String> it = sqlStatements.iterator();
			while (it.hasNext())
			{
				String curSqlStatement=it.next();
				jdbcTemplate.execute(curSqlStatement);
			}
		}
		catch(Exception e) 
		{ 
			if (e instanceof org.springframework.dao.DuplicateKeyException)
			{
				throw new DataAccessConstraintException(e);
			}
			else { throw new DataAccessErrorException(e); } 
		}
	}
	
	public void updateSqlData(String sqlUpdateCmd) throws DataAccessErrorException,DataAccessConstraintException {
		try { jdbcTemplate.update(sqlUpdateCmd); }
		catch(Exception e) 
		{ 
			if (e instanceof  org.springframework.dao.DuplicateKeyException)
			{
				throw new DataAccessConstraintException(e);
			}
			else { throw new DataAccessErrorException(e); }		
		}
	}
	
	public int insertSqlRow(String sqlInsertCmd) throws DataAccessErrorException,DataAccessConstraintException {
		
		final String sqlInsert=sqlInsertCmd;

		// We must execute the command and the ID retrieval within a transaction, in order to ensure
		// that we keep everybody in a unique connection. 
		// If not, connection is reset between the SQL query and the ID retrieval, 
		// and LAST_INSERT_ID() always return 0.
		int id = txTemplate.execute(new TransactionCallback<Integer>() {
			  String sqlGetLastId="select LAST_INSERT_ID();";
		      public Integer doInTransaction(TransactionStatus status) {
		    	  
		    	// perform insert
		    	try { jdbcTemplate.update(sqlInsert); }
		  		catch(Exception e) { 
		  			if (e instanceof  org.springframework.dao.DuplicateKeyException)
		  			{ throw new DataAccessConstraintException(e); }
		  			else { throw new DataAccessErrorException(e); }		
		  		}
		    	  // retrieve and return the new row ID
		    	  return jdbcTemplate.queryForObject(sqlGetLastId,Integer.class);
		      }
		    });		
		
		return id;
		
}
	
	
	@Override
	public < T > List<T> querySqlData(String sqlQueryCmd, RowMapper<T> mapper) throws DataAccessErrorException {
		try { return jdbcTemplate.query(sqlQueryCmd, mapper); }
		catch(Exception e) {
			e.printStackTrace();
			throw new DataAccessErrorException(e); 
		}		
	}
	
	@Override
	public  List<Integer> queryForIntegerList(String sqlQueryCmd) throws DataAccessErrorException {
		try {	
			return jdbcTemplate.queryForList(sqlQueryCmd, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<Integer>();
		} 
	}
	
	@Override
	public  List<String> queryForStringList(String sqlQueryCmd) throws DataAccessErrorException {
		
		try {	
			return jdbcTemplate.queryForList(sqlQueryCmd, String.class);
		} catch (EmptyResultDataAccessException e) {
			return new ArrayList<String>();
		} 
		
	}	
	public void executeSqlScript(Resource sqlScriptResource) throws DataAccessErrorException {
		try {			
			ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(sqlScriptResource);
		    databasePopulator.populate(jdbcTemplate.getDataSource().getConnection());
		    
		} catch (SQLException e) {
			throw new DataAccessErrorException(e);
		}
	}
	
	public void executeSqlParameterizedScript(Resource sqlScript, Hashtable<String,String> keymap) 
							throws DataAccessErrorException,DataAccessConstraintException {
		
		String sqlScriptContents = "";
		
		try { 
			//log.error("### executeSqlParameterizedScript : "+sqlScriptContents);
			String scriptFilePath = sqlScript.getFile().getPath();
			FileReader input = new FileReader(scriptFilePath);
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			
			while ( (myLine = bufRead.readLine()) != null) 
			{ 
				if (!myLine.startsWith("--")) { sqlScriptContents+=myLine+" "; }
			}
			
			bufRead.close();
		} catch (IOException e) { throw new DataAccessErrorException(e); }
		
		List<String> sqlStatements = new ArrayList<String>();
		List<String> finalizedSqlStatements = new ArrayList<String>();
		ScriptUtils.splitSqlScript(sqlScriptContents, ";", sqlStatements);
		// log.error("### -> nb SQL= "+sqlStatements.size());
		// For each statement
		Iterator<String> it = sqlStatements.iterator();
		while (it.hasNext())
		{
			String sqlStatement = it.next()+";";
			String finalizedStatement=sqlStatement;
			Iterator<String> itKeys = keymap.keySet().iterator();
			// replace each key by the corresponding value
			while (itKeys.hasNext()) 
			{  
				String key = itKeys.next();
				String val = keymap.get(key);
				finalizedStatement=finalizedStatement.replaceAll(key, val);
			}
			finalizedSqlStatements.add(finalizedStatement);
		}
		
		// execute parameterized statements
		//log.error("### Executing "+finalizedSqlStatements.size()+" SQL statements");
		this.executeSqlTransaction(finalizedSqlStatements);
	}

}
