package metaindex.data.userprofile.dbinterface;

/*
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
import metaindex.data.commons.globals.plans.IPlan;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;

public class SqlDbInterface  extends SQLDatabaseInterface<IUserProfileData> 
{
	
	public SqlDbInterface(SQLDataConnector ds) { super(ds); }
	
	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new PopulateUserProfileFromDb(data, getDataConnector());
	}

	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileFromDbStmt(List<IUserProfileData> data,Boolean onlyIfContentsUpdated) throws DataProcessException {
		return new PopulateUserProfileFromDb(data, getDataConnector(),onlyIfContentsUpdated);
	}

	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getPopulateUserProfileFromDbStmt(list);
	}
		
	public SQLPopulateStmt<IUserProfileData> getPopulateUserProfileIdFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return new PopulateUserProfileIdFromDb(list, getDataConnector());
	}

	public SQLWriteStmt<IUserProfileData> getUpdatePreferencesIntoDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UpdateUserPreferencesIntoDb(data, getDataConnector());
	}
	
	public SQLWriteStmt<IUserProfileData> getUpdatePreferencesIntoDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<>();
		list.add(data);
		return getUpdatePreferencesIntoDbStmt(list);
	}

	public SQLWriteStmt<IUserProfileData> getUpdatePlanIntoDbStmt(IUserProfileData data) throws DataProcessException {
		return new CreateOrUpdateUserPlan(data,data.getPlan(),getDataConnector());
	}
	public SQLWriteStmt<IUserProfileData> getUpdatePasswordIntoDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UpdateUserPasswordIntoDb(data, getDataConnector());
	}
	public SQLWriteStmt<IUserProfileData> getUpdatePassswordIntoDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<>();
		list.add(data);
		return getUpdatePasswordIntoDbStmt(list);
	}
	public SQLWriteStmt<IUserProfileData> getCreateUserIntoSqlDbStmt(IUserProfileData data) throws DataProcessException {
		return new CreateSqlUser(data,getDataConnector());
	}
	public SQLWriteStmt<IUserProfileData> getCreateorUpdateUserRoleIntoSqlDbStmt(IUserProfileData data, USER_ROLE role) throws DataProcessException {
		return new CreateOrUpdateUserRole(data,role,getDataConnector());
	}
	public SQLPopulateStmt<IUserProfileData> getCountUserCatalogsInDbStmt(IUserProfileData data) throws DataProcessException {
		return new CountUserCatalogsInDb(data,getDataConnector());
	}
	
	// Access Rights
	public SQLPopulateStmt<IUserProfileData> getPopulateAccessRightsFromDbStmt(List<IUserProfileData> data,Boolean onlyIfRequired) throws DataProcessException {
		return new PopulateUserCatalogsAccessRights(data,getDataConnector(),onlyIfRequired);
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
		return new CreateOrUpdateUserCatalogsAccessRights(data,catalog,getDataConnector());
	}
	
	// Catalogs Customization
	public SQLPopulateStmt<IUserProfileData> getPopulateCatalogCustomizationFromDbStmt(List<IUserProfileData> data) throws DataProcessException {
		return new UserCatalogsCustomizationLoad(data,getDataConnector());
	}
	public SQLPopulateStmt<IUserProfileData> getPopulateCatalogCustomizationFromDbStmt(IUserProfileData data) throws DataProcessException {
		List<IUserProfileData> list = new ArrayList<IUserProfileData>();
		list.add(data);
		return getPopulateCatalogCustomizationFromDbStmt(list);
	}
	public SQLWriteStmt<IUserProfileData> getSetUserCatalogCustomizationIntoDbStmt(IUserProfileData data, ICatalog catalog) throws DataProcessException {
		return new CreateOrUpdateUserCatalogsCustomization(data,catalog,getDataConnector());
	}
	
}
