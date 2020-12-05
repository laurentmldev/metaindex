package metaindex.app.beans;



/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import metaindex.app.beans.ABeanEmailConfirmPasswdAction.AwaitingAccount;
import toolbox.exceptions.DataProcessException;

    
/**
 * Parent class for passwd reset and account creation.
 * Disable link once password has been set, but 
 * keep the link valid after several clicks as long
 * as password has not be reset.
 * @author Laurent ML
 */
public abstract class ABeanEmailConfirmPasswdAction extends AMetaindexBean {  
  	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(ABeanEmailConfirmPasswdAction.class);

	// 12h to confirm account
	private static final Integer MAX_ACCOUNT_CONFIRMATION_DELAY_SEC=43200;
	
	private String _email = "";
	private Long _requestId=null;
	

	// list is different between signup and reset password
	private static Map<String,AwaitingAccount> _awaitingAccountsByEmail=new ConcurrentHashMap<>();
	
	protected Map<String,AwaitingAccount>  getAwaitingAccountsByEmailMap() { return _awaitingAccountsByEmail; }
	

	class AwaitingAccount {
		
		public String email="";
		public Map<String,Object> properties=new HashMap<>();
		public Date requestDate=new Date();
		public Long randomRequestId=new Random().nextLong();
		
		public AwaitingAccount(String newEmail) throws DataProcessException {
			email=newEmail;			
		}
		
	}
	
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
