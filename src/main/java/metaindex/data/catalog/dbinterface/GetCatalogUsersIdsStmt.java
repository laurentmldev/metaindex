package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.ICatalogUser;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

class GetCatalogUsersIdsStmt extends SQLReadStreamStmt<Integer>   {

	private Log log = LogFactory.getLog(GetCatalogUsersIdsStmt.class);
	
	public static final String SQL_REQUEST = 
			"select DISTINCT users.user_id "
			+" from users,user_roles,user_catalogs_rights"
			+" where user_roles.role='ROLE_ADMIN' or (user_roles.role='ROLE_USER'  and users.user_id=user_roles.user_id and users.user_id=user_catalogs_rights.user_id)";
	
	ICatalog _data;
	
	
	public GetCatalogUsersIdsStmt(ICatalog d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	
	
	@Override
	public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
		Integer userId = rs.getInt(1);
		return userId; 		
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST+" and user_catalogs_rights.catalog_id="+_data.getId();		
		return sql;
	}
					
};
