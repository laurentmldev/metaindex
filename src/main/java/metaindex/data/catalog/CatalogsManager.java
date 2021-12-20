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

import metaindex.app.Globals;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public class CatalogsManager implements ICatalogsManager {
	
	private Log log = LogFactory.getLog(CatalogsManager.class);
	
	private Map<Integer,ICatalog> _catalogsById = new java.util.concurrent.ConcurrentHashMap<>();
		
	@Override
	public ICatalog getCatalog(Integer catalogId) {
		ICatalog c = _catalogsById.get(catalogId);
		// if catalog not already loaded we load it from DB
		if (c==null) {
			c = new Catalog();
			c.setId(catalogId);
			try {
				Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(c);
				if (c.getName().length()>0 || c.isDbIndexFound()) {
					_catalogsById.put(c.getId(),c);
					return c;
				} else { return null; }
			} catch (DataProcessException e) {
				e.printStackTrace();
				return null;
			}			
		}
		return c;
	}

	private void loadFromDb(ICatalog c) {
		// loading catalogs contents
		// do it one by one rather than grouped, so that we can load as many valid catalogs as we can
		// and identify easily which ones failed
		try {
			// reuse exiting catalog instance if alreay loaded
			if (!_catalogsById.containsKey(c.getId())) { _catalogsById.put(c.getId(),c); }
			else { c=_catalogsById.get(c.getId()); }
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
		
		// catalog services are started only when on first user enter for first time

	}
	
	@Override 
	public void loadFromDb(String catalogName) throws DataProcessException {
		List<ICatalog> loadedCatalogs = new ArrayList<>();						
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(loadedCatalogs).execute();
		for (ICatalog c : loadedCatalogs) { 
			if (c.getName().equals(catalogName)) { 
				loadFromDb(c); 
				break;
			}
		}
	}

	@Override 
	public void loadFromDb(Integer ownUserId) throws DataProcessException {
		List<ICatalog> loadedCatalogs = new ArrayList<>();						
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(loadedCatalogs).execute();
		for (ICatalog c : loadedCatalogs) { 
			if (c.getOwnerId().equals(ownUserId)) { 
				loadFromDb(c); 				
			}
		}
	}
	
	@Override
	public void loadFromDb() throws DataProcessException {
		
		List<ICatalog> loadedCatalogs = new ArrayList<>();						
		Globals.Get().getDatabasesMgr().getCatalogDefDbInterface().getPopulateFromDefDbStmt(loadedCatalogs).execute();
		
		// Loading filters definition		
		Globals.Get().getDatabasesMgr().getFiltersDbInterface().getPopulateFilterFromDbStmt(loadedCatalogs).execute();
		
		for (ICatalog c : loadedCatalogs) { loadFromDb(c); }
	}
	
	@Override
	public List<ICatalog> getCatalogsList() {
		List<ICatalog> rst = new ArrayList<ICatalog>();
		Iterator<Integer> it = _catalogsById.keySet().iterator();
		while (it.hasNext()) { rst.add(_catalogsById.get(it.next())); }
		return rst;
	}
	
	@Override
	public List<ICatalog> getOwnedCatalogsList(Integer ownerUserId) {
		List<ICatalog> rst = new ArrayList<>();		
		// TODO : use java stream/filter ? 
		for (ICatalog c : _catalogsById.values()) { 
			if (c.getOwnerId().equals(ownerUserId)) { rst.add(c); }
		}
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
