package metaindex.data.commons.statistics.filters;


import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.statistics.AMxStatisticItem;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public abstract class AFilterMxStatisticItem extends AMxStatisticItem {
	
	public AFilterMxStatisticItem(IUserProfileData u,ICatalog c) {
		// hash the user id to ensure no nominal statistic is possible
		super(u);
		this.setProperty("catalog", hashString(c.getName())); 
	}
	
}
