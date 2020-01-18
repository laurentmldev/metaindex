package metaindex.websockets.items;

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
import metaindex.data.commons.globals.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.UserItemContents;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.commons.AMxWSController;
import metaindex.websockets.users.WsControllerUser.COMMUNITY_MODIF_TYPE;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;

@Controller
public class WsControllerItem extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItem.class);
	private final Integer MAX_ELASTIC_SEARCH_RESULT_SIZE=10000; 
	
	@Autowired
	public WsControllerItem(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
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
    		List< IPair<String,SORTING_ORDER> > sortByFieldName = new ArrayList<IPair<String,SORTING_ORDER>>();
    		if (requestMsg.getSortByFieldName().length()>0) {
    			sortByFieldName.add(new BasicPair<String,SORTING_ORDER>(requestMsg.getSortByFieldName(),sortOrder));
    		}    		
			
			// actually perform search query
			List<DbSearchResult> results = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
							.getLoadFromDbStmt(user.getCurrentCatalog(),
							    				requestMsg.getFromIdx(), 
							    				requestMsg.getSize(),
							    				requestMsg.getQuery(),
							    				preFilters,							    				
							    				sortByFieldName).execute();
    		
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
    	}  catch (Exception e) 
    	{
    		answer.setIsSuccess(false);
    		answer.setSize(-1);
    		answer.setRejectMessage(user.getText("Items.server.ErrorOccured",e.getMessage()));
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", 
					getCompressedRawString(answer));    		    		
    	}
    	
    }
    
    @MessageMapping("/update_field_value")
    @SubscribeMapping ( "/user/queue/field_value")
    public void handleUpdateFieldValueRequest(SimpMessageHeaderAccessor headerAccessor, 
    		WsMsgUpdateFieldValue_request requestMsg) throws Exception {

    	IUserProfileData user = getUserProfile(headerAccessor);	
		ICatalog c = user.getCurrentCatalog();		
    	WsMsgUpdateFieldValue_answer answer = new WsMsgUpdateFieldValue_answer(requestMsg);

    	if (!this.userHasWriteAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/field_value", answer);
			return;         		
    	}
    	
    	try {
    		c.acquireLock();
    		Date now = new Date();
    		
    		// use proper object mapping
    		// typically 'relation' datatypes needs a JSON object, not a string
    		String termValueStr = requestMsg.getFieldValue().trim(); 
    		Object termValue=termValueStr;
    		ICatalogTerm termDef = c.getTerms().get(requestMsg.getFieldName());
    		if (termDef==null) {
    			answer.setRejectMessage(user.getText("Items.serverside.uploadItems.unknownFields",c.getName(),requestMsg.getFieldName()));
    			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/field_value", answer);
    			return;
    		}
    		
    		if (termDef.getDatatype().equals(TERM_DATATYPE.RELATION)) {
    			if (termValueStr.length()==0) {
    				termValueStr="{ \"name\" = \"\" }";
    			}
    			termValue = new JSONObject(termValueStr.toString());    			
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
        		c.releaseLock();
        		return;
    		}
    		
    		user.setItemsLastChangeDate(now);
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
    												"/queue/field_value", 
													getRawString(answer)); 		
    		user.notifyCatalogContentsChanged(COMMUNITY_MODIF_TYPE.FIELD_VALUE, 1);
    		
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
    			List<DbSearchResult> results = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
							.getLoadFromDbStmt(user.getCurrentCatalog(),
									fromIdx, 
									size,
				    				requestMsg.getQuery(),
				    				preFilters,
				    				new ArrayList< IPair<String,SORTING_ORDER> >()).execute();
    		
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
    		
    	} catch (DataProcessException e) 
    	{
    		e.printStackTrace();
    	}  catch (Exception e) 
    	{
    		e.printStackTrace();
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
    	
    	ESBulkProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface().getNewItemsBulkProcessor(user, c, "Creating Item", 1, now);    	
    	
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
    		c.releaseLock(); 			
 		}   
    		
    	
    }
    

    
}
