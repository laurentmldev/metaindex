package metaindex.app.control.websockets.terms.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgDeleteTerm_request  {
		
	private Integer _catalogId;
	private String _termName;
	
	public String getTermName() {
		return _termName;
	}

	public void setTermName(String termName) {
		this._termName = termName;
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}

}
