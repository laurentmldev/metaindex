package toolbox.database.elasticsearch;

import toolbox.database.IDatabasePopulateStmt;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseReadStreamStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.database.sql.SQLDataSource;
import toolbox.exceptions.DataProcessException;

public abstract class ESPopulateStmt<TData> implements IDatabasePopulateStmt,IDatasourcedStmt<ESDataSource> 
 {

	private ESDataSource _datasource;
	
	public ESPopulateStmt(ESDataSource ds) throws DataProcessException {		
		_datasource=ds;
	}
	
		
	@Override
	public ESDataSource getDatasource() {
		return _datasource;
	}
	
	
	
}
