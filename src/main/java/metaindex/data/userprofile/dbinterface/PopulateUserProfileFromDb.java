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
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.exceptions.DataProcessException;

class PopulateUserProfileFromDb extends SQLPopulateStmt<IUserProfileData>   {

	public static final String SQL_REQUEST = 
			"select users.user_id,users.email,users.password, "
			+"users.nickname,users.guilanguage_id,users.guitheme_id,users.category,role,users.lastLogin,users.lastUpdate,"
			+" user_roles.lastUpdate,users.enabled,"
			+" user_plans.plan_id, user_plans.startDate,user_plans.endDate,user_plans.nbQuotaWarnings,user_plans.lastUpdate"
			+" from users,user_roles,user_plans";

	private Boolean _onlyIfTimestampChanged=false;
	
	private List<IUserProfileData> _data;
	
	public PopulateUserProfileFromDb(List<IUserProfileData> d, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	public PopulateUserProfileFromDb(List<IUserProfileData> d, SQLDataConnector ds,Boolean onlyIfTimestampChanged) throws DataProcessException { 
		super(ds);
		_data=d;
		_onlyIfTimestampChanged=onlyIfTimestampChanged;
	}
	public PopulateUserProfileFromDb(SQLDataConnector ds) throws DataProcessException { 
		super(ds);
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
		
		Timestamp dbDateUsers = rs.getTimestamp(10);
		Timestamp dbDateRoles = rs.getTimestamp(11);
		Timestamp dbDatePlans = rs.getTimestamp(17);
		Timestamp newerDbDate = dbDateUsers;
		if (newerDbDate.before(dbDateRoles)) { newerDbDate=dbDateRoles; }
		if (newerDbDate.before(dbDatePlans)) { newerDbDate=dbDatePlans; }
		if (_onlyIfTimestampChanged==true) {
			if (!d.shallBeProcessed(newerDbDate)) { return d; } 
		}
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setEncryptedPassword(rs.getString(3));
		d.setNickname(rs.getString(4));
		d.setGuiLanguageId(rs.getInt(5));
		d.setGuiThemeId(rs.getInt(6));
		d.setCategory(CATEGORY.valueOf(rs.getString(7)));
		d.setRole(USER_ROLE.valueOf(rs.getString(8)));
		d.setLastLoginDate(rs.getTimestamp(9));
		d.setLastUpdate(newerDbDate);
		d.setEnabled(rs.getBoolean(12));
		
		d.setPlanId(rs.getInt(13));
		d.setPlanStartDate(rs.getDate(14));
		d.setPlanEndDate(rs.getDate(15));
		d.setPlanNbQuotaWarnings(rs.getInt(16));
		
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		sql += " where users.user_id=user_roles.user_id and users.user_id=user_plans.user_id ";
		if (_data.size()>0) {
			sql+=" and (";
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
