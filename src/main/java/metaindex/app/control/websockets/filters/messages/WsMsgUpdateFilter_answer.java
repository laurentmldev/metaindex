package metaindex.app.control.websockets.filters.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUpdateFilter_answer extends WsMsgCreateFilter_answer {
	private String _filterName;

	public String getFilterName() {
		return _filterName;
	}

	public void setFilterName(String filterName) {
		this._filterName = filterName;
	}
	
}
