package metaindex.websockets.items;

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
