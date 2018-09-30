package metaindex.dbaccess;

import javax.sql.DataSource;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;


public interface IDBAccessedData   {



	
	public class BeanDataException extends Exception{
		private static final long serialVersionUID = 363032203695317138L;
		public BeanDataException(String msg) { super(msg); }
	}
	public class PopulateBeanException extends Exception {
		private static final long serialVersionUID = 5361221365887111315L;
		public PopulateBeanException(Exception e) { super(e); }
	}	
    /**
     * Get datasource object of this bean.
     * If not set, try to retrieve one using the 'DB_DATASOURCE' bean from current Web Application context (Spring)
     * 								
     * @return datasource used by this bean
     */
	public DataSource getDataSource();

	/**
	 * @param ds the data source to be used
	 * @throws BeanDataException if a datasource is already set for this bean
	 */
	public void setDataSource(DataSource ds) throws BeanDataException;
	
    /**
     * Get DB transaction manager
     * If not set, try to retrieve one using the 'DB_TXMANAGER' bean from current Web Application context (Spring)
     * 								
     * @return txManager used by this bean
     */
	public PlatformTransactionManager getTxManager();
	
	
}
