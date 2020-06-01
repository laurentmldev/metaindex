package metaindex.app.control.websockets.terms.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgCreateTerm_answer extends WsMsgCreateTerm_request  implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	
	public WsMsgCreateTerm_answer(WsMsgCreateTerm_request request) {
		this.setTermName(request.getTermName());
		this.setCatalogId(request.getCatalogId());
		this.setTermDatatype(request.getTermDatatype());
		this.setComplementaryInfoMap(request.getComplementaryInfoMap());
	}
}
