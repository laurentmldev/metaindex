package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.HashMap;
import java.util.Map;

import metaindex.app.control.websockets.catalogs.messages.WsMsgUserDataFileUpload_request;

public class WsMsgCsvFileUpload_request extends WsMsgUserDataFileUpload_request {

	public static final String CSV_MAPPING_NEWTERM_PREFIX="__new__";
	private Map<String,String> _fieldsMapping= new HashMap<>();
	private Integer _totalNbEntries = 0;
	private String _separator=";";
	
	
	public String getSeparator() { return _separator; }
	public void setSeparator(String sep) { _separator = sep; }
	

	public Map<String,String> getFieldsMapping() {
		return _fieldsMapping;
	}
	public void setFieldsMapping(Map<String,String> map) {
		_fieldsMapping=map;
	}

	public Integer getTotalNbEntries() {
		return _totalNbEntries;
	}

	public void setTotalNbEntries(Integer totalNbEntries) {
		this._totalNbEntries = totalNbEntries;
	}

	
	
}
