package metaindex.data.commons.globals.guitheme;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.List;

import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface IGuiThemesManager extends ILoadableFromDb {

	void loadFromDb() throws DataProcessException;
	IGuiTheme getGuiTheme(Integer guiThemeId);
	Collection<IGuiTheme> getGuiThemes();
}
