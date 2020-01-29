package toolbox.utils;

import java.util.Date;

import toolbox.exceptions.DataProcessException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/**
 * Gets an auto-refresh mechanism, typically monitoring DB contents for reload when changed
 * @author laurentml
 *
 */
public interface IAutoRefresh {	
	
	public Date getLastUpdate();
	public Boolean shallBeRefreshed(Date testedUpdateDate);
	/**
	 * Update contents of this object only if needed (depending on what returns shallBeRefreshed() method).
	 * @return true if contents actually refreshed, false otherwise
	 * @throws DataProcessException
	 */
	public Boolean updateContentsIfNeeded() throws DataProcessException;
	public Integer getAutoRefreshPeriodSec();
	
}
