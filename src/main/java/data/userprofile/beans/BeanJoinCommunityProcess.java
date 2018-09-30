package metaindex.data.userprofile.beans;  

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.CommunityHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunityHandle;
import metaindex.data.management.CommunitiesAccessor;


public class BeanJoinCommunityProcess extends BeanProfile {  
  
	private static final long serialVersionUID = -2616542792633700713L;
	private Log log = LogFactory.getLog(BeanJoinCommunityProcess.class);
	
	private String communityIdName = "";
	private String communityGroupName = "";
	
	@Override
	public String execute()
	{  
		try 
		{
			
			ICommunity c = CommunitiesAccessor.getOrLoadCommunity(this.getLoggedUserProfile(),this.getCommunityIdName());
						
			log.error("joining community. user = "+this.getLoggedUserProfile().getUsername()+" loggedIn="+this.getLoggedUserProfile().isLoggedIn());
			ICommunityHandle chdl = new CommunityHandle(this.getLoggedUserProfile(),c);
			chdl.joinCommunity();
			this.getLoggedUserProfile().setSelectedCommunity(chdl);							
		}
		catch (DataAccessErrorException e) {			
				log.error("Error while udating data for user '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
				return BeanProcessResult.BeanProcess_DBERROR.toString(); 
		} catch (DataAccessConstraintException e) {			
			log.error("Constraint Error while udating data for user '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		} catch (DataReferenceErrorException e) {
			log.error("Data reference error while udating data for user '"+this.getLoggedUserProfile().getUsername()+"' : "+e.getMessage());
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		} 		
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();				
	}

	public String getCommunityIdName() {
		return communityIdName;
	}

	public void setCommunityIdName(String communityIdName) {
		this.communityIdName = communityIdName;
	}

	public String getCommunityGroupName() {
		return communityGroupName;
	}

	public void setCommunityGroupName(String communityGroupName) {
		this.communityGroupName=communityGroupName;
	}

	
}  
