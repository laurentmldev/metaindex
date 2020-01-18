package toolbox.database.sql;

import toolbox.database.IDatabaseInterface;

public class SQLDatabaseInterface<T> implements IDatabaseInterface<SQLDataSource> {

	private SQLDataSource _ds;

	public SQLDatabaseInterface(SQLDataSource ds) { _ds=ds; }
	
	@Override
	public SQLDataSource getDatasource() { return _ds; }

	
}
