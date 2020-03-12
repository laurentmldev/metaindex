package metaindex.data.userprofile.dbinterface;

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
import metaindex.data.userprofile.IUserProfileData;

public class DbInterface  extends SQLDatabaseInterface<IUserProfileData> 
{
	
	public DbInterface(SQLDataSource ds) { super(ds); }
	
	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new PopulateUserProfileFromDb(data, getDatasource());
	}

	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileFromDbStmt(List<IUserProfileData> data,Boolean onlyIfContentsUpdated) throws DataProcessException {
		return new PopulateUserProfileFromDb(data, getDatasource(),onlyIfContentsUpdated);
	}

	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getPopulateUserProfileFromDbStmt(list);
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
	public SQLPopulateStmt<IUserProfileData> getPopulateAccessRightsFromDbStmt(List<IUserProfileData> data,Boolean onlyIfRequired) throws DataProcessException {
		return new PopulateUserCatalogsAccessRights(data,getDatasource(),onlyIfRequired);
	}
	public SQLPopulateStmt<IUserProfileData> getPopulateAccessRightsFromDbStmt(IUserProfileData data,Boolean onlyIfRequired) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getPopulateAccessRightsFromDbStmt(list,onlyIfRequired);
	}
	public SQLPopulateStmt<IUserProfileData> getPopulateAccessRightsFromDbStmt(IUserProfileData data) throws DataProcessException {
		return getPopulateAccessRightsFromDbStmt(data,false);
	}
	public SQLWriteStmt<IUserProfileData> getSetUserAccessRightsIntoDbStmt(IUserProfileData data, ICatalog catalog) throws DataProcessException {
		return new CreateOrUpdateUserCatalogsAccessRights(data,catalog,getDatasource());
	}
	
	// Catalogs Customization
	public SQLPopulateStmt<IUserProfileData> getPopulateCatalogCustomizationFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UserCatalogsCustomizationLoad(data,getDatasource());
	}
	public SQLPopulateStmt<IUserProfileData> getPopulateCatalogCustomizationFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getPopulateCatalogCustomizationFromDbStmt(list);
	}
	public SQLWriteStmt<IUserProfileData> getSetUserCatalogCustomizationIntoDbStmt(IUserProfileData data, ICatalog catalog) throws DataProcessException {
		return new CreateOrUpdateUserCatalogsCustomization(data,catalog,getDatasource());
	}
	
}
