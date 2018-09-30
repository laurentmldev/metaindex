package metaindex.websockets.protocol.elementData;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.specialized.Metadata_LongText;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.IWSProtocol;


/** 
 * ElementData protocol based on JSON and websockets
 * @author laurent
 *
 */
public class WSElementDataProtocol implements IWSProtocol {

	private Log log = LogFactory.getLog(WSElementDataProtocol.class);
	
	Hashtable<String,IWSMessage> protocolMessagesTypes = new Hashtable<String,IWSMessage>();
	
	public WSElementDataProtocol() {
		
		IWSMessage loginMsg = new WSElementMsgLogin();
		protocolMessagesTypes.put(loginMsg.getMsgType(), loginMsg);
				
		IWSMessage getElContentsMsg = new WSElementMsgGetContents();
		protocolMessagesTypes.put(getElContentsMsg.getMsgType(), getElContentsMsg);
		
		IWSMessage elContents = new WSElementMsgElementContents();
		protocolMessagesTypes.put(elContents.getMsgType(), elContents);
		
		IWSMessage datasetContents = new WSElementMsgDatasetContents();
		protocolMessagesTypes.put(datasetContents.getMsgType(), datasetContents);
		
		IWSMessage metadataImageContents = new WSElementMsgMetadataContents_Image();
		protocolMessagesTypes.put(metadataImageContents.getMsgType(), metadataImageContents);
		
	}
	
	@Override
	public String getProtocolType() {
		return "elementdata-protocol";
	}

	public WSElementMsgLogin getNewLoginMsg() { return new WSElementMsgLogin(); }
	
	public WSElementMsgGetContents getNewMsgGetContents() { return new WSElementMsgGetContents(); }
	public WSElementMsgElementContents getNewElementContentsMsg(IElementHandle e, IUserProfileData p) { return new WSElementMsgElementContents(e,p); }
	public WSElementMsgDatasetContents getNewDatasetContentsMsg(IDatasetHandle e, IUserProfileData p) { return new WSElementMsgDatasetContents(e,p); }
	public AWSElementMsgMetadataContents getNewMetadataContentsMsg(IMetadataHandle e, IUserProfileData p) { 
		if (e.isImage()) { return new WSElementMsgMetadataContents_Image(e,p); }
		else if (e.isTinyText()) { return new WSElementMsgMetadataContents_TinyText(e,p); }
		else if (e.isLongText()) { return new WSElementMsgMetadataContents_LongText(e,p); }
		else if (e.isWebLink()) { return new WSElementMsgMetadataContents_WebLink(e,p); }
		else if (e.isNumber()) { return new WSElementMsgMetadataContents_Number(e,p); }
		else {	
			log.warn("Unhandled metadata type '"+e.getDatatypeId()+"' for sendning contents to client through websocket. Metadata skipped.");
			return null; 
		}
	}
	
	
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
