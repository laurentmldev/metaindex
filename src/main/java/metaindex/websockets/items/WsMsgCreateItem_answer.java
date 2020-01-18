package metaindex.websockets.items;

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgCreateItem_answer extends WsMsgCreateItem_request implements IWsMsg_answer {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	public WsMsgCreateItem_answer(WsMsgCreateItem_request request) {
		this.setCatalogId(request.getCatalogId());
		this.setFieldsMap(request.getFieldsMap());
		this.setRequestId(request.getRequestId());
	}


}
