package metaindex.app.periodic.statistics.items;

import metaindex.app.periodic.statistics.catalog.ACatalogMxStatisticItem;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class GraphDownloadMxStat extends ACatalogMxStatisticItem {

	public GraphDownloadMxStat(IUserProfileData u, ICatalog c) {
		super(u,c); 		
	}
	@Override
	public String getName() {
		return "items.graph-download";
	}	
}
