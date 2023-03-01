package toolbox.utils.statistics;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//TODO : remove dep to metaindex classes
import metaindex.app.Globals;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.PeriodicProcessMonitor;



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
	public void stop() {
		_periodicFlushMonitor.stopMonitoring();		
	}
	public void handleStatItem(IStatisticItem s) throws DataProcessException {
		
		if (!Globals.isInServerMode()) { return; }
		try {
			
			this.acquireLock();		
			if (!_inMemoryStats.containsKey(s.getName())) {
				_inMemoryStats.put(s.getName(), new ArrayList<>());
			}
			_inMemoryStats.get(s.getName()).add(s);			
			this.releaseLock();
		} catch (InterruptedException e) {
			this.releaseLock();
			throw new DataProcessException("unable to handle new "+s.getName()+" statistic item",e);					
		}	
		
	}

	public List<IStatisticItem> getStatItemsByName(String statName) {
		try {			
			this.acquireLock();		
			List<IStatisticItem> values = _inMemoryStats.get(statName);		
			this.releaseLock();
			return values;
		} catch (InterruptedException e) {
			this.releaseLock();
			log.error("unable to get "+statName+" statistic item, returned empty result",e);
			return new ArrayList<>();
		}	
		
	}
	public Set<String> getStatItemsNames() {
		try {			
			this.acquireLock();		
			Set<String> values = _inMemoryStats.keySet();		
			this.releaseLock();
			return values;
		} catch (InterruptedException e) {
			this.releaseLock();
			log.error("unable to get statistic items keyset, returned empty result",e);
			return new HashSet<String>();
		}			
	}
	@Override
	public Date getLastUpdate() { return _lastUpdate; }
	
	@Override
	public Boolean shallBeProcessed(Date testedUpdateDate) { return true; }
	
	@Override
	/**
	 * clear contents currently stored. Any processing must thus be done before invoking 
	 * this implementation of the method.
	 */
	public void doPeriodicProcess() throws DataProcessException {
		try {			
			this.acquireLock();
			_inMemoryStats.clear();
			_lastUpdate=new Date();
			this.releaseLock();
		} catch (InterruptedException e) {
			this.releaseLock();
			log.error("unable to reset statistics table",e);
		}
						
	}
	@Override
	public String getDetailsStr() {
		String statsStr=this.getName()+" since "+_lastUpdate+":";
		try {			
			this.acquireLock();					
			// For each stat name, show number and last date		
			for (String statName : _inMemoryStats.keySet()) {
				statsStr+="\n\t"+statName+":"+_inMemoryStats.get(statName).size();
			}
			this.releaseLock();
		} catch (InterruptedException e) {
			this.releaseLock();
			log.error("unable to get statistic items keyset (2)",e);
		}
		return statsStr;
	}
	
	public String getDetailsHtml() {
		String statsStr="<table>";
		statsStr+="<tr><th>Name</th><th>Nb Detections</th></tr>";
		// For each stat name, show number and last date
		try {
			this.acquireLock();	
			for (String statName : _inMemoryStats.keySet()) {
				statsStr+="<tr><td>"+statName+"</td><td>"+_inMemoryStats.get(statName).size()+"</td></tr>";
			}
			this.releaseLock();
		} catch (InterruptedException e) {
			this.releaseLock();
			log.error("unable to get statistic items keyset (3)",e);
		}
		
		statsStr+="</table>";
		return statsStr;
	}

}
