package metaindex.app.control.websockets.filters;

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

import metaindex.data.filter.Filter;
import metaindex.data.filter.IFilter;
import metaindex.app.Globals;
import metaindex.app.control.websockets.filters.messages.*;
import metaindex.app.control.websockets.commons.AMxWSController;
import metaindex.app.periodic.statistics.filters.CreateFilterMxStat;
import metaindex.app.periodic.statistics.filters.DeleteFilterMxStat;
import metaindex.app.periodic.statistics.filters.UpdateFilterMxStat;
import metaindex.app.periodic.statistics.terms.UpdateLexicTermMxStat;
import metaindex.app.periodic.statistics.user.ErrorOccuredMxStat;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

@Controller
public class WsControllerFilter extends AMxWSController {
	
	private Log log = LogFactory.getLog(WsControllerFilter.class);
	
	@Autowired
	public WsControllerFilter(SimpMessageSendingOperations messageSender) {
		super(messageSender);		
	}		
		
    @MessageMapping("/create_filter")
    @SubscribeMapping ( "/user/queue/created_filter")
    public void handleCreateFilterRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgCreateFilter_request requestMsg) throws Exception {

    	WsMsgCreateFilter_answer answer = new WsMsgCreateFilter_answer(requestMsg);
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (!this.userHasWriteAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_filter", answer);
			return;         		
    	}
    	
    	IFilter newFilter = new Filter();
    	newFilter.setName(requestMsg.getFilterName());
    	newFilter.setQuery(requestMsg.getQuery());
    	Boolean result = Globals.Get().getDatabasesMgr().getFiltersDbInterface().createIntoDbStmt(c, newFilter).execute();
    	
    	// refresh filters list in catalog
    	c.acquireLock();
    	c.clearFilters();
    	Globals.Get().getDatabasesMgr().getFiltersDbInterface().getLoadFromDbStmt(c).execute();
    	c.releaseLock();
    	
    	if (result==false) {
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_filter", answer);
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.create_filter.refused_by_server"));
    	}
    	
    	// retrieve full filter data (filterName typically)
    	IFilter updatedFilter = c.getFilter(user, requestMsg.getFilterName()); 
    	answer.setIsSuccess(true);  
    	answer.setFilterName(updatedFilter.getName());
    	answer.setFilterId(updatedFilter.getId());
    	
    	answer.setIsSuccess(true);  
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/created_filter", answer);
    	Globals.GetStatsMgr().handleStatItem(new CreateFilterMxStat(user,c));
        	
    }
    
	
    @MessageMapping("/update_filter")
    @SubscribeMapping ( "/user/queue/updated_filter")
    public void handleUpdateFilterRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgUpdateFilter_request requestMsg) throws Exception {

    	WsMsgUpdateFilter_answer answer = new WsMsgUpdateFilter_answer();
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (!this.userHasWriteAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/updated_filter", answer);
			return;         		
    	}
    	
    	IFilter newFilter = new Filter();
    	newFilter.setId(requestMsg.getFilterId());
    	newFilter.setQuery(requestMsg.getQuery());
    	Boolean result = Globals.Get().getDatabasesMgr().getFiltersDbInterface().updateIntoDbStmt(c, newFilter).execute();
    	
    	// refresh filters list in catalog
    	c.acquireLock();
    	c.clearFilters();
    	Globals.Get().getDatabasesMgr().getFiltersDbInterface().getLoadFromDbStmt(c).execute();
    	c.releaseLock();
    	
    	if (result==false) {
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/updated_filter", answer);
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.update_filter.refused_by_server"));
    	}
    	
    	// retrieve full filter data (filterName typically)
    	IFilter updatedFilter = c.getFilter(user, requestMsg.getFilterId()); 
    	answer.setIsSuccess(true);  
    	answer.setFilterName(updatedFilter.getName());
    	answer.setFilterId(updatedFilter.getId());
    	answer.setQuery(updatedFilter.getQuery());
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/updated_filter", answer);
    	Globals.GetStatsMgr().handleStatItem(new UpdateFilterMxStat(user,c));
        	
    }
    
	
    @MessageMapping("/delete_filter")
    @SubscribeMapping ( "/user/queue/deleted_filter")
    public void handleDeleteFilterRequest(
    					SimpMessageHeaderAccessor headerAccessor, 
    					WsMsgDeleteFilter_request requestMsg) throws Exception {

    	WsMsgDeleteFilter_answer answer = new WsMsgDeleteFilter_answer();
    	IUserProfileData user = getUserProfile(headerAccessor);	
    	ICatalog c = user.getCurrentCatalog();
    	
    	if (!this.userHasWriteAccess(user,c)) { 
    		answer.setRejectMessage(user.getText("globals.noAccessRights"));
			this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_filter", answer);
			return;         		
    	}
    	
    	IFilter newFilter = new Filter();
    	newFilter.setId(requestMsg.getFilterId());
    	Boolean result = Globals.Get().getDatabasesMgr().getFiltersDbInterface().deleteFromDbStmt(c, newFilter).execute();
    	
    	// refresh filters list in catalog
    	c.acquireLock();
    	c.clearFilters();
    	Globals.Get().getDatabasesMgr().getFiltersDbInterface().getLoadFromDbStmt(c).execute();
    	c.releaseLock();
    	
    	if (result==false) {
    		this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_filter", answer);
    		Globals.GetStatsMgr().handleStatItem(new ErrorOccuredMxStat(user,"websockets.delete_filter.refused_by_server"));
    	}
    	
    	answer.setIsSuccess(true);  
    	answer.setFilterId(newFilter.getId());
    	this.messageSender.convertAndSendToUser(headerAccessor.getUser().getName(),"/queue/deleted_filter", answer);
    	Globals.GetStatsMgr().handleStatItem(new DeleteFilterMxStat(user,c));
        	
    }
}
