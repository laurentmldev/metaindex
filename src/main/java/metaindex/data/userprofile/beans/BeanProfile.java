package metaindex.data.userprofile.beans;


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
