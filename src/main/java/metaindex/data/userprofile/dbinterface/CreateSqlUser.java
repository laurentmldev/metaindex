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
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import metaindex.data.commons.globals.plans.IPlansManager;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataConnector;

import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;
class CreateSqlUser extends SQLWriteStmt<IUserProfileData>   {
	
	private List<IUserProfileData> _users = new ArrayList<>();
	
	public CreateSqlUser(IUserProfileData u, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_users.add(u);
	}
		
	@Override
	protected List<IUserProfileData> getDataList() { return _users; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
	
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"insert into users "
								+"(email,nickname,enabled,guilanguage_id) "
								+"values (?,?,?,?) "
							));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setString(1, dataObject.getName());
			stmt.setString(2, dataObject.getNickname());
			stmt.setInt(3, 1);			
			stmt.setInt(4, dataObject.getGuiLanguageId());			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }
		
	}
					
};
