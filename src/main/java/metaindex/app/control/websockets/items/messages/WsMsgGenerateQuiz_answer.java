package metaindex.app.control.websockets.items.messages;

import metaindex.app.control.websockets.commons.IWsMsg_answer;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


public class WsMsgGenerateQuiz_answer extends WsMsgGenerateQuiz_request implements IWsMsg_answer {

	
	private Boolean _isSuccess=false;
	private String _rejectMessage="";
	@Override public Boolean getIsSuccess() { return _isSuccess; }
	@Override public void setIsSuccess(Boolean isSuccess) { this._isSuccess = isSuccess; }
	@Override public String getRejectMessage() { return _rejectMessage; }
	@Override public void setRejectMessage(String rejectMessage) { this._rejectMessage = rejectMessage; }
	
	
	private String _quizFileUrl="";
	private String _quizFileName="";
	public WsMsgGenerateQuiz_answer(WsMsgGenerateQuiz_request request) {
		this.setRequestId(request.getRequestId());
		this.setJsonQuizConfig(request.getJsonQuizConfig());
	}
	public String getQuizFileUrl() {
		return _quizFileUrl;
	}
	public void setQuizFileUrl(String quizFileUrl) {
		this._quizFileUrl = quizFileUrl;
	}
	public String getQuizFileName() {
		return _quizFileName;
	}
	public void setQuizFileName(String quizFileName) {
		this._quizFileName = quizFileName;
	}
}
