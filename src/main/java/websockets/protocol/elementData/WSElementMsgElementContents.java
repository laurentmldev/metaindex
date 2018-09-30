package metaindex.websockets.protocol.elementData;


import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgElementContents extends AWSJsonMessage {

	Integer elementId=0;
	String name = "";
	String comment = "";
	String thumbnailUrl = "";
	String searchText = "";
	Boolean isStatic=false;
	Boolean isTemplate=false;
	Boolean isTemplated=false;
	Boolean isTemplateLoadError=false;
	Boolean isSelected=false;
	Boolean isReadOnly=false;
	Integer nbReferencingElements=0;
	Integer templateRefElementId=0;
	Integer nbDatasets=0;
	
	public WSElementMsgElementContents() {}
	
	public WSElementMsgElementContents(IElementHandle e, IUserProfileData user) {
		this.setElementName(e.getName());
		this.setElementComment(e.getComment());
		this.setElementId(e.getElementId());
		this.setThumbnailUrl(e.getThumbnailUrl());
		this.setSearchText(e.getSearchText());
		this.setStatic(user.getSelectedCatalog().isStaticElement(e.getElementId()));
		this.setTemplate(e.isTemplate());
		this.setTemplated(e.isTemplated());
		this.setTemplateLoadError(e.isTemplateLoadError());
		this.setSelected(user.getSelectedElement().getElementId().equals(e.getElementId()));
		this.setReadOnly(e.isReadOnly());
		this.setNbReferencingElements(e.getNbReferencingElements());
		this.setTemplateRefElementId(e.getTemplateRefElementId());
		this.setNbDatasets(e.getNbDatasets());
	}
	
	@Override
	public String getMsgType() {
		return "element-contents";
	}

	public Integer getElementId() { return elementId; }
	public void setElementId(Integer newElementId) { elementId=newElementId; }
	
	public String getElementName() { return name; }
	public void setElementName(String newText) { name=newText; }
	
	public String getElementComment() { return comment; }
	public void setElementComment(String mycomment) { comment = mycomment; }
	
	public String getThumbnailUrl() { return thumbnailUrl; }
	public void setThumbnailUrl(String myThumbnailUrl) { thumbnailUrl = myThumbnailUrl; }

	public String getSearchText() { return searchText; }
	public void setSearchText(String searchText) { this.searchText = searchText; }

	public Boolean isStatic() { return isStatic; }
	public void setStatic(Boolean isStatic) { this.isStatic = isStatic; }

	public Boolean isTemplate() { return isTemplate; }
	public void setTemplate(Boolean isTemplate) { this.isTemplate = isTemplate; }

	public Boolean isTemplated() { return isTemplated; }
	public void setTemplated(Boolean isTemplated) { this.isTemplated = isTemplated; }

	
	public Boolean isTemplateLoadError() { return isTemplateLoadError; }
	public void setTemplateLoadError(Boolean isTemplateLoadError) { this.isTemplateLoadError = isTemplateLoadError; }

	public Boolean isSelected() { return isReadOnly; }
	public void setSelected(Boolean isSelected) { this.isSelected = isSelected; }
	
	public Boolean isReadOnly() { return isReadOnly; }
	public void setReadOnly(Boolean isReadOnly) { this.isReadOnly = isReadOnly; }
	
	public Integer getNbReferencingElements() { return nbReferencingElements; }
	public void setNbReferencingElements(Integer nbReferencingElements) { this.nbReferencingElements=nbReferencingElements; }
	
	public Integer getTemplateRefElementId() { return templateRefElementId; }
	public void setTemplateRefElementId(Integer templateRefElementId) { this.templateRefElementId=templateRefElementId; }
	
	public Integer getNbDatasets() { return nbDatasets; }
	public void setNbDatasets(Integer nbDatasets) { this.nbDatasets=nbDatasets; }
}
