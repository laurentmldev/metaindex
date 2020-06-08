package metaindex.app.control.websockets.catalogs;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import metaindex.app.control.websockets.catalogs.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IProcessingTask;
import toolbox.utils.filetools.FileDescriptor;

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
	    	Long totalSpaceNeededByte = 0L;
	    	for (FileDescriptor desc : requestMsg.getFileDescriptions()) { totalSpaceNeededByte+=desc.getByteSize();}	    	
	    	if (c.getQuotaFtpDiscSpaceBytes()-c.getDiscSpaceUseBytes()<totalSpaceNeededByte) {
	    		answer.setIsSuccess(false);
	    		answer.setRejectMessage(user.getText("Catalogs.quotasExceededDiscSpace",
	    												new Long(c.getQuotaFtpDiscSpaceBytes()/1000000).toString())	    								);
	    		this.messageSender.convertAndSendToUser(
	    				headerAccessor.getUser().getName(),
	    				"/queue/upload_userdata_files_response", 
	    				answer);
	    		return;
	    	}
	    	
	    	// Instantiate a Processing task
	    	HandleFileUploadProcess procTask = new HandleFileUploadProcess(
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
    @MessageMapping("/upload_userdata_file_contents")
    @SubscribeMapping ("/user/queue/upload_userdata_file_contents_progress")
    public void handleUploadUserDataFileContents(SimpMessageHeaderAccessor headerAccessor, WsMsgUserDataFileUploadContents_request requestMsg) {
    	
    	Integer taskId = requestMsg.getProcessingTaskId();
    	
    	WsMsgUserDataFileUploadContents_answer answer = new WsMsgUserDataFileUploadContents_answer(
				requestMsg.getClientFileId(),requestMsg.getProcessingTaskId());
		
	    try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	
	    	HandleFileUploadProcess procTask;
	    	
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
			if (pt instanceof HandleFileUploadProcess) { procTask=(HandleFileUploadProcess)pt;}
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
	    catch (DataProcessException e) 
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
