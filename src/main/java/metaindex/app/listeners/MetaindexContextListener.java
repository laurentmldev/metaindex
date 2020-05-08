package metaindex.app.listeners;

import java.io.File;

import javax.servlet.ServletContext;

/*
GNU GENERAL PUBLIC LICENSE
 Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import toolbox.exceptions.DataProcessException;

public class MetaindexContextListener implements ServletContextListener{

	private Log log = LogFactory.getLog(MetaindexContextListener.class);
	

    //Run this before web application is started
	 @Override
	 public void contextInitialized(ServletContextEvent event) {
	 	//try {
	 		log.info("Initializing MetaindeX context ... ");
			ServletContext context = event.getServletContext();
			Globals.Get().setWebappsFsPath(context.getRealPath("/").replaceAll("[^/]+/$", ""));					

	        log.info(Globals.Get().getDetailsStr());
	 
	 }
	 
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    	log.info("Terminating MetaindeX context ... ");
    	File userDataRootPath=new File(Globals.Get().getUserdataFsPath());
        if (userDataRootPath.exists()){ 
        	if (!userDataRootPath.delete()) {
        		log.error("##### UNABLE TO CLEAN USERDATA WEB-FOLDER : "+Globals.Get().getUserdataFsPath()+" #####");        		
        	} 
        	else {
        		log.info(" - cleaned userdata web-folder : "+Globals.Get().getUserdataFsPath());
        	}
        	
        }
        log.info("MetaindeX destroyed, bye bye.");
    }

}