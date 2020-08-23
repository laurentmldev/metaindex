package metaindex.data.commons.globals.plans;

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

import metaindex.app.Globals;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public class PlansManager implements IPlansManager {
	
	private Map<Integer,IPlan> _plans = new java.util.concurrent.ConcurrentHashMap<>();
	
	@Override
	public void loadFromDb() throws DataProcessException {
		class PlansHandler implements IStreamHandler<IPlan> {
			@Override public void handle(List<IPlan> loadedPlans) {
				Iterator<IPlan> it = loadedPlans.iterator();
				while (it.hasNext()) {
					IPlan curPlan = it.next();
					_plans.put(curPlan.getId(),curPlan);
		}}}
		Globals.Get().getDatabasesMgr().getPlansDbInterface().getLoadFromDbStmt().execute(new PlansHandler());
		
	}
	
	@Override
	public IPlan getPlan(Integer PlanId) {
		return _plans.get(PlanId);
	}
	@Override
	public Collection<IPlan> getPlans() {
		return _plans.values();
	}
}
