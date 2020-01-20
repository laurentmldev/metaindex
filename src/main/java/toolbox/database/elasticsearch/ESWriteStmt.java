package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;

import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.RequestOptions;

import toolbox.database.IDatabaseWriteStmt;
import toolbox.database.IDatasourcedStmt;
import toolbox.exceptions.DataProcessException;

// no templatisation needed for ESWarite statement, so using Object as a Tdata template parameter
public abstract class ESWriteStmt<T> implements IDatabaseWriteStmt<T>,IDatasourcedStmt<ESDataSource> {

	private static Integer ELASTIC_SEARCH_UPDATE_DELAY_SECONDS = 1;
	private ESDataSource _datasource;
	
	public ESWriteStmt(ESDataSource ds) {		
		_datasource=ds; 
	}
	
	@Override
	public Boolean execute() throws DataProcessException {
		return false;
	}
	
	@Override
	public ESDataSource getDatasource() {
		return _datasource;
	}
	
	public static void waitUntilEsIndexRefreshed(String indexName, ESDataSource datasource) throws DataProcessException {
		try {
			// give time to ElasticSearch to update its state
			Thread.sleep(ELASTIC_SEARCH_UPDATE_DELAY_SECONDS*1000);
			
			RefreshRequest request = new RefreshRequest(indexName);			
			RefreshResponse refreshResponse = datasource.getHighLevelClient().indices().refresh(request, RequestOptions.DEFAULT);
			if (refreshResponse.getFailedShards()>0) {
				throw new DataProcessException("ElasticSearch refresh index '"+indexName+"' : "
							+refreshResponse.getFailedShards()+" shards failed to be refreshed");
			}
		} catch (IOException | InterruptedException e) {
			throw new DataProcessException(
					"ElasticSearch refresh index '"+indexName+"' failed : "+e.getMessage());
		}
	}

		
}
