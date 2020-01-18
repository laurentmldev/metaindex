package metaindex.data.catalog;

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
