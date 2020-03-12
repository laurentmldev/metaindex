package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import toolbox.exceptions.DataProcessException;
import toolbox.utils.IStreamHandler;

public abstract class AESSearchDocumentsRequest<TData> extends ESReadStmt<TData> {

	private SearchRequest _request;
	
	AESSearchDocumentsRequest(ESDataSource ds, String indexId, Integer fromIdx, Integer nbResults) throws DataProcessException {
		super(ds);		
		_request = new SearchRequest(indexId);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		if (fromIdx>0) { searchSourceBuilder.from(fromIdx); }
		if (nbResults>0) { searchSourceBuilder.size(nbResults); }
		_request.source(searchSourceBuilder);		
	}

	// Example :
	/*
	SearchHits hits = searchResponse.getHits();
	SearchHit[] searchHits = hits.getHits();
	
	for (SearchHit hit : searchHits) {
		String index = hit.getIndex();
		String id = hit.getId();
		float score = hit.getScore();
		
		String sourceAsString = hit.getSourceAsString();
		
		Map<String, Object> sourceAsMap = hit.getSourceAsMap();
		String documentTitle = (String) sourceAsMap.get("title");
		List<Object> users = (List<Object>) sourceAsMap.get("user");
		Map<String, Object> innerObject =
		        (Map<String, Object>) sourceAsMap.get("innerObject");
	}
	
	*/
	public abstract List<TData> mapSearchResponse(SearchResponse searchResponse);
	
	
	@Override
	public void execute(IStreamHandler<TData> d) throws DataProcessException {
		try {
			SearchResponse searchResponse = this.getDatasource().getHighLevelClient().search(_request,RequestOptions.DEFAULT);
			d.handle(mapSearchResponse(searchResponse));	
			
		} catch (IOException e) {
			throw new DataProcessException(e);
		}
	}

	
	
}
