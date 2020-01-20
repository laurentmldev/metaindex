package metaindex.websockets.perspectives;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdatePerspective_request  {
		
	private Integer _requestId;
	private Integer _perspectiveId;
	private Integer _catalogId;
	private String _jsonDef;
	
	public WsMsgUpdatePerspective_request() {
	
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}

	public Integer getPerspectiveId() {
		return _perspectiveId;
	}

	public void setPerspectiveId(Integer perspectiveId) {
		this._perspectiveId = perspectiveId;
	}

	public String getJsonDef() {
		return _jsonDef;
	}

	public void setJsonDef(String jsonDef) {
		this._jsonDef = jsonDef;
	}

	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}


}
