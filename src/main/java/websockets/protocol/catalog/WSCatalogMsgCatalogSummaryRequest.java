package metaindex.websockets.protocol.catalog;



import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.websockets.protocol.AWSJsonMessage;


// Class used for UTest only
public class WSCatalogMsgCatalogSummaryRequest extends AWSJsonMessage {

	Integer catalogId=0;
	
	
	public WSCatalogMsgCatalogSummaryRequest(Integer catalogId) {
		this.setCatalogId(catalogId);
	}
	
	@Override
	public String getMsgType() {
		return "catalog-summary";
	}

	public Integer getCatalogId() { return catalogId; }
	public void setCatalogId(Integer newCatalogId) { catalogId=newCatalogId; }
		
}
