package toolbox.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.exceptions.DataProcessException;

public interface ILoadableFromDb {
	public void loadFromDb() throws DataProcessException;

}
