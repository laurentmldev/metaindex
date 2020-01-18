package metaindex.data.filter.dbinterface;

import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;

public class DbInterface  extends SQLDatabaseInterface<IFilter> 
{
	
	public DbInterface(SQLDataSource ds) { super(ds); }
	
	public SQLReadStmt<IFilter> getLoadFromDbStmt(List<ICatalog> c) throws DataProcessException {
		return new FilterLoadStmt(c,getDatasource());
	}
	
	public SQLReadStmt<IFilter> getLoadFromDbStmt(ICatalog c) throws DataProcessException {
		List<ICatalog> list = new ArrayList<ICatalog>();
		list.add(c);
		return getLoadFromDbStmt(list);
	}

	public SQLReadStmt<IFilter> getLoadFromDbStmt(ICatalog c, List<IFilter> data) throws DataProcessException {
		return new FilterLoadStmt(c, data, getDatasource());
	}

	public SQLReadStmt<IFilter> getLoadFromDbStmt(ICatalog c,IFilter data) throws DataProcessException {
		List<IFilter> list = new ArrayList<IFilter>();
		list.add(data);
		return getLoadFromDbStmt(c,list);
	}
	
	public SQLWriteStmt<IFilter> createIntoDbStmt(ICatalog c,IFilter data) throws DataProcessException {
		return new CreateFilterIntoDbStmt(c,data, getDatasource());
	}
	
	public SQLWriteStmt<IFilter> updateIntoDbStmt(ICatalog c,IFilter data) throws DataProcessException {
		return new UpdateFilterIntoDbStmt(c,data, getDatasource());
	}
	
	public SQLWriteStmt<IFilter> deleteFromDbStmt(ICatalog c,IFilter data) throws DataProcessException {
		return new DeleteFilterFromDbStmt(c,data, getDatasource());
	}

}
