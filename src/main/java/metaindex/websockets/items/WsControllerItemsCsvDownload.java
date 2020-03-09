package metaindex.websockets.items;

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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.data.commons.globals.Globals;
import metaindex.data.catalog.UserItemCsvParser;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.websockets.commons.AMxWSController;
import metaindex.websockets.terms.WsControllerTerm;
import metaindex.websockets.terms.WsMsgCreateTerm_request;
import toolbox.database.IDbItem;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IProcessingTask;
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
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	
    		// set-up the download and start the processing task
	    	/*
    		user.addProcessingTask(procTask);
    		procTask.setParser(csvParser);
    		procTask.start();
	    	 */
    		String csvFileUrl="TODO";
 
			WsMsgCsvDownload_answer msg = new WsMsgCsvDownload_answer(csvFileUrl);
			msg.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/upload_items_csv_response", 
    				msg);
        	
			
	    } catch (DataProcessException e) 
		{
			log.error("Unable to process download_items_csv_file from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
		}   
    }
    
}
