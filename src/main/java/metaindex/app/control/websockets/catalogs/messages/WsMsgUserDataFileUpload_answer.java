package metaindex.app.control.websockets.catalogs.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUserDataFileUpload_answer implements IWsMsg_answer {

	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	private int _processingTaskId = 0;
	private int _requestId = 0;

	public WsMsgUserDataFileUpload_answer(int processingTaskId,int requestId) {
		this._processingTaskId=processingTaskId;
		this._requestId=requestId;
	}
	public int getProcessingTaskId() {
		return _processingTaskId;
	}

	public void setProcessingTaskId(int processingTaskId) {
		this._processingTaskId = processingTaskId;
	}
	public int getRequestId() {
		return _requestId;
	}
	public void setRequestId(int requestId) {
		this._requestId = requestId;
	}
	
}
