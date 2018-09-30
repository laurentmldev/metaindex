package metaindex.data.management;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADBAccessedData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class UsersAccessor extends ADBAccessedData {

	private static Log log = LogFactory.getLog(UsersAccessor.class);
	private static final String ADMIN_USER_NAME="root";
	
	
	// Map : sessionId / Community object
	static Map<String,IUserProfileData> instanciatedUsers = new HashMap<String, IUserProfileData>();
	
	static public ADataAccessFactory getDataAccessors() {
		return singleton_myself.getDataAccess();
	}
	static UsersAccessor singleton_myself = new UsersAccessor();
	
		
	private UsersAccessor() {
		super(ADataAccessFactory.getDataAccessImplFactory(
				ADataAccessFactory.DATA_ACCESS_IMPL_DB_METAINDEX));	
		
		
	} 
	
	/**
	 * Get user already found in the list, or if not create a new instance
	 * @param sessionId
	 * @return updated community. Create community if not already exists.
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public static IUserProfileData getOrCreateUserProfile(String sessionId) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		if (sessionId.length()==0) {
			throw new DataReferenceErrorException("sessionId is empty, unable to retrieve corresponding user.");
		}
		
		if (!instanciatedUsers.containsKey(sessionId)) { 		
			IUserProfileData newUserInstance = new UserProfileData();
			newUserInstance.setSessionId(sessionId);
			addUser(newUserInstance);	
		}
		return instanciatedUsers.get(sessionId);
	}
	
	public static void addUser(IUserProfileData u) throws DataAccessConstraintException {
		if (instanciatedUsers.containsKey(u.getSessionId())) {
			throw new DataAccessConstraintException("User '"+u.getSessionId()+"' already registered, unable to add it again.");			
		}
		
		instanciatedUsers.put(u.getSessionId(), u);		
	}
	
	public static void logUser(String sessionId, String username) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		if (!userExists(sessionId)) {
			throw new DataAccessConstraintException("User '"+sessionId+"' not registered yet, unable to log it in.");			
		}
		IUserProfileData u = getUserProfileData(sessionId);		
		if (u.isLoggedIn()) {
			throw new DataAccessConstraintException("User '"+sessionId+"' already logged in. Unable to log it in again.");
		}
		
		IUserProfileData adminUser = new UserProfileData();
		adminUser.setUsername(ADMIN_USER_NAME);
		u.logIn(adminUser,username);
	}
	
	/**
	 * Retrieve required community if found null otherwise.
	 * @param sessionId
	 * @return searched community if found, throw exception otherwise. 
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 * @throws DataReferenceErrorException
	 */
	public static IUserProfileData getUserProfileData(String sessionId) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
		
		if (sessionId.length()==0) {
			throw new DataReferenceErrorException("SessionId is empty, unable to retrieve corresponding community.");
		}		
		
		if (! instanciatedUsers.containsKey(sessionId)) {			
			throw new DataReferenceErrorException("Unable to properly get User with session id '"+sessionId);			
		}
		IUserProfileData u = instanciatedUsers.get(sessionId);
		return u;
	}
	
	public static boolean userExists(String sessionId) {		
		return instanciatedUsers.containsKey(sessionId);
	}

	
	public static IUserProfileData getUserProfileDataByEmail(String searchedSessionId) 
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException  {
				
				Iterator<IUserProfileData> it = instanciatedUsers.values().iterator();
				
				while (it.hasNext()) {
					IUserProfileData u = it.next();
					if (u.getSessionId().equals(searchedSessionId)) { return u; }
				}
				throw new DataReferenceErrorException("User with sessionId '"+searchedSessionId+"' not found.");
		}
	
	/**
	 * Empty currently loaded communities (useful for UTests essentially)
	 */
	public static void reset() {
		instanciatedUsers.clear();
		CatalogsAccessor.clear();			
	}
		
}
