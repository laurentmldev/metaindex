package metaindex.app.periodic.monitoring;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.commons.globals.plans.IPlan;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPeriodicProcess;
import toolbox.utils.PeriodicProcessMonitor;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

/**
 * When plans constraints are overpassed, signal user and takes required actions 
 * @author laurentml
 *
 */
public class UsersQuotasChecker implements IPeriodicProcess {

	private Log log = LogFactory.getLog(UsersQuotasChecker.class);
	
	private PeriodicProcessMonitor _periodicMonitor=new PeriodicProcessMonitor(this);
	
	// run once a day
	public static final Integer USERS_QUOTA_CHECK_PERIOD_DAY = 2;
	public static final Integer USERS_MAX_QUOTA_WARNINGS = 90;
	private Integer _id=666;
	private String _name="Users Quotas Checker";
	private Date _lastUpdate=new Date();	
	private Integer _nbUsersChecked=0;
	private Integer _nbUsersFrozen=0;
	private Integer _nbUsersOverQuota=0;
	private Integer _nbUsersOverQuotaCatalogs=0;
	private Integer _nbUsersOverQuotaDocs=0;
	private Integer _nbUsersOverQuotaDrive=0;
	
	public void start() {
		log.info("Starting monitoring of users quota every "+USERS_QUOTA_CHECK_PERIOD_DAY+" day(s)");
		_periodicMonitor.start();				
	}
	public void stop() {
		log.info("Starting monitoring of users quota");
		_periodicMonitor.stopMonitoring();				
	}
	
	@Override
	public Integer getId() {
		return _id;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Date getLastUpdate() {
		return _lastUpdate;
	}

	@Override
	public Boolean shallBeProcessed(Date testedUpdateDate) {
		return true;
	}

	@Override
	public void doPeriodicProcess() throws DataProcessException {
		Integer nbUsersChecked=0;
		Integer nbFrozen=0;
		Integer nbOverQuota=0;
		Integer nbQuotaCats=0;
		Integer nbQuotaDocs=0;
		Integer nbQuotaDrive=0;
		// for each user loaded in memory, ensure quotas are respected
		// if not, send warning emails and worst case disable account and catalogs.
		for (IUserProfileData u : Globals.Get().getUsersMgr().getUsersList()) {
			
			// ignore already disabled user accounts
			if (u.isEnabled()==false) { 
				nbFrozen++;
				continue; 
			}
			nbUsersChecked++;
			
			IPlan userPlan = u.getPlan();
			
			Boolean overQuota=false;
			Boolean tooManyCatalogs=false;
			String catalogsOverQuotaListStr="";
			// check for over-quota catalogs
			for (ICatalog c : u.getOwnedCatalogs()) {		
				Boolean overQuotaContents = false;
				if (userPlan.getQuotaNbDocsPerCatalog()<c.getNbDocuments()) {
					nbQuotaDocs++;
					overQuotaContents=true;
				}
				if (userPlan.getQuotaDriveMBytesPerCatalog()<c.getDriveUseMBytes()) {
					nbQuotaDrive++;
					overQuotaContents=true;
				}
				if (overQuotaContents) {
					overQuota=true;
					if (catalogsOverQuotaListStr.length()>0) { catalogsOverQuotaListStr+=", "; }
					catalogsOverQuotaListStr+=c.getName();					
				}
			}
			
			// check amount of owned catalogs
			if (userPlan.getQuotaCatalogsCreated()<u.getCurNbCatalogsCreated()) {
				overQuota=true;
				tooManyCatalogs=true;
				nbQuotaCats++;
			}
			
			if (overQuota==false) {
				if (u.getPlanNbQuotaWarnings()>0) {
					u.setPlanNbQuotaWarnings(0);
					Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
							.getCreateOrUpdatePlanIntoDbStmt(u).execute();
					if (result==false) {
		    			log.error("Unable to set nb quota-warnings to '"+u.getPlanNbQuotaWarnings()+"' "
		    							+"into database for user "+u.getId());	    			
		    		}
				}
				continue;
			}
			
			nbOverQuota++;
			u.setPlanNbQuotaWarnings(u.getPlanNbQuotaWarnings()+1); 
			Boolean result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
					.getCreateOrUpdatePlanIntoDbStmt(u).execute();
			if (result==false) {
    			log.error("Unable to set nb quota-warnings to '"+u.getPlanNbQuotaWarnings()+"' "
    							+"into database for user "+u.getId());	    			
    		}
			
			if (u.getPlanNbQuotaWarnings()>=USERS_MAX_QUOTA_WARNINGS) {
				u.setEnabled(false);
				result = Globals.Get().getDatabasesMgr().getUserProfileSqlDbInterface()
														.getUpdateUserProfileIntoDbStmt(u).execute();	    		
	    		if (result==false) {
	    			log.error("Unable to disable user '"+u.getId()+"' into database");
	    		}
	    		try {
	    			u.sendEmailCCiAdmin(u.getText("Profile.plans.email.disabledAccountOverQuota.title"), 
						   		   u.getText("Profile.plans.email.disabledAccountOverQuota.body",
								   u.getNickname(),
								   Globals.Get().getWebAppBaseUrl()
								   ));
	    		} catch (DataProcessException e) {
	    			log.error("Unable to send 'disabled account' email to user "+u.getId());
	    		}
	    		
			} else {
				Integer remainingDaysBeforeFreeze=(USERS_MAX_QUOTA_WARNINGS-u.getPlanNbQuotaWarnings())*USERS_QUOTA_CHECK_PERIOD_DAY;
				if (catalogsOverQuotaListStr.length()>0) {
					try {
						u.sendEmailCCiAdmin(u.getText("Profile.plans.email.warningCatalogsOverQuota.title"), 
	    						   u.getText("Profile.plans.email.warningCatalogsOverQuota.body",
	    								   u.getNickname(),
	    								   catalogsOverQuotaListStr,
	    								   remainingDaysBeforeFreeze.toString(),
	    								   Globals.Get().getWebAppBaseUrl()
	    								   ));
					} catch (DataProcessException e) {
		    			log.error("Unable to send 'quota warning' email to user "+u.getId());
		    		}
	    		}
				if (tooManyCatalogs==true) {
					try {
						u.sendEmailCCiAdmin(u.getText("Profile.plans.email.warningTooManyCatalogs.title"), 
 						   u.getText("Profile.plans.email.warningTooManyCatalogs.body",
 								   u.getNickname(),
 								   u.getCurNbCatalogsCreated().toString(),
 								   u.getPlan().getName(),
 								   u.getPlan().getQuotaCatalogsCreated().toString(),
 								   remainingDaysBeforeFreeze.toString(),
 								   Globals.Get().getWebAppBaseUrl()));
					} catch (DataProcessException e) {
		    			log.error("Unable to send 'nbCatalogs quota warning' email to user "+u.getId());
		    		}
				}
			}
		}
		
		setNbUsersChecked(nbUsersChecked);
		setNbUsersOverQuota(nbOverQuota);		
		setNbUsersOverQuotaCatalogs(nbQuotaCats);
		setNbUsersOverQuotaDocs(nbQuotaDocs);
		setNbUsersOverQuotaDrive(nbQuotaDrive);
		log.info(getDetailsStr());
	}
	

	@Override
	public Integer getPeriodicProcessPeriodSec() {
		return USERS_QUOTA_CHECK_PERIOD_DAY * 3600*24;
	}

	@Override
	public String getDetailsStr() {
		return "\tnb users checked : "+getNbUsersChecked()+"\n"
				+"\tnb frozen : "+getNbUsersFrozen()+"\n"
				+"\tnb over quota : "+getNbUsersOverQuota()+"\n"
					+"\t\tnb catalogs : "+getNbUsersOverQuotaCatalogs()+"\n"
					+"\t\tnb docs : "+getNbUsersOverQuotaDocs()+"\n"
					+"\t\tnb drives : "+getNbUsersOverQuotaDrive()+"\n"			
				;
	}

	public Integer getNbUsersChecked() {
		return _nbUsersChecked;
	}

	public void setNbUsersChecked(Integer _nbUsersChecked) {
		this._nbUsersChecked = _nbUsersChecked;
	}

	public Integer getNbUsersFrozen() {
		return _nbUsersFrozen;
	}

	public void setNbUsersFrozen(Integer _nbUsersFrozen) {
		this._nbUsersFrozen = _nbUsersFrozen;
	}

	public Integer getNbUsersOverQuotaCatalogs() {
		return _nbUsersOverQuotaCatalogs;
	}

	public void setNbUsersOverQuotaCatalogs(Integer _nbUsersOverQuotaCatalogs) {
		this._nbUsersOverQuotaCatalogs = _nbUsersOverQuotaCatalogs;
	}

	public Integer getNbUsersOverQuotaDocs() {
		return _nbUsersOverQuotaDocs;
	}

	public void setNbUsersOverQuotaDocs(Integer _nbUsersOverQuotaDocs) {
		this._nbUsersOverQuotaDocs = _nbUsersOverQuotaDocs;
	}

	public Integer getNbUsersOverQuotaDrive() {
		return _nbUsersOverQuotaDrive;
	}

	public void setNbUsersOverQuotaDrive(Integer _nbUsersOverQuotaDrive) {
		this._nbUsersOverQuotaDrive = _nbUsersOverQuotaDrive;
	}

	public Integer getNbUsersOverQuota() {
		return _nbUsersOverQuota;
	}

	public void setNbUsersOverQuota(Integer _nbUsersOverQuota) {
		this._nbUsersOverQuota = _nbUsersOverQuota;
	}

}
