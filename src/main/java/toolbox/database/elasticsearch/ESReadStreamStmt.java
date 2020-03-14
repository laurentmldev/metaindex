package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseReadStreamStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.exceptions.DataProcessException;

public abstract class ESReadStreamStmt<TData> implements IDatabaseReadStreamStmt<TData>,IDatasourcedStmt<ESDataSource> 
 {

	private ESDataSource _datasource;
	
	public ESReadStreamStmt(ESDataSource ds) throws DataProcessException {		
		_datasource=ds;
	}
	
		
	@Override
	public ESDataSource getDatasource() {
		return _datasource;
	}
	
	
	
}
