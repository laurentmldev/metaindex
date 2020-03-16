package metaindex.app.control.websockets.commons;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.commons.globals.Globals;
import metaindex.data.commons.globals.Globals.APPLICATION_STATUS;

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
