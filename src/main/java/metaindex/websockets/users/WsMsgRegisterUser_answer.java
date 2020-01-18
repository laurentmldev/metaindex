package metaindex.websockets.users;

import java.util.ArrayList;
import java.util.List;



public class WsMsgRegisterUser_answer  {
		
	Boolean registrationStatus=false;
	
	public WsMsgRegisterUser_answer(Boolean status) {
		setRegistrationStatus(status);
	}	
	
	public Boolean getRegistrationStatus() {
		return registrationStatus;
	}
	public void setRegistrationStatus(Boolean registrationStatus) {
		this.registrationStatus = registrationStatus;
	}

}
