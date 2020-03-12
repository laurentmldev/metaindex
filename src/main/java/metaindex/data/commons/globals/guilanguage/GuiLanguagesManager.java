package metaindex.data.commons.globals.guilanguage;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import metaindex.data.commons.globals.Globals;
import metaindex.data.commons.globals.guilanguage.IGuiLanguage;
import metaindex.data.commons.globals.guitheme.IGuiTheme;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public class GuiLanguagesManager implements IGuiLanguagesManager {
		
	private Map<Integer,IGuiLanguage> _guiLanguages = new java.util.concurrent.ConcurrentHashMap<>();
	private Log log = LogFactory.getLog(GuiLanguagesManager.class);
	
	@Override
	public void loadFromDb() throws DataProcessException {
		try {
			class GuiLanguagesHandler implements IStreamHandler<IGuiLanguage> {
				@Override public void handle(List<IGuiLanguage> loadedGuiLanguages) {
					Iterator<IGuiLanguage> it = loadedGuiLanguages.iterator();
					while (it.hasNext()) {
						IGuiLanguage curGuiLanguage = it.next();
						_guiLanguages.put(curGuiLanguage.getId(),curGuiLanguage);
			}}}
			Globals.Get().getDatabasesMgr().getGuiLanguageDbInterface().getLoadFromDbStmt().execute(new GuiLanguagesHandler());			
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
