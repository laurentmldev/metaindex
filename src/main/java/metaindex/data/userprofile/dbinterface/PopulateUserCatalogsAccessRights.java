package metaindex.data.userprofile.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.exceptions.DataProcessException;

class PopulateUserCatalogsAccessRights extends SQLPopulateStmt<IUserProfileData>   {

	public static final String SQL_REQUEST = 
			"select user_catalogs_rights_id,user_id,"
			+"catalog_id,access_rights, lastUpdate"							
			+" from user_catalogs_rights";
	
	
	private List<IUserProfileData> _data;
	private Boolean _onlyIfTimestampChanged = false;
	
	public PopulateUserCatalogsAccessRights(List<IUserProfileData> d, SQLDataConnector ds, Boolean onlyIfTimestampChanged) throws DataProcessException { 
		super(ds);
		_data=d;
		_onlyIfTimestampChanged=onlyIfTimestampChanged;
	}

	@Override
	public IUserProfileData mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Integer dbKey = rs.getInt(2);
		 
		IUserProfileData d = _data.stream()
			.filter(p -> p.getId().equals(dbKey))
			.findFirst()
			.orElse(null);
			
		if (_onlyIfTimestampChanged==true) {
			Timestamp dbDate = rs.getTimestamp(5);
			if (!d.shallBeProcessed(dbDate)) { return d; } 
		}
		Integer catalogId=rs.getInt(3);
		String accessRightsStr=rs.getString(4);
		USER_CATALOG_ACCESSRIGHTS accessRights = USER_CATALOG_ACCESSRIGHTS.valueOf(accessRightsStr);
		d.setUserCatalogAccessRights(catalogId, accessRights);
		d.setLastUpdateIfNewer(rs.getTimestamp(5));
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST+" where ";
		Iterator<IUserProfileData> it = _data.iterator();
		Boolean firstOne=true;
		while (it.hasNext()) {
			if (!firstOne) { sql+=" or "; }
			sql += "user_id='"+it.next().getId()+"'";
			firstOne=false;
		}
		return sql;
	}
					
};
