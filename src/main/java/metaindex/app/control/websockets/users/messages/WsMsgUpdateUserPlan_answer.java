package metaindex.app.control.websockets.users.messages;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.app.control.websockets.commons.IWsMsg_answer;
import metaindex.app.control.websockets.users.WsControllerUser;
import metaindex.app.control.websockets.users.WsControllerUser.PlanBreakdownEntry;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WsMsgUpdateUserPlan_answer extends WsMsgUpdateUserPlan_request implements IWsMsg_answer {
	
	private Log log = LogFactory.getLog(WsControllerUser.class);
	
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	public WsMsgUpdateUserPlan_answer(WsMsgUpdateUserPlan_request request) {
		setRequestId(request.getRequestId());
		setPlanId(request.getPlanId());
		setUserId(request.getUserId());
	}

	private String _transactionId;
	private Float _totalCost;
	private List<PlanBreakdownEntry> _breakdownEntries =  new ArrayList<>();
		
	public Float getTotalCost() {
		return _totalCost;
	}
	public void setTotalCost(Float _totalCost) {
		this._totalCost = _totalCost;
	}
	public List<PlanBreakdownEntry> getBreakdownEntries() {
		return _breakdownEntries;
	}
	public void setBreakdownEntries(List<PlanBreakdownEntry> _breakdownEntries) {
		this._breakdownEntries = _breakdownEntries;
	}
	
	public String getTransactionId() { 
		return _transactionId; 
	}
	public void setTransactionId(String transactionId) { 
		_transactionId = transactionId; 
	}
	
}
