package metaindex.dbaccess.impl.metaindexdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.IGenericMetaindexData;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.community.ICommunityTerm;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;


public class DBCatalogAccessor extends ACatalogAccessor  {

	private static boolean SHOW_TODO_IMPROVE_SQL_FULL_TEXT=true;
	
	private final static String SQL_GET_ALL_FROM_DB="select community_catalog_id, community_id,"
																+"name, comment, search_query"
																+" from community_catalogs";
	
	private final static String SQL_DELETE_FROM_DB="delete from community_catalogs";
//-------- DB Management InnerClasses ----------
		/// DB result mapper
		class CatalogMapper implements RowMapper<ICatalog> 
		{
			ICatalog catalog=null;
			ICommunity communityData=null;
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public CatalogMapper(ICatalog data) { catalog = data; }
			
			/** Constructor when we want to create as many instances of Catalog as we found */
			public CatalogMapper(ICommunity community) { communityData = community; }
			
			
			public ICatalog mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  CatalogExtractor extractor;
				  if (catalog==null) { extractor = new CatalogExtractor(communityData); }
				  else { extractor = new CatalogExtractor(catalog); }
				  return extractor.extractData(resultSet);
			}
		}
		class UserCatalogMapper implements RowMapper<ICatalogHandle> 
		{
			ICatalogHandle catalog=null;
			IUserProfileData user=null;
			ICommunity communityData=null;
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public UserCatalogMapper(IUserProfileData userData, ICatalogHandle data) { catalog = data; user=userData; }
			
			/** Constructor when we want to create as many instances of Catalog as we found */
			public UserCatalogMapper(ICommunity community) { communityData = community; }
			
			
			public ICatalogHandle mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  UserCatalogExtractor extractor;
				  if (catalog==null) { extractor = new UserCatalogExtractor(user,communityData); }
				  else { extractor = new UserCatalogExtractor(catalog); }
				  return extractor.extractData(resultSet);
			}
		}
		
		private void extractDBCatalogData(ICatalog catalog, ResultSet resultSet) throws SQLException {
			 catalog.setCatalogId(resultSet.getInt(1));
			 catalog.setCommunityId(resultSet.getInt(2));
			 catalog.setName(unescape(resultSet.getString(3)));
			 catalog.setComment(unescape(resultSet.getString(4)));
			 catalog.setSearchQuery(unescape(resultSet.getString(5)));
		}
		private ICatalog extractDBCatalogData(ICommunity community, ResultSet resultSet) throws SQLException {
			Integer catalogId = resultSet.getInt(1);
			ICatalog cat = null;
			try { cat = community.getCatalog(catalogId); }
			catch (Exception e) {
				cat = new Catalog(community);
				cat.setCatalogId(catalogId);
				community.addCatalog(cat);
			}
			extractDBCatalogData(cat,resultSet);
			return cat;
		}
		/// DB result fields extractor
		class CatalogExtractor implements ResultSetExtractor<ICatalog> 
		{
			ICatalog catalog;
			ICommunity communityData=null;
			public CatalogExtractor(ICatalog data) { catalog=data; communityData = catalog.getCommunityData(); } 
			public CatalogExtractor(ICommunity community) { communityData = community; }
			 
			public ICatalog extractData(ResultSet resultSet) throws SQLException, DataAccessException {  
				 try {
					 if (communityData!=null) { catalog=extractDBCatalogData(communityData,resultSet);}
					 else { extractDBCatalogData(catalog,resultSet); }
					 return catalog;
				 } catch (Exception e) { log.error(e.getMessage()); }				 
				 return null;
			 }
		}
		class UserCatalogExtractor implements ResultSetExtractor<ICatalogHandle> 
		{
			ICatalog catalog=null;
			IUserProfileData user=null;
			ICommunity communityData=null;
			public UserCatalogExtractor(ICatalogHandle data) {
					user=data.getUserProfile();
					try { communityData = CommunitiesAccessor.getCommunity(data.getCommunityId()); } 
					catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {						
						e.printStackTrace();
					}
					catalog=communityData.getCatalog(data.getCatalogId()); 
					
			} 
			public UserCatalogExtractor(IUserProfileData userData,ICommunity community) { user=userData;communityData = community; }
			 
			public ICatalogHandle extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (communityData!=null) { catalog=extractDBCatalogData(communityData,resultSet);}
					 else { extractDBCatalogData(catalog,resultSet); }					 
					 return new CatalogHandle(user, catalog);
				 } catch (Exception e) { log.error(e.getMessage()); }
				 
				 return null;
			 }
		}
	//-------- End of DB Management InnerClasses ----------
	private Log log = LogFactory.getLog(DBCatalogAccessor.class);
	
	public String getTableName() { return "community_catalogs"; };
	
	public DBCatalogAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
		if (SHOW_TODO_IMPROVE_SQL_FULL_TEXT) { log.warn("### TODO : improve SQL fulltext search"); SHOW_TODO_IMPROVE_SQL_FULL_TEXT=false; }
	}
	
	@Override
	public ADBBatchOperator getStoreIntoDBOperator() {
		class StoreIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				result.add(con.prepareStatement(
						"update community_catalogs set "
							+" name=?,"
							+" comment=?,"
							+" search_query=?"			
						+" where community_catalog_id=?"
						
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICatalog dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				PreparedStatement stmt = stmts.get(0);
				stmt.setString(1, escape(dataObject.getName()));
				stmt.setString(2, escape(dataObject.getComment()));
				stmt.setString(3, escape(dataObject.getSearchQuery()));
				stmt.setInt(4, dataObject.getCatalogId());
				stmt.addBatch();
			}
			
		}
		return new StoreIntoDBOperator();
	}
	
	@Override
	public void refreshFromDB(IUserProfileData activeUser, ICatalog catalog)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		loadBasicCatalog(activeUser,catalog);
	}
	
	@Override
	public List<ICatalog> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {
				
		log.error("loadAllDataFromDB not available for Catalog.");
		throw new DataAccessErrorException("loadAllDataFromDB not available for Catalog");
	}

	/**
	 * This method will get basic  community data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadBasicCatalog(IUserProfileData activeUser, ICatalog catalog)
			throws DataAccessErrorException,DataAccessConstraintException {
		
		// get catalog infos						
			String sql=SQL_GET_ALL_FROM_DB 
							+ " where community_catalogs.community_catalog_id='"+(catalog.getCatalogId())+"';";
						
			List<ICatalog> results = querySqlData(sql, new CatalogMapper(catalog));
			if (results.size()==0) 
			{ 
				throw new DataAccessErrorException("No result for catalog '"+catalog.getCatalogId()+"'"); 
			}		
			else if (results.size()>1) {
				throw new DataAccessErrorException("More than 1 result for catalog '"+catalog.getCatalogId()+"'");
			}
	}


	@Override
	public List<Integer> getTemplateElementsIds(IUserProfileData activeUser, ICommunity community)
			throws DataAccessErrorException {
		
		String sql="select community_elements.community_element_id from community_elements "
						+ " left join community_templates on community_templates.community_element_id = community_elements.community_element_id"
						+" where community_elements.community_id='"+community.getCommunityId()+"' "
						+" and community_templates.isTemplate='1'"
						+" order by community_elements.name";
		
		List<Integer> result = queryForIntegerList(sql);

		return result;

		
	}

	@Override
	public List<Integer> getCatalogStaticElementsIds(IUserProfileData activeUser, ICatalog catalog)
			throws DataAccessErrorException {
		
		String sql="select community_element_id from community_static_catalogs_elements "
						+"where community_catalog_id='"+catalog.getCatalogId()+"'";
		
		List<Integer> result = queryForIntegerList(sql);

		return result;

		
	}
	
	@Override
	public void deleteFromDB(IUserProfileData activeUser, ICatalog catalog) throws DataAccessErrorException {
		
		List<String> deleteCatalogQueries=new ArrayList<String>();
		
		// delete static refs
		deleteCatalogQueries.add("DELETE FROM community_static_catalogs_elements where community_catalog_id='"+catalog.getCatalogId()+"'");
		
		// delete the catalog itself
		// here the community_id check is not functionally required but sounds like a proper safeguard in case of any
		// DB corruption if happens
		deleteCatalogQueries.add("DELETE FROM community_catalogs where community_catalog_id='"+catalog.getCatalogId()+"' "
																		 +"and community_id='"+catalog.getCommunityId()+"'");
		this.executeSqlTransaction(deleteCatalogQueries);
		
	}
	
	@Override
	public List<Integer> getCatalogDynamicElementsIds(IUserProfileData activeUser, ICatalog catalog) throws DataAccessErrorException {

		
		// Matching searchQuery over element, datasets and metadata of our community
		// returning IDs of matching elements
		String sql="select community_elements.community_element_id "
				+" from community_elements"
				+ " left join community_datasets on community_datasets.community_element_id=community_elements.community_element_id"
				+ " left join community_metadata on community_metadata.community_dataset_id=community_datasets.community_dataset_id"
				+ " left join community_templates on community_templates.community_element_id = community_elements.community_element_id"
				+ " where "
				+" community_templates.isTemplate is NULL"
				+" and community_elements.community_id="+catalog.getCommunityId()
				+ " and "
				+ " ("			
				
				+" match(community_metadata.name,community_metadata.comment,"
						+"community_metadata.valueString1,"
						+"community_metadata.valueString2,"
						+"community_metadata.valueString3,"
						+"community_metadata.valueString4,"
						+"valueLongString)"
						+ " against ('"+escape(catalog.getSearchQuery())+"' in boolean mode)"
						
				+ " or match(community_datasets.name,community_datasets.comment)"
						+ " against ('"+escape(catalog.getSearchQuery())+"' in boolean mode)"
				+ " or match(community_elements.name,community_elements.comment)"
						+ " against ('"+escape(catalog.getSearchQuery())+"' in boolean mode)"
				+ " )"
				+ " group by community_elements.community_element_id"
				+ " order by community_elements.name"
				+";";
		
		// just build a simplified request for the 'get all elements' query
		// This is used for the automatic 'all' catalog. 
		if (catalog.getSearchQuery().equals("*")) {
			sql="select community_elements.community_element_id "
					+" from community_elements"
					+ " left join community_templates on community_templates.community_element_id = community_elements.community_element_id"
					+ " where community_elements.community_id="+catalog.getCommunityId()
					+ " and community_templates.isTemplate is NULL"
					+" order by community_elements.community_element_id";
		}	
						
		List<Integer> result = queryForIntegerList(sql);
		return result;
		
	}
	
	@Override 
	public ADBBatchOperator getCreateIntoDBOperator() {
		class CreateIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"insert into community_catalogs (community_catalog_id,community_id,name,comment) "
						+"values (?,?,?,?)"));
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICatalog dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				PreparedStatement stmt = stmts.get(0);
				dataObject.setCatalogId(CommunitiesAccessor.getNewCatalogId());
				stmt.setInt(1, dataObject.getCatalogId());
				stmt.setInt(2, dataObject.getCommunityId());
				stmt.setString(3, escape(dataObject.getName()));
				stmt.setString(4, escape(dataObject.getComment()));
				stmt.addBatch();
			}
		}
		return new CreateIntoDBOperator();
	}		


	@Override 
	public ADBBatchOperator getDeleteFromDBOperator() {
		class DeleteFromDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"delete from community_catalogs,community_static_catalogs_elements where "
						+" community_catalogs.community_catalog_id = ?"
						+" and community_static_catalogs_elements.community_catalog_id = community_catalogs.community_catalog_id"
						+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, ICatalog dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				
				PreparedStatement stmt = stmts.get(0); 
				stmt.setInt(1, dataObject.getCatalogId());
				stmt.addBatch();
				
			}
		}
		return new DeleteFromDBOperator();
	}
	

	
	@Override
	public List<ICatalog> loadAssociatedData(IUserProfileData activeUser, ICommunity associatedObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		String sql=SQL_GET_ALL_FROM_DB +
					" where community_catalogs.community_id='"+associatedObject.getCommunityId()+"';";
		
		List<ICatalog> results = querySqlData(sql, new CatalogMapper(associatedObject));
		
		return results;
	}

	@Override
	public List<ICatalogHandle> loadUserAssociatedData(IUserProfileData activeUser, ICommunity associatedObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		String sql=SQL_GET_ALL_FROM_DB +
				" where community_catalogs.community_id='"+associatedObject.getCommunityId()+"';";
	
		List<ICatalogHandle> results = querySqlData(sql, new UserCatalogMapper(associatedObject));
			
			return results;
	}

	@Override
	public void addAssociation(IUserProfileData activeUser, ICatalog managedData, ICommunity associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("addAssociation(ElementData) not implemented yet for DBCatalogAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : addAssociation for Catalog/ElementData (user="+activeUser.getUsername()+")");
		
	}


	@Override
	public void removeAssociation(IUserProfileData activeUser, ICatalog managedData, ICommunity associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("removeAssociation(ElementData) not implemented yet for DBCatalogAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : removeAssociation for Catalog/ElementData (user="+activeUser.getUsername()+")");
		
	}

	
	
}
