package metaindex.app.control.websockets.commons;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public interface IWsMsg_answer  {
			
	
	public Boolean getIsSuccess();
	public void setIsSuccess(Boolean isSuccess);
	public String getRejectMessage();
	public void setRejectMessage(String rejectMessage);


	
}
