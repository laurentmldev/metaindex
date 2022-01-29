package metaindex.app.control.websockets.filters.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateFilter_request  {
	
	private Integer _requestId;
	private Integer _filterId;
	private String _query;
	
	public Integer getFilterId() {
		return _filterId;
	}

	public void setFilterId(Integer filterId) {
		this._filterId = filterId;
	}

	public String getQuery() {
		return _query;
	}

	public void setQuery(String query) {
		this._query = query;
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}	

}
