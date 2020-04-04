package metaindex.data.filter.dbinterface;

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

import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;
import toolbox.database.sql.SQLDataConnector;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateFilterIntoDbStmt extends SQLWriteStmt<IFilter>   {

	ICatalog _catalog;
	List<IFilter> _data = new ArrayList<IFilter>();
	public CreateFilterIntoDbStmt(ICatalog c,IFilter newCat, SQLDataConnector ds) throws DataProcessException { 
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
			result.add(this.getDataConnector().getConnection().prepareStatement(
					"insert into filters (catalog_id,name,query) "
					+"values (?,?,?)"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(IFilter dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, _catalog.getId());
			stmt.setString(2, dataObject.getName());
			stmt.setString(3, dataObject.getQuery());		
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		
	}
	
					
};
