package metaindex.app.periodic.statistics.publicpages;

import metaindex.app.periodic.statistics.AMxStatisticItem;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class TutosPageMxStat extends AMxStatisticItem {

	public TutosPageMxStat(IUserProfileData u) {
		super(u); 
		
	}

	@Override
	public String getName() {
		return "publicpage.tutorials";
	}

	
}
