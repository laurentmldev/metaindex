package metaindex.data.catalog.dbinterface;

/**
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;

public class SQLDbInterface  extends SQLDatabaseInterface<ICatalog> 
{
	
	public SQLDbInterface(SQLDataConnector ds) { super(ds); }
	
	
	public SQLReadStreamStmt<Integer> getCatalogUsersIdsStmt(ICatalog data) throws DataProcessException {	
		return new GetCatalogUsersIdsStmt(data,getDataConnector());
	}
	public SQLPopulateStmt<ICatalog> getPopulateFromDefDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> l = new ArrayList<>();
		l.add(data);
		return getPopulateFromDefDbStmt(l);
	}
	public SQLPopulateStmt<ICatalog> getPopulateFromDefDbStmt(List<ICatalog> data) throws DataProcessException {
		return new PopulateCatalogFromDbStmt(data, getDataConnector());
	}
	public SQLPopulateStmt<ICatalog> getPopulateFromDefDbStmt(List<ICatalog> data, Boolean onlyIfTimestampChanged) throws DataProcessException {
		return new PopulateCatalogFromDbStmt(data, getDataConnector(),onlyIfTimestampChanged);
	}
	
	// --- set access rights
	public SQLWriteStmt<IUserProfileData> getSetCatalogUserAccessStmt(ICatalog c, IUserProfileData user, USER_CATALOG_ACCESSRIGHTS accessRights) throws DataProcessException {
		List<IUserProfileData> users=new ArrayList<>();
		users.add(user);
		return getSetCatalogUsersAccessStmt(c,users,accessRights);
	}
	public SQLWriteStmt<IUserProfileData> getSetCatalogUsersAccessStmt(ICatalog c, List<IUserProfileData> users, USER_CATALOG_ACCESSRIGHTS accessRights) throws DataProcessException {
		return new CreateOrUpdateCatalogUsersAccessStmt(c,users,accessRights,getDataConnector());
	}
	// --- create catalog
	public SQLWriteStmt<ICatalogCustomParams> getCreateIntoDefDbStmt(IUserProfileData activeUser,List<ICatalogCustomParams> data) throws DataProcessException {
		return new CreateOrUpdateCatalogIntoSqlDbStmt(activeUser,data, getDataConnector());
	}
	public SQLWriteStmt<ICatalogCustomParams> getCreateIntoDefDbStmt(IUserProfileData activeUser,ICatalog data) throws DataProcessException {
		List<ICatalogCustomParams> list = new ArrayList<>();
		list.add(data);
		return getCreateIntoDefDbStmt(activeUser,list);
	}
	
	// --- delete catalog
	public SQLWriteStmt<ICatalog> getDeleteFromDefDbStmt(IUserProfileData activeUser,List<ICatalog> data) throws DataProcessException {
		return new DeleteCatalogFromSqlDbStmt(activeUser,data, getDataConnector());
	}
	public SQLWriteStmt<ICatalog> getDeleteFromDefDbStmt(IUserProfileData activeUser,ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getDeleteFromDefDbStmt(activeUser,list);
	}
	
	// --- update catalog
	public SQLWriteStmt<ICatalogCustomParams> getUpdateIntoDefDbStmt(IUserProfileData activeUser,List<ICatalogCustomParams> data) throws DataProcessException {
		for (ICatalogCustomParams c : data) {
			if (c.getId().equals(0)) { throw new DataProcessException("While updating catalog '"+c.getName()+"' : no primary key defined."); }			
		}
		return new CreateOrUpdateCatalogIntoSqlDbStmt(activeUser,data, getDataConnector());
	}
	public SQLWriteStmt<ICatalogCustomParams> getUpdateIntoDefDbStmt(IUserProfileData activeUser,ICatalogCustomParams data) throws DataProcessException {
		List<ICatalogCustomParams> list = new ArrayList<>();
		list.add(data);
		return getUpdateIntoDefDbStmt(activeUser,list);
	}
	
}
