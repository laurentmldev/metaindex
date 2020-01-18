package metaindex.websockets.items;

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgFileUploadContents_answer implements IWsMsg_answer {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private Integer _clientFileId = 0;
	private Float _progressPourcentage=0.0F;
	
	WsMsgFileUploadContents_answer(Integer clientFileId) {
		setClientFileId(clientFileId);
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

	
}
