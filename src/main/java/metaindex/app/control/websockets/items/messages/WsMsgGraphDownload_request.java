package metaindex.app.control.websockets.items.messages;

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

public class WsMsgGraphDownload_request extends WsMsgGetItems_request {

	private List<Integer> _nodesDataTermsIdsList= new ArrayList<>();
	private List<Integer> _edgesTermsIdsList= new ArrayList<>();
		
	public List<Integer> getNodesDataTermIdsList() {
		return _nodesDataTermsIdsList;
	}

	public void setNodesDataTermIdsList(List<Integer> idsList) {
		this._nodesDataTermsIdsList= idsList;		
	}
	
	public List<Integer> getEdgesTermIdsList() {
		return _edgesTermsIdsList;
	}

	public void setEdgesTermIdsList(List<Integer> idsList) {
		this._edgesTermsIdsList= idsList;		
	}
	
}
