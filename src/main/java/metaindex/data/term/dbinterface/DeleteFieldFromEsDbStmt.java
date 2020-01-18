package metaindex.data.term.dbinterface;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.term.ICatalogTerm;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;

/** NOT YET FUNCTIONAL */
class DeleteFieldFromEsDbStmt extends ESWriteStmt<ICatalogTerm>   {

	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public DeleteFieldFromEsDbStmt(List<ICatalogTerm> terms, ESDataSource ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);
		
	}
	
	@Override
	public Boolean execute() throws DataProcessException {
		// TODO ... re-index operation ???
		return false;
	}
	
};
