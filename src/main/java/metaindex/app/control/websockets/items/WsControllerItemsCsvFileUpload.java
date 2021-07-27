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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.items.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
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
import toolbox.database.elasticsearch.ESDataProcessException;
import toolbox.database.sql.SQLDataProcessException;
import toolbox.exceptions.DataProcessException;

import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IProcessingTask;
import toolbox.utils.parsers.ACsvParser;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;
import toolbox.utils.parsers.IListParser.ParseException;

@Controller
public class WsControllerItemsCsvFileUpload extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsCsvFileUpload.class);
	
	// map[UserId][processingTaskId][msgId]=chunksList[]
	private static Map<Integer, Map<Integer, Map<Integer,String[]> > > _pendingMessagesMapUser = 
										new java.util.concurrent.ConcurrentHashMap<>();  
	
	// map[UserId][processingTaskId]=next MsgId to decode
	private static Map<Integer, Map<Integer, Integer> > _nextMsgIdToDecodeByUser = 
										new java.util.concurrent.ConcurrentHashMap<>();  
	
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
	    	ICatalog c = user.getCurrentCatalog();
	    	
	    	//log.error("### expecting "+requestMsg.getTotalNbEntries()+" lines");
    		ESBulkProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
    						.getNewItemsBulkProcessor(user, user.getCurrentCatalog(), 
 													user.getText("Items.serverside.createFromCsvTask"), 
													requestMsg.getTotalNbEntries(),now);    		
    		UserItemCsvParser csvParser = new UserItemCsvParser();
    		csvParser.setCsvSeparator(requestMsg.getSeparator());
    		List<IPair<String,PARSING_FIELD_TYPE>> csvParsingType = new ArrayList<IPair<String,PARSING_FIELD_TYPE>>();    		
    		
    		List<String> fieldsNotFound=new ArrayList<String>();
    		
    		WsMsgCsvFileUpload_answer answer = new WsMsgCsvFileUpload_answer(
					procTask.getId(),requestMsg.getClientFileId());
    		
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
    		
    		// display CSV preparation only if some terms have to be created
			// otherwise it's very fast, and no use to have a progress bar
    		Integer curTermToCreateIndex=0;
    		
    		// 
    		List<ICatalogTerm> termsToCreate = new ArrayList<ICatalogTerm>();
    		String newTermsList="";
    		// go through required mapping, and check that terms exist or create them
    		for (String csvColName : requestMsg.getCsvColsList()) {
    			
    			String termName=requestMsg.getChosenFieldsMapping().get(csvColName);
    			ICatalogTerm term=null;
    					
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
    				term = c.getTerms().get(newTermName);
    				if (term!=null) {   
    					String msgStr = user.getText("Items.serverside.uploadItems.emptyCsvColsList");
    	    			WsMsgCsvFileUpload_answer msg = new WsMsgCsvFileUpload_answer(
    							procTask.getId(),requestMsg.getClientFileId());
    					msg.setIsSuccess(false);
    					msg.setRejectMessage(msgStr);
    					
    					this.messageSender.convertAndSendToUser(
    		    				headerAccessor.getUser().getName(),
    		    				"/queue/upload_items_csv_response", 
    		    				msg);
    					
    		    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.already_exists"));
    		    		return;
    		    	}
    				term = ICatalogTerm.BuildCatalogTerm(TERM_DATATYPE.valueOf(termName.replace(WsMsgCsvFileUpload_request.CSV_MAPPING_NEWTERM_PREFIX, "")));
    		    	term.setCatalogId(user.getCurrentCatalog().getId());
    		    	term.setName(newTermName);   
    		    	
    				termsToCreate.add(term);
    				termName=newTermName;
    				newTermsList+=" "+newTermName;
    				// update the term to match with CSV col name (it was a special string before
    				// saying "create a new term"
    				requestMsg.getChosenFieldsMapping().put(csvColName, newTermName);
    			}
    			
    			// else check term already exists
    			else {
	    			
	    			term =  user.getCurrentCatalog().getTerms().get(termName);
	    			// if field not found, send back an error message
	    			if (term==null) { 
	    					fieldsNotFound.add(termName); 
	    					continue;  
	    			}
    			}
    			
    			RAW_DATATYPE dbType = term.getRawDatatype();    			
    			PARSING_FIELD_TYPE parsingType = PARSING_FIELD_TYPE.TEXT;
    			if (dbType==RAW_DATATYPE.Tfloat 
    					|| dbType==RAW_DATATYPE.Tinteger
    					|| dbType==RAW_DATATYPE.Tshort) {
    				parsingType=PARSING_FIELD_TYPE.NUMBER;
    			}
    			csvParsingType.add(new BasicPair<String,PARSING_FIELD_TYPE>(csvColName,parsingType));
    			    			
    		}
    		
    		// creating missing terms
    		if (termsToCreate.size()>0) {
    			user.sendGuiInfoMessage(user.getText("Items.uploadItems.creatingNewTerms")+":"+newTermsList);
    			try {    				
    				
    	    		Boolean result = Globals.Get().getDatabasesMgr().getTermsDbInterface()
    	    					.createIntoDbStmt(c,termsToCreate).execute();
    	    		
    	    		if (result==false) {
    	    			answer.setIsSuccess(false);    	    			
    	        		answer.setRejectMessage(user.getText("Items.serverside.uploadItems.unableToAutomaticallyCreateTerm"));
    	        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/upload_items_csv_response", answer);	
    	        		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.refused_by_server"));
    	        		return;
    	        	}
    	    		    	    		
    	    		
    	    		try {
    			    	c.acquireLock();
    			    	c.clearTerms();
    			    	c.loadMappingFromDb();
    			    	c.loadTermsFromDb();
    			    	c.releaseLock();
    			    	
    		    		// Refresh Kibana index-pattern so that new term is available for statistics
    		        	Boolean rst = Globals.Get().getDatabasesMgr().getCatalogManagementDbInterface().refreshStatisticsIndexPattern(user, c);
    		        	if (rst==false) {
    		        		answer.setRejectMessage(user.getText("Items.serverside.uploadItems.unableToImportItems"));
    		        		log.error("Unable to update index-pattern in Kibana for current catalog '"+c.getName()+"'");    		        		
    		        		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.kibana"));
    		        	}

    		    	} catch (Throwable t) {
    		    		c.releaseLock();
    		    		String msgStr = user.getText("Items.serverside.uploadItems.unableToAutomaticallyCreateTerm");
    	    			user.sendGuiErrorMessage(msgStr);				
    					return;	    		
    		    	}
    	    		
    	    	} catch (ESDataProcessException e) {
    	    		e.printStackTrace();
    	    		answer.setIsSuccess(false);
    	    		log.error("unable to create new terms '"+newTermsList+"' : "+e.getMessage());
    	    		e.printStackTrace();
	        		answer.setRejectMessage(user.getText("Items.serverside.uploadItems.unableToAutomaticallyCreateTerm"));
	        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/upload_items_csv_response", answer);    	    		
    	    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.elasticsearch"));
    	    		return;
    	    	}
    	    	catch (SQLDataProcessException e) {
    	    		// most probably term description already exist
    	    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.sql"));
    	    		// no return here, we still want to refresh contents (field could still be added into ES db)
    	    	}
    		}
    	
    		
    		// display CSV preparation only if some terms have to be created
			// otherwise it's very fast, and no use to have a progress bar
			
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
    		log.error("### adding proc task "+procTask.getId());
    		user.addProcessingTask(procTask);
    		csvParser.setCsvColsTypes(csvParsingType);
    		csvParser.setChosenFieldsMapping(requestMsg.getChosenFieldsMapping());
    		procTask.setParser(csvParser);
    		procTask.start();
    		
    		// send back successful answer
    		// now we'll wait for CSV lines coming via upload_filter_file_contents message
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
    
    

	public static List<String> decodeCompressedCsvLineStr(String totalZippedCsvData) throws IOException {
		
		List<String> rst = new ArrayList<String>();
		String jsonArrayStr=AMxWSController.getUncompressedRawString(totalZippedCsvData);
		JSONArray parsedStr = new JSONArray(jsonArrayStr);
 		List<Object> csvLinesObj = parsedStr.toList();
 		for (Object o : csvLinesObj) {
 			rst.add(o.toString()); 			
 		}
 		return rst;
	}

	
	private void processReceivedMessages(Integer userId, Integer processingTaskId, 
											ACsvParser<IDbItem> csvParser,ESBulkProcess procTask) 
						throws IOException, ParseException, DataProcessException {
		
		
		
		// retrieve data structures
		Map<Integer,Integer> nextMsgToDecodeByProcId = _nextMsgIdToDecodeByUser.get(userId);
    	Integer nextMsgIdToDecoce = nextMsgToDecodeByProcId.get(processingTaskId);
    	if (nextMsgIdToDecoce==null) {
    		nextMsgToDecodeByProcId.put(processingTaskId,1);
    		nextMsgIdToDecoce=1;
    	}
    	
    	Map<Integer,Map<Integer,String[]>> pendingMessagesMapProcId = _pendingMessagesMapUser.get(userId);
    	Map<Integer,String[]> pendingMessagesById = 
				pendingMessagesMapProcId.get(processingTaskId);
    	
    	
    	List<Integer> processedMsgIds = new ArrayList<>();
    	//log.error("###	- decoding phase from msg "+nextMsgIdToDecoce);
    	// process all messages fully received, in proper order
		while (pendingMessagesById.get(nextMsgIdToDecoce)!=null) {
			
			//log.error("###		- decoding msg "+nextMsgIdToDecoce);
			
			String[] msgChunks = pendingMessagesById.get(nextMsgIdToDecoce);
			
			// ensure all chunks could be received			
			List<String> csvRows;
			String fullCompressedBase64Str="";
			for (String curChunk:msgChunks) { 
				if (curChunk==null) { return; }
				fullCompressedBase64Str+=curChunk; 
			}
			
			csvRows=decodeCompressedCsvLineStr(fullCompressedBase64Str);
			List<IDbItem> parsedItemsToIndex = csvParser.parseAll(csvRows);
			
			procTask.postDataToIndexOrUpdate(parsedItemsToIndex);
			
			//log.error("###	- msg "+nextMsgIdToDecoce+" --> "+parsedItemsToIndex.size());
		
			processedMsgIds.add(nextMsgIdToDecoce);
			nextMsgIdToDecoce++;
			nextMsgToDecodeByProcId.put(processingTaskId,nextMsgIdToDecoce);
			
		}		
		
		// removing already processed msgs from the list
		for (Integer msgId : processedMsgIds) {
			pendingMessagesById.remove(msgId);
		}
	
	}
    /**
     * Send the processingTaskId back to _client when a new task is created
     * @param headerAccessor
     * @param requestMsg
     */
    @MessageMapping("/upload_filter_file_contents")
    @SubscribeMapping ("/user/queue/upload_filter_file_contents_progress")
    public void handleUploadFilterFileContents(SimpMessageHeaderAccessor headerAccessor, 
    										WsMsgCsvFileUploadContents_request requestMsg) {
    	
    	
    	Integer taskId = requestMsg.getProcessingTaskId();
    	    	
	    try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	ESBulkProcess procTask;
	    	
    		IProcessingTask pt = user.getProcessingTask(taskId);
    		if (pt==null) {
    			// ignore unknown processing task (it may have been aborted due to a parsing error in previous contents typically)
    			return;
    		}
    		
    		
			if (pt instanceof ESBulkProcess) { procTask=(ESBulkProcess)pt;}
			else {
				log.error("Processing Task '"+pt.getId()+"' is not from proper type, request ignored.");
				return;
			}
						
			// ensure previous data is finished to be processed before starting next one
    		// (preserve order of data sent)
    		procTask.lock();
    		
	    	// initialize pending requests table for current user
	    	if (!_pendingMessagesMapUser.containsKey(user.getId())) {
	    		_pendingMessagesMapUser.put(user.getId(),new java.util.concurrent.ConcurrentHashMap<>());
	    		_nextMsgIdToDecodeByUser.put(user.getId(),new java.util.concurrent.ConcurrentHashMap<>());
	    		
	    	}
	    	Map<Integer,Map<Integer,String[]>> pendingMessagesMapProcId = _pendingMessagesMapUser.get(user.getId());
	    	
	    	//Thread.sleep(500);
    		    		
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
    		
			// starting a new one
			if (!pendingMessagesMapProcId.containsKey(taskId)) {
				pendingMessagesMapProcId.put(requestMsg.getProcessingTaskId(),new java.util.concurrent.ConcurrentHashMap<>());
			}	
			
			Map<Integer,String[]> pendingMessagesById = 
								pendingMessagesMapProcId.get(requestMsg.getProcessingTaskId());
			
			
			if (pendingMessagesById.get(requestMsg.getMsgNb())==null) {
				pendingMessagesById.put(requestMsg.getMsgNb(),new String[requestMsg.getTotalNbChunks()]);
			}
			String[] curMsgChunksList= pendingMessagesById.get(requestMsg.getMsgNb());
			curMsgChunksList[requestMsg.getCurChunkNb()-1]=requestMsg.getCompressedCsvLineStr();
			//log.error("### adding chunk "+requestMsg.getCurChunkNb()+": "+requestMsg.getCompressedCsvLineStr());
			//procTask.unlock();
			//procTask.lock();
			
			processReceivedMessages(user.getId(), requestMsg.getProcessingTaskId(),csvParser,procTask);
			procTask.unlock();

	    } 
	    catch (ParseException e) 
		{
	    	
	    	try {
	    		log.error("Aborting CSV upload operation due to parsing errors in user's input data");
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
	    catch (Throwable e) 
		{
	    	log.error("Aborting CSV upload operation due to server error");
    		e.printStackTrace();
    		
	    	try {	    		
				IUserProfileData user = getUserProfile(headerAccessor);
				IProcessingTask pt = user.getProcessingTask(taskId);
				pt.abort();
				user.sendGuiErrorMessage(user.getText("Items.serverside.bulkprocess.failed", pt.getName()+" : "+e.getMessage()));
				
			} catch (DataProcessException e1) {
				e1.printStackTrace();
			}
		} 
	    
    }	
	
    
}
