package metaindex.app.control.websockets.users.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class WsMsgUserSetPreferences_request  {
		
	private Integer _requestId;	
	private Integer _userId;
	private String _nickName;
	private Integer _languageId;
	private Integer _themeId;
	public Integer getUserId() {
		return _userId;
	}
	public void setUserId(Integer _userId) {
		this._userId = _userId;
	}
	public String getNickName() {
		return _nickName;
	}
	public void setNickName(String _nickName) {
		this._nickName = _nickName;
	}
	public Integer getLanguageId() {
		return _languageId;
	}
	public void setLanguageId(Integer _languageId) {
		this._languageId = _languageId;
	}
	public Integer getThemeId() {
		return _themeId;
	}
	public void setThemeId(Integer _themeId) {
		this._themeId = _themeId;
	}
	public Integer getRequestId() {
		return _requestId;
	}
	public void setRequestId(Integer _requestId) {
		this._requestId = _requestId;
	}
	
	

}
