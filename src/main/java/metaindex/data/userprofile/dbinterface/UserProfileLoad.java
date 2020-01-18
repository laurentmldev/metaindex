package metaindex.data.userprofile.dbinterface;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLReadStmt;
import toolbox.exceptions.DataProcessException;

class UserProfileLoad extends SQLReadStmt<IUserProfileData>   {

	public static final String SQL_REQUEST = 
			"select users.user_id,users.email,users.password, "
			+"users.nickname,users.guilanguage_id,users.guitheme_id, role"							
			+" from users,user_roles";

	List<IUserProfileData> _data;
	public UserProfileLoad(List<IUserProfileData> d, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data=d;
	}
	public UserProfileLoad(SQLDataSource ds) throws DataProcessException { 
		super(ds);
	}
	@Override
	public IUserProfileData mapRow(ResultSet rs, int rowNum) throws SQLException {
		IUserProfileData d;
		String dbKey = rs.getString(2);
		if (_data==null) { d =new UserProfileData(); }
		else { 
			d = _data.stream()
				.filter(p -> p.getName().equals(dbKey))
				.findFirst()
				.orElse(null);
		}
		d.setId(rs.getInt(1));
		d.setName(rs.getString(2));
		d.setEncryptedPassword(rs.getString(3));
		d.setNickname(rs.getString(4));
		d.setGuiLanguageId(rs.getInt(5));
		d.setGuiThemeId(rs.getInt(6));
		d.setRole(USER_ROLE.valueOf(rs.getString(7)));
		return d;
	}

	@Override
	public String buildSqlQuery()  {				
		String sql = SQL_REQUEST;
		sql += " where users.user_id=user_roles.user_id ";
		if (_data!=null && _data.size()>0) {
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
