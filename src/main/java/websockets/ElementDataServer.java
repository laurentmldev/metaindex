package metaindex.websockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.catalog.WSCatalogMsgCatalogSummary;
import metaindex.websockets.protocol.catalog.WSCatalogMsgElementSummary;
import metaindex.websockets.protocol.catalog.WSCatalogMsgLogin;
import metaindex.websockets.protocol.catalog.WSCatalogProtocol;
import metaindex.websockets.protocol.chat.WSChatMsgLogin;
import metaindex.websockets.protocol.chat.WSChatMsgTweet;
import metaindex.websockets.protocol.chat.WSChatProtocol;
import metaindex.websockets.protocol.elementData.WSElementDataProtocol;
import metaindex.websockets.protocol.elementData.WSElementMsgDatasetContents;
import metaindex.websockets.protocol.elementData.WSElementMsgElementContents;
import metaindex.websockets.protocol.elementData.WSElementMsgGetContents;
import metaindex.websockets.protocol.elementData.WSElementMsgLogin;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ElementDataServer extends AMetaindexWSServer {

	private class WSConnectionNotOpenException extends Exception {
		private static final long serialVersionUID = 5429014437509716923L;		
	}
	private Log log = LogFactory.getLog(ElementDataServer.class);
	
	private static final int delay_between_send_elem_summary_ms = 0;
	private static final int delay_between_send_elem_summary_ns = 100000;
	public static final String VALIDATION_SESSION_ID="TESTER_SESSION_ID";
	Map<String, IUserProfileData> usersMapping = new HashMap<String, IUserProfileData>();
	
	WSElementDataProtocol myProtocol = new WSElementDataProtocol();
	
	public ElementDataServer( int port, ICommunity c ) throws IOException, InterruptedException {
		super(port,c);
		//log.error("### Server port = "+port);
	}

	
	private String getUserWSId(WebSocket conn) throws WSConnectionNotOpenException {
		if (conn.isClosed()) { throw new WSConnectionNotOpenException(); }		
		return conn.getRemoteSocketAddress().getHostString()+":"+conn.getRemoteSocketAddress().getPort();
	}
	
	
	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		/*		
		Iterator<String> it = handshake.iterateHttpFields(); 
		while (it.hasNext()) {
			String curField=it.next();
			log.error("### httpField '"+curField+"' = "+handshake.getFieldValue(curField));
		}*/
		log.error("### opened connection to ElementDataServer");
		// ask for login info
		conn.send(myProtocol.getNewLoginMsg().encode().toString());				
		
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		// nothing special to do
		try {
			usersMapping.remove(getUserWSId(conn));			
			//log.info("closed catalog server connection with "+getUserWSId(conn));
		} catch (WSConnectionNotOpenException e) {
			// do nothing
		}
		
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		
		// /!\ ATTENTION - No Debug in this class, it seems to break connexion
		
		//log.error("#### received msg : "+message);
		IWSMessage msg = myProtocol.decode(message);
		try {			
			if (msg.getMsgType().equals(myProtocol.getNewLoginMsg().getMsgType())) {
				handleLoginResponse((WSElementMsgLogin) msg,conn);			
			}
			else if (msg.getMsgType().equals(myProtocol.getNewMsgGetContents().getMsgType())) {
				handleGetElementContentsRequestMsg((WSElementMsgGetContents) msg,conn);
			}
			else {
				log.error("Received unhandled message '"+msg.getMsgType()+"'. Closing connection.");
				conn.close();
			}
		} catch (WSConnectionNotOpenException e) {
			log.error("Received msg while conn was not open (anymore)");
		}
	}

	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		//log.error( "### "+"received fragment: " + fragment );
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
	
	private IUserProfileData getMsgUser(WSCatalogMsgLogin m,WebSocket conn) {
		String sessionId=m.getSessionId();	
		
		// check for validation test user
		if (sessionId.equals(VALIDATION_SESSION_ID) && conn.getRemoteSocketAddress().getHostName().equals("localhost")) {
			// create fake user with 'null' action support.
			// action support is only used for GUI : it offers getText with proper language.
			// this is not needed for the validation used
			IUserProfileData testuser = new UserProfileData();	
			testuser.setUsername("Test User");
			testuser.setSessionId(VALIDATION_SESSION_ID);
			this.getCommunity().enter(testuser);
		}
		
		IUserProfileData user = this.getCommunity().getLoggedUserProfile(sessionId);
		if (user==null || !user.isLoggedIn()) { log.error("No logged user found for sessionId="+sessionId+". Closing connection."); }		
		return user;
	}

	private void handleGetElementContentsRequestMsg(WSElementMsgGetContents msg, WebSocket conn)
			throws WSConnectionNotOpenException {
		
		//log.error("#### server received request for catalog "+msg.getCatalogId());
		IUserProfileData user = usersMapping.get(getUserWSId(conn));
		if (user == null || !user.isLoggedIn()) {
			log.error("While handling element contents request : Unilaterally closing connection with client");
			conn.close(); 
			return; 
		}		
		
		// Element data
		//log.error("### Sending element "+msg.getElementId()+" data");
		if (msg.getElementId()!=0) { 
			try {				
				user.getSelectedCommunity().getSelectedCatalog().setSelectedElement(msg.getElementId());
				//log.error("### new selected element is "+msg.getElementId());
			} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		} 
		
		// there might be no selected element if community is fully empty
		IElementHandle e = user.getSelectedElement();
		if (e!=null) {
			
			WSElementMsgElementContents m = myProtocol.getNewElementContentsMsg(e,user);
			conn.send(m.encode().toString());

			// Element's datasets 
			// we group them as a unique bigger message to ensure they arrive in expected order
			//log.error("### Sending datasets of element "+msg.getElementId()+" data");
			
			Iterator<IDatasetHandle> itDatasets = e.getDatasets().iterator();
			String jsonDatasetsDescr = "{ \"msgType\" : \"element-datasets\", \"datasets\" : [] }";
			JSONObject myGroupedDatasetsMessage = new JSONObject(jsonDatasetsDescr);
			JSONArray datasetsArray = myGroupedDatasetsMessage.getJSONArray("datasets");
			
			while (itDatasets.hasNext()) {
	
				IDatasetHandle d = itDatasets.next();
				//log.info("	sending dataset "+d.getName()+" "+d.getNbMetadata());
				JSONObject datasetJsonDataMsg = myProtocol.getNewDatasetContentsMsg(d,user).encode();
				
				// and embedded metadata
				JSONArray metadatasArray = new JSONArray();
				Iterator<IMetadataHandle> itMetadata = d.getMetadata().iterator();
				while (itMetadata.hasNext()) {
					IMetadataHandle metadata = itMetadata.next();
					//log.info("		sending metadata "+metadata.getName());
					IWSMessage metadataMsg = myProtocol.getNewMetadataContentsMsg(metadata,user);
					if (metadataMsg==null) {
						log.warn("Unhandled type for metadata "+metadata.getMetadataId()+" '"+metadata.getName()+"' WebSocket send, skipped.");
					} else {
						datasetJsonDataMsg.append("metadatas", metadataMsg.encode());					
					}				
				}
				datasetsArray.put(datasetJsonDataMsg);			
			}		
			conn.send(myGroupedDatasetsMessage.toString());
		}
	}
	
	
	private void handleLoginResponse(WSElementMsgLogin msg, WebSocket conn) throws WSConnectionNotOpenException {
		IUserProfileData user = getMsgUser(msg,conn);
		if (user == null || !user.isLoggedIn()) {
			log.error("While handling login : unilaterally closing connection with client");			
			conn.close(); 
			return; 
		}
		String userWSId=getUserWSId(conn);
		usersMapping.put(userWSId,user);
		log.info(user.getUsername()+" connected to Catalogs contents WS server.");
		
	}

	@Override
	public String getServerName() {
		return this.getCommunity().getIdName()+" elements contents server.";
	}
}
