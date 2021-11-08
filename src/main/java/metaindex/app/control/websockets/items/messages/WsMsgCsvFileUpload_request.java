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
	
	/** 
	 * Mapping between a column name in input file and a field name in database 
	 * Special value __new__<type>__<fieldname> might be use to precise that
	 * a new database field shall be created, with given data type.
	 */ 
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
