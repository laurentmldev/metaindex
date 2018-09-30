package metaindex.data;

import java.util.List;

import org.json.JSONObject;

import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UserProfileData;
import metaindex.dbaccess.IJsonEncodable;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.dbaccess.IDataAccessAware.DataReferenceErrorException;
import metaindex.dbaccess.IGenericEncodable.UnableToPopulateException;

public interface IBufferizedData extends IJsonEncodable {

	/**
	 * Set floag that data is not coherent with datasource
	 */
	public void invalidate();
	
	/**
	 * Apply modifications made to this subdata
	 * @param activeUser
	 */
	public void commit(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException ;

	/**
	 * Apply modifications made to this subdata and all its subdata
	 * @param activeUser
	 */
	public void commitFull(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException ;
	
	/**
	 * Update values from data source
	 * @param activeUser
	 */
	public void update(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException,DataReferenceErrorException ;
		
	/**
	 * Update values from data source and all sub-datasources if any
	 * @param activeUser
	 */
	public void updateFull(IUserProfileData activeUser) throws DataAccessErrorException,DataAccessConstraintException,DataReferenceErrorException ;
	
	/**
	 * Insert a new entry into DB corresponding to the given Data
	 * @param activeUser the user trying to perform operation. This parameter is used when access rights control is performed
	 */
	public void create(IUserProfileData activeUser) 
			throws DataAccessErrorException,DataAccessConstraintException ;
	
	/** 
	 * Delete (definitely) this data from DB and all included subdata
	 * @param activeUser the user trying to perform operation. This parameter is used when access rights control is performed
	 */
	public void delete(IUserProfileData activeUser)
			throws DataAccessErrorException,DataAccessConstraintException ;
	
	
	/**
	 * Say whether this data structure it is coherent with model behind
	 */
	public boolean isSynchronized();
	
	/**
	 * Populate this message with the given JSON string, based on JAVA reflect API
	 * @throws UnableToPopulateException TODO
	 */
	public void populateFromJson(IUserProfileData activeUser, JSONObject json, POPULATE_POLICY policy) throws UnableToPopulateException;
}
