package metaindex.websockets.users;

public class WsMsgUserSetCatalogCustomization_request  {
		
	private Integer _requestId;	
	private Integer _userId;
	private Integer _catalogId;
	private String _kibanaIFrame;
	
	public Integer getUserId() {
		return _userId;
	}
	public void setUserId(Integer _userId) {
		this._userId = _userId;
	}
	
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}
	public Integer getCatalogId() {
		return _catalogId;
	}
	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}
	public String getKibanaIFrame() {
		return _kibanaIFrame;
	}
	public void setKibanaIFrame(String _kibanaIFrame) {
		this._kibanaIFrame = _kibanaIFrame;
	}
	
	

}
