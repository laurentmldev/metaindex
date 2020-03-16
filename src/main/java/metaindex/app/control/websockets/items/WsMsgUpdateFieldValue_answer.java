package metaindex.app.control.websockets.items;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUpdateFieldValue_answer extends WsMsgUpdateFieldValue_request implements IWsMsg_answer {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	public WsMsgUpdateFieldValue_answer(WsMsgUpdateFieldValue_request requestMsg) {
		this.setRequestId(requestMsg.getRequestId());   		
		this.setFieldName(requestMsg.getFieldName());
		this.setFieldValue(requestMsg.getFieldValue());
		this.setItemId(requestMsg.getItemId());
	}

	
}
