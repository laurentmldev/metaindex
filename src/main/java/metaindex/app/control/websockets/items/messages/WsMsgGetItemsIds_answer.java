package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgGetItemsIds_answer extends WsMsgGetItems_request implements IWsMsg_answer {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private List<String> _itemsIds = new ArrayList<String>();
	
	public WsMsgGetItemsIds_answer(WsMsgGetItems_request request) {
		this.setRequestId(request.getRequestId());
		this.setFromIdx(request.getFromIdx());
		this.setSize(request.getSize());
		this.setQuery(request.getQuery());
		this.setFiltersNames(request.getFiltersNames());
		this.setSortByFieldName(request.getSortByFieldName());
		this.setReverseSortOrder(request.getReverseSortOrder());
	}

	public List<String> getItemsIds() {
		return _itemsIds;
	}

	public void setItemsIds(List<String> itemsIds) {
		this._itemsIds = itemsIds;
	}

}
