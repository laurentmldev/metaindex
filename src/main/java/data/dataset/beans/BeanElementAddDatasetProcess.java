package metaindex.data.dataset.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.beans.BeanElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanElementAddDatasetProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementAddDatasetProcess.class);
	
	private String formDatasetName="";
	private String formDatasetComment="";
	private int formDatasetPosition=1;
	private int formDatasetElementId=0;
		
	@Override
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		ICommunity c = CommunitiesAccessor.getCommunity(this.getSelectedCommunity().getCommunityId());
		if (!this.getSelectedElement().isIdentified()) 
		{
			addActionMessage("Sorry, no current element selected for creating new dataset");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		IDatasetHandle newDataset = new DatasetHandle(this.getLoggedUserProfile(),new Dataset(c));
		newDataset.setElementId(this.getFormDatasetElementId());
		newDataset.setName(this.getFormDatasetName());
		newDataset.setComment(this.getFormDatasetComment());
		newDataset.setLayoutPosition(this.getFormDatasetPosition());
		
		try { newDataset.create(); } 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
			
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("createMetadata.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getSelectedElement().updateFull(); }
		catch (Exception e)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}
  	@Override
  	public void prepare() throws Exception { 
  		super.prepare();
  	}
  	
  	public String getFormDatasetName() {
		return formDatasetName;
	}
	public void setFormDatasetName(String formDatasetName) {
		this.formDatasetName = formDatasetName;
	}
	public String getFormDatasetComment() {
		return formDatasetComment;
	}
	public void setFormDatasetComment(String formDatasetComment) {
		this.formDatasetComment = formDatasetComment;
	}
	public int getFormDatasetPosition() {
		return formDatasetPosition;
	}
	public void setFormDatasetPosition(int formDatasetPosition) {
		this.formDatasetPosition = formDatasetPosition;
	}
	public int getFormDatasetElementId() {
		return formDatasetElementId;
	}
	public void setFormDatasetElementId(int formDatasetElementId) {
		this.formDatasetElementId = formDatasetElementId;
	}
	  	
}
