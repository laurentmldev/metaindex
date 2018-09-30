package metaindex.dbaccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.transaction.TransactionException;

import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.accessors.AMetadataAccessor;

public interface IDataAccessAware {

	static public enum DBResult { DB_SUCCESS, DB_ERROR };


public class DataAccessConnectException extends Exception  {

	private static final long serialVersionUID = 7747773603984777420L;
	public DataAccessConnectException(Exception e) {
		super(e);
	}
}

public class DataAccessConstraintException extends TransactionException  {

	private static final long serialVersionUID = 7747773603984777420L;
	
	List<String> msgs=null;
	
	public DataAccessConstraintException(Exception e) {
		super(e.getMessage());
	}
	public DataAccessConstraintException(String msg) {
		super(msg);
	}
	public DataAccessConstraintException(List<String> messages) throws DataAccessConstraintException {
		super("Several errors occured during data operation");
		msgs=messages;
	}
	
	@Override
	public String getMessage() {
		if (msgs!=null) {
			String msg=super.getMessage();
			Iterator<String> it = msgs.iterator();
			while (it.hasNext()) {
				msg+="\n"+it.next();
			}
			return msg;
		} else { return super.getMessage(); }
	}
}


public class DataAccessErrorException extends TransactionException{

	private static final long serialVersionUID = 1986838654428553716L;
	public DataAccessErrorException(Exception e) {
		super(e.getMessage());
	}

	public DataAccessErrorException(String msg) {
		super(msg);
	}
}


public class DataReferenceErrorException extends Exception{

	private static final long serialVersionUID = 1986838654428553716L;
	public DataReferenceErrorException(Exception e) {
		super(e.getMessage());
	}
	public DataReferenceErrorException(String msg) {
		super(msg);
	}
}


}
