package metaindex.app.control.websockets.items;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toolbox.utils.IPair;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

public class WsMsgFileUpload_request {

	public static final String CSV_MAPPING_NEWTERM_PREFIX="__new__";
	private Integer _clientFileId = 0;
	private List<String> _csvColsList= new ArrayList<>();
	private Map<String,String> _csvMapping= new HashMap<>();
	private Integer _totalNbEntries = 0;
	private String _separator=";";
	
	
	public String getSeparator() { return _separator; }
	public void setSeparator(String sep) { _separator = sep; }
	
	public Integer getClientFileId() {
		return _clientFileId;
	}

	public void setClientFileId(Integer clientFileId) {
		this._clientFileId = clientFileId;
	}

	public List<String> getCsvColsList() {
		return _csvColsList;
	}

	
	public void setCsvColsList(List<String> csvColsList) {
		this._csvColsList = csvColsList;		
	}

	public Map<String,String> getChosenFieldsMapping() {
		return _csvMapping;
	}
	public void setChosenFieldsMapping(Map<String,String> map) {
		_csvMapping=map;
	}

	public Integer getTotalNbEntries() {
		return _totalNbEntries;
	}

	public void setTotalNbEntries(Integer totalNbEntries) {
		this._totalNbEntries = totalNbEntries;
	}

	
	
}
