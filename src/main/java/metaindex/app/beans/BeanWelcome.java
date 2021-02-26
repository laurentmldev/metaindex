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
import metaindex.app.beans.AMetaindexBean;
import metaindex.app.periodic.statistics.publicpages.WelcomePageMxStat;

    
/**
 * Bean for welcome page
 * @author Laurent ML
 */
public class BeanWelcome extends AMetaindexBean {  
  
	private static final long serialVersionUID = -2366950152373009921L;
	private Log log = LogFactory.getLog(BeanProfile.class);

	
	@Override
  	public String execute() throws Exception {
		Globals.GetStatsMgr().handleStatItem(new WelcomePageMxStat(this.getCurrentUserProfile()));
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
