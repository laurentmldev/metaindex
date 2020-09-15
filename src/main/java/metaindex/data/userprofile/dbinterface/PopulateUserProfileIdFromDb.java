package metaindex.data.userprofile.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.exceptions.DataProcessException;

class PopulateUserProfileIdFromDb extends SQLPopulateStmt<IUserProfileData>   {

	private static final String SQL_REQUEST = 
			"select user_id,email from users";
	
	private List<IUserProfileData> _data;
	
	public PopulateUserProfileIdFromDb(List<IUserProfileData> d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	
	@Override
	public IUserProfileData mapRow(ResultSet rs, int rowNum) throws SQLException {
		IUserProfileData d;
		String dbKey = rs.getString(2);
		d = _data.stream()
				.filter(p -> p.getName().equals(dbKey))
				.findFirst()
				.orElse(null);
		
		if (d==null) { 
			d =new UserProfileData();
			_data.add(d);
		}
		
		d.setId(rs.getInt(1));
		
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		
		if (_data.size()>0) {
			sql+=" where (";
			Iterator<IUserProfileData> it = _data.iterator();
			Boolean firstOne=true;
			while (it.hasNext()) {
				if (!firstOne) { sql+=" or "; }
				sql += "users.email='"+it.next().getName()+"'";
				firstOne=false;
			}
			sql+=" )";
		}
		return sql;
	}	
					
};
