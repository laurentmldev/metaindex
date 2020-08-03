package metaindex.app.control.websockets.users.messages;

import java.util.HashMap;
import java.util.Map;

import metaindex.app.control.websockets.commons.IWsMsg_answer;
import metaindex.data.catalog.ICatalog;
import metaindex.data.userprofile.IUserProfileData;
import metaindex.data.userprofile.IUserProfileData.USER_CATALOG_ACCESSRIGHTS;
import metaindex.data.userprofile.UserProfileData;
import toolbox.utils.IIdentifiable;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUserProfile_answer implements IWsMsg_answer {
		
	private Integer _requestId;
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	public class UserProfileGuiData implements IIdentifiable<Integer> {
		private Integer _id=0;
		private String _name="";
		private Integer _maxNbCatalogsCreated=0;
		private Integer _curNbCatalogsCreated=0;
		private Integer _catalogId=0;
		private USER_CATALOG_ACCESSRIGHTS _catalogAccesRights=USER_CATALOG_ACCESSRIGHTS.NONE;
		
		UserProfileGuiData(IUserProfileData u, ICatalog c) {
			populate(u,c);
		}
		UserProfileGuiData(IUserProfileData u) {
			populate(u,null);			
		}
		
		private void populate(IUserProfileData u, ICatalog catalog) {
			setId(u.getId());
			setName(u.getName());
			setMaxNbCatalogsCreated(u.getMaxNbCatalogsCreated());
			setCurNbCatalogsCreated(u.getCurNbCatalogsCreated());
			if (catalog!=null) {
				setCatalogId(catalog.getId());
				setCatalogAccesRights(u.getUserCatalogAccessRights(catalog.getId()));
			}
		}
		
		@Override
		public Integer getId() { return _id; }
		public void setId(Integer id) { _id=id; }
		
		@Override
		public String getName() { return _name; }
		public void setName(String name) { _name=name; }
		
		public Integer getMaxNbCatalogsCreated() { return _maxNbCatalogsCreated; }
		public void setMaxNbCatalogsCreated(Integer maxNbCatalogsCreated) { _maxNbCatalogsCreated=maxNbCatalogsCreated; }
		public Integer getCurNbCatalogsCreated() { return _curNbCatalogsCreated; }
		public void setCurNbCatalogsCreated(Integer curNbCatalogsCreated) { _curNbCatalogsCreated=curNbCatalogsCreated; }
		
		public USER_CATALOG_ACCESSRIGHTS getCatalogAccesRights() { return _catalogAccesRights; }
		public void setCatalogAccesRights(USER_CATALOG_ACCESSRIGHTS curCatalogAccesRights) { _catalogAccesRights=curCatalogAccesRights; }
		public Integer getCatalogId() { return _catalogId; }
		public void setCatalogId(Integer curCatalogId) { _catalogId = curCatalogId; }
	}
	
	private Map<Integer,UserProfileGuiData> _usersMap = new HashMap<>();
	
	public Map<Integer,UserProfileGuiData> getUsers() { return _usersMap; }
	public void addUser(IUserProfileData u) {
		_usersMap.put(u.getId(), new UserProfileGuiData(u));
	}
	
	public Integer getRequestId() { return _requestId; }
	public void setRequestId(Integer requestId) { _requestId = requestId; }
	
	

}
