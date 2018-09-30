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

import metaindex.data.AGenericMetaindexData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.specialized.IMetadata_Image;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;
import metaindex.data.AAccessControledData;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class ElementHandle extends AAccessControledData<IElement> implements IElementHandle {

	public ElementHandle(IUserProfileData userProfile, IElement refData) {		
		super(userProfile, refData);

	}
	
	public boolean isWritableByUser() {
		if (this.isOutDated()) {
			log.warn("Trying to access 'write' outdated Element '"+this.getRefData().getElementId()+"'");
			return false;
		}
		return super.isWritableByUser(); 
	}
	public boolean isManageableByUser() {
		if (this.isOutDated()) {
			log.warn("Trying to access 'manage' outdated Element '"+this.getRefData().getElementId()+"'");
			return false;
		}
		return super.isManageableByUser();
	}
	public boolean isReadableByUser() {
		if (this.isOutDated()) {
			log.warn("Trying to access 'read' outdated Element '"+this.getRefData().getElementId()+"'");
			return false;
		}
		return super.isReadableByUser();
	}

	private Log log = LogFactory.getLog(ElementHandle.class);
	
	@Override
	public void moveMetadata(IMetadataHandle movedMetadata,int newDatasetId, int newCol, int newPos) throws DataReferenceErrorException {

		IMetadata movedMetadataObj = this.getRefData().getMetadata(movedMetadata.getMetadataId());
		this.getRefData().moveMetadata(this.getUserProfile(), movedMetadataObj, newDatasetId, newCol, newPos);
	}
	
	@Override
	public IMetadataHandle createMetadata(Integer datasetId,String metadataName, String metadataComment, 
									Integer columnNb, Integer positionNb, Integer termId) 
						throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		return new MetadataHandle(this.getUserProfile(), 
							this.getRefData().addMetadata(this.getUserProfile(), datasetId, metadataName, metadataComment, columnNb, positionNb, termId));
	}

	@Override
	public boolean isOutDated() { return this.getRefData().isOutDated(); }
	
	@Override
	public Integer getElementId() {
		this.checkReadableByUser();
		return this.getRefData().getElementId();
	}

	@Override
	public void setElementId(Integer elementId) {
		this.checkWritableByUser();
		this.getRefData().setElementId(elementId);
	}

	@Override
	public Integer getCommunityId() {
		this.checkReadableByUser();
		return this.getRefData().getCommunityId();
	}

	@Override
	public void setCommunityId(int communityID) {
		this.checkManageableByUser();
		this.getRefData().setCommunityId(communityID);		
	}

	@Override
	public String getComment() {
		this.checkReadableByUser();
		return this.getRefData().getComment();
	}

	@Override
	public void setComment(String comment) {
		this.checkWritableByUser();
		this.getRefData().setComment(comment);
		
	}

	@Override
	public boolean isTemplate() {
		this.checkReadableByUser();
		return this.getRefData().isTemplate();
	}

	@Override
	public void setTemplate(Boolean isTemplate) {
		this.checkWritableByUser();
		this.getRefData().setTemplate(isTemplate);
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
	public boolean hasThumbnail() {
		this.checkReadableByUser();
		return this.getRefData().hasThumbnail();
	}

	@Override
	public IElementHandle getTemplateRefElement() throws DataReferenceErrorException {
		return new ElementHandle(this.getUserProfile(),this.getRefData().getTemplateRefElement());
	}
	
	
	@Override
	/**
	 * Prior to returning the list, a sort is performed by 'layoutPosition'.
	 * @return datasets of this element, sorted by position
	 */
	public List<IDatasetHandle> getDatasets() {
		this.checkReadableByUser();
		
		List<IDatasetHandle> result=new ArrayList<IDatasetHandle>();
		List<IDataset> elDatasets = this.getRefData().getDatasets();
		Iterator<IDataset> it = elDatasets.iterator();
		while (it.hasNext()) { result.add(new DatasetHandle(this.getUserProfile(),it.next())); }
		return result;
		
	}
	
	@Override
	public IDatasetHandle getDataset(int datasetId) {
		this.checkReadableByUser();
		IDataset d = this.getRefData().getDataset(datasetId);
		return new DatasetHandle(this.getUserProfile(), d);
	}
	
	@Override
	public Map<Integer, IDatasetHandle> getDatasetsMap() {
		this.checkReadableByUser();
		Map<Integer, IDatasetHandle> result = new HashMap<Integer, IDatasetHandle>();
		Map<Integer, IDataset> map = this.getRefData().getDatasetsMap(); 
		Iterator<Integer> it = map.keySet().iterator();
		while (it.hasNext()) {
			Integer curId = it.next();
			IDatasetHandle curDataset = new DatasetHandle(this.getUserProfile(),this.getRefData().getDataset(curId));
			result.put(curDataset.getDatasetId(), curDataset);
		}
		return result;
	}

	@Override
	public int getNbDatasets() {
		this.checkReadableByUser();
		return this.getRefData().getNbDatasets();
	}
	@Override
	public IMetadataHandle getMetadata(int metadataId) {
		this.checkReadableByUser();
		IMetadata m = this.getRefData().getMetadata(metadataId);
		if (m==null) { return null; }
		return new MetadataHandle(this.getUserProfile(), m);
	}

	@Override
	public Integer getTemplateRefElementId() {
		return this.getRefData().getTemplateRefElementId();
	}
	
	/**
	 * This is needed when updating an element when templateRefElementId is empty
	 * TODO: instead it would be better to set a predefined value in the form corresponding to "no ref" (value=0 for example)
	 * @return
	 */
	public String getTemplateRefElementIdStr() {
		return this.getRefData().getTemplateRefElementId().toString();
	}
	/**
	 * This is needed when updating an element when templateRefElementId is empty
	 * TODO: instead it would be better to set a predefined value in the form corresponding to "no ref" (value=0 for example)
	 * @return
	 */
	public void setTemplateRefElementIdStr(String val) {
		if (val.length()>0) {
			this.getRefData().setTemplateRefElementId(new Integer(val));
		}
	}
	@Override
	public void setTemplateRefElementId(Integer refId) {
		this.getRefData().setTemplateRefElementId(refId);		
	}
	@Override
	public boolean isModifyOverridenTemplate() {
		return this.getRefData().isModifyOverridenTemplate();
	}
	@Override
	public String getSearchText() {
		return this.getRefData().getSearchText();
	}

	@Override
	public boolean isTemplateLoadError() {
		return this.getRefData().isTemplateLoadError();
	}

	@Override
	public String getTemplateRefElementName() {
		return this.getRefData().getTemplateRefElementName();
	}

	@Override
	public int getNbReferencingElements() {
		return this.getRefData().getNbReferencingElements();
	}
	

	@Override
	public boolean isReadOnly() {		
		return this.getRefData().isReadOnly() || !this.isWritableByUser();
	}

	@Override
	public boolean isTemplated() {
		return this.getRefData().isTemplated();
	}

	
	@Override
	public void addDataset(IDatasetHandle d) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		IDataset dataset = CommunitiesAccessor.getCommunity(this.getCommunityId()).getDataset(d.getDatasetId());
		this.getRefData().addDataset(dataset);		
	}

	@Override
	public void clearDatasets() {
		this.getRefData().clearDatasets();		
	}
	
	@Override
	public void addAssociation(ICatalogHandle catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		this.getRefData().addAssociation(this.getUserProfile(), catalog);
		
	}

	@Override
	public void removeAssociation(ICatalogHandle catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		this.getRefData().removeAssociation(this.getUserProfile(), catalog);
		
	}

	@Override
	public int indexIn(ICatalogHandle catalog) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		return this.getRefData().indexIn(catalog);
	}

	@Override
	public Set<Integer> getDatasetIds() {
		return this.getRefData().getDatasetIds();
	}


	@Override
	public void dump(String depthStr) {
		this.getRefData().dump(depthStr);		
	}
	
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		this.getRefData().checkDataDBCompliance();		
	}

	@Override
	public String getThumbnailUrl() {		 	
		return this.getRefData().getThumbnailUrl(); 
	}

	@Override
	public void setThumbnailUrl(String url) { this.getRefData().setThumbnailUrl(url); }

}
