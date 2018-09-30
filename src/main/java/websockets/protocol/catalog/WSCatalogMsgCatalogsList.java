package metaindex.websockets.protocol.catalog;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSCatalogMsgCatalogsList extends AWSJsonMessage {

	List<Integer> catalogsList=new ArrayList<Integer>();	
	
	public WSCatalogMsgCatalogsList() {}
	

	
	public WSCatalogMsgCatalogsList(ICommunityHandle c) {
		Iterator<ICatalogHandle> it = c.getCatalogs().iterator();
		while (it.hasNext()) {
			Integer curCatId = it.next().getCatalogId();
			catalogsList.add(curCatId);
		}				
	}
	
	@Override
	public String getMsgType() {
		return "catalogs-list";
	}

	public List<Integer> getCatalogsList() { return catalogsList; }
	public void setCatalogId(List<Integer> catalogsList) { this.catalogsList=catalogsList; }
	
}
