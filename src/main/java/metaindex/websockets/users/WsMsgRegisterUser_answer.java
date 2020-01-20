package metaindex.websockets.users;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

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
