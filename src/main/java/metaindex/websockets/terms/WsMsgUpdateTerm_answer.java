package metaindex.websockets.terms;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.websockets.commons.IWsMsg_answer;

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
