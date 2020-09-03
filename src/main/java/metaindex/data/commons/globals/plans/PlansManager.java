package metaindex.data.commons.globals.plans;

import java.util.ArrayList;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UsersManager;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public class PlansManager implements IPlansManager {
	
	private Log log = LogFactory.getLog(PlansManager.class);
	private Map<Integer,IPlan> _plans = new java.util.concurrent.ConcurrentHashMap<>();
	private Semaphore _plansLock = new Semaphore(1,true);
	
	@Override
	public void loadFromDb() throws DataProcessException {
		try {
			_plansLock.acquire();
			List<IPlan> loadedPlans=new ArrayList<>();
			for (IPlan p : _plans.values()) {
				loadedPlans.add(p);
			}
			Globals.Get().getDatabasesMgr().getPlansDbInterface().getLoadFromDbStmt(loadedPlans).execute();
			for (IPlan p : loadedPlans) {
				_plans.put(p.getId(), p);
			}
			_plansLock.release();
		} catch (Exception e) {
			log.error("Unable to load contents of Plans from DB "+e.getMessage());
			_plansLock.release();
			throw new DataProcessException(e); 
		}		
		
	}
	
	protected void loadNewPlansFromDb() throws DataProcessException {

		try {
			_plansLock.acquire();
			List<IPlan> loadedPlans=new ArrayList<>();
			for (IPlan p : _plans.values()) {
				loadedPlans.add(p);
			}
			Globals.Get().getDatabasesMgr().getPlansDbInterface().getLoadNewPlansFromDbStmt(loadedPlans).execute();
			for (IPlan p : loadedPlans) {
				_plans.put(p.getId(), p);
			}
			_plansLock.release();
		} catch (Exception e) {
			log.error("Unable to load contents of Plans from DB "+e.getMessage());
			_plansLock.release();
			throw new DataProcessException(e); 
		}		
		
	}
	
	
	@Override
	public IPlan getPlan(Integer planId) {
		IPlan plan = null;
		
		if (!_plans.containsKey(planId)) {
			try {
				loadNewPlansFromDb();
			} catch (DataProcessException e) {
				log.error("Unable to load new plans : "+e.getMessage());
				e.printStackTrace();
			}
		}
		plan=_plans.get(planId);
	
		return plan;
	}
	@Override
	public Collection<IPlan> getPlans() {
		return _plans.values();
	}
}
