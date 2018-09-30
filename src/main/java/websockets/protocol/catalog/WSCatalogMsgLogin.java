package metaindex.websockets.protocol.catalog;


import metaindex.websockets.protocol.chat.WSChatMsgLogin;


public class WSCatalogMsgLogin extends WSChatMsgLogin {
	
	@Override
	public String getMsgType() {
		return "catalog-login";
	}
	
}
