package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgGetItemFieldFullValue_answer extends WsMsgGetItemFieldFullValue_request implements IWsMsg_answer {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
		
	private String _fieldValue;

	public WsMsgGetItemFieldFullValue_answer(WsMsgGetItemFieldFullValue_request request) {
		this.setRequestId(request.getRequestId());
		this.setItemId(request.getItemId());
		this.setFieldName(request.getFieldName());		
	}
	public String getFieldValue() {
		return _fieldValue;
	}
	public void setFieldValue(String _fieldValue) {
		this._fieldValue = _fieldValue;
	}

}
