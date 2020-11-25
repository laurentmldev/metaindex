package metaindex.app.periodic.fs;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
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
 * Cleaning Tmp Folder from files older than x minutes
 * @author laurentml
 *
 */
public class MxTmpFolderMonitor implements IPeriodicProcess {

		public static final Long TMP_FILE_TTL_SEC=600L;
		
		private Log log = LogFactory.getLog(MxTmpFolderMonitor.class);
		public static final Integer AUTOREFRESH_PERIOD_SEC=5;
		private Integer _autoRefreshPeriodSec=AUTOREFRESH_PERIOD_SEC;
		
		private PeriodicProcessMonitor _periodicCleanMonitor=new PeriodicProcessMonitor(this);
		private Semaphore _tmpFsLock = new Semaphore(1,true);
		public void acquireLock() throws InterruptedException { _tmpFsLock.acquire(); }
		public void releaseLock() { _tmpFsLock.release(); }
		private Date _lastUpdate=new Date(0);
		
		public void start() {
			log.info("Starting tmp-files cleaner over '"+Globals.Get().getWebappsTmpFsPath()+"' (Tmp Files TTL = "+TMP_FILE_TTL_SEC+"s)");
			_periodicCleanMonitor.start();				
		}
		public void stop() {
			log.info("Stopping tmp-files cleaner over '"+Globals.Get().getWebappsTmpFsPath()+"'");
			_periodicCleanMonitor.stopMonitoring();				
		}
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
			// scan tmp folder for files older than x minutes and remove them
			Date now = new Date();
			//log.info("Cleaning tmp folder");
			String webappsTmpFolderPath = Globals.Get().getWebappsTmpFsPath();
			File tmpFolder = new File(webappsTmpFolderPath);
			if (!tmpFolder.exists()) {
				if (!tmpFolder.mkdirs()) {
					log.error("unable to create tmp files folder"+webappsTmpFolderPath);
					return;
				}
			}

			for (File curFile : tmpFolder.listFiles()) {
				
				Long fileTimestampMs = new Date(curFile.lastModified()).getTime();
				Long nowTimestampMs = now.getTime(); 
				if (nowTimestampMs-fileTimestampMs > TMP_FILE_TTL_SEC*1000) {
					Boolean rst = curFile.delete();
					if (!rst) { log.info("Unable to remove old tmp file "+curFile.getName()); }
					log.info("cleaning old tmp file "+curFile.getName());
				}				
			}
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

