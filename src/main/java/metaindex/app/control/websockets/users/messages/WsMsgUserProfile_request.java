package metaindex.app.control.websockets.users.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

public class WsMsgUserProfile_request  {
	
	private Integer _requestId;	
	private List<Integer> _usersIds = new ArrayList<>();
	
	
	public List<Integer> getUsersIds() { return _usersIds; }
	public void setUsersIds(List<Integer> idsList) { _usersIds=idsList; }
	public Integer getRequestId() { return _requestId; }
	public void setRequestId(Integer requestId) { _requestId = requestId; }

}
