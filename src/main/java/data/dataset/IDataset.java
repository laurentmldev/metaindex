package metaindex.data.dataset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IBufferizedData;
import metaindex.data.community.ACommunitySubdata;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.ICommunityAccessing;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.element.IElement;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IJsonEncodable;
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
public interface IDataset extends ICommunityAccessing,IDatasetContents,IDatasetFunctions,ICommunitySubdata {


	public static class PositionComparator implements Comparator<IDataset> {

		@Override
		public int compare(IDataset o1, IDataset o2) {
			if (o1.getLayoutPosition()<o2.getLayoutPosition()) { return -1; }
			if (o1.getLayoutPosition()>o2.getLayoutPosition()) { return 1; }
			return 0;
		}
		
	}
	
	public IMetadata getMetadata(int metadataId) throws DataAccessErrorException; 
	public IMetadata getMetadata(String metadataName) throws DataAccessErrorException; 
	public List<List<IMetadata> > getColumnsMetadata();
	public Map<Integer, IMetadata> getMetadatasMap();
	
	public void addMetadata(IMetadata newChild);	
	
	
	public List<IMetadata> getMetadata(); 
	public IElement getParentElement();
	public void setParentElement(IElement e);
	public void loadMetadatasFromDB(IUserProfileData activeUser, IDataset parentDataset)  throws DataAccessErrorException,DataAccessConstraintException;
	//public void storeMetadatasIntoDB(IUserProfileData activeUser, List<IMetadata> metadatas) throws DataAccessErrorException,DataAccessConstraintException;
	
	
}
