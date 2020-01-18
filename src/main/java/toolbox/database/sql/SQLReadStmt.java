package toolbox.database.sql;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import toolbox.database.IDataSource;
import toolbox.database.IDatabaseReadStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.exceptions.DataProcessException;

public abstract class SQLReadStmt<TData> implements IDatabaseReadStmt<TData>, 
													IDatasourcedStmt<SQLDataSource>, 
													RowMapper<TData> {

	private SQLDataSource _datasource;
	private final JdbcTemplate jdbcTemplate;
	
	public SQLReadStmt(SQLDataSource ds) throws DataProcessException {		
		_datasource=ds; 
		try {
			  jdbcTemplate = new JdbcTemplate(_datasource.getSqlDataSource());
		} catch (Exception e) { throw new DataProcessException(e); }
	}
	
	protected abstract String buildSqlQuery() throws DataProcessException;	
	
	@Override
	public List<TData> execute() throws DataProcessException {
		try {
			List<TData> result = jdbcTemplate.query(buildSqlQuery(), this);			
			return result;
		} catch(Exception e) {
			String clientInfo = "???";
			try { clientInfo=_datasource.getConnection().getClientInfo().toString(); } catch (Exception ex) {}
			throw new DataProcessException("unable to run SQL statement over datasource "+clientInfo,e); 
		}
	}
	
	@Override
	public SQLDataSource getDatasource() {
		return _datasource;
	}
	
}
