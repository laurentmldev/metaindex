package metaindex.app.control.websockets.users;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

public class WsMsgUserSetPreferences_answer extends WsMsgUserSetPreferences_request implements IWsMsg_answer  {
	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }

	public WsMsgUserSetPreferences_answer(WsMsgUserSetPreferences_request request) {
		this.setNickName(request.getNickName());
		this.setUserId(request.getUserId());
		this.setLanguageId(request.getLanguageId());
		this.setThemeId(request.getThemeId());
		this.setRequestId(request.getRequestId());
	}
}
