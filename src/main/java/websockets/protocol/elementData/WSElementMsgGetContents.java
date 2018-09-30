package metaindex.websockets.protocol.elementData;


import metaindex.websockets.protocol.AWSJsonMessage;

public class WSElementMsgGetContents extends AWSJsonMessage {
	
	Integer elementId=0;
	String sessionId=""; 
	
	@Override
	public String getMsgType() {
		return "element-getcontents";
	}
	
	public Integer getElementId() { return elementId; }
	public void setElementId(Integer newId) { elementId=newId; }
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String newSessiondId) {
		sessionId=newSessiondId;
	}
	
}
