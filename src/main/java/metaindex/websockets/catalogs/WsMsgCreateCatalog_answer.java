package metaindex.websockets.catalogs;

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgCreateCatalog_answer extends WsMsgCreateCatalog_request implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }	
	
	public WsMsgCreateCatalog_answer(WsMsgCreateCatalog_request request) {
		this.setCatalogName(request.getCatalogName());	
	}

}
