package metaindex.websockets.filters;

public class WsMsgCreateFilter_request  {
		
	private Integer _requestId;
	private String _filterName;
	private String _query;
	
	public String getFilterName() {
		return _filterName;
	}

	public void setFilterName(String filterName) {
		this._filterName = filterName;
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
