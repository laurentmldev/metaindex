package metaindex.app.control.websockets.catalogs.messages;

import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class WsMsgSetCatalogUserAccess_request extends WsMsgCatalogUsers_request {
		
	private Integer _userId;
	private USER_CATALOG_ACCESSRIGHTS _accessRights;
	public Integer getUserId() {
		return _userId;
	}
	public void setUserId(Integer _userId) {
		this._userId = _userId;
	}
	public USER_CATALOG_ACCESSRIGHTS getAccessRights() {
		return _accessRights;
	}
	public void setAccessRights(USER_CATALOG_ACCESSRIGHTS _accessRights) {
		this._accessRights = _accessRights;
	}
	
	
}
