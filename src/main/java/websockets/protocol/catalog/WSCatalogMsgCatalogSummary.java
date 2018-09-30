package metaindex.websockets.protocol.catalog;



import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSCatalogMsgCatalogSummary extends AWSJsonMessage {

	Integer catalogId=0;
	String name = "";
	String comment = "";
	Integer nbElements=0;
	Boolean isVirtual=false;
	Boolean isDynamic=false;
	Boolean isSelected=false;
	List<Integer> staticElementsIds =  new ArrayList<Integer>();
	String dynamicElementsFilter =  "*";
	Integer selectedElementId = 0;
	
	public WSCatalogMsgCatalogSummary() {}
	
	public WSCatalogMsgCatalogSummary(ICatalogHandle c) {
		this.setCatalogName(c.getName());
		this.setCatalogComment(c.getComment());
		this.setCatalogId(c.getCatalogId());
		this.setCatalogNbElements(c.getElementsCount());
		this.setVirtual(c.isVirtual());
		this.setDynamic(c.isDynamic());
		this.setStaticElementsIds(c.getStaticElementsList());
		this.setDynamicElementsFilter(c.getSearchQuery());
		this.setSelected(c.getUserProfile().getSelectedCatalog().getCatalogId()==c.getCatalogId());
		if (c.getSelectedElement()!=null) { this.setSelectedElementId(c.getSelectedElement().getElementId()); }
	}
	
	@Override
	public String getMsgType() {
		return "catalog-summary";
	}

	public Integer getCatalogId() { return catalogId; }
	public void setCatalogId(Integer newCatalogId) { catalogId=newCatalogId; }
	
	public String getCatalogName() { return name; }
	public void setCatalogName(String newText) { name=newText; }
	
	public String getCatalogComment() { return comment; }
	public void setCatalogComment(String mycomment) { comment = mycomment; }
	
	public Integer getSelectedElementId() { return this.selectedElementId; }
	public void setSelectedElementId(Integer selectedElementId) { this.selectedElementId=selectedElementId; }
	
	public String getDynamicElementsFilter() { return dynamicElementsFilter; }
	public void setDynamicElementsFilter(String dynamicElementsFilter) { this.dynamicElementsFilter=dynamicElementsFilter; }
	
	public Integer getCatalogNbElements() { return nbElements; }
	public void setCatalogNbElements(Integer nbElems) { nbElements=nbElems; }
	
	public Boolean isVirtual() { return isVirtual; }
	public void setVirtual(Boolean isVirtual) { this.isVirtual=isVirtual; }
	
	public Boolean isDynamic() { return isDynamic; }
	public void setDynamic(Boolean isDynamic) { this.isDynamic=isDynamic; }
	
	public Boolean isSelected() { return isSelected; }
	public void setSelected(Boolean isSelected) { this.isSelected=isSelected; }
	
	public List<Integer> getStaticElementsIds() { return staticElementsIds; }
	public void setStaticElementsIds(List<Integer> list) { staticElementsIds = list; }
	
	
}
