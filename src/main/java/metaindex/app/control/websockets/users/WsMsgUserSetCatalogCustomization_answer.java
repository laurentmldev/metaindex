package metaindex.app.control.websockets.users;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUserSetCatalogCustomization_answer extends WsMsgUserSetCatalogCustomization_request implements IWsMsg_answer  {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	public WsMsgUserSetCatalogCustomization_answer(WsMsgUserSetCatalogCustomization_request request) {
		this.setUserId(request.getUserId());
		this.setCatalogId(request.getCatalogId());
		this.setKibanaIFrame(request.getKibanaIFrame());
		this.setRequestId(request.getRequestId());
	}
}
