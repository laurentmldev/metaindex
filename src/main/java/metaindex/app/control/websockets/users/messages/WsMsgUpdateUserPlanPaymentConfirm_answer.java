package metaindex.app.control.websockets.users.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateUserPlanPaymentConfirm_answer implements IWsMsg_answer {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private Integer _requestId;	
	private Integer _requestedPlanId;
	private String _transactionId;
	
	public WsMsgUpdateUserPlanPaymentConfirm_answer(WsMsgUpdateUserPlanPaymentConfirm_request request) {
		this.setRequestId(request.getRequestId());
		this.setTransactionId(request.getTransactionId());
		this.setPlanId(request.getPlanId());
	}
	public Integer getRequestId() { return _requestId; }
	public void setRequestId(Integer requestId) { _requestId = requestId; }
	
	public Integer getPlanId() { return _requestedPlanId; }
	public void setPlanId(Integer requestedPlanId) { _requestedPlanId = requestedPlanId; }
	
	public String getTransactionId() { 
		return _transactionId; 
	}
	public void setTransactionId(String transactionId) { 
		_transactionId = transactionId; 
	}
	
}
