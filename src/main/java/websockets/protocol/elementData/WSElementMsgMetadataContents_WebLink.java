package metaindex.websockets.protocol.elementData;


import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_WebLink;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgMetadataContents_WebLink extends AWSElementMsgMetadataContents {

	String linkText="";
	String url="";
	
	public WSElementMsgMetadataContents_WebLink() {}
	
	public WSElementMsgMetadataContents_WebLink(IMetadataHandle metadata, IUserProfileData user) {
		super(metadata,user);
		
		assert(metadata.isWebLink());
		
		IMetadata_WebLink m = metadata.getAsWebLink();
		this.setUrl(m.getUrl());
	}
	
	@Override
	public String getMetadataType() { return "web-link"; };
		
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url=url; }
	
	public String getLinkText() { return linkText; }
	public void setLinkText(String linkText) { this.linkText=linkText; }
	
	
	
}
