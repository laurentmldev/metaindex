package metaindex.app.control.websockets.perspectives;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUpdatePerspective_answer extends WsMsgUpdatePerspective_request  implements IWsMsg_answer   {

	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }	
	
	public WsMsgUpdatePerspective_answer(WsMsgUpdatePerspective_request request) {
		this.setJsonDef(request.getJsonDef());
		this.setPerspectiveId(request.getPerspectiveId());
		this.setRequestId(request.getRequestId());
		this.setCatalogId(request.getCatalogId());
	}
}
