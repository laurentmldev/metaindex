package metaindex.app.beans;


/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import toolbox.exceptions.DataProcessException;

    
/**
 * Common Bean for all the 'Profile' JSP pages (profile,createProfile, editProfile).
 * @author Laurent ML
 */
public class BeanCheckout extends AMetaindexBean {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(BeanCheckout.class);
	
	/**
	 * Synchronized to ensure that it is not possible to create together an account with same email
	 */
	@Override
  	synchronized public String execute() throws Exception {
		
		try { 
			
			if (Globals.Get().getApplicationStatus()==APPLICATION_STATUS.STOPPED) {
					throw new DataProcessException("application is not running, unable to create new account for now");								
			}
			
				
			
		} catch (Throwable e) {
  			e.printStackTrace();		  			  			  			  			
  		} 
		
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
