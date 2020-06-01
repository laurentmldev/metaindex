package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgSelectCatalog_request  {
		
	private Integer _catalogId;
	
	public WsMsgSelectCatalog_request() {
	
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}	

}
