package metaindex.data.term.dbinterface;

import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import toolbox.database.IDatabaseWriteStmt;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.sql.SQLDataSource;
import toolbox.exceptions.DataProcessException;

/** NOT YET FUNCTIONAL (see DeleteFieldFromEsDbStmt) */
class DeleteTermFromDbStmt implements IDatabaseWriteStmt<ICatalogTerm>   {

	DeleteFieldFromEsDbStmt _esFieldDbStmt;
	DeleteTermFromSqlDbStmt _sqlTermDbStmt;
	public DeleteTermFromDbStmt(ICatalog c, List<ICatalogTerm> terms, SQLDataSource dsSql, ESDataSource dsEs) throws DataProcessException { 
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
