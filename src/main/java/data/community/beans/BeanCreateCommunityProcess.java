package metaindex.data.community.beans;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.Community;
import metaindex.data.community.CommunityHandle;
import metaindex.data.community.CommunityVocabularySet;
import metaindex.data.community.ICommunity;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;


public class BeanCreateCommunityProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCreateCommunityProcess.class);
	
	@Override
	public String execute()
	{  

		// prepare Username field from session
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS; 
		
		if (CommunitiesAccessor.communityExists(this.getIdName())) {
			this.addFieldError("idName",getText("createCommunity.error.existingCommunity"));
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}
		ICommunity c=null;
		try { 
			c=new Community(this.getIdName());
			c.setCreatorName(this.getLoggedUserProfile().getUsername());
			c.create(this.getLoggedUserProfile());
			CommunitiesAccessor.addCommunity(c);			
			c.getVocabulary(this.getLoggedUserProfile()).setCommunityNameTraduction(this.getIdName());
			c.commit(this.getLoggedUserProfile());
		} 
		catch (DataAccessConstraintException e) 
		{
			this.addFieldError("idName",getText("createCommunity.error.existingCommunity"));
			status = BeanProcessResult.BeanProcess_ConstraintERROR; 
		}
		catch (DataAccessErrorException e) 
		{
			log.error("DBError while creating new community: "+e.getMessage());
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		} 

		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("createCommunity.success"));				
		}	
		this.setSelectedCommunity(new CommunityHandle(this.getLoggedUserProfile(),c));


		return status.toString();
		
	}
	
}
