package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONObject;
import org.elasticsearch.search.sort.ScoreSortBuilder;

import metaindex.data.commons.database.MxDbSearchItem;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import metaindex.data.term.ICatalogTerm.TERM_DATATYPE;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESReadStmt;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPair;

class GetItemsFromDbStmt extends ESReadStmt<DbSearchResult>   {
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private SearchRequest _request;
	private Integer _fromIdx;
	private Integer _size;
	private ICatalog _catalog;
	private List<String> _prefilters = new ArrayList<String>();	
	private String _query;
	private List< IPair<String,SORTING_ORDER> > _sortByFieldName = new ArrayList< IPair<String,SORTING_ORDER> >();
	
	
	private QueryBuilder buildESQuery(String queryStr) {
		// if its a valid JSON string we consider it is an ElasticSearch raw query
		try { new JSONObject(queryStr); }
		catch (Exception e) { return QueryBuilders.queryStringQuery(queryStr); } 
		return QueryBuilders.wrapperQuery(queryStr);		
				
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
	public GetItemsFromDbStmt(ICatalog c, Integer fromIdx, Integer size, 
			String query,
			List<String> prefilters, 
			List< IPair<String,SORTING_ORDER> > sort, 
			ESDataSource ds) throws DataProcessException { 
		super(ds);
		_catalog=c;
		_fromIdx=fromIdx;
		_size=size;
		_prefilters=prefilters;
		_query=query;
		_sortByFieldName=sort;
		
		// ES index matches catalog name
		_request = new SearchRequest(c.getName());   			
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.from(_fromIdx);
		searchSourceBuilder.size(_size);
		
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
		}
		
		// Sorting (by fieldName)
		for (IPair<String,SORTING_ORDER> curSort : _sortByFieldName) {
			SortOrder order = SortOrder.ASC;
			if (curSort.getSecond()==SORTING_ORDER.DESC) { order = SortOrder.DESC; }
			
			// Ttext fields need to use our 'raw' Tkeyword subtype for sorting
			String sortFieldName=curSort.getFirst();			
			TERM_DATATYPE fieldType = c.getTerms().get(sortFieldName).getDatatype();
			if (ICatalogTerm.getRawDatatype(fieldType).equals(RAW_DATATYPE.Ttext)) {
				sortFieldName+=".raw";
			}
			searchSourceBuilder.sort(new FieldSortBuilder(sortFieldName).order(order));
		}
				
		// by score
		searchSourceBuilder.sort(new ScoreSortBuilder());
		
		// for same score, by modification date (more recent first)
		searchSourceBuilder.sort(new FieldSortBuilder(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP).order(SortOrder.DESC));
				
		_request.source(searchSourceBuilder);		 			
	}

	
	@Override
	public List<DbSearchResult> execute() throws DataProcessException {
		try {
	
			List<DbSearchResult> resultList = new ArrayList<DbSearchResult>(); 
			SearchResponse searchResponse = this.getDatasource()
											.getHighLevelClient()
											.search(_request,RequestOptions.DEFAULT);
			
			DbSearchResult result = new DbSearchResult();
			
			if (searchResponse.status()!=RestStatus.OK) { 
				throw new DataProcessException("Error while performing search request : "+searchResponse.status()); 
			}
			
			if (searchResponse.getHits()!=null && searchResponse.getHits().getHits().length>0) {
			
				result.setTotalHits(searchResponse.getHits().getTotalHits().value);
				result.setFromIdx(_fromIdx);
				SearchHit[] hits = searchResponse.getHits().getHits();
				
				for (SearchHit h : hits) {
					Map<String,Object> data = h.getSourceAsMap();
					// handle elements not created by metaindex, missing lastmodif and userId special fields
					if (!data.containsKey(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP)) {
						data.put(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP, ICatalogTerm.MX_TERM_EPOCH_TIMESTAMP);
					}
					if (!data.containsKey(ICatalogTerm.MX_TERM_LASTMODIF_USERID)) {
						data.put(ICatalogTerm.MX_TERM_LASTMODIF_USERID, 0);
					}
					IDbItem item = new MxDbSearchItem(
												h.getId(), data,
												_catalog.getItemNameFields(),
												_catalog.getItemThumbnailUrlField(),
												_catalog.getItemsUrlPrefix());
					result.addItem(item);									
				}				
			}
			
			resultList.add(result);			
			return resultList;			
			
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			throw new DataProcessException(e.getRootCause().getMessage(),e);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new DataProcessException(e.getMessage(),e);
		}
	}					
};
