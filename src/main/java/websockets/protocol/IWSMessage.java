package metaindex.websockets.protocol;

import metaindex.dbaccess.IJsonEncodable;

public interface IWSMessage extends IJsonEncodable {

	public static final String MSGTYPE_STRING = "MsgType";
	
	public String getMsgType();
		
}
