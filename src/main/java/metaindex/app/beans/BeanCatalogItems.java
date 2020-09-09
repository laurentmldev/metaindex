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
import metaindex.data.userprofile.IUserProfileData.USER_ROLE;

    
/**
 * Bean for Catalog contents
 * @author Laurent ML
 */
public class BeanCatalogItems extends AMetaindexBean {  
  
	private static final long serialVersionUID = 3038124169664227842L;
	private Log log = LogFactory.getLog(BeanCatalogItems.class);

	
	@Override
  	public String execute() throws Exception {
		// ensure cannot access contents of a disabled catalog
		// except app admin
		if (this.getCurrentUserProfile()==null 
				|| this.getCurrentUserProfile().isEnabled()==false
				|| this.getCurrentUserProfile().isLoggedIn()==false
				|| this.getCurrentCatalog()==null 
				|| this.getCurrentCatalog().isEnabled()==false 
						&& 
				   this.getCurrentUserProfile().getRole()!=USER_ROLE.ROLE_ADMIN) {
			return BeanProcessResult.BeanProcess_ConstraintERROR.toString();
		}
		return BeanProcessResult.BeanProcess_SUCCESS.toString();
	}
	
}  
