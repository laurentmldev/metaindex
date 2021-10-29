package toolbox.database;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IProcessingTask;

public interface IDbItemsProcessor extends IProcessingTask   {
	
	public IUserProfileData getActiveUser();
	
	public void lock() throws InterruptedException;
	public void unlock();
	
	/** decide depending on contents if each item shall be created or updated
	 * (typically depending if an 'id' field is listed and already present in DB */
	public void postDataToIndexOrUpdate(List<IDbItem> d) throws DataProcessException;
	
	/** Create given new document */
	public void postDataToIndex(List<IDbItem> d) throws DataProcessException;
	/** Update document or create it with requested id if it does not exist */
	public void postDataToUpdate(List<IDbItem> d) throws DataProcessException;
	public void postDataToDelete(List<IDbItem> d) throws DataProcessException;
	
};
