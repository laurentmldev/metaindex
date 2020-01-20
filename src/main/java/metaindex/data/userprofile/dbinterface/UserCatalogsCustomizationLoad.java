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
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLReadStmt;
import toolbox.exceptions.DataProcessException;

class UserCatalogsCustomizationLoad extends SQLReadStmt<IUserProfileData>   {

	public static final String SQL_REQUEST = 
			"select user_catalogs_customization_id,user_id,"
			+"catalog_id,kibana_iframe"							
			+" from user_catalogs_customization";
	
	List<IUserProfileData> _data;
	public UserCatalogsCustomizationLoad(List<IUserProfileData> d, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}

	@Override
	public IUserProfileData mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Integer dbKey = rs.getInt(2);
		 
		IUserProfileData d = _data.stream()
			.filter(p -> p.getId().equals(dbKey))
			.findFirst()
			.orElse(null);
			
		Integer catalogId=rs.getInt(3);
		String kibanaIFrameStr=rs.getString(4);
		d.setUserCatalogKibanaIFrameHtml(catalogId,kibanaIFrameStr);
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
