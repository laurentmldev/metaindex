package metaindex.data.filter.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLPopulateStmt;
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
	
	public SQLPopulateStmt<IFilter> getPopulateFilterFromDbStmt(List<ICatalog> c) throws DataProcessException {
		return new PopulateFilterStmt(c,getDatasource());
	}
	
	public SQLPopulateStmt<IFilter> getLoadFromDbStmt(ICatalog c) throws DataProcessException {
		List<ICatalog> list = new ArrayList<ICatalog>();
		list.add(c);
		return getPopulateFilterFromDbStmt(list);
	}

	public SQLPopulateStmt<IFilter> getLoadFromDbStmt(ICatalog c, List<IFilter> data) throws DataProcessException {
		return new PopulateFilterStmt(c, data, getDatasource());
	}

	public SQLPopulateStmt<IFilter> getLoadFromDbStmt(ICatalog c,IFilter data) throws DataProcessException {
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
