package toolbox.exceptions;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class DataAccessException extends Exception {
	public DataAccessException(Exception e) {
		super(e.getMessage());
	}

	public DataAccessException(String msg) {
		super(msg);
	}
}
