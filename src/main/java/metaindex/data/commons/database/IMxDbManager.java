package metaindex.data.commons.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

public interface IMxDbManager {
	
	
	public metaindex.data.commons.globals.guitheme.dbinterface.DbInterface getGuiThemeDbInterface();
	public metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface getGuiLanguageDbInterface();
	
	public metaindex.data.userprofile.dbinterface.SqlDbInterface getUserProfileSqlDbInterface();
	public metaindex.data.userprofile.dbinterface.ESDbInterface getUserProfileESDbInterface();
	
	public metaindex.data.catalog.dbinterface.SQLDbInterface getCatalogDefDbInterface();
	public metaindex.data.catalog.dbinterface.ESCatalogDbInterface getCatalogContentsDbInterface();
	public metaindex.data.catalog.dbinterface.ESDocumentsDbInterface getDocumentsDbInterface();
	public metaindex.data.catalog.dbinterface.KibanaCatalogDbInterface getCatalogManagementDbInterface();
	
	public metaindex.data.filter.dbinterface.DbInterface getFiltersDbInterface();
	
	public metaindex.data.term.dbinterface.DbInterface getTermsDbInterface();
	public metaindex.data.perspective.dbinterface.DbInterface getPerspectivesDbInterface();
	public metaindex.data.catalog.dbinterface.VocabularySQLDbInterface getCatalogVocDbInterface();

}
