package metaindex.websockets.items;

public class WsMsgGetItems_request extends WsMsgDeleteItemsByQuery_request  {
		
	private Integer _requestId=0;
	private Integer _fromIdx=0;
	private Integer _size=0;
	private String _sortByFieldName="";
	private Boolean _reverseSortOrder=false;

	public WsMsgGetItems_request() {
	
	}
	public Integer getFromIdx() {
		return _fromIdx;
	}
	public void setFromIdx(Integer fromIdx) {
		this._fromIdx = fromIdx;
	}
	public Integer getSize() {
		return _size;
	}
	public void setSize(Integer size) {
		this._size = size;
	}
	public String getSortByFieldName() {
		return _sortByFieldName;
	}
	public void setSortByFieldName(String sortByFieldName) {
		this._sortByFieldName = sortByFieldName;
	}
	public Boolean getReverseSortOrder() {
		return _reverseSortOrder;
	}
	public void setReverseSortOrder(Boolean _reverseSortOrder) {
		this._reverseSortOrder = _reverseSortOrder;
	}
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}
}
