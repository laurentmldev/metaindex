package metaindex.data.userprofile;
/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import toolbox.exceptions.DataProcessException;

public class UsersManager implements IUsersManager {
	
	private Log log = LogFactory.getLog(UsersManager.class);
	
	private Map<String,IUserProfileData> _usersByName = new java.util.concurrent.ConcurrentHashMap<>();
	private Semaphore _usersLock = new Semaphore(1,true);
	
	@Override
	public IUserProfileData getUserByHttpSessionId(String userSessionId) {
		
		return _usersByName.values().stream()
						.filter(p -> p.getHttpSessionId().equals(userSessionId))
						.findFirst()
						.orElse(null);
	}
	
	@Override
	public IUserProfileData getUserByName(String name) {
		
		IUserProfileData result = _usersByName.values().stream()
						.filter(p -> p.getName().equals(name))
						.findFirst()
						.orElse(null);
		
		// if user could not be found, maybe it has been added
		// in DB and contents need to be reloaded
		if (result==null) { 
			try { 
				log.info("User '"+name+"' not found, reloading users list from DB");
				loadFromDb();
				result = _usersByName.values().stream()
						.filter(p -> p.getName().equals(name))
						.findFirst()
						.orElse(null);
			} 
			catch (DataProcessException e) { e.printStackTrace(); }
		}
		
		return result;
	}

	@Override
	public IUserProfileData getUserById(Integer userId) {
		for (IUserProfileData user : _usersByName.values()) {
			if (user.getId().equals(userId)) { return user; }			
		}
		return null;
	}
	
	@Override
	public void registerUser(IUserProfileData u) throws DataProcessException {
		try {
			_usersLock.acquire();
		} catch (InterruptedException e) {
			throw new DataProcessException("Unable to add user '"+u.getName()+" : "+e.getMessage());
		}
		if (u.getHttpSessionId().length()>0 
				&& getUserByHttpSessionId(u.getHttpSessionId())!=null
				&& !getUserByHttpSessionId(u.getHttpSessionId()).getName().equals(u.getName())) {
			throw new DataProcessException("Session ID '"+u.getHttpSessionId()
					+"' already registered to user '"
					+getUserByHttpSessionId(u.getHttpSessionId()).getName()
					+"', unable to add new user '"+u.getName()+"'.");
		}
		_usersByName.put(u.getName(), u);
		_usersLock.release();
		
	}
	
	@Override
	public void loadFromDb() throws DataProcessException {
		try {
			_usersLock.acquire();
			List<IUserProfileData> loadedUsers=new ArrayList<>();
			Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getPopulateUserProfileFromDbStmt(loadedUsers).execute();
			for (IUserProfileData u : loadedUsers) {
				_usersByName.put(u.getName(), u);
			}
			_usersLock.release();
		} catch (Exception e) {
			log.error("Unable to load contents of UserProfileData from DB "+e.getMessage());
			_usersLock.release();
			throw new DataProcessException(e); 
		}
	}
	
}
