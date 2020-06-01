package metaindex.app.control.websockets.users.messages;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

public class WsUserGuiMessageText implements IWsUserGuiMessage  {
		

	public enum MESSAGE_CRITICITY { INFO, WARNING, ERROR, SUCCESS };
	
	private String _text;
	private List<String> _details;
	private MESSAGE_CRITICITY _level;
	
	public WsUserGuiMessageText(MESSAGE_CRITICITY level, String msg,List<String> details) {
		_level=level;
		_text=msg;
		setDetails(details);
	}


	public MESSAGE_TYPE getMsgType() {
		return MESSAGE_TYPE.TEXT;
	}

	public String getText() {
		return _text;
	}

	public void setText(String msg) {
		this._text = msg;
	}

	public MESSAGE_CRITICITY getLevel() {
		return _level;
	}

	public void setLevel(MESSAGE_CRITICITY level) {
		this._level = level;
	}


	public List<String> getDetails() {
		return _details;
	}


	public void setDetails(List<String> details) {
		this._details = details;
	}

}
