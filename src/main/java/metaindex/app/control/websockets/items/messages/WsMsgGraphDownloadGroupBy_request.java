package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class WsMsgGraphDownloadGroupBy_request extends WsMsgGetItems_request {

	private Integer _groupingTermId = null;
	private Integer _edgeTermId = null;
	
	public Integer getGroupingTermId() {
		return _groupingTermId;
	}
	public void setGroupingTermId(Integer groupingTermId) {
		this._groupingTermId = groupingTermId;
	}
	public Integer getEdgeTermId() {
		return _edgeTermId;
	}
	public void setEdgeTermId(Integer edgeTermId) {
		this._edgeTermId = edgeTermId;
	}
		
	
}
