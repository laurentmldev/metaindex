package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;

import metaindex.app.Globals;

import org.elasticsearch.client.RestHighLevelClient;

import toolbox.database.IDataConnector;

public class ElasticSearchConnector implements IDataConnector {
	
	RestHighLevelClient _client;
	String _hostname;
	Integer _port1,_port2;
	String _protocol;
	
	//new HttpHost("localhost", 9200, "http")
	public ElasticSearchConnector(String host, Integer port1, Integer port2, String protocol) { 
		_hostname=host;
		_protocol=protocol;
		_port1=port1;
		_port2=port2;
		
		final CredentialsProvider credentialsProvider =
			    new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY,
			    new UsernamePasswordCredentials(Globals.GetMxProperty("mx.elk.user"), 
			    								Globals.GetMxProperty("mx.elk.passwd")));
			
		HttpClientConfigCallback clientConfigCallback = new HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(
                        HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider);
                }
            };
            
        HttpHost host1 = new HttpHost(host, port1, protocol);
        HttpHost host2 = new HttpHost(host, port2, protocol);
        
		_client = new RestHighLevelClient(RestClient.builder(host1,host2).setHttpClientConfigCallback(clientConfigCallback));
		/*
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> vars = Collections.singletonMap("hotel", "42");
		String result =
		  restTemplate.getForObject("http://example.com/hotels/{hotel}/rooms/{hotel}", String.class, vars);
		
		
		URL url;
		try {
			url = new URL("http://example.com");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			Map<String, String> parameters = new HashMap<>();
			parameters.put("param1", "val");

			con.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			out.writeBytes("plop");
			out.flush();
			out.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

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
