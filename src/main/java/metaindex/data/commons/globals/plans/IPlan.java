package metaindex.data.commons.globals.plans;

import java.util.Date;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.commons.globals.plans.IPlan;
import metaindex.data.userprofile.IUserProfileData.CATEGORY;
import toolbox.utils.IIdentifiable;
import toolbox.utils.IPeriodicProcess;

public interface IPlan extends IIdentifiable<Integer>, IPeriodicProcess {
	
	public static final Integer PLANS_DURATION_MONTHS = 12;
	
	public void setId(Integer id);
	public void setName(String name);
	
	public Boolean getAvailableForPurchase();
	public void setAvailableForPurchase(Boolean value);
	
	public Integer getQuotaCatalogsCreated();
	public void setQuotaCatalogsCreated(Integer nbCatalogs);
	
	public Long getQuotaNbDocsPerCatalog();
	public void setQuotaNbDocsPerCatalog(Long nbDocs);
	
	public Long getQuotaDriveBytesPerCatalog();
	public void setQuotaDriveBytesPerCatalog(Long nbBytes);
	
	public Float getYearlyCostEuros();
	public void setYearlyCostEuros(Float cost);
	
	public void setLastUpdate(Date newDate);
	
	public CATEGORY getCategory();
	public void setCategory(CATEGORY c);
	
}
