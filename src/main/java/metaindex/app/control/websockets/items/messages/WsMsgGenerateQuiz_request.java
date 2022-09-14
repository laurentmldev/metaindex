package metaindex.app.control.websockets.items.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/



public class WsMsgGenerateQuiz_request extends  WsMsgCsvDownload_request {

	private String _quizConfigJsonString="";
	private Integer _nbQuestions=30;
	private Double _quizFileSizeMB=0.0;
	private Boolean _asJson=false;
	
	public String getJsonQuizConfig() {
		return _quizConfigJsonString;
	}
	public void setJsonQuizConfig(String quizConfigJsonString) {
		this._quizConfigJsonString = quizConfigJsonString;
	}
	public Integer getNbQuestions() {
		return _nbQuestions;
	}
	public void setNbQuestions(Integer _nbQuestions) {
		this._nbQuestions = _nbQuestions;
	}
	public Double getQuizFileSizeMB() {
		return _quizFileSizeMB;
	}
	public void setQuizFileSizeMB(Double quizFileSizeMB) {
		this._quizFileSizeMB = quizFileSizeMB;
	}
	public Boolean getAsJson() {
		return _asJson;
	}
	public void setAsJson(Boolean asJson) {
		this._asJson = asJson;
	}
	
	
	
}
