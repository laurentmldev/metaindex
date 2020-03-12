package metaindex.data.commons.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.commons.database.IMxDbManager;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.sql.SQLDataSource;
import toolbox.exceptions.DataProcessException;

public class MxDbManager implements IMxDbManager {
	
	private SQLDataSource _sqlDatasource;
	private ESDataSource _esDatasource;
	
	private metaindex.data.commons.globals.guitheme.dbinterface.DbInterface _guithemeInterface;
	private metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface _guilanguageInterface;
	private metaindex.data.userprofile.dbinterface.DbInterface _userProfileInterface;
	private metaindex.data.catalog.dbinterface.SQLDbInterface _catalogInterface;
	private metaindex.data.catalog.dbinterface.ESCatalogDbInterface _catalogContentsInterface;
	private metaindex.data.catalog.dbinterface.ESDocumentsDbInterface _documentsInterface;
	private metaindex.data.filter.dbinterface.DbInterface _filtersInterface; 
	private metaindex.data.term.dbinterface.DbInterface _termsInterface;
	private metaindex.data.perspective.dbinterface.DbInterface _perspectivesInterface;
	private metaindex.data.catalog.dbinterface.VocabularySQLDbInterface _catalogVocInterface;
	
	public MxDbManager(SQLDataSource sqlDs, ESDataSource esDs) {
		_sqlDatasource=sqlDs;
		_esDatasource=esDs;
		_guithemeInterface = new metaindex.data.commons.globals.guitheme.dbinterface.DbInterface(_sqlDatasource);
		_guilanguageInterface = new metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface(_sqlDatasource);
		
		_userProfileInterface = new metaindex.data.userprofile.dbinterface.DbInterface(_sqlDatasource);
		
		_catalogInterface = new metaindex.data.catalog.dbinterface.SQLDbInterface(_sqlDatasource);
		_catalogContentsInterface = new metaindex.data.catalog.dbinterface.ESCatalogDbInterface(_esDatasource);
		_documentsInterface = new metaindex.data.catalog.dbinterface.ESDocumentsDbInterface(_esDatasource);
		_catalogVocInterface = new metaindex.data.catalog.dbinterface.VocabularySQLDbInterface(_sqlDatasource);
		
		_filtersInterface = new metaindex.data.filter.dbinterface.DbInterface(_sqlDatasource);
		_termsInterface = new metaindex.data.term.dbinterface.DbInterface(_sqlDatasource,_esDatasource);
		_perspectivesInterface = new metaindex.data.perspective.dbinterface.DbInterface(_sqlDatasource);
		
	}
	@Override
	public metaindex.data.userprofile.dbinterface.DbInterface getUserProfileDbInterface() { 
		return _userProfileInterface; 
	}	
	@Override
	public metaindex.data.commons.globals.guitheme.dbinterface.DbInterface getGuiThemeDbInterface() {
		return _guithemeInterface;
	}
	@Override
	public metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface getGuiLanguageDbInterface() {
		return _guilanguageInterface;
	}
	@Override
	public metaindex.data.catalog.dbinterface.SQLDbInterface getCatalogDefDbInterface() {
		return _catalogInterface;
	}
	@Override
	public metaindex.data.catalog.dbinterface.ESCatalogDbInterface getCatalogContentsDbInterface() {
		return _catalogContentsInterface;
	}
	@Override
	public metaindex.data.catalog.dbinterface.ESDocumentsDbInterface getDocumentsDbInterface() {
		return _documentsInterface;
	}
	@Override
	public metaindex.data.filter.dbinterface.DbInterface getFiltersDbInterface() {
		return _filtersInterface;
	}
	@Override
	public metaindex.data.term.dbinterface.DbInterface getTermsDbInterface() {
		return _termsInterface;
	}
	@Override
	public metaindex.data.perspective.dbinterface.DbInterface getPerspectivesDbInterface() {
		return _perspectivesInterface;
	}
	@Override
	public metaindex.data.catalog.dbinterface.VocabularySQLDbInterface getCatalogVocDbInterface() {
		return _catalogVocInterface;
	}
	
	// ----- helpers about user profile
	public void loadFullUserData(IUserProfileData u) throws DataProcessException {
		// load user data from DB
		this.getUserProfileDbInterface()
				.getPopulateUserProfileFromDbStmt(u)
				.execute();
		
		// load user roles data from DB
		this.getUserProfileDbInterface()
				.getPopulateAccessRightsFromDbStmt(u)
				.execute();
		
		// load user custos
		this.getUserProfileDbInterface()
				.getPopulateCatalogCustomizationFromDbStmt(u)
				.execute();
	}
	

}
