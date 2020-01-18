package metaindex.data.commons.globals.guilanguage;

import java.util.Collection;
import java.util.Map;

import toolbox.database.ILoadableFromDb;

public interface IGuiLanguagesManager extends ILoadableFromDb {

	public IGuiLanguage getGuiLanguage(Integer guiLanguageId);
	public IGuiLanguage getGuiLanguage(String guiLanguageShortName);
	public Collection<IGuiLanguage> getGuiLanguages();
	
}
