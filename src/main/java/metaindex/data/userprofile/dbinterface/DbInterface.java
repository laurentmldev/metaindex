package metaindex.data.userprofile.dbinterface;

/*
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

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

public class DbInterface  extends SQLDatabaseInterface<IUserProfileData> 
{
	
	public DbInterface(SQLDataSource ds) { super(ds); }
	
	public SQLReadStmt<IUserProfileData> getLoadFromDbStmt() throws DataProcessException {
		return new UserProfileLoad(getDatasource());
	}

	public SQLReadStmt<IUserProfileData> getLoadFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UserProfileLoad(data, getDatasource());
	}

	public SQLReadStmt<IUserProfileData> getLoadFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getLoadFromDbStmt(list);
	}

	public SQLWriteStmt<IUserProfileData> getUpdateIntoDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UpdateUserPreferencesIntoDb(data, getDatasource());
	}
	
	public SQLWriteStmt<IUserProfileData> getUpdateIntoDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<>();
		list.add(data);
		return getUpdateIntoDbStmt(list);
	}
	
	// Access Rights
	public SQLReadStmt<IUserProfileData> getLoadAccessRightsFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UserCatalogsAccessRightsLoad(data,getDatasource());
	}
	public SQLReadStmt<IUserProfileData> getLoadAccessRightsFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getLoadAccessRightsFromDbStmt(list);
	}
	public SQLWriteStmt<IUserProfileData> getSetUserAccessRightsIntoDbStmt(IUserProfileData data, ICatalog catalog) throws DataProcessException {
		return new CreateOrUpdateUserCatalogsAccessRights(data,catalog,getDatasource());
	}
	
	// Catalogs Customization
	public SQLReadStmt<IUserProfileData> getLoadCatalogCustomizationFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UserCatalogsCustomizationLoad(data,getDatasource());
	}
	public SQLReadStmt<IUserProfileData> getLoadCatalogCustomizationFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getLoadCatalogCustomizationFromDbStmt(list);
	}
	public SQLWriteStmt<IUserProfileData> getSetUserCatalogCustomizationIntoDbStmt(IUserProfileData data, ICatalog catalog) throws DataProcessException {
		return new CreateOrUpdateUserCatalogsCustomization(data,catalog,getDatasource());
	}
	
}
