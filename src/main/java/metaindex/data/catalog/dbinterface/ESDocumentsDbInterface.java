package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESDatabaseInterface;
import toolbox.database.elasticsearch.ESReadStmt;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPair;

import java.util.Date;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

public class ESDocumentsDbInterface extends ESDatabaseInterface<IDbItem> 
{
	
	public ESDocumentsDbInterface(ESDataSource ds) { 
		super(ds); 
	}
	// -- load documents by query 
	public ESReadStmt<DbSearchResult> getLoadFromDbStmt(ICatalog c, Integer fromIdx, Integer size,
														String query,
														List<String> filter, 
														List< IPair<String,SORTING_ORDER> > sort) 
					throws DataProcessException {
		return new GetItemsFromDbStmt(c,fromIdx,size,query,filter,sort,getDatasource());
	}
	
	// -- update document field value
	public ESWriteStmt<IDbItem> getUpdateFieldValueIntoDbStmt(IUserProfileData u, ICatalog c, 
															  String docId, String fieldName, Object fieldValue, 
															  Date timestamp) throws DataProcessException 
	{
		return new UpdateFieldValueIntoDbStmt(u,c,docId,fieldName,fieldValue,timestamp,getDatasource());
	}
	
	// -- create new documents
	public ESBulkProcess getNewItemsBulkProcessor(IUserProfileData u,
												  ICatalog c, 
												  String name, 
												  Integer expectedNbActions,
												  Date timestamp) throws DataProcessException 
	{
		return new ESBulkProcess(u,name,expectedNbActions,c,timestamp,getDatasource());
	}
	
}
