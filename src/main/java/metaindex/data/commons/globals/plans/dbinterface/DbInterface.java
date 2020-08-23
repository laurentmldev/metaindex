package metaindex.data.commons.globals.plans.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import metaindex.data.commons.globals.plans.IPlan;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

public class DbInterface extends SQLDatabaseInterface<IPlan> {

	public DbInterface(SQLDataConnector ds) {
		super(ds);
	}
	
	
	public SQLReadStreamStmt<IPlan> getLoadFromDbStmt() throws DataProcessException {
		return new LoadFromDbStmt(getDataConnector());
	}
	
}
