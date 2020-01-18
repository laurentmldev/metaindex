package toolbox.database.elasticsearch;

import toolbox.exceptions.DataProcessException;

public class ESDataProcessException extends DataProcessException {
	
	public ESDataProcessException(String s) { super(s); }
	public ESDataProcessException(String s, Exception e) { super(s,e); }
	
};
