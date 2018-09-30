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
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.CompositeMetadata;
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.data.metadata.specialized.Metadata_Image;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetadataAccessor;

public class DBMetadataAccessor extends AMetadataAccessor {

	// that's the keyword it is defined in DB
	private final String IMAGE_DATATYPE_NAME = "Image";
	private final String NUMBER_DATATYPE_NAME = "Number";
	private final String WEBLINK_DATATYPE_NAME = "WebLink";
	private final String TINYTEXT_DATATYPE_NAME = "TinyText";
	private final String LONGTEXT_DATATYPE_NAME = "LongText";
	
	public String getImageDatatypeName() { return IMAGE_DATATYPE_NAME; }
	public String getNumberDatatypeName() { return NUMBER_DATATYPE_NAME; }
	public String getWebLinkDatatypeName() { return WEBLINK_DATATYPE_NAME; }
	public String getTinyTextDatatypeName() { return TINYTEXT_DATATYPE_NAME; }
	public String getLongTextDatatypeName() { return LONGTEXT_DATATYPE_NAME; }
	
		private final static String SQL_GET_ALL_FROM_DB="select community_metadata_id,community_dataset_id,community_term_id,"
																+"name, comment, layoutColumn,layoutPosition,layoutdoDisplayName,layoutAlign,layoutSize,"
																+"valueString1,valueString2,valueString3,valueString4,valueLongString,"
																+"valueNumber1,valueNumber2,valueNumber3,valueNumber4,"
																+"valueBoolean1,valueBoolean2,valueBoolean3,valueBoolean4"
																+" from community_metadata"; 
	
	
//-------- DB Management InnerClasses ----------
		/// DB result mapper
		class MetaDataMapper implements RowMapper<IMetadata> 
		{
			IMetadata metadata;
			ICommunity communityData;
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public MetaDataMapper(IMetadata data) { metadata = data; }
			
			/** Constructor when we want to create as many instances of Metadata as we found */
			public MetaDataMapper(ICommunity communityData) { this.communityData=communityData; }
			public IMetadata mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  MetaDataExtractor metadataExtractor;
				  if (metadata!=null) { metadataExtractor = new MetaDataExtractor(metadata); }
				  else { metadataExtractor = new MetaDataExtractor(communityData); }
				  return metadataExtractor.extractData(resultSet);
			}
		}

		class UserMetaDataMapper implements RowMapper<IMetadataHandle> 
		{
			IUserProfileData user;
			IMetadata metadata;
			ICommunity communityData;
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public UserMetaDataMapper(IMetadata data) { metadata = data; }
			
			/** Constructor when we want to create as many instances of Metadata as we found */
			public UserMetaDataMapper(IUserProfileData user, ICommunity communityData) { 
				this.user=user;this.communityData=communityData; 
			}
			public IMetadataHandle mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  UserMetaDataExtractor metadataExtractor;
				  if (metadata!=null) { metadataExtractor = new UserMetaDataExtractor(user,metadata); }
				  else { metadataExtractor = new UserMetaDataExtractor(user,communityData); }
				  return metadataExtractor.extractData(resultSet);
			}
		}
		
		private void extractDBdata(IMetadata metadata, ResultSet resultSet) throws SQLException, BeanDataException {
			 metadata.setMetadataId(resultSet.getInt(1));
			 metadata.setDatasetId(resultSet.getInt(2));
			 metadata.setTermId(resultSet.getInt(3));
			 metadata.setName(unescape(resultSet.getString(4)));
			 metadata.setComment(unescape(resultSet.getString(5)));
			 metadata.setLayoutColumn(resultSet.getInt(6));
			 metadata.setLayoutPosition(resultSet.getInt(7));
			 metadata.setLayoutDoDisplayName(resultSet.getInt(8)==1);
			 metadata.setLayoutAlign(unescape(resultSet.getString(9)));
			 metadata.setLayoutSize(unescape(resultSet.getString(10)));
			 metadata.setString1(unescape(resultSet.getString(11)));
			 metadata.setString2(unescape(resultSet.getString(12)));
			 metadata.setString3(unescape(resultSet.getString(13)));
			 metadata.setString4(unescape(resultSet.getString(14)));
			 metadata.setLongString(unescape(resultSet.getString(15)));
			 metadata.setValueNumber1(resultSet.getDouble(16));
			 metadata.setValueNumber2(resultSet.getDouble(17));
			 metadata.setValueNumber3(resultSet.getDouble(18));
			 metadata.setValueNumber4(resultSet.getDouble(19));
			 metadata.setValueBoolean1(resultSet.getBoolean(20));
			 metadata.setValueBoolean2(resultSet.getBoolean(21));
			 metadata.setValueBoolean3(resultSet.getBoolean(22));
			 metadata.setValueBoolean4(resultSet.getBoolean(23));
		}
		/// DB result fields extractor
		class MetaDataExtractor implements ResultSetExtractor<IMetadata> 
		{
			IMetadata metadata;
			ICommunity communityData;
			public MetaDataExtractor(IMetadata data) { metadata=data; } 
			public MetaDataExtractor(ICommunity communityData) { this.communityData=communityData; }
			
			public IMetadata extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (metadata==null) { metadata=new Metadata(communityData);}
					 extractDBdata(metadata,resultSet);					
					 return metadata;
				 } catch (Exception e) { log.error("While extracting metadata DB : "+e.getMessage()); }
				 
				 return null;
			 }
		}

		/// DB result fields extractor
		class UserMetaDataExtractor implements ResultSetExtractor<IMetadataHandle> 
		{
			IMetadata metadata;
			IUserProfileData user;
			ICommunity communityData;
			public UserMetaDataExtractor(IUserProfileData activeUser,IMetadata data) { user=activeUser; metadata=data; } 
			public UserMetaDataExtractor(IUserProfileData activeUser,ICommunity communityData) {
				user=activeUser; 
				this.communityData=communityData; 
			}
			
			public IMetadataHandle extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (metadata==null) { metadata=new Metadata(communityData);}
					 extractDBdata(metadata,resultSet);					
					 return new MetadataHandle(user,metadata);
				 } catch (Exception e) { log.error(e.getMessage()); }
				 
				 return null;
			 }
		}
	//-------- End of DB Management InnerClasses ----------
	private Log log = LogFactory.getLog(DBMetadataAccessor.class);
	
	public DBMetadataAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}

	
	@Override
	public String getTableName() { return "community_metadata"; }
	
	@Override 
	public ADBBatchOperator getCreateIntoDBOperator() {
		class CreateIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"insert into community_metadata "
							+"("
							+"community_metadata_id, "
							+"community_dataset_id,"
							+"community_term_id,"
							+" name,"
							+" comment,"
							+" layoutColumn,"
							+" layoutPosition,"
							+" layoutDoDisplayName,"
							+" layoutAlign,"
							+" layoutSize,"
							+" valueString1,"
							+" valueString2,"
							+" valueString3,"
							+" valueString4,"
							+" valueLongString,"
							+" valueNumber1,"
							+" valueNumber2,"
							+" valueNumber3,"
							+" valueNumber4,"
							+" valueBoolean1,"
							+" valueBoolean2,"
							+" valueBoolean3,"
							+" valueBoolean4"	
							+") "
						+"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IMetadata dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				dataObject.checkDataDBCompliance();
				PreparedStatement stmt = stmts.get(0); 
				int doDisplayIntVal=0;
				if (dataObject.isLayoutDoDisplayName()) { doDisplayIntVal=1; }
				stmt.setInt(1, dataObject.getMetadataId());
				stmt.setInt(2, dataObject.getDatasetId());
				stmt.setInt(3, dataObject.getTermId());
				stmt.setString(4, escape(dataObject.getName()));
				stmt.setString(5, escape(dataObject.getComment()));
				stmt.setInt(6, dataObject.getLayoutColumn());
				stmt.setInt(7, dataObject.getLayoutPosition());
				stmt.setInt(8, doDisplayIntVal);
				stmt.setString(9, dataObject.getLayoutAlign());
				stmt.setString(10, dataObject.getLayoutSize());
				stmt.setString(11, escape(dataObject.getString1()));
				stmt.setString(12, escape(dataObject.getString2()));
				stmt.setString(13, escape(dataObject.getString3()));
				stmt.setString(14, escape(dataObject.getString4()));
				stmt.setString(15, escape(dataObject.getLongString()));
				stmt.setDouble(16, dataObject.getValueNumber1());
				stmt.setDouble(17, dataObject.getValueNumber2());
				stmt.setDouble(18, dataObject.getValueNumber3());
				stmt.setDouble(19, dataObject.getValueNumber4());
				stmt.setBoolean(20, dataObject.isValueBoolean1());
				stmt.setBoolean(21, dataObject.isValueBoolean2());
				stmt.setBoolean(22, dataObject.isValueBoolean3());
				stmt.setBoolean(23, dataObject.isValueBoolean4());
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
				
				result.add(con.prepareStatement(
						"update community_metadata set "
						+" community_term_id=?,"
						+" community_dataset_id=?,"
						+" name=?,"
						+" comment=?,"
						+" layoutColumn=?,"
						+" layoutPosition=?,"
						+" layoutDoDisplayName=?,"
						+" layoutAlign=?,"
						+" layoutSize=?,"
						+" valueString1=?,"
						+" valueString2=?,"
						+" valueString3=?,"
						+" valueString4=?,"
						+" valueLongString=?,"
						+" valueNumber1=?,"
						+" valueNumber2=?,"
						+" valueNumber3=?,"
						+" valueNumber4=?,"
						+" valueBoolean1=?,"
						+" valueBoolean2=?,"
						+" valueBoolean3=?,"
						+" valueBoolean4=?"
						+" where community_metadata_id=?"
						
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IMetadata dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				dataObject.checkDataDBCompliance();
				PreparedStatement stmt = stmts.get(0); 
				int doDisplayIntVal=0;
				if (dataObject.isLayoutDoDisplayName()) { doDisplayIntVal=1; }

				stmt.setInt(1, dataObject.getTermId());
				stmt.setInt(2, dataObject.getDatasetId());			
				stmt.setString(3, escape(dataObject.getName()));
				stmt.setString(4, escape(dataObject.getComment()));
				stmt.setInt(5, dataObject.getLayoutColumn());
				stmt.setInt(6, dataObject.getLayoutPosition());
				stmt.setInt(7, doDisplayIntVal);
				stmt.setString(8, dataObject.getLayoutAlign());
				stmt.setString(9, dataObject.getLayoutSize());
				stmt.setString(10, escape(dataObject.getString1()));
				stmt.setString(11, escape(dataObject.getString2()));
				stmt.setString(12, escape(dataObject.getString3()));
				stmt.setString(13, escape(dataObject.getString4()));
				stmt.setString(14, escape(dataObject.getLongString()));
				stmt.setDouble(15, dataObject.getValueNumber1());
				stmt.setDouble(16, dataObject.getValueNumber2());
				stmt.setDouble(17, dataObject.getValueNumber3());
				stmt.setDouble(18, dataObject.getValueNumber4());
				stmt.setBoolean(19, dataObject.isValueBoolean1());
				stmt.setBoolean(20, dataObject.isValueBoolean2());
				stmt.setBoolean(21, dataObject.isValueBoolean3());
				stmt.setBoolean(22, dataObject.isValueBoolean4());
				stmt.setInt(23, dataObject.getMetadataId());
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
						"delete from community_metadata where "
						+" community_metadata.community_metadata_id = ?"
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IMetadata dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				PreparedStatement stmt = stmts.get(0); 
				stmt.setInt(1, dataObject.getMetadataId());
				stmt.addBatch();
				
			}
		}
		return new DeleteFromDBOperator();
	}
	
	@Override
	public void refreshFromDB(IUserProfileData activeUser, IMetadata metadata)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		loadMetadata(activeUser,metadata);

	}
	
	
	@Override
	public List<IMetadata> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {
				
		log.error("loadUserRelatedDataFromDB not available for Metadata.");
		throw new DataAccessErrorException("loadUserRelatedDataFromDB not available for Metadata");
	}

	/**
	 * This method will get basic  community data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadMetadata(IUserProfileData activeUser, IMetadata metadata)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// get metadata infos						
			String sql=SQL_GET_ALL_FROM_DB
							+" where community_metadata.community_metadata_id='"+(metadata.getMetadataId())+"';";

			List<IMetadata> results = querySqlData(sql, new MetaDataMapper(metadata));
			if (results.size()==0) 
			{ 
				throw new DataAccessErrorException("No result for metadata '"+metadata.getMetadataId()+"'"); 
			}		
			else if (results.size()>1) {
				throw new DataAccessErrorException("More than 1 result for metadata '"+metadata.getMetadataId()+"'");
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
	public void loadMetadatasFromDB(IUserProfileData activeUser, IDataset parentDataset) 
								throws DataAccessErrorException,DataAccessConstraintException {
		
		List<IMetadata> metadatas=loadAssociatedData(activeUser, parentDataset);
		parentDataset.clearMetadata();
		
		// load subdata (Metadata)
		Iterator<IMetadata> it = metadatas.iterator();
		while (it.hasNext()) {
			IMetadata cur = it.next();			
			parentDataset.addMetadata(cur);			 
		}
	}
	


	@Override
	public List<IMetadata> loadAssociatedData(IUserProfileData activeUser, IDataset dataset)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		String sql=SQL_GET_ALL_FROM_DB
						+" where community_metadata.community_dataset_id='"+(dataset.getDatasetId())+"';";

		List<IMetadata> results = querySqlData(sql, new MetaDataMapper(dataset.getCommunityData()));
		
		return results;
	}

	@Override
	public List<IMetadataHandle> loadUserAssociatedData(IUserProfileData activeUser, IDataset dataset)
			throws DataAccessErrorException, DataAccessConstraintException {
		String sql=SQL_GET_ALL_FROM_DB
				+" where community_metadata.community_dataset_id='"+(dataset.getDatasetId())+"';";

			List<IMetadataHandle> results = querySqlData(sql, new UserMetaDataMapper(activeUser,dataset.getCommunityData()));

			return results;
	}
	
	@Override
	public void addAssociation(IUserProfileData activeUser, IMetadata managedData, IDataset associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("addAssociation(DatasetData) not implemented yet for DBMetadataAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : addAssociation for Metadata/Dataset (user="+activeUser.getUsername()+")");
		
	}


	@Override
	public void removeAssociation(IUserProfileData activeUser, IMetadata managedData, IDataset associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("removeAssociation(DatasetData) not implemented yet for DBMetadataAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : removeAssociation for Metadata/Dataset (user="+activeUser.getUsername()+")");
		
	}
	
}
