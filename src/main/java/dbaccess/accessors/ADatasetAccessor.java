package metaindex.dbaccess.accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.dataset.Dataset;
import metaindex.data.dataset.DatasetHandle;
import metaindex.data.dataset.IDataset;
import metaindex.data.dataset.IDatasetHandle;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public abstract class ADatasetAccessor extends AMetaindexAccessor<IDataset> 
								implements IAssociatedData<IDataset,IElement,IDatasetHandle>{

	public ADatasetAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource,
					PlatformTransactionManager txManager) throws DataAccessConnectException {
		super(accessorsFactory,dataSource,txManager);		
	}


	@Override
	public void createFullIntoDB(IUserProfileData activeUser, List<IDataset> dataObjects) {
		Connection con=null;
		try {
			con = this.getDataSource().getConnection();
			con.setAutoCommit(false);
			
			List<PreparedStatement> datasetStmts = this.getCreateIntoDBOperator().createStatements(activeUser.getSelectedCommunity(),con);
			List<PreparedStatement> metadataStmts = this.getMetadataDBAccessor().getCreateIntoDBOperator().createStatements(activeUser.getSelectedCommunity(),con);						
			
			Iterator<IDataset> itD = dataObjects.iterator();
			while (itD.hasNext()) {
				IDataset curD = itD.next();
				if (!curD.isTemplated() || curD.isModifyOverridenTemplate()) {						
					this.getDatasetDBAccessor().getCreateIntoDBOperator().populateStatements(activeUser, curD, datasetStmts);
					Iterator<IMetadata> itM = curD.getMetadata().iterator();
					while (itM.hasNext()) {
						IMetadata curM = itM.next();
						if (!curM.isTemplated() || curM.isModifyOverridenTemplate()) {
							this.getMetadataDBAccessor().getCreateIntoDBOperator().populateStatements(activeUser, curM, metadataStmts);								
						}
					}
				}
			}
			
			Iterator<PreparedStatement> itStmtsD = datasetStmts.iterator();
			while (itStmtsD.hasNext()){ itStmtsD.next().executeBatch(); }			
			Iterator<PreparedStatement> itStmtsM = metadataStmts.iterator();
			while (itStmtsM.hasNext()){ itStmtsM.next().executeBatch(); }
			con.commit();
			con.setAutoCommit(true);
			
			// TODO : elements storeIntoDB?
		} catch (SQLException e) {
			e.printStackTrace();
			if (con!=null) {
				try { con.rollback(); } 
				catch (SQLException e1) { e1.printStackTrace(); }
			}
			throw new DataAccessErrorException(e);
		}
	}
	
	public abstract void loadDatasetsFromDB(IUserProfileData activeUser, IElement parentElementData) 
										throws DataAccessErrorException,DataAccessConstraintException; 	 
	
}
