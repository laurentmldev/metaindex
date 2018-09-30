package metaindex.data;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.community.ICommunitySubdata;
import metaindex.data.element.IElement;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AccessRightException;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;


@SuppressWarnings("rawtypes")
public interface IObserver<T extends IObservable> {
		
	
	public void notifyChange(T observedObject);	
	
}

