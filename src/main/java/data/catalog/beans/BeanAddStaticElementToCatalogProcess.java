package metaindex.data.catalog.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.beans.BeanCommunity;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class BeanAddStaticElementToCatalogProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanAddStaticElementToCatalogProcess.class);
	
	private List<IElementHandle> formElementsToAdd=new ArrayList<IElementHandle>();
	private List<ICatalogHandle> formElementStaticCatalogs=new ArrayList<ICatalogHandle>();
	
	@Override
	public String execute()
	{  
				
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		// statically adding it to the required catalogs
		Iterator<ICatalogHandle> it = formElementStaticCatalogs.iterator();
		while (it.hasNext()) {
			ICatalogHandle cur= it.next();
			if (!cur.isVirtual()) {
				Iterator<IElementHandle> itEl = formElementsToAdd.iterator();
				while (itEl.hasNext()) {
					IElementHandle curEl = itEl.next();
					try { 
						cur.addStaticElement(curEl.getElementId());						
					}
					catch (DataAccessErrorException e) {
						e.printStackTrace();
						IElementHandle element = this.getSelectedCommunity().getElement(curEl.getElementId());
						addActionError("Unable to add element '"+element.getName()+"' to catalog '"+cur.getName()+"'");
						status=BeanProcessResult.BeanProcess_ConstraintERROR;
					}	
				}
				
				try {
					cur.updateFull();
				} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
					e.printStackTrace();
					status = BeanProcessResult.BeanProcess_DBERROR;
				}
			}
		}
						
		return status.toString();
	}

	/**
	 * @return the formElementToAddId
	 */
	public int getFormElementToAddId() {
		return formElementsToAdd.get(0).getElementId();
	}

	/**
	 * @param formElementToAddId the formElementToAddId to set
	 */
	public void setFormElementToAddId(int formElementToAddId) {
		formElementsToAdd.add(this.getSelectedCommunity().getElement(new Integer(formElementToAddId)));
	}

	/**
	 * @param formElementStaticCatalogs the formElementStaticCatalogs to set
	 */
	public void setFormElementStaticCatalogs(String[] elementCatalogsIds) {		
		for (int i=0;i<elementCatalogsIds.length;i++) {
			String curId=elementCatalogsIds[i];
			formElementStaticCatalogs.add(this.getSelectedCommunity().getCatalog(new Integer(curId)));
		} 					
	}

	/**
	 * @param formElementsToAdd the formElementsToAdd to set
	 */
	public void setFormElementsToAdd(String elementsIdsStr) {
		// it does not work by default ... don't know why
		String[] elementsIds = elementsIdsStr.split(",");
		for (int i=0;i<elementsIds.length;i++) {
			String curId=elementsIds[i];
			formElementsToAdd.add(this.getSelectedCommunity().getElement(new Integer(curId)));
		}
			
	}
 
	
	// some of the forms use a single catalogId instead of a list, so we emulate a pseudo parameter for it
	public void setFormTargetCatalogId(int targetCatalogId) {
		formElementStaticCatalogs.add(this.getSelectedCommunity().getCatalog(new Integer(targetCatalogId)));
	}
	// some of the forms use a single catalogId instead of a list, so we emulate a pseudo parameter for it
	public int getFormTargetCatalogId() {
		return formElementStaticCatalogs.get(0).getCatalogId();
	}


}
