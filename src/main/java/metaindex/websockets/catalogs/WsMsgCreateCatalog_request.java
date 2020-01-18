package metaindex.websockets.catalogs;

public class WsMsgCreateCatalog_request  {
		
	private String _catalogName;
	
	public WsMsgCreateCatalog_request() {
	
	}

	public String getCatalogName() {
		return _catalogName;
	}

	public void setCatalogName(String catalogName) {
		this._catalogName = catalogName;
	}	

}
