package metaindex.app.control.websockets.catalogs.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgJoinCatalog_answer extends WsMsgJoinCatalog_request implements IWsMsg_answer  {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	public WsMsgJoinCatalog_answer(WsMsgJoinCatalog_request request) {
		this.setRequestId(request.getRequestId());
		this.setCatalogId(request.getCatalogId());
	}


}
