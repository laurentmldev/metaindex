package metaindex.app.control.websockets.admin.messages;

import java.util.ArrayList;
import java.util.List;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgGetMonitoringInfo_answer extends WsMsgGetMonitoringInfo_request implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	private Integer _nbThreads=0;
	
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }	
	
	private List<Integer> _activeUsersIdList=new ArrayList<>();
	private List<String> _runningTasksNameList=new ArrayList<>();
	
	public WsMsgGetMonitoringInfo_answer(WsMsgGetMonitoringInfo_request request) {
			
	}
	public List<Integer> getActiveUsersIdList() {
		return _activeUsersIdList;
	}
	public void setActiveUsersIdList(List<Integer> activeUsersIdList) {
		this._activeUsersIdList = activeUsersIdList;
	}
	public List<String> getRunningTasksNameList() {
		return _runningTasksNameList;
	}
	public void setRunningTasksNameList(List<String> runningTasksNameList) {
		this._runningTasksNameList = runningTasksNameList;
	}
	public Integer getNbThreads() {
		return _nbThreads;
	}
	public void setNbThreads(Integer nbThreads) {
		this._nbThreads = nbThreads;
	}
	

}
