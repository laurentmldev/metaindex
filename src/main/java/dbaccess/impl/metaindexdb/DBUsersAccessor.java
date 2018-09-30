package metaindex.dbaccess.impl.metaindexdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AUserProfileAccessor;

/**
 * Accessor to a MetaIndex UserProfile.
 * @author laurent
 *
 */
public class DBUsersAccessor extends AUserProfileAccessor {

//-------- DB Management InnerClasses ----------
	/// DB result mapper
	class UserProfileMapper implements RowMapper<IUserProfileData> 
	{
		IUserProfileData userData = null;
		UserProfileMapper(IUserProfileData userData){ this.userData=userData;}
		 public IUserProfileData mapRow(ResultSet resultSet, int line) throws SQLException 
		 {
			  UserProfileExtractor languagesExtractor = new UserProfileExtractor(userData);
			  return languagesExtractor.extractData(resultSet);
		 }
	}

	/// DB result fields extractor
	class UserProfileExtractor implements ResultSetExtractor<IUserProfileData> 
	{
		IUserProfileData userData = null;
		UserProfileExtractor(IUserProfileData userData) { this.userData=userData;}
		 public IUserProfileData extractData(ResultSet resultSet) throws SQLException, DataAccessException {  

			 userData.setUserId(resultSet.getInt(1));
			 userData.setUsername(resultSet.getString(2));
			 userData.setEmail(resultSet.getString(3));
			 userData.setGuiLanguageId(resultSet.getInt(4));
			 userData.setGuiThemeId(resultSet.getInt(5));
			 return userData;
		 }
	}
//-------- End of DB Management InnerClasses ----------

	
	private Log log = LogFactory.getLog(DBUsersAccessor.class);
	
	static final String SQL_GET_ALL_FROM_GUILANGUAGES = "select * from guilanguages";
	static final String SQL_ROLE_USER = "ROLE_USER";
	
	static final String SQL_GET_ALL_FROM_USERPROFILE = 
				"select users.user_id,users.username,users.email,users.guilanguage_id,users.guitheme_id"							
					+" from users";
	

	public DBUsersAccessor(ADataAccessFactory accessorsFactory,DataSource datasource,PlatformTransactionManager txManager) 
								throws DataAccessErrorException,DataAccessConnectException
	{
		super(accessorsFactory,datasource,txManager);		
	}	
	
	@Override
	public void createIntoDB(IUserProfileData activeUser, IUserProfileData dataObject)
		throws DataAccessErrorException,DataAccessConstraintException {
				
		String email=dataObject.getEmail();
		String username=dataObject.getUsername();
		String password=dataObject.getPassword();
		boolean isEnabled=dataObject.isEnabled();
		int languageId=dataObject.getGuiLanguageId();
		int themeId=dataObject.getGuiThemeId();
		
		String enabled="0";
		List<String> createUserProfileTransaction = new ArrayList<String>();
		if (isEnabled) { enabled="1"; }
		// add 'users' entry
		createUserProfileTransaction.add("insert into users (email, username, password, enabled,guilanguage_id,guitheme_id) values ("
									+"'"+email+"','"+username+"','"+password+"','"+enabled+"','"+languageId+"','"+themeId+"');");
		// add 'user_roles' entry
		createUserProfileTransaction.add("insert into user_roles (role,user_id) values ("
									+"'"+SQL_ROLE_USER+"',(select user_id from users where email = '"+email+"'));"); 
		try {
			executeSqlTransaction(createUserProfileTransaction);			
		}
		catch (DataAccessErrorException|DataAccessConstraintException e)
		{	
			//log.error("DB Error while creating Profile : "+e.getMessage());
			throw e;
		}				

	}
	
	@Override
	public List<String> prepareStoreIntoDB(IUserProfileData activeUser, IUserProfileData dataObject)
			throws DataAccessErrorException {
		
			List<String> sqlUpdateSequence=new ArrayList<String>();
			String username=dataObject.getUsername();
			String email=dataObject.getEmail();
			int languageId=dataObject.getGuiLanguageId();
			int themeId=dataObject.getGuiThemeId();
			String sql_userDetails="update users set guilanguage_id='"+languageId+"',guitheme_id='"+themeId+"',email='"+email+"'"
									+" where username='"+username+"';"; 
			sqlUpdateSequence.add(sql_userDetails);
			return sqlUpdateSequence;		
	}
	
	
	@Override
	public void refreshFromDB(IUserProfileData activeUser, IUserProfileData profileData) throws DataAccessErrorException
	{		
		String sql = SQL_GET_ALL_FROM_USERPROFILE + " where users.username = '"+profileData.getUsername()+"'";
		
		try {
			// only one result so we just get the first one
			List<IUserProfileData> results = this.querySqlData(sql, new UserProfileMapper(profileData));
			if (results.size()==0) { throw new DataAccessErrorException("No result for user '"+profileData.getUsername()+"'"); }
		} catch (Exception e)
		{
			log.error("DB Error while retrieving UserProfile details : "+e.getMessage());
			throw new DataAccessErrorException(e);
		}
	}

	@Override
	public List<IUserProfileData> loadAllDataFromDB(IUserProfileData activeUser)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("loadAllFromDB(username) not implemented.");
		throw new DataAccessErrorException("loadAllFromDB(username) not implemented.");
	}

	@Override
	public List<String> prepareDeleteFromDB(IUserProfileData activeUser, IUserProfileData dataObject)
			throws DataAccessErrorException, DataAccessConstraintException {
		log.error("deleteFromDB not implemented yet for User objects (user="+activeUser.getUsername()+")");
		throw new DataAccessErrorException("Operation not available : deleteFromDB for User objects (user="+activeUser.getUsername()+")");		
	}

}

