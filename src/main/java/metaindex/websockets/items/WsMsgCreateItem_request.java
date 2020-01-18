package metaindex.websockets.items;

import java.util.HashMap;
import java.util.Map;

public class WsMsgCreateItem_request  {
		
	private Integer _catalogId=0;
	private Integer _requestId=0; 
	private Map<String,Object> _fieldsMap=new HashMap<>();
	

	public WsMsgCreateItem_request() {
	
	}


	public Integer getCatalogId() {
		return _catalogId;
	}


	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}


	public Map<String,Object> getFieldsMap() {
		return _fieldsMap;
	}


	public void setFieldsMap(Map<String,Object> _fieldsMap) {
		this._fieldsMap.putAll(_fieldsMap);
	}


	public Integer getRequestId() {
		return _requestId;
	}


	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}
	
	
	
}
