package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import metaindex.data.term.ICatalogTerm;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;

/** NOT YET FUNCTIONAL */
class DeleteFieldFromEsDbStmt extends ESWriteStmt<ICatalogTerm>   {

	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public DeleteFieldFromEsDbStmt(List<ICatalogTerm> terms, ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);
		
	}
	
	@Override
	public Boolean execute() throws DataProcessException {
		// TODO ... re-index operation ???
		return false;
	}
	
};
