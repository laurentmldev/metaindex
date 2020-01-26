package metaindex.data.term.dbinterface;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import metaindex.data.catalog.ICatalog;
import metaindex.data.term.CatalogTerm;
import metaindex.data.term.CatalogTermRelation;
import metaindex.data.term.ICatalogTerm;
import metaindex.data.term.ICatalogTerm.RAW_DATATYPE;
import toolbox.database.elasticsearch.ESDataSource;
import toolbox.database.elasticsearch.ESWriteStmt;
import toolbox.exceptions.DataProcessException;

class CreateFieldIntoEsDbStmt extends ESWriteStmt<ICatalogTerm>   {

	ICatalog _catalog;
	List<ICatalogTerm> _data = new ArrayList<ICatalogTerm>();
	public CreateFieldIntoEsDbStmt(ICatalog c, List<ICatalogTerm> terms, ESDataSource ds) throws DataProcessException { 
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
				            
				            // join fields need name of parent/childs as extra information
				            if (ICatalogTerm.getRawDatatype(t.getDatatype()).equals(RAW_DATATYPE.Tjoin)) {
				            	builder.startObject("relations"); {
				            		CatalogTermRelation termRelation = (CatalogTermRelation) t;
				            		
				            		// add new relation parent/child names
				            		builder.field(termRelation.getParentRoleName(),termRelation.getChildRoleName());				            	
				            		
				            		// add in the request the list of other relations already existing in this catalog
				            		// i.e. defined in some other 'relation' terms
				            		Map<String,String> catalogExistingRelationsDef = _catalog.getTermsRelationsDefinitions();
				            		for (String parentName : catalogExistingRelationsDef.keySet()) {				            		
				            			String childName=catalogExistingRelationsDef.get(parentName);
				            			builder.field(parentName, childName);
				            		}
				            		
				            	} builder.endObject();
				            }
				            				            
				            // for text objects, add a keyword subtype as 'raw', in order to allow
				            // ElasticSearch to sort search results following this field
				            else if (ICatalogTerm.getRawDatatype(t.getDatatype())==RAW_DATATYPE.Ttext) {
				            	builder.startObject("fields");
				            	{
				            		builder.startObject("raw"); {
					            		builder.field("type", "keyword");
					            	} builder.endObject();
				            	} builder.endObject();
				            }
				            else if (ICatalogTerm.getRawDatatype(t.getDatatype())==RAW_DATATYPE.Tdate) {
		            			builder.field("ignore_malformed", "true");
		            			builder.field("null_value", "0000");
		            			builder.field("format", "yyyy||dd/MM/yy||dd/MM/yyyy||MM/yy||MM/yyyy||yyyy-MM-dd"
						            					+"||yyyy/MM/dd HH:mm:ss.SSS"
				    									+"||yyyy-MM-dd HH:mm:ss.SSS"
				    									+"||yyyy-MM-dd HH:mm"
				    									+"||dd-MM-yyyy HH:mm"
				    									+"||dd/MM/yyyy HH:mm"
		            									);
				            }
				            
				        } builder.endObject();
					}			    
			      } builder.endObject();
			} builder.endObject();
			request.source(builder);
					
			AcknowledgedResponse putMappingResponse = 
					this.getDatasource().getHighLevelClient().indices().putMapping(request, RequestOptions.DEFAULT);

			//waitUntilEsIndexRefreshed(_catalog.getName(),this.getDatasource());
			return putMappingResponse.isAcknowledged(); 				
			
		} catch (Exception e) {			
			throw new DataProcessException("unable to create ElasticSearch field : "+e.getMessage(),e);
		}
		
	}
	
	
	
						
};
