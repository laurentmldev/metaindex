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
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.catalog.WSCatalogMsgCatalogSummary;
import metaindex.websockets.protocol.catalog.WSCatalogMsgCatalogsList;
import metaindex.websockets.protocol.catalog.WSCatalogMsgElementSummary;
import metaindex.websockets.protocol.catalog.WSCatalogMsgLogin;
import metaindex.websockets.protocol.catalog.WSCatalogMsgSelectCatalog;
import metaindex.websockets.protocol.catalog.WSCatalogProtocol;
import metaindex.websockets.protocol.catalog.WSCommunityMsgElementsSummaries;
import metaindex.websockets.protocol.chat.WSChatMsgLogin;
import metaindex.websockets.protocol.chat.WSChatMsgTweet;
import metaindex.websockets.protocol.chat.WSChatProtocol;

/**
 * Server in charge of sending catalogs and thumbnails data to clients through web-sockets.
 */
public class ThumbnailsServer extends AMetaindexWSServer {

	private class WSConnectionNotOpenException extends Exception {
		private static final long serialVersionUID = 5429014437509716923L;		
	}
	private Log log = LogFactory.getLog(ThumbnailsServer.class);
	
	public static final String VALIDATION_SESSION_ID="TESTER_SESSION_ID";
	Map<String, IUserProfileData> usersMapping = new HashMap<String, IUserProfileData>();
	
	private static final Integer THUMBNAIL_SEND_INTERSLEEP_MS = 5;
	private static final Integer NB_ELS_PER_WS_MSG = 50;
	
	WSCatalogProtocol myProtocol = new WSCatalogProtocol();
	
	public ThumbnailsServer( int port, ICommunity c ) throws IOException, InterruptedException {
		super(port,c);
		log.error("### "+this.getCommunity().getIdName() + " Server port = "+port);
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
		log.error("### opened connection");
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
				handleLoginResponse((WSCatalogMsgLogin) msg,conn);			
			}
			else if (msg.getMsgType().equals(myProtocol.getNewMsgCatalogsList().getMsgType())) {
				handleGetCatalogsListRequest(conn);
			}
			else if (msg.getMsgType().equals(myProtocol.getNewCatalogSummaryMsg().getMsgType())) {
				handleGetCatalogSummaryRequest((WSCatalogMsgCatalogSummary) msg, conn);
			}
			else if (msg.getMsgType().equals(myProtocol.getNewMsgGetCommunityElementsSummaries().getMsgType())) {
				handleGetCommunityElementsSummariesRequest((WSCommunityMsgElementsSummaries) msg,conn);
			}
			else if (msg.getMsgType().equals(myProtocol.getNewMsgSelectCatalog().getMsgType())) {
				handleSelectCatalogRequest((WSCatalogMsgSelectCatalog) msg, conn);
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
		if (user==null) { log.error("No user found for sessionId="+sessionId+". Closing connection."); }		
		return user;
	}
	
	private void handleGetCommunityElementsSummariesRequest(WSCommunityMsgElementsSummaries msg, WebSocket conn) throws WSConnectionNotOpenException {
				
		//log.error("#### server received request for community elements request.");
		IUserProfileData user = usersMapping.get(getUserWSId(conn));
		if (user == null || !user.isLoggedIn()) {
			log.error("While handling catalog contents request : Unilaterally closing connection with client");
			conn.close(); 
			return; 
		}		
		msg.setCommunityNbElements(this.getCommunity().getNbElements());
		conn.send(msg.encode().toString());
		// leave time to the community msg to go before sending the elements summaries 
		try { Thread.sleep(THUMBNAIL_SEND_INTERSLEEP_MS); } catch (InterruptedException e) { e.printStackTrace(); }
		sendCommunityElementsSummaries(conn, user);		
	}

	private void sendCommunityElementsSummaries(WebSocket conn, IUserProfileData user) {
		
		// send elements summaries goruped by packets instead of one by one
		// for faster data transfer
		String jsonElementsDescr = "{ \"msgType\" : \"elements-summaries\", \"elements\" : [] }";
		JSONObject myGroupedElementsMessage = new JSONObject(jsonElementsDescr);
		JSONArray elementsArray = myGroupedElementsMessage.getJSONArray("elements");
		
		
		//log.error("### nb elements = "+c.getElementsCount());
		
		Iterator<IElementHandle> it = user.getSelectedCommunity().getElements().iterator();
		while (it.hasNext()) {
			IElementHandle curEl = it.next();			
			//log.error("#### adding summary of element "+curEl.getElementId());
			elementsArray.put(myProtocol.getNewElementSummaryMsg(curEl,user).encode());			
			if (elementsArray.length()==NB_ELS_PER_WS_MSG) {
				//log.error("#### sending summaries of "+elementsArray.length()+" elements");				
				conn.send((myGroupedElementsMessage.toString()));
				myGroupedElementsMessage = new JSONObject(jsonElementsDescr);
				elementsArray = myGroupedElementsMessage.getJSONArray("elements");
				try { Thread.sleep(THUMBNAIL_SEND_INTERSLEEP_MS); } catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		if (elementsArray.length()>0) {
			//log.error("#### sending summaries of "+nbElsInCurMsg+" elements");
			conn.send((myGroupedElementsMessage.toString()));
		}
		
		//log.error("### Sending elements summaries : "+msgList.size());
	}
	

	private void handleGetCatalogsListRequest(WebSocket conn) throws WSConnectionNotOpenException {
		
		IUserProfileData user = usersMapping.get(getUserWSId(conn));
		if (user == null || !user.isLoggedIn()) {
			log.error("While handling catalog contents request : Unilaterally closing connection with client");
			conn.close(); 
			return; 
		}		

		//log.error("### Performing send of catalogsds list: ");
		WSCatalogMsgCatalogsList catsIdsMsg = this.myProtocol.getNewMsgCatalogsList(user.getSelectedCommunity());
		conn.send(catsIdsMsg.encode().toString());
		
	}
	
	private void handleGetCatalogSummaryRequest(WSCatalogMsgCatalogSummary msg, WebSocket conn)
			 												throws WSConnectionNotOpenException {
		IUserProfileData user = usersMapping.get(getUserWSId(conn));
		if (user == null || !user.isLoggedIn()) {
			if (user==null) { log.error("While handling catalog contents request : Pirate! (unidentified user). Unilaterally closing connection with client"); }
			else  if (!user.isLoggedIn()) { log.error("While handling catalog contents request : user '"+user.getUsername()+"' not logged (anymore). Unilaterally closing connection with client"); }
			conn.close(); 
			return; 
		}		

		//log.error("### Performing send of catalog summary "+msg.getCatalogId());
		ICatalogHandle c = user.getSelectedCommunity().getCatalog(msg.getCatalogId());
		WSCatalogMsgCatalogSummary catsSummaryMsg = this.myProtocol.getNewCatalogSummaryMsg(c);
		conn.send(catsSummaryMsg.encode().toString());
		
	}
	
	
	private void handleSelectCatalogRequest(WSCatalogMsgSelectCatalog msg, WebSocket conn)
				throws WSConnectionNotOpenException {
		IUserProfileData user = usersMapping.get(getUserWSId(conn));
		if (user == null || !user.isLoggedIn()) {
		log.error("While handling catalog contents request : Unilaterally closing connection with client");
		conn.close(); 
		return; 
		}		
		
		ICatalogHandle c = user.getSelectedCommunity().getCatalog(msg.getCatalogId());
		
		try {
			//log.error("### Switching selected catalog to "+c.getCatalogId());
			user.getSelectedCommunity().setSelectedCatalog(c);
		} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
			log.error("While handling catalog select request : Unilaterally closing connection with client. Error : "+e.getMessage());
			conn.close();
		}
		
		handleGetCatalogsListRequest(conn);
		
	}
	
	
	
	
	private void handleLoginResponse(WSCatalogMsgLogin msg, WebSocket conn) throws WSConnectionNotOpenException {
		IUserProfileData user = getMsgUser(msg,conn);
		if (user == null) {
			log.error("While handling login : unilaterally closing connection with client");			
			conn.close(); 
			return; 
		}
		user.logIn(); 
		String userWSId=getUserWSId(conn);
		usersMapping.put(userWSId,user);
		log.info(user.getUsername()+" connected to Catalogs contents WS server.");
		
	}

	@Override
	public String getServerName() {
		return this.getCommunity().getIdName()+" catalogs contents server.";
	}
}
