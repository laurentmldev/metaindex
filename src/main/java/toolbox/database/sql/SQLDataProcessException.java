package toolbox.database.sql;

import toolbox.exceptions.DataProcessException;

public class SQLDataProcessException extends DataProcessException {
	
	public SQLDataProcessException(String s) { super(s); }
	public SQLDataProcessException(String s,Exception e) { super(s,e); }
	
};
