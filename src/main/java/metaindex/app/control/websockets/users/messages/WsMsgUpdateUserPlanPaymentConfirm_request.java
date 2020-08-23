package metaindex.app.control.websockets.users.messages;

import toolbox.utils.payment.IPaymentInterface.PAYMENT_METHOD;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateUserPlanPaymentConfirm_request  {
	
	private Integer _userId;
	private Integer _requestId;	
	private Integer _planId;
	private String _transactionId;
	private Float _totalCost;
	private PAYMENT_METHOD _paymentMethod;
	private String _paymentDetails;
	
	public Integer getRequestId() { return _requestId; }
	public void setRequestId(Integer requestId) { _requestId = requestId; }
	
	public Integer getPlanId() { return _planId; }
	public void setPlanId(Integer requestedPlanId) { _planId = requestedPlanId; }
	
	public Float getTotalCost() {
		return _totalCost;
	}
	public void setTotalCost(Float _totalCost) {
		this._totalCost = _totalCost;
	}	
	public String getTransactionId() { 
		return _transactionId; 
	}
	public void setTransactionId(String transactionId) { 
		_transactionId = transactionId; 
	}
	public String getPaymentDetails() { 
		return _paymentDetails; 
	}
	public void setPaymentDetails(String transactionDetails) { 
		_paymentDetails = transactionDetails; 
	}
	public Integer getUserId() {
		return _userId;
	}
	public void setUserId(Integer _userId) {
		this._userId = _userId;
	}
	public PAYMENT_METHOD getPaymentMethod() {
		return _paymentMethod;
	}
	public void setPaymentMethod(PAYMENT_METHOD _paymentMethod) {
		this._paymentMethod = _paymentMethod;
	}
}
