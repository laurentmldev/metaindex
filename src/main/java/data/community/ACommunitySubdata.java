package metaindex.data.community;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.ContextLoader;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.community.Community;
import metaindex.data.management.CommunitiesAccessor;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.ADataAccessFactory;
import metaindex.dbaccess.IDBAccessedData;
import metaindex.dbaccess.IDataAccessAware;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;
import metaindex.dbaccess.impl.metaindexdb.MetaindexDBAccessFactory;

public abstract class ACommunitySubdata <T> extends AGenericMetaindexData<T> implements ICommunityAccessing,ICommunitySubdata {
	
	private Log log = LogFactory.getLog(ACommunitySubdata.class);

	
	
	private ICommunity myCommunity; 
	/**
	 *  Say wether this data is read-only or not (regardless of user access rights)
	 */
	private boolean readOnly=false;
	
	/**
	 * We use a separate field for the community Id since it is used
	 * when populating the data object from DB requests results.
	 * Though we check that retrieved data is as expected
	 * (same communityId than our communityData object)
	 */
	private int communityId;
	
	public ACommunitySubdata(ICommunity myCommunity, ADataAccessFactory dataAccessor)  {
			super(dataAccessor);
			this.setCommunityData(myCommunity);
		
	}
	
	/**
	 * Ensure that all contents are compliant with DB contstriants
	 * Typically contents max. length etc.
	 */
	public abstract void checkDataDBCompliance() throws DataAccessConstraintException;

	@Override
	public ICommunity getCommunityData() {
		try {
			return CommunitiesAccessor.getCommunity(this.getCommunityId());
		} catch (DataAccessErrorException | DataAccessConstraintException | DataReferenceErrorException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @param communityData the communityData to set
	 */
	private void setCommunityData(ICommunity communityData) {
		this.myCommunity = communityData;
		this.setCommunityId(myCommunity.getCommunityId());
	}

	/**
	 * @return the communityId
	 */
	@Override
	public Integer getCommunityId() {
		return communityId;
	}

	/**
	 * @param communityId the communityId to set
	 */
	public void setCommunityId(int communityId) {
		this.communityId = communityId;
	}
	

	/**
	 * @return the readOnly
	 * Say wether this subdata is structurally ReadOnly (i.e. regardless of any access rights consideration) 
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the readOnly to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public boolean isModifyOverridenTemplate() {
		return false;
	}

	@Override	
	public boolean isTemplated() {
		return false;
	}
	
	/**
	 * Return a clone of this element
	 * @return
	 */
	/* Clone
	@Override
	public abstract T clone() throws CloneNotSupportedException;
	*/
	
	@Override
	public void populateFromJson(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException {
		populate(activeUser, json, POPULATE_POLICY.REPLACE_EXISTING_AND_CREATE_WHEN_NEW);				
	}
}

