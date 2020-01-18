package metaindex.data.commons.globals.guilanguage;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import toolbox.exceptions.DataProcessException;

public class GuiLanguagesManager implements IGuiLanguagesManager {
		
	private Map<Integer,IGuiLanguage> _guiLanguages = new java.util.concurrent.ConcurrentHashMap<>();
	private Log log = LogFactory.getLog(GuiLanguagesManager.class);
	
	@Override
	public void loadFromDb() throws DataProcessException {
		try {
			List<IGuiLanguage> loadedGuiLanguages=Globals.Get().getDatabasesMgr().getGuiLanguageDbInterface().getLoadFromDbStmt().execute();
			Iterator<IGuiLanguage> it = loadedGuiLanguages.iterator();
			while (it.hasNext()) {
				IGuiLanguage curGuiLanguage = it.next();
				_guiLanguages.put(curGuiLanguage.getId(),curGuiLanguage);
			}
		}
		catch(Exception e) {			
			throw new DataProcessException("Unable to load languages definition from database",e);
		}
	}
	
	@Override
	public IGuiLanguage getGuiLanguage(Integer guiLanguageId) {		
		return _guiLanguages.get(guiLanguageId);
	}
	
	@Override
	public Collection<IGuiLanguage> getGuiLanguages() {
		return  _guiLanguages.values();
	}
	
	@Override
	public IGuiLanguage getGuiLanguage(String guiLanguageShortName) {
		return _guiLanguages.values().stream()
				.filter(l -> l.getShortname().equals(guiLanguageShortName))
				.findFirst()
				.orElse(null);
	}
	
}
