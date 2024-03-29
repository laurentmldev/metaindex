package metaindex.app.periodic.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.utils.PeriodicProcessMonitor;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/**
 * Implements auto-refresh mechanism to synchronize a catalog to DB contents
 * if change detected based on DB timestamp. 
 * @author laurentml
 *
 */
public class UserProfilePeriodicDbReloader extends PeriodicProcessMonitor {	
	
	private Log log = LogFactory.getLog(CatalogPeriodicDbReloader.class);
	
	public UserProfilePeriodicDbReloader(IUserProfileData c) {
		super(c);	
	}
	
}
