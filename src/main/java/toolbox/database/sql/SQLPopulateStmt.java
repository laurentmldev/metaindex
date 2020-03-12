package toolbox.database.sql;

import java.util.ArrayList;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


import toolbox.database.IDatabasePopulateStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.exceptions.DataProcessException;

public abstract class SQLPopulateStmt<TData> implements IDatabasePopulateStmt, 
													IDatasourcedStmt<SQLDataSource>, 
													RowMapper<TData> {

	private SQLDataSource _datasource;
	private final JdbcTemplate jdbcTemplate;
	
	public SQLPopulateStmt(SQLDataSource ds) throws DataProcessException {		
		_datasource=ds; 
		try {
			  jdbcTemplate = new JdbcTemplate(_datasource.getSqlDataSource());
		} catch (Exception e) { throw new DataProcessException(e); }
	}
	
	protected abstract String buildSqlQuery() throws DataProcessException;	
	
	@Override
	public void execute() throws DataProcessException {
		try {
			jdbcTemplate.query(buildSqlQuery(), this);							
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
