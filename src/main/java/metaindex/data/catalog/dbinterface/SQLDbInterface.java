package metaindex.data.catalog.dbinterface;

/**
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.perspective.dbinterface.CreateOrUpdatePerspectiveIntoSqlDbStmt;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;

public class SQLDbInterface  extends SQLDatabaseInterface<ICatalog> 
{
	
	public SQLDbInterface(SQLDataSource ds) { super(ds); }
	
	
	public SQLReadStmt<ICatalog> getLoadFromDbStmt() throws DataProcessException {
		return new LoadContentsFromDbStmt(getDatasource());
	}

	public SQLReadStmt<ICatalog> getLoadFromDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadContentsFromDbStmt(data, getDatasource());
	}
	
	// --- create catalog
	public SQLWriteStmt<ICatalogCustomParams> getCreateIntoDbStmt(IUserProfileData activeUser,List<ICatalogCustomParams> data) throws DataProcessException {
		return new CreateOrUpdateCatalogIntoSqlDbStmt(activeUser,data, getDatasource());
	}
	public SQLWriteStmt<ICatalogCustomParams> getCreateIntoDbStmt(IUserProfileData activeUser,ICatalog data) throws DataProcessException {
		List<ICatalogCustomParams> list = new ArrayList<>();
		list.add(data);
		return getCreateIntoDbStmt(activeUser,list);
	}
	
	// --- delete catalog
	public SQLWriteStmt<ICatalog> getDeleteFromDbStmt(IUserProfileData activeUser,List<ICatalog> data) throws DataProcessException {
		return new DeleteCatalogFromSqlDbStmt(activeUser,data, getDatasource());
	}
	public SQLWriteStmt<ICatalog> getDeleteFromDbStmt(IUserProfileData activeUser,ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getDeleteFromDbStmt(activeUser,list);
	}
	
	// --- update catalog
	public SQLWriteStmt<ICatalogCustomParams> getUpdateIntoDbStmt(IUserProfileData activeUser,List<ICatalogCustomParams> data) throws DataProcessException {
		for (ICatalogCustomParams c : data) {
			if (c.getId().equals(0)) { throw new DataProcessException("While updating catalog '"+c.getName()+"' : no primary key defined."); }
		}
		return new CreateOrUpdateCatalogIntoSqlDbStmt(activeUser,data, getDatasource());
	}
	public SQLWriteStmt<ICatalogCustomParams> getUpdateIntoDbStmt(IUserProfileData activeUser,ICatalogCustomParams data) throws DataProcessException {
		List<ICatalogCustomParams> list = new ArrayList<>();
		list.add(data);
		return getUpdateIntoDbStmt(activeUser,list);
	}
	
}
