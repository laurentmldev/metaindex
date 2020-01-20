package toolbox.database.sql;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseInterface;

public class SQLDatabaseInterface<T> implements IDatabaseInterface<SQLDataSource> {

	private SQLDataSource _ds;

	public SQLDatabaseInterface(SQLDataSource ds) { _ds=ds; }
	
	@Override
	public SQLDataSource getDatasource() { return _ds; }

	
}
