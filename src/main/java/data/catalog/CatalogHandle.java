package metaindex.data.catalog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;

import metaindex.data.AAccessControledData;
import metaindex.data.IBufferizedData;
import metaindex.data.IBufferizedDataHandle;
import metaindex.data.IObserver;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.test.TestCommunity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Bean storing community data
 * @author laurent
 *
 */
public class CatalogHandle extends AAccessControledData<ICatalog> implements ICatalogHandle {

	private static Log log = LogFactory.getLog(CatalogHandle.class);

	private static boolean tmp_display_todo_notify_changes = true;
		
	private boolean isSynchronized=false;
	Integer selectedElementId=0;	
	//Map<Integer,IElementData> bufCatalogElements = new HashMap<Integer,IElementData>();
	
	public CatalogHandle(IUserProfileData userProfile, ICatalog refData) {
		super(userProfile,refData);
		this.getRefData().addObserver(this);
		if (!this.getRefData().getUserElements(userProfile).isEmpty()) {	
			Integer idOfFirstEl = this.getRefData().getUserElements(userProfile).get(0).getElementId();
			
			try {
				this.setSelectedElement(idOfFirstEl);
			} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
				unsetSelectedElement();
			}			
		}
		
	}

	@Override
	public void invalidate() {
		isSynchronized=false;
		//bufCatalogElements.clear();		
	}
	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}

	@Override
	public int getCatalogId() {
		this.checkReadableByUser();
		return this.getRefData().getCatalogId();
	}

	@Override
	public void setCatalogId(int catalogID) {
		this.checkReadableByUser();
		this.getRefData().setCatalogId(catalogID);
		
	}
	
	@Override
	public Integer getCommunityId() {
		this.checkReadableByUser();
		return this.getRefData().getCommunityId();
	}

	@Override
	public void setCommunityId(int communityId) {
		this.checkReadableByUser();
		this.getRefData().setCommunityId(communityId);
		
	}

	@Override
	public String getName() {
		this.checkReadableByUser();
		return this.getRefData().getName();
		
	}

	@Override
	public void setName(String name) {
		this.checkReadableByUser();
		this.getRefData().setName(name);
		
	}

	@Override
	public String getComment() {
		this.checkReadableByUser();
		return this.getRefData().getComment();
	}

	@Override
	public void setComment(String comment) {
		this.checkReadableByUser();
		this.getRefData().setComment(comment);
		
	}

	@Override
	public String getSearchQuery() {
		this.checkReadableByUser();
		return this.getRefData().getSearchQuery();
	}

	@Override
	public void setSearchQuery(String searchQuery) {
		this.checkReadableByUser();
		this.getRefData().setSearchQuery(searchQuery);
		
	}

	@Override
	public boolean isDynamic() {
		this.checkReadableByUser();
		return this.getRefData().isDynamic();
	}

	public List<IElementHandle> getElements() {
		this.checkReadableByUser();		
		return this.getRefData().getUserElements(this.getUserProfile());
		
	}

	@Override
	public int getElementsCount() {
		this.checkReadableByUser();
		return this.getRefData().getElementsCount();
	}
	@Override
	public IElementHandle getElement(int elementId) {
		this.checkReadableByUser();
		
		IElement el = this.getRefData().getElement(elementId);
		if (el==null) { return null; }
		
		return new ElementHandle(this.getUserProfile(),el);

	}

	
	public IElementHandle getSelectedElement() {
		this.checkReadableByUser();
		try 
		{ 			
			return new ElementHandle(this.getUserProfile(),this.getRefData().getElement(selectedElementId)); 
		}
		catch (DataAccessErrorException e) 
		{ 
			// required element does not exist (anymore) in catalog
			// so we get the first element of the catalog instead
			if (!this.getRefData().getElements().isEmpty()) {
				IElement el = this.getRefData().getElements().get(0);
				selectedElementId = el.getElementId();
				return new ElementHandle(this.getUserProfile(),this.getRefData().getElement(selectedElementId));
			} else { return null; }
		}
		
		
		
	}

	@Override
	public boolean isVirtual() {
		this.checkReadableByUser();
		return this.getRefData().isVirtual();
	}
	public List<Integer> getStaticElementsList() { return this.getRefData().getStaticElementsList(this.getUserProfile()); }
	public List<Integer> getDynamicElementsList() { return this.getRefData().getDynamicElementsList(this.getUserProfile()); }

	/**
	 * Set the currently selected item. If given Id==0 then set selected element to null.
	 * @param elementId the id (in DB) of the required element
	 * @throws DataAccessErrorException if element does not belong to the catalog
	 * @throws DataReferenceErrorException 
	 * @throws DataAccessConstraintException 
	 */
	public void setSelectedElement(int elementId) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.checkReadableByUser();
		IElement e = null;
		if (elementId!=0) {
			try {
				e = this.getRefData().getElement(elementId);
				
			} catch (DataAccessErrorException|DataAccessConstraintException ex) {
				log.error("Element '"+elementId+"' does not belong to catalog '"+this.getCatalogId()+"'. "
															+"Unable to set it as catalogs' selected element : "+ex.getMessage());
				throw ex;
			}
		}
		if (e!=null) { e.updateFull(this.getUserProfile()); };
		selectedElementId=elementId;
		
	}

	@Override
	public IElementHandle nextElement() throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		int nextPos;
	
		Integer curPos=0;
		if (this.getRefData().getElements().isEmpty()) { selectedElementId=0; return null;}
		
		if (this.getSelectedElement()!=null) { curPos=this.getSelectedElement().indexIn(this); }		
		if (curPos==this.getRefData().getElements().size()-1) { nextPos=0; }
		else { nextPos=curPos+1; }
		
		setSelectedElement(this.getRefData().getElements().get(nextPos).getElementId());
		return this.getSelectedElement();
	}
	
	/**
	 * Set the selectedUserElement to an empty object
	 */
	@Override
	public void unsetSelectedElement()  {
		this.checkReadableByUser();
		selectedElementId=0;
	}
	
	
	public void addStaticElement (int elementToAddId) throws DataAccessErrorException, DataAccessConstraintException
	{
		this.checkWritableByUser();
		this.getRefData().addStaticElement(this.getUserProfile(), elementToAddId);
		//this.bufCatalogElements.get(elementToAddId);
	}
	
	public boolean isStaticElement(int elementId) {
		this.checkReadableByUser();
		return this.getRefData().isStaticElement(elementId);
	}
	
	public int getNbStaticElements() {
		this.checkReadableByUser();
		return this.getRefData().getNbStaticElements();
	}

	public int getNbDynamicElements() {
		this.checkReadableByUser();
		return this.getRefData().getNbDynamicElements();
	}

	@Override
	public void removeStaticElement(int elementToAddId)
			throws DataAccessErrorException, DataAccessConstraintException {
		this.checkWritableByUser();
		
		this.getRefData().removeStaticElement(this.getUserProfile(), elementToAddId);
		
	}

	@Override
	public Map<Integer, IMetadataHandle> getMetadatas() {
		this.checkReadableByUser();
		
		return this.getRefData().getUserMetadatas(this.getUserProfile());
	}

	@Override
	public Map<Integer, IDatasetHandle> getDatasets() {		
		this.checkReadableByUser();
		
		return this.getRefData().getUserDatasets(this.getUserProfile());				
	}


	@Override
	public void notifyChange(ICatalog observedObject) {
		
		if (tmp_display_todo_notify_changes) {
			log.error("### TODO: notify catalog changes");
			tmp_display_todo_notify_changes=false;
		}
			
		
		// choose a pre-selected element
		if (this.getRefData().getElementsCount()>0) { 
			// if previously selected element is still in the catalog we select it
			// if not we select the first element o fthe array
			if (this.getUserProfile().getSelectedElement()!=null) { 
					try { this.setSelectedElement(selectedElementId); } 
					catch (Exception e) 
					{ 										
						try {
							List<IElementHandle> elementsList = this.getElements();
							if (elementsList.size()>0) { this.setSelectedElement(elementsList.get(0).getElementId()); }
						} catch (DataAccessErrorException | DataAccessConstraintException
								| DataReferenceErrorException e1) {
							e1.printStackTrace();
						}						
					}
			}
			else { 
				if (this!=null) 
				{ 
					// if currently selected element is not anymore in the catalog, then we unset it
					try { this.setSelectedElement(this.getRefData().getElements().get(0).getElementId()); }
					catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) { 
						this.unsetSelectedElement(); }		
				}
			}
		}
		// if catalog is empty, just fill in an empty element for safety
		else { if (this!=null) { this.unsetSelectedElement(); }}
		
	}

	@Override
	public void dump(String depthStr) {
		this.getRefData().dump(depthStr);
		
	}

	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		this.getRefData().checkDataDBCompliance();		
	}

}
