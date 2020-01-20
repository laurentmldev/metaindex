package metaindex.data.term.dbinterface;

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
import metaindex.data.term.ICatalogTerm;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateTermIntoDbStmt extends SQLWriteStmt<ICatalogTerm>   {

	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public CreateOrUpdateTermIntoDbStmt(List<ICatalogTerm> terms, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);
		
	}
	@Override
	protected List<ICatalogTerm> getDataList() { return _data; }
	
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			// insert if not present, update if already present
			result.add(this.getDatasource().getConnection().prepareStatement(
					"insert into catalog_terms (catalog_id,name,datatype,enumsList,isMultiEnum) values (?,?,?,?,?) "
						+"ON DUPLICATE KEY UPDATE datatype=?,enumsList=?,isMultiEnum=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(ICatalogTerm dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		
		String enumsListStr="";
		for (String curEnum : dataObject.getEnumsList()) {
			if (enumsListStr.length()>0) { enumsListStr+=","; }
			enumsListStr+=curEnum;
		}
		
		try {
			stmt.setInt(1, dataObject.getCatalogId());
			stmt.setString(2, dataObject.getName());
			
			stmt.setString(3, dataObject.getDatatype().toString());
			stmt.setString(4, enumsListStr);
			stmt.setBoolean(5, dataObject.getIsMultiEnum());
			
			stmt.setString(6, dataObject.getDatatype().toString());
			stmt.setString(7, enumsListStr);
			stmt.setBoolean(8, dataObject.getIsMultiEnum());
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
