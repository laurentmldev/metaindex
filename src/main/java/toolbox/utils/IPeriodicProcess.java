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
 * Intended to be used with PeriodicProcessMonitor object.
 * @author laurentml
 *
 */
public interface IPeriodicProcess extends IIdentifiable<Integer>,ILockable {	
	
	public Date getLastUpdate();
	public Boolean shallBeProcessed(Date testedUpdateDate);
	/**
	 * Update contents of this object only if needed (depending on what returns shallBeRefreshed() method).
	 * When invoked, this object has already been acquired by the PeriodicProcessMonitor
	 * @return true if contents actually refreshed, false otherwise
	 * @throws DataProcessException
	 */
	public Boolean doPeriodicProcess() throws DataProcessException;
	public Integer getPeriodicProcessPeriodSec();
	
	/**
	 * Called by PeriodicProcessMonitor when doPeriodicProcess returns true;
	 * @return
	 */
	public String getDetailsStr();
}
