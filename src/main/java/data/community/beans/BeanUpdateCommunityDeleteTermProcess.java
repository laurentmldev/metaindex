package metaindex.data.community.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.community.CommunityTerm;
import metaindex.data.community.CommunityTermHandle;
import metaindex.data.community.ICommunityTermData;
import metaindex.data.community.ICommunityTermHandle;
import metaindex.data.community.TermVocabularySet;
import metaindex.data.element.IElementHandle;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public class BeanUpdateCommunityDeleteTermProcess extends BeanCommunity {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanUpdateCommunityDeleteTermProcess.class);
	Integer termId=0;
	
	
	@Override
	public String execute()
	{  
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
			
			ICommunityTermHandle termData=this.getSelectedCommunity().getTermDataById(this.getFormTermId());
			
			// TODO : lock community 
			
			// ensure no element from community is currently using this term.
			List<Integer> elementsIdsList = this.getSelectedCommunity().getElementsByTerm(this.getFormTermId());
			String strlist = "";
			Iterator<Integer> it = elementsIdsList.iterator();
			while (it.hasNext()) { strlist+=it.next()+" "; }
			if (elementsIdsList.size()>0) {
				String msg = "Unable to remove term '"+termData.getIdName()
				+"' from community, it is currently used by "+elementsIdsList.size()+" "
				+this.getSelectedCommunity().getVocabulary().getElementsTraduction()+" :" + strlist;
				
				this.addActionError(msg);
				return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
			}
			
			// perform delete
			termData.delete();			
			
			// TODO : unlock community
						
		
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("updateCommunity.success"));				
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

	public Integer getFormTermId() {
		return termId;
	}
	public void setFormTermId(Integer newTermId) {
		this.termId = newTermId;
	}
	
	
}
