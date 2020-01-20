package metaindex.data.userprofile.beans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.data.beans.AMetaindexBean;

    
/**
 * Common Bean for all the 'Profile' JSP pages (profile,createProfile, editProfile).
 * @author Laurent ML
 */
public class BeanProfile extends AMetaindexBean {  
  
	private static final long serialVersionUID = -8112077614648473484L;
	private Log log = LogFactory.getLog(BeanProfile.class);

	
	@Override
  	public String execute() throws Exception {
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
