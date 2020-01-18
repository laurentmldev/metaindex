package toolbox.exceptions;

public class DataAccessException extends Exception {
	public DataAccessException(Exception e) {
		super(e.getMessage());
	}

	public DataAccessException(String msg) {
		super(msg);
	}
}
