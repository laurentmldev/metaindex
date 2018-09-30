package metaindex.websockets.protocol.elementData;


import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_LongText;
import metaindex.data.metadata.specialized.IMetadata_TinyText;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgMetadataContents_LongText extends AWSElementMsgMetadataContents {

	String text="";	
	
	public WSElementMsgMetadataContents_LongText() {}
	
	public WSElementMsgMetadataContents_LongText(IMetadataHandle metadata, IUserProfileData user) {
		super(metadata,user);
		
		assert(metadata.isLongText());
		
		IMetadata_LongText m = metadata.getAsLongText();
		this.setText(m.getText());
	}
	
	@Override
	public String getMetadataType() { return "long-text"; };
	
	public String getText() { return text; }
	public void setText(String newText) { text=newText; }
	
	
}
