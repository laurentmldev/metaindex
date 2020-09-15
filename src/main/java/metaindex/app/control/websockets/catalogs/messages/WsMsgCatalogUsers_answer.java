package metaindex.app.control.websockets.catalogs.messages;

import java.util.ArrayList;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import metaindex.app.control.websockets.commons.IWsMsg_answer;
import metaindex.data.userprofile.ICatalogUser.USER_CATALOG_ACCESSRIGHTS;
import toolbox.utils.IIdentifiable;


public class WsMsgCatalogUsers_answer extends WsMsgCatalogUsers_request implements IWsMsg_answer  {
		
	public static class GuiCatalogUser implements IIdentifiable<Integer>{

		private String _name;
		private String _nickname;
		private Integer _id;
		private Integer _catalogId;
		private USER_CATALOG_ACCESSRIGHTS _catalogAccessRights;
		
		@Override
		public String getName() { return _name; }
		public void setName(String name) { _name=name; }

		@Override
		public Integer getId() { return _id; }
		public void setId(Integer id) { _id=id; }
		
		public String getNickname() { return _nickname; }
		public void setNickname(String nickname) { _nickname=nickname; }
		
		public Integer getCatalogId() { return _catalogId; }
		public void setCatalogId(Integer id) { _catalogId=id; }
		
		public USER_CATALOG_ACCESSRIGHTS getCatalogAccessRights() { return _catalogAccessRights; }
		public void setCatalogAccessRights(USER_CATALOG_ACCESSRIGHTS rights) {  _catalogAccessRights=rights; }
		
		
	}
	private List<GuiCatalogUser> _users = new ArrayList<>();
		
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	public WsMsgCatalogUsers_answer(WsMsgCatalogUsers_request r) {	
		this.setRequestId(r.getRequestId());
		this.setCatalogId(r.getCatalogId());
	
	}	
	
	public List<GuiCatalogUser> getUsers() {
		return _users;
	}
	public void setUsers(List<GuiCatalogUser> users) {
		this._users = users;
	}
	
}
