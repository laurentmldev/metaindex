package metaindex.data.community.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.element.beans.BeanElement;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanCommunityNextElementDataProcess extends BeanCommunity  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanCommunityNextElementDataProcess.class);
	
	private int nextElementId=0;
		
	@Override
  	public String execute() throws Exception {
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;

		if (!this.getSelectedCommunity().isIdentified()) 
		{
			addActionMessage("Sorry, no current community selected for creating new element");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		if (this.getNextElementId()==0) {
			this.getSelectedCommunity().getSelectedCatalog().nextElement();
		} else {
			try {
				this.getSelectedCommunity().getSelectedCatalog().setSelectedElement(this.getNextElementId());
			} catch (DataAccessErrorException e) {
				addActionError("Element '"+this.getNextElementId()+"' not found in Catalog.");
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
			}
		}
		return status.toString();
	}
  	@Override
  	public void prepare() throws Exception { 
  		super.prepare();
  	}
	public int getNextElementId() {
		return nextElementId;
	}
	public void setNextElementId(int nextElementId) {
		this.nextElementId = nextElementId;
	}

  	
  	

}
