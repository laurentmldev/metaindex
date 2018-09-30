package metaindex.data.catalog.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.IElement;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanCommunityUpdateCatalogProcess extends BeanCommunity  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunityUpdateCatalogProcess.class);
	
	private String formCatalogName="";
	private String formCatalogComment="";
	private String formCatalogSearchQuery="";
		
	@Override 
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		if (!this.getSelectedCommunity().isIdentified()) 
		{
			addActionMessage("Sorry, no current community selected for creating new catalog");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		ICatalogHandle modifiedCatalog = this.getSelectedCommunity().getSelectedCatalog();
		modifiedCatalog.setName(this.getFormCatalogName());
		modifiedCatalog.setComment(this.getFormCatalogComment());
		modifiedCatalog.setSearchQuery(this.getFormCatalogSearchQuery());
		
		if (!modifiedCatalog.isVirtual()) {
			try { modifiedCatalog.commit(); } 
			catch (DataAccessErrorException|DataAccessConstraintException e) 
			{ 
				e.printStackTrace();
				addActionError(getText("error.DBprocess"));
				status = BeanProcessResult.BeanProcess_DBERROR; 
			}
		}
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("catalog.create.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try {  modifiedCatalog.updateFull(); }
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

	public String getFormCatalogName() {
		return formCatalogName;
	}
	public void setFormCatalogName(String formCatalogName) {
		this.formCatalogName = formCatalogName;
	}
	public String getFormCatalogComment() {
		return formCatalogComment;
	}
	public void setFormCatalogComment(String formCatalogComment) {
		this.formCatalogComment = formCatalogComment;
	}
	public String getFormCatalogSearchQuery() {
		return formCatalogSearchQuery;
	}
	public void setFormCatalogSearchQuery(String formCatalogSearchQuery) {
		this.formCatalogSearchQuery = formCatalogSearchQuery;
	}

}
