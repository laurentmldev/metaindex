package metaindex.data.metadata.beans;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;

import metaindex.data.dataset.beans.BeanElementDeleteDatasetProcess;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/// Same processing than for Dataset, since we just re-export in DB all data of the Bean
public class BeanElementDeleteMetadataProcess extends BeanElementDeleteDatasetProcess  {

	private static final long serialVersionUID = -3096098703226452138L;
	private int formMetadataId=0;
	
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
			this.getSelectedElement().getMetadata(this.getFormMetadataId()).delete();			
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
	public int getFormMetadataId() {
		return formMetadataId;
	}
	/**
	 * @param metadataId the datasetId to set
	 */
	public void setFormMetadataId(int metadataId) {
		this.formMetadataId = metadataId;
	}
  
}
