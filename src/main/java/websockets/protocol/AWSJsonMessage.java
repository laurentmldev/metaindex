package metaindex.websockets.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.AJsonEncodable;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;


/**
 * A JSON encoded message.
 * Only basic types Double, Int and String are supported.
 * @author laurent
 *
 */
public abstract class AWSJsonMessage extends AJsonEncodable implements IWSMessage {

	private Log log = LogFactory.getLog(AWSJsonMessage.class);
	
	/**
	 * Populate this message with the given JSON string, based on JAVA reflect API
	 * Check that this object type is coherent with JSON type
	 */
	@Override
	public void populate(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException 
	{ 
		
		Iterator<String> it = json.keys();
		while (it.hasNext()) {
			String curKey = it.next();
			String casedKey = curKey.substring(0,1).toUpperCase()+curKey.substring(1);
			// check types compliance
			if (casedKey.equals(IWSMessage.MSGTYPE_STRING)) {
				if (! this.getMsgType().equals(json.get(curKey))) {
					log.warn("Trying to populate a WSMsg '"+this.getMsgType()+"' with a JSON msg '"+json.getDouble(curKey)+"'.");
				}
				continue;	
			}
			try { populateField(this,curKey,json); }
			catch (Exception e) {
				log.error(this.getClass().getCanonicalName()+", unable to populate object with received JSON contents: "+ e.getMessage());
			}
		}
	}
	
	/**
	 * Automatic clone based on class reflection and set/get methods names
	 */
	@Override
	public AWSJsonMessage clone() {		
		return (AWSJsonMessage) this.clone();
	}
	
}
