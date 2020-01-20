package metaindex.websockets.terms;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgCreateTerm_answer extends WsMsgCreateTerm_request  implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	
	WsMsgCreateTerm_answer(WsMsgCreateTerm_request request) {
		this.setTermName(request.getTermName());
		this.setCatalogId(request.getCatalogId());
		this.setTermDatatype(request.getTermDatatype());
		this.setComplementaryInfoMap(request.getComplementaryInfoMap());
	}
}
