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
	public ESReadStmt<ICatalog> getLoadMappingFromDocsDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadMappingFromDbStmt(data, getDatasource());
	}
	public ESReadStmt<ICatalog> getLoadMappingFromDocsDbStmt(ICatalog data) throws DataProcessException {
		List<ICatalog> list = new ArrayList<>();
		list.add(data);
		return getLoadMappingFromDocsDbStmt(list);
	}
	
	// --- load index stats	
	public ESReadStmt<ICatalog> getLoadStatsFromDocsDbStmt(List<ICatalog> data) throws DataProcessException {
		return new LoadStatsFromDbStmt(data, getDatasource());
	}
	public ESReadStmt<ICatalog> getLoadStatsFromDocsDbStmt(ICatalog data) throws DataProcessException {
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
