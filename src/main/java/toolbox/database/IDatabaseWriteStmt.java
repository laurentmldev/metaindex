package toolbox.database;

import toolbox.exceptions.DataProcessException;

public interface IDatabaseWriteStmt<TData>  {
				
	public Boolean execute() throws DataProcessException;	

}
