package metaindex.app.control.websockets.items;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.filter.IFilter;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.app.Globals;
import metaindex.app.control.websockets.items.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.control.websockets.users.WsControllerUser.CATALOG_MODIF_TYPE;
import metaindex.app.periodic.statistics.items.CreateItemMxStat;
import metaindex.app.periodic.statistics.items.DeleteItemsByQueryMxStat;
import metaindex.app.periodic.statistics.items.DeleteItemsMxStat;
import metaindex.app.periodic.statistics.items.UpdateFieldValueItemMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.UserItemContents;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IStreamHandler;
import toolbox.utils.StreamHandler;

@Controller
public class WsControllerItem extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItem.class);
	private final Integer MAX_ELASTIC_SEARCH_RESULT_SIZE=10000; 
	
	@Autowired
	public WsControllerItem(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
		
	private IDbItem getDocumentContents(IUserProfileData user,String documentId) {
		
		List<String> preFilters = new ArrayList<String>();
		List< IPair<String,SORTING_ORDER> > sortByFieldName = new ArrayList<IPair<String,SORTING_ORDER>>();
		List<DbSearchResult> results = new ArrayList<>();
		try {
			
			Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getLoadDocsFromDbStmt(user.getCurrentCatalog(),
					    				0, // offset
					    				1, // nb docs
					    				"_id:"+documentId,
					    				preFilters,							    				
					    				sortByFieldName).execute(new StreamHandler<DbSearchResult>(results));
			
			if (results.size()!=1 || results.get(0).getItems().size()!=1) { 
				return null; 
			}
			IDbItem item = results.get(0).getItems().get(0);
			
			return item;
			
		} catch (DataProcessException e) {
			return null;
		}
		
		
	}
    @MessageMapping("/get_catalog_items")
    @SubscribeMapping ( "/user/queue/items")
    public void handleItemsRequest(SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgGetItems_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	
    	WsMsgGetItems_answer answer = new WsMsgGetItems_answer(requestMsg);
    	
    	// refresh stats to have fresh total number of items
    	// seems safer to do it here too (in addition to 'when add/delete is ended'
    	if (user.getCurrentCatalog()==null) { 
    		answer.setSize(-1);
    		answer.setRejectMessage(user.getText("Items.server.noCatalogCurrentlySelected"));
    		
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", 
    				getCompressedRawString(answer));
    		return;
    	}
    	
    	if (!this.userHasReadAccess(user,user.getCurrentCatalog())) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", answer);
			return;         		
    	}
    	user.getCurrentCatalog().loadStatsFromDb();
    	
    	try {
    		
    		// populate filters from selected filters
    		List<String> preFilters = new ArrayList<String>();    
    		for (String filterName : requestMsg.getFiltersNames()) {
    			IFilter c = user.getCurrentCatalog().getFilter(filterName);
    			if (c==null) {
    				answer.setIsSuccess(false);
    	    		answer.setSize(-1);
    	    		answer.setRejectMessage(user.getText("Items.server.unknownFilterForSearch",
    	    							filterName.toString(),user.getCurrentCatalog().getName()));
    	    		
    	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", 
    						getCompressedRawString(answer));
    	    		return;
    			}
    			preFilters.add(c.getQuery());
    		}		
    		
    		// popoulate sorting order definition
    		SORTING_ORDER sortOrder = SORTING_ORDER.ASC;
    		if (requestMsg.getReverseSortOrder()) { sortOrder = SORTING_ORDER.DESC; }
    		List< IPair<String,SORTING_ORDER> > sortByFieldName = new ArrayList<>();
    		if (requestMsg.getSortByFieldName().length()>0) {
    			sortByFieldName.add(new BasicPair<String,SORTING_ORDER>(requestMsg.getSortByFieldName(),sortOrder));
    		}    		
			
			// actually perform search query
			List<DbSearchResult> results = new ArrayList<>();
			
			Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
							.getLoadDocsFromDbStmt(user.getCurrentCatalog(),
							    				requestMsg.getFromIdx(), 
							    				requestMsg.getSize(),
							    				requestMsg.getQuery(),
							    				preFilters,							    				
							    				sortByFieldName).execute(new StreamHandler<DbSearchResult>(results));
    		
    		answer.setIsSuccess(true);
    		// seems to return null value when 0 hits
    		answer.setTotalHits(results.get(0).getTotalHits());
    		answer.setTotalItems(user.getCurrentCatalog().getNbDocuments());
    		if (answer.getTotalHits()==null) { answer.setTotalHits(0L); }
    		// such request shall return only one response
    		assert(results.size()==1);    		
    		answer.setItems(results.get(0).getItems());
    		
    		
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
    												"/queue/items", 
    												getCompressedRawString(answer));
    		
    		
    	} catch (DataProcessException e) 
    	{
    		answer.setIsSuccess(false);
    		answer.setSize(-1);
    		answer.setRejectMessage(user.getText("Items.server.DbErrorOccured",e.getMessage()));
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", 
					getCompressedRawString(answer));    
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.get_catalog_items.db_error"));
    	}  catch (Exception e) 
    	{
    		answer.setIsSuccess(false);
    		answer.setSize(-1);
    		answer.setRejectMessage(user.getText("Items.server.ErrorOccured",e.getMessage()));
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", 
					getCompressedRawString(answer));
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.get_catalog_items.server_error"));
    	}
    	
    }
    
    @MessageMapping("/update_field_value")
    @SubscribeMapping ( "/user/queue/field_value")
    public void handleUpdateFieldValueRequest(SimpMessageHeaderAccessor headerAccessor, 
    		WsMsgUpdateFieldValue_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	
		ICatalog c = user.getCurrentCatalog();		
    	WsMsgUpdateFieldValue_answer answer = new WsMsgUpdateFieldValue_answer(requestMsg);
    	
    	if (c==null) {
    		answer.setRejectMessage(user.getText("Items.server.noCatalogCurrentlySelected"));
    			    	
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/field_value", 
    				answer);
    		
    		return;
    	}
    	
    	if (!this.userHasWriteAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/field_value", answer);
			return;         		
    	}
    	
    	try {
    		c.acquireLock();
    		Date now = new Date();
    		
    		// use proper object mapping
    		// in case some datatype needs a JSON object rather than a basic not a string
    		String termValueStr = requestMsg.getFieldValue().trim(); 
    		Object termValue=termValueStr;
    		ICatalogTerm termDef = c.getTerms().get(requestMsg.getFieldName());
    		if (termDef==null) {
    			answer.setRejectMessage(user.getText("Items.serverside.uploadItems.unknownFields",c.getName(),requestMsg.getFieldName()));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/field_value", answer);
    			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_field_value.unknown_field"));
    			c.releaseLock();
    			return;
    		}
    		
    		Boolean isSuccess = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
    						.getUpdateFieldValueIntoDbStmt(
    								user,
    								user.getCurrentCatalog(),
    								requestMsg.getItemId().toString(),
    								termDef.getRawFieldName(),
    								termValue,
    								now).execute();
							    		    		
    		if (!isSuccess) {
    			answer.setIsSuccess(false);
        		String errMsg=user.getText("Items.server.unableToUpdateItem",
        								requestMsg.getItemId(),
        								requestMsg.getFieldName(),
        								requestMsg.getFieldValue(),"");
        		
        		answer.setRejectMessage(errMsg);
        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
        												"/queue/field_value", 
    													getRawString(answer));
        		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_field_value.refused_by_elk"));
        		c.releaseLock();
        		return;
    		}
    		
    		user.setItemsLastChangeDate(now);
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
    												"/queue/field_value", 
													getRawString(answer)); 	
    		
    		// Notify all users that a document has been modified. 
    		// retrieve corresponding item in order to forward a descent doc name to the users
    		// instead of simply the doc id
    		String itemName=requestMsg.getItemId();
    		IDbItem item = getDocumentContents(user,requestMsg.getItemId());
    		if (item!=null) { itemName=item.getName(); }
    		user.notifyCatalogContentsChanged(CATALOG_MODIF_TYPE.FIELD_VALUE,
    						itemName,
    						requestMsg.getFieldName()+"=\""+requestMsg.getFieldValue()+"\"" );
    	
    		Globals.GetStatsMgr().handleStatItem(new UpdateFieldValueItemMxStat(user,c,item.getId()));
    		c.releaseLock();    		
    		
    	} catch (Exception e) 
    	{
    		answer.setIsSuccess(false);
    		String errMsg=user.getText("Items.server.unableToUpdateItem",
    								requestMsg.getItemId(),
    								requestMsg.getFieldName(),
    								requestMsg.getFieldValue(),
    								e.getMessage());
    		
    		answer.setRejectMessage(errMsg);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
    												"/queue/field_value", 
													getRawString(answer));
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_field_value.server_error"));
    		c.releaseLock();
    	}
    	
    }
    

    @MessageMapping("/delete_items")
    public void handleDeleteItemseRequest(SimpMessageHeaderAccessor headerAccessor, 
    		WsMsgDeleteItems_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
		Date now = new Date();
    	ESBulkProcess procTask = (ESBulkProcess) Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
						.getNewItemsBulkProcessor(user, c, 
													user.getText("Items.serverside.delete",
																 new Integer(requestMsg.getItemsIds().size()).toString()), 
												requestMsg.getItemsIds().size(),now);
    	user.addProcessingTask(procTask);
    	
    	procTask.start();
    	
    	List<IDbItem> itemsToDelete = new ArrayList<IDbItem>();  
    	for (String itemId : requestMsg.getItemsIds()) {
    		UserItemContents item = new UserItemContents();
    		item.setId(itemId);
    		itemsToDelete.add(item);
    	}
		procTask.postDataToDelete(itemsToDelete);
		Globals.GetStatsMgr().handleStatItem(new DeleteItemsMxStat(user,c,requestMsg.getItemsIds().size()));
    }
    
	
    @MessageMapping("/delete_items_by_query")
    public void handleDeleteItemsByQueryRequest(SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgDeleteItemsByQuery_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	 
    	ICatalog c = user.getCurrentCatalog();
    	Date now = new Date();
    	
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	
    	try {
    		// 1- first retrieve list of ids matching given query and filters
    		List<String> preFilters = new ArrayList<String>();
    
    		for (String filterName : requestMsg.getFiltersNames()) {
    			IFilter f = user.getCurrentCatalog().getFilter(filterName);
    			if (f==null) {    	    		
    	    		user.sendGuiErrorMessage(user.getText("Items.server.unknownFilter"));
    	    		return;
    			}
    			preFilters.add(f.getQuery());
    		}		
    		Integer fromIdx=0;
    		Integer size=MAX_ELASTIC_SEARCH_RESULT_SIZE;
    		List<IDbItem> itemsToDelete=new ArrayList<IDbItem>();
    		Integer lastResultsize=MAX_ELASTIC_SEARCH_RESULT_SIZE;
    		while (lastResultsize==MAX_ELASTIC_SEARCH_RESULT_SIZE) {
    			List<DbSearchResult> results = new ArrayList<>(); 
    			Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
							.getLoadDocsFromDbStmt(user.getCurrentCatalog(),
									fromIdx, 
									size,
				    				requestMsg.getQuery(),
				    				preFilters,
				    				new ArrayList< IPair<String,SORTING_ORDER> >()).execute(new StreamHandler<DbSearchResult>(results));
    		
    			// only one query --> only one result
    			assert(results.size()==1);
    			DbSearchResult result = results.get(0);
    			lastResultsize=result.getTotalHits().intValue();
    			fromIdx+=lastResultsize;    			
    			itemsToDelete.addAll(result.getItems());
    			
    		}
    		
    		// 2- delete items
    		ESBulkProcess procTask = (ESBulkProcess) Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewItemsBulkProcessor(user, user.getCurrentCatalog(), 
												user.getText("Items.serverside.delete",
															 new Integer(itemsToDelete.size()).toString()), 
															 itemsToDelete.size(),now);
			user.addProcessingTask(procTask);			
			procTask.start();			
			procTask.postDataToDelete(itemsToDelete);
			Globals.GetStatsMgr().handleStatItem(new DeleteItemsByQueryMxStat(user,c,itemsToDelete.size()));
    		
    	} catch (DataProcessException e) 
    	{
    		e.printStackTrace();
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_items_by_query.processing_error"));
    	}  catch (Exception e) 
    	{
    		e.printStackTrace();
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_items_by_query.server_error"));
    	}
    	
    }
    
    
    @MessageMapping("/create_item")
    @SubscribeMapping ( "/user/queue/created_item")
    public void handleCreatItemRequest(SimpMessageHeaderAccessor headerAccessor, 
    		WsMsgCreateItem_request requestMsg) throws Exception {
    	
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	WsMsgCreateItem_answer answer = new WsMsgCreateItem_answer(requestMsg);
    	Date now = new Date();
		ICatalog c = user.getCurrentCatalog();
    	if (c==null || !c.getId().equals(requestMsg.getCatalogId())) {
    		// return failure notif (default status of answer is 'failed')
    		answer.setRejectMessage("Current user catalog does not match request");
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_item", answer);
    		return;
    	}
    	if (!this.userHasWriteAccess(user,c)) { 
    		user.sendGuiErrorMessage(user.getText("globals.noAccessRights"));			
			return;         		
    	}
    	
    	if (!c.checkQuotasNbDocsOk()) {
    		user.sendGuiErrorMessage(user.getText("Catalogs.quotasExceededNbDocuments",c.getQuotaNbDocs().toString()) );			
			return;
    	}
    	
    	ESBulkProcess procTask = 
    			Globals.Get().getDatabasesMgr().getDocumentsDbInterface().getNewItemsBulkProcessor(user, c, 
    																					user.getText("Items.creatingItem"), 1, now);    	
    	
    	try {
	    	    
	    	c.acquireLock();
	    	UserItemContents itemToCreate = new UserItemContents();
	    	
	    	// check fields exist in current catalog
	    	String undefinedKeysStr = "";
	    	List<String> emptyFieldsToRemove = new ArrayList<>();
	    	for (String curFieldName : requestMsg.getFieldsMap().keySet()) {
	    		Boolean exists = false;
	    		for (String existingFieldName : c.getTerms().keySet()) {
	    			if (curFieldName.equals(existingFieldName)) { exists=true;break; }
	    		}
	    		if (!exists) {
	    			if (undefinedKeysStr.length()>0) { undefinedKeysStr+=", "; }
	    			undefinedKeysStr+=curFieldName;
	    		}
	    		// checking for empty string, useless to add when creating new document
	    		else {	    			
					Object val=requestMsg.getFieldsMap().get(curFieldName);
					String valStr = val.toString();
					if (valStr.length()==0) { 
						emptyFieldsToRemove.add(curFieldName); 
					}				
	    		}
	    	}
	    	if (undefinedKeysStr.length()>0) {
	    		answer.setRejectMessage("Unable to create item, following fields are not defined in catalog : "+undefinedKeysStr);
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_item", answer);
	    		c.releaseLock();
	    		return;
	    	}
	    	// removing fields with empty value
	    	for (String fieldNameToRemove : emptyFieldsToRemove) {
	    		requestMsg.getFieldsMap().remove(fieldNameToRemove);
	    	}
	    	
	    	itemToCreate.setData(requestMsg.getFieldsMap());
	    	procTask.start();
	    	List<IDbItem> itemsList = new ArrayList<>();
	    	itemsList.add(itemToCreate);
	    	procTask.postDataToIndexOrUpdate(itemsList);
	    	
	    	Globals.GetStatsMgr().handleStatItem(new CreateItemMxStat(user,c,itemToCreate.getName()));
	    	
	    	// blocking while data not added yet
	    	while (!procTask.isTerminated() && procTask.isRunning()) { Thread.sleep(200); }	    	
	    	
	    	user.setItemsLastChangeDate(now);
	    	// a message will be sent by the processing task, no need to
	    	// do it explicitly here
    		c.releaseLock();	    	
		} 
		catch (DataProcessException | InterruptedException e) { 	    	
			procTask.abort();
			answer.setRejectMessage("Unable to create item : "+e.getMessage());
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_item", answer);
			e.printStackTrace();
			Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_item"));
    		c.releaseLock(); 			
 		}   
    		
    	
    }
    

    
}
