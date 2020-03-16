package metaindex.app.periodic.fs;

import java.util.Date;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPeriodicProcess;
import toolbox.utils.PeriodicProcessMonitor;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/**
 * Cleaning Temp Folder from files older than x minutes
 * @author laurentml
 *
 */
public class TempFolderCleaningProcess implements IPeriodicProcess {

		private Log log = LogFactory.getLog(TempFolderCleaningProcess.class);
		public static final Integer AUTOREFRESH_PERIOD_SEC=5;
		private Integer _autoRefreshPeriodSec=AUTOREFRESH_PERIOD_SEC;
		
		private Semaphore _tmpFsLock = new Semaphore(1,true);
		public void acquireLock() throws InterruptedException { _tmpFsLock.acquire(); }
		public void releaseLock() { _tmpFsLock.release(); }
		private Date _lastUpdate=new Date(0);
		
		@Override
		public String getName() {
			return "MX Temp Folder Cleaner";
		}

		@Override
		public Integer getId() {
			return 0;
		}

		@Override
		public Date getLastUpdate() {
			return _lastUpdate;
		}

		@Override
		public Boolean shallBeProcessed(Date testedUpdateDate) {
			return true;
		}

		@Override
		public void doPeriodicProcess() throws DataProcessException {
			// TODO :
			// scan tmp folder for files older than x minutes and remove them
			log.info("CLeaning temp folder");
			
		}

		@Override
		public Integer getPeriodicProcessPeriodSec() {
			return _autoRefreshPeriodSec;
		}

		@Override
		public String getDetailsStr() {
			return "";
		}
		
		
	}

