package metaindex.websockets.items;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateFieldValue_request  {
		
	private Integer _requestId=0;
	private String _itemId="";
	private String _fieldName="";
	private String _fieldValue="";
	
	
	public WsMsgUpdateFieldValue_request() {
	
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


	public void setItemId(String itemId) {
		this._itemId = itemId;
	}


	public String getFieldName() {
		return _fieldName;
	}


	public void setFieldName(String fieldName) {
		this._fieldName = fieldName;
	}


	public String getFieldValue() {
		return _fieldValue;
	}


	public void setFieldValue(String fieldValue) {
		this._fieldValue = fieldValue;
	}
	
}
