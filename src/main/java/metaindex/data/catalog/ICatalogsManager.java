package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;
import java.util.Map;

import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface ICatalogsManager extends ILoadableFromDb {

	ICatalog getCatalog(Integer catalogId);
	ICatalog getCatalog(String catalogName);
	List<ICatalog> getCatalogsList();
	void removeCatalog(Integer catalogId);
}
