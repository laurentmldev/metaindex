
package metaindex.app.session;


import javax.servlet.http.HttpSessionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.stereotype.Component;

import metaindex.app.Globals;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;


@Component
public class MxHttpSessionEventListener  extends HttpSessionEventPublisher /* implements ApplicationListener<SessionDestroyedEvent> */{

	private Log log = LogFactory.getLog(MxHttpSessionEventListener.class);
	
	   @Override
	   public void sessionCreated(HttpSessionEvent event) {
	      super.sessionCreated(event);
	   }

	   
	   @Override
	   public void sessionDestroyed(HttpSessionEvent event) {
	      
		  IUserProfileData user = Globals.Get().getUsersMgr().getUserByHttpSessionId(event.getSession().getId());
		  
		  if (user != null) {
			  try {
				  // does not work, seems that websockets connection is already closed
				  // when this method is called?
				  user.sendGuiErrorMessage(user.getText("session.expired"));
				  user.logOut(); 				  
			  }
			  catch (DataProcessException e) {
				  log.error("Error while performing uer logOut operations : "+e);
			  }
			  
		  }
		   
	      super.sessionDestroyed(event);
	      
	      
	   }

}