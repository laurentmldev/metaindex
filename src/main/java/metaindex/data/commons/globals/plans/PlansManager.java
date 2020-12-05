package metaindex.data.commons.globals.plans;

import java.util.ArrayList;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.UsersManager;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IIdentifiable;
import toolbox.utils.IStreamHandler;

public class PlansManager implements IPlansManager {
	
	private Log log = LogFactory.getLog(PlansManager.class);
	
	// need to keep the plans ordered by Id, because we use this order
	// to display plans by importance
	// first one is the default plan
	private SortedMap<Integer,IPlan> _plans = new TreeMap<Integer,IPlan>();
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
	
	/**
	 * 
	 * Default plan shall be the one with smallest Id for its category
	 */
	@Override 
	public IPlan getDefaultPlan(CATEGORY c) {
		IPlan currentDefaultPlan = null;
		for (IPlan p : getPlans()) {
			if (p.getCategory().equals(c)) {
				if (currentDefaultPlan==null 
						|| p.getId()<currentDefaultPlan.getId()) { currentDefaultPlan=p; }
			}				
		}
		if (currentDefaultPlan!=null) { return currentDefaultPlan; }
		for (IPlan p : getPlans()) {
			if (p.getCategory().equals(CATEGORY.ALL)) { 
				if (currentDefaultPlan==null 
						|| p.getId()<currentDefaultPlan.getId()) { currentDefaultPlan=p; } 
			}
		}
		return currentDefaultPlan;
	}
}
