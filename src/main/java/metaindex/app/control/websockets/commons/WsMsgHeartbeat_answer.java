package metaindex.app.control.websockets.commons;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;

public class WsMsgHeartbeat_answer{	
	
	private Integer _count=0;
	private APPLICATION_STATUS _applicationStatus;
	
	WsMsgHeartbeat_answer() { 
		_count++;
		this.setApplicationStatus(Globals.Get().getApplicationStatus());
	}
	
	public Integer getCount() { return _count; }
	public void incrCount() { _count++; }

	public APPLICATION_STATUS getApplicationStatus() {
		return _applicationStatus;
	}

	public void setApplicationStatus(APPLICATION_STATUS _applicationStatus) {
		this._applicationStatus = _applicationStatus;
	}
	
	
}
