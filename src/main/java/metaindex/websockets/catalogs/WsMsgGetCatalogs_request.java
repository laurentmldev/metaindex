package metaindex.websockets.catalogs;

public class WsMsgGetCatalogs_request  {
		
	// 0 means all catalogs
	private Integer _catalogId=0;
	
	public WsMsgGetCatalogs_request() {
		
		
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}	

}
