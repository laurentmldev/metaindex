package metaindex.app.listeners;


import java.util.List;

/*
GNU GENERAL PUBLIC LICENSE
 Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;

public class MetaindexContextListener implements ServletContextListener{

	private Log log = LogFactory.getLog(MetaindexContextListener.class);
	

    //Run this before web application is started
	 @Override
	 public void contextInitialized(ServletContextEvent event) {
	 	//try {
	 		log.info("Initializing MetaindeX context");
			ServletContext context = event.getServletContext();
			Globals.Get().setWebappsFsPath(context.getRealPath("/").replaceAll("[^/]+/$", ""));					

	        log.info(Globals.Get().getDetailsStr());
	 
	 }
	 
    @Override
    public void contextDestroyed(ServletContextEvent event) {   
    	
    	log.info("Shuting down MetaindeX context ...");
    	
    	try {
    		List<IUserProfileData> users = Globals.Get().getUsersMgr().getUsersList();
    		for (IUserProfileData u : users) {
    			// does not work ... ?!
    			if (u.isLoggedIn()) {
    				u.sendGuiWarningMessage(u.getText("globals.systemShutdown"));
    			}
    		}
    	}
		catch (Throwable t) {
			log.error("Unable to send shutdown notification to all users : "+t.getMessage());
		}    	
    	
    	List<ICatalog> catalogs = Globals.Get().getCatalogsMgr().getCatalogsList();
    	for (ICatalog c : catalogs) {
    		try { c.stopServices(); }
    		catch (Throwable t) {
    			log.error("Unable to stop services of catalog '"+c.getName()+"' : "+t.getMessage());
    		}    		
    	}
    	
    	try {
    		List<IUserProfileData> users = Globals.Get().getUsersMgr().getUsersList();
    		for (IUserProfileData u : users) {
    			if (u.isLoggedIn()) { u.logOut(); }
    		}
    	}
		catch (Throwable t) {
			log.error("Unable to logout some users : "+t.getMessage());
		}    		
	
    	
    	try { Globals.Get().stop(); }
		catch (Throwable t) {
			log.error("Unable to stop global resources : "+t.getMessage());
		}
    	
        log.info("MetaindeX context destroyed, bye bye.");
    }

}