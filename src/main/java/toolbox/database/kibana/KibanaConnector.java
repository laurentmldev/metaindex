package toolbox.database.kibana;


import java.util.List;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import reactor.core.publisher.Mono;
import toolbox.database.IDataConnector;
import toolbox.exceptions.DataProcessException;

public class KibanaConnector implements IDataConnector {
	
	private Log log = LogFactory.getLog(KibanaConnector.class);
	
	public static enum KIBANA_PRIVILEGE { read,write,all,manage,index,view_index_metadata };
	public static enum KIBANA_HTTP_METHOD { GET,POST,PUT,DELETE };
	public static enum KIBANA_SPACE_FEATURE { dev_tools,advancedSettings,indexPatterns,savedObjectsManagement,graph,monitoring,ml,apm,maps,canvas,infrastructure,siem,logs,uptime }
	
	String _hostname;
	Integer _port;
	String _protocol;
	
	
	//new HttpHost("localhost", 9200, "http")
	public KibanaConnector(String host, Integer port, String protocol) { 
		_hostname=host;
		_protocol=protocol;
		_port=port;
	
	}
	
	public String getUrl() {
		return _protocol+"://"+_hostname+":"+_port;
	}
	
	
	@Override
	public void close() {}
	
	private JSONObject requestRestService(String username, String pwd,
					KIBANA_HTTP_METHOD method,String serviceUri, JSONObject params) 
							throws DataProcessException {
		
		
		try {
			if (!serviceUri.startsWith("/") ) { serviceUri="/"+serviceUri; }			
			
			WebClient client = WebClient.builder().clientConnector(new ReactorClientHttpConnector())				
											.filter(ExchangeFilterFunctions.basicAuthentication(username, pwd))
											.baseUrl(getUrl()).build();
			
			client.head().attribute("kbn-xsrf", "true").accept(MediaType.APPLICATION_JSON);
			
			WebClient.RequestHeadersSpec<?>	request = null;
			if (method==KIBANA_HTTP_METHOD.POST) { 
					request=client.post()
								.uri(serviceUri)
								.body(Mono.just(params.toString()),String.class)
								; 
			}
			else if (method==KIBANA_HTTP_METHOD.PUT) { 
				request=client.put()
							.uri(serviceUri)
							.body(Mono.just(params.toString()),String.class)
							; 
			}
			else if (method==KIBANA_HTTP_METHOD.DELETE) { 
				request=client.delete()
							.uri(serviceUri)
							; 
			}else { 
				request=client.get()
						.uri(serviceUri)
						;
			}
			request.header(HttpHeaders.ACCEPT_ENCODING, "*");
			request.header(HttpHeaders.CONTENT_TYPE, "application/json");
			request.header("kbn-xsrf", "true");
			//request.attribute("content-type", "application/json");
			request.accept(MediaType.APPLICATION_JSON); 
			
			//log.info("Kibana REST Request : "+method+" "+getUrl()+serviceUri+" params="+params);
			WebClient.ResponseSpec webResponse = request.retrieve();
			/*
			webResponse.onStatus(httpStatus -> HttpStatus.ACCEPTED.equals(httpStatus), 
			        response -> response.bodyToMono(String.class).map(body -> new Exception()));
			*/

			String responseStr=webResponse.bodyToMono(String.class).block();	
			if (responseStr==null) { return null; }		
			JSONObject response = new JSONObject(responseStr.replaceAll("^\\[", "").replaceAll("\\]$", ""));
			return response;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new DataProcessException("Error while performing Kibana REST command : "+e.getMessage(),e);
		}
		catch (Throwable e) {
			e.printStackTrace();
			throw new DataProcessException("Error while performing Kibana REST command : "+e.getMessage());
		}
		
	
	}
	
	
	// -------- ROLES -------
	public Boolean createKibanaRole(String adminUser, String adminPwd,
			String roleName,
			List<String> indicesNames, KIBANA_PRIVILEGE indicesPrivilege,
			List<String> spacesListStr,
			List<String> featuresListStr,KIBANA_PRIVILEGE featuresPrivilege) {
		
		// method will probably change to POST in Kibana future versions I guess
		return createOrUpdateKibanaRole(adminUser,adminPwd,KIBANA_HTTP_METHOD.PUT,
							roleName,indicesNames,indicesPrivilege,spacesListStr,featuresListStr,featuresPrivilege) ;
	}
	public Boolean updateKibanaRole(String adminUser, String adminPwd,
			String roleName,
			List<String> indicesNames, KIBANA_PRIVILEGE indicesPrivilege,
			List<String> spacesListStr,
			List<String> featuresListStr,KIBANA_PRIVILEGE featuresPrivilege) {
		
		return createOrUpdateKibanaRole(adminUser,adminPwd,KIBANA_HTTP_METHOD.PUT,
				roleName,indicesNames,indicesPrivilege,spacesListStr,featuresListStr,featuresPrivilege) ;
	}
	
	private Boolean createOrUpdateKibanaRole(String adminUser, String adminPwd,KIBANA_HTTP_METHOD method,
									String roleName,
									List<String> indicesNames, KIBANA_PRIVILEGE indicesPrivilege,
									List<String> spacesListStr,
									List<String> featuresListStr,KIBANA_PRIVILEGE featuresPrivilege) {
		
		JSONObject createRoleRequestData = new JSONObject(); 
		JSONArray kibanaDataArray = new JSONArray();
		JSONObject kibanaData = new JSONObject();
		kibanaDataArray.put(kibanaData);
		createRoleRequestData.put("kibana", kibanaDataArray);
		
		//  spaces access
		JSONArray spaces = new JSONArray();
		for (String spaceName : spacesListStr) { spaces.put(spaceName); }
		kibanaData.put("spaces", spaces);
		
		// features access
		JSONObject feature = new JSONObject();
		kibanaData.put("feature", feature);
		JSONArray featurePrivilegesList = new JSONArray();
		featurePrivilegesList.put(featuresPrivilege.toString());
		for (String featureName : featuresListStr) { feature.put(featureName, featurePrivilegesList); }
				
		// ES data
		JSONObject esData = new JSONObject();
		createRoleRequestData.put("elasticsearch", esData);
		
		//indicies access
		JSONArray indices = new JSONArray();
		esData.put("indices", indices);
		JSONObject indexData = new JSONObject();
		indices.put(indexData);
		;
		//---- User index access
		JSONArray indicesArray = new JSONArray();
		for (String indexName : indicesNames) { indicesArray.put(indexName); }
		indexData.put("names", indicesArray);
		
		JSONArray indexPrivilegesArray = new JSONArray();
		indexPrivilegesArray.put(indicesPrivilege);
		indexPrivilegesArray.put(KIBANA_PRIVILEGE.view_index_metadata);
		indexData.put("privileges", indexPrivilegesArray);
		
		/*
		//----Kibana indices access	
		//JSONObject kibanaIndexData = new JSONObject();
		//indices.put(kibanaIndexData)
		JSONArray kibanaIndicesArray = new JSONArray();
		kibanaIndicesArray.put(".kibana*");
		kibanaIndexData.put("names", kibanaIndicesArray);
		
		JSONArray kibanaIndexPrivilegesArray = new JSONArray();
		kibanaIndexPrivilegesArray.put(KIBANA_PRIVILEGE.manage);
		kibanaIndexPrivilegesArray.put(KIBANA_PRIVILEGE.read);
		kibanaIndexPrivilegesArray.put(KIBANA_PRIVILEGE.index);
		kibanaIndexPrivilegesArray.put(KIBANA_PRIVILEGE.view_index_metadata);
		kibanaIndexData.put("privileges", kibanaIndexPrivilegesArray);
		*/
		
		//----
		try {
			/*JSONObject response = */requestRestService(adminUser, adminPwd, method,
													"/api/security/role/"+roleName, createRoleRequestData);
			return true;
		} catch (DataProcessException  e) {
			log.error("Error while performing Kibana operation (via REST API) for creating or updating role '"+roleName+"'");
			return false;
		}
		
	}
	public Boolean deleteKibanaRole(String adminUser, String adminPwd,String roleName) {
		
		JSONObject createIndexPatternRequestData = new JSONObject();
		
		try {
			/*JSONObject response = */requestRestService(adminUser, adminPwd, 
														KIBANA_HTTP_METHOD.DELETE,"/api/security/role/"+roleName,
														createIndexPatternRequestData);
			return true;
		} catch (DataProcessException  e) {
			log.error("Error while performing Kibana operation (via REST API) for deleting role '"+roleName+"'");
			return false;
		}
		
	}
	
	
	// ----------- SPACES ------------
	public Boolean createKibanaSpace(String adminUser, String adminPwd,
			String spaceId,String spaceName,String description,String color,String initials,String imageUrl,
			List<KIBANA_SPACE_FEATURE> disabledFeaturesStr)
	{
		return createOrUpdateKibanaSpace(adminUser,adminPwd,KIBANA_HTTP_METHOD.POST,
							spaceId,spaceName,description,color,initials,imageUrl,disabledFeaturesStr);
	}
	public Boolean updateKibanaSpace(String adminUser, String adminPwd,
			String spaceId,String spaceName,String description,String color,String initials,String imageUrl,
			List<KIBANA_SPACE_FEATURE> disabledFeaturesStr)
	{
		return createOrUpdateKibanaSpace(adminUser,adminPwd,KIBANA_HTTP_METHOD.PUT,
							spaceId,spaceName,description,color,initials,imageUrl,disabledFeaturesStr);
	}
	private Boolean createOrUpdateKibanaSpace(String adminUser, String adminPwd,KIBANA_HTTP_METHOD method,
									String spaceId,String spaceName,String description,String color,String initials,String imageUrl,
									List<KIBANA_SPACE_FEATURE> disabledFeaturesStr) {
		
		JSONObject createSpaceRequestData = new JSONObject(); 
		
		createSpaceRequestData.put("id", spaceId);
		createSpaceRequestData.put("name", spaceName);
		
		createSpaceRequestData.put("description", description);
		createSpaceRequestData.put("color", color);
		createSpaceRequestData.put("initials", initials);
		createSpaceRequestData.put("imageUrl", imageUrl);
				
		JSONArray disabledFeatures = new JSONArray();
		for (KIBANA_SPACE_FEATURE feature : disabledFeaturesStr) { disabledFeatures.put(feature.toString()); }
		createSpaceRequestData.put("disabledFeatures", disabledFeatures);
		
		try {
			JSONObject response = requestRestService(adminUser, adminPwd, method,
													"/api/spaces/space", createSpaceRequestData);
			return response!=null;
		} catch (DataProcessException  e) {
			log.error("Error while performing Kibana operation (via REST API) for creating or updating space '"+spaceId+"'");
			return false;
		}
	}
	
	public Boolean deleteKibanaSpace(String adminUser, String adminPwd,String spaceId) {
	
		JSONObject createIndexPatternRequestData = new JSONObject();
		try {
			/*JSONObject response = */requestRestService(adminUser, adminPwd, 
														KIBANA_HTTP_METHOD.DELETE,"/api/spaces/space/"+spaceId,
														createIndexPatternRequestData);
			return true;
		} catch (DataProcessException  e) {
			log.error("Error while performing Kibana operation (via REST API) for deleting space '"+spaceId+"'");
			return false;
		}
	}
	// ---------- INDEX-PATTERNS -------------

	public Boolean createKibanaIndexPattern(String adminUser, String adminPwd,
						String spaceId,String indexPatternName,String indicesFilter,String timeFieldName) {
		
		JSONObject createIndexPatternRequestData = new JSONObject();
		JSONObject attributes = new JSONObject();
		createIndexPatternRequestData.put("attributes", attributes);
		
		attributes.put("title", indicesFilter);
		if (timeFieldName!=null && timeFieldName.length()>0) { attributes.put("timeFieldName", timeFieldName); }
		
		try {
			/*
			JSONObject response = requestRestService(adminUser, adminPwd, 
				KIBANA_HTTP_METHOD.POST,"/api/saved_objects/index-pattern/"+indexPatternName, createIndexPatternRequestData);
			*/
			JSONObject response = requestRestService(adminUser, adminPwd, 
					KIBANA_HTTP_METHOD.POST,"/s/"+spaceId+"/api/saved_objects/index-pattern/"+indexPatternName, 
					createIndexPatternRequestData);
					
			return response!=null;
		} catch (DataProcessException  e) {
			log.error("Error while performing Kibana operation (via REST API) for creating index-pattern '"+indexPatternName+"'");
			return false;
		}
	}
	public Boolean deleteKibanaIndexPattern(String adminUser, String adminPwd,
			String spaceId,String indexPatternName) {
	
		JSONObject createIndexPatternRequestData = new JSONObject();
		try {
			/*
			JSONObject response = requestRestService(adminUser, adminPwd, 
					KIBANA_HTTP_METHOD.DELETE,"/api/saved_objects/index-pattern/"+indexPatternName, createIndexPatternRequestData);
			*/
			JSONObject response = requestRestService(adminUser, adminPwd, 
					KIBANA_HTTP_METHOD.DELETE,"/s/"+spaceId+"/api/saved_objects/index-pattern/"+indexPatternName, 
					createIndexPatternRequestData);
					
			
			return response!=null;
		} catch (DataProcessException  e) {
			log.error("Error while performing Kibana operation (via REST API) for deleting index-pattern '"+indexPatternName+"'");
			return false;
		}
	}
	
	// ---------- USERS -------------
	// Kibana's users management lies directly on ElasticSearch users. See ES connector.
}
