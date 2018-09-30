package metaindex.data;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.ContextLoader;

import metaindex.data.community.Community;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;

public interface IGenericMetaindexData {
	

	/**
	 * Ensure that all contents are compliant with DB contstriants
	 * Typically contents max. length etc.
	 */
	public void checkDataDBCompliance() throws DataAccessConstraintException;
	

	/**
	 * Used to know if a given data object contains necessary keys for a refresh from DB
	 * @return Return true if necessary fields for a refreshFromDB are filled, false otherwise
	 */
	public boolean isIdentified(); 
	
	
	/**
	 * Says wether this subdata is read-only for technical or logical reason 
	 * (i.e. not for user access reason, which is managed through the UserXxx subclasses)
	 * @return
	 */
	public boolean isReadOnly();
	
	/**
	 * Changes the RO status of this data
	 * @param isReadOnly
	 */
	public void setReadOnly(boolean isReadOnly);
	

}

