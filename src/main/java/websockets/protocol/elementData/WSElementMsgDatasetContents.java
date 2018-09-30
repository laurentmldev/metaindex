package metaindex.websockets.protocol.elementData;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.protocol.AWSJsonMessage;


public class WSElementMsgDatasetContents extends AWSJsonMessage {

	Integer datasetId=0;
	Integer elementId=0;
	String datasetName = "";
	String datasetComment = "";
	Integer layoutNbColumns= 0;
	Integer layoutPosition= 0;
	Integer nbMetadata= 0;
	Boolean layoutAlwaysExpand= true;
	Boolean layoutDoDisplayName= true;
	Boolean isTemplated= false;
	List<List<Integer> > columnsMetadata = new ArrayList<List<Integer> >();
	
	
	public WSElementMsgDatasetContents() {}
	
	public WSElementMsgDatasetContents(IDatasetHandle d, IUserProfileData user) {
		this.setDatasetId(d.getDatasetId());
		this.setElementId(d.getElementId());
		this.setDatasetName(d.getName());
		this.setDatasetComment(d.getComment());
		this.setLayoutNbColumns(d.getLayoutNbColumns());
		this.setLayoutPosition(d.getLayoutPosition());
		this.setLayoutAlwaysExpand(d.isLayoutAlwaysExpand());
		this.setLayoutDoDisplayName(d.isLayoutDoDisplayName());
		this.setTemplated(d.isTemplated());
		
		List<List<IMetadataHandle> > columns = d.getColumnsMetadata();
		this.columnsMetadata.clear();
		Iterator<List<IMetadataHandle> > itCols = columns.iterator();
		while (itCols.hasNext()) {
			List<IMetadataHandle> curCol = itCols.next();
			List<Integer> curColIds= new ArrayList<Integer>();
			Iterator<IMetadataHandle> colMetadatasIt = curCol.iterator();
			while (colMetadatasIt.hasNext()) {
				IMetadataHandle curMetadata = colMetadatasIt.next();
				curColIds.add(curMetadata.getMetadataId());
				nbMetadata++;
			}
			this.columnsMetadata.add(curColIds);
		}
				
	}
	
	@Override
	public String getMsgType() {
		return "element-datasetcontents";
	}

	public Integer getDatasetId() { return datasetId; }
	public void setDatasetId(Integer newId) { datasetId=newId; }
	
	public Integer getElementId() { return elementId; }
	public void setElementId(Integer newElementId) { elementId=newElementId; }
	
	public List<List<Integer> > getColumnsMetadata() { return columnsMetadata; }
	public void setColumnsMetadata(List<List<Integer> > columnsMetadata) { this.columnsMetadata=columnsMetadata; }
	
	public String getDatasetName() { return datasetName; }
	public void setDatasetName(String newText) { datasetName=newText; }
	
	public String getDatasetComment() { return datasetComment; }
	public void setDatasetComment(String mycomment) { datasetComment = mycomment; }
	
	public Integer getLayoutNbColumns() { return layoutNbColumns; }
	public void setLayoutNbColumns(Integer layoutNbColumns) { this.layoutNbColumns = layoutNbColumns; }
	
	public Integer getLayoutPosition() { return layoutPosition; }
	public void setLayoutPosition(Integer layoutPosition) { this.layoutPosition = layoutPosition; }

	public Integer getNbMetadata() { return nbMetadata; }
	public void setNbMetadata(Integer nbMetadata) { this.nbMetadata = nbMetadata; }

	public Boolean isLayoutAlwaysExpand() { return layoutAlwaysExpand; }
	public void setLayoutAlwaysExpand(Boolean layoutAlwaysExpand) { this.layoutAlwaysExpand = layoutAlwaysExpand; }

	public Boolean isLayoutDoDisplayName() { return layoutDoDisplayName; }
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) { this.layoutDoDisplayName = layoutDoDisplayName; }
	
	public Boolean isTemplated() { return isTemplated; }
	public void setTemplated(Boolean isTemplated) { this.isTemplated = isTemplated; }
}
