package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class WsMsgUserDataFileUploadContents_request {

	private Integer processingTaskId = 0;
	private Integer _clientFileId = 0;
	private byte[] _rawContents; 
	
	// shall start from 1 
	private Integer _sequenceNumber=0;
	
	
	public int getProcessingTaskId() {
		return processingTaskId;
	}

	public void setProcessingTaskId(int processingTaskId) {
		this.processingTaskId = processingTaskId;
	}

	public Integer getClientFileId() {
		return _clientFileId;
	}

	public void setClientFileId(Integer _clientFileId) {
		this._clientFileId = _clientFileId;
	}

	public byte[] getRawContents() {
		return _rawContents;
	}

	public void setRawContents(byte[] _rawContents) {
		this._rawContents = _rawContents;
	}

	public Integer getSequenceNumber() {
		return _sequenceNumber;
	}

	public void setSequenceNumber(Integer _sequenceNumber) {
		this._sequenceNumber = _sequenceNumber;
	}

	
}
