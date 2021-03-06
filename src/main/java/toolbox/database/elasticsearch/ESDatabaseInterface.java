package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseInterface;

public class ESDatabaseInterface<T> implements IDatabaseInterface<ElasticSearchConnector> {

	private ElasticSearchConnector _ds;

	public ESDatabaseInterface(ElasticSearchConnector ds) { 
		_ds=ds;
	}
	@Override
	public ElasticSearchConnector getDataConnector() { return _ds; }
	
}
