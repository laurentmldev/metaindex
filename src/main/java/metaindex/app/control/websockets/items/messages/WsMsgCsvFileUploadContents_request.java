package metaindex.app.control.websockets.items.messages;

import java.io.IOException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import metaindex.app.control.websockets.commons.AMxWSController;

public class WsMsgCsvFileUploadContents_request {


	
	private List<String> _csvLines= new ArrayList<String>();
	private Integer processingTaskId = 0;
	private Integer _clientFileId = 0;
	private String _compressedCsvLineStr = "";
	private Integer _msgNb = 0;
	
	/// when line is itself too big to be sent over WebSockets
	/// it must be split into several chunks
	private Integer _totalNbChunks = 1;
	private Integer _curChunkNb = 1;
	
	public List<String> getCsvLines() {
		return _csvLines;
	}

	public void setCsvLines(List<String> csvLines) {
		this._csvLines = csvLines;
	}
	

	public String getCompressedCsvLineStr() {
		return _compressedCsvLineStr;
	}

	public void setCompressedCsvLineStr(String b64ZippedCsvLines) {
		_compressedCsvLineStr= b64ZippedCsvLines;
	}
	

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

	public Integer getTotalNbChunks() {
		return _totalNbChunks;
	}

	public void setTotalNbChunks(Integer totalNbChunks) {
		this._totalNbChunks = totalNbChunks;
	}

	public Integer getCurChunkNb() {
		return _curChunkNb;
	}

	public void setCurChunkNb(Integer curChunkNb) {
		this._curChunkNb = curChunkNb;
	}

	public Integer getMsgNb() {
		return _msgNb;
	}

	public void setMsgNb(Integer msgNb) {
		this._msgNb = msgNb;
	}

	
}
