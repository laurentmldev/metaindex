package metaindex.app.periodic.statistics.items;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class DeleteItemsByQueryMxStat extends DeleteItemsMxStat {

	public DeleteItemsByQueryMxStat(IUserProfileData u, ICatalog c,Integer nbItems) {
		super(u,c,nbItems);		
	}
	@Override
	public String getName() {
		return "items.delete-items-by-query";
	}	
}
