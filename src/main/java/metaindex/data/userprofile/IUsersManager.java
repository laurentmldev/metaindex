package metaindex.data.userprofile;

import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface IUsersManager extends ILoadableFromDb {

	IUserProfileData getUserByHttpSessionId(String userSessionId);	
	IUserProfileData getUserById(Integer userId);
	IUserProfileData getUserByName(String name);
	void registerUser(IUserProfileData u) throws DataProcessException;
	
}
