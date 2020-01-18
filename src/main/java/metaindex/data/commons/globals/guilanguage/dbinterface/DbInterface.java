package metaindex.data.commons.globals.guilanguage.dbinterface;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

public class DbInterface extends SQLDatabaseInterface<IGuiLanguage> {

	public DbInterface(SQLDataSource ds) {
		super(ds);
	}

	public SQLReadStmt<IGuiLanguage> getLoadFromDbStmt() throws DataProcessException {
		return new LoadFromDbStmt(getDatasource());
	}
	
	public SQLReadStmt<IGuiLanguage> getLoadFromDbStmt(IGuiLanguage data) throws DataProcessException {
		List<IGuiLanguage> list = new ArrayList<IGuiLanguage>();
		list.add(data);
		return getLoadFromDbStmt(list);
	}
	
	public SQLReadStmt<IGuiLanguage> getLoadFromDbStmt(List<IGuiLanguage> data) throws DataProcessException {
		return new LoadFromDbStmt(getDatasource());
	}
	
}
