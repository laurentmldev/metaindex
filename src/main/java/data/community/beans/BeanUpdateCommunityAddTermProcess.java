package metaindex.data.community.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.ICommunity;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public class BeanUpdateCommunityAddTermProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanUpdateCommunityAddTermProcess.class);
	String newTermIdName="";
	int newTermDatatypeId=0;
	
	
	@Override
	public String execute()
	{  
		
		List<TermVocabularySet> emptyVocabulary = new ArrayList<TermVocabularySet>();  
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		try {
			ICommunity c = CommunitiesAccessor.getCommunity(this.getSelectedCommunity().getCommunityId());
			
			CommunityTermHandle newData = new CommunityTermHandle(this.getLoggedUserProfile(),new CommunityTerm(c));
			newData.setIdName(this.getNewTermIdName());
			newData.setDatatypeId(this.getNewTermDataTypeId());
			newData.setVocabularySets(emptyVocabulary);
			newData.create();
			this.getSelectedCommunity().addTerm(newData);
			this.getSelectedCommunity().update();
						
		} catch (DataAccessErrorException e) {
			status=BeanProcessResult.BeanProcess_DBERROR;
		} catch (DataAccessConstraintException e) {
			status=BeanProcessResult.BeanProcess_ConstraintERROR;
		} catch (DataReferenceErrorException e) {
			status=BeanProcessResult.BeanProcess_ConstraintERROR;
			e.printStackTrace();
		}
		
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("updateCommunity.success"));	
				
		}	

		return status.toString();
	}

	public String getNewTermIdName() {
		return newTermIdName;
	}
	public void setNewTermIdName(String newTermIdName) {
		this.newTermIdName = newTermIdName;
	}
	public int getNewTermDataTypeId() {
		return newTermDatatypeId;
	}
	public void setNewTermDataTypeId(int newTermDatatypeId) {
		this.newTermDatatypeId = newTermDatatypeId;
	}
	
	
}
