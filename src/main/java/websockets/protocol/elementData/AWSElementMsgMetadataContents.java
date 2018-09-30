package metaindex.websockets.protocol.elementData;


import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public abstract class AWSElementMsgMetadataContents extends AWSJsonMessage {

	Integer metadataId=0;
	Integer datasetId=0;
	String metadataName = "";
	String metadataType = "";
	String metadataComment = "";
	Integer layoutColumn= 0;
	Integer layoutPosition= 0;
	Integer termId=0;
	String layoutAlign= "";
	String layoutSize= "";
	Boolean layoutDoDisplayName= true;
	Boolean isTemplated= false;
	Boolean modifyOverridenTemplate=false;
	Boolean isReadOnly = true;
	
	public AWSElementMsgMetadataContents() {}
	
	public AWSElementMsgMetadataContents(IMetadataHandle m, IUserProfileData user) {
		this.setMetadataId(m.getMetadataId());
		this.setDatasetId(m.getDatasetId());
		this.setMetadataName(m.getName());
		this.setMetadataComment(m.getComment());
		this.setLayoutColumn(m.getLayoutColumn());
		this.setLayoutPosition(m.getLayoutPosition());
		this.setLayoutAlign(m.getLayoutAlign());
		this.setLayoutSize(m.getLayoutSize());
		this.setTermId(m.getTermId());
		this.setLayoutDoDisplayName(m.isLayoutDoDisplayName());
		this.setTemplated(m.isTemplated());
		this.setModifyOverridenTemplate(m.isModifyOverridenTemplate());
		this.setReadOnly(m.isReadOnly() || m.isTemplated() && !m.isModifyOverridenTemplate());		
		
	}
	
	@Override
	public String getMsgType() {
		return "element-metadatacontents";
	}

	abstract public String getMetadataType();
	
	public Integer getMetadataId() { return metadataId; }
	public void setMetadataId(Integer newId) { metadataId=newId; }
	
	public Integer getDatasetId() { return datasetId; }
	public void setDatasetId(Integer newDatasetId) { datasetId=newDatasetId; }
	
	public String getMetadataName() { return metadataName; }
	public void setMetadataName(String newText) { metadataName=newText; }
	
	public String getMetadataComment() { return metadataComment; }
	public void setMetadataComment(String mycomment) { metadataComment = mycomment; }
	
	public Integer getLayoutColumn() { return layoutColumn; }
	public void setLayoutColumn(Integer layoutColumn) { this.layoutColumn = layoutColumn; }
	
	public Integer getLayoutPosition() { return layoutPosition; }
	public void setLayoutPosition(Integer layoutPosition) { this.layoutPosition = layoutPosition; }
	
	public Integer getTermId() { return termId; }
	public void setTermId(Integer termId) { this.termId=termId; }

	public String getLayoutAlign() { return layoutAlign; }
	public void setLayoutAlign(String layoutAlign) { this.layoutAlign = layoutAlign; }

	public String getLayoutSize() { return layoutSize; }
	public void setLayoutSize(String layoutSize) { this.layoutSize = layoutSize; }

	public Boolean isLayoutDoDisplayName() { return layoutDoDisplayName; }
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) { this.layoutDoDisplayName = layoutDoDisplayName; }
	
	public Boolean isTemplated() { return isTemplated; }
	public void setTemplated(Boolean isTemplated) { this.isTemplated = isTemplated; }	
	
	public Boolean isModifyOverridenTemplate() { return modifyOverridenTemplate; }
	public void setModifyOverridenTemplate(Boolean modifyOverridenTemplate) { this.modifyOverridenTemplate = modifyOverridenTemplate; }
	
	public Boolean isReadOnly() { return this.isReadOnly; }
	public void setReadOnly(Boolean isReadOnly) { this.isReadOnly = isReadOnly; }
	
	
}
