package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseReadStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.database.sql.SQLDataSource;
import toolbox.exceptions.DataProcessException;

public abstract class ESReadStmt<TData> implements IDatabaseReadStmt<TData>,IDatasourcedStmt<ESDataSource> 
 {

	private ESDataSource _datasource;
	
	public ESReadStmt(ESDataSource ds) throws DataProcessException {		
		_datasource=ds;
	}
	
		
	@Override
	public ESDataSource getDatasource() {
		return _datasource;
	}
	
	
	
}
