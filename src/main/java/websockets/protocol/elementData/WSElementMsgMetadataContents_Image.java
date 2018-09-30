package metaindex.websockets.protocol.elementData;


import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgMetadataContents_Image extends AWSElementMsgMetadataContents {

	String imageUrl="";
	Boolean isThumbnail=false;
	Long borderSize=new Long(0);
	String borderColor="";
	
	public WSElementMsgMetadataContents_Image() {}
	
	public WSElementMsgMetadataContents_Image(IMetadataHandle metadata, IUserProfileData user) {
		super(metadata,user);
		
		assert(metadata.isImage());
		
		IMetadata_Image m = metadata.getAsImage();
		this.setBorderColor(m.getBorderColor());
		this.setBorderSize(m.getBorderSize());
		this.setThumbnail(m.isThumbnail());
		this.setImageUrl(m.getImageUrl());
	}
	
	@Override
	public String getMetadataType() { return "image"; };
		
	public Long getBorderSize() { return borderSize; }
	public void setBorderSize(Long newBorderSize) { borderSize=newBorderSize; }
	
	public String getBorderColor() { return borderColor; }
	public void setBorderColor(String newBorderColor) { borderColor=newBorderColor; }
	
	public String getImageUrl() { return imageUrl; }
	public void setImageUrl(String url) { imageUrl=url; }
	
	public Boolean isThumbnail() { return isThumbnail; }
	public void setThumbnail(Boolean useAsThumbnail) { isThumbnail=useAsThumbnail; }
	
}
