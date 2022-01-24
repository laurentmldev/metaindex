package toolbox.database.elasticsearch;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONObject;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPair;


/**
 * Help class to build request objets over ES documents
 * @author laurentml
 *
 * @param fromIndex : use -1 for undefined
 * @param size : use -1 for undefined
 */
public class ESDocumentsRequestBuilder {
	
	private Log log = LogFactory.getLog(ESDocumentsRequestBuilder.class);
	
	private static final Integer DEFAULT_STREAM_DOCS_SIZE=1000;
	
	private SearchRequest _searchRequest;
	private CountRequest _countRequest;
	private Integer _fromIdx=-1;
	private Integer _size=DEFAULT_STREAM_DOCS_SIZE;
	private List<String> _prefilters = new ArrayList<String>();	
	private String _query="";
	private List< IPair<String,SORTING_ORDER> > _sortByFieldName = new ArrayList< IPair<String,SORTING_ORDER> >();
	private String _applicativeTiemstampFieldName="";
	// if null, retrieve all, if empty, retrieve only the _id
	private List<String> _sourceFieldsToRetrieve = null;
	
	
	private QueryBuilder buildESQuery(String queryStr) {
		// if its a valid JSON string we consider it is an ElasticSearch raw query
		try { new JSONObject(queryStr); }
		catch (Exception e) { return QueryBuilders.queryStringQuery(queryStr); } 
		return QueryBuilders.wrapperQuery(queryStr);		
				
	}
	
	private void init() {
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		SearchSourceBuilder countSourceBuilder = new SearchSourceBuilder();
		if (_fromIdx>=0) { searchSourceBuilder.from(_fromIdx); }
		if (_size>=0) { searchSourceBuilder.size(_size); }	
		if (_sourceFieldsToRetrieve!=null) {
			if (_sourceFieldsToRetrieve.size()==0) {
				searchSourceBuilder.fetchSource(false);
			}
			else {
				searchSourceBuilder.fetchSource((String[])_sourceFieldsToRetrieve.toArray(), null);
			}
		}
		
		// Combine filter queries :
		// user query
		//    AND
		//  (Filter1Query OR Filter2Query ... ) :
		//
		// BoolQuery:
		//    must :
		//		- user query
		//	  should
		//		- filter1
		//		- filter2
		//		...
		
				
		// no specific query, just retrieve all
		if (_query.length()==0 && _prefilters.size()==0) {
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			countSourceBuilder.query(QueryBuilders.matchAllQuery());
		// some specific filtering required by user
		} else {
			BoolQueryBuilder currentQuery = QueryBuilders.boolQuery();
			// user query, either 'match' query or a full Json query
			if (_query.length()>0) { 
				currentQuery.must(buildESQuery(_query)); 
			}
				
			// add selected filters if any, cumulative
			for (String q : _prefilters) {
				if (q.length()==0) { continue; }
				currentQuery.should(buildESQuery(q));								
			}
			
			searchSourceBuilder.query(currentQuery);
			countSourceBuilder.query(currentQuery);
		}
		
		// Sorting (by fieldName)
		for (IPair<String,SORTING_ORDER> curSort : _sortByFieldName) {
			SortOrder order = SortOrder.ASC;
			
			String sortFieldName=curSort.getFirst();			
			if (curSort.getSecond()==SORTING_ORDER.DESC) { order = SortOrder.DESC; }
			
			searchSourceBuilder.sort(new FieldSortBuilder(sortFieldName).order(order));
		}
				
		// by score
		searchSourceBuilder.sort(new ScoreSortBuilder());
		
		// for same score, by modification date (more recent first)
		if (_applicativeTiemstampFieldName!=null && _applicativeTiemstampFieldName.length()>0) {
			searchSourceBuilder.sort(new FieldSortBuilder(_applicativeTiemstampFieldName).order(SortOrder.DESC));
		}	
		_searchRequest.source(searchSourceBuilder);
		_countRequest.source(countSourceBuilder);
	}
	

	/**
	 * Retrieve list of items from ElasticSearch
	 * @param c the catalog (aka the ES index)
	 * @param query actually query to be substractivly applied (AND) to get searched items
	 * @param prefilter list of queries additionally (OR) combined to build a pool on which to perform search
	 * @param sort list of fields to sort from, with associated order (ASC or DESC)
	 * @param ds
	 * @throws ESDataProcessException
	 */
	public ESDocumentsRequestBuilder(String indexName, 
			String query,
			List<String> prefilters, 
			List< IPair<String,SORTING_ORDER> > sort,
			String applicativeTimestampFieldName, // for sorting by modif timestamp, ignored if null
			List<String> sourceFields /*null to retrieve all*/
			) 
					throws DataProcessException {
		_prefilters=prefilters;
		_query=query;
		_sortByFieldName=sort;
		_applicativeTiemstampFieldName=applicativeTimestampFieldName;
		_searchRequest = new SearchRequest(indexName);
		_countRequest = new CountRequest(indexName);
		_sourceFieldsToRetrieve=sourceFields;
		init();
	}

	/**
	 * Retrieve list of items from ElasticSearch
	 * @param c the catalog (aka the ES index)
	 * @param fromIdx the 'fromIdx' of elements to retrieve
	 * @param size how many max elements to retrieve
	 * @param query actually query to be substractivly applied (AND) to get searched items
	 * @param prefilter list of queries additionally (OR) combined to build a pool on which to perform search
	 * @param sort list of fields to sort from, with associated order (ASC or DESC)
	 * @param ds
	 * @throws ESDataProcessException
	 */
	public ESDocumentsRequestBuilder(String indexName, Integer fromIdx, Integer size, 
			String query,
			List<String> prefilters, 
			List< IPair<String,SORTING_ORDER> > sort,
			String applicativeTimestampFieldName, // for sorting by modif timestamp, ignored if null
			List<String> sourceFields /*null to retrieve all*/
			) 
					throws DataProcessException { 
		
		_fromIdx=fromIdx;
		_size=size;
		_prefilters=prefilters;
		_query=query;
		_sortByFieldName=sort;
		_applicativeTiemstampFieldName=applicativeTimestampFieldName;
		_searchRequest = new SearchRequest(indexName);
		_countRequest = new CountRequest(indexName);
		_sourceFieldsToRetrieve=sourceFields;
		init();
	}
	
	
	

	public SearchRequest getSearchRequest() {
		return _searchRequest;
	}

	public CountRequest getCountRequest() {
		return _countRequest;
	}

	public Integer getFromIdx() {
		return _fromIdx;
	}
	public Integer getSize() {
		return _size;
	}

	
					
};
