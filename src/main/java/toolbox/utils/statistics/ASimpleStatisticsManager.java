package toolbox.utils.statistics;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.catalog.Catalog;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPeriodicProcess;
import toolbox.utils.PeriodicProcessMonitor;
import toolbox.utils.statistics.IStatisticItem;
import toolbox.utils.statistics.IStatisticsManager;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public abstract class ASimpleStatisticsManager implements IStatisticsManager {	
	
	private Log log = LogFactory.getLog(ASimpleStatisticsManager.class);
	
	private PeriodicProcessMonitor _periodicFlushMonitor=new PeriodicProcessMonitor(this);
	private Semaphore _statsManagerLock = new Semaphore(1,true);
	public void acquireLock() throws InterruptedException { _statsManagerLock.acquire(); }
	public void releaseLock() { _statsManagerLock.release(); }
	
	private Date _lastUpdate=new Date();
	
	private Map<String, List<IStatisticItem> > _inMemoryStats = new java.util.concurrent.ConcurrentHashMap<>();
	
	public void start() {
		_periodicFlushMonitor.start();
	}
	public void handleStatItem(IStatisticItem s) throws DataProcessException {
		try {
			this.acquireLock();		
			if (!_inMemoryStats.containsKey(s.getName())) {
				_inMemoryStats.put(s.getName(), new java.util.concurrent.CopyOnWriteArrayList<>());
			}
			_inMemoryStats.get(s.getName()).add(s);			
			this.releaseLock();
		} catch (InterruptedException e) {
			this.releaseLock();
			throw new DataProcessException("unable to handle new "+s.getName()+" statistic item",e);					
		}	
	}

	public List<IStatisticItem> getStatItemsByName(String statName) {
		return _inMemoryStats.get(statName);
	}
	public Set<String> getStatItemsNames() {
		return _inMemoryStats.keySet();
	}
	@Override
	public Date getLastUpdate() { return _lastUpdate; }
	
	@Override
	public Boolean shallBeProcessed(Date testedUpdateDate) { return true; }
	
	@Override
	public Boolean doPeriodicProcess() throws DataProcessException {
		log.info(this.getDetailsStr());
		_inMemoryStats.clear();
		_lastUpdate=new Date();		
		return true;
	}
	@Override
	public String getDetailsStr() {
		String statsStr=this.getName()+" since "+_lastUpdate+":";
		// For each stat name, show number and last date		
		for (String statName : _inMemoryStats.keySet()) {
			statsStr+="\n\t"+statName+":"+_inMemoryStats.get(statName).size();
		}
				
		return statsStr;
	}

}
