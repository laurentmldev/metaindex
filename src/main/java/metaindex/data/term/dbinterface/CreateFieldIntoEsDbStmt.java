package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import toolbox.database.elasticsearch.ElasticSearchConnector;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateFieldIntoEsDbStmt extends ESWriteStmt<ICatalogTerm>   {

	ICatalog _catalog;
	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public CreateFieldIntoEsDbStmt(ICatalog c, List<ICatalogTerm> terms, ElasticSearchConnector ds) throws DataProcessException { 
		super(ds);
		_data.addAll(terms);		
		_catalog=c;
	}
	
	@Override
	public Boolean execute() throws DataProcessException {
		PutMappingRequest request = new PutMappingRequest(_catalog.getName());
		
		XContentBuilder builder;
		try {
			builder = XContentFactory.jsonBuilder();
			
			builder.startObject(); {
				
			  builder.startObject("properties"); {
			      
			    	for (ICatalogTerm t : _data) {
			    		String termName=t.getRawFieldName();
			    		
			    		builder.startObject(termName); {
				        	// build directly elasticsearch type string from our FIELD_TYPE enum string,
				        	// removing prefix 'T' letter.
				        	String esTypeStr = ICatalogTerm.getRawDatatype(t.getDatatype()).toString();
				        	esTypeStr=esTypeStr.replaceFirst("T", "");
				            builder.field("type", esTypeStr);				            
				            				            
				            // for text objects, add a keyword subtype as 'raw', in order to allow
				            // ElasticSearch to sort search results following this field
				            if (ICatalogTerm.getRawDatatype(t.getDatatype())==RAW_DATATYPE.Ttext) {
				            	builder.field("fielddata", true);// allow significant terms search
				            	builder.startObject("fields");
				            	{
				            		builder.startObject("keyword"); {
					            		builder.field("type", "keyword");
					            	} builder.endObject();
				            	} builder.endObject();
				            }
				            else if (ICatalogTerm.getRawDatatype(t.getDatatype())==RAW_DATATYPE.Tdate) {
		            			builder.field("ignore_malformed", "true");
		            			builder.field("null_value", "1970/01/01");
		            			
		            			builder.field("format",  
		            									"yyyy"
		            											            									
		            									+"||MM/yy"
		            									+"||MM-yy"
		            									
		            									+"||MM/yyyy"
		            									+"||MM-yyyy"
		            									
		            									+"||yyyy/MM/dd"
		            									+"||yyyy/MM"
		            									+"||yyyy/MM/dd HH:mm"
														+"||yyyy/MM/dd HH:mm:ss"
		            									+"||yyyy/MM/dd HH:mm:ss.SSS"
				    									
														+"||yyyy-MM-dd"
														+"||yyyy-MM"
														+"||yyyy-MM-dd HH:mm"
														+"||yyyy-MM-dd HH:mm:ss"
						            					+"||yyyy-MM-dd HH:mm:ss.SSS"
				    													    									
														+"||dd-MM-yy"
														+"||dd-MM-yyyy"
														+"||dd-MM-yyyy HH:mm"
														+"||dd-MM-yyyy HH:mm:ss"
				    									+"||dd-MM-yyyy HH:mm:ss.SSS"
														
				    									+"||dd/MM/yy"
				    									+"||dd/MM/yyyy"
														+"||dd/MM/yyyy HH:mm"
				    									+"||dd/MM/yyyy HH:mm:ss"
				    									+"||dd/MM/yyyy HH:mm:ss.SSS"
		            									);

		            			builder.startObject("fields");
				            	{
				            		builder.startObject("keyword"); {
					            		builder.field("type", "keyword");
					            	} builder.endObject();
				            	} builder.endObject();
				            }
				            
				        } builder.endObject();
					}			    
			      } builder.endObject();
			} builder.endObject();
			request.source(builder);
					
			AcknowledgedResponse putMappingResponse = 
					this.getDataConnector().getHighLevelClient().indices().putMapping(request, RequestOptions.DEFAULT);

			//waitUntilEsIndexRefreshed(_catalog.getName(),this.getDatasource());
			return putMappingResponse.isAcknowledged(); 				
			
		} catch (Exception e) {			
			throw new DataProcessException("unable to create ElasticSearch field : "+e.getMessage(),e);
		}
		
	}
	
	
	
						
};
