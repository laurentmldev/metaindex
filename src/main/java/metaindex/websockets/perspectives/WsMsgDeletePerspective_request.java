package metaindex.websockets.perspectives;

public class WsMsgDeletePerspective_request  {
		
	private Integer _requestId;
	private Integer _perspectiveId;
	private Integer _catalogId;
	
	
	public WsMsgDeletePerspective_request() {
	
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

	
	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer catalogId) {
		this._catalogId = catalogId;
	}


}
