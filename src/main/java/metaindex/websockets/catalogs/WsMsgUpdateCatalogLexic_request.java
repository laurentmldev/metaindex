package metaindex.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateCatalogLexic_request {
	
	private Integer _requestId;
	private Integer _catalogId;
	private String _langShortName;
	private String _entryName;
	private String _entryTranslation;
	
	public WsMsgUpdateCatalogLexic_request() {
	
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}

	public String getLangShortName() {
		return _langShortName;
	}

	public void setLangShortName(String _langShortName) {
		this._langShortName = _langShortName;
	}

	public String getEntryName() {
		return _entryName;
	}

	public void setEntryName(String _entryName) {
		this._entryName = _entryName;
	}

	public String getEntryTranslation() {
		return _entryTranslation;
	}

	public void setEntryTranslation(String _entryTranslation) {
		this._entryTranslation = _entryTranslation;
	}
	

}
