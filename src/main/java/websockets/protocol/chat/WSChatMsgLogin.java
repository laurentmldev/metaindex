package metaindex.websockets.protocol.chat;


import metaindex.websockets.protocol.AWSJsonMessage;


public class WSChatMsgLogin extends AWSJsonMessage {

	
	String sessionId = "";
	
	@Override
	public String getMsgType() {
		return "chat-login";
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String newSessiondId) {
		sessionId=newSessiondId;
	}
}
