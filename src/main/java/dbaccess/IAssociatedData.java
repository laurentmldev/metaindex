package metaindex.dbaccess;

import java.util.List;

import metaindex.data.AGenericMetaindexData;
import metaindex.data.IAccessControledData;
import metaindex.data.IGenericMetaindexData;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.dbaccess.IDataAccessAware.DataAccessConstraintException;
import metaindex.dbaccess.IDataAccessAware.DataAccessErrorException;
import metaindex.data.AAccessControledData;

/**
 * Describe a data access for an element associated with a community  
 * @author laurent
 *
 * @param <T> the managed data type
 * @param <A> the associated type
 */
public interface IAssociatedData <T extends IGenericMetaindexData,A,UT extends IAccessControledData> {

	/**
	 * Load all elements T associated to the given element A
	 * @param activeUser the profile of the user performing operation, for potential access rights checking
	 * @param associatedObject the associated data object 
	 * @return the list of found elements corresponding to the given associated object
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public List<T> loadAssociatedData(IUserProfileData activeUser, A associatedObject)
						throws DataAccessErrorException,DataAccessConstraintException;


	/**
	 * Load all elements T associated to the given element A and given active user
	 * @param activeUser the profile of the user performing operation, for potential access rights checking
	 * @param associatedObject the associated data object 
	 * @return the list of found elements corresponding to the given associated object
	 * @throws DataAccessErrorException
	 * @throws DataAccessConstraintException
	 */
	public List<UT> loadUserAssociatedData(IUserProfileData activeUser, A associatedObject)
						throws DataAccessErrorException,DataAccessConstraintException;
	
	/**
	 * Add a link between the data managed by this dataAccess and the given data to associate
	 * @param activeUser the profile of the user performing operation, for potential access rights checking
	 * @param managedData the managed data to be associated
	 * @param associatedData the data to which we want to associate the managed data
	 * @throw DataAccessErrorException when a problem with DB connection or operation occurred
	 * @throw DataAccessConstraintException when such a link already exist
	 */
	public void addAssociation(IUserProfileData activeUser, T managedData,  A associatedData) 
								throws DataAccessErrorException,DataAccessConstraintException;
	
	
	/**
	 * Remove a link between the data managed by this dataAccess and the given associated data
	 * @param activeUser the profile of the user performing operation, for potential access rights checking
	 * @param managedData the managed data to be "disassociated"
	 * @param associatedData the data to which we want to "disassociate" the managed data
	 * @throw DataAccessErrorException when a problem with DB connection or operation occurred
	 * @throw DataAccessConstraintException when such a link already exist
	 */
	public void removeAssociation(IUserProfileData activeUser, T managedData,  A associatedData) 
								throws DataAccessErrorException,DataAccessConstraintException;
}
