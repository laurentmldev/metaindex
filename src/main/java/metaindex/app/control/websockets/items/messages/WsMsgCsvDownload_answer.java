package metaindex.app.control.websockets.items.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgCsvDownload_answer implements IWsMsg_answer {

	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private String _csvFileUrl = "";
	private String _csvFileName = "";
	private Integer _requestId=0;
	private Double _csvFileSizeMB=0.0;

	public WsMsgCsvDownload_answer(WsMsgCsvDownload_request request) {
		this.setRequestId(request.getRequestId());
	}
	
	public String getCsvFileUrl() {
		return _csvFileUrl;
	}

	public void setCsvFileUrl(String csvFileUrl) {
		this._csvFileUrl = csvFileUrl;
	}
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}
	public String getCsvFileName() {
		return _csvFileName;
	}
	public void setCsvFileName(String _csvFileName) {
		this._csvFileName = _csvFileName;
	}
	public Double getCsvFileSizeMB() {
		return _csvFileSizeMB;
	}
	public void setCsvFileSizeMB(Double csvFileSizeMB) {
		this._csvFileSizeMB = csvFileSizeMB;
	}
	
}
