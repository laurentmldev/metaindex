package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESDatabaseInterface;
import toolbox.database.elasticsearch.ESReadStmt;
import toolbox.database.elasticsearch.ESWriteStmt;
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
	public ESReadStmt<ICatalog> getLoadMappingFromDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadMappingFromDbStmt(data, getDatasource());
	}
	public ESReadStmt<ICatalog> getLoadMappingFromDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getLoadMappingFromDbStmt(list);
	}
	
	// --- load index stats	
	public ESReadStmt<ICatalog> getLoadStatsFromDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadStatsFromDbStmt(data, getDatasource());
	}
	public ESReadStmt<ICatalog> getLoadStatsFromDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getLoadStatsFromDbStmt(list);
	}
	
	// --- create index
	public ESWriteStmt<ICatalog> getCreateIndexIntoDbStmt(List<ICatalog> data) throws DataProcessException {
		return new CreateIndexIntoEsDbStmt(data, getDatasource());
	}
	public ESWriteStmt<ICatalog> getCreateIndexIntoDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getCreateIndexIntoDbStmt(list);
	}
	
	// --- delete catalog
	public ESWriteStmt<ICatalog> getDeleteFromDbStmt(IUserProfileData activeUser,List<ICatalog> data) throws DataProcessException {
		return new DeleteIndexFromESDbStmt(activeUser,data, getDatasource());
	}
	public ESWriteStmt<ICatalog> getDeleteFromDbStmt(IUserProfileData activeUser,ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getDeleteFromDbStmt(activeUser,list);
	}

}
