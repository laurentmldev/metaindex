package metaindex.app.periodic.statistics.user;

import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class UpdatePlanPaymentMxStat extends UpdatePlanRequestMxStat {

	
	public UpdatePlanPaymentMxStat(IUserProfileData u, String transactionId, 
							Integer curPlanId,Integer newPlanId, Float cost) {
		super(u,transactionId,curPlanId,newPlanId,cost); 
	}
	@Override
	public String getName() {
		return "user.update-plan-payment";
	}
	
	
}
