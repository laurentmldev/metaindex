package metaindex.data.commons.database;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

public interface IMxDbManager {
	
	public metaindex.data.commons.globals.guitheme.dbinterface.DbInterface getGuiThemeDbInterface();
	public metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface getGuiLanguageDbInterface();
	
	public metaindex.data.userprofile.dbinterface.DbInterface getUserProfileDbInterface();
	
	public metaindex.data.catalog.dbinterface.SQLDbInterface getCatalogDefDbInterface();
	public metaindex.data.catalog.dbinterface.ESCatalogDbInterface getCatalogContentsDbInterface();
	public metaindex.data.catalog.dbinterface.ESDocumentsDbInterface getDocumentsDbInterface();
	
	public metaindex.data.filter.dbinterface.DbInterface getFiltersDbInterface();
	
	public metaindex.data.term.dbinterface.DbInterface getTermsDbInterface();
	public metaindex.data.perspective.dbinterface.DbInterface getPerspectivesDbInterface();
	public metaindex.data.catalog.dbinterface.VocabularySQLDbInterface getCatalogVocDbInterface();

	// helpers
	/// load user data and associated roles
	public void loadFullUserData(IUserProfileData u) throws DataProcessException;
}
