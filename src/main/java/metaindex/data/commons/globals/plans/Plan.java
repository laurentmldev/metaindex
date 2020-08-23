package metaindex.data.commons.globals.plans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import metaindex.data.commons.globals.plans.IPlan;
import toolbox.utils.IIdentifiable;

public class Plan implements IPlan {
	
	private Integer _id;
	private String _name;
	private Integer _catalogsCreatedQuota;
	private Integer _docsQuotaPerCatalog;
	private Long _dicsBytesQuotaPerCatalog;
	private Float _yearlyCost=0.0F;
	
	@Override
	public Integer getId() { return _id; }
	@Override
	public void setId(Integer id) { _id=id; }	
	
	@Override
	public String getName() { return _name; }
	@Override
	public void setName(String name) { _name=name; }
	
	@Override
	public Integer getQuotaCatalogsCreated() { return _catalogsCreatedQuota; }
	@Override
	public void setQuotaCatalogsCreated(Integer nbCatalogs) { _catalogsCreatedQuota=nbCatalogs; }
	
	@Override
	public Integer getQuotaNbDocsPerCatalog() { return _docsQuotaPerCatalog; }
	@Override
	public void setQuotaNbDocsPerCatalog(Integer nbDocs) { _docsQuotaPerCatalog=nbDocs; }
	
	@Override
	public Long getQuotaDiscBytesPerCatalog() { return _dicsBytesQuotaPerCatalog; }
	@Override
	public void setQuotaDiscBytesPerCatalog(Long nbBytes) { _dicsBytesQuotaPerCatalog=nbBytes; }
	
	@Override
	public Float getYearlyCostEuros() { return _yearlyCost; }
	@Override
	public void setYearlyCostEuros(Float cost) { _yearlyCost=cost; }
}
