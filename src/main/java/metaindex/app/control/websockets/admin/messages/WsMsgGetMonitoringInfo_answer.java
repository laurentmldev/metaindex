package metaindex.app.control.websockets.admin.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgGetMonitoringInfo_answer extends WsMsgGetMonitoringInfo_request implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }	
	
	private Integer _nbActiveUsers=0;
	private Integer _nbRunningProcessingTasks=0;
	
	public WsMsgGetMonitoringInfo_answer(WsMsgGetMonitoringInfo_request request) {
			
	}
	public Integer getNbActiveUsers() {
		return _nbActiveUsers;
	}
	public void setNbActiveUsers(Integer nbActiveUsers) {
		this._nbActiveUsers = nbActiveUsers;
	}
	public Integer getNbRunningProcessingTasks() {
		return _nbRunningProcessingTasks;
	}
	public void setNbRunningProcessingTasks(Integer nbRunningProcessingTasks) {
		this._nbRunningProcessingTasks = nbRunningProcessingTasks;
	}

}
