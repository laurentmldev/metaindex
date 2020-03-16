package metaindex.app.control.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

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
