package metaindex.app.beans;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.beans.AMetaindexBean;
import metaindex.app.beans.AMetaindexBean.BeanProcessResult;

    
/**
 * Bean for Catalogs list and overview page
 * @author Laurent ML
 */
public class BeanCatalogsOverview extends AMetaindexBean {  
  
	private static final long serialVersionUID = -1077822977888301466L;
	private Log log = LogFactory.getLog(BeanCatalogsOverview.class);

	
	@Override
  	public String execute() throws Exception {
		// ensure can access only if properly logged in
		if (this.getCurrentUserProfile()==null 
				|| this.getCurrentUserProfile().isEnabled()==false
				|| this.getCurrentUserProfile().isLoggedIn()==false
				) {
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
