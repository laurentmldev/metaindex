package metaindex.data.commons.globals.plans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Collection;
import java.util.List;

import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import toolbox.database.ILoadableFromDb;
import toolbox.exceptions.DataProcessException;

public interface IPlansManager extends ILoadableFromDb {

	
	
	void loadFromDb() throws DataProcessException;
	IPlan getPlan(Integer planId);
	Collection<IPlan> getPlans();
	IPlan getDefaultPlan(CATEGORY c);
}
