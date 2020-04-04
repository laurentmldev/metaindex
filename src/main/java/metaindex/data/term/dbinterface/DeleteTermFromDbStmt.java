package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import toolbox.database.IDatabaseWriteStmt;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.sql.SQLDataConnector;
import toolbox.exceptions.DataProcessException;

/** NOT YET FUNCTIONAL (see DeleteFieldFromEsDbStmt) */
class DeleteTermFromDbStmt implements IDatabaseWriteStmt<ICatalogTerm>   {

	DeleteFieldFromEsDbStmt _esFieldDbStmt;
	DeleteTermFromSqlDbStmt _sqlTermDbStmt;
	public DeleteTermFromDbStmt(ICatalog c, List<ICatalogTerm> terms, SQLDataConnector dsSql, ElasticSearchConnector dsEs) throws DataProcessException { 
		_esFieldDbStmt=new DeleteFieldFromEsDbStmt(terms,dsEs);
		_sqlTermDbStmt=new DeleteTermFromSqlDbStmt(terms,dsSql);
	}

	@Override
	public Boolean execute() throws DataProcessException {
		Boolean result = _esFieldDbStmt.execute();
		if (result) { result=_sqlTermDbStmt.execute(); }
		return result;
	}
					
};
