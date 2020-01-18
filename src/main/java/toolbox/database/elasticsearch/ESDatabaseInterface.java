package toolbox.database.elasticsearch;

import toolbox.database.IDatabaseInterface;

public class ESDatabaseInterface<T> implements IDatabaseInterface<ESDataSource> {

	private ESDataSource _ds;

	public ESDatabaseInterface(ESDataSource ds) { 
		_ds=ds;
	}
	@Override
	public ESDataSource getDatasource() { return _ds; }
	
}
