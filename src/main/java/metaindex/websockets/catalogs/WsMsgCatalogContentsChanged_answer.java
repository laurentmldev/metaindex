package metaindex.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;

public class WsMsgCatalogContentsChanged_answer   {
		

	
	private String _catalogName;
	private String _userNickname;
	private Long _nbImpactedDocs;
	
	// used if only one document has been modified
	private String _impactedDocName="";
	private String _impactDetails="";
	
	private CATALOG_MODIF_TYPE _modifType;
	
	public WsMsgCatalogContentsChanged_answer(String catalogName,
												String userNickname,
												Long nbImpactedDocs,
												CATALOG_MODIF_TYPE modifType) {
		_catalogName=catalogName;
		_userNickname=userNickname;
		_nbImpactedDocs=nbImpactedDocs;
		_modifType=modifType;
	}

	public WsMsgCatalogContentsChanged_answer(String catalogName,
												String userNickname,
												String impactedDocName,
												String impactDetails,
												CATALOG_MODIF_TYPE modifType) {
		_catalogName=catalogName;
		_userNickname=userNickname;
		_nbImpactedDocs=1L;
		_modifType=modifType;
		_impactedDocName=impactedDocName;
		_impactDetails=impactDetails;
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


	public Long getNbImpactedDocs() {
		return _nbImpactedDocs;
	}


	public void setNbImpactedDocs(Long nbImpactedDocs) {
		this._nbImpactedDocs = nbImpactedDocs;
	}


	public CATALOG_MODIF_TYPE getModifType() {
		return _modifType;
	}


	public void setModifType(CATALOG_MODIF_TYPE modifType) {
		this._modifType = modifType;
	}


	public String getImpactedDocName() {
		return _impactedDocName;
	}


	public void setImpactedDocName(String _impactedDocName) {
		this._impactedDocName = _impactedDocName;
	}


	public String getImpactDetails() {
		return _impactDetails;
	}


	public void setImpactDetails(String impactDetails) {
		this._impactDetails = impactDetails;
	}	


}
