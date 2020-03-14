package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESDatabaseInterface;
import toolbox.database.elasticsearch.ESPopulateStmt;
import toolbox.database.elasticsearch.ESReadStreamStmt;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.database.sql.SQLPopulateStmt;
import toolbox.exceptions.DataProcessException;

import java.util.ArrayList;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.term.dbinterface.LoadMappingFromDbStmt;

public class ESCatalogDbInterface  extends ESDatabaseInterface<ICatalog> 
{
	
	public ESCatalogDbInterface(ESDataSource ds) { 
		super(ds); 
	}
	
	// --- load index mapping
	public ESPopulateStmt<ICatalog> getLoadMappingFromDocsDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadMappingFromDbStmt(data, getDatasource());
	}
	public ESPopulateStmt<ICatalog> getLoadMappingFromDocsDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getLoadMappingFromDocsDbStmt(list);
	}
	
	// --- load index stats	
	public ESPopulateStmt<ICatalog> getLoadStatsFromDocsDbStmt(List<ICatalog> data) throws DataProcessException {
		return new PopulateStatsFromDbStmt(data, getDatasource());
	}
	public ESPopulateStmt<ICatalog> getLoadStatsFromDocsDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getLoadStatsFromDocsDbStmt(list);
	}
	
	// --- create index
	public ESWriteStmt<ICatalog> getCreateIndexIntoDocsDbStmt(List<ICatalog> data) throws DataProcessException {
		return new CreateIndexIntoEsDbStmt(data, getDatasource());
	}
	public ESWriteStmt<ICatalog> getCreateIndexIntoDocsDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getCreateIndexIntoDocsDbStmt(list);
	}
	
	// --- delete catalog
	public ESWriteStmt<ICatalog> getDeleteFromDocsDbStmt(IUserProfileData activeUser,List<ICatalog> data) throws DataProcessException {
		return new DeleteIndexFromESDbStmt(activeUser,data, getDatasource());
	}
	public ESWriteStmt<ICatalog> getDeleteFromDocsDbStmt(IUserProfileData activeUser,ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getDeleteFromDocsDbStmt(activeUser,list);
	}

}
