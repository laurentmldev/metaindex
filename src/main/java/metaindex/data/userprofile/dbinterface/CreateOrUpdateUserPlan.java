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

import metaindex.data.commons.globals.plans.IPlan;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateUserPlan extends SQLWriteStmt<IUserProfileData>   {
	
	List<IUserProfileData> _users = new ArrayList<>();
	IPlan _plan;
	
	public CreateOrUpdateUserPlan(IUserProfileData u, IPlan p, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_users.add(u);
		_plan=p;
	}
	
@Override
protected List<IUserProfileData> getDataList() { return _users; }

@Override
protected List<PreparedStatement> prepareStmts() throws DataProcessException {

List<PreparedStatement> result = new ArrayList<PreparedStatement>();

try {
	result.add(this.getDataConnector().getConnection().prepareStatement(
			"insert into user_plans (user_id,plan_id,startDate,endDate,nbQuotaWarnings) values (?,?,?,?,?) "
					+"ON DUPLICATE KEY UPDATE plan_id=?, startDate=?, endDate=?, nbQuotaWarnings=?"));
	
} catch (SQLException e) { throw new DataProcessException(e); }

return result;
}
@Override
protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {

PreparedStatement stmt = stmts.get(0);
try {
	
	stmt.setInt(1, dataObject.getId());
	stmt.setInt(2, _plan.getId());
	stmt.setDate(3, new java.sql.Date(dataObject.getPlanStartDate().getTime()));
	stmt.setDate(4, new java.sql.Date(dataObject.getPlanEndDate().getTime()));
	stmt.setInt(5, dataObject.getPlanNbQuotaWarnings());
	
	stmt.setInt(6, _plan.getId());
	stmt.setDate(7, new java.sql.Date(dataObject.getPlanStartDate().getTime()));
	stmt.setDate(8, new java.sql.Date(dataObject.getPlanEndDate().getTime()));
	stmt.setInt(9, dataObject.getPlanNbQuotaWarnings());
	
	stmt.addBatch();
} catch (SQLException e) { throw new DataProcessException(e); }		
}
					
};
