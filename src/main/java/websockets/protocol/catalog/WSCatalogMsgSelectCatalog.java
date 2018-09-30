package metaindex.websockets.protocol.catalog;



import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSCatalogMsgSelectCatalog extends AWSJsonMessage {

	Integer catalogId=0;
	
	public WSCatalogMsgSelectCatalog() {}
	
	public WSCatalogMsgSelectCatalog(ICatalogHandle c) {
		this.setCatalogId(c.getCatalogId());		
	}
	
	@Override
	public String getMsgType() {
		return "catalog-select";
	}

	public Integer getCatalogId() { return catalogId; }
	public void setCatalogId(Integer newCatalogId) { catalogId=newCatalogId; }
	
}
