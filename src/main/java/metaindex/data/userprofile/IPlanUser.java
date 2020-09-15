package metaindex.data.userprofile;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Date;
import metaindex.data.commons.globals.plans.IPlan;


/**
 * Java object container for users DB table.
 * Retrieve also String info of corresponding foreign keys (guilanguage and guitheme).
 * @author Laurent ML
 */
public interface IPlanUser 
{
	// plan info
	public Integer getPlanId();
	public void setPlanId(Integer planId);
	public IPlan getPlan();
	public Date getPlanStartDate();
	/**
	 * count how many quota warnings have been detected.
  	 * user account can then be disabled once max allowed warnings has been reached
	 */
	public Integer getPlanNbQuotaWarnings();
	public void setPlanNbQuotaWarnings(Integer nbWarnings);
	/** for GUI usage */
	public String getPlanStartDateStr();
	public void setPlanStartDate(Date planStartDate);
	public Date getPlanEndDate();
	/** for GUI usage */
	public String getPlanEndDateStr(); 
	public void setPlanEndDate(Date planEndDate);


}
