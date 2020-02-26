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
public class PeriodicProcessMonitor extends Thread implements IRunnable {	
	
	private Log log = LogFactory.getLog(PeriodicProcessMonitor.class);
	
	IPeriodicProcess _objToProcess=null;
	Boolean _continueRunning=true;
	
	public  PeriodicProcessMonitor(IPeriodicProcess obj) {
		_objToProcess=obj;
	}
	
	/// to be overriden if needed
	public Boolean prePeriodicProcessTest() { return true; }
	
	public void stopMonitoring() {
		_continueRunning=false;
	}
	
	@Override
	public void run() {

		while (_continueRunning) {
			try {
				Thread.sleep(_objToProcess.getPeriodicProcessPeriodSec()*1000);
				
				if (prePeriodicProcessTest()) {
					_objToProcess.acquireLock();
					_objToProcess.doPeriodicProcess();
					_objToProcess.releaseLock();
				}
				
			} catch (InterruptedException|DataProcessException  e) {
				log.error("While performing cyclic update check on "+_objToProcess.getId()+ ": "+e.getMessage());
				e.printStackTrace();
				_continueRunning=false;
				_objToProcess.releaseLock();
			}
		}
	}

	@Override
	public Boolean isRunning() {
		return _continueRunning==true && this.isAlive();
	}

	
}
