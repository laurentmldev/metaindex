package metaindex.data.commons.globals.guilanguage.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

public class DbInterface extends SQLDatabaseInterface<IGuiLanguage> {

	public DbInterface(SQLDataConnector ds) {
		super(ds);
	}

	public SQLReadStreamStmt<IGuiLanguage> getLoadFromDbStmt() throws DataProcessException {
		return new LoadFromDbStmt(getDataConnector());
	}
	
	public SQLReadStreamStmt<IGuiLanguage> getLoadFromDbStmt(IGuiLanguage data) throws DataProcessException {
		List<IGuiLanguage> list = new ArrayList<IGuiLanguage>();
		list.add(data);
		return getLoadFromDbStmt(list);
	}
	
	public SQLReadStreamStmt<IGuiLanguage> getLoadFromDbStmt(List<IGuiLanguage> data) throws DataProcessException {
		return new LoadFromDbStmt(getDataConnector());
	}
	
}
