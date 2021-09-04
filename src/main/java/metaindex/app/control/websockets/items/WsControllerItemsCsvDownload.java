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
public class WsControllerItemsCsvDownload extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsCsvFileUpload.class);
	
	 
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
    		
    		
	    	ESDownloadProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewCsvExtractProcessor(
		    			user, 
		    			user.getCurrentCatalog(),
		    			 user.getText("Items.downloadItems.csv.extracting"),
		    			 targetFileFsPath,
		    			 requestMsg.getTermNamesList(),
		    			 new Long(requestMsg.getSize()),
		    			 new Long(requestMsg.getFromIdx()),
		    			 requestMsg.getQuery(),
		    			 preFilters,
		    			 sortByFieldName,
		    			 now);

    		procTask.start();
    		// wait for end of processing
    		procTask.stop();
    		//String csvFileUrl="http://metaindex.fr/metaindex/downloads/xyzazertutj2454RHHF433a";
    		
    		Boolean success = procTask.isDataGenerated();
			answer.setIsSuccess(success);
			String targetFileUri=Globals.Get().getWebAppsTmpUrl()+targetFileBasename;
			answer.setCsvFileUrl(targetFileUri);
			answer.setCsvFileName(targetFileBasename);
			answer.setCsvFileSizeMB(new Double(FileSystemUtils.GetTotalSizeBytes(targetFileFsPath)/1000000.0));
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_csv_response", 
    				answer);
    		Globals.GetStatsMgr().handleStatItem(new CsvDownloadMxStat(user,user.getCurrentCatalog()));
    		log.info("generated CSV file '"+targetFileBasename+"' ("+answer.getCsvFileSizeMB()+"MB)");
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
