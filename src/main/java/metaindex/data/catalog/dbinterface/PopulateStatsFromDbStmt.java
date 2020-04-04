package metaindex.data.catalog.dbinterface;

/**
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;

import org.json.JSONObject;

import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESPopulateStmt;
import toolbox.database.elasticsearch.ESReadStreamStmt;

import toolbox.exceptions.DataProcessException;

class PopulateStatsFromDbStmt extends ESPopulateStmt<ICatalog>   {

	private Log log = LogFactory.getLog(Catalog.class);
	
	private Request _request;
	
	List<ICatalog> _data;
	
	public PopulateStatsFromDbStmt(List<ICatalog> d, ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
	
		_data=d;
		
		String reqStr = "/";
		for (ICatalog c : _data) {
			if (reqStr.length()>1) { reqStr+=","; }
			reqStr+=c.getName().replaceAll(" ", "%20");
		}
		reqStr+="/_stats/docs";
		
		_request = new Request("GET",reqStr);
				
	}

	
	@Override
	public void execute() throws DataProcessException {
		try {
			
			Response response = this.getDataConnector()
								.getHighLevelClient().getLowLevelClient()
								.performRequest(_request);
			
			String responseBody = EntityUtils.toString(response.getEntity());
			JSONObject decodedData = new JSONObject(responseBody);
			
			for (ICatalog c : _data) { 
				c.setNbDocuments(0L);
				JSONObject curCatalogStats = decodedData.getJSONObject("indices")
														 .getJSONObject(c.getName());
				if (curCatalogStats==null) {
					log.error("ignored index : "+c.getName());
					continue;
				}
				
				Long nbDocs =  curCatalogStats.getJSONObject("total")
												.getJSONObject("docs")
												.getLong("count");
				c.setNbDocuments(nbDocs);
				c.setDbIndexFound(true);
			}
			
			
		} catch (ResponseException e) {
			throw new DataProcessException(e.getMessage());
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataProcessException(e);
		}
		
	}
	
					
};
