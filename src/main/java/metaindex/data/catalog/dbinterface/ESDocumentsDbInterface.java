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
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESDatabaseInterface;
import toolbox.database.elasticsearch.ESDownloadProcess;
import toolbox.database.elasticsearch.ESReadStreamStmt;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AStreamHandler;
import toolbox.utils.IPair;
import toolbox.utils.parsers.CsvDumper;
import toolbox.utils.parsers.GexfDumper;
import toolbox.utils.parsers.GexfGroupByDumper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;

public class ESDocumentsDbInterface extends ESDatabaseInterface<IDbItem> 
{
	
	public ESDocumentsDbInterface(ElasticSearchConnector ds) { 
		super(ds); 
	}
	/**
	 * Retrieve documents by query 
	 * @param c the catalog in which perform operation
	 * @param fromIdx starting the retrieval from given index
	 * @param size total amount of docs to retrieve (ELK limitation to 10000)
	 * @param query the ELK query to perform
	 * @param filter list of queries additionally (OR) combined to build a pool on which to perform main query 
	 * @param sort list of fields to sort from, with associated order (ASC or DESC)
	 * @return
	 * @throws DataProcessException
	 */
	public ESReadStreamStmt<DbSearchResult> getLoadDocsFromDbStmt(ICatalog c, Integer fromIdx, Integer size,
														String query,
														List<String> filter, 
														List< IPair<String,SORTING_ORDER> > sort,
														Integer strFieldsMaxLength) 
					throws DataProcessException {
		return new GetItemsFromDbStmt(c,fromIdx,size,query,filter,sort,strFieldsMaxLength,getDataConnector());
	}
	
	public ESReadStreamStmt<DbSearchResult> getLoadDocsStreamFromDbStmt(ICatalog c, Long fromIdx, Long size,
			String query,
			List<String> filter, 
			List< IPair<String,SORTING_ORDER> > sort,
			Integer strFieldsMaxLength) 
	throws DataProcessException {
	return new GetItemsStreamFromDbStmt(c,fromIdx,size,query,filter,sort,strFieldsMaxLength,getDataConnector());
	}
	
	public ESReadStreamStmt<DbSearchResult> getLoadDocsStreamFromDbStmt(ICatalog c, Long fromIdx, Long size,
			String query,
			List<String> filter, 
			List< IPair<String,SORTING_ORDER> > sort,
			Integer strFieldsMaxLength,
			List<String> srcFieldsList) 
	throws DataProcessException {
	return new GetItemsStreamFromDbStmt(c,fromIdx,size,query,filter,sort,strFieldsMaxLength,srcFieldsList,
			getDataConnector());
	}
	
	// -- update document field value
	public ESWriteStmt<IDbItem> getUpdateFieldValueIntoDbStmt(IUserProfileData u, ICatalog c, 
															  String docId, String fieldName, Object fieldValue, 
															  Date timestamp) throws DataProcessException 
	{
		return new UpdateFieldValueIntoDbStmt(u,c,docId,fieldName,fieldValue,timestamp,getDataConnector());
	}
	
	// -- create new documents
	public ESBulkProcess getNewItemsBulkProcessor(IUserProfileData u,
												  ICatalog c, 
												  String name, 
												  Integer expectedNbActions,
												  Date timestamp) throws DataProcessException 
	{
		return new ESBulkProcess(u,name,expectedNbActions,c,timestamp,getDataConnector());
	}
	
	public class IdsStreamHandler extends AStreamHandler<IDbItem> {

		private List<String> _targetIdsList=null;
		public IdsStreamHandler(IUserProfileData u, String name, Long expectedNbActions,List<String> targetIdsList) throws DataProcessException {
			super(u, name, expectedNbActions, new Date(),"",u.getText("Items.getIdsFilterProgressMsg"));
			_targetIdsList=targetIdsList;
		}

		@Override
		public void beforeFirst() { }

		@Override
		public void handle(IDbItem o) { _targetIdsList.add(o.getId()); }

		@Override
		public void flush() throws IOException {}

		@Override
		public void afterLast() throws IOException { }
		 		
	}
	
	// -- extract Ids from given search as a Lucene search query
	public ESDownloadProcess getNewIdsExtractProcessor(IUserProfileData u,
												  ICatalog c, 
												  List<String> targetList,/*list to be filled by the processor*/
												  String name, 
												  Long expectedNbActions, 
												  String query,
												  List<String> preFilters,
												  List< IPair<String,SORTING_ORDER> > sortingOrder) 
												throws DataProcessException 
	{

		AStreamHandler<IDbItem> streamHandler=new IdsStreamHandler(u, name+":IdsGenerator", expectedNbActions, targetList);		
		
		return new ESDownloadProcess(u,name, "ids-list", streamHandler,-1L,c,0L,query,preFilters,sortingOrder,
					new ArrayList<>()/*if empty list, retrieve only _id*/);
	}
		
	// -- extract CSV from given search
	public ESDownloadProcess getNewCsvExtractProcessor(IUserProfileData u,
												  ICatalog c, 
												  String name, 
												  String targetFileName,
												  List<String> csvColsList,
												  Long maxNbItems,
												  Long fromIndex,
												  String query,
												  List<String> preFilters,
												  List< IPair<String,SORTING_ORDER> > sortingOrder,
												  Date timestamp) throws DataProcessException 
	{

		AStreamHandler<IDbItem> streamHandler=new CsvDumper<IDbItem>(
				u,
				name+":CsvGenerator",
				maxNbItems,
				csvColsList,
				timestamp,
				targetFileName);
		
		
		return new ESDownloadProcess(u,name, targetFileName,streamHandler,maxNbItems,c,fromIndex,query,preFilters,sortingOrder,
				null/*if null, retrieve all fields from docs sources*/);
	}
	
	// -- extract GEXF from given search
	public ESDownloadProcess getNewGexfExtractProcessor(IUserProfileData u,
												  ICatalog c, 
												  String name, 
												  String targetFileName,
												  List<ICatalogTerm> nodesDataTermsList,
												  List<ICatalogTerm> edgesTermsList,
												  Long maxNbItems,
												  Long fromIndex,
												  String query,
												  List<String> preFilters,
												  List< IPair<String,SORTING_ORDER> > sortingOrder,
												  Date timestamp) throws DataProcessException 
	{

		AStreamHandler<IDbItem> streamHandler=new GexfDumper<IDbItem>(
				u,
				name,
				maxNbItems,
				nodesDataTermsList,
				edgesTermsList,
				timestamp,
				targetFileName);
		
		
		return new ESDownloadProcess(u,name, targetFileName,streamHandler,maxNbItems,c,fromIndex,query,preFilters,sortingOrder,
				null/*if null, retrieve all fields from docs sources*/);
	}
	

	// -- extract grouped GEXF from given search
	public ESDownloadProcess getNewGexfGroupByExtractProcessor(IUserProfileData u,
												  ICatalog c, 
												  String name, 
												  String targetFileName,
												  ICatalogTerm groupingTerm,
												  List<ICatalogTerm> edgeTermsList,
												  Long maxNbItems,
												  Long fromIndex,
												  String query,
												  List<String> preFilters,
												  List< IPair<String,SORTING_ORDER> > sortingOrder,
												  Date timestamp) throws DataProcessException 
	{

		AStreamHandler<IDbItem> streamHandler=new GexfGroupByDumper<IDbItem>(
				u,
				name,
				maxNbItems,
				groupingTerm,
				edgeTermsList,
				timestamp,
				targetFileName);
		
		
		return new ESDownloadProcess(u,name, targetFileName,streamHandler,maxNbItems,c,fromIndex,query,preFilters,sortingOrder,
				null/*if null, retrieve all fields from docs sources*/);
	}
}
