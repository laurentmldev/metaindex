package metaindex.app.periodic.statistics.user;

import metaindex.app.periodic.statistics.AMxStatisticItem;
import metaindex.data.userprofile.IUserProfileData;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class UpdatePlanRequestMxStat extends AMxStatisticItem {

	private String _transactionId;
	private Integer _curPlanId;
	private Integer _newPlanId;
	private Float _cost;
	
	public UpdatePlanRequestMxStat(IUserProfileData u, String transactionId, 
							Integer curPlanId,Integer newPlanId, Float cost) {
		super(u); 
	}
	@Override
	public String getName() {
		return "user.update-plan-request";
	}
	public String getTransactionId() {
		return _transactionId;
	}
	public void setTransactionId(String _transactionId) {
		this._transactionId = _transactionId;
	}
	public Integer getCurPlanId() {
		return _curPlanId;
	}
	public void setCurPlanId(Integer _curPlanId) {
		this._curPlanId = _curPlanId;
	}
	public Integer getNewPlanId() {
		return _newPlanId;
	}
	public void setNewPlanId(Integer _newPlanId) {
		this._newPlanId = _newPlanId;
	}
	public Float getCost() {
		return _cost;
	}
	public void setCost(Float _cost) {
		this._cost = _cost;
	}
	
	
}
