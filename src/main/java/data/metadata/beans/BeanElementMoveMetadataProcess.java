package metaindex.data.metadata.beans;

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
import metaindex.data.metadata.IMetadata;
import metaindex.data.metadata.IMetadataHandle;
import metaindex.data.metadata.Metadata;
import metaindex.data.metadata.MetadataHandle;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;


public class BeanElementMoveMetadataProcess extends BeanElement  {

	private static final long serialVersionUID = -3096098703226452138L;
	private Log log = LogFactory.getLog(BeanElementMoveMetadataProcess.class);
	
	private int movedMetadataId=0;
	private int moveToDatasetId=0;
	private int moveToColumn=0;
	private int moveToPosition=0;
		
	@Override
  	public String execute() throws Exception {
		
		
		BeanProcessResult status = BeanProcessResult.BeanProcess_SUCCESS;
		
		
		if (!this.getSelectedElement().isIdentified()) 
		{
			addActionError("Sorry, no current element selected for creating new metadata");
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
						
		// get the corresponding metadata		
		IMetadataHandle movedMetadata = this.getSelectedCatalog().getSelectedElement().getMetadata(getMovedMetadataId());
		
		// if not found return an error
		if (movedMetadata==null) {
			addActionError("Sorry, requested metadata '"+getMovedMetadataId()+"' not found (anymore?) "
								+"in currently selected element '"+this.getSelectedCatalog().getSelectedElement().getElementId()+"'");
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}		
		
		// move data
		try {
			this.getSelectedElement().moveMetadata(movedMetadata,this.getMoveToDatasetId(),this.getMoveToColumn(), this.getMoveToPosition());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return BeanProcessResult.BeanProcess_ERROR.toString(); 
		}
		
		if (status==BeanProcessResult.BeanProcess_SUCCESS){
				addActionMessage(getText("createMetadata.success"));				
		}	
		
		// Refresh bean data from Database for display
		// No difference if the update was successful or not, 
		// we want to show the user a GUI with data as it's in the DB 
		//IDatasetHandle targetDataset=this.getSelectedCatalog().getSelectedElement().getDataset(this.getMoveToDatasetId());		
		try { 
			//this.getSelectedElement().getDataAccess().getNewMetadatasAccessor(this.getDataSource(), getTxManager())
			//												.loadMetadatasFromDB(this.getLoggedUserProfile(),targetDataset);
			this.getSelectedElement().commitFull();
		}
		catch (Exception e)
		{ 
				return BeanProcessResult.BeanProcess_ERROR.toString(); 
		} 
		
		return status.toString();
	}

	/**
	 * @return the moveToDatasetId
	 */
	public int getMoveToDatasetId() {
		return moveToDatasetId;
	}

	/**
	 * @param moveToDatasetId the moveToDatasetId to set
	 */
	public void setMoveToDatasetId(int moveToDatasetId) {
		this.moveToDatasetId = moveToDatasetId;
	}

	/**
	 * @return the moveToColumn
	 */
	public int getMoveToColumn() {
		return moveToColumn;
	}

	/**
	 * @param moveToColumn the moveToColumn to set
	 */
	public void setMoveToColumn(int moveToColumn) {
		this.moveToColumn = moveToColumn;
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
	public int getMovedMetadataId() {
		return movedMetadataId;
	}

	/**
	 * @param moveMetadataId the moveMetadataId to set
	 */
	public void setMovedMetadataId(int moveMetadataId) {
		this.movedMetadataId = moveMetadataId;
	}

  	

}
