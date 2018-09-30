package metaindex.data.metadata.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.element.beans.BeanElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;


public class BeanElementAddMetadataProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementAddMetadataProcess.class);
	
	private String formMetadataName="";
	private String formMetadataComment="";
	private int formTermId=1;
	private int formColumn=0;
	private int formPosition=0;
	private int formDatasetId=0;
	
	@Override
  	public String execute() throws Exception {

		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		if (!this.getLoggedUserProfile().getSelectedElement().isIdentified()) 
		{
			addActionMessage("Sorry, no current element selected for creating new metadata");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		try {
			IElementHandle el = this.getSelectedElement();
			IMetadataHandle m = el.createMetadata(this.getFormDatasetId(), 
										this.getFormMetadataName(), this.getFormMetadataComment(), 
										this.getFormColumn(), this.getFormPosition(), this.getFormTermId());
			// if  new dataset has been implcitely created (which happends when overriding
			// a metadata from a template parent element) then we must also create
			// this dataset in database
			if (m.getParentDataset().getDatasetId()!=this.getFormDatasetId()) {
				m.getParentDataset().create();
			}
			m.create();
		} catch (Exception e) { 
			e.printStackTrace();
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		addActionMessage(getText("createMetadata.success"));				
		
		return status.toString();
	}
  
	public String getFormMetadataName() {
		return formMetadataName;
	}
	public void setFormMetadataName(String metadataName) {
		this.formMetadataName = metadataName;
	}
	public int getFormDatasetId() {
		return formDatasetId;
	}
	public void setFormDatasetId(int datasetId) {
		this.formDatasetId = datasetId;
	}
	public String getFormMetadataComment() {
		return formMetadataComment;
	}
	public void setFormMetadataComment(String metadataComment) {
		this.formMetadataComment = metadataComment;
	}
	public int getFormTermId() {
		return formTermId;
	}
	public void setFormTermId(int termId) {
		this.formTermId = termId;
	}
	public int getFormColumn() {
		return formColumn;
	}
	public void setFormColumn(int createInColumn) {
		this.formColumn = createInColumn;
	}
	public int getFormPosition() {
		return formPosition;
	}
	public void setFormPosition(int createInPosition) {
		this.formPosition = createInPosition;
	} 	
  	

}
