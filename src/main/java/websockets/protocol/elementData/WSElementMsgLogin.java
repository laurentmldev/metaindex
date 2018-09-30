package metaindex.websockets.protocol.elementData;


import metaindex.websockets.protocol.catalog.WSCatalogMsgLogin;



public class WSElementMsgLogin extends WSCatalogMsgLogin {
	
	@Override
	public String getMsgType() {
		return "element-login";
	}
	
}
