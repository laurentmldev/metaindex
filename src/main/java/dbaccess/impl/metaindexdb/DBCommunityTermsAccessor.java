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
import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.CommunityVocabularySet;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.dataset.IDataset;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;

public class DBCommunityTermsAccessor extends ACommunityTermsAccessor {

	private final static String SQL_POPULATE_NEW_TERM="sql/populate_new_term.sql";
	
	private final static String SQL_GET_ALL_FROM_DB="select community_term_id,community_id,datatype_id, idName, isEnum "
						+"from community_terms";
	
	
	
	//-------- DB Management InnerClasses ----------
		/// DB result mapper
		class BasicCommunityTermDataMapper implements RowMapper<ICommunityTerm> 
		{
			ICommunityTerm communityTermData;
			ICommunity communityData;
			
			/**
			 *  Constructor used when we use the given dataObject as a container for the unique expected result
			 * @param dataObject
			 */
			public BasicCommunityTermDataMapper(ICommunityTerm dataObject) { communityTermData=dataObject; }
			
			/**
			 *  Constructor used when we use a new CommunityTermData object for each result
			 * @param dataObject
			 */
			public BasicCommunityTermDataMapper(ICommunity dataObject) 
			{ 
				communityData=dataObject; 
			}
			 public ICommunityTerm mapRow(ResultSet resultSet, int line) throws SQLException 
			 {
				 BasicCommunityTermDataExtractor termDataExtractor ;
				 if (communityTermData==null) { termDataExtractor = new BasicCommunityTermDataExtractor(communityData);
				 } else { termDataExtractor = new BasicCommunityTermDataExtractor(communityTermData); }
					 
				  return termDataExtractor.extractData(resultSet);
			 }
		}
		class BasicUserCommunityTermDataMapper implements RowMapper<CommunityTermHandle> 
		{
			IUserProfileData user;
			CommunityTerm communityTermData;
			ICommunity communityData;
			
			/**
			 *  Constructor used when we use the given dataObject as a container for the unique expected result
			 * @param dataObject
			 */
			public BasicUserCommunityTermDataMapper(IUserProfileData user,CommunityTerm dataObject) {
				this.user=user;
				communityTermData=dataObject; }
			
			/**
			 *  Constructor used when we use a new CommunityTermData object for each result
			 * @param dataObject
			 */
			public BasicUserCommunityTermDataMapper(IUserProfileData user,ICommunity dataObject) 
			{ 
				this.user=user;
				communityData=dataObject; 
			}
			 public CommunityTermHandle mapRow(ResultSet resultSet, int line) throws SQLException 
			 {
				 BasicUserCommunityTermDataExtractor termDataExtractor ;
				 if (communityTermData==null) { termDataExtractor = new BasicUserCommunityTermDataExtractor(communityData);
				 } else { termDataExtractor = new BasicUserCommunityTermDataExtractor(user,communityTermData); }
					 
				  return termDataExtractor.extractData(resultSet);
			 }
		}	

		private void extractDBdata(ICommunityTerm communityTermData, ResultSet resultSet) throws SQLException {
			communityTermData.setTermId(resultSet.getInt(1));
			 communityTermData.setCommunityId(resultSet.getInt(2));					 
			 communityTermData.setDatatypeId(resultSet.getInt(3));
			 communityTermData.setIdName(resultSet.getString(4));
			 communityTermData.setEnum(resultSet.getBoolean(5));
		}

		/// DB result fields extractor
		class BasicCommunityTermDataExtractor implements ResultSetExtractor<ICommunityTerm> 
		{

			ICommunityTerm communityTermData;
			ICommunity communityData;
			public BasicCommunityTermDataExtractor(ICommunityTerm dataObject) { 
				communityTermData=dataObject; 
				communityData=communityTermData.getCommunityData();
			}
			public BasicCommunityTermDataExtractor(ICommunity dataObject) { communityData=dataObject; }
			
			 public ICommunityTerm extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (communityTermData==null) { communityTermData=new CommunityTerm(communityData); }
					 extractDBdata(communityTermData,resultSet);
				 } catch (Exception e) { log.error(e.getMessage()); }				 
				 return communityTermData;
			 }
		}
		

		/// DB result fields extractor
		class BasicUserCommunityTermDataExtractor implements ResultSetExtractor<CommunityTermHandle> 
		{
			IUserProfileData user;
			CommunityTerm communityTermData;
			ICommunity communityData;
			public BasicUserCommunityTermDataExtractor(IUserProfileData user,CommunityTerm dataObject) { 
				this.user=user;
				communityTermData=dataObject; 
				communityData=communityTermData.getCommunityData();				
			}
			public BasicUserCommunityTermDataExtractor(ICommunity dataObject) { communityData=dataObject; }
			
			 public CommunityTermHandle extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (communityTermData==null) { communityTermData=new CommunityTerm(communityData); }
					 extractDBdata(communityTermData,resultSet);
				 } catch (Exception e) { log.error(e.getMessage()); }				 
				 return new CommunityTermHandle(user,communityTermData);
			 }
		}

		class CommunityTermVocabularyDataMapper implements RowMapper<TermVocabularySet> 
		{
			 public TermVocabularySet mapRow(ResultSet resultSet, int line) throws SQLException 
			 {
				  CommunityTermVocabularyDataExtractor vocabularySetDataExtractor = new CommunityTermVocabularyDataExtractor();
				  return vocabularySetDataExtractor.extractData(resultSet);
			 }
		}

		/// DB result fields extractor
		class CommunityTermVocabularyDataExtractor implements ResultSetExtractor<TermVocabularySet> 
		{

			 public TermVocabularySet extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 TermVocabularySet vocabularySet = new TermVocabularySet(); 
				 vocabularySet.setGuiLanguageID(resultSet.getInt(3));
				 vocabularySet.setTermNameTraduction(unescape(resultSet.getString(4)));
				 vocabularySet.setTermCommentTraduction(unescape(resultSet.getString(5)));
				 return vocabularySet;
			 }
		}
	//-------- End of DB Management InnerClasses ----------
	private Log log = LogFactory.getLog(DBCommunityTermsAccessor.class);
	
	public DBCommunityTermsAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
			super(accessorsFactory,dataSource,txManager);
	}
	
	@Override
	public String getTableName() { return "community_terms"; }
		
	/**
	 * This method will get basic term data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadBasicCommunityTermData(IUserProfileData activeUser, ICommunityTerm dataObject)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		String communityIdCode=dataObject.getCommunityData().getIdName();
		String termIdName=dataObject.getIdName();
		// get community ID
			String sql=SQL_GET_ALL_FROM_DB
					+" where community_id='"+dataObject.getCommunityData().getCommunityId()+"' "
					+" and community_term_id='"+dataObject.getTermId()+"'"
					+";";

			List<ICommunityTerm> results = querySqlData(sql, new BasicCommunityTermDataMapper(dataObject));
			
			if (results.size()==0) { throw new DataAccessErrorException(
					"No result for Term '"+termIdName+"' in Community '"+communityIdCode+"'"); } 
			
			if (results.size()>1) { throw new DataAccessErrorException(
					"More than one result for Term '"+termIdName+"' in Community '"+communityIdCode+"'"); } 			
	}

	/**
	 * This method will get basic term data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private List<ICommunityTerm> getBasicCommunityTermsData(IUserProfileData activeUser, Community communityData)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// get community ID
			String sql=SQL_GET_ALL_FROM_DB+" where community_id='"+communityData.getCommunityId()+"';";
			List<ICommunityTerm> results = querySqlData(sql, new BasicCommunityTermDataMapper(communityData));			
			
		return results;
	}


	private List<CommunityTermHandle> getBasicUserCommunityTermsData(IUserProfileData activeUser, ICommunity communityData)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// get community ID
			String sql=SQL_GET_ALL_FROM_DB+" where community_id='"+communityData.getCommunityId()+"';";
			List<CommunityTermHandle> results = querySqlData(sql, new BasicUserCommunityTermDataMapper(activeUser,communityData));			
			
		return results;
	}

	
	private List<TermVocabularySet> getCommunityTermVocabulary(IUserProfileData activeUser, int termId)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// Retrieve DataSets
		String sql="select * from community_terms_vocabulary where "
					+"community_term_id='"+termId+"';"
				;
				
		List<TermVocabularySet> vocabularies = querySqlData(sql, new CommunityTermVocabularyDataMapper());

		return vocabularies;
	}
	
	@Override
	public ADBBatchOperator getCreateIntoDBOperator() {
		class CreateIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"insert into community_terms (community_term_id,community_id, idName, datatype_id,isEnum) values (?,?,?,?,?)"));		
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICommunityTerm dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				dataObject.checkDataDBCompliance();
				boolean isEnum=dataObject.isEnum();
				
				int isEnumInt=0;
				if (isEnum) { isEnumInt=1; }
				
				PreparedStatement stmt = stmts.get(0);
				dataObject.setTermId(CommunitiesAccessor.getNewTermId());			
				stmt.setInt(1, dataObject.getTermId());
				stmt.setInt(2, dataObject.getCommunityId());
				stmt.setString(3, escape(dataObject.getIdName()));
				stmt.setInt(4, dataObject.getDatatypeId());
				stmt.setInt(5, isEnumInt);
				stmt.addBatch();
			}
		}
		return new CreateIntoDBOperator();
	}
	
	
	/**
	 * Create Community Tables
	 * @param name Community name (must be unique)
	 * @param creatorUserID the user ID creating this community
	 * @return The new community ID
	 * @throws DataAccessErrorException when DB error or constraint check failure
	 */
	public void createIntoDB(IUserProfileData activeUser, List<ICommunityTerm> dataObjects)
			throws DataAccessErrorException,DataAccessConstraintException{
			
		super.createIntoDB(activeUser, dataObjects);
			
		// create default terms entries
		Iterator<ICommunityTerm> it = dataObjects.iterator();
		while (it.hasNext()) {
			ICommunityTerm dataObject = it.next();
			Hashtable<String,String> keymap = new Hashtable<String,String>();
			keymap.put("%term_id%", new Integer(dataObject.getTermId()).toString());			
			try {
				executeSqlParameterizedScript(new ClassPathResource(SQL_POPULATE_NEW_TERM), keymap);
			} catch (Exception e) 
			{ 
				log.error(e.getMessage() );
				throw e;
			}
		
		}
	}

	@Override
	public ADBBatchOperator getStoreIntoDBOperator() {
		class StoreIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				// first statement is for internal data
				result.add(con.prepareStatement(
						"update community_terms set "
						+"datatype_id=? "
						+"where community_term_id=?"
						));
				
				// second is for vocabulary sets
				result.add(con.prepareStatement(
						"update community_terms_vocabulary set "
						+"termNameTraduction=?,"
						+"termCommentTraduction=? "  
						+"where community_terms_vocabulary.guilanguage_id=?"
						+" and community_term_id =?"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICommunityTerm dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				dataObject.checkDataDBCompliance();
				PreparedStatement dataStmt = stmts.get(0);
				dataStmt.setInt(1, dataObject.getDatatypeId());
				dataStmt.setInt(2, dataObject.getTermId());	
				dataStmt.addBatch();
				
				PreparedStatement vocabularyStmt = stmts.get(1);
				Iterator<TermVocabularySet> it = dataObject.getVocabularySets().iterator();
				while (it.hasNext()) {
					TermVocabularySet curVocabularySet = it.next();					
					vocabularyStmt.setString(1, escape(curVocabularySet.getTermNameTraduction()));
					vocabularyStmt.setString(2, escape(curVocabularySet.getTermCommentTraduction()));
					vocabularyStmt.setInt(3, curVocabularySet.getGuiLanguageID());					
					vocabularyStmt.setInt(4, dataObject.getTermId());
					vocabularyStmt.addBatch();
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
				
				result.add(con.prepareStatement(
						"delete from community_terms where "
						+" community_terms.community_term_id = ?"
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICommunityTerm dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				PreparedStatement stmt = stmts.get(0); 
				stmt.setInt(1, dataObject.getTermId());
				stmt.addBatch();
				
			}
		}
		return new DeleteFromDBOperator();
	}
	


	@Override
	public void refreshFromDB(IUserProfileData activeUser, ICommunityTerm dataObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		loadBasicCommunityTermData(activeUser, dataObject);
		List<TermVocabularySet> termVocabulary = getCommunityTermVocabulary(activeUser, dataObject.getTermId());
		dataObject.setVocabularySets(termVocabulary);

	}


	@Override
	public List<ICommunityTerm> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("loadAllFromDB(activeUser) not implemented.");
		throw new DataAccessErrorException("loadAllFromDB(activeUser) not implemented.");
	}


	@Override
	public List<ICommunityTerm> loadAssociatedData(IUserProfileData activeUser, Community communityData)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		List<ICommunityTerm> termsData = getBasicCommunityTermsData(activeUser, communityData);
		Iterator<ICommunityTerm> it = termsData.iterator();
		while (it.hasNext())
		{
			ICommunityTerm curTermData=it.next();
			List<TermVocabularySet> termVocabulary = getCommunityTermVocabulary(activeUser, curTermData.getTermId());
			curTermData.setVocabularySets(termVocabulary);
		}
	
		return termsData;
		
	}


	@Override
	public List<CommunityTermHandle> loadUserAssociatedData(IUserProfileData activeUser, Community communityData) throws DataAccessErrorException, DataAccessConstraintException {
		List<CommunityTermHandle> termsData = getBasicUserCommunityTermsData(activeUser, communityData);
		Iterator<CommunityTermHandle> it = termsData.iterator();
		while (it.hasNext())
		{
			CommunityTermHandle curTermData=it.next();
			List<TermVocabularySet> termVocabulary = getCommunityTermVocabulary(activeUser, curTermData.getTermId());
			curTermData.setVocabularySets(termVocabulary);
		}
	
		return termsData;
	}
	

	@Override
	public void addAssociation(IUserProfileData activeUser, ICommunityTerm managedData, Community associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("addAssociation(CommunityTermData,CommunityData) not implemented yet for DBCommunitiesAccessor (user="+activeUser+")");
		throw new DataAccessErrorException("Operation not available : addAssociation from communityTerm (user="+activeUser+")");
		
	}


	@Override
	public void removeAssociation(IUserProfileData activeUser, ICommunityTerm managedData, Community associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("removeAssociation(CommunityTermData,CommunityData) not implemented yet for DBCommunitiesAccessor (user="+activeUser+")");
		throw new DataAccessErrorException("Operation not available : removeAssociation from communityTerm (user="+activeUser+")");
		
	}


}
