package metaindex.app.control.websockets.terms.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUpdateTerm_answer extends WsMsgUpdateTerm_request implements IWsMsg_answer {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	public WsMsgUpdateTerm_answer() {}
	
	public WsMsgUpdateTerm_answer(WsMsgUpdateTerm_request requestMsg) {	
		this.setRequestId(requestMsg.getRequestId());   		
		this.setCatalogId(requestMsg.getCatalogId());		
		this.setTermName(requestMsg.getTermName());
		this.setTermType(requestMsg.getTermType());	
		this.setTermEnumsList(requestMsg.getTermEnumsList());	
		this.setTermIsMultiEnum(requestMsg.getTermIsMultiEnum());
	}
	
}
