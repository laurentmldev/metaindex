package metaindex.app.listeners;


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
        log.info("MetaindeX context destroyed, bye bye.");
    }

}