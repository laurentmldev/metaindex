package metaindex.data.commons.globals.plans;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.app.periodic.db.PlansPeriodicDbReloader;
import metaindex.app.periodic.db.UserProfilePeriodicDbReloader;
import metaindex.data.catalog.Catalog;
import metaindex.data.commons.globals.plans.IPlan;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.PeriodicProcessMonitor;


public class Plan implements IPlan {
	
	private Log log = LogFactory.getLog(Plan.class);
	
	private Integer _id;
	private String _name;
	private Integer _catalogsCreatedQuota;
	private Long _docsQuotaPerCatalog;
	private Long _dicsBytesQuotaPerCatalog;
	private Float _yearlyCost=0.0F;
	private Boolean _availableForPurchase=false;
	
	private Date _lastUpdate;
	private Integer _autoRefreshPeriodSec=Catalog.AUTOREFRESH_PERIOD_SEC;
	
	private PeriodicProcessMonitor _dbAutoRefreshProcessing=null;
	
	public Plan() {
		_lastUpdate=new Date(0);
		_dbAutoRefreshProcessing=new PlansPeriodicDbReloader(this); 
		_dbAutoRefreshProcessing.start();
	}
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
	public Long getQuotaNbDocsPerCatalog() { return _docsQuotaPerCatalog; }
	@Override
	public void setQuotaNbDocsPerCatalog(Long nbDocs) { _docsQuotaPerCatalog=nbDocs; }
	
	@Override
	public Long getQuotaDiscBytesPerCatalog() { return _dicsBytesQuotaPerCatalog; }
	@Override
	public void setQuotaDiscBytesPerCatalog(Long nbBytes) { _dicsBytesQuotaPerCatalog=nbBytes; }
	
	@Override
	public Float getYearlyCostEuros() { return _yearlyCost; }
	@Override
	public void setYearlyCostEuros(Float cost) { _yearlyCost=cost; }
	
	@Override
	public Boolean getAvailableForPurchase() { return _availableForPurchase; }
	@Override
	public void setAvailableForPurchase(Boolean value) { _availableForPurchase=value; }
	@Override
	public Date getLastUpdate() {
		return _lastUpdate;
	}
	@Override 
	public void setLastUpdate(Date newDate) { _lastUpdate=newDate; }
	@Override
	public Boolean shallBeProcessed(Date testedUpdateDate) {
		return this.getLastUpdate().before(testedUpdateDate);
	}
	@Override
	public void doPeriodicProcess() throws DataProcessException {
		
		Date prevCurDate = this.getLastUpdate();
		Boolean onlyIfDbcontentsUpdated=true;
		List<IPlan> list = new ArrayList<>();
		list.add(this);		
		Globals.Get().getDatabasesMgr().getPlansDbInterface().getLoadFromDbStmt(list, onlyIfDbcontentsUpdated).execute();

		// detect if contents actually changed
		if (this.getLastUpdate().after(prevCurDate)) { 
			log.info(this.getDetailsStr());
		}
		
	}
	@Override
	public Integer getPeriodicProcessPeriodSec() {
		return _autoRefreshPeriodSec;
	}
	@Override
	public String getDetailsStr() {
		String str = "'"+this.getName()+"' :"
				+"\n\t- id: "+this.getId()
				+"\n\t- name: "+this.getName()
				+"\n\t- quotaCatalogsCreated: "+this.getQuotaCatalogsCreated()
				+"\n\t- quotaNbDocsPerCatalog: "+this.getQuotaNbDocsPerCatalog()
				+"\n\t- quotaDiscPerCatalog: "+(this.getQuotaDiscBytesPerCatalog()/1000000)+"MB"
				;
		
		return str;
	}
}
