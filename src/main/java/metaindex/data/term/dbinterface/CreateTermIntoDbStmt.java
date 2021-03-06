package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import toolbox.database.IDatabaseWriteStmt;
import toolbox.database.elasticsearch.ESDataProcessException;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.sql.SQLDataProcessException;
import toolbox.database.sql.SQLDataConnector;
import toolbox.exceptions.DataProcessException;

class CreateTermIntoDbStmt implements IDatabaseWriteStmt<ICatalogTerm>   {

	
	
	CreateFieldIntoEsDbStmt _esFieldDbStmt = null;
	CreateTermIntoSqlDbStmt _sqlTermDbStmt = null;
	public CreateTermIntoDbStmt(ICatalog c, List<ICatalogTerm> terms, SQLDataConnector dsSql, ElasticSearchConnector dsEs) throws DataProcessException {
		// create ES field if not already present
		List<ICatalogTerm> termsWithoutESField = new ArrayList<>();
		for (ICatalogTerm term : terms) {
			if (c.getTerms().get(term.getName())==null) {
				termsWithoutESField.add(term);
			}
		}
		if (termsWithoutESField.size()>0) { _esFieldDbStmt=new CreateFieldIntoEsDbStmt(c,termsWithoutESField,dsEs); }
		_sqlTermDbStmt=new CreateTermIntoSqlDbStmt(terms,dsSql);
	}

	@Override
	public Boolean execute() throws ESDataProcessException,SQLDataProcessException {	
		// try to create missing ES fields mapping if necessary
		Boolean result = true;
		if (_esFieldDbStmt!=null) { 
			try {
				result=_esFieldDbStmt.execute();
			} catch (Exception e) {
				throw new ESDataProcessException("Unable to create term in storage database",e);
			}
		}
		
		// create corresponding Metaindex term
		if (result) { 
			try {
				result=_sqlTermDbStmt.execute();
			} catch (Exception e) {
				throw new SQLDataProcessException("Unable to create term in description database",e);
			}
		}
		return result;
		
	}
					
};
