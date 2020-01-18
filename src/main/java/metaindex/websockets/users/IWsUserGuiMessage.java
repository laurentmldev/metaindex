package metaindex.websockets.users;

public interface IWsUserGuiMessage  {
	
	public enum MESSAGE_TYPE { TEXT, PROGRESS };
	
	public MESSAGE_TYPE getMsgType();

}
