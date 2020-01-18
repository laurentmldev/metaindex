package metaindex.data.commons.globals.guitheme.dbinterface;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

public class DbInterface extends SQLDatabaseInterface<IGuiTheme> {

	public DbInterface(SQLDataSource ds) {
		super(ds);
	}

	public SQLReadStmt<IGuiTheme> getLoadFromDbStmt() throws DataProcessException {
		return new LoadFromDbStmt(getDatasource());
	}
	
	public SQLReadStmt<IGuiTheme> getLoadFromDbStmt(IGuiTheme data) throws DataProcessException {
		List<IGuiTheme> list = new ArrayList<IGuiTheme>();
		list.add(data);
		return getLoadFromDbStmt(list);
	}
	
	public SQLReadStmt<IGuiTheme> getLoadFromDbStmt(List<IGuiTheme> data) throws DataProcessException {
		return new LoadFromDbStmt(getDatasource());
	}
	
}
