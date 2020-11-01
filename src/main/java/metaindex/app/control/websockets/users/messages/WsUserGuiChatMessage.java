package metaindex.app.control.websockets.users.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogChatMsg;

public class WsUserGuiChatMessage  {
		
	
	private Integer _catalogId=0;
	private List<ICatalogChatMsg> _chatMessages;
	
	public WsUserGuiChatMessage(ICatalog c, List<ICatalogChatMsg> messages) {
		setCatalogId(c.getId());
		setChatMessages(messages);
	}

	public List<ICatalogChatMsg> getChatMessages() {
		return _chatMessages;
	}

	public void setChatMessages(List<ICatalogChatMsg> _chatMessages) {
		this._chatMessages = _chatMessages;
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}


}
