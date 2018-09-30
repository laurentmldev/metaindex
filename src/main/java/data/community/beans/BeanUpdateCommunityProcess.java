package metaindex.data.community.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public class BeanUpdateCommunityProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanUpdateCommunityProcess.class);
	private String formIdName="";
	
	@Override
	public String execute()
	{  
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		try {
			this.getSelectedCommunity().commit();			
		} 
		catch (DataAccessConstraintException e) 
		{
			this.addFieldError("idName",getText("createCommunity.error.existingCommunity"));
			status = BeanProcessResult.BeanProcess_ConstraintERROR; 
		}
		catch (DataAccessErrorException e) 
		{ 
			addActionError(getText("error.DBprocess"));
			e.printStackTrace();
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
		
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("updateCommunity.success"));				
		}	
			
		return status.toString();
	}

	
}
