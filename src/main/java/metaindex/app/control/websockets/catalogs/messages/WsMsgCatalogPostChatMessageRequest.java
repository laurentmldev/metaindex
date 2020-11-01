package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgCatalogPostChatMessageRequest  {
			
	private Integer _catalogId=0;
	private String _chatMessage;
	
	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}

	public String getChatMessage() {
		return _chatMessage;
	}

	public void setChatMessage(String _chatMessage) {
		this._chatMessage = _chatMessage;
	}

}
