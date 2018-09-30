package metaindex.data.dataset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunity;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadata.PositionComparator;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AJsonEncodable;
import metaindex.dbaccess.IDBAccessedData;
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
import java.util.Set;

/**
 * Bean storing community data
 * @author laurent
 *
 */
public class Dataset extends ACommunitySubdata<IDataset> implements IDataset {

	private Log log = LogFactory.getLog(Dataset.class);
	private Boolean isSynchronized = false;
	private Integer datasetId=0;
	private Integer elementId=0;
	private String name="";	
	private String comment="";	
	private Integer layoutNbColumns=1;
	private Integer layoutPosition=1;
	private Boolean layoutAlwaysExpand=true;
	private Boolean layoutDoDisplayName=true;
	
	private IElement parentElement=null;
	List<IMetadata> metadatas = new ArrayList<IMetadata>();
	Map<Integer,IMetadata> metadatasMap = new HashMap<Integer,IMetadata>();
	
	public void dump(String depthStr) {
		log.error(depthStr+"Dataset "+this.getDatasetId()+" : "+this.getName());
		Iterator<IMetadata> it = this.getMetadata().iterator();		
		while (it.hasNext()) {
			IMetadata m = it.next();
			m.dump(depthStr+"  ");
		}
	}
	
	/**
	 * Just used for JSON encoding
	 * Cannot use directly Metadata class (crashes, don't know really why)
	 * @author laurent
	 *
	 */
	protected class DatasetExchange implements IDatasetContents {
		
		IDataset myDataset;
		DatasetExchange(IDataset d) {myDataset=d;}
		@Override public String getName() { return myDataset.getName(); }		
		@Override public void setName(String name) { myDataset.setName(name); }
		@Override public String getComment() { return myDataset.getComment(); }
		@Override public void setComment(String comment) { myDataset.setComment(comment); }
		@Override public boolean isLayoutDoDisplayName() { return myDataset.isLayoutDoDisplayName(); }
		@Override public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) { myDataset.setLayoutDoDisplayName(layoutDoDisplayName); }		
		@Override public Integer getElementId() { return myDataset.getElementId(); }
		@Override public void setElementId(Integer elId) { myDataset.setElementId(elId); }
		@Override public Integer getDatasetId() { return myDataset.getDatasetId(); }
		@Override public void setDatasetId(Integer datasetId) { myDataset.setDatasetId(datasetId); }
		@Override public Integer getLayoutNbColumns() { return myDataset.getLayoutNbColumns(); }
		@Override public void setLayoutNbColumns(Integer nbColumns) { myDataset.getLayoutNbColumns(); }
		@Override public Integer getLayoutPosition() { return myDataset.getLayoutPosition(); }
		@Override public void setLayoutPosition(Integer position) { myDataset.setLayoutPosition(position); }
		@Override public boolean isLayoutAlwaysExpand() { return myDataset.isLayoutAlwaysExpand(); }
		@Override public void setLayoutAlwaysExpand(Boolean alwaysExpand) { myDataset.setLayoutAlwaysExpand(alwaysExpand); }
		
	}
	

	public static class PositionComparator implements Comparator<IDataset> {

		@Override
		public int compare(IDataset o1, IDataset o2) {
			if (o1.getLayoutPosition()<o2.getLayoutPosition()) { return -1; }
			if (o1.getLayoutPosition()>o2.getLayoutPosition()) { return 1; }
			return 0;
		}
		
	}
	public Dataset(ICommunity myCommunity) {
		super(myCommunity,CommunitiesAccessor.getDataAccessors());		
	}

	
	@Override
	public void checkDataDBCompliance() throws DataAccessConstraintException {
		checkCompliantWithDBSmallString("field 'name' of dataset '"+this.getName()+"'",this.getName());
		checkCompliantWithDBSmallString("field 'comment' of dataset '"+this.getName()+"'",this.getComment());	
	}
	
  	@Override
  	public boolean isIdentified() {
  		return this.getDatasetId()!=0 && this.getCommunityId()>0;
  	}  	


	@Override 
	public void commit(IUserProfileData activeUser) 
	{ 
		getDatasetDBAccessor().storeIntoDB(activeUser, this);
		
		// store all data sets
		Iterator<IMetadata> it = metadatas.iterator();
		while (it.hasNext()) {
			IMetadata cur = it.next();
			cur.commit(activeUser);
		} 
	}

	@Override
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.commit(activeUser);
		Iterator<IMetadata> it = this.getMetadata().iterator();
		while (it.hasNext()) {
			IMetadata m = it.next();
			m.commitFull(activeUser);
		}	
	}

	@Override
	public void invalidate() {
		isSynchronized=false;		
	}

	@Override
	public void update(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {

		getDatasetDBAccessor().refreshFromDB(activeUser, this);

	}

	@Override
	public void updateFull(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		
		this.update(activeUser);
		
		// (re)load all metadata of given Dataset
		loadMetadatasFromDB(activeUser,this);
		
		isSynchronized=true;
	}
	@Override
	public void loadMetadatasFromDB(IUserProfileData activeUser, IDataset parentDataset)
			throws DataAccessErrorException, DataAccessConstraintException {
		getMetadataDBAccessor().loadMetadatasFromDB(activeUser,this);
		
	}

	@Override
	public boolean isSynchronized() {
		return isSynchronized;
	}

	@Override
	public void create(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getDatasetDBAccessor().createIntoDB(activeUser, this);
		
	}

	@Override
	public void delete(IUserProfileData activeUser) throws DataAccessErrorException, DataAccessConstraintException {
		this.getDatasetDBAccessor().deleteFromDB(activeUser, this);		
	}


	
	
	
	@Override
	public void addMetadata(IMetadata newChild) {
		metadatasMap.put(newChild.getMetadataId(), newChild);
		metadatas.add(newChild);
		newChild.setParentDataset(this);
	}
	
	@Override
	public void removeMetadata(Integer metadataId) {
		IMetadata m = this.getMetadata(metadataId);
		metadatasMap.remove(metadataId);
		metadatas.remove(m);
		
	}
	
	@Override
	public void clearMetadata() { 
		metadatasMap.clear();
		metadatas.clear();
		
	}

	@Override
	public Map<Integer, IMetadata> getMetadatasMap() {
		return metadatasMap;
	}	

	@Override
	public List<IMetadata> getMetadata() {				 
		return metadatas;	
	}
	
	@Override
	public IMetadata getMetadata(String metadataName) throws DataAccessErrorException {
		IMetadata result=null;
		Integer nbMatching=0;
		Iterator<IMetadata> it = getMetadata().iterator();
		while (it.hasNext()) {
			IMetadata cur = it.next();
			if (cur.getName().equals(metadataName)) {
				result = cur;
				nbMatching++;
			}
		}
		if (nbMatching==0) {
			throw new DataAccessErrorException("No metadata named '"+metadataName+"' in given dataset '"+this.getDatasetId()+"'");
		}
		if (nbMatching>1) {
			throw new DataAccessErrorException("More than 1 metadata named '"+metadataName
									+"' in given dataset '"+this.getDatasetId()+"' : " + nbMatching);
		}		
		return result;
	}
	@Override
	public IMetadata getMetadata(int metadataId) throws DataAccessErrorException  {
		 
		IMetadata result = metadatasMap.get(metadataId);
		if (result==null) { throw new DataAccessErrorException("Metadata id='"+metadataId+"' not found in dataset id='"+getDatasetId()+"'"); }
		return result;	
	}
	 
	/** Accessor required for JSP */
	@Override
	public int getNbMetadata(){ return metadatas.size(); }
	
	/**
	 * Get the Metadata lists by column, and metadata position. This is used by JSP pages
	 * @return
	 */
	@Override
	public List<List<IMetadata> > getColumnsMetadata() {
		
		List<List<IMetadata> > result = new ArrayList<List<IMetadata> >();
		for (int curColNb=1;curColNb<=this.getLayoutNbColumns();curColNb++) {
			List<IMetadata> curColContents = new ArrayList<IMetadata>();
			Iterator<IMetadata> it=getMetadata().iterator();			
			while(it.hasNext()) {
				IMetadata cur=it.next();
				Integer mCol=cur.getLayoutColumn();
				if (mCol==curColNb) { 
					curColContents.add(cur); 
				}
			}
			curColContents.sort(new IMetadata.PositionComparator());
			
			result.add(curColContents);			
		}
		return result;
	}

	
	/**
	 * Store (inside a transaction) the given Metadata into DB, only if they are marked as belonging to this Dataset (after the DB operation).
	 * @param activeUser
	 * @param metadataToStore
	 * @throws DataAccessErrorException
	 */
	/*
	@Override
	public void storeMetadatasIntoDB(IUserProfileData activeUser,List<IMetadata> metadataToStore) throws DataAccessErrorException,DataAccessConstraintException {
		List<String> sqlStoreSequence=new ArrayList<String>();
		Iterator<IMetadata> it = metadataToStore.iterator();
		while (it.hasNext()) {
			IMetadata cur = it.next();
			if (! cur.getDatasetId().equals(this.getDatasetId())) {
				throw new DataAccessConstraintException("Trying to storeIntoDB metadata having datasetId='"+cur.getDatasetId()
									+"' while it is in the metadatas list of dataset '"+this.getDatasetId()+"'.");
			}
			sqlStoreSequence.addAll(this.getMetadataDBAccessor().prepareStoreIntoDB(activeUser, cur));
		}
		
		this.getDatasetDBAccessor().executeSqlTransaction(sqlStoreSequence);
	}
*/

	@Override
	public Integer getElementId() {
		return elementId;
	}
	@Override
	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}

	/**
	 * Only used by the Metadata object
	 */
	@Override
	public void setParentElement(IElement el) {
			this.parentElement=el;
			this.setElementId(el.getElementId());
	}
	
	/**
	 *  Get the Dataset to which belong this Metadata
	 * @return
	 */
	@Override
	public IElement getParentElement() {
		if (parentElement==null && elementId!=0
				|| parentElement.getElementId()!=elementId) {
			parentElement=this.getCommunityData().getElement(elementId);			
		}
		return parentElement;
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
	public Integer getDatasetId() {
		return datasetId;
	}
	
	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {		
		this.name = name;
	}
	@Override
	public Integer getLayoutNbColumns() {
		return layoutNbColumns;
	}
	@Override
	public void setLayoutNbColumns(Integer nbColumns) {
		this.layoutNbColumns = nbColumns;
	}
	@Override
	public Integer getLayoutPosition() {
		return layoutPosition;
	}
	@Override
	public void setLayoutPosition(Integer position) {
		this.layoutPosition = position;
	}
	@Override
	public boolean isLayoutAlwaysExpand() {
		return layoutAlwaysExpand;
	}
	@Override
	public void setLayoutAlwaysExpand(Boolean alwaysExpand) {
		this.layoutAlwaysExpand = alwaysExpand;
	}
	
	@Override
	/**
	 * @return the layoutDoDisplayName
	 */
	public boolean isLayoutDoDisplayName() {
		return layoutDoDisplayName;
	}
	
	@Override
	/**
	 * @param layoutDoDisplayName the layoutDoDisplayName to set
	 */
	public void setLayoutDoDisplayName(Boolean layoutDoDisplayName) {
		this.layoutDoDisplayName = layoutDoDisplayName;
	}
	
	@Override
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		String result=this.getName()+";"+this.getComment()+";";
		Iterator<IMetadata> it = this.getMetadata().iterator();
		while (it.hasNext()) {
			IMetadata curm = it.next();
			result+=curm.getSearchText()+";";
		}
		return result;
	}

	

	@Override
	public JSONObject encode() {
		
		try {
			return new JSONObject(new Dataset.DatasetExchange(this));		
		} catch (Exception e) {
			log.error("Error Json : "+e.getMessage());
		}
		
		return null;
	}
	


}
