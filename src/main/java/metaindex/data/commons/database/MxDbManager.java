package metaindex.data.commons.database;

import metaindex.app.Globals;
import metaindex.data.catalog.dbinterface.KibanaCatalogDbInterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.commons.database.IMxDbManager;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.kibana.KibanaConnector;
import toolbox.database.sql.SQLDataConnector;
import toolbox.exceptions.DataProcessException;

public class MxDbManager implements IMxDbManager {
	
	private SQLDataConnector _sqlConnector;
	private ElasticSearchConnector _esConnector;
	private KibanaConnector _kibanaConnector;
	
	private metaindex.data.commons.globals.guitheme.dbinterface.DbInterface _guithemeInterface;
	private metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface _guilanguageInterface;
	private metaindex.data.userprofile.dbinterface.SqlDbInterface _userProfileSqlInterface;
	private metaindex.data.userprofile.dbinterface.ESDbInterface _userProfileESInterface;
	private metaindex.data.catalog.dbinterface.SQLDbInterface _catalogInterface;
	private metaindex.data.catalog.dbinterface.ESCatalogDbInterface _catalogContentsInterface;
	private metaindex.data.catalog.dbinterface.ESDocumentsDbInterface _documentsInterface;
	private metaindex.data.catalog.dbinterface.KibanaCatalogDbInterface _catalogManagementInterface;
	private metaindex.data.filter.dbinterface.DbInterface _filtersInterface; 
	private metaindex.data.term.dbinterface.DbInterface _termsInterface;
	private metaindex.data.perspective.dbinterface.DbInterface _perspectivesInterface;
	private metaindex.data.catalog.dbinterface.VocabularySQLDbInterface _catalogVocInterface;
	
	public MxDbManager(SQLDataConnector sqlConnector, ElasticSearchConnector esConnector, KibanaConnector kibanaConnector) {
		_sqlConnector=sqlConnector;
		_esConnector=esConnector;
		_kibanaConnector=kibanaConnector;
		
		_guithemeInterface = new metaindex.data.commons.globals.guitheme.dbinterface.DbInterface(_sqlConnector);
		_guilanguageInterface = new metaindex.data.commons.globals.guilanguage.dbinterface.DbInterface(_sqlConnector);
		
		_userProfileSqlInterface = new metaindex.data.userprofile.dbinterface.SqlDbInterface(_sqlConnector);
		_userProfileESInterface = new metaindex.data.userprofile.dbinterface.ESDbInterface(esConnector);
		
		_catalogInterface = new metaindex.data.catalog.dbinterface.SQLDbInterface(_sqlConnector);
		_catalogContentsInterface = new metaindex.data.catalog.dbinterface.ESCatalogDbInterface(_esConnector);
		_documentsInterface = new metaindex.data.catalog.dbinterface.ESDocumentsDbInterface(_esConnector);
		_catalogVocInterface = new metaindex.data.catalog.dbinterface.VocabularySQLDbInterface(_sqlConnector);
		_catalogManagementInterface = new metaindex.data.catalog.dbinterface.KibanaCatalogDbInterface(_kibanaConnector);
		
		_filtersInterface = new metaindex.data.filter.dbinterface.DbInterface(_sqlConnector);
		_termsInterface = new metaindex.data.term.dbinterface.DbInterface(_sqlConnector,_esConnector);
		_perspectivesInterface = new metaindex.data.perspective.dbinterface.DbInterface(_sqlConnector);
		
	}
	@Override
	public metaindex.data.userprofile.dbinterface.SqlDbInterface getUserProfileSqlDbInterface() { 
		return _userProfileSqlInterface; 
	}
	@Override
	public metaindex.data.userprofile.dbinterface.ESDbInterface getUserProfileESDbInterface() { 
		return _userProfileESInterface; 
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
	public KibanaCatalogDbInterface getCatalogManagementDbInterface() {
		return _catalogManagementInterface;
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
	
	

}
