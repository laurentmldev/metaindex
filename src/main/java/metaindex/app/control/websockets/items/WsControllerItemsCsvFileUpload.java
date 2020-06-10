package metaindex.app.control.websockets.items;

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

import metaindex.app.Globals;
import metaindex.app.control.websockets.items.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.control.websockets.terms.WsControllerTerm;
import metaindex.app.control.websockets.terms.messages.WsMsgCreateTerm_request;
import metaindex.app.periodic.statistics.items.CsvUploadMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.UserItemCsvParser;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItem;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IProcessingTask;
import toolbox.utils.parsers.ACsvParser;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;
import toolbox.utils.parsers.IListParser.ParseException;

@Controller
public class WsControllerItemsCsvFileUpload extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsCsvFileUpload.class);
	
	@Autowired
	public WsControllerItemsCsvFileUpload(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
    @MessageMapping("/upload_items_csv_request")
    @SubscribeMapping ("/user/queue/upload_items_csv_response")
    public void handleUploadFilterFileRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgCsvFileUpload_request requestMsg) {
    	Date now = new Date();
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	
    		ESBulkProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
    						.getNewItemsBulkProcessor(user, user.getCurrentCatalog(), 
 													user.getText("Items.serverside.createFromCsvTask"), 
													requestMsg.getTotalNbEntries(),now);    		
    		UserItemCsvParser csvParser = new UserItemCsvParser();
    		csvParser.setCsvSeparator(requestMsg.getSeparator());
    		List<IPair<String,PARSING_FIELD_TYPE>> csvParsingType = new ArrayList<IPair<String,PARSING_FIELD_TYPE>>();    		
    		
    		List<String> fieldsNotFound=new ArrayList<String>();
    		WsControllerTerm termsController = new WsControllerTerm(this.messageSender);
    		
    		if (requestMsg.getCsvColsList().size()==0) {
    			String msgStr = user.getText("Items.serverside.uploadItems.emptyCsvColsList");
    			WsMsgCsvFileUpload_answer msg = new WsMsgCsvFileUpload_answer(
						procTask.getId(),requestMsg.getClientFileId());
				msg.setIsSuccess(false);
				msg.setRejectMessage(msgStr);
				
				this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_items_csv_response", 
	    				msg);
				
				return;
    		}
    		
    		// display CSV prepation only if some terms have to be created
			// otherwise it's very fast, and no use to have a progress bar
    		Integer curTermToCreateIndex=0;
    		Integer totalNbTermsToCreate=0;    		
    		Integer csvCheckProcId=AProcessingTask.getNewProcessingTaskId();
    		Boolean someTermsToCreate=false;
    		
    		// detect if some new terms shall be created
    		for (String csvColName : requestMsg.getCsvColsList()) {
    			String termName=requestMsg.getChosenFieldsMapping().get(csvColName);
    			if (termName==null) { continue; }
    			if (termName.startsWith(WsMsgCsvFileUpload_request.CSV_MAPPING_NEWTERM_PREFIX)) {
    				someTermsToCreate=true;
    				totalNbTermsToCreate++;
    				user.sendGuiProgressMessage(csvCheckProcId, 
    	    				user.getText("Items.uploadItems.creatingNewTerms"), new Float(0), true);
    			}    			
    		}
    		
    		// go through required mapping, and check that terms exist or create them
    		for (String csvColName : requestMsg.getCsvColsList()) {
    			
    			String termName=requestMsg.getChosenFieldsMapping().get(csvColName);
    			
    			// '_id' special field or field to be ignored so no need to check if corresponding term exists
    			if (termName==null || termName.equals("_id")) { 
    				// declare it with TEXT as default, but anyway that won't be used later on because field
    				// is not part of the getChosenFieldsMapping, so it will be ignored when processing file contents
    				csvParsingType.add(new BasicPair<String,PARSING_FIELD_TYPE>(csvColName,PARSING_FIELD_TYPE.TEXT));
    				continue; 
    			}    			
    			
    			if (termName.startsWith(WsMsgCsvFileUpload_request.CSV_MAPPING_NEWTERM_PREFIX)) {
    				
    				curTermToCreateIndex++;
    				
    				String newTermName = csvColName.toLowerCase();
    				WsMsgCreateTerm_request newTermRequest = new WsMsgCreateTerm_request();
    				newTermRequest.setCatalogId(user.getCurrentCatalog().getId());
    				newTermRequest.setTermName(newTermName);
    				newTermRequest.setTermDatatype(TERM_DATATYPE.valueOf(termName.replace(WsMsgCsvFileUpload_request.CSV_MAPPING_NEWTERM_PREFIX, "")));    				
    				
    				try {
						termsController.handleCreateTermRequest(headerAccessor, newTermRequest);
					} catch (Exception e) {
						String msgStr = user.getText("Items.serverside.uploadItems.unableToAutomaticallyCreateTerm");
		    			user.sendGuiErrorMessage(msgStr);
						return;
					}
    				
    				user.sendGuiProgressMessage(csvCheckProcId, 
            				user.getText("Items.uploadItems.creatingNewTerms"), new Float(curTermToCreateIndex*100/totalNbTermsToCreate), true);
    				
    				user.sendGuiInfoMessage("Term '"+newTermName+"' created in catalog "+user.getCurrentCatalog().getName());
    				termName=newTermName;
    				requestMsg.getChosenFieldsMapping().put(csvColName, termName);
    			}
    			
    			// else check term already exists
    			ICatalogTerm term =  user.getCurrentCatalog().getTerms().get(termName);
    			// if field not found, send back an error message
    			if (term==null) { 
    					fieldsNotFound.add(termName); 
    					continue;  
    			}    				
    			RAW_DATATYPE dbType = term.getRawDatatype();    			
    			PARSING_FIELD_TYPE parsingType = PARSING_FIELD_TYPE.TEXT;
    			if (dbType==RAW_DATATYPE.Tfloat 
    					|| dbType==RAW_DATATYPE.Tinteger
    					|| dbType==RAW_DATATYPE.Tshort) {
    				parsingType=PARSING_FIELD_TYPE.NUMBER;
    			}
    			csvParsingType.add(new BasicPair<String,PARSING_FIELD_TYPE>(termName,parsingType));
    		}
    		
    		// display CSV prepation only if some terms have to be created
			// otherwise it's very fast, and no use to have a progress bar
			if (someTermsToCreate) {			
				user.sendGuiProgressMessage(csvCheckProcId, 
    				user.getText("Items.uploadItems.creatingNewTerms"), new Float(100.0), false);
			}			
    		
    		// if some fields in CSV file could not be found in catalog, reject the request
    		if (fieldsNotFound.size()>0) {
    			WsMsgCsvFileUpload_answer msg = new WsMsgCsvFileUpload_answer(
						procTask.getId(),requestMsg.getClientFileId());
				msg.setIsSuccess(false);
				String rejectFilesStr = "";
				for (String curFieldName : fieldsNotFound) { rejectFilesStr+=" '"+curFieldName+"'"; }
				String msgParms[]= {user.getCurrentCatalog().getName(),rejectFilesStr};				
				String msgStr = user.getText("Items.serverside.uploadItems.unknownFields",msgParms);
				msg.setRejectMessage(msgStr);
				
				this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_items_csv_response", 
	    				msg);
				return;
    		}
    		
    	
    		// set-up the parser and start the processing task
    		user.addProcessingTask(procTask);
    		csvParser.setCsvColsTypes(csvParsingType);
    		csvParser.setChosenFieldsMapping(requestMsg.getChosenFieldsMapping());
    		procTask.setParser(csvParser);
    		procTask.start();
    		
    		// send back successful answer
    		// now the we'll wait for CSV lines coming via upload_filter_file_contents message
			WsMsgCsvFileUpload_answer msg = new WsMsgCsvFileUpload_answer(
						procTask.getId(),requestMsg.getClientFileId());
			msg.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/upload_items_csv_response", 
    				msg);
        	
			user.setItemsLastChangeDate(now);
			Globals.GetStatsMgr().handleStatItem(new CsvUploadMxStat(user,user.getCurrentCatalog()));
	    } catch (DataProcessException e) 
		{
			log.error("Unable to process upload_filter_file_request from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
		}   
    	
    	
    }
    
    /**
     * Send the processingTaskId back to client when a new task is created
     * @param headerAccessor
     * @param requestMsg
     */
    @MessageMapping("/upload_filter_file_contents")
    @SubscribeMapping ("/user/queue/upload_filter_file_contents_progress")
    public void handleUploadFilterFileContents(SimpMessageHeaderAccessor headerAccessor, WsMsgCsvFileUploadContents_request requestMsg) {
    	
    	Integer taskId = requestMsg.getProcessingTaskId();
		
	    try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	ICatalog c = user.getCurrentCatalog();
	    	ESBulkProcess procTask;
	    	
    		IProcessingTask pt = user.getProcessingTask(taskId);
    		if (pt==null) {
    			log.error("Processing Task '"+requestMsg.getProcessingTaskId()
    					+"' does not match any running procesing task for user '"+user.getName()+"', request ignored.");
				return;
    		}
			if (pt instanceof ESBulkProcess) { procTask=(ESBulkProcess)pt;}
			else {
				log.error("Processing Task '"+pt.getId()+"' is not from proper type, request ignored.");
				return;
			}
			
    		if (procTask.isAllDataReceived()) {
    			log.error("Processing task '"+procTask.getName()+"' already finalized, unable to add more items to process.");
    			return;
    		} 
    		
    		if (procTask.isTerminated()) {
    			log.error("Processing task '"+procTask.getName()+"' not running, unable to add more items to process.");
    			return;
    		}
    		ACsvParser<IDbItem> csvParser = procTask.getParser();
    		if (csvParser==null) {
    			log.error("Processing task '"+procTask.getName()+"' has no parser associated, unable to parse given contents");
    			return;
    		}
    		// ensure previous data is finished to be processed before starting next one
    		// (preserve order of data sent)
    		procTask.lock();
    		List<IDbItem> parsedItemsToIndex = csvParser.parseAll(requestMsg.getCsvLines());  
    		Long nbMaxItemsToAdd=c.getQuotaNbDocs()-c.getNbDocuments();
    		procTask.postDataToIndexOrUpdate(parsedItemsToIndex);
    		
    		procTask.unlock();    		

	    } catch (ParseException e) 
		{
	    	try {
				IUserProfileData user = getUserProfile(headerAccessor);
				IProcessingTask pt = user.getProcessingTask(taskId);
				pt.abort();
				String msg = user.getText("Items.serverside.uploadItems.parseFailed", 
						pt.getName(),
						new Integer(e.getParseErrors().size()).toString());
		
				user.sendGuiErrorMessage(msg,e.getParseErrors());
				
			} catch (DataProcessException e1) {
				e.printStackTrace();				
			}
	    	
			WsMsgCsvFileUploadContents_answer msg = new WsMsgCsvFileUploadContents_answer(requestMsg.getClientFileId());
			this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/upload_filter_file_contents_progress", 
    				msg);
		}   
	    catch (DataProcessException | InterruptedException e) 
		{
	    	try {
				IUserProfileData user = getUserProfile(headerAccessor);
				IProcessingTask pt = user.getProcessingTask(taskId);
				pt.abort();
				user.sendGuiErrorMessage(user.getText("Items.serverside.bulkprocess.failed", pt.getName()+" : "+e.getMessage()));
				
			} catch (DataProcessException e1) {
				e.printStackTrace();
			}
		}   
	    
	    
    }	
	
    
}
