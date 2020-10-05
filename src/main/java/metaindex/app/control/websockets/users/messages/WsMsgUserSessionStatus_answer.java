package metaindex.app.control.websockets.users.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUserSessionStatus_answer  {
		
	public static enum SESSION_STATUS { VALID, EXPIRED };
	
	private SESSION_STATUS _sessionStatus=SESSION_STATUS.VALID;
	
	public WsMsgUserSessionStatus_answer(SESSION_STATUS status) {
		setSessionStatus(status);
		
	}

	public SESSION_STATUS getSessionStatus() {
		return _sessionStatus;
	}

	public void setSessionStatus(SESSION_STATUS _sessionStatus) {
		this._sessionStatus = _sessionStatus;
	}	
	
}
