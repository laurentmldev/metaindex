package metaindex.websockets.items;

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgFileUpload_answer implements IWsMsg_answer {

	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	private int processingTaskId = 0;
	private int clientFileId = 0;

	public WsMsgFileUpload_answer(int processingTaskId,int clientFileId) {
		this.processingTaskId=processingTaskId;
		this.clientFileId=clientFileId;
	}
	public int getProcessingTaskId() {
		return processingTaskId;
	}

	public void setProcessingTaskId(int processingTaskId) {
		this.processingTaskId = processingTaskId;
	}
	public int getClientFileId() {
		return clientFileId;
	}
	public void setClientFileId(int clientFileId) {
		this.clientFileId = clientFileId;
	}
	
}
