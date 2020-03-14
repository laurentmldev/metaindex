package metaindex.data.commons.globals.guitheme.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

public class DbInterface extends SQLDatabaseInterface<IGuiTheme> {

	public DbInterface(SQLDataSource ds) {
		super(ds);
	}
	
	
	public SQLReadStreamStmt<IGuiTheme> getLoadFromDbStmt() throws DataProcessException {
		return new LoadFromDbStmt(getDatasource());
	}
	
}
