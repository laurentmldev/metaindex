package metaindex.data.dataset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunity;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;
import metaindex.dbaccess.accessors.AMetaindexAccessor;
import metaindex.data.AAccessControledData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class DatasetHandle extends AAccessControledData<IDataset> implements IDatasetHandle {

	private Log log = LogFactory.getLog(DatasetHandle.class);
	
	private Map<Integer,IMetadata> bufMetadatas = new HashMap<Integer,IMetadata>();

	public DatasetHandle(IUserProfileData user,IDataset dataset) {
		super(user, dataset);
	}
	

	@Override
	public void addMetadata(IMetadataHandle m) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.checkWritableByUser();
		ICommunity c = CommunitiesAccessor.getCommunity(this.getCommunityId()); 
		IMetadata d = c.getMetadata(m.getMetadataId());
		if (d==null) { throw new DataReferenceErrorException("Metadata '"+m.getMetadataId()+"' not reachable in community '"+c.getCommunityId()+"'"); }
		this.getRefData().addMetadata(d);	
	}

	@Override
	public void clearMetadata() {
		this.checkWritableByUser();
		this.getRefData().clearMetadata();
		
	}

	@Override
	public void removeMetadata(Integer metadataId) {
		this.checkWritableByUser();
		this.getRefData().removeMetadata(metadataId);
	}
	@Override
	public int getNbMetadata() {
		this.checkReadableByUser();
		return this.getRefData().getNbMetadata();
	}
	
	@Override
	public IElementHandle getParentElementData() {
		IElement parent = this.getRefData().getParentElement();
		if (parent==null) {return null; }
		return new ElementHandle(this.getUserProfile(),parent);
	}
	/**
	 * Here we have to go through all the result
	 * TODO maybe do it in the Dataset method itself in order to
	 * do the go-through only once...
	 * @return
	 */
	public List<List<IMetadataHandle>> getColumnsMetadata() {
		this.checkReadableByUser();
		
		List<List<IMetadataHandle>> result = new ArrayList<List<IMetadataHandle>>();
		
		List<List<IMetadata>> colsMetadatas = this.getRefData().getColumnsMetadata();
		Iterator<List<IMetadata>> colsIt = colsMetadatas.iterator();
		
		while (colsIt.hasNext()) {
			List<IMetadata> curColMetadatas = colsIt.next();						
			Iterator<IMetadata> it =  curColMetadatas.iterator();
			List<IMetadataHandle> curColUserData = new ArrayList<IMetadataHandle>();
			while(it.hasNext()) {
				IMetadata curMetadata = it.next();
				curColUserData.add(new MetadataHandle(this.getUserProfile(),curMetadata)); 
			}			
			result.add(curColUserData);
		}
		return result;
	}

	/**
	 * Get metadatas visible to the user for this dataset
	 * @return
	 */
	@Override
	public List<IMetadataHandle> getMetadata() {
		this.checkReadableByUser();
		
		List<IMetadataHandle> result = new ArrayList<IMetadataHandle>();
		List<IMetadata> metadatas = this.getRefData().getMetadata();
		Iterator<IMetadata> it = metadatas.iterator();
		while (it.hasNext()) {
			IMetadata curMetadata = it.next();
			// ignore metadata being out of the nbColumns
			if (curMetadata.getLayoutColumn()<=this.getLayoutNbColumns()) {
				result.add(new MetadataHandle(this.getUserProfile(),curMetadata));
			}
		}
		return result;
	}

	
	public Map<Integer, IMetadataHandle> getMetadatasMap() {
		this.checkReadableByUser();
		Map<Integer, IMetadataHandle> result = new HashMap<Integer, IMetadataHandle>();
		Map<Integer, IMetadata> map = this.getRefData().getMetadatasMap(); 
		Iterator<Integer> it = map.keySet().iterator();
		while (it.hasNext()) {
			Integer curId = it.next();
			IMetadataHandle curMetadata = this.getMetadata(curId);
			result.put(curMetadata.getMetadataId(), curMetadata);
		}
		return result;
	}
	
	public MetadataHandle getMetadata(int metadataId) {
		this.checkReadableByUser();
		return new MetadataHandle(this.getUserProfile(),this.getRefData().getMetadata(metadataId));
	}
	
	@Override
	public Integer getElementId() {
		this.checkReadableByUser();
		return this.getRefData().getElementId();
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
	public Integer getDatasetId() {
		this.checkReadableByUser();
		return this.getRefData().getDatasetId();
	}

	@Override
	public void setDatasetId(Integer datasetId) {
		this.getRefData().setDatasetId(datasetId);		
	}

	@Override
	public String getName() {
		this.checkReadableByUser();
		return this.getRefData().getName();
	}

	@Override
	public void setName(String name) {
		this.checkWritableByUser();
		this.getRefData().setName(name);
		
	}

	@Override
	public Integer getLayoutNbColumns() {
		this.checkReadableByUser();
		return this.getRefData().getLayoutNbColumns();
	}

	@Override
	public void setLayoutNbColumns(Integer nbColumns) {
		this.checkWritableByUser();
		this.getRefData().setLayoutNbColumns(nbColumns);		
	}

	@Override
	public Integer getLayoutPosition() {
		this.checkReadableByUser();
		return this.getRefData().getLayoutPosition();
	}

	@Override
	public void setLayoutPosition(Integer position) {
		this.checkWritableByUser();
		this.getRefData().setLayoutPosition(position);
		
	}

	@Override
	public boolean isLayoutAlwaysExpand() {
		this.checkReadableByUser();
		return this.getRefData().isLayoutAlwaysExpand();
	}

	@Override
	public void setLayoutAlwaysExpand(Boolean alwaysExpand) {
		this.checkWritableByUser();
		this.getRefData().setLayoutAlwaysExpand(alwaysExpand);		
	}

	@Override
	public boolean isLayoutDoDisplayName() {
		this.checkReadableByUser();
		return this.getRefData().isLayoutDoDisplayName();
	}

	@Override
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) {
		this.checkWritableByUser();
		this.getRefData().setLayoutDoDisplayName(layoutDoDisplayName);
	}

	@Override
	public String getSearchText() {
		this.checkReadableByUser();
		return this.getRefData().getSearchText();
	}
  	
	@Override
	public Integer getCommunityId() {
		this.checkReadableByUser();
		return this.getRefData().getCommunityId();
	}

	@Override
	public boolean isModifyOverridenTemplate() {
		this.checkReadableByUser();
		return this.getRefData().isModifyOverridenTemplate();
	}

	@Override
	public boolean isReadOnly() {
		return this.getRefData().isReadOnly()  || !this.isWritableByUser()
				|| this.isTemplated();
	}

	@Override
	public boolean isTemplated() {
		return this.getRefData().isTemplated();
	}

	/* Clone
	@Override
	public IDataset clone() throws CloneNotSupportedException {
		return new UserDataset(this.getUserProfile(), this.getRefData().clone());
	}*/


	@Override
	public void setElementId(Integer elId) {
		this.getRefData().setElementId(elId);
	}


	@Override
	public void setParentElementData(IElementHandle e) throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		ICommunity c = CommunitiesAccessor.getCommunity(this.getCommunityId());
		this.getRefData().setParentElement(c.getElement(e.getElementId()));		
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
