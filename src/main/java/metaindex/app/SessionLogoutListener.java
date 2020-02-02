
package metaindex.app;


import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import metaindex.data.commons.globals.Globals;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;


@Component
public class SessionLogoutListener  extends HttpSessionEventPublisher {

	private Log log = LogFactory.getLog(SessionLogoutListener.class);
	
	   @Override
	   public void sessionCreated(HttpSessionEvent event) {
	      super.sessionCreated(event);
	      
	   }

	   @Override
	   public void sessionDestroyed(HttpSessionEvent event) {
	      
		  IUserProfileData user = Globals.Get().getUsersMgr().getUserByHttpSessionId(event.getSession().getId());
		  
		  if (user != null) {
			  try {
				  user.acquireLock();
				  user.logOut(); 				  
			  }
			  catch (InterruptedException|DataProcessException  e) {
				  log.error("Error while performing uer logOut operations : "+e);
			  }
			  user.releaseLock();
		  }
		   
	      super.sessionDestroyed(event);
	      
	      
	   }

}