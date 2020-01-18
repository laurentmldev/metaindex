package metaindex.websockets.users;

public class WsUserGuiMessageProgress implements IWsUserGuiMessage {
		
	private String _text;
	private Integer _processingId;
	private Float _pourcentage;
	private Boolean _processingActive;

	public WsUserGuiMessageProgress(Integer processingId, String msg, Float pourcentage, Boolean processingActive) {
		_text=msg;
		setProcessingId(processingId);
		setPourcentage(pourcentage);
		setProcessingActive(processingActive);
	}
	public MESSAGE_TYPE getMsgType() {
		return MESSAGE_TYPE.PROGRESS;
	}
	public String getText() {
		return _text;
	}

	public void setText(String msg) {
		this._text = msg;
	}

	public Integer getProcessingId() {
		return _processingId;
	}

	public void setProcessingId(Integer processingId) {
		this._processingId = processingId;
	}

	public Float getPourcentage() {
		return _pourcentage;
	}

	public void setPourcentage(Float pourcentage) {
		this._pourcentage = pourcentage;
	}
	public Boolean getProcessingActive() {
		return _processingActive;
	}
	public void setProcessingActive(Boolean _processingActive) {
		this._processingActive = _processingActive;
	}


}
