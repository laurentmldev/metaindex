package metaindex.app.control.websockets.catalogs.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import toolbox.utils.filetools.FileDescriptor;

public class WsMsgUserDataFileUpload_request {

	
	private Integer _requestId;
	private Integer _catalogId = 0;
	private List<FileDescriptor> _fileDescriptions= new ArrayList<>();
	
	public Integer getCatalogId() {
		return _catalogId;
	}

	public void setCatalogId(Integer _catalogId) {
		this._catalogId = _catalogId;
	}

	public List<FileDescriptor> getFileDescriptions() {
		return _fileDescriptions;
	}

	public void setFileDescriptions(List<FileDescriptor> fileDescriptions) {
		this._fileDescriptions = fileDescriptions;
	}

	public Integer getRequestId() {
		return _requestId;
	}

	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}

}
