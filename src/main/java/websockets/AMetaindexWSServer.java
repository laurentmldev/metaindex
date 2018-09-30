package metaindex.websockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import metaindex.data.community.ICommunity;
import metaindex.data.userprofile.UserProfileData;
import metaindex.websockets.protocol.IWSProtocol;
import metaindex.websockets.protocol.chat.WSChatMsgLogin;
import metaindex.websockets.protocol.chat.WSChatProtocol;

/**
 * A simple WebSocketServer implementation. Keeps track of a "chatroom".
 */
public abstract class AMetaindexWSServer extends WebSocketServer {

	private Log log = LogFactory.getLog(AMetaindexWSServer.class);
	
	private ICommunity myCommunity = null;
	
	public AMetaindexWSServer(int port, ICommunity community ) throws IOException, InterruptedException {
		super( new InetSocketAddress( port ) );
		myCommunity = community;
		this.start();		
	}

	public abstract String getServerName();
	
	public String getServerUri() {
		return getAddress().toString();
	}
	public ICommunity getCommunity() { return myCommunity; }
	
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * 
	 * @param text
	 *            The String to send across the network.
	 * @throws InterruptedException
	 *             When socket related I/O errors occur.
	 */
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
	
}
