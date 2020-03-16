package metaindex.app.periodic.statistics.terms;

import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class UpdateLexicTermMxStat extends ATermMxStatisticItem {

	public UpdateLexicTermMxStat(IUserProfileData u, ICatalog c, String termRef) {
		super(u,c,termRef); 		
	}
	@Override
	public String getName() {
		return "terms.update-lexic";
	}	
}
