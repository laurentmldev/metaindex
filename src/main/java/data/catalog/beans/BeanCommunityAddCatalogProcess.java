package metaindex.data.catalog.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.Catalog;
import metaindex.data.community.ICommunity;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanCommunityAddCatalogProcess extends BeanCommunity  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunityAddCatalogProcess.class);
	
	private String newCatalogName="";
	
		
	@Override 
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;

		ICommunity c = CommunitiesAccessor.getOrLoadCommunity(this.getLoggedUserProfile(),this.getSelectedCommunity().getIdName());
		Catalog newCatalog = new Catalog(c);
		newCatalog.setName(this.getNewCatalogName());
		newCatalog.setCommunityId(this.getSelectedCommunity().getCommunityId());
		
		try { newCatalog.create(this.getLoggedUserProfile()); } 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			e.printStackTrace();
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
			
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("catalog.create.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getSelectedCommunity().update(); }
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
	public String getNewCatalogName() {
		return newCatalogName;
	}
	public void setNewCatalogName(String newCatalogName) {
		this.newCatalogName = newCatalogName;
	}

}
