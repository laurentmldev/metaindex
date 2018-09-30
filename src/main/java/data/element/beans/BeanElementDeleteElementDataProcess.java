package metaindex.data.element.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

/// We just re-export in DB all data of the Bean
public class BeanElementDeleteElementDataProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementDeleteElementDataProcess.class);

	private List<IElementHandle> formElementsToDelete=new ArrayList<IElementHandle>();
		
	@Override
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		 
		try {
			this.getSelectedCommunity().deleteElements(this.getLoggedUserProfile(), formElementsToDelete);
			this.getSelectedCommunity().updateFull();			
		} 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			e.printStackTrace();
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
	
		setSessionLanguage(getLoggedUserProfile().getGuiLanguageShort(), ActionContext.getContext());
		setSessionGuiTheme(getLoggedUserProfile().getGuiThemeShort(),ServletActionContext.getRequest());
		
		if (status==BeanProcessResult.BeanProcess_SUCCESS){ addActionMessage(getText("updateMetadata.success")); }	
		
		return status.toString();
	}
  
	/**
	 * @return the datasetId
	 */
	public int getFormElementId() {
		return formElementsToDelete.get(0).getElementId();
	}
	/**
	 * @param elementId the datasetId to set
	 */
	public void setFormElementId(int elementId) {
		try { 
			IElementHandle curEl = (IElementHandle)this.getSelectedCommunity().getElement(new Integer(elementId));
			this.formElementsToDelete.add(curEl);
		} catch (DataAccessErrorException e) { this.addActionError(e.getMessage()); }
	}


	/**
	 * @param formElementsToDelete the formElementsToDelete to set
	 * If given ID is '-1', then we select all elements of current catalog
	 */
	public void setFormElementsIds(String elementsToDeleteIdsStr) {
		String[] elementsToDeleteIds = elementsToDeleteIdsStr.split(","); 
		for (int i=0;i<elementsToDeleteIds.length;i++) {
			try { 
				Integer elId=new Integer(elementsToDeleteIds[i]);
				if (elId==-1) {
					formElementsToDelete.addAll(this.getSelectedCatalog().getElements());
					return;
				}
				IElementHandle curEl = (IElementHandle) this.getSelectedCommunity().getElement(elId);
				this.formElementsToDelete.add(curEl);		
			} catch (DataAccessErrorException e) { this.addActionError(e.getMessage()); }			
		}
		
	}
  

}
