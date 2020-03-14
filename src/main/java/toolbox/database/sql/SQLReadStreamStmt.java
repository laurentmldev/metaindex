package toolbox.database.sql;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import toolbox.database.IDatabaseReadStreamStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public abstract class SQLReadStreamStmt<TData> implements IDatabaseReadStreamStmt<TData>, 
													IDatasourcedStmt<SQLDataSource>, 
													RowMapper<TData> {

	private SQLDataSource _datasource;
	private final JdbcTemplate jdbcTemplate;
	
	public SQLReadStreamStmt(SQLDataSource ds) throws DataProcessException {		
		_datasource=ds; 
		try {
			  jdbcTemplate = new JdbcTemplate(_datasource.getSqlDataSource());
		} catch (Exception e) { throw new DataProcessException(e); }
	}
	
	protected abstract String buildSqlQuery() throws DataProcessException;	
	
	@Override
	public void execute(IStreamHandler<TData> h) throws DataProcessException {
		try {
			List<TData> result = jdbcTemplate.query(buildSqlQuery(), this);	
			h.handle(result);			
		} catch(Exception e) {
			String clientInfo = "???";
			try { clientInfo=_datasource.getConnection().getClientInfo().toString(); } catch (Exception ex) {}
			throw new DataProcessException("unable to run SQL statement over datasource "+clientInfo+" : "+e.getMessage(),e); 
		}
	}
	
	@Override
	public SQLDataSource getDatasource() {
		return _datasource;
	}
	
}
