package metaindex.app.control.websockets.items.messages;

import java.util.ArrayList;
import java.util.List;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class WsMsgGraphDownloadGroupBy_request extends WsMsgGetItems_request {

	private Integer _groupingTermId = null;
	private List<Integer> _edgesTermsIdsList= new ArrayList<>();
	
	public Integer getGroupingTermId() {
		return _groupingTermId;
	}
	public void setGroupingTermId(Integer groupingTermId) {
		this._groupingTermId = groupingTermId;
	}
	
	public List<Integer> getEdgesTermIdsList() {
		return _edgesTermsIdsList;
	}

	public void setEdgesTermIdsList(List<Integer> idsList) {
		this._edgesTermsIdsList= idsList;		
	}
		
	
}
