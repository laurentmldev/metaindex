package metaindex.data.perspective.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.perspectives.WsControllerPerspective;
import toolbox.database.sql.SQLDataSource;
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
										SQLDataSource ds) throws DataProcessException { 
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
			result.add(this.getDatasource().getConnection().prepareStatement(
					"insert into catalog_perspectives (catalog_id,name,perspective_json_string) values (?,?,?)"
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
			stmt.setString(2,  json.getString("name"));
			stmt.setString(3, dataObject);
			stmt.setString(4,  json.getString("name"));
			stmt.setString(5, dataObject);
						
			stmt.addBatch();
		} catch (Exception e) { 
			throw new DataProcessException("While creating or updated perspective : could not parse json definition : "+e.getMessage());
		}
	}
						
};
