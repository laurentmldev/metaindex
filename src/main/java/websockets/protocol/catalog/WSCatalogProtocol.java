package metaindex.websockets.protocol.catalog;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.IWSProtocol;


/** 
 * Simple chat protocol based on JSON
 * @author laurent
 *
 */
public class WSCatalogProtocol implements IWSProtocol {

	Hashtable<String,IWSMessage> protocolMessagesTypes = new Hashtable<String,IWSMessage>();
	
	public WSCatalogProtocol() {
		
		IWSMessage loginMsg = new WSCatalogMsgLogin();
		protocolMessagesTypes.put(loginMsg.getMsgType(), loginMsg);
		
		IWSMessage elSummaryMsg = new WSCatalogMsgElementSummary();
		protocolMessagesTypes.put(elSummaryMsg.getMsgType(), elSummaryMsg);
		
		IWSMessage catalogSummaryMsg = new WSCatalogMsgCatalogSummary();
		protocolMessagesTypes.put(catalogSummaryMsg.getMsgType(), catalogSummaryMsg);
		
		IWSMessage catalogsListMsg = new WSCatalogMsgCatalogsList();
		protocolMessagesTypes.put(catalogsListMsg.getMsgType(), catalogsListMsg);
		
		IWSMessage communityElsMsg = new WSCommunityMsgElementsSummaries();
		protocolMessagesTypes.put(communityElsMsg.getMsgType(), communityElsMsg);
		
		IWSMessage catalogSelectMsg = new WSCatalogMsgSelectCatalog();
		protocolMessagesTypes.put(catalogSelectMsg.getMsgType(), catalogSelectMsg);
		
	}
	
	
	@Override
	public String getProtocolType() {
		return "catalog-protocol";
	}

	public WSCatalogMsgLogin getNewLoginMsg() { return new WSCatalogMsgLogin(); }
	public WSCatalogMsgElementSummary getNewElementSummaryMsg(IElementHandle e, IUserProfileData p) { return new WSCatalogMsgElementSummary(e,p); }
	public WSCatalogMsgCatalogSummary getNewCatalogSummaryMsg(ICatalogHandle c) { return new WSCatalogMsgCatalogSummary(c); }
	public WSCatalogMsgCatalogSummary getNewCatalogSummaryMsg() { return new WSCatalogMsgCatalogSummary(); }
	public WSCatalogMsgCatalogsList getNewMsgCatalogsList(ICommunityHandle c) { return new WSCatalogMsgCatalogsList(c); }
	public WSCatalogMsgCatalogsList getNewMsgCatalogsList() { return new WSCatalogMsgCatalogsList(); }
	public WSCommunityMsgElementsSummaries getNewMsgGetCommunityElementsSummaries() { return new WSCommunityMsgElementsSummaries(); }
	public WSCatalogMsgSelectCatalog getNewMsgSelectCatalog() { return new WSCatalogMsgSelectCatalog(); }
	
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
