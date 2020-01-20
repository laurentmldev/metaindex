package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;

import metaindex.data.catalog.ICatalog;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;

public class CreateIndexIntoEsDbStmt extends ESWriteStmt<ICatalog>   {

	public class IndexAlreadyExistException extends DataProcessException {
		IndexAlreadyExistException(Exception e) { super(e); }		
	};
	
	private Log log = LogFactory.getLog(CreateIndexIntoEsDbStmt.class);
	
	List<ICatalog> _data = new ArrayList<ICatalog>();
	public CreateIndexIntoEsDbStmt(List<ICatalog> catalogs, ESDataSource ds) throws DataProcessException { 
		super(ds);
		_data.addAll(catalogs);
	}
	
	@Override
	public Boolean execute() throws DataProcessException {
		try {
			for (ICatalog c : _data) {
				CreateIndexRequest request = new CreateIndexRequest(c.getName()); 
				
				CreateIndexResponse createIndexResponse = 
						this.getDatasource().getHighLevelClient().indices().create(request, RequestOptions.DEFAULT);	
				
				return createIndexResponse.isAcknowledged();					
			}
		} catch (ElasticsearchStatusException e) {
			if (e.getDetailedMessage().contains("type=resource_already_exists_exception")) {
				throw new IndexAlreadyExistException(e);
			}			
			throw new DataProcessException(e.getMessage());
		} catch (ConnectException e) {		
			log.error("Unable to connect to create index into ElasticSearch, unable to establish connection to server "
																							+this.getDatasource().toString());
			return false;
		} catch (Exception e) {		
			e.printStackTrace();
			throw new DataProcessException(e.getMessage());
		}
		return true;
	}
	
	
	
						
};
