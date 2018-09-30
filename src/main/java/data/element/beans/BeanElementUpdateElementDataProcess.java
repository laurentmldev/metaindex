package metaindex.data.element.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.AllElementsCatalog;
import metaindex.data.catalog.TemplatesElementsCatalog;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/// We just re-export in DB all data of the Bean
public class BeanElementUpdateElementDataProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementUpdateElementDataProcess.class);

	private boolean isCurrentElementTemplateRef = false;
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		isCurrentElementTemplateRef=this.getSelectedElement().isTemplate();
	}
	
	@Override
  	public String execute() throws Exception {

		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;

		if (!this.getSelectedElement().isIdentified()) 
		{
			addActionMessage("Sorry, no current element selected for creating new dataset");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}

		try 
		{ 
			this.getSelectedElement().commitFull();	
			
			setSessionLanguage(getLoggedUserProfile().getGuiLanguageShort(), ActionContext.getContext());
			setSessionGuiTheme(getLoggedUserProfile().getGuiThemeShort(),ServletActionContext.getRequest());
		} 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			e.printStackTrace();
			this.addActionError(e.getMessage());
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
			
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("updateMetadata.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { 
			int selectedElId=this.getSelectedElement().getElementId();
			boolean isSaticEl=this.getSelectedCatalog().isStaticElement(selectedElId);
			this.getSelectedElement().updateFull();
			// if we changed the 'isTemplate' status, we need to reorder the catalogs contents
			if (this.getSelectedElement().isTemplate()!=this.isCurrentElementTemplateRef) {
				this.getSelectedCommunity().updateFull();
				// move to appropriate catalog if necessary
				if (!isSaticEl) {
					if (this.getSelectedCommunity().getElement(selectedElId).isTemplate()) {
						this.getSelectedCommunity().setSelectedCatalog(
								this.getSelectedCommunity().getCatalog(TemplatesElementsCatalog.TEMPLATES_ELEMENTS_CATALOG_ID));
						this.getSelectedCatalog().setSelectedElement(selectedElId);
					} else {
						if (this.getSelectedCatalog().getCatalogId()==TemplatesElementsCatalog.TEMPLATES_ELEMENTS_CATALOG_ID) {
							this.getSelectedCommunity().setSelectedCatalog(
								this.getSelectedCommunity().getCatalog(AllElementsCatalog.ALL_ELEMENTS_CATALOG_ID));
							this.getSelectedCatalog().setSelectedElement(selectedElId);
						}
					}
				}
			}
		}
		catch (Exception e)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}
  

}
