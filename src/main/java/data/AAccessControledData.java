package metaindex.data;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.transaction.PlatformTransactionManager;

import metaindex.data.community.Community;
import metaindex.data.community.ICommunity;
import metaindex.data.community.ICommunitySubdata;
import metaindex.data.element.IElement;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.AJsonEncodable;
import metaindex.dbaccess.AccessRightException;
import metaindex.dbaccess.IDBAccessFactoryManager;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDBAccessedData.BeanDataException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConnectException;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.dbaccess.accessors.ACatalogAccessor;
import metaindex.dbaccess.accessors.ACommunityAccessor;
import metaindex.dbaccess.accessors.ACommunityTermsAccessor;
import metaindex.dbaccess.accessors.ADatasetAccessor;
import metaindex.dbaccess.accessors.AElementAccessor;
import metaindex.dbaccess.accessors.AMetadataAccessor;


public abstract class AAccessControledData<T extends ICommunitySubdata> 
														extends AJsonEncodable implements IAccessControledData {
		
	private Log log = LogFactory.getLog(AAccessControledData.class);

	IUserProfileData userProfile;
	T refData;
	public AAccessControledData(IUserProfileData userProfile, T refData) {
		this.userProfile=userProfile;
		this.refData=refData;
	}
	
	public IUserProfileData getUserProfile() { return userProfile; }
	protected T getRefData() { return refData; }
	
	public boolean isWritableByUser() { return true; }
	public boolean isManageableByUser() { return true; }
	public boolean isReadableByUser() { return true; }
	
	protected void checkWritableByUser() throws AccessRightException {
		if (!isWritableByUser()) {
			throw new AccessRightException("Object is not Writable for user "+this.getUserProfile().getUsername());
		}
	}
	protected void checkManageableByUser() throws AccessRightException {
		if (!isManageableByUser()) {
			throw new AccessRightException("Object is not Manageable for user "+this.getUserProfile().getUsername());
		}
	}
	protected void checkReadableByUser() throws AccessRightException {
		if (!isReadableByUser()) {
			throw new AccessRightException("Object is not Readable for user "+this.getUserProfile().getUsername());
		}
	}	


	@Override
	public boolean isIdentified() {
		this.checkReadableByUser();
		return this.getRefData().isIdentified();
	}

	@Override
	public void invalidate() {
		this.getRefData().invalidate();	
	}

	@Override
	public void commit() throws DataAccessErrorException, DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().commit(this.getUserProfile());		
	}

	@Override
	public void commitFull() throws DataAccessErrorException, DataAccessConstraintException {
		this.checkWritableByUser();
		this.getRefData().commitFull(this.getUserProfile());		
	}

	@Override
	public void update()
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.getRefData().update(this.getUserProfile());		
	}

	@Override
	public void updateFull()
			throws DataAccessErrorException, DataAccessConstraintException, DataReferenceErrorException {
		this.getRefData().updateFull(this.getUserProfile());		
	}

	@Override
	public void create() throws DataAccessErrorException, DataAccessConstraintException {
		this.checkManageableByUser();
		this.getRefData().create(this.getUserProfile());
		
	}

	@Override
	public void delete() throws DataAccessErrorException, DataAccessConstraintException {
		this.checkManageableByUser();
		this.getRefData().delete(this.getUserProfile());
		
	}


	@Override
	public boolean isSynchronized() {		
		return this.getRefData().isSynchronized();
	}
	

	@Override
	public void setDataAccess(ADataAccessFactory dataAccess) throws DataAccessErrorException {
		this.getRefData().setDataAccess(dataAccess);
		
	}

	@Override
	public ADataAccessFactory getDataAccess() {
		return this.getRefData().getDataAccess();
	}

	@Override
	public DataSource getDataSource() {
		return this.getRefData().getDataSource();
	}

	@Override
	public void setDataSource(DataSource ds) throws BeanDataException {
		this.getRefData().setDataSource(ds);
		
	}

	@Override
	public PlatformTransactionManager getTxManager() {
		return this.getRefData().getTxManager();
	}

	@Override
	public boolean isReadOnly() {
		return this.getRefData().isReadOnly();		
	}

	@Override
	public void setReadOnly(boolean isReadOnly) {
		this.getRefData().setReadOnly(isReadOnly);		
	}
	
	/* Clone
	@Override
	public abstract T clone() throws CloneNotSupportedException;
	*/
	
	@Override
	public void populateFromJson(JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException 
	{ 
		this.getRefData().populateFromJson(this.getUserProfile(),json, policy); 		
	}
	

	@Override
	public JSONObject encode() {
		return this.getRefData().encode();		
	}
}

