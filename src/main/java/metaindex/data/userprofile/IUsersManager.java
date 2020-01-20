package metaindex.data.userprofile;

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
	IUserProfileData getUserByName(String name);
	void registerUser(IUserProfileData u) throws DataProcessException;
	
}
