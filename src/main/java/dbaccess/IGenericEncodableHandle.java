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
public interface IGenericEncodableHandle<T> {
	
	/**
	 * Serialize this object into a Json string
	 * @return
	 */
	public T encode();

	
}
