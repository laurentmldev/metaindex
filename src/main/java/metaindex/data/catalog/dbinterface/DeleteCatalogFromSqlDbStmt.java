package metaindex.data.catalog.dbinterface;

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

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class DeleteCatalogFromSqlDbStmt extends SQLWriteStmt<ICatalog>   {

	IUserProfileData _activeUser;
	List<ICatalog> _data = new ArrayList<>();
	public DeleteCatalogFromSqlDbStmt(IUserProfileData activeUser, 
										List<ICatalog> catalogs, 
										SQLDataConnector ds) throws DataProcessException { 
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
			result.add(this.getDataConnector().getConnection().prepareStatement(
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
