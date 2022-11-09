package metaindex.data.perspective.dbinterface;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.app.control.websockets.perspectives.WsControllerPerspective;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

public class CreateOrUpdatePerspectiveIntoSqlDbStmt extends SQLWriteStmt<String>   {

	private Log log = LogFactory.getLog(CreateOrUpdatePerspectiveIntoSqlDbStmt.class);
	
	IUserProfileData _activeUser;
	List<String> _data = new ArrayList<>();
	ICatalog _catalog;
	public CreateOrUpdatePerspectiveIntoSqlDbStmt(IUserProfileData activeUser, 
										ICatalog catalog, 
										List<String> perspectivesJsonStr,
										SQLDataConnector ds) throws DataProcessException { 
		super(ds);
		_activeUser=activeUser;
		_data.addAll(perspectivesJsonStr);		
		_catalog=catalog;
	}
	
				
	@Override
	protected List<String> getDataList() { return _data; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"insert into catalog_perspectives (catalog_id,catalog_perspective_id,name,perspective_json_string) values (?,?,?,?) "
					+"ON DUPLICATE KEY UPDATE name=?, perspective_json_string=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(String dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			
			JSONObject json = new JSONObject(dataObject);			
			stmt.setInt(1, _catalog.getId());
			stmt.setInt(2, json.getInt("id"));
			stmt.setString(3,  json.getString("name"));
			stmt.setString(4, dataObject);
			stmt.setString(5,  json.getString("name"));
			stmt.setString(6, dataObject);
						
			stmt.addBatch();
		} catch (Exception e) { 
			throw new DataProcessException("While creating or updated perspective : could not parse json definition : "+e.getMessage());
		}
	}
						
};
