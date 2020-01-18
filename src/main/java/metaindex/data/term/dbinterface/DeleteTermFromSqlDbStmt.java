package metaindex.data.term.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class DeleteTermFromSqlDbStmt extends SQLWriteStmt<ICatalogTerm>   {

	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public DeleteTermFromSqlDbStmt(List<ICatalogTerm> terms, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);
		
	}
	@Override
	protected List<ICatalogTerm> getDataList() { return _data; }
	
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDatasource().getConnection().prepareStatement(
					"delete from catalog_terms where catalog_id=? and name=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(ICatalogTerm dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, dataObject.getCatalogId());
			stmt.setString(2, dataObject.getName());
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
