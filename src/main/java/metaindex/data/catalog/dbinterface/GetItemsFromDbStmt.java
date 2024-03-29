package metaindex.data.catalog.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
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
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESDocumentsRequestBuilder;
import toolbox.database.elasticsearch.ESReadStreamStmt;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.database.IDbSearchResult.SORTING_ORDER;
import toolbox.exceptions.DataProcessException;
import toolbox.utils.IPair;
import toolbox.utils.IStreamHandler;

class GetItemsFromDbStmt extends ESReadStreamStmt<DbSearchResult>   {
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private ICatalog _catalog;
	private ESDocumentsRequestBuilder _requestsBuilder;
	private String _query="";
	
	// -1 = no limit
	private Integer _strFieldsMaxLength=0;
	
	public static void filterData(Map<String,Object> data, Integer strMaxLength) {
		// handle elements not created by metaindex, missing lastmodif and userId special fields
		if (!data.containsKey(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP)) {
			data.put(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP, ICatalogTerm.MX_TERM_EPOCH_TIMESTAMP);
		}
		if (!data.containsKey(ICatalogTerm.MX_TERM_LASTMODIF_USERID)) {
			data.put(ICatalogTerm.MX_TERM_LASTMODIF_USERID, 0);
		}
		// truncate too long fields to longuest authorized length
		// dedicated viewing shall be used for very long values.
		if (strMaxLength>0) {
			for (String k : data.keySet()) {
				Object o = data.get(k);
				if (o instanceof String && ((String)o).length()>strMaxLength) {
					String truncatedStr=((String)o).substring(0,strMaxLength);
					data.put(k,truncatedStr+" ...");
				}
			}
		}		
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
			Integer strFieldsMaxLength,
			ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_catalog=c;
		_query=query;
		_strFieldsMaxLength=strFieldsMaxLength;
		
		// Applying proper raw field for terms requiring it
		for (IPair<String,SORTING_ORDER> curSort : sort) {
			String sortFieldName=curSort.getFirst();			
			TERM_DATATYPE fieldType = c.getTerms().get(sortFieldName).getDatatype();
			if (ICatalogTerm.getRawDatatype(fieldType).equals(RAW_DATATYPE.Ttext)) {
				curSort.setFirst(sortFieldName+=".keyword");
			}			
		}
		_requestsBuilder=new ESDocumentsRequestBuilder(
				c.getName(), fromIdx,size,query,prefilters,sort,ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP,null);
				
	}

	
	@Override
	public void execute(IStreamHandler<DbSearchResult> h) throws DataProcessException {
		try {
	
			List<DbSearchResult> resultList = new ArrayList<DbSearchResult>();
			
			// first get total count (even if over max EL search size limit
			CountResponse countResponse = this.getDataConnector()
											.getHighLevelClient()
											.count(_requestsBuilder.getCountRequest(),RequestOptions.DEFAULT);
			
			SearchResponse searchResponse = this.getDataConnector()
											.getHighLevelClient()
											.search(_requestsBuilder.getSearchRequest(),RequestOptions.DEFAULT);
			
			DbSearchResult result = new DbSearchResult();
			
			// check if thumbnail field is multi
			// (because in that case we use the first pic as a thumbnail
			Boolean isThumbnailFieldMulti=false;			
			ICatalogTerm thumbnailUrlTerm = _catalog.getTerms().get(_catalog.getItemThumbnailUrlField());
			if (thumbnailUrlTerm!=null) { isThumbnailFieldMulti=thumbnailUrlTerm.getIsMultiEnum(); }
			
			if (searchResponse.status()!=RestStatus.OK) { 
				throw new DataProcessException("Error while performing search request : "+searchResponse.status()); 
			}
			
			if (searchResponse.getHits()!=null && searchResponse.getHits().getHits().length>0) {
			
				result.setTotalHits(countResponse.getCount());
				result.setFromIdx(new Long(_requestsBuilder.getFromIdx()));
				SearchHit[] hits = searchResponse.getHits().getHits();
				
				
				
				for (SearchHit hit : hits) {
					Map<String,Object> data = hit.getSourceAsMap();
					filterData(data,this._strFieldsMaxLength);
										
					IDbItem item = new MxDbSearchItem(
												hit.getId(), data,
												_catalog.getItemNameFields(),
												_catalog.getItemThumbnailUrlField(),
												_catalog.getItemsUrlPrefix(),
												isThumbnailFieldMulti
												);
					result.addItem(item);									
				}				
			}
			
			resultList.add(result);			
			h.handle(resultList);			
			
		// ex: occurs when syntax error in user search query 
		} catch (ElasticsearchStatusException e) {
			throw new DataProcessException("Possibly wrong query syntax : "+_query+" : "+e.getMessage(),e);
		} catch (ElasticsearchException e) {
			//e.printStackTrace();
			throw new DataProcessException(e.getRootCause().getMessage(),e);
		}
		catch (Exception e) {
			//e.printStackTrace();
			throw new DataProcessException(e.getMessage(),e);
		}
	}					
};
