package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.exceptions.DataProcessException;

public class ESDataProcessException extends DataProcessException {
	private static final long serialVersionUID = 1501212805101111551L;
	public ESDataProcessException(String s) { super(s); }
	public ESDataProcessException(String s, Exception e) { super(s,e); }
	
};
