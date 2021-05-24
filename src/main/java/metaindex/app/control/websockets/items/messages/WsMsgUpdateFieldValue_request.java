package metaindex.app.control.websockets.items.messages;

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
	private Integer _nbChunks=0;
	private Integer _curChunkNb=0;
	
	
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


	public Integer getNbChunks() {
		return _nbChunks;
	}


	public void setNbChunks(Integer nbChunks) {
		this._nbChunks = nbChunks;
	}

	public Integer getCurChunkNb() {
		return _curChunkNb;
	}


	public void setCurChunkNb(Integer curChunkNb) {
		this._curChunkNb = curChunkNb;
	}	
}
