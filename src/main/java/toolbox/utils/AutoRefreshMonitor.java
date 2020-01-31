package toolbox.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import toolbox.exceptions.DataProcessException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/**
 * Implements auto-refresh mechanism to synchronize an object to DB contents
 * if change detected based on DB timestamp. 
 * @author laurentml
 *
 */
public class AutoRefreshMonitor extends Thread implements IRunnable {	
	
	private Log log = LogFactory.getLog(AutoRefreshMonitor.class);
	
	IAutoRefresh _objToRefresh=null;
	Boolean _continueRunning=true;
	
	public  AutoRefreshMonitor(IAutoRefresh obj) {
		_objToRefresh=obj;
	}
	
	/// to be overriden if needed
	public Boolean preRefreshTest() { return true; }
	
	@Override
	public void run() {

		while (_continueRunning) {
			try {
				Thread.sleep(_objToRefresh.getAutoRefreshPeriodSec()*1000);
				
				if (preRefreshTest()) {
					_objToRefresh.acquireLock();
					Boolean wasUpdated = _objToRefresh.updateContentsIfNeeded();
					if (wasUpdated) {
						log.info("Reloaded DB-Data for Catalog "+_objToRefresh.getDetailsStr() );
					} 
					_objToRefresh.releaseLock();
				}
				
			} catch (InterruptedException|DataProcessException  e) {
				log.error("While performing cyclic update check on "+_objToRefresh.getId()+ ": "+e.getMessage());
				e.printStackTrace();
				_continueRunning=false;
				_objToRefresh.releaseLock();
			}
		}
	}

	@Override
	public Boolean isRunning() {
		return _continueRunning==true && this.isAlive();
	}

	
}
