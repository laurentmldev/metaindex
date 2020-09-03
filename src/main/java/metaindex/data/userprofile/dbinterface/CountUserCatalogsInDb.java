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

import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.exceptions.DataProcessException;

class CountUserCatalogsInDb extends SQLPopulateStmt<IUserProfileData>   {

	public static final String SQL_REQUEST = 
			"select count(catalog_id) from catalogs ";

	
	private IUserProfileData _data;
	
	public CountUserCatalogsInDb(IUserProfileData d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);		
		_data=d;
	}

	@Override
	public IUserProfileData mapRow(ResultSet rs, int rowNum) throws SQLException {
		_data.setCurNbCatalogsCreated(rs.getInt(1));
		return _data;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		sql+=" where catalogs.owner_id="+_data.getId();
		return sql;
	}	
					
};
