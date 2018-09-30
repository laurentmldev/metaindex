package metaindex.websockets.protocol.catalog;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSCatalogMsgElementSummary extends AWSJsonMessage {

	private Log log = LogFactory.getLog(WSCatalogMsgElementSummary.class);
	
	Integer elementId=0;
	String name = "";
	String comment = "";
	String thumbnailUrl = "";
	String searchText = "";
	Boolean isTemplate=false;
	Boolean isTemplated=false;
	Boolean isTemplateLoadError=false;
	Boolean isSelected=false;
	
	public WSCatalogMsgElementSummary() {}
	
	public WSCatalogMsgElementSummary(IElementHandle e, IUserProfileData user) {
		this.setElementName(e.getName());
		this.setElementComment(e.getComment());
		this.setElementId(e.getElementId());
		this.setThumbnailUrl(e.getThumbnailUrl());
		this.setSearchText(e.getSearchText());
		this.setTemplate(e.isTemplate());
		this.setTemplated(e.isTemplated());
		this.setTemplateLoadError(e.isTemplateLoadError());
		if (user!=null && user.getSelectedElement()!=null && e!=null) {
			this.setSelected(user.getSelectedElement().getElementId().equals(e.getElementId()));
		}		
	}
	
	@Override
	public String getMsgType() {
		return "catalog-elementsummary";
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

	public Boolean isTemplate() { return isTemplate; }
	public void setTemplate(Boolean isTemplate) { this.isTemplate = isTemplate; }

	public Boolean isTemplated() { return isTemplated; }
	public void setTemplated(Boolean isTemplated) { this.isTemplated = isTemplated; }

	
	public Boolean isTemplateLoadError() { return isTemplateLoadError; }
	public void setTemplateLoadError(Boolean isTemplateLoadError) { this.isTemplateLoadError = isTemplateLoadError; }

	public Boolean isSelected() { return isSelected; }
	public void setSelected(Boolean isSelected) { this.isSelected = isSelected; }
}
