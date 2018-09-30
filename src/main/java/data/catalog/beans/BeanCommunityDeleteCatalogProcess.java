package metaindex.data.catalog.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanCommunityDeleteCatalogProcess extends BeanCommunity  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunityDeleteCatalogProcess.class);
	
	private int formCatalogId=0;
	
		
	@Override 
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;

		if (!this.getSelectedCommunity().isIdentified()) 
		{
			addActionMessage("Sorry, no current community selected for creating new catalog");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		ICatalogHandle catalogToDelete = this.getSelectedCommunity().getCatalog(getFormCatalogId());
		boolean  switchSelectedCatalog=this.getSelectedCommunity().getSelectedCatalog().getCatalogId()==getFormCatalogId();
		if (catalogToDelete==null) {
			addActionError("Sorry, catalog with id '"+getFormCatalogId()+"' not found.");
			return BeanProcessResult.BeanProcess_ERROR.toString();
		}

		try { catalogToDelete.delete(); } 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
			
		// if we've deleted the currently selected catalog, we go to the default "All" catalog.
		if (switchSelectedCatalog) {
			this.getSelectedCommunity().setSelectedCatalog(this.getSelectedCommunity().getCatalog(0));
		}
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("catalog.create.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getSelectedCommunity().updateFull(); }
		catch (Exception e)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}


	public int getFormCatalogId() {
		return formCatalogId;
	}


	public void setFormCatalogId(int formCatalogId) {
		this.formCatalogId = formCatalogId;
	}



}
