package metaindex.app.periodic.statistics.user;

import metaindex.app.periodic.statistics.AMxStatisticItem;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class ErrorOccuredMxStat extends AMxStatisticItem {

	public ErrorOccuredMxStat(IUserProfileData u, String errorType) {
		super(u); 
		this.setProperty("error-type", errorType);
	}
	@Override
	public String getName() {
		return "error";
	}
	
	
}
