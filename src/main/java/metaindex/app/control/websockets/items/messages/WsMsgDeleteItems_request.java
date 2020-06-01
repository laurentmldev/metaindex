package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

public class WsMsgDeleteItems_request  {
	
	// filters within the query shall be applied
	private List<String> _itemIds = new ArrayList<String>();
	
	public List<String> getItemsIds() {
		return _itemIds;
	}
	public void setItemsIds(List<String> itemIds) {
		this._itemIds = itemIds;
	}
	

}
