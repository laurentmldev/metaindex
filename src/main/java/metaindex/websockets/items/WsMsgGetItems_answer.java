package metaindex.websockets.items;

import java.util.ArrayList;
import java.util.List;

import metaindex.websockets.commons.IWsMsg_answer;
import toolbox.database.IDbItem;

public class WsMsgGetItems_answer extends WsMsgGetItems_request implements IWsMsg_answer {
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	private List<IDbItem> _items = new ArrayList<IDbItem>();
	private Long _totalHits;
	private Long _totalItems;
	
	public WsMsgGetItems_answer(WsMsgGetItems_request request) {
		this.setRequestId(request.getRequestId());
		this.setFromIdx(request.getFromIdx());
		this.setSize(request.getSize());
		this.setQuery(request.getQuery());
		this.setFiltersNames(request.getFiltersNames());
		this.setSortByFieldName(request.getSortByFieldName());
		this.setReverseSortOrder(request.getReverseSortOrder());
	}

	public List<IDbItem> getItems() {
		return _items;
	}

	public void setItems(List<IDbItem> items) {
		this._items = items;
	}

	public Long getTotalHits() {
		return _totalHits;
	}

	public void setTotalHits(Long totalHits) {
		this._totalHits = totalHits;
	}
	public Long getTotalItems() {
		return _totalItems;
	}
	public void setTotalItems(Long totalItems) {
		this._totalItems = totalItems;
	}

}
