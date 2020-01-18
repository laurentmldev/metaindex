package toolbox.database.elasticsearch;


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
