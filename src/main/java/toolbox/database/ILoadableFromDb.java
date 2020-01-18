package toolbox.database;

import toolbox.exceptions.DataProcessException;

public interface ILoadableFromDb {
	public void loadFromDb() throws DataProcessException;

}
