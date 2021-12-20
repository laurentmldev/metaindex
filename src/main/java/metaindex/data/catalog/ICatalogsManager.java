package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;
import java.util.Map;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface ICatalogsManager extends ILoadableFromDb {

	ICatalog getCatalog(Integer catalogId);
	ICatalog getCatalog(String catalogName);
	List<ICatalog> getCatalogsList();
	List<ICatalog> getOwnedCatalogsList(Integer ownerUserId);	
	void removeCatalog(Integer catalogId);
	void loadFromDb(String catalogName) throws DataProcessException;
	/** (re)load catalogs data owned by given user */
	void loadFromDb(Integer ownUserId) throws DataProcessException;
}
