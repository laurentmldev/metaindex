package metaindex.dbaccess.impl.metaindexdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.IGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.dataset.CompositeDataset;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.CompositeMetadata;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.Metadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ADatasetAccessor;

public class DBDatasetAccessor extends ADatasetAccessor  {

	private final static String SQL_GET_ALL_FROM_DB="select community_dataset_id, community_element_id,"
															+"name, comment, layoutNbColumns, layoutPosition, layoutAlwaysExpand,layoutDoDisplayName"
																				+" from community_datasets";
//-------- DB Management InnerClasses ----------
		/// DB result mapper
		class DatasetMapper implements RowMapper<IDataset> 
		{
			IDataset dataset;
			ICommunity communityData;
			
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public DatasetMapper(IDataset data) { dataset = data; }
			
			/** Constructor when we want to create as many instances of Dataset as we found */
			public DatasetMapper(ICommunity communityData) { this.communityData=communityData; }
			public IDataset mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  DatasetExtractor datasetExtractor;
				  if (dataset!=null) { datasetExtractor = new DatasetExtractor(dataset);  }
				  else { datasetExtractor = new DatasetExtractor(communityData);  }
				  return datasetExtractor.extractData(resultSet);
			}
		}

		class UserDatasetMapper implements RowMapper<IDatasetHandle> 
		{
			IUserProfileData user;
			IDataset dataset;
			ICommunity communityData;
			
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public UserDatasetMapper(IUserProfileData user,IDataset data) { this.user=user;dataset = data; }
			
			/** Constructor when we want to create as many instances of Dataset as we found */
			public UserDatasetMapper(IUserProfileData user,ICommunity communityData) { this.user=user;this.communityData=communityData; }
			public IDatasetHandle mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  UserDatasetExtractor datasetExtractor;
				  if (dataset!=null) { datasetExtractor = new UserDatasetExtractor(user,dataset);  }
				  else { datasetExtractor = new UserDatasetExtractor(user,communityData);  }
				  return datasetExtractor.extractData(resultSet);
			}
		}
		
		private void extractDBdata(IDataset dataset,ResultSet resultSet) throws SQLException {
			 dataset.setDatasetId(resultSet.getInt(1));
			 dataset.setElementId(resultSet.getInt(2));
			 dataset.setName(unescape(resultSet.getString(3)));
			 dataset.setComment(unescape(resultSet.getString(4)));
			 dataset.setLayoutNbColumns(resultSet.getInt(5));
			 dataset.setLayoutPosition(resultSet.getInt(6));
			 dataset.setLayoutAlwaysExpand(resultSet.getInt(7)==1);
			 dataset.setLayoutDoDisplayName(resultSet.getInt(8)==1);
		}
		/// DB result fields extractor
		class DatasetExtractor implements ResultSetExtractor<IDataset> 
		{
			IDataset dataset=null;
			ICommunity communityData;
			
			public DatasetExtractor(IDataset data) { dataset=data; } 
			public DatasetExtractor(ICommunity communityData) { this.communityData=communityData; } 
			public IDataset extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (dataset==null) { dataset=new Dataset(communityData); }
					 extractDBdata(dataset, resultSet);
					 return dataset;
				 } catch (Exception e) { log.error(e.getMessage()); }
				 
				 return null;
			 }
		}

		class UserDatasetExtractor implements ResultSetExtractor<IDatasetHandle> 
		{
			IUserProfileData user;
			IDataset dataset=null;
			ICommunity communityData;
			
			public UserDatasetExtractor(IUserProfileData user,IDataset data) { this.user=user;dataset=data; } 
			public UserDatasetExtractor(IUserProfileData user,ICommunity communityData) { this.user=user;this.communityData=communityData; } 
			public IDatasetHandle extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (dataset==null) { dataset=new Dataset(communityData); }
					 extractDBdata(dataset, resultSet);
					 return new DatasetHandle(user,dataset);
				 } catch (Exception e) { log.error(e.getMessage()); }
				 
				 return null;
			 }
		}
	//-------- End of DB Management InnerClasses ----------
	private Log log = LogFactory.getLog(DBDatasetAccessor.class);
	
	public DBDatasetAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}
	
	@Override
	public String getTableName() { return "community_datasets"; }

	@Override 
	public ADBBatchOperator getCreateIntoDBOperator() {
		class CreateIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"insert into community_datasets (community_dataset_id,community_element_id,name,comment,layoutPosition) "
						+"values (?,?,?,?,?)"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IDataset dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				dataObject.checkDataDBCompliance();
				PreparedStatement stmt = stmts.get(0);
				stmt.setInt(1, dataObject.getDatasetId());
				stmt.setInt(2, dataObject.getElementId());
				stmt.setString(3, escape(dataObject.getName()));
				stmt.setString(4, escape(dataObject.getComment()));
				stmt.setInt(5, dataObject.getLayoutPosition());
				stmt.addBatch();
			}
		
		}
		return new CreateIntoDBOperator();
	}
	
	
	@Override 
	public ADBBatchOperator getStoreIntoDBOperator() {
		class StoreIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement("update community_datasets set "
							+" name=?,"
							+" comment=?,"
							+" layoutNbColumns=?,"
							+" layoutPosition=?,"
							+" layoutAlwaysExpand=?,"
							+" layoutDoDisplayName=?"

						+" where community_dataset_id=?"
						
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IDataset dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				int alwaysExpandInt=0;
				int doDisplayNameInt=0;
				if (dataObject.isLayoutAlwaysExpand()) { alwaysExpandInt=1; }
				if (dataObject.isLayoutDoDisplayName()) { doDisplayNameInt=1; }
				
				dataObject.checkDataDBCompliance();
				PreparedStatement stmt = stmts.get(0); 
				stmt.setString(1, escape(dataObject.getName()));
				stmt.setString(2, escape(dataObject.getComment()));
				stmt.setInt(3, dataObject.getLayoutNbColumns());
				stmt.setInt(4, dataObject.getLayoutPosition());
				stmt.setInt(5, alwaysExpandInt);
				stmt.setInt(6, doDisplayNameInt);
				stmt.setInt(7, dataObject.getDatasetId());
				stmt.addBatch();
			}
		}
		return new StoreIntoDBOperator();
	}
	

	@Override 
	public ADBBatchOperator getDeleteFromDBOperator() {
		class DeleteFromDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"delete from community_datasets"
						+" where community_datasets.community_dataset_id = ?"
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IDataset dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				PreparedStatement stmt = stmts.get(0); 
				stmt.setInt(1, dataObject.getDatasetId());
				stmt.addBatch();
				
			}
		}
		return new DeleteFromDBOperator();
	}
	

	
	@Override
	public void refreshFromDB(IUserProfileData activeUser, IDataset dataset)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		loadBasicDataset(activeUser,dataset);

	}
	
	@Override
	public List<IDataset> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {
				
		log.error("loadUserRelatedDataFromDB not available for Dataset.");
		throw new DataAccessErrorException("loadUserRelatedDataFromDB not available for Dataset");
	}

	/**
	 * This method will get basic  community data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadBasicDataset(IUserProfileData activeUser, IDataset dataset)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// get dataset infos						
			String sql=SQL_GET_ALL_FROM_DB 
							+ " where community_datasets.community_dataset_id='"+(dataset.getDatasetId())+"';";
			
			List<IDataset> results = querySqlData(sql, new DatasetMapper(dataset));
			if (results.size()==0) 
			{ 
				throw new DataAccessErrorException("No result for dataset '"+dataset.getDatasetId()+"'"); 
			}		
			else if (results.size()>1) {
				throw new DataAccessErrorException("More than 1 result for dataset '"+dataset.getDatasetId()+"'");
			}
	}


	/**
	 * Load from DB metadatas regarding this dataset.
	 * Build also the corresponding 'searchText' string used for client side javascript search.
	 * @param activeUser
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	@Override
	public void loadDatasetsFromDB(IUserProfileData activeUser, IElement parentElementData) 
										throws DataAccessErrorException,DataAccessConstraintException {

		List<IDataset> datasets=this.loadAssociatedData(activeUser, parentElementData);
		parentElementData.clearDatasets();
		
		// load subdata (Metadatas) of each Dataset
		Iterator<IDataset> it = datasets.iterator();
		while (it.hasNext()) {
			IDataset curDataset = it.next();
			parentElementData.addDataset(curDataset);
			getMetadataDBAccessor().loadMetadatasFromDB(activeUser,curDataset);

		}
	}
	

	@Override
	public List<IDataset> loadAssociatedData(IUserProfileData activeUser, IElement associatedObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		String sql=SQL_GET_ALL_FROM_DB +
					" where community_datasets.community_element_id='"+associatedObject.getElementId()+"';";
		
		List<IDataset> results = querySqlData(sql, new DatasetMapper(associatedObject.getCommunityData()));
		return results;
	}



	@Override
	public List<IDatasetHandle> loadUserAssociatedData(IUserProfileData activeUser, IElement associatedObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		String sql=SQL_GET_ALL_FROM_DB +
				" where community_datasets.community_element_id='"+associatedObject.getElementId()+"';";
	
		List<IDatasetHandle> results = querySqlData(sql, new UserDatasetMapper(activeUser,associatedObject.getCommunityData()));
	
		return results;
	}
	
	
	
	@Override
	public void addAssociation(IUserProfileData activeUser, IDataset managedData, IElement associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("addAssociation(ElementData) not implemented yet for DBDatasetAccessor (user="+activeUser+")");
		throw new DataAccessErrorException("Operation not available : addAssociation for Dataset/ElementData (user="+activeUser+")");
		
	}


	@Override
	public void removeAssociation(IUserProfileData activeUser, IDataset managedData, IElement associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("removeAssociation(ElementData) not implemented yet for DBDatasetAccessor (user="+activeUser+")");
		throw new DataAccessErrorException("Operation not available : removeAssociation for Dataset/ElementData (user="+activeUser+")");
		
	}
	

	
}
