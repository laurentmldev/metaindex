package metaindex.data.term.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.term.TermVocabularySet;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateVocabularyIntoDbStmt extends SQLWriteStmt<TermVocabularySet>   {

	List<TermVocabularySet> _data = new ArrayList<>();
	public CreateOrUpdateVocabularyIntoDbStmt(List<TermVocabularySet> terms, SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);
	}
	
	@Override
	protected List<TermVocabularySet> getDataList() { return _data; }
	
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			// insert if not present, update if already present
			result.add(this.getDatasource().getConnection().prepareStatement(
					"insert into catalog_terms_vocabulary (catalog_term_id,guilanguage_id,termTraduction) values (?,?,?) "
						+"ON DUPLICATE KEY UPDATE termTraduction=?"));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(TermVocabularySet dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		
		try {
			stmt.setInt(1, dataObject.getId());
			stmt.setInt(2, dataObject.getGuiLanguageId());
			stmt.setString(3, dataObject.getName());
			stmt.setString(4, dataObject.getName());
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
