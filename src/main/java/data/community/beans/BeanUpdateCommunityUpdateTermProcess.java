package metaindex.data.community.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.community.ICommunityTermHandle;
import metaindex.data.community.TermVocabularySet;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public class BeanUpdateCommunityUpdateTermProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanUpdateCommunityUpdateTermProcess.class);
	String formTermIdName="";	
	
	@Override
	public String execute()
	{  
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		ICommunityTermHandle termData=this.getSelectedCommunity().getTermData(getFormTermIdName());
		
		if (termData==null) 
		{
			log.error("Unable to find term '"+this.getFormTermIdName()+"' in community '"+this.getSelectedCommunity().getIdName()+"'");
			return BeanProcessResult.BeanProcess_DBERROR.toString();
		}
		
		try {
			termData.commit();
		} catch (DataAccessErrorException e) {
			status=BeanProcessResult.BeanProcess_DBERROR;
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (DataAccessConstraintException e) {
			status=BeanProcessResult.BeanProcess_ConstraintERROR;
			log.error(e.getMessage());
		}
		
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("updateTerm.success"));				
		}	
		
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getSelectedCommunity().update(); }
		catch (Exception e)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}

	public String getFormTermIdName() {
		return formTermIdName;
	}
	public void setFormTermIdName(String termIdName) {
		this.formTermIdName = termIdName;
	}
	
}
