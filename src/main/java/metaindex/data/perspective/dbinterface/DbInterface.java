package metaindex.data.perspective.dbinterface;

import toolbox.database.elasticsearch.ESPopulateStmt;

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

import metaindex.data.catalog.ICatalog;
import metaindex.data.perspective.ICatalogPerspective;
import metaindex.data.userprofile.IUserProfileData;

public class DbInterface  extends SQLDatabaseInterface<ICatalogPerspective> 
{
	public DbInterface(SQLDataSource ds) { 
		super(ds);		
	}

	// ---
	public SQLPopulateStmt<ICatalogPerspective> getLoadFromDbStmt(List<ICatalog> list) throws DataProcessException {
		return new PopulateCatalogPerspectiveFromDbStmt(list, getDatasource());
	}
	public SQLPopulateStmt<ICatalogPerspective> getLoadFromDbStmt(ICatalog c) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(c);
		return new PopulateCatalogPerspectiveFromDbStmt(list, getDatasource());
	}
	

	public SQLWriteStmt<String> getUpdatePerspectiveIntoDbStmt(IUserProfileData activeUser,ICatalog c, List<String> perspectivesJsonData) throws DataProcessException {
		return new CreateOrUpdatePerspectiveIntoSqlDbStmt(activeUser,c,perspectivesJsonData, getDatasource());
	}
	
	public SQLWriteStmt<String> getUpdatePerspectiveIntoDbStmt(IUserProfileData activeUser,ICatalog c, String perspectiveJsonData) throws DataProcessException {
		List<String> list = new ArrayList<>();
		list.add(perspectiveJsonData);
		return getUpdatePerspectiveIntoDbStmt(activeUser,c,list);
	}
	
	public SQLWriteStmt<Integer> getDeletePerspectiveFromDbStmt(IUserProfileData activeUser,ICatalog c, List<Integer> perspectivesIds) throws DataProcessException {		
		return new DeletePerspectiveSqlDbStmt(activeUser,c,perspectivesIds,getDatasource());
	}
	
	public SQLWriteStmt<Integer> getDeletePerspectiveFromDbStmt(IUserProfileData activeUser,ICatalog c, Integer perspectiveId) throws DataProcessException {
		List<Integer> list = new ArrayList<>();
		list.add(perspectiveId);
		return getDeletePerspectiveFromDbStmt(activeUser,c,list);
	}
	
}
