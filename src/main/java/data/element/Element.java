package metaindex.data.element;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.CompositeDataset;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.CompositeMetadata;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AJsonEncodable;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class Element extends ACommunitySubdata<IElement> implements IElement {

	private Log log = LogFactory.getLog(Element.class);
	
	private boolean isOutDated=false;
	private boolean isSynchronized=false;
	private boolean loadError = false;
	private int elementId=0;
	private String name="";	
	private String comment="";	
	private String thumbnailUrl="";
	private boolean isTemplate=false;
	private Integer templateRefElementId=0;	
		
	List<IDataset> datasets = new ArrayList<IDataset>();
	Map<Integer,IDataset> datasetsMap = new HashMap<Integer,IDataset>();

	public void dump(String depthStr) {
		log.error(depthStr+"Element "+this.getElementId()+" : "+this.getName()+" : isTemplated="+this.isTemplated()+",isTemplate="+this.isTemplate());
		Iterator<IDataset> it = this.getDatasets().iterator();		
		while (it.hasNext()) {
			IDataset d = it.next();
			d.dump(depthStr+"  ");
		}
	}
	
	protected class ElementDataExchange implements IElementContents {
		IElement myElementData;
		ElementDataExchange(IElement e) {myElementData=e;}
		@Override public String getName() { return myElementData.getName(); }		
		@Override public void setName(String name) { myElementData.setName(name); }
		@Override public String getComment() { return myElementData.getComment(); }
		@Override public void setComment(String comment) { myElementData.setComment(comment); }
		
		@Override public Integer getElementId() { return myElementData.getElementId(); }
		@Override public void setElementId(Integer elId) { myElementData.setElementId(elId); }
		@Override public boolean isTemplate() { return myElementData.isTemplate(); }
		@Override public void setTemplate(Boolean isTemplate) { myElementData.setTemplate(isTemplate); }
		@Override public Integer getTemplateRefElementId() { return myElementData.getTemplateRefElementId(); }
		@Override public void setTemplateRefElementId(Integer refId) { myElementData.setTemplateRefElementId(refId); }
		@Override public String getThumbnailUrl() { return myElementData.getThumbnailUrl(); }
		@Override public void setThumbnailUrl(String url) { myElementData.setThumbnailUrl(url);}
		@Override public boolean hasThumbnail() { return myElementData.hasThumbnail(); }
		
		
	}
	public static class NameComparator implements Comparator<IElement> {

		@Override
		public int compare(IElement o1, IElement o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());			
		}
		
	}
	
	
	public Element(ICommunity myCommunity) {
		super(myCommunity,CommunitiesAccessor.getDataAccessors());	
	}
	@Override
	public boolean isTemplateLoadError() { return loadError; }
	
	private void setTemplateLoadError(boolean val) { loadError=val; }
	
	@Override
	public void moveMetadata(IUserProfileData activeUser, IMetadata movedMetadata,int newDatasetId, int newCol, int newPos) throws DataReferenceErrorException  {
				
				// check that requested dataset exist and belongs to currently selected element
				IDataset targetDataset=this.getDataset(newDatasetId);				
				
				// if not found return an error
				if (targetDataset==null) {
					throw new DataReferenceErrorException("Sorry, target dataset '"+newDatasetId+"' not found (anymore?) "
										+"in currently selected element '"+this.getElementId()+"'");					
				}		
				
				movedMetadata.setParentDataset(targetDataset);
				movedMetadata.setLayoutColumn(newCol);
				movedMetadata.setLayoutPosition(newPos);
				
				// set position of other element of the set
				List<List<IMetadata> > sortedMetadata = this.getDataset(newDatasetId).getColumnsMetadata();
				// List index start form 0, so we -1
				int newColIdx=newCol-1;
				if (newColIdx<0) { newColIdx=0; }
				List<IMetadata> curCol=sortedMetadata.get(newColIdx);
				if (curCol==null){
					throw new DataReferenceErrorException("Sorry, target dataset '"+newDatasetId+"' don't have column '"+newCol+"'"
							+" in currently selected element '"+this.getElementId()+"'");
				}				
				Iterator<IMetadata> it = curCol.iterator();
				int lastPos=newPos;
				while(it.hasNext()) {
					IMetadata cur=it.next();
					if (cur.getMetadataId()!=movedMetadata.getMetadataId() && cur.getLayoutPosition()==lastPos) {
						cur.setLayoutPosition(++lastPos);						
					}			
				}				 												 
	}
	
	private IDataset addDatasetIfNeeded(IUserProfileData activeUser, Integer datasetId) 
											throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
				
				Boolean isDatasetFound=true;
				IDataset dataset = null;
				try {  
					dataset = getDataset(datasetId); 
				} catch (Exception e) {
					// if required dataset not directly found, before creating a CompositeDataset referencing it,
					// we check if there isn't already one existing 
					dataset = null;
					Iterator<IDataset> it = this.getDatasets().iterator();
					while (it.hasNext()) {
						IDataset d = it.next();
						if (d.isTemplated()) {
							CompositeDataset cd = (CompositeDataset) d;
							if (cd.getTemplateReference()!=null && cd.getTemplateReference().getDatasetId()==datasetId) {
								dataset = d;
								break;
							}
						}
					}
					isDatasetFound=dataset!=null; 
				}
				// if required dataset is not found in current element, that means that is was a dataset from template,
				// and so we must create a new dataset in current element matching the one in the template, 
				// before creating the required metadata itself
				if (isDatasetFound &&
						dataset.getElementId().equals(getElementId())) { return dataset; }
					
				if (!isTemplated()) {						
					throw new DataReferenceErrorException("While creating a new metadata : required parent dataset '"+datasetId+"' not found in "
							+"cur. element '"+getElementId()+"' which is not templated, so cannot retrieve missing dataset in ref. template element.");
				}
				
				dataset = new Dataset(this.getCommunityData());
				dataset.setDatasetId(CommunitiesAccessor.getNewDatasetId());
				int refElementId=getTemplateRefElementId();
				IDataset refDataset = this.getCommunityData().getElement(refElementId).getDataset(datasetId);
				dataset.setElementId(getElementId());
				dataset.setName(refDataset.getName());
				this.addDataset(dataset);
				return dataset;	
	}
	
	@Override
	public IMetadata addMetadata(IUserProfileData activeUser, Integer datasetId,
									String metadataName, String metadataComment, 
									Integer columnNb, Integer positionNb, Integer termId) 
					throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
						
		
		// 0- we might need to create a dataset implicitely if we are
		// redefining a metadata from template element
		IDataset dataset = addDatasetIfNeeded(activeUser,datasetId); 
							
		Metadata newMetadata = new Metadata(this.getCommunityData());
		newMetadata.setMetadataId(CommunitiesAccessor.getNewMetadataId());
		newMetadata.setDatasetId(dataset.getDatasetId());
		newMetadata.setName(metadataName);
		newMetadata.setComment(metadataComment);
		newMetadata.setTermId(termId);
		
		// 1- create it at a default location		
		dataset.addMetadata(newMetadata);
		
		// 2- move it to the desired position 
		// if not a templated element only, because if templated, position is decided by the template itself
		if (! isTemplated()) {
			moveMetadata(activeUser, newMetadata,datasetId,columnNb,positionNb);
		}
		
		
		// if element is a templated we should return composite metadata
		// not the real one, so we ivke the 'getMetadata which
		// will do this job for us
		return this.getMetadata(newMetadata.getMetadataId());
	}
	
	@Override
	public boolean isOutDated() { return isOutDated; }
	@Override
	public void setOutDated() { isOutDated=true; }
	
	@Override
	public IElement getTemplateRefElement() throws DataReferenceErrorException {
		IElement templateRefElement=null;
		try { templateRefElement=this.getCommunityData().getElement(templateRefElementId); }
		catch (DataAccessErrorException e) {
			this.setTemplateLoadError(true);
			this.setReadOnly(true);	
			throw new DataReferenceErrorException("Element '"+this.getElementId()+"' "
							+"references unknown template element '"+templateRefElementId+"' : "+e.getMessage());				
		}
		
		if (!templateRefElement.isTemplate()) {
			this.setTemplateLoadError(true);
			this.setReadOnly(true);	
		} else { 
			this.setTemplateLoadError(false);
			this.setReadOnly(false);	
		}
		return templateRefElement;
	}
	/**
	 * if element does not ref. a template, return "",
	 * if template ref element is found we display the name,
	 * it template ref element is not found, we display the (not found) element ID
	 * @return
	 */
	@Override
	public String getTemplateRefElementName() {
		if (!this.isTemplated()) { return ""; }
		// if template ref element is found we display the name,
		// it not, we display the (not found) element ID
		String name = this.getTemplateRefElementId().toString();
		try { name = this.getTemplateRefElement().getName(); } catch (DataReferenceErrorException e) {}
		
		return name;
	}
	
	
	/**
	 * TODO : we do here a go through for each while we'd better to it once
	 * when loading community data. This is a very naive approach to be improved.
	 */
	@Override
	public int getNbReferencingElements() {
		int nbrefs=0;
		Iterator<IElement> it = this.getCommunityData().getElements().iterator();
		while(it.hasNext()) {
			IElement cur = it.next();
			if (cur.getTemplateRefElementId().equals(this.getElementId())) { nbrefs++; }
		}		
		return nbrefs;
	}
	
	
  	@Override
  	public boolean isIdentified() {
  		return this.getElementId()!=0;
  	}

	@Override
	public void addDataset(IDataset dataset) {
		datasetsMap.put(dataset.getDatasetId(), dataset);
		datasets.add(dataset);		
		dataset.setParentElement(this);		
	}
	
	@Override
	public void clearDatasets() { 
		datasetsMap.clear();
		datasets.clear();	

	}
	@Override
	public void updateFull(IUserProfileData activeUser) 
			throws DataAccessErrorException,DataAccessConstraintException,DataReferenceErrorException {		
		// refresh Element level data
		this.update(activeUser);				
		
		// (re)load all datasets of given Element (and datasets' metadata...)
		this.getDatasetDBAccessor().loadDatasetsFromDB(activeUser,this);
		
		if (this.isTemplated()) { 
			try { this.getTemplateRefElement().updateFull(activeUser); }
			catch (Exception e) { log.error(e.getMessage()); }
		}
		
		isSynchronized=true;
	}

	@Override
	public void invalidate() {
		isSynchronized=false;
		
	}
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		getElementDBAccessor().storeIntoDB(activeUser, this);
		
	}
	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		
		this.commit(activeUser);
		
		// store all data sets
		Iterator<IDataset> it = datasets.iterator();
		while (it.hasNext()) {
			IDataset cur = it.next();
			cur.commitFull(activeUser);
		}
		
	}
	@Override
	public void update(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		getElementDBAccessor().refreshFromDB(activeUser, this);
		
	}
	
	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}
	@Override
	public IElement clone() throws CloneNotSupportedException {
		log.error("Clone not implemented for ElementData.");
		return null;
	}



	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getElementDBAccessor().createIntoDB(activeUser, this);
		this.getCommunityData().addElement(activeUser,this);
	}
	
	
	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {		
		this.getElementDBAccessor().deleteFromDB(activeUser, this);		
		this.getCommunityData().removeElement(activeUser,this);
	}
			
	/**
	 * return the Metadata with the given id (if found in this element datasets 
	 * @param id
	 * @return
	 */
	@Override
	public IMetadata getMetadata(int metadataId) {
		
		Iterator<IDataset> it = this.getDatasets().iterator();
		while (it.hasNext()) {			
			IDataset cur = it.next();
			try {
				IMetadata foundData=cur.getMetadata(metadataId);
			if (foundData!=null) { return foundData; }
			} catch (Exception e) {
			 // do nothing special if not found in this case (we simply return null). 
			}
		}			
		return null;
	}
	
	/**
	 * Get a string usable for full text search on the element.
	 * This is used for javascript search
	 * @return
	 */
	@Override
	public String getSearchText() {
		String result=	this.getElementId()+" - "
							+this.getName()+";"+this.getComment();
		Iterator<IDataset> it = this.getDatasets().iterator();
		while (it.hasNext()) {
			IDataset curd = it.next();
			result+=";"+curd.getSearchText();
		}
		return result;
	}
	@Override
	public Integer getElementId() {
		return elementId;
	}
	@Override
	public void setElementId(Integer elementId) {
		this.elementId = elementId;
		this.invalidate();
	}

	@Override
	public String getComment() {
		return comment;
	}
	
	@Override
	public void setComment(String comment) {		
		this.comment = comment;
	}
	@Override
	public boolean isTemplate() {
		return isTemplate;
	}
	@Override
	public void setTemplate(Boolean isTemplate) {
		this.isTemplate = isTemplate;
	}
	
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		checkCompliantWithDBSmallString("field 'name' of element '"+this.getName()+"'",this.getName());
		checkCompliantWithDBSmallString("field 'comment' of element '"+this.getName()+"'",this.getComment());	
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		// empty name is not allowed, gets the web-socket to fail loading contents
		if (name.length()==0) { name = "Element_"+this.getElementId().toString(); }
		this.name = name;
	}

	private static IElement getErrorTemplateElement(ICommunity c, int elId) {
		IElement errorTemplateElement = new Element(c);
		errorTemplateElement.setElementId(elId);
		errorTemplateElement.setName("Unknown Template Element");		
		errorTemplateElement.setCommunityId(c.getCommunityId());
		
		errorTemplateElement.setReadOnly(true);
		return errorTemplateElement;
	}

	@Override
	public List<IDataset> getLocalDatasets() { return this.datasets; }
	
	@Override
	public List<IDataset> getDatasets() {
		
		List<IDataset> instanceDatasets = this.datasets;
		if (!this.isTemplated()) { 
			instanceDatasets.sort(new Dataset.PositionComparator());
			return instanceDatasets;
		}
		
		// Templated element need a more complex mechanism to retrieve actual list of its datasets and metadata.
		
		// TODO very naive algo, improve it
		List<IDataset> result = new ArrayList<IDataset>(); 

		IElement refTemplate =null;
		try {refTemplate = this.getTemplateRefElement(); }		
		catch (DataReferenceErrorException e) {
			log.warn("Unable to load element '"+this.getElementId()+"' datasets : "+e.getMessage());
			refTemplate = getErrorTemplateElement(this.getCommunityData(), this.getTemplateRefElementId()); 
		}
		// For each dataset from reference element, we check if there is a matching (by name) dataset
		// in current instance : if yes that means that current one overrides reference one
		// (same algo for metadatas)
		List<IDataset> refDatasets = refTemplate.getDatasets();
		Iterator<IDataset> it = refDatasets.iterator();
		while (it.hasNext()) {
			IDataset curRef = it.next();
			IDataset curInstance = null;
			String datasetName = curRef.getName();
			Iterator<IDataset> it2 = instanceDatasets.iterator();
			while (it2.hasNext()) { 				
				IDataset curCheckedDataset = it2.next();
				if (curCheckedDataset.getName().equals(datasetName)) { 
					curInstance = curCheckedDataset;
					break;
				}
			}
			IDataset curDataset = new CompositeDataset(this.getCommunityData(),curRef,curInstance);
			result.add(curDataset);			
		}	
		result.sort(new Dataset.PositionComparator());
		
		
		return result;		
	}
	
	@Override
	public Map<Integer, IDataset> getDatasetsMap() {	
		if (!this.isTemplated()) { return datasetsMap; }
		
		// templated element need additional processing
		Map<Integer,IDataset> result = new HashMap<Integer, IDataset>();
		List<IDataset> datasets=this.getDatasets();
		Iterator<IDataset> it = datasets.iterator();
		while(it.hasNext()) { 
			IDataset d = it.next();
			result.put(d.getDatasetId(), d);
		}
		return result;
		
	}
	
	@Override
	 public IDataset getDataset(int datasetId) throws DataAccessErrorException  {
		IDataset result = getDatasetsMap().get(datasetId);
		if (result==null) { throw new DataAccessErrorException("Dataset id='"+datasetId+"' not found in element id='"+getElementId()+"'"); }
		return result;
	
	}
	

	/** Accessor required for JSP */
	@Override
	public int getNbDatasets(){ return getDatasets().size(); }
	
	/**
	 * Add association to given static catalog
	 * @param activeuser
	 * @param catalog
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	@Override
	public void addAssociation(IUserProfileData activeUser, ICatalogContents catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		 this.getElementDBAccessor().addAssociation(activeUser, this, catalog);
	}
	
	/**
	 * Remove association with given static catalog
	 * @param activeuser
	 * @param catalog
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	@Override
	public void removeAssociation(IUserProfileData activeUser, ICatalogContents catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		 this.getElementDBAccessor().removeAssociation(activeUser, this, catalog);
	}

	@Override
	public String getThumbnailUrl() {
		
		/*
		Iterator<IDataset> itd = this.getDatasets().iterator();
		while(itd.hasNext()) {
			Iterator<List<IMetadata>> colsIt = itd.next().getColumnsMetadata().iterator();
			while(colsIt.hasNext()) {				
				Iterator<IMetadata> metadatasIt = colsIt.next().iterator();
				while(metadatasIt.hasNext()) {
					IMetadata m = metadatasIt.next();
					if (m.isImage()) {
						IMetadata_Image i = m.getAsImage();
						//log.error("El. "+this.getElementId()+" thumbnailURL="+i.getImageUrl());
						if (i.isThumbnail()) { this.thumbnailUrl=i.getImageUrl(); }
					}
				}
			}
		}
			*/
		return thumbnailUrl;
	}
	@Override
	public void setThumbnailUrl(String url) {
		thumbnailUrl=url;		
	}
	
	@Override
	public boolean hasThumbnail() { return !getThumbnailUrl().equals(""); }


	@Override
	public Integer getTemplateRefElementId() {
		return templateRefElementId;
	}


	@Override
	public void setTemplateRefElementId(Integer refId) {
		templateRefElementId=refId;	
		this.setTemplateLoadError(false);
	}

	@Override 
	public boolean isModifyOverridenTemplate() {
		return isTemplated();
	}

	@Override
	public boolean isTemplated() {
		return this.getTemplateRefElementId()!=0;
	}
	@Override
	public int indexIn(ICatalogContents catalog) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		ICatalog c = CommunitiesAccessor.getCommunity(this.getCommunityId()).getCatalog(catalog.getCatalogId());
		return c.getElements().indexOf(this);
	}
	@Override
	public Set<Integer> getDatasetIds() {
		return this.datasetsMap.keySet();
	}

	@Override
	public JSONObject encode() {
		
		try {
			 return new JSONObject(new Element.ElementDataExchange(this));
		 
		} catch (Exception e) {
			log.error("Error Json : "+e.getMessage());
		}
		
		return null;
	}

}
