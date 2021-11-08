package toolbox.exceptions;

import java.util.ArrayList;
import java.util.List;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class DataProcessException extends Exception {
	
	List<String> _errorDetails = new ArrayList<String>();
	
	public DataProcessException(Exception e) {
		super(e.getMessage());
		if (e instanceof DataProcessException) {
			this._errorDetails=((DataProcessException)e).getErrorDetails();
		}
	}

	public DataProcessException(String msg) {
		super(msg);
	}
	
	public DataProcessException(String msg, Exception e) {
		super(msg,e);
	}	
	
	public List<String> getErrorDetails() { return  _errorDetails; }
	
	public DataProcessException(String msg,List<String> errorDetails) { 
		super(msg);
		_errorDetails=errorDetails;
	}
	
}
