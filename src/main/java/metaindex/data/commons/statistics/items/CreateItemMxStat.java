package metaindex.data.commons.statistics.items;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class CreateItemMxStat extends AItemMxStatisticItem {

	public CreateItemMxStat(IUserProfileData u, ICatalog c, String itemRef) {
		super(u,c,itemRef); 		
	}
	@Override
	public String getName() {
		return "items.create-item";
	}	
}
