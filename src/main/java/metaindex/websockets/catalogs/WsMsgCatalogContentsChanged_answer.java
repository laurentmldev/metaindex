package metaindex.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.websockets.users.WsControllerUser.COMMUNITY_MODIF_TYPE;

public class WsMsgCatalogContentsChanged_answer   {
		

	
	private String _catalogName;
	private String _userNickname;
	private Integer _nbImpactedItems;
	private COMMUNITY_MODIF_TYPE _modifType;
	
	public WsMsgCatalogContentsChanged_answer(String catalogName,
												String userNickname,
												Integer nbImpactedItems,
												COMMUNITY_MODIF_TYPE modifType) {
		_catalogName=catalogName;
		_userNickname=userNickname;
		_nbImpactedItems=nbImpactedItems;
		_modifType=modifType;
	}


	public String getCatalogName() {
		return _catalogName;
	}


	public void setCatalogName(String catalogName) {
		this._catalogName = catalogName;
	}


	public String getUserNickname() {
		return _userNickname;
	}


	public void setUserNickname(String userNickname) {
		this._userNickname = userNickname;
	}


	public Integer getNbImpactedItems() {
		return _nbImpactedItems;
	}


	public void setNbImpactedItems(Integer nbImpactedItems) {
		this._nbImpactedItems = nbImpactedItems;
	}


	public COMMUNITY_MODIF_TYPE getModifType() {
		return _modifType;
	}


	public void setModifType(COMMUNITY_MODIF_TYPE modifType) {
		this._modifType = modifType;
	}	


}
