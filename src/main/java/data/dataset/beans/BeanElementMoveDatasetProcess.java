package metaindex.data.dataset.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.Preparable;

import metaindex.data.beans.AMetaindexBean;
import metaindex.data.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.beans.BeanElement;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.metadata.Metadata;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanElementMoveDatasetProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementMoveDatasetProcess.class);
	
	private int movedDatasetId=0;
	private int moveToPosition=0;
		
	@Override
  	public String execute() throws Exception {
		
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		IElement e = CommunitiesAccessor.getCommunity(this.getSelectedCommunity().getCommunityId()).getElement(this.getSelectedElement().getElementId());
		
		List<IDataset> impactedDatasets = new ArrayList<IDataset>();
		
		if (!this.getSelectedElement().isIdentified()) 
		{
			addActionError("Sorry, no current element selected for creating new metadata");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
						
		// get the corresponding metadata
		IDataset movedDataset = e.getDataset(getMovedDatasetId());
		// if not found return an error
		if (movedDataset==null) {
			addActionError("Sorry, requested dataset '"+getMovedDatasetId()+"' not found (anymore?) "
								+"in currently selected element '"+this.getSelectedCatalog().getSelectedElement().getElementId()+"'");
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}		
				
		movedDataset.setLayoutPosition(this.getMoveToPosition());
		impactedDatasets.add(movedDataset);
		
		// set position of other element of the set
		List<IDataset> sortedDatasets = e.getDatasets();
		
		
		Iterator<IDataset> it = sortedDatasets.iterator();
		int lastPos=getMoveToPosition();
		while(it.hasNext()) {
			IDataset cur=it.next();
			if (cur!=movedDataset && cur.getLayoutPosition()==lastPos) {
				cur.setLayoutPosition(++lastPos);
				impactedDatasets.add(cur);
			}			
		}
		
		try { 
			e.getDataAccess().getNewDatasetsAccessor(e.getDataSource(), e.getTxManager()).storeIntoDB(this.getLoggedUserProfile(),impactedDatasets);			
		} 
		catch (DataAccessErrorException|DataAccessConstraintException ex) 
		{ 
			addActionError(getText("error.DBprocess"));
			status = BeanProcessResult.BeanProcess_DBERROR; 
		}
			
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("createMetadata.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		try { this.getSelectedCommunity().getSelectedCatalog().getSelectedElement().update(); }
		catch (Exception ex)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}

	
	/**
	 * @return the moveToPosition
	 */
	public int getMoveToPosition() {
		return moveToPosition;
	}

	/**
	 * @param moveToPosition the moveToPosition to set
	 */
	public void setMoveToPosition(int moveToPosition) {
		this.moveToPosition = moveToPosition;
	}

	/**
	 * @return the moveMetadataId
	 */
	public int getMovedDatasetId() {
		return movedDatasetId;
	}

	/**
	 * @param moveDatsetId the moveMetadataId to set
	 */
	public void setMovedDatasetId(int moveDatsetId) {
		this.movedDatasetId = moveDatsetId;
	}

  	

}
