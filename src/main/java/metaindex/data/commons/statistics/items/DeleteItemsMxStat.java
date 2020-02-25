package metaindex.data.commons.statistics.items;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class DeleteItemsMxStat extends AItemMxStatisticItem {

	public DeleteItemsMxStat(IUserProfileData u, ICatalog c,Integer nbItems) {
		super(u,c,"");
		this.setProperty("nbItems", nbItems);
	}
	@Override
	public String getName() {
		return "items.delete-items";
	}	
}
