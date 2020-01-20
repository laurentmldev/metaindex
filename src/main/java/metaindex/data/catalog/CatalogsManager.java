package metaindex.data.catalog;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.commons.globals.Globals;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.exceptions.DataProcessException;

public class CatalogsManager implements ICatalogsManager {
	
	private Log log = LogFactory.getLog(CatalogsManager.class);
	
	private Map<Integer,ICatalog> _catalogsById = new java.util.concurrent.ConcurrentHashMap<>();
		
	@Override
	public ICatalog getCatalog(Integer catalogId) {
		return _catalogsById.get(catalogId);
	}
	
	@Override
	public void loadFromDb() throws DataProcessException {
		
		List<ICatalog> loadedCatalogs=Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getLoadFromDbStmt().execute();
		
		// Loading filters definition
		Globals.Get().getDatabasesMgr().getFiltersDbInterface().getLoadFromDbStmt(loadedCatalogs).execute();
		
		for (ICatalog c : loadedCatalogs) {			
			// loading catalogs contents
			// do it one by one rather than grouped, so that we can load as many valid catalogs as we can
			// and identify easily which ones failed
			try {
				_catalogsById.put(c.getId(),c);
				c.loadStatsFromDb();
				c.loadMappingFromDb();
				c.setDbIndexFound(true);
			} catch (Exception e) {
				log.warn("Unable to load ElasticSearch index for catalog "+c.getName());
				c.setDbIndexFound(false);				
			}
			try {
				c.loadVocabulariesFromDb();
				c.loadTermsFromDb();
				c.loadTermsVocabularyFromDb();
				c.loadPerspectivesFromdb();				
			} catch (Exception e) {
				log.error("Error occured while loading definitions of catalog "+c.getName());
				e.printStackTrace();
			}
			try {
				c.startServices();
			} catch (Exception e) {
				log.error("Error occured while services of catalog "+c.getName());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public List<ICatalog> getCatalogsList() {
		List<ICatalog> rst = new ArrayList<ICatalog>();
		Iterator<Integer> it = _catalogsById.keySet().iterator();
		while (it.hasNext()) { rst.add(_catalogsById.get(it.next())); }
		return rst;
	}

	@Override
	public ICatalog getCatalog(String catalogName) {
		return _catalogsById.values().stream()
			.filter(c -> c.getName().equals(catalogName))
			.findFirst()
			.orElse(null);
	}
	
	@Override
	public void removeCatalog(Integer catalogId) {
		ICatalog c = getCatalog(catalogId);
		if (c!=null) { _catalogsById.remove(c.getId()); }
	}
	
}
