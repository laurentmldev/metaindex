package metaindex.app.control.websockets.catalogs;



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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.Globals;
import metaindex.app.control.websockets.catalogs.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.control.websockets.items.messages.WsMsgCsvFileUpload_request;
import metaindex.app.periodic.statistics.catalog.WebFileUploadMxStat;
import metaindex.app.periodic.statistics.items.CsvUploadMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItemsProcessor;
import toolbox.database.elasticsearch.ESDataProcessException;
import toolbox.database.sql.SQLDataProcessException;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.IPair;
import toolbox.utils.IProcessingTask;
import toolbox.utils.filetools.FileDescriptor;
import toolbox.utils.parsers.IFieldsListParser.PARSING_FIELD_TYPE;

/**
 * 
 * Handle upload of file (typically an image) from web browser to userdata space of user's current catalog
 * Current implementation uses simple gziped base64 encoded binary data though text websocket
 * 
 * @author laurentml
 *
 */
@Controller
public class WsControllerCatalogUserDataFileUpload extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerCatalogUserDataFileUpload.class);
	
	private static final Long MAX_UPLOAD_SIZE_MBYTES=50L; // 50Mo max, otherwise go via SFTP
	// limit size of CSV files to upload at once, in order to mitigate
	// risks of server lock down
	private static final Integer MAX_CSV_UPLOAD_NB_ELEMENTS = 250000;
	
	@Autowired
	public WsControllerCatalogUserDataFileUpload(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
    @MessageMapping("/upload_userdata_files")
    @SubscribeMapping ("/user/queue/upload_userdata_files_response")                                    
    public void handleUploadUserDataFileRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgUserDataFileUpload_request requestMsg) {
    	
    	WsMsgUserDataFileUpload_answer answer = new WsMsgUserDataFileUpload_answer(
				/*procTask.getId() : will be set later down there*/ 0,
				requestMsg.getRequestId());
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	// check current catalog
	    	ICatalog c = user.getCurrentCatalog();
	    	if (c==null) {
	    		answer.setRejectMessage(user.getText("Items.server.noCatalogCurrentlySelected"));	    			    	
	    		this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_files_response", 
	    				answer);
	    		
	    		return;
	    	}
	    	// check access rights
	    	if (!this.userHasWriteAccess(user,c)) { 
	    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
				this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/upload_userdata_files_response", answer);
				return;         		
	    	}
	    	
	    	// check quota
	    	Long totalSpaceNeededMByte = 0L;
	    	for (FileDescriptor desc : requestMsg.getFileDescriptions()) { totalSpaceNeededMByte+=desc.getByteSize()/1000000;}	    	
	    	if (c.getQuotaDriveMBytes()-c.getDriveUseMBytes()<totalSpaceNeededMByte) {
	    		answer.setIsSuccess(false);
	    		answer.setRejectMessage(user.getText("Catalogs.quotasExceededDriveSpace",
						c.getQuotaDriveMBytes().toString()));
	    		this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_files_response", 
	    				answer);
	    		return;
	    	}
	    	
	    	if (totalSpaceNeededMByte>MAX_UPLOAD_SIZE_MBYTES) {
	    		answer.setIsSuccess(false);
	    		answer.setRejectMessage(user.getText("Catalogs.exceededMaxUploadSize",
						totalSpaceNeededMByte.toString(),
						MAX_UPLOAD_SIZE_MBYTES.toString(),
						Globals.GetMxProperty("mx.host"),
						Globals.GetMxProperty("mx.drive.sftp.port")));

	    		this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_files_response", 
	    				answer);
	    		return;
	    	}
	    	// Instantiate a Processing task
	    	IHandleFileUploadProcess procTask = new HandleFileUploadProcess(
	    									user,
	    									user.getText("Items.uploadItems.uploadingFiles"),
	    									c.getLocalFsFilesPath(),
	    									requestMsg.getFileDescriptions()); 
    				
    		// set-up the parser and start the processing task
    		user.addProcessingTask(procTask);
    		answer.setProcessingTaskId(procTask.getId());
    		procTask.start();
    		
    		// send back successful answer
    		// now the we'll wait for binary chunks of bin files to be uploaded			
			answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/upload_userdata_files_response", 
    				answer);
        				
    		Globals.GetStatsMgr().handleStatItem(new WebFileUploadMxStat(user,c));
	    } catch (DataProcessException e) 
		{
			log.error("Unable to process upload_filter_file_request from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
		}   
    }
    

    @MessageMapping("/upload_items_request")
    @SubscribeMapping ("/user/queue/upload_items_response")
    public void handleUploadItemsRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgCsvFileUpload_request requestMsg) {
    	Date now = new Date();
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	ICatalog c = user.getCurrentCatalog();
	    	
	    	
	    	//log.error("### expecting "+requestMsg.getTotalNbEntries()+" lines");
    		

    		HandleItemsUploadProcess procTask=new HandleItemsUploadProcess(
    					user,  
						user.getText("Items.serverside.createFromCsvTask",
									requestMsg.getTotalNbEntries().toString()),
						requestMsg.getFileDescriptions());
    		
    		WsMsgUserDataFileUpload_answer answer = new WsMsgUserDataFileUpload_answer(
    				procTask.getId(),
    				requestMsg.getRequestId());
    		
	    	// limit size of CSV files to upload at once, in order to mitigate
	    	// risks of server lock down
	    	if (requestMsg.getTotalNbEntries()>MAX_CSV_UPLOAD_NB_ELEMENTS ) {
	    		String msgStr = user.getText("Items.serverside.createFromCsvTask.tooManyEntries",
	    				requestMsg.getTotalNbEntries().toString(),MAX_CSV_UPLOAD_NB_ELEMENTS.toString());
	    		
	    		answer.setIsSuccess(false);
	    		answer.setRejectMessage(msgStr);
				
				this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_items_response", 
	    				answer);
				
				return;
	    	}
	    	
    		List<IPair<String,PARSING_FIELD_TYPE>> fieldsParsingType = new ArrayList<IPair<String,PARSING_FIELD_TYPE>>();    		    		
    		List<String> fieldsNotFound=new ArrayList<String>();
    		    	
    		// display CSV preparation only if some terms have to be created
			// otherwise it's very fast, and no use to have a progress bar
    		Integer curTermToCreateIndex=0;
    		
    		// 
    		List<ICatalogTerm> termsToCreate = new ArrayList<ICatalogTerm>();
    		String newTermsList="";
    		// go through required mapping, and check that terms exist or create them
    		for (String userFileColName : requestMsg.getFieldsMapping().values()) {
    			
    			ICatalogTerm term=null;
    			String termName=requestMsg.getFieldsMapping().get(userFileColName);
    			if (termName.startsWith(WsMsgCsvFileUpload_request.CSV_MAPPING_NEWTERM_PREFIX)) {
    				
    				curTermToCreateIndex++;
    				String newTermName = "";
    				TERM_DATATYPE newTermType=TERM_DATATYPE.UNKNOWN;
    				for (TERM_DATATYPE t : TERM_DATATYPE.values()) {
    					newTermName = termName.replace(
    							WsMsgCsvFileUpload_request.CSV_MAPPING_NEWTERM_PREFIX+"__","");
    					
    					if (newTermName.replace(t.toString(),"").length()<newTermName.length()) {
    						newTermType=t;
    					}
    					newTermName = newTermName.replace(t.toString(),"").toLowerCase();
    				}
    				term = c.getTerms().get(newTermName);
    				if (term!=null) {   
    					String msgStr = user.getText("Items.serverside.uploadItems.emptyCsvColsList");
    					answer.setIsSuccess(false);
    					answer.setRejectMessage(msgStr);
    					
    					this.messageSender.convertAndSendToUser(
    		    				headerAccessor.getUser().getName(),
    		    				"/queue/upload_items_response", 
    		    				answer);
    					
    		    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_term.already_exists"));
    		    		return;
    		    	}
    				term = ICatalogTerm.BuildCatalogTerm(newTermType);
    		    	term.setCatalogId(user.getCurrentCatalog().getId());
    		    	term.setName(newTermName);   
    		    	
    				termsToCreate.add(term);
    				termName=newTermName;
    				newTermsList+=" "+newTermName;
    				// update the term to match with CSV col name (it was a special string before
    				// saying "create a new term"
    				requestMsg.getFieldsMapping().put(userFileColName, newTermName);
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
    			fieldsParsingType.add(new BasicPair<String,PARSING_FIELD_TYPE>(termName,parsingType));
    			    			
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
    	        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/upload_items_response", answer);	
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
	        		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/upload_items_response", answer);    	    		
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
    			answer.setIsSuccess(false);
				String rejectFilesStr = "";
				for (String curFieldName : fieldsNotFound) { rejectFilesStr+=" '"+curFieldName+"'"; }
				String msgParms[]= {user.getCurrentCatalog().getName(),rejectFilesStr};				
				String msgStr = user.getText("Items.serverside.uploadItems.unknownFields",msgParms);
				answer.setRejectMessage(msgStr);
				
				this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_items_response", 
	    				answer);
				return;
    		}
    		
    	
    		// set-up the parser and start the processing task
    		user.addProcessingTask(procTask);
    		
    		IDbItemsProcessor dbItemsProcessor = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewItemsBulkProcessor(user, user.getCurrentCatalog(), 
												user.getText("Items.serverside.createFromCsvTask",requestMsg.getTotalNbEntries().toString()),
											requestMsg.getTotalNbEntries(),now);    
	
    		procTask.init(dbItemsProcessor,fieldsParsingType,requestMsg.getFieldsMapping());
    		procTask.start();
    		
    		// send back successful answer
    		// now we'll wait for CSV lines coming via upload_filter_file_contents message
    		answer.setIsSuccess(true);
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/upload_items_response", 
    				answer);
        	
			user.setItemsLastChangeDate(now);
			Globals.GetStatsMgr().handleStatItem(new CsvUploadMxStat(user,user.getCurrentCatalog()));
	    } catch (DataProcessException e) 
		{
			log.error("Unable to process upload_filter_file_request from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
		}   
    	
    	
    }
    
	
    /**
     * Send the processingTaskId back to _client when a new task is created
     * @param headerAccessor
     * @param requestMsg
     */
    @MessageMapping("/upload_userdata_file_contents")
    @SubscribeMapping ("/user/queue/upload_userdata_file_contents_progress")
    public void handleUploadUserDataFileContents(SimpMessageHeaderAccessor headerAccessor, 
    											 WsMsgUserDataFileUploadContents_request requestMsg) {
    	
    	Integer taskId = requestMsg.getProcessingTaskId();
    	
    	WsMsgUserDataFileUploadContents_answer answer = new WsMsgUserDataFileUploadContents_answer(
				requestMsg.getClientFileId(),requestMsg.getProcessingTaskId());
		
	    try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	
	    	IHandleFileUploadProcess procTask;
	    	
    		IProcessingTask pt = user.getProcessingTask(taskId);
    		if (pt==null) {
    			log.error("Processing Task '"+requestMsg.getProcessingTaskId()
    					+"' does not match any running processing task for user '"+user.getName()+"', request ignored.");
    			answer.setIsSuccess(false);
    			this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_file_contents_progress", 
	    				answer);
				return;
    		}
			if (pt instanceof IHandleFileUploadProcess) { procTask=(IHandleFileUploadProcess)pt;}
			else {
				log.error("Processing Task '"+pt.getId()+"' is not from proper type, request ignored.");
				answer.setIsSuccess(false);
    			this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_file_contents_progress", 
	    				answer);				
				return;
			}
			
    		if (procTask.isAllDataReceived()) {
    			log.error("Processing task '"+procTask.getName()+"' already finalized, unable to add more items to process.");
    			answer.setIsSuccess(false);
    			this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_file_contents_progress", 
	    				answer);				
    			return;
    		} 
    		
    		if (procTask.isTerminated()) {
    			log.error("Processing task '"+procTask.getName()+"' not running, unable to add more items to process.");
    			answer.setIsSuccess(false);
    			this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_file_contents_progress", 
	    				answer);				
    			return;
    		}
    		
    		procTask.postFileData(	requestMsg.getClientFileId(),
    								requestMsg.getSequenceNumber() ,
    								requestMsg.getRawContents());
    		    		
	    }   
	    catch (DataProcessException|InterruptedException e) 
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
