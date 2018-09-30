package metaindex.websockets;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;

import metaindex.data.community.Community;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.chat.WSChatMsgLogin;
import metaindex.websockets.protocol.chat.WSChatMsgTweet;
import metaindex.websockets.protocol.chat.WSChatProtocol;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public class ChatServer extends AMetaindexWSServer {

	private Log log = LogFactory.getLog(ChatServer.class);
	
	Map<String, IUserProfileData> usersChatMapping = new HashMap<String, IUserProfileData>();
	
	WSChatProtocol myProtocol = new WSChatProtocol();
	
	public ChatServer( int port, Community c ) throws IOException, InterruptedException {
		super(port,c);
	}

	private String getUserChatId(WebSocket conn) {
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
		
		// ask for login info
		conn.send(myProtocol.getNewLoginMsg().encode().toString());				
		
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		WSChatMsgTweet msg = new WSChatMsgTweet();
		IUserProfileData user = usersChatMapping.get(getUserChatId(conn));
		msg.setAuthor(user.getUsername());
		msg.setText("bye bye");
		this.sendToAll( msg.encode().toString());
		
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		
		IWSMessage msg = myProtocol.decode(message);
		
		if (msg.getMsgType().equals(new WSChatMsgLogin().getMsgType())) {
			handleLoginResponse((WSChatMsgLogin) msg,conn);
		}
		else if (msg.getMsgType().equals(new WSChatMsgTweet().getMsgType())) {
			handleTweetMsg((WSChatMsgTweet) msg,conn);
		}
		else {
			log.error("Received unhandle message '"+msg.getMsgType()+"'.");
			conn.close();
		}
		
	}

	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		log.error( "### "+"received fragment: " + fragment );
	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}
	
	private IUserProfileData getMsgUser(WSChatMsgLogin m,WebSocket conn) {
		String sessionId=m.getSessionId();		
		IUserProfileData user = this.getCommunity().getLoggedUserProfile(sessionId);
		if (user==null || !user.isLoggedIn()) { log.error("No user found for sessionId="+sessionId+". Closing connection."); }		
		return user;
	}

	public void handleTweetMsg(WSChatMsgTweet msg, WebSocket conn) {
		IUserProfileData user = usersChatMapping.get(getUserChatId(conn));
		if (user == null || !user.isLoggedIn()) { conn.close(); return; }
		msg.setAuthor(user.getUsername());
		this.sendToAll( msg.encode().toString());		
		
	}
	
	public void handleLoginResponse(WSChatMsgLogin msg, WebSocket conn) {
		IUserProfileData user = getMsgUser(msg,conn);
		if (user == null || !user.isLoggedIn()) { conn.close(); return; }
		
		usersChatMapping.put(getUserChatId(conn),user);
		
		WSChatMsgTweet msgTxt = new WSChatMsgTweet();
		msgTxt.setAuthor(user.getUsername());
		msgTxt.setText("Hugh");
		
		this.sendToAll( msgTxt.encode().toString());
		log.info(user.getUsername()+" started chatting.");
		
	}

	@Override
	public String getServerName() {
		return this.getCommunity().getIdName()+" chat room";
	}
}
