package toolbox.exceptions;

public class DataProcessException extends Exception {
	public DataProcessException(Exception e) {
		super(e.getMessage());
	}

	public DataProcessException(String msg) {
		super(msg);
	}
	
	public DataProcessException(String msg, Exception e) {
		super(msg,e);
	}		
}
