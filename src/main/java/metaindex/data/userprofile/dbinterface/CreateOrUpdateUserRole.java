package metaindex.data.userprofile.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateUserRole extends SQLWriteStmt<IUserProfileData>   {
	
	List<IUserProfileData> _users = new ArrayList<>();
	USER_ROLE _role = null;
	
	public CreateOrUpdateUserRole(IUserProfileData u, USER_ROLE r, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_users.add(u);
		_role=r;
	}
	
@Override
protected List<IUserProfileData> getDataList() { return _users; }

@Override
protected List<PreparedStatement> prepareStmts() throws DataProcessException {

List<PreparedStatement> result = new ArrayList<PreparedStatement>();

try {
	result.add(this.getDataConnector().getConnection().prepareStatement(
			"insert into user_roles (user_id,role) values (?,?) "
					+"ON DUPLICATE KEY UPDATE role=?"));
	
} catch (SQLException e) { throw new DataProcessException(e); }

return result;
}
@Override
protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {

PreparedStatement stmt = stmts.get(0);
try {
	stmt.setInt(1, dataObject.getId());
	stmt.setString(2, _role.toString());
	stmt.setString(3, _role.toString());
	stmt.addBatch();
} catch (SQLException e) { throw new DataProcessException(e); }		
}
					
};
