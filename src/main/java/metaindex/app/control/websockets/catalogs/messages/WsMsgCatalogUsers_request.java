package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class WsMsgCatalogUsers_request  {
		
	private Integer _requestId;
	private Integer _catalogId;
	
	public Integer getCatalogId() {
		return _catalogId;
	}
	
	public void setCatalogId(Integer id) {
		this._catalogId = id;
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}
	
}
