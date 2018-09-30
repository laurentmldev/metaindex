package metaindex.data.dataset.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.IElement;
import metaindex.data.element.beans.BeanElement;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/// We just re-export in DB all data of the Bean
public class BeanElementDeleteDatasetProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementDeleteDatasetProcess.class);

	private int formDatasetId=0;
		
	@Override
  	public String execute() throws Exception {

		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;

		if (!this.getSelectedElement().isIdentified()) 
		{
			addActionMessage("Sorry, no current element selected for deleting dataset");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}

		try 
		{ 
			this.getSelectedElement().getDataset(this.getFormDatasetId()).delete();			
			setSessionLanguage(getLoggedUserProfile().getGuiLanguageShort(), ActionContext.getContext());
			setSessionGuiTheme(getLoggedUserProfile().getGuiThemeShort(),ServletActionContext.getRequest());
		} 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
			
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("updateMetadata.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the operation was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getSelectedElement().updateFull(); }
		catch (Exception e)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}
  
	/**
	 * @return the datasetId
	 */
	public int getFormDatasetId() {
		return formDatasetId;
	}
	/**
	 * @param datasetId the datasetId to set
	 */
	public void setFormDatasetId(int datasetId) {
		this.formDatasetId = datasetId;
	}
  

}
