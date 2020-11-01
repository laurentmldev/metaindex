package metaindex.app.control.websockets.catalogs.messages;

import java.util.List;

import metaindex.app.control.websockets.commons.IWsMsg_answer;
import metaindex.data.catalog.ICatalogChatMsg;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgCatalogChatHistoryAnswer extends WsMsgCatalogChatHistoryRequest implements IWsMsg_answer  {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private List<ICatalogChatMsg> _chatHistory=null;
	
	public WsMsgCatalogChatHistoryAnswer(WsMsgCatalogChatHistoryRequest req) {
		this.setRequestId(req.getRequestId());
		this.setCatalogId(req.getCatalogId());
	}
	public List<ICatalogChatMsg> getChatHistory() {
		return _chatHistory;
	}

	public void setChatHistory(List<ICatalogChatMsg> _chatHistory) {
		this._chatHistory = _chatHistory;
	}

	
}
