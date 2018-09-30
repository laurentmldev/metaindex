package metaindex.websockets.protocol.chat;


import metaindex.websockets.protocol.AWSJsonMessage;


public class WSChatMsgTweet extends AWSJsonMessage {

	String text = "";
	String author = "";
	
	@Override
	public String getMsgType() {
		return "chat-tweet";
	}

	public String getText() {
		return text;
	}
	public void setText(String newText) {
		text=newText;
	}
	
	public String getAuthor() { return author; }
	public void setAuthor(String myauthor) { author = myauthor; }
}
