package metaindex.data.userprofile.dbinterface;

import java.net.ConnectException;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.security.PutUserRequest;
import org.elasticsearch.client.security.PutUserResponse;
import org.elasticsearch.client.security.RefreshPolicy;
import org.elasticsearch.client.security.user.User;

import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.exceptions.DataProcessException;

class CreateOrUpdateESUserStmt extends ESWriteStmt<IUserProfileData>   {
	
	private Log log = LogFactory.getLog(CreateOrUpdateESUserStmt.class);
	List<IUserProfileData> _users = new ArrayList<>();
	List<String> _roles;
	
	public CreateOrUpdateESUserStmt(IUserProfileData u, ElasticSearchConnector ds,List<String> roles) throws DataProcessException { 
		super(ds);
		_users.add(u);
		_roles=roles;
	}


	@Override
	public Boolean execute() throws DataProcessException {
		try {
			 Map<String, Object> metadata = new HashMap<>();
			 metadata.put("origin", "metaindex");
			 
			for (IUserProfileData user : _users) {
				Boolean enabled=user.isEnabled();
				
				User esUser = new User(user.getName(), _roles,metadata,user.getNickname(),user.getName());
				PutUserRequest request = PutUserRequest
						.withPasswordHash(esUser,user.getPassword().toCharArray(), enabled, RefreshPolicy.NONE);
						//.withPassword(esUser,"lolopwd".toCharArray(), enabled, RefreshPolicy.NONE);

				PutUserResponse putUserResponse = 
						this.getDataConnector().getHighLevelClient().security().putUser(request, RequestOptions.DEFAULT);	
				
				if (!putUserResponse.isCreated()) {
					log.error(putUserResponse.toString());
				}
				
				return putUserResponse.isCreated();					
			}
		} catch (ElasticsearchStatusException e) {
			throw new DataProcessException(e.getMessage());
		} catch (ConnectException e) {		
			log.error("Unable to connect to create index into ElasticSearch, unable to establish connection to server "
																							+this.getDataConnector().toString());
			return false;
		} catch (Exception e) {		
			e.printStackTrace();
			throw new DataProcessException(e.getMessage());
		}
		return true;
	}
	
					
};
