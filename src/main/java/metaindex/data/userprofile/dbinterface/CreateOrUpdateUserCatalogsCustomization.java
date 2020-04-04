package metaindex.data.userprofile.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLReadStreamStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateUserCatalogsCustomization extends SQLWriteStmt<IUserProfileData>   {
	
	List<IUserProfileData> _users = new ArrayList<>();
	ICatalog _catalog;
	
	public CreateOrUpdateUserCatalogsCustomization(IUserProfileData u, ICatalog c, SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_users.add(u);
		_catalog=c;
	}
	
@Override
protected List<IUserProfileData> getDataList() { return _users; }

@Override
protected List<PreparedStatement> prepareStmts() throws DataProcessException {

List<PreparedStatement> result = new ArrayList<PreparedStatement>();

try {
	result.add(this.getDataConnector().getConnection().prepareStatement(
			"insert into user_catalogs_customization (user_id,catalog_id,kibana_iframe) values (?,?,?) "
					+"ON DUPLICATE KEY UPDATE kibana_iframe=?"));
	
} catch (SQLException e) { throw new DataProcessException(e); }

return result;
}
@Override
protected void populateStatements(IUserProfileData dataObject, List<PreparedStatement> stmts) throws DataProcessException {

PreparedStatement stmt = stmts.get(0);
try {
	stmt.setInt(1, dataObject.getId());
	stmt.setInt(2, _catalog.getId());
	stmt.setString(3, dataObject.getUserCatalogKibanaIFrameHtml(_catalog.getId()).toString());	
	stmt.setString(4, dataObject.getUserCatalogKibanaIFrameHtml(_catalog.getId()).toString());	
	stmt.addBatch();
} catch (SQLException e) { throw new DataProcessException(e); }		
}
					
};
