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

class UpdateUserPreferencesIntoDb extends SQLWriteStmt<IUserProfileData>   {

	List<IUserProfileData> _data = new ArrayList<>();
	public UpdateUserPreferencesIntoDb(List<IUserProfileData> terms, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);
		
	}
	@Override
	protected List<IUserProfileData> getDataList() { return _data; }
	
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			// insert if not present, update if already present
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"update users set nickname=?, guilanguage_id=?, guitheme_id=? where email=? and enabled=1;")); 
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		
		try {
			
			stmt.setString(1, dataObject.getNickname());
			stmt.setInt(2, dataObject.getGuiLanguageId());
			stmt.setInt(3, dataObject.getGuiThemeId());
			stmt.setString(4, dataObject.getName());
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
