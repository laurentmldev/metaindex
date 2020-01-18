package metaindex.websockets.catalogs;

public class WsMsgDeleteCatalog_request  {
		
	private Integer _catalogId;
	private Integer _requestId;
	
	public WsMsgDeleteCatalog_request() {
	
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}	

}
