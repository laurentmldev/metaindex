package metaindex.data.catalog.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class DeleteCatalogFromSqlDbStmt extends SQLWriteStmt<ICatalog>   {

	IUserProfileData _activeUser;
	List<ICatalog> _data = new ArrayList<>();
	public DeleteCatalogFromSqlDbStmt(IUserProfileData activeUser, 
										List<ICatalog> catalogs, 
										SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_activeUser=activeUser;
		_data=catalogs;		
	}
	
				
	@Override
	protected List<ICatalog> getDataList() { return _data; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDatasource().getConnection().prepareStatement(
					"delete from catalogs where catalog_id=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(ICatalog dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, dataObject.getId());
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
