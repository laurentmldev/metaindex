package metaindex.data.userprofile;

import java.util.List;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface IUsersManager extends ILoadableFromDb {

	IUserProfileData getUserByHttpSessionId(String userSessionId);	
	IUserProfileData getUserById(Integer userId);
	/// create user if no exist
	IUserProfileData getUserByName(String name);
	/// return only if exist, null otherwise
	IUserProfileData getExistingUserByName(String name);
	List<IUserProfileData> getUsersList() throws DataProcessException;
	void registerUser(IUserProfileData u) throws DataProcessException;
	void registerAnonymousUser(IUserProfileData u) throws DataProcessException;
	
}
