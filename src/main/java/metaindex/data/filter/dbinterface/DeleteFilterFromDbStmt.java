package metaindex.data.filter.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class DeleteFilterFromDbStmt extends SQLWriteStmt<IFilter>   {

	ICatalog _catalog;
	List<IFilter> _data = new ArrayList<IFilter>();
	public DeleteFilterFromDbStmt(ICatalog c,IFilter newCat, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_catalog=c;
		_data.add(newCat);
	}
	@Override
	protected List<IFilter> getDataList() { return _data; }
	
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDatasource().getConnection().prepareStatement(
					"delete from filters where catalog_id=? and name=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(IFilter dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, _catalog.getId());
			stmt.setString(2, dataObject.getName());
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
