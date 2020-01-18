package metaindex.data.commons.globals.guitheme;

import java.util.Collection;
import java.util.List;

import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface IGuiThemesManager extends ILoadableFromDb {

	void loadFromDb() throws DataProcessException;
	IGuiTheme getGuiTheme(Integer guiThemeId);
	Collection<IGuiTheme> getGuiThemes();
}
