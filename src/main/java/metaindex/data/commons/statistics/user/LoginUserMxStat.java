package metaindex.data.commons.statistics.user;

import metaindex.data.commons.statistics.AMxStatisticItem;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class LoginUserMxStat extends AMxStatisticItem {

	public LoginUserMxStat(IUserProfileData u) {
		super(u); 
		
	}
	@Override
	public String getName() {
		return "user.login";
	}
	
	
}
