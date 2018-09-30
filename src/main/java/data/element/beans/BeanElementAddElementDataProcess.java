package metaindex.data.element.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import metaindex.data.catalog.TemplatesElementsCatalog;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanElementAddElementDataProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementAddElementDataProcess.class);
	
	private String newElementName="";
	private String newElementComment="";
	private List<ICatalogHandle> newElementCatalogs=new ArrayList<ICatalogHandle>();
		
	@Override
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		ICommunity c = CommunitiesAccessor.getCommunity(this.getSelectedCommunity().getCommunityId());
		if (!this.getSelectedCommunity().isIdentified()) 
		{
			addActionMessage("Sorry, no current community selected for creating new element");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		log.error("### "+this.getLoggedUserProfile()+" : adding an element to community "+this.getSelectedCommunity().getIdName()
																+" VS "+this.getLoggedUserProfile().getSelectedCommunity().getIdName());
		
		
		IElement newElement = new Element(c);
		newElement.setElementId(CommunitiesAccessor.getNewElementId());
		newElement.setName(this.getNewElementName());
		newElement.setComment(this.getNewElementComment());
		newElement.setCommunityId(this.getSelectedCommunity().getCommunityId());
		if (this.getSelectedCatalog().getCatalogId()==TemplatesElementsCatalog.TEMPLATES_ELEMENTS_CATALOG_ID) {
			newElement.setTemplate(true);
		}
		
		try { 			
			// first creating element
			newElement.create(this.getLoggedUserProfile());
			addActionMessage(getText("createMetadata.success"));
			
			// then statically adding it to the required catalogs
			Iterator<ICatalogHandle> it = newElementCatalogs.iterator();
			while (it.hasNext()) {
				ICatalogHandle cur= it.next();
				if (cur.getCatalogId()!=0) {
					try { 
						cur.addStaticElement(newElement.getElementId());
						
						// Set new element as selected element for current catalog
						if (this.getSelectedCatalog().getCatalogId() == cur.getCatalogId()) {
							this.getSelectedCommunity().getSelectedCatalog().setSelectedElement(newElement.getElementId());
						}
					}
					catch (DataAccessErrorException e) {
						e.printStackTrace();
						addActionError("Unable to add element '"+newElement.getName()+"' to catalog '"+cur.getName()+"'");
					}
				}
			}
			this.getSelectedCommunity().updateFull();
		} 
		catch (DataAccessErrorException|DataAccessConstraintException e) 
		{ 
			e.printStackTrace();
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}			
			
		return status.toString();
	}
  	@Override
  	public void prepare() throws Exception { 
  		super.prepare();  		
  	}
	public String getNewElementName() {
		return newElementName;
	}
	public void setNewElementName(String elementName) {
		this.newElementName = elementName;
	}
	/**
	 * @return the newElementComment
	 */
	public String getNewElementComment() {
		return newElementComment;
	}
	/**
	 * @param newElementComment the newElementComment to set
	 */
	public void setNewElementComment(String newElementComment) {
		this.newElementComment = newElementComment;
	}
	
	/**
	 * @param newElementCatalogs the newElementCatalogs to set
	 */
	public void setNewElementCatalogs(String[] newElementCatalogsIds) {
		
		for (int i=0;i<newElementCatalogsIds.length;i++) {
			String curId=newElementCatalogsIds[i];
			newElementCatalogs.add(this.getSelectedCommunity().getCatalog(new Integer(curId)));
		}
		
	
	}

  	
  	

}
