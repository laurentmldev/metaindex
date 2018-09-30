package metaindex.dbaccess;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;


/**
 * A JSON encoded message.
 * Only basic types Double, Int and String are supported.
 * @author laurent
 *
 */
public interface IGenericEncodable<T> {

	public enum POPULATE_POLICY { REPLACE_EXISTING_ONLY, REPLACE_EXISTING_AND_CREATE_WHEN_NEW, ALWAYS_CREATE};
	
	@SuppressWarnings("serial")
	public class UnableToPopulateException extends Exception { public UnableToPopulateException(String msg) { super(msg); } };
	
	/**
	 * Populate this message with the given JSON string, based on JAVA reflect API
	 * @param activeUser user performing operation
	 * @param policy populate policy (might not always be taking into consideration depending on which object is populated)
	 * @throws UnableToPopulateException thrown when unable to perform populate operation.
	 */
	public void populate(IUserProfileData activeUser, T json, POPULATE_POLICY policy) throws UnableToPopulateException;
	
	
	/**
	 * Serialize this object into a Json string
	 * @return
	 */
	public T encode();

	
}
