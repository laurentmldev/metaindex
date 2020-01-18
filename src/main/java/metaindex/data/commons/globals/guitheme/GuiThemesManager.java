package metaindex.data.commons.globals.guitheme;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import metaindex.data.commons.globals.Globals;
import toolbox.exceptions.DataProcessException;

public class GuiThemesManager implements IGuiThemesManager {
	
	// used for proper GUI in degraded mode when no DB could be loaded
	public static final String DEFAULT_GUITHEME_SHORTNAME = "silver";
	
	private Map<Integer,IGuiTheme> _guiThemes = new java.util.concurrent.ConcurrentHashMap<>();
	
	@Override
	public void loadFromDb() throws DataProcessException {
		List<IGuiTheme> loadedGuiThemes=Globals.Get().getDatabasesMgr().getGuiThemeDbInterface().getLoadFromDbStmt().execute();
		Iterator<IGuiTheme> it = loadedGuiThemes.iterator();
		while (it.hasNext()) {
			IGuiTheme curGuiTheme = it.next();
			_guiThemes.put(curGuiTheme.getId(),curGuiTheme);
		}
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
