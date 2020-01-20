package metaindex.websockets.filters;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.websockets.commons.IWsMsg_answer;

public class WsMsgCreateFilter_answer extends WsMsgCreateFilter_request implements IWsMsg_answer  {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	private Integer _filterId;
	
	public WsMsgCreateFilter_answer() {}
	
	public WsMsgCreateFilter_answer(WsMsgCreateFilter_request request) {
		this.setFilterName(request.getFilterName());
		this.setQuery(request.getQuery());
		this.setRequestId(request.getRequestId());
	}
	public Integer getFilterId() {
		return _filterId;
	}
	public void setFilterId(Integer filterId) {
		this._filterId = filterId;
	}
}
