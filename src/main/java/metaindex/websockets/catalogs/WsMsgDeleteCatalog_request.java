package metaindex.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

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
