package metaindex.dbaccess.accessors;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.CatalogHandle;
import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.ICatalogContents;
import metaindex.data.catalog.ICatalogHandle;
import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.dataset.Dataset;
import metaindex.data.element.Element;
import metaindex.data.element.IElement;
import metaindex.data.metadata.Metadata;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IAssociatedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;

public abstract class ACatalogAccessor extends AMetaindexAccessor<ICatalog>
									implements IAssociatedData<ICatalog,ICommunity,ICatalogHandle>,
									IDataAccessAware{

	public ACatalogAccessor(ADataAccessFactory accessorsFactory,DataSource dataSource, 
			PlatformTransactionManager txManager) throws DataAccessConnectException {
			super(accessorsFactory,dataSource,txManager);		
	}

	
	
	/**
	 * Retrieve the explicit list of elements ID belonging to the given catalog 
	 * @param activeUser
	 * @param catalog
	 * @return
	 * @throws DataAccessErrorException 
	 */
	public abstract List<Integer> getCatalogStaticElementsIds(IUserProfileData activeUser, ICatalog catalog) throws DataAccessErrorException;
	

	/**
	 * Retrieve list of elements ID belonging to the given catalog as returned by the searchQuery.
	 * The user searchQuery is converted here in a query understandable by the data system.
	 * Typically this consist of a conversion into an SQL statement.  
	 * @param activeUser
	 * @param catalog
	 * @return
	 * @throws DataAccessErrorException 
	 */
	public abstract List<Integer> getCatalogDynamicElementsIds(IUserProfileData activeUser, ICatalog catalog) throws DataAccessErrorException;

	
	/**
	 * Get all Tmeplates elements from given community.
	 * @param activeUser
	 * @param community
	 * @return
	 */
	public abstract List<Integer> getTemplateElementsIds(IUserProfileData activeUser, ICommunity community);
}
