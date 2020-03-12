package metaindex.data.commons.globals.guitheme;

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

import metaindex.data.commons.globals.Globals;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public class GuiThemesManager implements IGuiThemesManager {
	
	// used for proper GUI in degraded mode when no DB could be loaded
	public static final String DEFAULT_GUITHEME_SHORTNAME = "silver";
	
	private Map<Integer,IGuiTheme> _guiThemes = new java.util.concurrent.ConcurrentHashMap<>();
	
	@Override
	public void loadFromDb() throws DataProcessException {
		class GuiThemesHandler implements IStreamHandler<IGuiTheme> {
			@Override public void handle(List<IGuiTheme> loadedGuiThemes) {
				Iterator<IGuiTheme> it = loadedGuiThemes.iterator();
				while (it.hasNext()) {
					IGuiTheme curGuiTheme = it.next();
					_guiThemes.put(curGuiTheme.getId(),curGuiTheme);
		}}}
		Globals.Get().getDatabasesMgr().getGuiThemeDbInterface().getLoadFromDbStmt().execute(new GuiThemesHandler());
		
	}
	
	@Override
	public IGuiTheme getGuiTheme(Integer guiThemeId) {
		return _guiThemes.get(guiThemeId);
	}
	@Override
	public Collection<IGuiTheme> getGuiThemes() {
		return _guiThemes.values();
	}
}
