package metaindex.data.catalog.dbinterface;

import java.util.ArrayList;
import java.util.List;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;
import toolbox.database.kibana.KibanaConnector;
import toolbox.database.kibana.KibanaConnector.KIBANA_PRIVILEGE;
import toolbox.database.kibana.KibanaConnector.KIBANA_SPACE_FEATURE;
import toolbox.exceptions.DataProcessException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class KibanaCatalogDbInterface  
{
	
	public static String GetRORoleName(ICatalog c) { return c.getName()+"_RO"; }
	public static String GetWRoleName(ICatalog c) { return c.getName()+"_W"; }
	
	KibanaConnector _kibanaConnector;
	public KibanaCatalogDbInterface(KibanaConnector connector) { 
		_kibanaConnector=connector;
	}
	
	
	public Boolean createStatisticsSpace(IUserProfileData activeUser,ICatalog c) {
		List<KIBANA_SPACE_FEATURE> disabledFeaturesList = new ArrayList<>();
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.indexPatterns);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.apm);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.dev_tools);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.infrastructure);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.ml);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.advancedSettings);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.logs);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.maps);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.canvas);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.graph);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.monitoring);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.savedObjectsManagement);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.siem);
		disabledFeaturesList.add(KIBANA_SPACE_FEATURE.uptime);
		
		return _kibanaConnector.createKibanaSpace(	Globals.GetMxProperty("mx.elk.user"), 
													Globals.GetMxProperty("mx.elk.passwd"), 
				c.getName(), "Statistics for "+c.getName(),
				"Perform statistics over contents of catalog "+c.getName(), 
				"#444499", 
				c.getName().charAt(0)+""+c.getName().charAt(c.getName().length()-1), 
				"", 
				disabledFeaturesList);
	}


	public Boolean deleteStatisticsSpace(IUserProfileData activeUser,ICatalog c) {
		return _kibanaConnector.deleteKibanaSpace(	Globals.GetMxProperty("mx.elk.user"), 
													Globals.GetMxProperty("mx.elk.passwd"), 
													c.getName());
	}

	public Boolean createStatisticsIndexPattern(IUserProfileData activeUser,ICatalog c) {
	
		return _kibanaConnector.createKibanaIndexPattern(Globals.GetMxProperty("mx.elk.user"),
											Globals.GetMxProperty("mx.elk.passwd"),
											c.getName(),
											c.getName(), c.getName(), 
											c.getTimeFieldRawName());				
	}

	public Boolean updateStatisticsTimeField(IUserProfileData activeUser,ICatalog c) {
		
		return _kibanaConnector.setKibanaIndexTimeField(Globals.GetMxProperty("mx.elk.user"),
											Globals.GetMxProperty("mx.elk.passwd"),
											c.getName(),
											c.getName(),/* c.getName(),*/ 
											c.getTimeFieldRawName());				
	}
	
	public Boolean refreshStatisticsIndexPattern(IUserProfileData activeUser,ICatalog c) {
		
		// could not find 'update index pattern' command in current Kibana REST API
		// for now just delete and re-create it.		
		Boolean rst = _kibanaConnector.deleteKibanaIndexPattern(Globals.GetMxProperty("mx.elk.user"),
				Globals.GetMxProperty("mx.elk.passwd"),
				c.getName(),
				c.getName());		
		
		if (!rst) { return false; }
		
		return createStatisticsIndexPattern(activeUser,c);
	}
	
	public Boolean deleteStatisticsIndexPattern(IUserProfileData activeUser,ICatalog c) {
		
		Boolean rst = _kibanaConnector.deleteKibanaIndexPattern(Globals.GetMxProperty("mx.elk.user"),
				Globals.GetMxProperty("mx.elk.passwd"),
				c.getName(),
				c.getName());		
		
		return rst;	
	}

	public Boolean createCatalogStatisticsRoles(IUserProfileData activeUser,ICatalog c) {
		List<String> indicesList = new ArrayList<String>();
		indicesList.add(c.getName());
		List<String> spacesList = new ArrayList<String>();
		spacesList.add(c.getName());
		List<String> featuresListStr = new ArrayList<String>();
		featuresListStr.add("dashboard");
		featuresListStr.add("discover");
		featuresListStr.add("visualize");
		
		Boolean rst1 = _kibanaConnector.createKibanaRole(Globals.GetMxProperty("mx.elk.user"),
												Globals.GetMxProperty("mx.elk.passwd"),
												GetRORoleName(c),
												indicesList,KIBANA_PRIVILEGE.read,
												spacesList,
												featuresListStr,KIBANA_PRIVILEGE.read);
		
		Boolean rst2 = _kibanaConnector.createKibanaRole(Globals.GetMxProperty("mx.elk.user"),
												Globals.GetMxProperty("mx.elk.passwd"),
												GetWRoleName(c),
												indicesList,KIBANA_PRIVILEGE.read,
												spacesList,
												featuresListStr,KIBANA_PRIVILEGE.all);
		
		return rst1 && rst2;
	}
	public Boolean deleteCatalogStatisticsRoles(IUserProfileData activeUser,ICatalog c) {
		Boolean rst1 = _kibanaConnector.deleteKibanaRole(Globals.GetMxProperty("mx.elk.user"),
				Globals.GetMxProperty("mx.elk.passwd"),
				GetRORoleName(c));
		
		Boolean rst2 = _kibanaConnector.deleteKibanaRole(Globals.GetMxProperty("mx.elk.user"),
				Globals.GetMxProperty("mx.elk.passwd"),
				GetWRoleName(c));
		
		return rst1 && rst2;
	}
	public Boolean createOrUpdateCatalogStatisticsUser(IUserProfileData activeUser) throws DataProcessException {
		
		List<String> rolesList = new ArrayList<String>();
		
		if (activeUser.getRole()==USER_ROLE.ROLE_OBSERVER) { rolesList.add("kibana_dashboard_only_user"); }
		// issue #23
		// kibana_user gives rights to configure the 'Kibana' app including deleting all spaces
		// but is necessary for proper use of Kibana for now
		// (see elasticsearch issue https://github.com/elastic/kibana/issues/51759)
		else { rolesList.add("kibana_user"); }

		for (Integer catId : activeUser.getUserCatalogsIds()) {
			ICatalog c = Globals.Get().getCatalogsMgr().getCatalog(catId);
			USER_CATALOG_ACCESSRIGHTS access = activeUser.getUserCatalogAccessRights(catId);
			if (access==USER_CATALOG_ACCESSRIGHTS.CATALOG_ADMIN || access==USER_CATALOG_ACCESSRIGHTS.CATALOG_EDIT) {
				rolesList.add(GetWRoleName(c));
			}
			else if (access==USER_CATALOG_ACCESSRIGHTS.CATALOG_READ) {
				rolesList.add(GetRORoleName(c));
			}
		}
		
		return Globals.Get().getDatabasesMgr().getUserProfileESDbInterface().getCreateOrUpdateUserStmt(activeUser, rolesList).execute();
	 
	}
}
