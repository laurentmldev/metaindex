package metaindex.data.catalog.dbinterface;

import java.sql.PreparedStatement;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateCatalogUsersAccessStmt extends SQLWriteStmt<IUserProfileData>   {

	private Log log = LogFactory.getLog(CreateOrUpdateCatalogUsersAccessStmt.class);
	
	public static final String SQL_REQUEST = 
			"select users.user_id "
			+" from users,user_roles,user_catalogs_rights"
			+" where user_roles.role='ROLE_USER' and users.user_id=user_roles.user_id and users.user_id=user_catalogs_rights.user_id";
	
	private List<IUserProfileData> _users;
	private ICatalog _catalog;
	USER_CATALOG_ACCESSRIGHTS _accessRights;
	
	
	public CreateOrUpdateCatalogUsersAccessStmt(ICatalog catalog, 
												List<IUserProfileData> users, 
												USER_CATALOG_ACCESSRIGHTS accessRights, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_catalog=catalog;
		_users=users;
		_accessRights=accessRights;
	}
	
		
	@Override
	protected List<IUserProfileData> getDataList() { return _users; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
	
	List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"insert into user_catalogs_rights (user_id,catalog_id,access_rights)"
							+" values (?,?,?)"
					+"ON DUPLICATE KEY UPDATE access_rights=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, dataObject.getId());
			stmt.setInt(2, _catalog.getId());
			stmt.setString(3, _accessRights.toString());
			stmt.setString(4, _accessRights.toString());			
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
