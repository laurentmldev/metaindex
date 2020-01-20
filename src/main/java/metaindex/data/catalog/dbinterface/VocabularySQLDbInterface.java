package metaindex.data.catalog.dbinterface;

/**
GNU GENERAL PUBLIC LICENSE
 Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLReadStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.perspective.dbinterface.CreateOrUpdatePerspectiveIntoSqlDbStmt;
import metaindex.data.catalog.CatalogVocabularySet;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogCustomParams;
import metaindex.data.userprofile.IUserProfileData;

public class VocabularySQLDbInterface  extends SQLDatabaseInterface<ICatalog> 
{
	
	public VocabularySQLDbInterface(SQLDataSource ds) { super(ds); }
	
	
	public SQLReadStmt<ICatalog> getLoadFromDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadVocabularyFromDbStmt(data, getDatasource());
	}
	public SQLReadStmt<ICatalog> getLoadFromDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getLoadFromDbStmt(list);
	}
	
	public SQLWriteStmt<CatalogVocabularySet> getWriteIntoDbStmt(List<CatalogVocabularySet> data) throws DataProcessException {
		return new CreateOrUpdateVocabularyIntoDbStmt(data, getDatasource());
	}
	public SQLWriteStmt<CatalogVocabularySet> getWriteIntoDbStmt(CatalogVocabularySet data) throws DataProcessException {
		List<CatalogVocabularySet> list = new ArrayList<>();
		list.add(data);
		return getWriteIntoDbStmt(list);
	}
	
	

	
	
}
