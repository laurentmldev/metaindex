package metaindex.data.community.beans;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.websockets.ChatServer;


public class BeanOpenCommunityProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanOpenCommunityProcess.class);
	
	String idname = "";
	
	@Override
	public String execute()
	{  
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		this.getLoggedUserProfile().quitSelectedCommunity();
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it is in the DB 
		try { 
			ICommunity comm = CommunitiesAccessor.getCommunity(this.getIdName());
			comm.enter(this.getLoggedUserProfile());
			comm.updateFull(this.getLoggedUserProfile());			
		} catch (Exception e) { 
			e.printStackTrace();
			return BeanProcessResult.BeanProcess_ERROR.toString(); } 
							
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
			addActionMessage(getText("openCommunity.success"));				
		}	
		
		return status.toString();
	}
	
	
	public String getIdName() {
		return idname;
	}

	
	public void setIdName(String idName) {
		idname=idName;
	}
	

}
