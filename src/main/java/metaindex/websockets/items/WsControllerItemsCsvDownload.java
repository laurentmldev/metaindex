package metaindex.websockets.items;

import java.io.IOException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.commons.globals.Globals;
import metaindex.data.filter.IFilter;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.UserItemCsvParser;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.commons.AMxWSController;
import metaindex.websockets.terms.WsControllerTerm;
import metaindex.websockets.terms.WsMsgCreateTerm_request;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESDownloadCsvProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IProcessingTask;
import toolbox.utils.StrTools;
import toolbox.utils.parsers.ACsvParser;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;
import toolbox.utils.parsers.IListParser.ParseException;

@Controller
public class WsControllerItemsCsvDownload extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsFileUpload.class);
	
	 
	@Autowired
	public WsControllerItemsCsvDownload(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
    @MessageMapping("/download_items_csv_request")
    @SubscribeMapping ("/user/queue/download_items_csv_response")    				    
    public void handleDownloadItemsCsvRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgCsvDownload_request requestMsg) {
    	Date now = new Date();
    	WsMsgCsvDownload_answer answer = new  WsMsgCsvDownload_answer(requestMsg);
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
    		String timestamp = StrTools.Timestamp(new Date());
    		String targetFileBasename=user.getCurrentCatalog().getName()+"-extract_"+timestamp+".csv";
    		String targetFileFsPath=Globals.Get().getWebappsTmpFsPath()+targetFileBasename;
    		
    		
	    	ESDownloadCsvProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewCsvExtractProcessor(
		    			user, 
		    			user.getCurrentCatalog(),
		    			 "Extract from "+user.getCurrentCatalog().getName(), 
		    			 targetFileFsPath,
		    			 requestMsg.getTermNamesList(),
		    			 new Long(requestMsg.getSize()),
		    			 new Long(requestMsg.getFromIdx()),
		    			 requestMsg.getQuery(),
		    			 preFilters,
		    			 sortByFieldName,
		    			 now);
    		// set-up the download and start the processing task
	    	user.addProcessingTask(procTask);
    		procTask.start();
    		// wait for end of processing
    		procTask.stop();
    		//String csvFileUrl="http://metaindex.fr/metaindex/downloads/xyzazertutj2454RHHF433a";
    		
    		Boolean success = procTask.isCsvDataGenerated();
			answer.setIsSuccess(success);
			String targetFileUri=Globals.Get().getWebappsTmpUrl()+targetFileBasename;
			answer.setCsvFileUrl(targetFileUri);
			answer.setCsvFileName(targetFileBasename);
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_csv_response", 
    				answer);
        	
	    } catch (DataProcessException | MessagingException | IOException e) 
		{
			log.error("Unable to process download_items_csv_file from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
			answer.setIsSuccess(false);
			this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_csv_response", 
    				answer);
		}   
    }
    
}
