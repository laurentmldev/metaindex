package toolbox.database.sql;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.exceptions.DataProcessException;

public class SQLDataProcessException extends DataProcessException {
	private static final long serialVersionUID = -8568837462918258129L;
	public SQLDataProcessException(String s) { super(s); }
	public SQLDataProcessException(String s,Exception e) { super(s,e); }
	
};
