package metaindex.app.control.websockets.catalogs.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUserDataFileUploadContents_answer implements IWsMsg_answer {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	private Integer _processingTaskId = 0;
	private Integer _clientFileId = 0;
	private Integer _clientRequestId = 0;
	private Float _progressPourcentage=0.0F;
	
	public WsMsgUserDataFileUploadContents_answer(Integer clientFileId, Integer clientRequestId, Integer processingTaskId) {
		setClientFileId(clientFileId);
		setProcessingTaskId(processingTaskId);
		setClientRequestId(clientRequestId);
	}
	
	public Integer getClientFileId() {
		return _clientFileId;
	}
	public void setClientFileId(Integer _clientFileId) {
		this._clientFileId = _clientFileId;
	}
	
	public Float getProgressPourcentage() {
		return _progressPourcentage;
	}
	public void setProgressPourcentage(Float _progressPourcentage) {
		this._progressPourcentage = _progressPourcentage;
	}
	public Integer getProcessingTaskId() {
		return _processingTaskId;
	}
	public void setProcessingTaskId(Integer _processingTaskId) {
		this._processingTaskId = _processingTaskId;
	}
	public Integer getClientRequestId() {
		return _clientRequestId;
	}
	public void setClientRequestId(Integer _clientRequestId) {
		this._clientRequestId = _clientRequestId;
	}

	
}
