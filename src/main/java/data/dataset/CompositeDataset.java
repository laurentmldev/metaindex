package metaindex.data.dataset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunity;
import metaindex.data.element.ICompositeData;
import metaindex.data.element.IElement;
import metaindex.data.metadata.CompositeMetadata;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class CompositeDataset extends Dataset  implements ICompositeData<IDataset> {

	private Log log = LogFactory.getLog(CompositeDataset.class);
	
	private IDataset templateReference;  
	private IDataset templateInstance;  

	
	public CompositeDataset(ICommunity myCommunity, IDataset templateReference, IDataset templateInstance) {
		super(myCommunity);
		this.setTemplateInstance(templateInstance);
		this.setTemplateReference(templateReference);	
	}	
	
	@Override 
	public boolean isReadOnly() {
		return this.getTemplateInstance()==null;
	}
	
	@Override
	public Dataset clone() {
		Dataset clone = new CompositeDataset(this.getCommunityData(),this.getTemplateReference(),this);		
		return clone;
	}
	
	@Override 
	public boolean isModifyOverridenTemplate() {
		return this.getTemplateInstance()!=null;
	}

	
	@Override 
	public boolean isTemplated() {
		return true;
	}
	
	@Override
	public void update(IUserProfileData activeUser) throws DataReferenceErrorException,DataAccessErrorException,DataAccessConstraintException {
		this.getTemplateReference().update(activeUser);
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().update(activeUser); }
	}
	
	@Override
	public void addMetadata(IMetadata newChild) {
		if (this.isModifyOverridenTemplate()) {
			this.getTemplateInstance().addMetadata(newChild);
		} else {
			// disabled for pure templated dataset
			log.error("Warning : trying to addMetadata to a pure template composite dataset. Ignored.");
		}
			
	}
	
	@Override
	public void clearMetadata() { 
		// disabled for composite dataset
		log.error("Warning : trying to clearMetadata of a composite dataset. Ignored.");
	}
	
	@Override
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().commit(activeUser); }
	}
	
	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().create(activeUser); }
	}
	
	
	@Override
	public List<IMetadata> getMetadata() {		
		
		// TODO very naive algo, improve it
		List<IMetadata> result = new ArrayList<IMetadata>(); 
		List<IMetadata> refsMetadata = this.getTemplateReference().getMetadata();
		
		// TODO
		// If current dataset does not override the original value (i.e. if 'instance object is null),
		// then we use the original metadatas but read-only
		if (this.getTemplateInstance()==null) { 
			Iterator<IMetadata> it = refsMetadata.iterator();
			while (it.hasNext()) {
				IMetadata m = it.next();
				result.add(new CompositeMetadata(this.getCommunityData(),m,this));				
			}
			return result;
		}
		
		// If current dataset override values of its template (i.e. if 'instance object is null),
		// then we want to find metadata overriding original ones 
		List<IMetadata> instanceMetadata = this.getTemplateInstance().getMetadata();
		Iterator<IMetadata> it = refsMetadata.iterator();
		// try to find a metadata with the same name in the instance dataset
		while (it.hasNext()) {
			IMetadata curRef = it.next();
			IMetadata curInstance = null;
			String metadataName = curRef.getName();
			Iterator<IMetadata> it2 = instanceMetadata.iterator();
			// loop through metadatas to check is one of them has the same name
			// which means that its value should override the value of the template one
			while (it2.hasNext()) { 				
				IMetadata curCheckedMetadata = it2.next();
				if (curCheckedMetadata.getName().equals(metadataName)) { curInstance = curCheckedMetadata; }
			}
			if (curInstance!=null) { result.add(new CompositeMetadata(this.getCommunityData(),curRef,curInstance)); }
			else { result.add(new CompositeMetadata(this.getCommunityData(),curRef,this)); }
		} 
		return result;
	}
	@Override
	public IMetadata getMetadata(String metadataName) throws DataAccessErrorException {
		if (!this.isModifyOverridenTemplate()) {
			return this.getTemplateReference().getMetadata(metadataName);
		}
		IMetadata m = null;
		try { m = this.getTemplateInstance().getMetadata(metadataName); }
		catch (DataAccessErrorException e) { m = this.getTemplateReference().getMetadata(metadataName); }
		
		return m;		
	}

	@Override
	public Map<Integer, IMetadata> getMetadatasMap() {
		Map<Integer,IMetadata> result = new HashMap<Integer, IMetadata>();
		List<IMetadata> metadatas=this.getMetadata();
		Iterator<IMetadata> it = metadatas.iterator();
		while(it.hasNext()) { 
			IMetadata m = it.next();
			result.put(m.getMetadataId(), m);
		}
		return result;
	}
	 @Override
	 public IMetadata getMetadata(int metadataId) throws DataAccessErrorException  {
		 
		IMetadata result = this.getMetadatasMap().get(metadataId);
		if (result==null) { throw new DataAccessErrorException("Metadata id='"+metadataId+"' not found in dataset id='"+getDatasetId()+"'"); }
		return result;
	
	}
	/** Accessor required for JSP */
	 @Override
	public int getNbMetadata(){ return this.getTemplateReference().getMetadata().size(); }

	@Override
	public void setElementId(Integer elementId) {
		// disabled for composite dataset
		log.error("Warning : trying to setElementId of a composite dataset. Ignored.");
	}

	@Override
	public Integer getElementId() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getElementId(); }
		else { return this.getTemplateReference().getElementId(); }
	}

	/**
	 * Only used by the Metadata object
	 */
	@Override
	public void setParentElement(IElement el) {
		// disabled for composite dataset
		log.error("Warning : trying to setParentElementData of a composite dataset. Ignored.");
	}
	
	/**
	 *  Get the Dataset to which belong this Metadata
	 * @return
	 */
	@Override
	public IElement getParentElement() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getParentElement(); }
		else { return this.getTemplateReference().getParentElement(); }
	}
	@Override
	public String getComment() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getComment(); }
		else { return this.getTemplateReference().getComment(); }

	}
	@Override
	public void setComment(String comment) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setComment(comment); }
		else {
			// disabled for RO composite dataset
			log.error("Warning : trying to setComment of a RO composite dataset. Ignored.");
		}
	}
	@Override
	public Integer getDatasetId() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getDatasetId(); }
		else { return this.getTemplateReference().getDatasetId(); }
	}
	
	public void setDatasetId(Integer datasetId) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setDatasetId(datasetId); }
		else {
			// disabled for RO composite dataset
			log.error("Warning : trying to setDatasetId of a RO composite dataset. Ignored.");
		}
	}
	@Override
	public String getName() {
		if (this.getTemplateInstance()!=null) { return this.getTemplateInstance().getName(); }
		else { return this.getTemplateReference().getName(); }
	}
	@Override
	public void setName(String name) {
		if (this.getTemplateInstance()!=null) { this.getTemplateInstance().setName(name); }
		else {
			// disabled for RO composite dataset
			log.error("Warning : trying to setName of a RO composite dataset. Ignored.");
		}
	}
	@Override
	public Integer getLayoutNbColumns() {
		return this.getTemplateReference().getLayoutNbColumns();
	}
	@Override
	public void setLayoutNbColumns(Integer nbColumns) {
		// disabled for composite dataset
		log.error("Warning : trying to setLayoutNbColumns of a composite dataset. Ignored.");
	}
	@Override
	public Integer getLayoutPosition() {
		return this.getTemplateReference().getLayoutPosition();
	}
	@Override
	public void setLayoutPosition(Integer position) {
		// disabled for composite dataset
		log.error("Warning : trying to setLayoutPosition of a composite dataset. Ignored.");
	}
	@Override
	public boolean isLayoutAlwaysExpand() {
		return this.getTemplateReference().isLayoutAlwaysExpand();
	}
	@Override
	public void setLayoutAlwaysExpand(Boolean alwaysExpand) {
		// disabled for composite dataset
		log.error("Warning : trying to setLayoutAlwaysExpand of a composite dataset. Ignored.");
	}
	
	@Override
	/**
	 * @return the layoutDoDisplayName
	 */
	public boolean isLayoutDoDisplayName() {
		return this.getTemplateReference().isLayoutDoDisplayName();
	}
	
	@Override
	/**
	 * @param layoutDoDisplayName the layoutDoDisplayName to set
	 */
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) {
		// disabled for composite dataset
		log.error("Warning : trying to setLayoutDoDisplayName of a composite dataset. Ignored.");
	}
	
	@Override
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		String result=this.getTemplateReference().getName()+";"+this.getTemplateReference().getComment()+";";
		
		Iterator<IMetadata> it = this.getMetadata().iterator();
		while (it.hasNext()) {
			IMetadata cur = it.next();
			result+=cur.getSearchText()+";";
		}
		return result;
	}

	/**
	 * @return the templateReference
	 */
	@Override
	public IDataset getTemplateReference() {
		return templateReference;
	}

	/**
	 * @param templateReference the templateReference to set
	 */
	@Override
	public void setTemplateReference(IDataset templateReference) {
		this.templateReference = templateReference;
	}

	/**
	 * @return the templateInstance
	 */
	@Override
	public IDataset getTemplateInstance() {
		return templateInstance;
	}

	/**
	 * @param templateInstance the templateInstance to set
	 */
	@Override
	public void setTemplateInstance(IDataset templateInstance) {
		this.templateInstance = templateInstance;
	}

	

}
