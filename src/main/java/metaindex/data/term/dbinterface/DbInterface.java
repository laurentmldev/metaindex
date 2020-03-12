package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseWriteStmt;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESReadStmt;
import toolbox.database.sql.SQLDataSource;
import toolbox.database.sql.SQLDatabaseInterface;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.database.sql.SQLReadStmt;
import toolbox.database.sql.SQLWriteStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.TermVocabularySet;

public class DbInterface  extends SQLDatabaseInterface<ICatalogTerm> 
{
	ESDataSource _dsEs;
	public DbInterface(SQLDataSource ds, ESDataSource dsEs) { 
		super(ds);
		_dsEs=dsEs;
	}
	
	// ---
	public SQLPopulateStmt<ICatalogTerm> getPopulateTermsFromDbStmt(List<ICatalog> c) throws DataProcessException {
		assert(c.size()>0);
		return new PopulateTermsFromDbStmt(c,getDatasource());
	}
	public SQLPopulateStmt<ICatalogTerm> getPopulateTermsFromDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getPopulateTermsFromDbStmt(list);
	}
	
	public SQLPopulateStmt<ICatalogTerm> getLoadFromDbStmt(ICatalog c, List<ICatalogTerm> data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(c);
		return new PopulateTermsFromDbStmt(list, data, getDatasource());
	}
	
	public SQLWriteStmt<ICatalogTerm> getUpdateIntoDbStmt(List<ICatalogTerm> data) throws DataProcessException {
		return new CreateOrUpdateTermIntoDbStmt(data, getDatasource());
	}
	public SQLWriteStmt<ICatalogTerm> getUpdateIntoDbStmt(ICatalogTerm data) throws DataProcessException {
		List<ICatalogTerm> terms = new ArrayList<>();
		terms.add(data);
		return new CreateOrUpdateTermIntoDbStmt(terms, getDatasource());
	}
	
	public IDatabaseWriteStmt<ICatalogTerm> deleteFromDbStmt(ICatalog c, List<ICatalogTerm> data) throws DataProcessException {
		return new DeleteTermFromDbStmt(c,data, getDatasource(), _dsEs);
	}
	public IDatabaseWriteStmt<ICatalogTerm> deleteFromDbStmt(ICatalog c, ICatalogTerm data) throws DataProcessException {
		List<ICatalogTerm> list = new ArrayList<>();
		list.add(data);
		return deleteFromDbStmt(c,list);
	}
	
	// --- create terms
	public IDatabaseWriteStmt<ICatalogTerm> createIntoESDbStmt(ICatalog c, List<ICatalogTerm> data) throws DataProcessException {
		return new CreateFieldIntoEsDbStmt(c, data, _dsEs);
	}
	public IDatabaseWriteStmt<ICatalogTerm> createIntoDbStmt(ICatalog c, List<ICatalogTerm> data) throws DataProcessException {
		return new CreateTermIntoDbStmt(c, data, getDatasource(), _dsEs);
	}
	public IDatabaseWriteStmt<ICatalogTerm> createIntoDbStmt(ICatalog c, ICatalogTerm data) throws DataProcessException {
		List<ICatalogTerm> list = new ArrayList<>();
		list.add(data);
		return createIntoDbStmt(c,list);
	}
	
	// --- load vocabulary
	public SQLPopulateStmt<ICatalogTerm> getPopulateVocabularyFromDbStmt(Collection<ICatalogTerm> data) throws DataProcessException {
		return new PopulateVocabularyFromDbStmt(data, getDatasource());
	}
	// --- create or update vocabulary
	public SQLWriteStmt<TermVocabularySet> getCreateOrUpdateVocabularyIntoDbStmt(List<TermVocabularySet> data) throws DataProcessException {
		return new CreateOrUpdateVocabularyIntoDbStmt(data, getDatasource());
	}
	public SQLWriteStmt<TermVocabularySet> getCreateOrUpdateVocabularyIntoDbStmt(TermVocabularySet data) throws DataProcessException {
		List<TermVocabularySet> list = new ArrayList<>();
		list.add(data);
		return getCreateOrUpdateVocabularyIntoDbStmt(list);
	}
	
}
