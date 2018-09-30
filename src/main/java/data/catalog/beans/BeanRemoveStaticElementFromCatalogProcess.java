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
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class BeanRemoveStaticElementFromCatalogProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanRemoveStaticElementFromCatalogProcess.class);
	
	
	private List<IElementHandle> formElementsToRemove=new ArrayList<IElementHandle>();
	private int formCatalogId=0;
	
	@Override
	public String execute()
	{  
				
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		// then statically remove it from the required catalog		
		ICatalogHandle cur= this.getSelectedCommunity().getCatalog(this.getFormCatalogId());
		if (!cur.isVirtual()) {
			Iterator<IElementHandle> it = formElementsToRemove.iterator();
			while (it.hasNext()){
				IElementHandle el = it.next();
				try { cur.removeStaticElement(el.getElementId()); }
				catch (DataAccessErrorException e) {
					e.printStackTrace();
					addActionError("Unable to remove element '"+el.getName()+"' from catalog '"+cur.getName()+"'");
					status=BeanProcessResult.BeanProcess_ConstraintERROR;
				}
			}				
				
			try {
				cur.updateFull();
			} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
					e.printStackTrace();
					addActionError("Unable to reload catalog '"+cur.getCatalogId()+"' contents.");
					status=BeanProcessResult.BeanProcess_DBERROR;
			}  
			
		}
		
						
		return status.toString();
	}

	/**
	 * @return the formElementToAddId
	 */
	public int getFormElementToRemoveId() {
		return formElementsToRemove.get(0).getElementId();
	}

	/**
	 * @param formElementToRemoveId the formElementToAddId to set
	 */
	public void setFormElementToRemoveId(int formElementToRemoveId) {
		IElementHandle el=this.getSelectedCommunity().getElement(new Integer(formElementToRemoveId));
		
		formElementsToRemove.add(el);
	}

	/**
	 * @param formElementToRemoveId the formElementToAddId to set
	 */
	public void setFormElementsToRemoveIds(String formElementsToRemoveIdsStr) {
		
		String[] formElementsToRemoveIds = formElementsToRemoveIdsStr.split(",");
		for (int i=0;i<formElementsToRemoveIds.length;i++) {
			String elId = formElementsToRemoveIds[i];
			try {
				IElementHandle el=this.getSelectedCommunity().getElement(new Integer(elId));
				formElementsToRemove.add(el);
			} catch (DataAccessErrorException e) { this.addActionError(e.getMessage()); }
			
		}
		
	}


	/**
	 * @return the formCatalogId
	 */
	public int getFormCatalogId() {
		return formCatalogId;
	}

	/**
	 * @param formCatalogId the formCatalogId to set
	 */
	public void setFormCatalogId(int formCatalogId) {
		this.formCatalogId = formCatalogId;
	}

	
}
