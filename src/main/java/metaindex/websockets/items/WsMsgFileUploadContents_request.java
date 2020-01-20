package metaindex.websockets.items;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

public class WsMsgFileUploadContents_request {

	private List<String> csvLines= new ArrayList<String>();
	private Integer processingTaskId = 0;
	private Integer _clientFileId = 0;
	
	public List<String> getCsvLines() {
		return csvLines;
	}

	public void setCsvLines(List<String> csvLines) {
		this.csvLines = csvLines;
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

	
}
