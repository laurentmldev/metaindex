package metaindex.app.control.websockets.catalogs.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgSelectCatalog_answer extends WsMsgSelectCatalog_request implements IWsMsg_answer  {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	public WsMsgSelectCatalog_answer(WsMsgSelectCatalog_request request) {
		this.setCatalogId(request.getCatalogId());
	}
	
}
