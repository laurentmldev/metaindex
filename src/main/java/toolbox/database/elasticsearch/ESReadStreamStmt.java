package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseReadStreamStmt;
import toolbox.database.IDataStmt;
import toolbox.exceptions.DataProcessException;

public abstract class ESReadStreamStmt<TData> implements IDatabaseReadStreamStmt<TData>,IDataStmt<ElasticSearchConnector> 
 {

	private ElasticSearchConnector _datasource;
	
	public ESReadStreamStmt(ElasticSearchConnector ds) throws DataProcessException {		
		_datasource=ds;
	}
	
		
	@Override
	public ElasticSearchConnector getDataConnector() {
		return _datasource;
	}
	
	
	
}
