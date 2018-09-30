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

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;

import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;

import metaindex.data.dataset.IDataset;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;

import metaindex.dbaccess.accessors.AElementAccessor;



public class DBElementAccessor extends AElementAccessor {

	private final static String SQL_GET_ALL_FROM_DB="select e.community_element_id, e.community_id, "
													+"e.name, e.comment, t.isTemplate, e.templateRefElementId, e.thumbnailUrl "
													+"from community_elements as e "
													+"left join community_templates as t on t.community_element_id = e.community_element_id";
	
		
//-------- DB Management InnerClasses ----------
		/// DB result mapper
		class ElementMapper implements RowMapper<IElement> 
		{
			IElement elementData=null;
			ICommunity communityData;
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public ElementMapper(IElement data) { elementData = data; }
			
			/** Constructor when we want to create as many instances of ElementData as we found */
			public ElementMapper(ICommunity communityData) { this.communityData=communityData; }
			
			public IElement mapRow(ResultSet resultSet, int line) throws SQLException 
			{
				  ElementExtractor elementDataExtractor ;
				  if (elementData!=null) { elementDataExtractor = new ElementExtractor(elementData); }
				  else { elementDataExtractor = new ElementExtractor(communityData); }
				  return elementDataExtractor.extractData(resultSet);
			}
		}

		class UserElementMapper implements RowMapper<IElementHandle> 
		{
			IUserProfileData user;
			IElement elementData=null;
			ICommunity communityData;
			/** Constructor when we want to directly store the result in the given instance 
			 * If there is more than one result then the last result will be in the given instance 
			 */
			public UserElementMapper(IUserProfileData user,IElement data) { this.user=user;elementData = data; }
			
			/** Constructor when we want to create as many instances of ElementData as we found */
			public UserElementMapper(IUserProfileData user,ICommunity communityData) { this.user=user;this.communityData=communityData; }
			
			public IElementHandle mapRow(ResultSet resultSet, int line) throws SQLException 
			{

				  UserElementExtractor elementDataExtractor ;
				  if (elementData!=null) { elementDataExtractor = new UserElementExtractor(user,elementData); }
				  else { elementDataExtractor = new UserElementExtractor(user,communityData); }
				  return elementDataExtractor.extractData(resultSet);
			}
		}
		class ElementIdMapper implements RowMapper<Integer> 
		{			
			public Integer mapRow(ResultSet resultSet, int line) throws SQLException { return resultSet.getInt(1); }
		}
		
		/**
		 * Populate DB data into given Element object
		 * @param elementData
		 * @param resultSet
		 * @throws SQLException
		 */
		private void extractDBdata(IElement elementData,ResultSet resultSet) throws SQLException {
			 elementData.setElementId(resultSet.getInt(1));
			 elementData.setCommunityId(resultSet.getInt(2));
			 elementData.setName(unescape(resultSet.getString(3)));
			 elementData.setComment(unescape(resultSet.getString(4)));
			 elementData.setTemplate(resultSet.getInt(5)==1);
			 elementData.setTemplateRefElementId(resultSet.getInt(6));
			 
			 // To be removed : thumbnailUrl is dynamically deduced from metadatas marked as 'thumbnail'
			 elementData.setThumbnailUrl(resultSet.getString(7));
		}
		/**
		 * Try to find Element object in given community (based on elementId).
		 * If found reuse it, otherwise recreate it.
		 * @param community
		 * @param resultSet
		 * @throws SQLException
		 */
		private IElement extractDBdata(IUserProfileData activeUser, ICommunity community,ResultSet resultSet) throws SQLException {
			 Integer elementId = resultSet.getInt(1);			 
			 IElement el = null;			 
			 // if this element does not exist in the community we add it
			 try { el = community.getElement(elementId);}
			 catch (DataAccessErrorException ex)
			 { 
				 el = new Element(community);
				 el.setElementId(elementId);
				 community.addElement(activeUser,el);
			 }
			 extractDBdata(el, resultSet);
			 return el;
		}
		/// DB result fields extractor
		class ElementExtractor implements ResultSetExtractor<IElement> 
		{
			IUserProfileData activeUser=null;
			IElement elementData=null;
			ICommunity communityData;
			public ElementExtractor(IElement data) { elementData=data; } 
			public ElementExtractor(ICommunity communityData) { this.communityData=communityData; } 
			public IElement extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				try {
					 if (communityData!=null) { elementData=extractDBdata(activeUser,communityData,resultSet); }
					 else { extractDBdata(elementData,resultSet); }					 					 
					 return elementData;
				 } catch (Exception e) { log.error(e.getMessage()); }
				 				  
				 return null;
			 }
		}
		class UserElementExtractor implements ResultSetExtractor<IElementHandle> 
		{
			IUserProfileData activeUser;
			IElement elementData=null;
			ICommunity communityData=null;
			public UserElementExtractor(IUserProfileData user,IElement data) { this.activeUser=user;elementData=data; } 
			public UserElementExtractor(IUserProfileData user,ICommunity communityData) { this.activeUser=user;this.communityData=communityData; } 
			public IElementHandle extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

				 try {
					 if (communityData!=null) { elementData=extractDBdata(activeUser,communityData,resultSet); }
					 else { extractDBdata(elementData,resultSet); }					 
					 return new ElementHandle(activeUser,elementData);
				 } catch (Exception e) { log.error(e.getMessage()); }
				 
				 return null;
			 }
		}		
	
	//-------- End of DB Management InnerClasses ----------
	private Log log = LogFactory.getLog(DBElementAccessor.class);
	
	public DBElementAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);
	}
	
	@Override
	public String getTableName() { return "community_elements"; }


	@Override 
	public ADBBatchOperator getCreateIntoDBOperator() {
		class CreateIntoDBOperator extends ADBBatchOperator {

			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>();
				
				result.add(con.prepareStatement(
						"insert into community_elements (community_element_id, community_id, name, comment, templateRefElementId,thumbnailUrl) "
						+"values (?,?,?,?,?,?)"));
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IElement dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				dataObject.checkDataDBCompliance();
				PreparedStatement stmt = stmts.get(0);
				stmt.setInt(1, dataObject.getElementId());
				stmt.setInt(2, dataObject.getCommunityId());
				stmt.setString(3, escape(dataObject.getName()));
				stmt.setString(4, escape(dataObject.getComment()));
				if (dataObject.getTemplateRefElementId()==0) { stmt.setNull(5, java.sql.Types.INTEGER); }
				else { stmt.setInt(5, dataObject.getTemplateRefElementId()); }
				stmt.setString(6, escape(dataObject.getThumbnailUrl()));
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
						"update community_elements set "
						+" name=?,"
						+" comment=?,"
						+" templateRefElementId=?,"					
						+" thumbnailUrl=?"
						+" where community_element_id=?"				
					+";"));
				
				result.add(con.prepareStatement("delete from community_templates where community_templates.community_element_id=?;"));
				result.add(con.prepareStatement("insert into community_templates (community_element_id,isTemplate) "
																	+"values (?,?) on duplicate key update isTemplate=1"));
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IElement dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				PreparedStatement stmt_data = stmts.get(0);
				PreparedStatement stmt_delete_from_templates = stmts.get(1);
				PreparedStatement stmt_insert_into_templates = stmts.get(2);
				
				dataObject.checkDataDBCompliance();
				stmt_data.setString(1, escape(dataObject.getName()));
				stmt_data.setString(2, escape(dataObject.getComment()));
				if (dataObject.getTemplateRefElementId()!=0) { stmt_data.setInt(3, dataObject.getTemplateRefElementId()); }
				else { stmt_data.setNull(3, java.sql.Types.INTEGER); }
				stmt_data.setString(4, dataObject.getThumbnailUrl());
				stmt_data.setInt(5, dataObject.getElementId());
				stmt_data.addBatch();
				
				if (!dataObject.isTemplate()) {
					stmt_delete_from_templates.setInt(1,dataObject.getElementId());
					stmt_delete_from_templates.addBatch();
				}
				else {
					int isTemplateInt = 0;
					if (dataObject.isTemplate()) { isTemplateInt=1; } 
					stmt_insert_into_templates.setInt(1,dataObject.getElementId());
					stmt_insert_into_templates.setInt(2,isTemplateInt);
					stmt_insert_into_templates.addBatch();
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
							"delete from community_elements "
							+"where community_elements.community_element_id=?"
							+";"));
				
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IElement dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				PreparedStatement stmt_data = stmts.get(0);
				
				dataObject.checkDataDBCompliance();
				stmt_data.setInt(1, dataObject.getElementId());
				stmt_data.addBatch();								
			}
			
		}
		return new DeleteFromDBOperator();
	}
	
	@Override
	public void refreshFromDB(IUserProfileData activeUser, IElement elementData)
			throws DataAccessErrorException,DataAccessConstraintException {
		loadBasicElementData(activeUser,elementData);		
	}
	
	@Override
	public List<IElement> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException {
				
		log.error("loadUserRelatedDataFromDB not available for ElementData.");
		throw new DataAccessErrorException("loadUserRelatedDataFromDB not available for ElementData");
	}

	/**
	 * This method will get basic  community data (like ID for example)
	 * @param communityIdName the ID code (String) of the community
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	private void loadBasicElementData(IUserProfileData activeUser, IElement elementData)
			throws DataAccessErrorException,DataAccessConstraintException {
		
			// get elementData infos						
			String sql= SQL_GET_ALL_FROM_DB
							+ " where e.community_element_id='"+(elementData.getElementId())+"';";
			//log.error("### Loading element '"+elementData.getElementId()+"' into community "+elementData.getCommunityId()+" : "+sql);
				
			List<IElement> results = querySqlData(sql, new ElementMapper(elementData));
			if (results.size()==0) 
			{ 
				throw new DataAccessErrorException("No result for element '"+elementData.getElementId()+"'"); 
			}		
			else if (results.size()>1) {
				throw new DataAccessErrorException("More than 1 result for element '"+elementData.getElementId()+"'");
			}	
			//log.error("### Loaded element '"+elementData.getElementId()+"' into community "+elementData.getCommunityId());
	}

	@Override
	public List<IElement> loadAssociatedData(IUserProfileData activeUser, ICommunity communityData)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		String sql= SQL_GET_ALL_FROM_DB
				+ " where e.community_id='"+(communityData.getCommunityId())+"'"
				+" order by e.name;";

		List<IElement> results = querySqlData(sql, new ElementMapper(communityData));
		
		return results;
	}

	@Override
	public List<IElementHandle> loadUserAssociatedData(IUserProfileData activeUser, ICommunity communityData)
			throws DataAccessErrorException, DataAccessConstraintException {
		String sql= SQL_GET_ALL_FROM_DB
				+ " where e.community_id='"+(communityData.getCommunityId())+"'"
				+" order by e.name;";

		List<IElementHandle> results = querySqlData(sql, new UserElementMapper(activeUser,communityData));
		
		return results;
	}



	@Override
	public void addAssociation(IUserProfileData activeUser, IElement managedData, ICommunity associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("addAssociation(CommunityData) not implemented yet for DBCommunitiesAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : addAssociation for ElementData/CommunityData (user="+activeUser.getUsername()+")");
		
	}


	@Override
	public void removeAssociation(IUserProfileData activeUser, IElement managedData, ICommunity associatedData)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("removeAssociation(CommunityData) not implemented yet for DBCommunitiesAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : removeAssociation for ElementData/CommunityData (user="+activeUser.getUsername()+")");
	}


	@Override
	public List<IElement> loadAssociatedData(IUserProfileData activeUser, ICatalogContents catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		// this is never used : we just retrieve the list of ids, via a corresponding method
		// in DBCatalogsAccessor
		log.error("loadAssociatedData(Catalog) not implemented yet for DBElementsAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : loadAssociatedData for ElementData/Catalog (user="+activeUser.getUsername()+")");
	}


	@Override
	public List<IElement> loadUserAssociatedData(IUserProfileData activeUser, ICatalogContents associatedObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		// this is never used : we just retrieve the list of ids, via a corresponding method
		// in DBCatalogsAccessor
		log.error("loadAssociatedData(Catalog) not implemented yet for DBElementsAccessor (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : loadAssociatedData for ElementData/Catalog (user="+activeUser.getUsername()+")");
	}
	

	@Override
	public void addAssociation(IUserProfileData activeUser, IElement managedData, ICatalogContents catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		String sql="insert into community_static_catalogs_elements (community_catalog_id, community_element_id) values ('"
				+catalog.getCatalogId()+"', '"+managedData.getElementId()+"');";
		
		this.insertSqlRow(sql);		
		
	}

	public ADBBatchOperator getRemoveCatalogStaticAssocitationDBOperator(ICatalogContents c) {
    	class RemoveCatalogStaticAssocitationBatchOperator extends ADBBatchOperator {
    		Integer catalogId = 0;
    		RemoveCatalogStaticAssocitationBatchOperator(Integer cat) { catalogId = cat; }
			@Override
			public List<PreparedStatement> createStatements(ICommunityHandle c, Connection con) throws SQLException {
				List<PreparedStatement> result = new ArrayList<PreparedStatement>(); 
				result.add(con.prepareStatement(
						"delete from community_static_catalogs_elements "
								+"where community_catalog_id=?"
								+" and community_element_id=?;"));
				return result;
			}

			@Override
			public void populateStatements(IUserProfileData activeUser, IElement dataObject,
					List<PreparedStatement> stmts) throws SQLException {
				PreparedStatement stmt = stmts.get(0);
				stmt.setInt(1, catalogId);
				stmt.setInt(2, dataObject.getElementId());
				stmt.addBatch();				
			}    		
    	}
    	return new RemoveCatalogStaticAssocitationBatchOperator(c.getCatalogId());
    }
     
	@Override
	public void removeAssociation(IUserProfileData activeUser, IElement managedData, ICatalogContents catalog) {
		List<IElement> elList = new ArrayList<IElement>();
		elList.add(managedData);
		removeAssociation(activeUser, elList, catalog);
	}
	@Override
	public void removeAssociation(IUserProfileData activeUser, List<IElement> managedData, ICatalogContents catalog)
			throws DataAccessErrorException, DataAccessConstraintException {
		
		this.executeBatchDBOperations(activeUser, managedData, this.getRemoveCatalogStaticAssocitationDBOperator(catalog));
	}


	@Override
	public List<Integer> getElementsByTerm(Integer termId) {
		String sql="select community_datasets.community_element_id"
			//+",community_elements.name"
			+" from community_metadata,community_datasets"
			//+",community_elements"
			+" where community_term_id="+termId 
			+" and community_metadata.community_dataset_id=community_datasets.community_dataset_id"
			//+" and community_datasets.community_element_id=community_elements.community_element_id"
			+" group by community_datasets.community_element_id";
		
		List<Integer> results = querySqlData(sql, new ElementIdMapper());
		
		return results;	
	}


}
