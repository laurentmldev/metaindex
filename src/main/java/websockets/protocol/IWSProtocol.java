package metaindex.websockets.protocol;

public interface IWSProtocol {

	String getProtocolType();
		
	public IWSMessage decode(String data);
	
}
