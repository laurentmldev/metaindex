package toolbox.database.elasticsearch;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import toolbox.database.IDataSource;

public class ESDataSource implements IDataSource {
	
	RestHighLevelClient _client;
	String _hostname;
	Integer _port1,_port2;
	String _protocol;
	
	//new HttpHost("localhost", 9200, "http")
	public ESDataSource(String host, Integer port1, Integer port2, String protocol) { 
		_hostname=host;
		_protocol=protocol;
		_port1=port1;
		_port2=port2;
		
		_client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(host, port1, protocol),
		                new HttpHost(host, port2, protocol)));

	}
	
	@Override
	public String toString() {
		return _protocol+"://"+_hostname+":"+_port1;
	}
	public RestHighLevelClient getHighLevelClient() { return _client; }
	
	@Override
	public void close() {
		try {
			_client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
}
