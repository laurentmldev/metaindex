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
	private Map<String,IUserProfileData> _anonymousUsersBySessionId = new java.util.concurrent.ConcurrentHashMap<>();
	
	private Semaphore _usersLock = new Semaphore(1,true);
	
	@Override
	public IUserProfileData getUserByHttpSessionId(String userSessionId) {
		
		IUserProfileData u = _usersByName.values().stream()
						.filter(p -> p.getHttpSessionId().equals(userSessionId))
						.findFirst()
						.orElse(null);
				
		if (u==null) {
			return _anonymousUsersBySessionId.values().stream()
			.filter(p -> p.getHttpSessionId().equals(userSessionId))
			.findFirst()
			.orElse(null);
		}
		return u; 
	}
	
	@Override
	public IUserProfileData getUserByName(String name) {
		
		IUserProfileData result = _usersByName.values().stream()
						.filter(p -> p.getName().equals(name))
						.findFirst()
						.orElse(null);
		
		// if user could not be found, maybe it is not loaded yet from DB
		// so we give it a try
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
	public IUserProfileData getExistingUserByName(String name) {
		
		return _usersByName.values().stream()
						.filter(p -> p.getName().equals(name))
						.findFirst()
						.orElse(null);
		
	}
	
	@Override
	public IUserProfileData getUserById(Integer userId) {
		for (IUserProfileData user : _usersByName.values()) {
			if (user.getId().equals(userId)) { return user; }			
		}
		// if user could not be found, maybe it is not loaded yet from DB
		// so we give it a try
		 
		try { 
			log.info("User '"+userId+"' not found, reloading users list from DB");
			loadFromDb();
			return _usersByName.values().stream()
					.filter(p -> p.getId().equals(userId))
					.findFirst()
					.orElse(null);
		} 
		catch (DataProcessException e) { e.printStackTrace(); }
	
		return null;
	}
	
	@Override
	public void registerUser(IUserProfileData u) throws DataProcessException {
		try {
			_usersLock.acquire();
		} catch (InterruptedException e) {
			throw new DataProcessException("Unable to add user '"+u.getId()+" : "+e.getMessage());
		}
		if (u.getHttpSessionId().length()>0 
				&& getUserByHttpSessionId(u.getHttpSessionId())!=null
				&& !getUserByHttpSessionId(u.getHttpSessionId()).getName().equals(u.getName())
				&& getUserByHttpSessionId(u.getHttpSessionId()).getName().length()>0)
		{
			_usersLock.release();
			throw new DataProcessException("Session ID '"+u.getHttpSessionId()
					+"' already registered to user '"
					+getUserByHttpSessionId(u.getHttpSessionId()).getName()
					+"', unable to add new user '"+u.getId()+"'.");
		}
		if (u.getName().length()==0) {
			_usersLock.release();
			throw new  DataProcessException("Trying to register user by name with empty name");
		}
		if (u.getHttpSessionId().length()==0) {
			_usersLock.release();
			throw new  DataProcessException("Trying to register user by name with empty session id");
		}

		if (_usersByName.containsKey(u.getName())) {
			if (_usersByName.get(u.getName())!=u) {
				_usersLock.release();
				throw new  DataProcessException("Trying to register duplicate user "+u.getId());
			}
		}
		
		// remove user from pending anonymous users
		if (_anonymousUsersBySessionId.containsValue(u) ) { _anonymousUsersBySessionId.remove(u.getHttpSessionId()); }			

		_usersLock.release();
		
	}
	
	@Override
	public void registerAnonymousUser(IUserProfileData u) throws DataProcessException {
		try {
			_usersLock.acquire();
		} catch (InterruptedException e) {
			throw new DataProcessException("Unable to add user '"+u.getName()+" : "+e.getMessage());
		}
		
		if (u.getHttpSessionId().length()==0) {
			_usersLock.release();
			throw new  DataProcessException("Trying to register anonymous user with empty session id");
		}
		if (u.getName().length()>0) {
			_usersLock.release();
			throw new  DataProcessException("Trying to register anonymous user with a non-empty name "+u.getName());
		} 
			
		_anonymousUsersBySessionId.put(u.getHttpSessionId(),u);
		
		_usersLock.release();
	}
	
	@Override
	public void loadFromDb() throws DataProcessException {
		try {
			_usersLock.acquire();
			List<IUserProfileData> loadedUsers=new ArrayList<>();
			Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface().getPopulateUserProfileFromDbStmt(loadedUsers).execute();
			for (IUserProfileData u : loadedUsers) {
				if (!_usersByName.containsKey(u.getName())) {
					_usersByName.put(u.getName(), u);
				}
			}
			_usersLock.release();
		} catch (Exception e) {
			log.error("Unable to load contents of UserProfileData from DB "+e.getMessage());
			_usersLock.release();
			throw new DataProcessException(e); 
		}
	}

	@Override
	public List<IUserProfileData> getUsersList() throws DataProcessException {
		try {
			_usersLock.acquire();
			List<IUserProfileData> users=new ArrayList<>(_usersByName.values());
			_usersLock.release();
			return users;
		} catch (Exception e) {
			log.error("Unable to load contents of UserProfileData from DB "+e.getMessage());
			_usersLock.release();
			throw new DataProcessException(e); 
		}
	}
	
}
