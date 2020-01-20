package metaindex.data.commons.globals.guilanguage;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.Map;

import toolbox.database.ILoadableFromDb;

public interface IGuiLanguagesManager extends ILoadableFromDb {

	public IGuiLanguage getGuiLanguage(Integer guiLanguageId);
	public IGuiLanguage getGuiLanguage(String guiLanguageShortName);
	public Collection<IGuiLanguage> getGuiLanguages();
	
}
