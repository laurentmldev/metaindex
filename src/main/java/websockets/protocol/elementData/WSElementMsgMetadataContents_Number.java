package metaindex.websockets.protocol.elementData;


import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.IMetadata_Number;
import metaindex.data.metadata.specialized.IMetadata_WebLink;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgMetadataContents_Number extends AWSElementMsgMetadataContents {

	Double value=0.0;
	String unit="";
	
	public WSElementMsgMetadataContents_Number() {}
	
	public WSElementMsgMetadataContents_Number(IMetadataHandle metadata, IUserProfileData user) {
		super(metadata,user);
		
		assert(metadata.isNumber());
		
		IMetadata_Number m = metadata.getAsNumber();
		this.setValue(m.getValue());
		this.setUnit(m.getUnit());
	}
	
	@Override
	public String getMetadataType() { return "unit"; };
		
	public String getUnit() { return unit; }
	public void setUnit(String unit) { this.unit=unit; }
	
	public Double getValue() { return value; }
	public void setValue(Double value) { this.value=value; }
	
}
