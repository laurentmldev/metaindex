package metaindex.data.catalog.dbinterface;

/**
GNU GENERAL PUBLIC LICENSE
 Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.RequestLine;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;

import metaindex.data.commons.database.MxDbSearchItem;
import metaindex.data.catalog.Catalog;
import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.userprofile.IUserProfileData;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESReadStreamStmt;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.database.DbSearchResult;
import toolbox.database.IDbItem;
import toolbox.exceptions.DataProcessException;

class UpdateFieldValueIntoDbStmt extends ESWriteStmt<IDbItem>   {

	
	private Log log = LogFactory.getLog(Catalog.class);
	

	// necessary for functional 'join' fields
	private static final Integer DEFAULT_ES_UPDATEFIELD_ROUTING = 1;
	private ICatalog _catalog; 
	private IUserProfileData _user;
	private String _docId;
	private String _fieldName;
	private Object _fieldValue;
	private Date _timestamp;
	
	
	public UpdateFieldValueIntoDbStmt(IUserProfileData u,ICatalog c, String docId, String fieldName, Object fieldValue, Date timestamp,ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_catalog=c;
		_user=u;
		_docId=docId;
		_fieldName=fieldName;
		_fieldValue=fieldValue;
		_timestamp=timestamp;
			 			
	}

	
	@Override
	public Boolean execute() throws DataProcessException {
		try {
			
			UpdateRequest request = new UpdateRequest(_catalog.getName(), _docId);
			
			// necessary for functional 'join' fields
			request.routing(DEFAULT_ES_UPDATEFIELD_ROUTING.toString());
			
			Map<String, Object> jsonMap = new HashMap<>();					
			
			// basic fields values are encoding as string
			if (_fieldValue instanceof String) {
				jsonMap.put(_fieldName, _fieldValue);			
			// more complex fields might use structured description (in JSON)
			} else if (_fieldValue instanceof JSONObject) {
				JSONObject paramDef = new JSONObject();
				paramDef.append(_fieldName, _fieldValue);
				String jsonString =  paramDef.toString();
				request.doc(jsonString,XContentType.JSON);
			}
			jsonMap.put(ICatalogTerm.MX_TERM_LASTMODIF_TIMESTAMP,ICatalogTerm.MX_TERM_DATE_FORMAT.format(_timestamp));
			jsonMap.put(ICatalogTerm.MX_TERM_LASTMODIF_USERID, _user.getId());			
			request.doc(jsonMap);
			UpdateResponse updateResponse = this.getDataConnector()
					.getHighLevelClient().update(request, RequestOptions.DEFAULT);
			
			ReplicationResponse.ShardInfo shardInfo = updateResponse.getShardInfo();
			/* if number of succesfull shards is not same as total number of shards involved in transaction 
			if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
				throw new ESDataProcessException("Unable to update document "+_docId+" : "+shardInfo.);
			}
			*/
			if (shardInfo.getFailed() > 0) {
				String reasons="";
			    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
			    	reasons += failure.reason()+" "; 
			    }
			    throw new DataProcessException("Unable to update document "+_docId+" : "+reasons);
			}
			return true;			
			
		} catch (ElasticsearchException e) {
			e.printStackTrace();
			if (e.status() == RestStatus.NOT_FOUND) {
				throw new DataProcessException("document "+_docId+" does not exist (anymore?) : "+e.getMessage(),e);
		    }
			else if (e.status() == RestStatus.CONFLICT) {
				throw new DataProcessException("version conflict while updating document "+_docId+" : "+e.getMessage(),e);
		    } 
			else if (e.status() == RestStatus.BAD_REQUEST) {
				throw new DataProcessException("bad request while updating document "+_docId+" : "+e.getMessage(),e);
			}
			else if (e.status() == RestStatus.FORBIDDEN
					|| e.status() == RestStatus.UNAUTHORIZED) {
				throw new DataProcessException("access-rights refused while updating document "+_docId+" : "+e.getMessage(),e);
			}
			
			throw new DataProcessException("an error occured while updating document "+_docId+" : "+e.getMessage(), e);
			
		}	catch (IOException e) {
			e.printStackTrace();
			throw new DataProcessException("I/O error while updating document "+_docId+" : "+e.getMessage(), e);
		}			
	}					
};
