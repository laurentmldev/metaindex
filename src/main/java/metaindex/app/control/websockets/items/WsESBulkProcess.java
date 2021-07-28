package metaindex.app.control.websockets.items;

import java.io.IOException;
import java.util.ArrayList;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;

import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbItem;
import toolbox.database.elasticsearch.ESBulkProcess;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.AProcessingTask;
import toolbox.utils.parsers.IListParser.ParseException;

public class WsESBulkProcess extends ESBulkProcess {

	public WsESBulkProcess(IUserProfileData u, String name, Integer expectedNbActions, ICatalog c, Date timestamp,
			ElasticSearchConnector ds) throws DataProcessException {
		super(u, name, expectedNbActions, c, timestamp, ds);
		
	}


    

	private List<String> decodeCompressedCsvLineStr(String totalZippedCsvData) throws IOException {
		
		List<String> rst = new ArrayList<String>();
		String jsonArrayStr=AMxWSController.getUncompressedRawString(totalZippedCsvData);
		JSONArray parsedStr = new JSONArray(jsonArrayStr);
 		List<Object> csvLinesObj = parsedStr.toList();
 		for (Object o : csvLinesObj) {
 			rst.add(o.toString()); 			
 		}
 		return rst;
	}

	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private Map<Integer,String[]>  _pendingMessagesById = new HashMap<>();
	private Integer _nextMsgIdToDecode=0;
	
	private void uncompressAndPostPendingMessages() 
						throws IOException, ParseException, DataProcessException {
		
		
    	List<Integer> processedMsgIds = new ArrayList<>();
    	//log.error("###	- decoding phase from msg "+_nextMsgIdToDecode);
    	// process all messages fully received, in proper order
		while (_pendingMessagesById.get(_nextMsgIdToDecode)!=null) {
			
			
			String[] msgChunks = _pendingMessagesById.get(_nextMsgIdToDecode);
			
			// ensure all chunks could be received			
			List<String> csvRows;
			String fullCompressedBase64Str="";
			for (String curChunk:msgChunks) { 
				if (curChunk==null) { return; }
				fullCompressedBase64Str+=curChunk; 
			}
			
			//log.error("### uncompressing msg "+_nextMsgIdToDecode+" (pending msgs:"+_pendingMessagesById.size());
			
			csvRows=decodeCompressedCsvLineStr(fullCompressedBase64Str);
			List<IDbItem> parsedItemsToIndex = getParser().parseAll(csvRows);
			
			postDataToIndexOrUpdate(parsedItemsToIndex);
			
			//log.error("###	- msg "+_nextMsgIdToDecode+" --> "+parsedItemsToIndex.size());
		
			processedMsgIds.add(_nextMsgIdToDecode);
			_nextMsgIdToDecode++;			
			
		}		
		
		// removing already processed msgs from the list
		for (Integer msgId : processedMsgIds) {
			_pendingMessagesById.remove(msgId);
		}
	
	}
	

	@Override
	public void run()  { 
		
		getActiveUser().sendGuiProgressMessage(
    			getId(),
    			getActiveUser().getText("Items.serverside.bulkprocess.progress", getName()),
    			AProcessingTask.pourcentage(getProcessedNbData(),getTargetNbData()));
		
		
		
		while (!this.isAllDataProcessed() && !isInterruptFlagActive()) {
			try { 
				this.lock();
				uncompressAndPostPendingMessages();
				this.unlock();
				
				// wait for having received all data to be processed 
				Thread.sleep(200);
				
			} 
			catch (InterruptedException e) { 
				e.printStackTrace(); 
			} 
			catch (ParseException e) 
			{
		    	
		    	
	    		log.error("Aborting CSV upload operation due to parsing errors in user's input data");
	    		
				this.abort();
				String msg = getActiveUser().getText("Items.serverside.uploadItems.parseFailed", 
						this.getName(),
						new Integer(e.getParseErrors().size()).toString());
		
				getActiveUser().sendGuiErrorMessage(msg,e.getParseErrors());
				
			
		    	
		    	/* tmp lml
				WsMsgCsvFileUploadContents_answer msg = new WsMsgCsvFileUploadContents_answer(requestMsg.getClientFileId());
				this.messageSender.convertAndSendToUser(
						getActiveUser().getName(),
	    				"/queue/upload_filter_file_contents_progress", 
	    				msg);
	    				*/
			}  
		    catch (Throwable e) 
			{
		    	log.error("Aborting CSV upload operation due to server error");
	    		e.printStackTrace();
	    			    		    		
				this.abort();
				getActiveUser().sendGuiErrorMessage(getActiveUser().getText("Items.serverside.bulkprocess.failed",
													this.getName()+" : "+e.getMessage()));
			
			} 
		}

		//log.error("### stopping");
		stop();

	}

	
	/**
	 * Add compressed chunk data into internal table and free inbound thread. Processing itself is done by dedicated thread
	 * so that users requests are not locked when many messages arrive and block all the threads.
	 * @param compressedCsvLineStr
	 * @param msgNb
	 * @param curChunkNb
	 * @param msgTotalNbChunks
	 * @throws InterruptedException 
	 */
	public void postWsMsgChunk(String compressedCsvLineStr,Integer msgNb,Integer curChunkNb, Integer msgTotalNbChunks)
			throws InterruptedException {
		
		this.lock();
		if (_pendingMessagesById.get(msgNb-1)==null) {
			_pendingMessagesById.put(msgNb-1,new String[msgTotalNbChunks]);
		}

		String[] curMsgChunksList= _pendingMessagesById.get(msgNb-1);
		curMsgChunksList[curChunkNb-1]=compressedCsvLineStr;
		
		//log.error("### added pending msg "+msgNb+"  (pending msgs:"+_pendingMessagesById.size());
		this.unlock();
	}

};
