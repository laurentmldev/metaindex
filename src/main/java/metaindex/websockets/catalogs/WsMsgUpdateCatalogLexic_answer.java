package metaindex.websockets.catalogs;

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgUpdateCatalogLexic_answer extends WsMsgUpdateCatalogLexic_request implements IWsMsg_answer  {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	public WsMsgUpdateCatalogLexic_answer(WsMsgUpdateCatalogLexic_request request) {
		this.setRequestId(request.getRequestId());
		this.setCatalogId(request.getCatalogId());
		this.setLangShortName(request.getLangShortName());
		this.setEntryName(request.getEntryName());
		this.setEntryTranslation(request.getEntryTranslation());
	}

}
