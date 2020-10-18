package metaindex.app.control.websockets.items;

import java.io.IOException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.filter.IFilter;
import metaindex.app.Globals;
import metaindex.app.control.websockets.items.messages.*;
import metaindex.app.periodic.statistics.items.CsvDownloadMxStat;
import metaindex.app.periodic.statistics.items.CsvUploadMxStat;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESDownloadProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AStreamHandler;
import toolbox.utils.BasicPair;
import toolbox.utils.filetools.FileSystemUtils;
import toolbox.utils.IPair;
import toolbox.utils.StrTools;

@Controller
public class WsControllerItemsIds extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsCsvFileUpload.class);
	
	 
	@Autowired
	public WsControllerItemsIds(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
	/**
	 * Build a Lucene Query string from Ids 'ex: "(3 OR 354effege OR adfg34-fff)")  corresponding to given query
	 * This is used to help user building multi layer requests (ex: get all items whose referenced parent has 2 children
	 */
    @MessageMapping("/get_catalog_items_allids_request")
    @SubscribeMapping ("/user/queue/get_catalog_items_allids_response")    				    
    public void handleBuildItemsIdsRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgGetItems_request requestMsg) {
    	Date now = new Date();
    	WsMsgGetItemsIds_answer answer = new  WsMsgGetItemsIds_answer(requestMsg);
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	
	    	// populate filters from selected filters
    		List<String> preFilters = new ArrayList<String>();    
    		for (String filterName : requestMsg.getFiltersNames()) {
    			IFilter c = user.getCurrentCatalog().getFilter(filterName);
    			if (c==null) {
    				answer.setIsSuccess(false);    	    	
    	    		answer.setRejectMessage(user.getText("Items.server.unknownFilterForSearch",
    	    							filterName.toString(),user.getCurrentCatalog().getName()));
    	    		
    	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/items", 
    						getCompressedRawString(answer));
    	    		return;
    			}
    			preFilters.add(c.getQuery());
    		}		
    		
    		// populate sorting order definition
    		SORTING_ORDER sortOrder = SORTING_ORDER.ASC;
    		if (requestMsg.getReverseSortOrder()) { sortOrder = SORTING_ORDER.DESC; }
    		List< IPair<String,SORTING_ORDER> > sortByFieldName = new ArrayList<>();
    		if (requestMsg.getSortByFieldName().length()>0) {
    			sortByFieldName.add(new BasicPair<String,SORTING_ORDER>(requestMsg.getSortByFieldName(),sortOrder));
    		}    
    		
    		List<String> idsList = new ArrayList<String>();
    		
	    	ESDownloadProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewIdsExtractProcessor(
		    			user, 
		    			user.getCurrentCatalog(),
		    			idsList,
		    			 "Extract-Ids", 
		    			 new Long(requestMsg.getSize()),
		    			 requestMsg.getQuery(),
		    			 preFilters,
		    			 sortByFieldName);
    		// set-up the download and start the processing task
	    	user.addProcessingTask(procTask);
    		procTask.start();
    		// wait for end of processing
    		procTask.stop();
    		//String csvFileUrl="http://metaindex.fr/metaindex/downloads/xyzazertutj2454RHHF433a";
    		
    		Boolean success = procTask.isDataGenerated();
			answer.setIsSuccess(success);
			answer.setItemsIds(idsList);
			
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/get_catalog_items_allids_response", 
    				answer);
    		
	    } catch (DataProcessException | MessagingException | IOException e) 
		{
			log.error("Unable to process get_catalog_items_allids_file from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
			answer.setIsSuccess(false);
			this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/get_catalog_items_allids_response", 
    				answer);
		}   
    }
    
}
