package metaindex.dbaccess.impl.metaindexdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.IGenericMetaindexData;
import metaindex.data.catalog.ICatalog;
import metaindex.data.community.Community;
import metaindex.data.community.CommunityDatatype;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.CommunityVocabularySet;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.impl.metaindexdb.DBCommunityTermsAccessor.CommunityTermVocabularyDataExtractor;

public class DBCommunitiesAccessor extends ACommunityAccessor {

	
	private Log log = LogFactory.getLog(DBCommunitiesAccessor.class);
	
	private final static int IDENTITY_CODE_SQL_RAW=2;
	private final static String SQL_POPULATE_NEW_COMMUNITY="sql/populate_new_community.sql";

	private final static String SQL_GET_DATATYPES_FROM_DB="select datatype_id, name from datatypes ";
	
	private final static String SQL_GET_ALL_FROM_DB="select communities.community_id, communities.idName, "
			+"communities.lastUpdate, users.username as creator_name "
			+"from communities,users "  
			+"where communities.creator_id=users.user_id ";
	
	private final static String SQL_GET_ALL_WITH_GROUPS_FROM_DB="select communities.community_id, communities.idName, "
									+"u2.username as user, u1.username as creator_name, community_groups.community_group_id as group_id, "
									+"community_groups.name as group_name "
									+"from communities,users u1,community_groups,community_usergroups "  
									+"inner join users u2 on community_usergroups.user_id=u2.user_id "
									+"where communities.creator_id=u1.user_id "
									+"and community_groups.community_group_id=community_usergroups.community_group_id "
									+"and communities.community_id=community_groups.community_id ";

	
//-------- DB Management InnerClasses ----------
		/// DB result mapper
	
		private static void extractDBData(ICommunity communityData, ResultSet resultSet) throws SQLException {
			  			
			 communityData.setCommunityID(resultSet.getInt(1));
			 communityData.setIdName(unescape(resultSet.getString(IDENTITY_CODE_SQL_RAW)));
			 communityData.setCreatorName(unescape(resultSet.getString(4)));	
			 try {
			 	 communityData.setGroupId(resultSet.getInt(5));
				 communityData.setGroupName(unescape(resultSet.getString(6)));
			 } catch (Exception e) { 
				 // ignore here since those 2 columns are optionally used						 
			 }
		}
		class BasicUserCommunityDataMapper implements RowMapper<ICommunityHandle> 
		{
			ICommunity communityData;
			IUserProfileData userData;

			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public BasicUserCommunityDataMapper(IUserProfileData user, ICommunity data) { 
				communityData = data;
				userData=user;
			}
			
			/** Constructor when we want to create as many instances of CommunityData as we found */
			public BasicUserCommunityDataMapper(IUserProfileData user) { userData=user; }
			public ICommunityHandle mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  BasicUserCommunityDataExtractor communityDataExtractor = new BasicUserCommunityDataExtractor(userData,communityData);
				  return communityDataExtractor.extractData(resultSet);
			}
		}
		class BasicCommunityDataMapper implements RowMapper<ICommunity> 
		{
			ICommunity communityData;

			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public BasicCommunityDataMapper(ICommunity data) { communityData = data; }
			
			/** Constructor when we want to create as many instances of CommunityData as we found */
			public BasicCommunityDataMapper() {  }
			public ICommunity mapRow(ResultSet resultSet, int line) throws SQLException 
			{				
				  BasicCommunityDataExtractor communityDataExtractor = new BasicCommunityDataExtractor(communityData);
				  return communityDataExtractor.extractData(resultSet);
			}
		}
		/// DB result datatype mapper
		class CommunityDatatypeMapper implements RowMapper<CommunityDatatype> 
		{
			public CommunityDatatypeMapper() { }
			public CommunityDatatype mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				CommunityDatatypeExtractor dataExtractor = new CommunityDatatypeExtractor();
				return dataExtractor.extractData(resultSet);
			}
		}
		
		/// DB result fields extractor
		class BasicUserCommunityDataExtractor implements ResultSetExtractor<ICommunityHandle> 
		{
			ICommunity communityData;
			IUserProfileData userData;
			public BasicUserCommunityDataExtractor(IUserProfileData user, ICommunity data) {
				communityData = data;
				userData=user;
			}
			
			 public ICommunityHandle extractData(ResultSet resultSet) throws SQLException, DataAccessException {  
				 
				 try {
					 if (communityData==null) { 
						 communityData= 
								 CommunitiesAccessor.getOrLoadCommunity(userData,unescape(resultSet.getString(IDENTITY_CODE_SQL_RAW)));
					}
					 extractDBData(communityData,resultSet);
					 return new CommunityHandle(userData,communityData);
				 } catch (Exception e) { log.error(e.getMessage()); }
				 
				 return null;
			 }
		}
		class BasicCommunityDataExtractor implements ResultSetExtractor<ICommunity> 
		{
			ICommunity communityData;
			public BasicCommunityDataExtractor(ICommunity data) { communityData = data; }
			
			 public ICommunity extractData(ResultSet resultSet) throws SQLException, DataAccessException {  
				 
				if (communityData==null) { 
					 communityData=new Community(unescape(resultSet.getString(IDENTITY_CODE_SQL_RAW)));
				}
				extractDBData(communityData,resultSet);
				return communityData;

			 }
		}
		/// DB result fields extractor
		class CommunityDatatypeExtractor implements ResultSetExtractor<CommunityDatatype> 
		{
			public CommunityDatatypeExtractor() {  }			
			public CommunityDatatype extractData(ResultSet resultSet) throws SQLException {  
				int dataTypeId=resultSet.getInt(1);
				String datatypeName=resultSet.getString(2);
				 return new CommunityDatatype(dataTypeId,datatypeName);				 
			}
		}
				
		class CommunityVocabularyDataMapper implements RowMapper<CommunityVocabularySet> 
		{
			 public CommunityVocabularySet mapRow(ResultSet resultSet, int line) throws SQLException 
			 {
				  CommunityVocabularyDataExtractor vocabularySetDataExtractor = new CommunityVocabularyDataExtractor();
				  return vocabularySetDataExtractor.extractData(resultSet);
			 }
		}

		/// DB result fields extractor
		class CommunityVocabularyDataExtractor implements ResultSetExtractor<CommunityVocabularySet> 
		{

			 public CommunityVocabularySet extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 CommunityVocabularySet vocabularySet = new CommunityVocabularySet(); 
				 vocabularySet.setGuiLanguageID(resultSet.getInt(3));
				 vocabularySet.setCommunityNameTraduction(unescape(resultSet.getString(4)));
				 vocabularySet.setCommunityCommentTraduction(unescape(resultSet.getString(5)));
				 vocabularySet.setCommunityVocabularyDescription(unescape(resultSet.getString(6)));
				 vocabularySet.setElementTraduction(unescape(resultSet.getString(7)));
				 vocabularySet.setElementsTraduction(unescape(resultSet.getString(8)));
				 vocabularySet.setDatasetTraduction(unescape(resultSet.getString(9)));
				 vocabularySet.setDatasetsTraduction(unescape(resultSet.getString(10)));
				 vocabularySet.setMetadataTraduction(unescape(resultSet.getString(11)));
				 vocabularySet.setMetadatasTraduction(unescape(resultSet.getString(12)));
				 vocabularySet.setCatalogTraduction(unescape(resultSet.getString(13)));
				 vocabularySet.setCatalogsTraduction(unescape(resultSet.getString(14)));
				 vocabularySet.setUserTraduction(unescape(resultSet.getString(15)));
				 vocabularySet.setUsersTraduction(unescape(resultSet.getString(16)));
				 vocabularySet.setUserGroupTraduction(unescape(resultSet.getString(17)));
				 vocabularySet.setUserGroupsTraduction(unescape(resultSet.getString(18)));
				 
				 return vocabularySet;
			 }
		}
	//-------- End of DB Management InnerClasses ----------
	
	public DBCommunitiesAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}
	
	@Override
	public String getTableName() { return "communities"; };
	

	@Override
	public ADBBatchOperator getCreateIntoDBOperator() {
		class CreateIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>() ;
				result.add(con.prepareStatement("insert into communities (community_id, idName, creator_id) values (?,?,?)"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICommunity dataObject, List<PreparedStatement> stmts) {

				dataObject.checkDataDBCompliance();
				try {
					PreparedStatement stmt = stmts.get(0);
					dataObject.setCommunityID(CommunitiesAccessor.getNewCommunityId());
					stmt.setInt(1, dataObject.getCommunityId());
					stmt.setString(2, escape(dataObject.getIdName()));
					stmt.setInt(3, activeUser.getUserId());
					stmt.addBatch();
				} catch (SQLException e) {
					dataObject.setCommunityID(0);
					throw new DataAccessErrorException(e);
				}
				
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
						 "update community_vocabulary set "
							+"communityName=?,"
							+"communityComment=?,"
							+"elementTraduction=?,"
							+"elementsTraduction=?,"
							+"datasetTraduction=?,"
							+"datasetsTraduction=?,"
							+"metadataTraduction=?,"
							+"metadatasTraduction=?,"
							+"catalogTraduction=?,"
							+"catalogsTraduction=?,"
							+"userTraduction=?,"
							+"usersTraduction=?,"
							+"userGroupTraduction=?,"
							+"userGroupsTraduction=?"
							
							+" where community_id = ?"
							+" and guilanguage_id = ?"));		
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICommunity dataObject,
					List<PreparedStatement> stmts) {
										
				Iterator<CommunityVocabularySet> it = dataObject.getVocabularySets().iterator();
				
				dataObject.checkDataDBCompliance();
				PreparedStatement setVocabularyStmt = stmts.get(0);
				while (it.hasNext()) {								
					CommunityVocabularySet curVocabularySet = it.next();
							
					try {
						setVocabularyStmt.setString(1, escape(curVocabularySet.getCommunityNameTraduction()));
						setVocabularyStmt.setString(2, escape(curVocabularySet.getCommunityCommentTraduction()));
						setVocabularyStmt.setString(3, escape(curVocabularySet.getElementTraduction()));
						setVocabularyStmt.setString(4, escape(curVocabularySet.getElementsTraduction()));
						setVocabularyStmt.setString(5, escape(curVocabularySet.getDatasetTraduction()));
						setVocabularyStmt.setString(6, escape(curVocabularySet.getDatasetsTraduction()));
						setVocabularyStmt.setString(7, escape(curVocabularySet.getMetadataTraduction()));
						setVocabularyStmt.setString(8, escape(curVocabularySet.getMetadatasTraduction()));
						setVocabularyStmt.setString(9, escape(curVocabularySet.getCatalogTraduction()));
						setVocabularyStmt.setString(10, escape(curVocabularySet.getCatalogsTraduction()));
						setVocabularyStmt.setString(11, escape(curVocabularySet.getUserTraduction()));
						setVocabularyStmt.setString(12, escape(curVocabularySet.getUsersTraduction()));
						setVocabularyStmt.setString(13, escape(curVocabularySet.getUserGroupTraduction()));
						setVocabularyStmt.setString(14, escape(curVocabularySet.getUserGroupsTraduction()));
						setVocabularyStmt.setInt(15, dataObject.getCommunityId());
						setVocabularyStmt.setInt(16, curVocabularySet.getGuiLanguageID());
						setVocabularyStmt.addBatch();
					} catch (SQLException e) {
						throw new DataAccessErrorException(e);
					}
				}
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
				
				throw new SQLException("Operation not available : deletion of communities not implemented yet");
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICommunity dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				// Nothing
			}
		}
		return new DeleteFromDBOperator();
	}
	

	
	@Override
	public void refreshFromDB(IUserProfileData activeUser, ICommunity communityData)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		//log.error("### refreshFromDB community "+communityData.getIdName()+" ("+communityData+")");
		
		// Trying to detect if its a brand new community to load out of idName given by user,
		// or if it is a full load for community usage : the choice is based on community_id set or not.
		// This logic should probably better be somewhere up in the CommunityData class itself ...
		if (communityData.getCommunityId()==0) { loadBasicCommunityData(activeUser,communityData); }
		else { loadCommunityDataWithGroups(activeUser,communityData); }
		
		// retrieve composite community data
		communityData.setVocabularySets(getCommunityVocabulary(activeUser,communityData.getIdName()));
		communityData.setDatatypes(this.getDatatypes());
		
		//log.error("### refreshedFromDB community "+communityData.getIdName()+" ("+communityData+")");
	}
	
	@Override
	public  List<ICommunityHandle> loadUserAssociatedData(IUserProfileData activeUser, IUserProfileData userProfile)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// we retrieve all the communities that has groups the given user belongs to
		String sql=SQL_GET_ALL_WITH_GROUPS_FROM_DB
					+ " and u2.username='"+escape(userProfile.getUsername())+"'";
		
		List<ICommunityHandle> results = querySqlData(sql, new BasicUserCommunityDataMapper(activeUser));
		Iterator<ICommunityHandle> it =results.iterator();
		while (it.hasNext()) {
			ICommunityHandle curCommunity = it.next();
			curCommunity.setVocabularySets(getCommunityVocabulary(activeUser,curCommunity.getIdName()));
			
		}
		return results;
	}
	
	@Override
	public  List<ICommunity> loadAssociatedData(IUserProfileData activeUser, IUserProfileData userProfile)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// we retrieve all the communities that has groups the given user belongs to
		String sql=SQL_GET_ALL_WITH_GROUPS_FROM_DB
					+ " and u2.username='"+escape(userProfile.getUsername())+"'";
		
		List<ICommunity> results = querySqlData(sql, new BasicCommunityDataMapper());
		Iterator<ICommunity> it =results.iterator();
		while (it.hasNext()) {
			ICommunity curCommunity = it.next();
			curCommunity.setVocabularySets(getCommunityVocabulary(activeUser,curCommunity.getIdName()));
			
		}
		return results;
	}
	
	@Override
	public List<ICommunity> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		List<ICommunity> results = new ArrayList<ICommunity>();
		// we retrieve all the communities 
		String sql=SQL_GET_ALL_FROM_DB;
		
		List<ICommunity> communities = querySqlData(sql, new BasicCommunityDataMapper());
		Iterator<ICommunity> it = communities.iterator();
		while (it.hasNext()) {
			ICommunity curCommunity = it.next();
			curCommunity.setVocabularySets(getCommunityVocabulary(activeUser,curCommunity.getIdName()));
			
			results.add(curCommunity);
		}
		return results;
	}

	/**
	 * This method will get basic  community data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadBasicCommunityData(IUserProfileData activeUser, ICommunity communityData)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		//ICommunityData userCommunityData = new CommunityDataHandle(activeUser,communityData);
		// get community by community idname						
			String sql=SQL_GET_ALL_FROM_DB
					+ " and communities.idName='"+escape(communityData.getIdName())+"'";
	
			//log.error("### refreshFromDB community "+communityData.getIdName()+" : "+sql);
			List<ICommunity> results = querySqlData(sql, new BasicCommunityDataMapper(communityData));
			if (results.size()==0) 
			{ 
				throw new DataAccessErrorException("No result for community '"+communityData.getIdName()+"'"); 
			}
			if (results.size()>1) 
			{ 
				throw new DataAccessErrorException("Too many results for community '"+communityData.getIdName()+"'"); 
			}
	}
	/**
	 * This method will get basic  community data (like ID for example) + the group information of the active user
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadCommunityDataWithGroups(IUserProfileData activeUser, ICommunity communityData)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// get community by community idname						
			String 
				sql=SQL_GET_ALL_WITH_GROUPS_FROM_DB
							+" and communities.community_id='"+communityData.getCommunityId()+"'"
							+" and u2.username='"+escape(activeUser.getUsername())+"'";

			ICommunityHandle userCommunityData = new CommunityHandle(activeUser,communityData);
			
			List<ICommunity> results = querySqlData(sql, new BasicCommunityDataMapper());
			if (results.size()==0) 
			{ 
				throw new DataAccessErrorException("No result for community '"+userCommunityData.getIdName()+"'"); 
			}			
	}	
	private List<CommunityVocabularySet> getCommunityVocabulary(IUserProfileData activeUser, String communityIdName)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// Retrieve DataSets
		String sql="select community_vocabulary.* from communities,community_vocabulary where "
					+"communities.community_id=community_vocabulary.community_id"
				+" and communities.idName='"+escape(communityIdName)+"'"
				;
				
		List<CommunityVocabularySet> vocabularies = querySqlData(sql, new CommunityVocabularyDataMapper());
		
		return vocabularies;
	}
	
	
	/**
	 * Create Community Tables
	 * @param name Community name (must be unique)
	 * @param creatorUserID the user ID creating this community
	 * @return The new community ID
	 * @throws DataAccessErrorException when DB error or constraint check failure
	 */
	public void createIntoDB(IUserProfileData activeUser, List<ICommunity> dataObjects)
			throws DataAccessErrorException,DataAccessConstraintException{
			
		super.createIntoDB(activeUser, dataObjects);
			
		// create default terms entries
		Iterator<ICommunity> it = dataObjects.iterator();
		while (it.hasNext()) {
			ICommunity dataObject = it.next();
			
			// create default entries
			Hashtable<String,String> keymap = new Hashtable<String,String>();
			keymap.put("%community_id%", new Integer(dataObject.getCommunityId()).toString());
			keymap.put("%user_id%", escape(dataObject.getCreatorName()));
			try {
				executeSqlParameterizedScript(new ClassPathResource(SQL_POPULATE_NEW_COMMUNITY), keymap);
			} catch (Exception e) 
			{ 
				log.error(e.getMessage() );
				throw e;
			}	
		
		}
	}
	
	
	@Override
	/**
	 * @param profile the profile should be loaded with the 'Community' specific data 'groupName'
	 */
	public void addAssociation(IUserProfileData activeUser, ICommunity communityData, IUserProfileData profile) 
							throws DataAccessErrorException, DataAccessConstraintException {
		
		String sql="INSERT INTO `community_usergroups` (user_id,community_group_id) "
				+"VALUES ((select user_id from users where username='"+escape(profile.getUsername())+"'), "+
						"(select community_group_id from community_groups where name='"+escape(communityData.getGroupName())+"' "
						+"and community_id='"+communityData.getCommunityId()+"'));";
		
		this.updateSqlData(sql);		
		
	}

	@Override
	public void removeAssociation(IUserProfileData activeUser, ICommunity managedData, IUserProfileData associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("removeAssociation(IUserProfileData) not implemented yet for DBIUserProfileDataAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : removeAssociation for Community/UserProfile (user="+activeUser.getUsername()+")");
	}



	private List<CommunityDatatype> getDatatypes() {
		List<CommunityDatatype> results = querySqlData(SQL_GET_DATATYPES_FROM_DB, new CommunityDatatypeMapper()); 
		
		return results;
	}


	
	
}
