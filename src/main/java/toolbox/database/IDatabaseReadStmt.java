package toolbox.database;

import metaindex.data.commons.globals.guitheme.IGuiTheme;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;
import toolbox.utils.IStreamProducer;

public interface IDatabaseReadStmt<TData> extends IStreamProducer<TData> {

	public void execute(IStreamHandler<TData> h) throws DataProcessException;	
	

}
