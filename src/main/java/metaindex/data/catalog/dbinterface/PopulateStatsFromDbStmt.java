package metaindex.data.catalog.dbinterface;

import java.util.Iterator;

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
import org.json.JSONArray;
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
		
		String catsStrList="";
		for (ICatalog c : _data) {
			if (catsStrList.length()>1) { catsStrList+=","; }
			catsStrList+=c.getName().replace(" ", "%20");
		}
		
		String reqStr="/_cat/indices/"+catsStrList+"?format=json&h=index,docs.count,store.size";
		
		_request = new Request("GET",reqStr);
				
	}

	
	@Override
	public void execute() throws DataProcessException {
		try {
			
			Response response = this.getDataConnector()
								.getHighLevelClient().getLowLevelClient()
								.performRequest(_request);
			
			String responseBody = EntityUtils.toString(response.getEntity());
			JSONArray decodedData = new JSONArray(responseBody);
			
			//[{"index":"tagad","docs.count":"179","store.size":"35.2mb"}]
					
			for (ICatalog c : _data) { 
				c.setNbDocuments(0L);
				JSONObject curCatalogStats=null;
				Iterator<Object> it = decodedData.iterator();
				while (it.hasNext()) {
					JSONObject curStats = (JSONObject) it.next();
					if (curStats.getString("index").equals(c.getName())) {
						curCatalogStats = curStats;
						break;
					}					
				}
					
				if (curCatalogStats==null) {
					log.error("ignored index : "+c.getName());
					continue;
				}
				
				Long nbDocs =  curCatalogStats.getLong("docs.count");
				String storeSizeStr = curCatalogStats.getString("store.size");
				Float diskUsageValueFloat = new Float(storeSizeStr.substring(0,storeSizeStr.length()-2));
				
				// target disk usage unit is MBytes
				String diskUsageUnit=storeSizeStr.substring(storeSizeStr.length()-2);
				if (diskUsageUnit.equals("kb")) { diskUsageValueFloat=diskUsageValueFloat/1000; }
				else if (diskUsageUnit.equals("mb")) { /* nothing to do */}
				else if (diskUsageUnit.equals("gb")) { diskUsageValueFloat=diskUsageValueFloat*1000; }
				else if (diskUsageUnit.equals("tb")) { diskUsageValueFloat=diskUsageValueFloat*1000; }				
				
				else {
					// try if it is simply 'b' for bytes
					// in that case str size is different and we need to reparse initial string
					// TODO: maybe simply use regex instead ...
					String errmsg=c.getName()+": Unknown disk usage unit : '"+diskUsageUnit
							+"' (known ones are only b,kb,mb,gb,tb)";
					
					diskUsageUnit = storeSizeStr.substring(storeSizeStr.length()-1);
					if (!diskUsageUnit.equals("b")) { 
						log.error(errmsg);
						throw new DataProcessException(errmsg);
					}
					try {
						diskUsageValueFloat = 
								new Float(storeSizeStr.substring(0,storeSizeStr.length()-1));
						diskUsageValueFloat=diskUsageValueFloat/1000000; // convert to MBytes
					} catch(Exception e) {
						log.error(errmsg);
						throw new DataProcessException(errmsg);
						
					}
										
				}

				c.setNbDocuments(nbDocs);
				c.setELKIndexDiskUseMBytes(diskUsageValueFloat.longValue());
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
