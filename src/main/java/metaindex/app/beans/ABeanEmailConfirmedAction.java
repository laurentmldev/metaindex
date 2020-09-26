package metaindex.app.beans;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import metaindex.app.Globals;
import metaindex.app.Globals.APPLICATION_STATUS;
import metaindex.app.beans.AMetaindexBean.BeanProcessResult;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.exceptions.DataProcessException;

    
/**
 * @author Laurent ML
 */
public abstract class ABeanEmailConfirmedAction extends AMetaindexBean {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(ABeanEmailConfirmedAction.class);

	// 12h to confirm account
	private static final Integer MAX_ACCOUNT_CONFIRMATION_DELAY_SEC=43200;
	
	private String _email = "";
	private Long _requestId=null;
	
	class AwaitingAccount {
		
		public String email="";
		public Map<String,Object> properties=new HashMap<>();
		public Date requestDate=new Date();
		public Long randomRequestId=new Random().nextLong();
		
		public AwaitingAccount(String newEmail) throws DataProcessException {
			email=newEmail;			
		}
		
	}
	
	abstract protected Map<String,AwaitingAccount>  getAwaitingAccountsByEmailMap();
	
	public AwaitingAccount getAwaitingAccount(String email,Long requestId) {
		AwaitingAccount a = getAwaitingAccountsByEmailMap().get(email);
		if (a==null) { return null; }
		if (!a.randomRequestId.equals(requestId)) { return null; }
		return a;
	}
	
	protected Boolean awaitingAccountExists(String email) {
		return getAwaitingAccountsByEmailMap().containsKey(email);
	}
	protected void clearAwaitingAccount(String email) {
		getAwaitingAccountsByEmailMap().remove(email);		
	}
	protected void addAwaitingAccount(AwaitingAccount a) {
		getAwaitingAccountsByEmailMap().put(a.email,a);
	}
	
	protected void clearOldRequests() {
		// clean too old requests
		Date now = new Date();			
		for (AwaitingAccount awaitingAccount : getAwaitingAccountsByEmailMap().values()) {
			Date creationDate = awaitingAccount.requestDate;
			if (now.toInstant().getEpochSecond()
						-creationDate.toInstant().getEpochSecond()
						> MAX_ACCOUNT_CONFIRMATION_DELAY_SEC) {
				getAwaitingAccountsByEmailMap().remove(awaitingAccount.email);
			}
		}
		
	}
	
	
	public void setEmail(String email) {
		_email=email;
	}
	public String getEmail() {
		return _email;
	}
	public void setRequestId(Long requestId) {
		_requestId=requestId;
	}
	public Long getRequestId() {
		return _requestId;
	}
	
}  
