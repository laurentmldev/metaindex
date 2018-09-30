package metaindex.websockets.protocol.catalog;


import org.json.JSONObject;

import metaindex.websockets.protocol.AWSJsonMessage;

public class WSCommunityMsgElementsSummaries extends AWSJsonMessage {
	
	Integer communityNbElements=0;
	
	@Override
	public String getMsgType() {
		return "community-getelements_summaries";
	}
	
	public Integer getCommunityNbElements() { return communityNbElements; }
	public void setCommunityNbElements(Integer communityNbElements) { this.communityNbElements=communityNbElements; }
	
	
}
