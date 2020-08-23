package metaindex.app.control.websockets.users.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateUserPlan_request  {
	
	private Integer _userId;
	private Integer _requestId;	
	private Integer _planId;
	
	public Integer getRequestId() { return _requestId; }
	public void setRequestId(Integer requestId) { _requestId = requestId; }
	
	public Integer getPlanId() { return _planId; }
	public void setPlanId(Integer requestedPlanId) { _planId = requestedPlanId; }
	public Integer getUserId() {
		return _userId;
	}
	public void setUserId(Integer _userId) {
		this._userId = _userId;
	}
	
}
