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

import metaindex.data.catalog.ICatalog;
import metaindex.data.filter.IFilter;
import metaindex.data.term.ICatalogTerm;
import metaindex.app.Globals;
import metaindex.app.control.websockets.items.messages.*;
import metaindex.app.periodic.statistics.items.GexfDownloadMxStat;
import metaindex.app.periodic.statistics.items.GraphDownloadMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.database.elasticsearch.ESDownloadProcess;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.BasicPair;
import toolbox.utils.filetools.FileSystemUtils;
import toolbox.utils.IPair;
import toolbox.utils.StrTools;

@Controller
public class WsControllerItemsGraphDownload extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerItemsGraphDownload.class);
	
	 
	@Autowired
	public WsControllerItemsGraphDownload(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
	
    @MessageMapping("/download_items_graph_request")
    @SubscribeMapping ("/user/queue/download_items_graph_response")    				    
    public void handleDownloadItemsGraphRequest( SimpMessageHeaderAccessor headerAccessor, 
    											WsMsgGraphDownload_request requestMsg) {
    	Date now = new Date();
    	WsMsgGraphDownload_answer answer = new  WsMsgGraphDownload_answer(requestMsg);
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	ICatalog curCatalog = user.getCurrentCatalog();
	    	
	    	// populate filters from selected filters
    		List<String> preFilters = new ArrayList<String>();    
    		for (String filterName : requestMsg.getFiltersNames()) {
    			IFilter c = user.getCurrentCatalog().getFilter(filterName);
    			if (c==null) {
    				answer.setIsSuccess(false);    	    	
    	    		answer.setRejectMessage(user.getText("Items.server.unknownFilterForSearch",
    	    							filterName.toString(),user.getCurrentCatalog().getName()));
    	    		
    	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
    	    												"/queue/download_items_graph_response", 
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
    		String targetFileBasename=user.getCurrentCatalog().getName()+"-"+timestamp+".gexf";
    		String targetFileFsPath=Globals.Get().getWebappsTmpFsPath()+targetFileBasename;
    		
    		List<ICatalogTerm> nodesDataTermsList = new ArrayList<>();
    		for (Integer termId : requestMsg.getNodesDataTermIdsList()) {
    			for (ICatalogTerm t : curCatalog.getTerms().values())
    				if (t.getId().equals(termId)) {
    					nodesDataTermsList.add(t);
    				}    			
    		}
    		
    		List<ICatalogTerm> edgesTermsList = new ArrayList<>();
    		for (Integer termId : requestMsg.getEdgesTermIdsList()) {
    			for (ICatalogTerm t : curCatalog.getTerms().values())
    				if (t.getId().equals(termId)) {
    					edgesTermsList.add(t);
    				}    			
    		}
    		
	    	ESDownloadProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewGexfExtractProcessor(
		    			user, 
		    			user.getCurrentCatalog(),
		    			 user.getText("Items.downloadItems.gexf.extracting"), 
		    			 targetFileFsPath,
		    			 nodesDataTermsList,
		    			 edgesTermsList,
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
    		//String gexfFileUrl="http://metaindex.fr/metaindex/downloads/xyzazertutj2454RHHF433a";
    		
    		Boolean success = procTask.isDataGenerated();
			answer.setIsSuccess(success);
			String targetFileUri=Globals.Get().getWebAppsTmpUrl()+targetFileBasename;
			answer.setGraphFileUrl(targetFileUri);
			answer.setGraphFileName(targetFileBasename);
			answer.setGraphFileSizeMB(new Double(FileSystemUtils.GetTotalSizeBytes(targetFileFsPath)/1000000.0));
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_graph_response", 
    				answer);
    		Globals.GetStatsMgr().handleStatItem(new GraphDownloadMxStat(user,user.getCurrentCatalog()));
    		
    		log.info("generated GEXF file '"+targetFileBasename+"' ("+answer.getGraphFileSizeMB()+"MB)");
    		Globals.GetStatsMgr().handleStatItem(new GexfDownloadMxStat(user,curCatalog));
	    } catch (DataProcessException | MessagingException | IOException e) 
		{
			log.error("Unable to process download_items_graph_file from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
			answer.setIsSuccess(false);
			this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_graph_response", 
    				answer);
		}   
    }
    
    @MessageMapping("/download_items_graphgroupby_request")
    @SubscribeMapping ("/user/queue/download_items_graphgroupby_response")    				    
    public void handleDownloadItemsGraphGroupByRequest( SimpMessageHeaderAccessor headerAccessor, 
    												WsMsgGraphDownloadGroupBy_request requestMsg) {
    	Date now = new Date();
    	WsMsgGraphDownload_answer answer = new  WsMsgGraphDownload_answer(requestMsg);
    	try {
	    	
	    	IUserProfileData user = getUserProfile(headerAccessor);
	    	ICatalog curCatalog = user.getCurrentCatalog();
	    	
	    	// populate filters from selected filters
    		List<String> preFilters = new ArrayList<String>();    
    		for (String filterName : requestMsg.getFiltersNames()) {
    			IFilter c = user.getCurrentCatalog().getFilter(filterName);
    			if (c==null) {
    				answer.setIsSuccess(false);    	    	
    	    		answer.setRejectMessage(user.getText("Items.server.unknownFilterForSearch",
    	    							filterName.toString(),user.getCurrentCatalog().getName()));
    	    		
    	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/download_items_graphgroupby_response", 
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
    		
    		ICatalogTerm groupingTerm = null;
    		for (ICatalogTerm t : curCatalog.getTerms().values()) {
				if (t.getId().equals(requestMsg.getGroupingTermId())) {
					groupingTerm=t;
				}
    		}
    		if (groupingTerm==null) {
    			answer.setIsSuccess(false);    	    	
	    		answer.setRejectMessage(user.getText("Items.server.noMatchingTermForGrouping"));	    		
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
	    				"/queue/download_items_graphgroupby_response", answer);
	    		return;
    		}
    		List<ICatalogTerm> edgeTermsList = new ArrayList<>();
    		for (ICatalogTerm t : curCatalog.getTerms().values()) {
    			for (Integer termId : requestMsg.getEdgesTermIdsList()) {
    				if (t.getId().equals(termId)) {
    					edgeTermsList.add(t);
    				}
        		}				
    		}
    		if (edgeTermsList.size()!=requestMsg.getEdgesTermIdsList().size()) {
    			answer.setIsSuccess(false);    	    	
	    		answer.setRejectMessage(user.getText("Items.server.noMatchingTermForGroupEdges"));	    		
	    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),
	    				"/queue/download_items_graphgroupby_response", answer);
	    		return;
    		}
    		
    		String timestamp = StrTools.Timestamp(new Date());
    		String targetFileBasename=user.getCurrentCatalog().getName()+"-groupby-"+groupingTerm.getName()+"-"+timestamp+".gexf";
    		String targetFileFsPath=Globals.Get().getWebappsTmpFsPath()+targetFileBasename;
    		
    		
	    	ESDownloadProcess procTask = Globals.Get().getDatabasesMgr().getDocumentsDbInterface()
					.getNewGexfGroupByExtractProcessor(
		    			user, 
		    			user.getCurrentCatalog(),
		    			user.getText("Items.downloadItems.gexf.extracting"), 
		    			 targetFileFsPath,
		    			 groupingTerm,
		    			 edgeTermsList,
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
    		//String gexfFileUrl="http://metaindex.fr/metaindex/downloads/xyzazertutj2454RHHF433a";
    		
    		Boolean success = procTask.isDataGenerated();
			answer.setIsSuccess(success);
			String targetFileUri=Globals.Get().getWebAppsTmpUrl()+targetFileBasename;
			answer.setGraphFileUrl(targetFileUri);
			answer.setGraphFileName(targetFileBasename);
			answer.setGraphFileSizeMB(new Double(FileSystemUtils.GetTotalSizeBytes(targetFileFsPath)/1000000.0));
    		this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_graphgroupby_response", 
    				answer);
    		Globals.GetStatsMgr().handleStatItem(new GraphDownloadMxStat(user,user.getCurrentCatalog()));
    		
    		log.info("generated GEXF file '"+targetFileBasename+"' ("+answer.getGraphFileSizeMB()+"MB)");
    		Globals.GetStatsMgr().handleStatItem(new GexfDownloadMxStat(user,curCatalog));
	    } catch (DataProcessException | MessagingException | IOException e) 
		{
			log.error("Unable to process download_items_graphgroupby_file from '"+headerAccessor.getUser().getName()+"' : "+e);
			e.printStackTrace();
			
			answer.setIsSuccess(false);
			this.messageSender.convertAndSendToUser(
    				headerAccessor.getUser().getName(),
    				"/queue/download_items_graphgroupby_response", 
    				answer);
		}   
    }
}
