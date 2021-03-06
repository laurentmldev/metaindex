package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgGetCatalogs_request  {
		

	private Integer _requestId=0;
	// 0 means all catalogs for which user has minimum 'read' access rights
	// -1 means all catalogs for which user has no access rights
	private Integer _catalogId=0;
	
	public WsMsgGetCatalogs_request() {
		
		
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

	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}	

}
