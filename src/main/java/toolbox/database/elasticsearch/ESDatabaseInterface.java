package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.IDatabaseInterface;

public class ESDatabaseInterface<T> implements IDatabaseInterface<ESDataSource> {

	private ESDataSource _ds;

	public ESDatabaseInterface(ESDataSource ds) { 
		_ds=ds;
	}
	@Override
	public ESDataSource getDatasource() { return _ds; }
	
}
