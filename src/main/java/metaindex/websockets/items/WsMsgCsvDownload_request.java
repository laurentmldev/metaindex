package metaindex.websockets.items;

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

public class WsMsgCsvDownload_request extends WsMsgGetItems_request {

	private List<String> _termNamesList= new ArrayList<>();
	private String _separator=";";
	
	
	public String getSeparator() { return _separator; }
	public void setSeparator(String sep) { _separator = sep; }
	
	public List<String> getTermNamesList() {
		return _termNamesList;
	}

	
	public void setTermNamesList(List<String> termNamesList) {
		this._termNamesList = termNamesList;		
	}
	
}
