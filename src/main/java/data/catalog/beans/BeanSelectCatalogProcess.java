package metaindex.data.catalog.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;

import metaindex.data.community.beans.BeanCommunity;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class BeanSelectCatalogProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanSelectCatalogProcess.class);
	
	private int formSelectedCatalogId=0;
	
	@Override
	public String execute()
	{  
		
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
			
		try {
				// set the selected catalog
				this.getSelectedCommunity().setSelectedCatalog(this.getSelectedCommunity().getCatalog(this.getFormSelectedCatalogId()));
		} 
		catch (DataAccessConstraintException|DataAccessErrorException | DataReferenceErrorException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			e.printStackTrace();
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
								
		return status.toString();
	}

	/**
	 * @return the newCatalogId
	 */
	public int getFormSelectedCatalogId() {
		return formSelectedCatalogId;
	}

	/**
	 * @param newCatalogId the newCatalogId to set
	 */
	public void setFormSelectedCatalogId(int newCatalogId) {
		this.formSelectedCatalogId = newCatalogId;
	}
	
}
