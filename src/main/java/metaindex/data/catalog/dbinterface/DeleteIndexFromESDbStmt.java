package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;

import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;

import metaindex.data.catalog.ICatalog;
import metaindex.data.catalog.dbinterface.CreateIndexIntoEsDbStmt.IndexAlreadyExistException;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;

class DeleteIndexFromESDbStmt extends ESWriteStmt<ICatalog>   {

	IUserProfileData _activeUser;
	List<ICatalog> _data = new ArrayList<>();
	public DeleteIndexFromESDbStmt(IUserProfileData activeUser, 
										List<ICatalog> catalogs, 
										ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_activeUser=activeUser;
		_data=catalogs;		
	}
	

	@Override
	public Boolean execute() throws DataProcessException {
		try {
			for (ICatalog c : _data) {
				DeleteIndexRequest request = new DeleteIndexRequest(c.getName()); 
				
				AcknowledgedResponse deleteIndexResponse = 
						this.getDataConnector().getHighLevelClient().indices().delete(request, RequestOptions.DEFAULT);	
				
				return deleteIndexResponse.isAcknowledged();					
			}
		} catch (Exception e) {		
			e.printStackTrace();
			throw new DataProcessException(e.getMessage());
		}
		return true;
	}
	
						
};
