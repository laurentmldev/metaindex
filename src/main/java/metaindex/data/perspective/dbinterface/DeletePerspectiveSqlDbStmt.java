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

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

public class DeletePerspectiveSqlDbStmt extends SQLWriteStmt<Integer>   {

	private Log log = LogFactory.getLog(DeletePerspectiveSqlDbStmt.class);
	
	IUserProfileData _activeUser;
	List<Integer> _data = new ArrayList<>();
	ICatalog _catalog;
	public DeletePerspectiveSqlDbStmt(IUserProfileData activeUser, 
										ICatalog catalog, 
										List<Integer> perspectivesIds,
										SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_activeUser=activeUser;
		_data.addAll(perspectivesIds);		
		_catalog=catalog;
	}
	
				
	@Override
	protected List<Integer> getDataList() { return _data; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDatasource().getConnection().prepareStatement(
					"delete from catalog_perspectives where catalog_id=? and catalog_perspective_id=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(Integer dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {

			stmt.setInt(1, _catalog.getId());
			stmt.setInt(2, dataObject);
		
			stmt.addBatch();
		} catch (Exception e) { 
			throw new DataProcessException("While deleting perspective : could not extract definition : "+e.getMessage());
		}
	}
						
};
