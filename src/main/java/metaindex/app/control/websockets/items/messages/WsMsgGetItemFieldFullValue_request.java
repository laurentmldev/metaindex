package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgGetItemFieldFullValue_request {
		
	private Integer _requestId=0;
	private String _itemId="";
	private String _fieldName="";
	

	public WsMsgGetItemFieldFullValue_request() {
	
	}
	
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer requestId) {
		this._requestId = requestId;
	}

	public String getItemId() {
		return _itemId;
	}

	public void setItemId(String _itemId) {
		this._itemId = _itemId;
	}

	public String getFieldName() {
		return _fieldName;
	}

	public void setFieldName(String _fieldName) {
		this._fieldName = _fieldName;
	}
}
