package metaindex.dbaccess.accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.IDataset;
import metaindex.data.element.Element;
import metaindex.data.element.ElementHandle;
import metaindex.data.element.IElement;
import metaindex.data.element.IElementHandle;
import metaindex.data.metadata.IMetadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.accessors.AMetaindexAccessor.ADBBatchOperator;

public abstract class AElementAccessor extends AMetaindexAccessor<IElement> 
								implements IAssociatedData<IElement,ICommunity,IElementHandle> {

	public AElementAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
				PlatformTransactionManager txManager) throws DataAccessConnectException {
				super(accessorsFactory,dataSource,txManager);		
	}

	@Override
	public void createFullIntoDB(IUserProfileData activeUser, List<IElement> dataObjects) {
		
		Connection con=null;
		try {
			con = this.getDataSource().getConnection();
			con.setAutoCommit(false);
			
			List<PreparedStatement> elementStmts = this.getCreateIntoDBOperator().createStatements(activeUser.getSelectedCommunity(),con);
			List<PreparedStatement> datasetStmts = this.getDatasetDBAccessor().getCreateIntoDBOperator().createStatements(activeUser.getSelectedCommunity(),con);
			List<PreparedStatement> metadataStmts = this.getMetadataDBAccessor().getCreateIntoDBOperator().createStatements(activeUser.getSelectedCommunity(),con);
						
			Iterator<IElement> it = dataObjects.iterator();
			while (it.hasNext()) {
				IElement curEl = it.next();
				this.getCreateIntoDBOperator().populateStatements(activeUser, curEl, elementStmts);
				Iterator<IDataset> itD = curEl.getDatasets().iterator();
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
			}

			Iterator<PreparedStatement> itStmtsE = elementStmts.iterator();
			while (itStmtsE.hasNext()){ itStmtsE.next().executeBatch(); }
			Iterator<PreparedStatement> itStmtsD = datasetStmts.iterator();
			while (itStmtsD.hasNext()){ 
				PreparedStatement curStmt = itStmtsD.next(); 
				curStmt.executeBatch(); 
			}			
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
	
	// implements IAssociatedData<ElementData,Catalog>
	// but Java cowardly refuses to let a class implements twice the same interface with different
	// template parameters.
	// So we declare them explicitly.
	
	/**
	 * Load all elements T associated to the given element A
	 * @param activeUserName the username to use for potential access rights checking
	 * @param associatedObject the associated data object 
	 * @return the list of found elements corresponding to the given associated object
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public abstract List<IElement> loadAssociatedData(IUserProfileData activeUser, ICatalogContents associatedObject)
			throws DataAccessErrorException,DataAccessConstraintException;

	public abstract List<IElement> loadUserAssociatedData(IUserProfileData activeUser, ICatalogContents associatedObject)
			throws DataAccessErrorException,DataAccessConstraintException;
	
	/**
	 * Add a link between the data managed by this dataAccess and the given data to associate
	 * @param activeuser the name of the user performing operation (for potential access rights check)
	 * @param managedData the managed data to be associated
	 * @param associatedData the data to which we want to associate the managed data
	 * @throw DataAccessErrorException when a problem with DB connection or operation occurred
	 * @throw DataAccessConstraintException when such a link already exist
	 */
	public abstract void addAssociation(IUserProfileData activeUser, IElement managedData,  ICatalogContents associatedData) 
			throws DataAccessErrorException,DataAccessConstraintException;
	
	
	/**
	 * Remove a link between the data managed by this dataAccess and the given associated data
	 * @param activeuser the name of the user performing operation (for potential access rights check)
	 * @param managedData the managed data to be "disassociated"
	 * @param associatedData the data to which we want to "disassociate" the managed data
	 * @throw DataAccessErrorException when a problem with DB connection or operation occurred
	 * @throw DataAccessConstraintException when such a link already exist
	 */	
	public abstract void removeAssociation(IUserProfileData activeUser, IElement managedData,  ICatalogContents associatedData) 
			throws DataAccessErrorException,DataAccessConstraintException;

	public abstract void removeAssociation(IUserProfileData activeUser, List<IElement> managedData, ICatalogContents catalog);
	/**
	 * Retrieve list of elements having at least one metadata referencing given term
	 * @param termId id of
	 * @return ids list of elements referencing given term
	 */
	public abstract List<Integer> getElementsByTerm(Integer termId);
}
