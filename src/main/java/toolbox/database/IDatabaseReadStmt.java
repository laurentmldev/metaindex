package toolbox.database;

import java.util.List;

import toolbox.exceptions.DataProcessException;

public interface IDatabaseReadStmt<TData> {

	public List<TData> execute() throws DataProcessException;	

}
