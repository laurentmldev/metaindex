package metaindex.app.periodic.statistics.filters;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class DeleteFilterMxStat extends AFilterMxStatisticItem {

	public DeleteFilterMxStat(IUserProfileData u, ICatalog c) {
		super(u,c); 		
	}
	@Override
	public String getName() {
		return "filters.delete-filter";
	}	
}
