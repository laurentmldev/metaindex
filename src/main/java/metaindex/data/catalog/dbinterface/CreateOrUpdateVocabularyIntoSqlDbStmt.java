package metaindex.data.catalog.dbinterface;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateVocabularyIntoDbStmt extends SQLWriteStmt<CatalogVocabularySet>   {

	List<CatalogVocabularySet> _data = new ArrayList<>();
	public CreateOrUpdateVocabularyIntoDbStmt(List<CatalogVocabularySet> catalogVocabularies, 
										SQLDataSource ds) throws DataProcessException { 
		super(ds);
		_data.addAll(catalogVocabularies);		
	}
	
				
	@Override
	protected List<CatalogVocabularySet> getDataList() { return _data; }
	
	@Override
	protected List<PreparedStatement> prepareStmts() throws DataProcessException {
		
		List<PreparedStatement> result = new ArrayList<PreparedStatement>();
		
		try {
			result.add(this.getDatasource().getConnection().prepareStatement(
					"insert into catalog_vocabulary (catalog_id,guilanguage_id,catalogName,catalogComment,"
							+"itemTraduction,itemsTraduction,"
							+"userTraduction,usersTraduction"
								+") values (?,?,?,?,?,?,?,?)"
					+"ON DUPLICATE KEY UPDATE catalogName=?,catalogComment=?"
										+",itemTraduction=?,itemsTraduction=?"
										+",userTraduction=?,usersTraduction=?"
								));
			
		} catch (SQLException e) { throw new DataProcessException(e); }
		
		return result;
	}
	@Override
	protected void populateStatements(CatalogVocabularySet dataObject, List<PreparedStatement> stmts) throws DataProcessException {
		
		PreparedStatement stmt = stmts.get(0);
		try {
			stmt.setInt(1, dataObject.getCatalogId());
			stmt.setInt(2, dataObject.getGuiLanguageId());
			
			stmt.setString(3, dataObject.getName());
			stmt.setString(4, dataObject.getComment());
			stmt.setString(5, dataObject.getItem());
			stmt.setString(6, dataObject.getItems());
			stmt.setString(7, dataObject.getUser());
			stmt.setString(8, dataObject.getUsers());
			
			stmt.setString(9, dataObject.getName());
			stmt.setString(10, dataObject.getComment());
			stmt.setString(11, dataObject.getItem());
			stmt.setString(12, dataObject.getItems());
			stmt.setString(13, dataObject.getUser());
			stmt.setString(14, dataObject.getUsers());
			
			stmt.addBatch();
		} catch (SQLException e) { throw new DataProcessException(e); }		
	}
						
};
