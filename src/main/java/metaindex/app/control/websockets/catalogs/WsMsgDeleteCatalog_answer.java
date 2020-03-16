package metaindex.app.control.websockets.catalogs;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgDeleteCatalog_answer extends WsMsgDeleteCatalog_request implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	public WsMsgDeleteCatalog_answer(WsMsgDeleteCatalog_request request) {
		this.setRequestId(request.getRequestId());
		this.setCatalogId(request.getCatalogId());
	}


}
