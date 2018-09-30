package metaindex.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsSpringTestCase;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionProxy;

import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.element.beans.BeanElementAddElementDataProcess;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.POPULATE_POLICY;
import metaindex.websockets.ThumbnailsServer;
import metaindex.websockets.protocol.AWSJsonMessage;
import metaindex.websockets.protocol.IWSMessage;
import metaindex.websockets.protocol.catalog.WSCatalogMsgCatalogSummary;
import metaindex.websockets.protocol.catalog.WSCatalogMsgCatalogSummaryRequest;
import metaindex.websockets.protocol.catalog.WSCatalogMsgElementSummary;
import metaindex.websockets.protocol.catalog.WSCatalogMsgLogin;
import metaindex.websockets.protocol.catalog.WSCatalogProtocol;
import metaindex.websockets.protocol.catalog.WSCommunityMsgElementsSummaries;
import metaindex.websockets.protocol.chat.WSChatMsgLogin;
import metaindex.websockets.protocol.chat.WSChatMsgTweet;
import metaindex.websockets.protocol.chat.WSChatProtocol;


public class TestWebSockets extends StrutsSpringTestCase {

	private static Log log = LogFactory.getLog(TestWebSockets.class);
	
	public class TestedWSJsonMessage extends AWSJsonMessage {

		String teststring="";
		Integer testinteger=0;
		Double testdouble = 0.0;
		
		@Override
		public String getMsgType() {
			return "test-msg-type";
		}
		
		public String getTestString() { return teststring; }
		public void setTestString(String str) { teststring = str; }
		public Integer getTestInteger() { return testinteger; }
		public void setTestInteger(Integer myint) { testinteger = myint; }
		public Double getTestDouble() { return testdouble; }
		public void setTestDouble(Double mydouble) { testdouble = mydouble; }
		
		
	}
	  /**
	   * Test Json message mechanisms 
	   */
	  public void testJsonMessage() throws Exception {
		  
		TestedWSJsonMessage msg = new TestedWSJsonMessage();
		msg.setTestString("plop plop");
		msg.setTestInteger(17);
		msg.setTestDouble(3.1415);
		
		String jsontxt = msg.encode().toString();
			
		String expectedjsontxt= "{\"testInteger\":17,\"msgType\":\"test-msg-type\",\"testString\":\"plop plop\",\"testDouble\":3.1415}";
	       
		assertEquals("Result returned form executing the action was not success but it should have been.", 
	    		   																		expectedjsontxt, jsontxt);
	        
		TestedWSJsonMessage newmsg = new TestedWSJsonMessage();
		
		newmsg.populate(null, new JSONObject(jsontxt), POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW);
		
		assertEquals("Json msg type does not match", 
				msg.getMsgType(), newmsg.getMsgType());
		assertEquals("Json msg string does not match", 
				msg.getTestString(), newmsg.getTestString());
		assertEquals("Json msg integer does not match", 
				msg.getTestInteger(), newmsg.getTestInteger());
		assertEquals("Json msg double does not match", 
				msg.getTestDouble(), newmsg.getTestDouble());

	    }
	
	  /**
	   * Test metaindex chat protocol
	   */
	  public void testChatProtocol() {
		  WSChatProtocol chatproto = new WSChatProtocol();
		  
		  // Logion msg
		  String sessionId="tagada tsoin tsoin";
		  WSChatMsgLogin loginmsg = chatproto.getNewLoginMsg();
		  loginmsg.setSessionId(sessionId);		  
		  String encodedStr = loginmsg.encode().toString();
		  IWSMessage msg = chatproto.decode(encodedStr);		  
		  assertEquals("Chat protocol could not decode login msg", 
					loginmsg.getMsgType(), msg.getMsgType());

		  // Tweet msg
		  String msgText="Plop tagada lorem ipsum text.";
		  WSChatMsgTweet tweetmsg = chatproto.getNewTweetMsg();
		  tweetmsg.setText(msgText);
		  encodedStr = tweetmsg.encode().toString();
		  msg = chatproto.decode(encodedStr);		  
		  assertEquals("Chat protocol could not decode tweet msg", 
				  tweetmsg.getMsgType(), msg.getMsgType());

	  }
	  
	  /**
	   * Test catalog protocol
	 * @throws DataReferenceErrorException 
	 * @throws DataAccessConstraintException 
	 * @throws DataAccessErrorException 
	   */
	  public void testCatalogProtocol() throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		  
		  String communityIdName="Test Community";
		  ActionProxy proxy = getActionProxy("/openCommunity");
		  CommunitiesAccessor.reset();		      
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
		  BeanCommunity processAction = (BeanCommunity) proxy.getAction();
		  processAction.setValidationActiveUser("testuser");
		  processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));
		  processAction.getSelectedCommunity().updateFull();
	        
		  WSCatalogProtocol catalogproto = new WSCatalogProtocol();
		  
		  // Logion msg
		  String sessionId="tagada tsoin tsoin";
		  WSCatalogMsgLogin loginmsg = catalogproto.getNewLoginMsg();
		  loginmsg.setSessionId(sessionId);		  
		  String encodedStr = loginmsg.encode().toString();
		  IWSMessage msg = catalogproto.decode(encodedStr);		  
		  assertEquals("Catalog protocol could not decode login msg", 
					loginmsg.getMsgType(), msg.getMsgType());

		  // Catalog Summary
		  String expectedStrCat="{\"catalogName\":\"Tous\",\"virtual\":true,\"catalogId\":0,\"msgType\":\"catalog-summary\",\"catalogComment\":\"Tous les &eacute;l&eacute;ments de la communaut&eacute;\",\"selectedElementId\":42516,\"dynamic\":true,\"dynamicElementsFilter\":\"*\",\"selected\":true,\"catalogNbElements\":5,\"staticElementsIds\":[]}";
		  
		  WSCatalogMsgCatalogSummary catSum = catalogproto.getNewCatalogSummaryMsg(processAction.getSelectedCatalog());
		  String encodedJsonString = catSum.encode().toString();
		  //log.error("### catSum="+encodedJsonString);
		  //log.error("### expect="+expectedStrCat);
		  // Here we just compare the length, because order in which the fields are put in the JSON string
		  // vary from one run to another ...
		  assertEquals("Catalog summary JSON string does not match expected result", encodedJsonString.length(), expectedStrCat.length());
		  
		  // Element Message		  
		  String expectedStrEl="{\"elementId\":42516,\"template\":false,\"searchText\":\"42516 - A new test element;A comment for this element\",\"msgType\":\"catalog-elementsummary\",\"templated\":false,\"elementComment\":\"A comment for this element\",\"templateLoadError\":false,\"selected\":true,\"elementName\":\"A new test element\",\"thumbnailUrl\":\"\"}";
		                        
		  WSCatalogMsgElementSummary elMsg = 
				  catalogproto.getNewElementSummaryMsg(processAction.getSelectedElement(),processAction.getLoggedUserProfile());
		  encodedJsonString=elMsg.encode().toString();
		  //log.error("### eleSum="+encodedJsonString);
		  //log.error("### expect="+expectedStrEl);
		  // Here we just compare the length, because order in which the fields are put in the JSON string
		  // vary from one run to another ...		
		  //log.error("#### got string : "+elMsg.encode().toString());
		  assertEquals("Element summary JSON string does not match expected result", 
				  elMsg.encode().toString().length(), expectedStrEl.length());
		  //log.error("### elMsg="+elMsg.encode().toString());
		  
	  }
	 
public void testCatalogServer() throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException, IOException, InterruptedException, URISyntaxException {
		  
			class TestWSClient extends WebSocketClient {
				
				WSCatalogProtocol myProtocol = new WSCatalogProtocol();
				
				public WSCatalogMsgCatalogSummary catalogSummary = null;
				public List<WSCatalogMsgElementSummary> elements = new ArrayList<WSCatalogMsgElementSummary>();
				public Integer nbUnhandledMessages = 0;
				
				public TestWSClient(URI serverURI) {
					super(serverURI);
					
				}
				
				@Override
				public void onOpen(ServerHandshake handshakedata) {
					log.error("WSClient : UTest WS client connected to "+this.getConnection().getRemoteSocketAddress().toString());		
					
				}

				@Override
				public void onMessage(String message) {
					//log.error("### Client : Received msg : "+message);
					IWSMessage msg = myProtocol.decode(message);
					if (msg.getMsgType().equals(myProtocol.getNewLoginMsg().getMsgType())) {
						//log.error("### Client : login request");
						handleLoginResponse((WSCatalogMsgLogin)msg);
						
						// 1. sending get catalog summary request
						WSCatalogMsgCatalogSummaryRequest msgCatSum = new WSCatalogMsgCatalogSummaryRequest(0);
						this.send(msgCatSum.encode().toString());
						
						// sending getCommunityElementsSummaries
						WSCommunityMsgElementsSummaries getElementSummaries = new WSCommunityMsgElementsSummaries();
						this.send(getElementSummaries.encode().toString());
						
						// TODO CatalogsList
					}
					// 1.Catalog summary 
					else if (msg.getMsgType().equals(new WSCatalogMsgCatalogSummary().getMsgType())) {
						catalogSummary = (WSCatalogMsgCatalogSummary)msg;							
					}
					// 2.Element summary (x5)
					else if (msg.getMsgType().equals(new WSCatalogMsgElementSummary().getMsgType())) {
						elements.add((WSCatalogMsgElementSummary)msg);						
					}

					
					else {
						log.error("Client : unhandled message : "+message);
						nbUnhandledMessages++;
					}
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					log.error("WSClient : Closing WS Client");
					
				}

				@Override
				public void onError(Exception ex) {
					// TODO Auto-generated method stub
					
				}
				
				private void handleLoginResponse(WSCatalogMsgLogin msg) {
					msg.setSessionId(ThumbnailsServer.VALIDATION_SESSION_ID);
					log.error("WSClient : sending login response for user registration : "+msg.encode().toString());
					
					this.send(msg.encode().toString());			
				}
				
			}
			
		  int port = 12345;
		  String communityIdName="Test Community";
		  ActionProxy proxy = getActionProxy("/openCommunity");
		  CommunitiesAccessor.reset();		      
		  ICommunity c = CommunitiesAccessor.getCommunity(communityIdName);
		    
		  BeanCommunity processAction = (BeanCommunity) proxy.getAction();		  
		  processAction.setValidationActiveUser("testuser");
		  processAction.setSelectedCommunity(new CommunityHandle(processAction.getLoggedUserProfile(),c));		  
		  processAction.getSelectedCommunity().updateFull();
		  
		  ThumbnailsServer myWSCatalogServer = new ThumbnailsServer(port, c);
		  
		  TestWSClient myClient = new TestWSClient(new URI("ws","utest-user","localhost",port,"/","",""));

		  myClient.connect();
		  Thread.sleep(500);
		  myClient.close();
		  myWSCatalogServer.stop();
		  
		  assertNotNull("Catalog summary not received", myClient.catalogSummary);
		  
		  assertEquals("Catalog summary does not seem properly received", "Tous", myClient.catalogSummary.getCatalogName());
		  
		  assertEquals("Number of received elements-summary is not as expected", 6, myClient.elements.size());
		  
		  assertEquals("Received some unhandled messages", new Integer(0), myClient.nbUnhandledMessages);
}

}
