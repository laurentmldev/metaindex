package metaindex.app.control.websockets.items.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgGraphDownload_answer implements IWsMsg_answer {

	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private String _graphFileUrl = "";
	private String _graphFileName = "";
	private Integer _requestId=0;
	private Double _graphFileSizeMB=0.0;

	public WsMsgGraphDownload_answer(WsMsgGraphDownload_request request) {
		this.setRequestId(request.getRequestId());
	}
	public WsMsgGraphDownload_answer(WsMsgGraphDownloadGroupBy_request request) {
		this.setRequestId(request.getRequestId());
	}
	
	public String getGraphFileUrl() {
		return _graphFileUrl;
	}

	public void setGraphFileUrl(String graphFileUrl) {
		this._graphFileUrl = graphFileUrl;
	}
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}
	public String getGraphFileName() {
		return _graphFileName;
	}
	public void setGraphFileName(String _graphFileName) {
		this._graphFileName = _graphFileName;
	}
	public Double getGraphFileSizeMB() {
		return _graphFileSizeMB;
	}
	public void setGraphFileSizeMB(Double graphFileSizeMB) {
		this._graphFileSizeMB = graphFileSizeMB;
	}
	
}
