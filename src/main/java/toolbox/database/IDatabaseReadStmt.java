package toolbox.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import toolbox.exceptions.DataProcessException;

public interface IDatabaseReadStmt<TData> {

	public List<TData> execute() throws DataProcessException;	

}
