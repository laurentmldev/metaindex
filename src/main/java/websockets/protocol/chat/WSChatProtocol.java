package metaindex.websockets.protocol.chat;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.IWSProtocol;


/** 
 * Simple chat protocol based on JSON
 * @author laurent
 *
 */
public class WSChatProtocol implements IWSProtocol {

	Hashtable<String,IWSMessage> protocolMessagesTypes = new Hashtable<String,IWSMessage>();
	
	public WSChatProtocol() {
		
		IWSMessage loginMsg = new WSChatMsgLogin();
		protocolMessagesTypes.put(loginMsg.getMsgType(), loginMsg);
		
		IWSMessage tweetMsg = new WSChatMsgTweet();
		protocolMessagesTypes.put(tweetMsg.getMsgType(), tweetMsg);
	}
	
	@Override
	public String getProtocolType() {
		return "chat-protocol";
	}

	public WSChatMsgLogin getNewLoginMsg() { return new WSChatMsgLogin(); }
	public WSChatMsgTweet getNewTweetMsg() { return new WSChatMsgTweet(); }
	
	@Override
	public IWSMessage decode(String data) {
		
		String msgTypeJsonStr=IWSMessage.MSGTYPE_STRING.substring(0,1).toLowerCase()+IWSMessage.MSGTYPE_STRING.substring(1);
		String msgType=new JSONObject(data).getString(msgTypeJsonStr);
		
		IWSMessage msg = protocolMessagesTypes.get(msgType);
		
		try {
			msg.populate(null, new JSONObject(data), POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW);
		} catch (JSONException|UnableToPopulateException e) {
			e.printStackTrace();
			return null;
		}
		
		return msg;		
	}

}
