package metaindex.data.term.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.term.ICatalogTerm;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateTermIntoSqlDbStmt extends SQLWriteStmt<ICatalogTerm>   {

	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public CreateTermIntoSqlDbStmt(List<ICatalogTerm> terms, SQLDataSource ds) throws DataProcessException { 
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
					"insert into catalog_terms (catalog_id,name,datatype,enumsList,isMultiEnum) values (?,?,?,?,?) "));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(ICatalogTerm dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, dataObject.getCatalogId());
			stmt.setString(2, dataObject.getName());
			stmt.setString(3, dataObject.getDatatype().toString());
			String enumsStr="";
			for (String enumItem : dataObject.getEnumsList()) {
				if (enumsStr.length()>0) { enumsStr+=","; }
				enumsStr+=enumItem;
			}
			stmt.setString(4, enumsStr);
			stmt.setBoolean(5, dataObject.getIsMultiEnum());
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
