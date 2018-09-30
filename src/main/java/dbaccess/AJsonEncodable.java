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
public abstract class AJsonEncodable implements IJsonEncodable {

	private Log log = LogFactory.getLog(AJsonEncodable.class);
	
	public static class JsonPopulateException extends Exception {

		public JsonPopulateException(Exception e) {
			super(e.getMessage());
		}
		public JsonPopulateException(String msg) {
			super(msg);
		}
	}

	
	static public boolean populateField(Object obj,String keyName,JSONObject json) 
			throws JsonPopulateException 
	{
		String casedKey = keyName.substring(0,1).toUpperCase()+keyName.substring(1);
		String setterMethodName = "set"+casedKey;
		try { obj.getClass().getMethod(setterMethodName, Double.class).invoke(obj, json.getDouble(keyName));
		} catch (Exception notADouble) {
								
		try { obj.getClass().getMethod(setterMethodName, Boolean.class).invoke(obj, json.getBoolean(keyName));						
		} catch (Exception notABoolean) {
			
		try { obj.getClass().getMethod(setterMethodName, Integer.class).invoke(obj, json.getInt(keyName));					
		} catch (Exception notAnInt) {
			
		try { obj.getClass().getMethod(setterMethodName, String.class).invoke(obj, json.getString(keyName));						
		} catch (Exception notAString) {
			
				throw new JsonPopulateException("Method not found : "+obj.getClass().getName()+"."
								+setterMethodName+"(Integer|String|Double ...) ");
		}}}}
		return true;
	}
	
	/**
	 * Populate this object with the given JSON string, based on JAVA reflect API
	 */
	static public boolean populate(Object obj, JSONObject json) throws JsonPopulateException
	{ 
		
		Iterator<String> it = json.keys();
		while (it.hasNext()) {
			String curKey = it.next();
			populateField(obj,curKey,json); 			
		}
		return true;
	}
	
	
	@Override
	public void populate(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException 
	{ 
		try { populate(this, json); }
		catch (JsonPopulateException e){
			log.error(e.getMessage());		
		}
	}
	@Override
	public JSONObject encode() { return new JSONObject(this); }

	@Override
	public Object clone() throws CloneNotSupportedException {
		
		AJsonEncodable newObj=null;
		try {
			newObj = this.getClass().newInstance();
			newObj.populate(null, this.encode(), POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW);
			
			return newObj;
		} catch (InstantiationException | IllegalAccessException|metaindex.dbaccess.IGenericEncodable.UnableToPopulateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newObj;
	}
	
}
