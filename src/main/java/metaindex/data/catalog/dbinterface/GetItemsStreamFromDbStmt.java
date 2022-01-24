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
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;

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

class GetItemsStreamFromDbStmt extends ESReadStreamStmt<DbSearchResult>   {
	
	private Log log = LogFactory.getLog(Catalog.class);
	
	private ICatalog _catalog;
	private ESDocumentsRequestBuilder _requestsBuilder;
	
	// nb docs with been going through, starting from 1
	private Long _curDocIdx=0L;
	private Long _curNbDocs=0L;	
	private Long _fromIdx=-1L;
	private Long _maxNbDocs=-1L;
	
	// -1 = no limit
	private Integer _strFieldsMaxLength=0;
	
	private String _query="";
	private List<String> _prefilters = new ArrayList<>();
	private List<String> _sourceFieldsList = null;
	private List< IPair<String,SORTING_ORDER> > _sort = new ArrayList<>();
	
	/**
	 * Retrieve list of items from ElasticSearch
	 * @param c the catalog (aka the ES index)
	 * @param fromIdx the 'fromIdx' of elements to retrieve
	 * @param size how many max elements to retrieve. -1 to get all elements.
	 * @param query actually query to be substractivly applied (AND) to get searched items
	 * @param prefilter list of queries additionally (OR) combined to build a pool on which to perform search
	 * @param sort list of fields to sort from, with associated order (ASC or DESC)
	 * @param ds
	 * @throws ESDataProcessException
	 */
	public GetItemsStreamFromDbStmt(ICatalog c, 
			Long fromIdx, 
			Long size, 
			String query,
			List<String> prefilters, 
			List< IPair<String,SORTING_ORDER> > sort, 
			Integer strFieldsMaxLength,
			ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_catalog=c;
		_fromIdx=fromIdx;
		_maxNbDocs=size;
		_query=query;
		_prefilters=prefilters;
		_sort=sort;
		_strFieldsMaxLength=strFieldsMaxLength;
		
		// Applying proper raw field for terms requiring it
		for (IPair<String,SORTING_ORDER> curSort : _sort) {
			String sortFieldName=curSort.getFirst();			
			TERM_DATATYPE fieldType = c.getTerms().get(sortFieldName).getDatatype();
			if (ICatalogTerm.getRawDatatype(fieldType).equals(RAW_DATATYPE.Ttext)) {
				curSort.setFirst(sortFieldName+=".keyword");
			}			
		}
		
		_requestsBuilder=new ESDocumentsRequestBuilder(
				_catalog.getName(),_query,_prefilters,_sort,ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP,null);
	}

	/**
	 * 
	 * @param sourceFieldsList list of fields to actually retrieve from document contents. If empty, retrieve only _id.
	 * @throws DataProcessException
	 */
	public GetItemsStreamFromDbStmt(ICatalog c, 
			Long fromIdx, 
			Long size, 
			String query,
			List<String> prefilters, 
			List< IPair<String,SORTING_ORDER> > sort, 
			Integer strFieldsMaxLength,
			List<String> sourceFieldsList,
			ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_catalog=c;
		_fromIdx=fromIdx;
		_maxNbDocs=size;
		_query=query;
		_prefilters=prefilters;
		_sort=sort;
		_strFieldsMaxLength=strFieldsMaxLength;
		
		// Applying proper raw field for terms requiring it
		for (IPair<String,SORTING_ORDER> curSort : _sort) {
			String sortFieldName=curSort.getFirst();			
			TERM_DATATYPE fieldType = c.getTerms().get(sortFieldName).getDatatype();
			if (ICatalogTerm.getRawDatatype(fieldType).equals(RAW_DATATYPE.Ttext)) {
				curSort.setFirst(sortFieldName+=".keyword");
			}			
		}
		
		_requestsBuilder=new ESDocumentsRequestBuilder(
				_catalog.getName(),_query,_prefilters,_sort,
				ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP,sourceFieldsList);

	}

	private void streamSearchHits(IStreamHandler<DbSearchResult> hitsStreamHandler,SearchHit[] searchHits) {
		
		
		try {
			List<DbSearchResult> resultList= new ArrayList<>();
			DbSearchResult result = new DbSearchResult();
			result.setFromIdx(this.getFromIdx());
			result.setTotalHits(this.getMaxNbDocs());
			
			// check if thumbnail field is multi
			// (because in that case we use the first pic as a thumbnail
			Boolean isThumbnailFieldMulti=false;			
			ICatalogTerm thumbnailUrlTerm = _catalog.getTerms().get(_catalog.getItemThumbnailUrlField());
			if (thumbnailUrlTerm!=null) { isThumbnailFieldMulti=thumbnailUrlTerm.getIsMultiEnum(); }
		
			for (SearchHit hit : searchHits) {
		    	_curDocIdx++;
		    	// skipping first elements until we reach required position
		    	if (_curDocIdx<_fromIdx) { continue; }
		    	
				Map<String,Object> data = hit.getSourceAsMap();
				// if no source field requested, the data map might be null
				// so we replace it by an empty map to make smoother further processing
				if (data==null) { data=new HashMap<String,Object>(); }
				GetItemsFromDbStmt.filterData(data,_strFieldsMaxLength);
				
				IDbItem item = new MxDbSearchItem(
											hit.getId(), data,
											_catalog.getItemNameFields(),
											_catalog.getItemThumbnailUrlField(),
											_catalog.getItemsUrlPrefix(),
											isThumbnailFieldMulti);
				result.addItem(item);
				_curNbDocs++;
				if (_curNbDocs>=_maxNbDocs) { break; }
			}
			//log.error("#### streaming "+result.getItems().size()+" documents ("+_curNbDocs+"/"+_maxNbDocs+")"); 
			resultList.add(result);
			hitsStreamHandler.handle(resultList);
			
		} catch (DataProcessException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(IStreamHandler<DbSearchResult> hitsStreamHandler) throws DataProcessException {
		try {
			
			
			
			// first get total count (even if over max EL search size limit
			CountResponse countResponse = this.getDataConnector()
											.getHighLevelClient()
											.count(_requestsBuilder.getCountRequest(),RequestOptions.DEFAULT);
			
			DbSearchResult result = new DbSearchResult();
			
			final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
			_requestsBuilder.getSearchRequest().scroll(scroll);
			
			SearchResponse searchResponse = this.getDataConnector()
					.getHighLevelClient().search(_requestsBuilder.getSearchRequest(), RequestOptions.DEFAULT); 
			
			if (searchResponse.status()!=RestStatus.OK) { 
				throw new DataProcessException("Error while performing search request : "+searchResponse.status()); 
			}
			
			result.setTotalHits(countResponse.getCount());
			if (_maxNbDocs==-1 || countResponse.getCount()<_maxNbDocs) { _maxNbDocs=countResponse.getCount(); }
			result.setFromIdx(this.getFromIdx());
			
			String scrollId = searchResponse.getScrollId();
			SearchHit[] searchHits = searchResponse.getHits().getHits();
			
			streamSearchHits(hitsStreamHandler,searchHits);
			
			while (searchHits != null
					&& searchHits.length > 0 
					&& _curNbDocs<_maxNbDocs) { 
			    
			    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId); 
			    scrollRequest.scroll(scroll);
			    searchResponse = this.getDataConnector()
						.getHighLevelClient().scroll(scrollRequest, RequestOptions.DEFAULT);
			    scrollId = searchResponse.getScrollId();
			    searchHits = searchResponse.getHits().getHits();
			    
			    streamSearchHits(hitsStreamHandler,searchHits);
			    
			}
			
			ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); 
			clearScrollRequest.addScrollId(scrollId);
			ClearScrollResponse clearScrollResponse = this.getDataConnector()
					.getHighLevelClient().clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
			
			if (!clearScrollResponse.isSucceeded()) {
				log.error("ES clearScroll operation did not succeed");
			}
					
			
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			throw new DataProcessException(e.getRootCause().getMessage(),e);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new DataProcessException(e.getMessage(),e);
		}
	}


	public Long getFromIdx() {
		return _fromIdx;
	}

	public Long getCurDocIdx() {
		return _curDocIdx;
	}

	public Long getMaxNbDocs() {
		return _maxNbDocs;
	}

				
};
