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
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class UpdateUserLastLoginDateIntoDb extends SQLWriteStmt<IUserProfileData>   {

	List<IUserProfileData> _data = new ArrayList<>();
	public UpdateUserLastLoginDateIntoDb(List<IUserProfileData> users, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data.addAll(users);
		
	}
	@Override
	protected List<IUserProfileData> getDataList() { return _data; }
	
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			// insert if not present, update if already present
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"update users set lastLogin=? where email=?;")); 
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		
		try {
			
			stmt.setTimestamp(1, new java.sql.Timestamp(dataObject.getLastLoginDate().getTime()));
			stmt.setString(2, dataObject.getName());
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
