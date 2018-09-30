package metaindex.websockets.protocol.elementData;


import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_TinyText;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgMetadataContents_TinyText extends AWSElementMsgMetadataContents {

	String text="";
	String fontWeight="";
	
	public WSElementMsgMetadataContents_TinyText() {}
	
	public WSElementMsgMetadataContents_TinyText(IMetadataHandle metadata, IUserProfileData user) {
		super(metadata,user);
		
		assert(metadata.isTinyText());
		
		IMetadata_TinyText m = metadata.getAsTinyText();
		this.setText(m.getText());
		this.setFontWeight(m.getFontWeight());
	}
	
	@Override
	public String getMetadataType() { return "tiny-text"; };
	
	public String getText() { return text; }
	public void setText(String newText) { text=newText; }
	
	public String getFontWeight() { return fontWeight; }
	public void setFontWeight(String newFontWeight) { fontWeight=newFontWeight; }
	
}
