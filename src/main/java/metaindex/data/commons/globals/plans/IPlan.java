package metaindex.data.commons.globals.plans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.commons.globals.plans.IPlan;
import toolbox.utils.IIdentifiable;

public interface IPlan extends IIdentifiable<Integer> {
	
	public static final Integer PLANS_DURATION_MONTHS = 12;
	
	public void setId(Integer id);
	public void setName(String name);
	
	public Integer getQuotaCatalogsCreated();
	public void setQuotaCatalogsCreated(Integer nbCatalogs);
	
	public Integer getQuotaNbDocsPerCatalog();
	public void setQuotaNbDocsPerCatalog(Integer nbDocs);
	
	public Long getQuotaDiscBytesPerCatalog();
	public void setQuotaDiscBytesPerCatalog(Long nbBytes);
	
	public Float getYearlyCostEuros();
	public void setYearlyCostEuros(Float cost);
	
}
