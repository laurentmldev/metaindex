var VEGAGEN_CREATE_MXGRAPH_ID={"type": "identifier", "as": "mxgraph_id"};
var VEGAGEN_RPL_GROUPSTITLE="VEGAGEN_RPL_GROUPSTITLE";
var VEGAGEN_RPL_LINKSFILTER="VEGAGEN_RPL_LINKSFILTER";

var VEGAGEN_RELGRAPH_NODEDATA_ELK_TEMPLATE={
	      "name": "node-data",
	      "url": {
	        "index": VEGAGEN_RPL_INDEXNAME,
	        "body": {
	          "query": VEGAGEN_QUERY_MATCHALL,
	          "_source": "*",
	          "size": 10000
	        }
	      },
	      "format": {"property": "hits.hits"},
	      "transform": [
	    	{
	          "type": "formula",
	          "expr": VEGAGEN_RPL_NAMEFIELD,
	          "as": "mxgraph_name"
	        },
	        {
	          "type": "formula",
	          "expr": "!datum._source."+VEGAGEN_RPL_GROUPFIELD+" ? 'default' : datum._source."+VEGAGEN_RPL_GROUPFIELD,
	          "as": "mxgraph_group"
	        },	        
	        {
	          "type": "formula",
	          "expr": "!datum._source."+VEGAGEN_RPL_REFSFIELD+" ? '' : datum._source."+VEGAGEN_RPL_REFSFIELD,
	          "as": "mxgraph_refs"
	        }
	      ]
	    };

var VEGAGEN_RELGRAPH_DOTSDATAFLATTEN_ELK_TEMPLATE={
		  "name": "dots-data-flatten",
	      "source": "node-data",	      
	      "transform": [
	    	{
              "type": "formula",
              "expr": "split(datum.mxgraph_refs, ',')",
              "as": "mxgraph_ref"
            },
            {
              "type": "flatten",
              "fields": [
                "mxgraph_ref"
              ]
           }
	      ]
	    };

var VEGAGEN_RELGRAPH_DOTSDATA_ELK_TEMPLATE={
	     "name": "dots-data",
	      "source": "node-data",
	      "transform": [
	        {
	          "type": "formula",
	          "expr": "split(datum.mxgraph_refs, ',')",
	          "as": "mxgraph_ref"
	        },
	        {
	          "type": "flatten",
	          "fields": [
	            "mxgraph_ref"
	          ]
	        },
	        {
	          "type": "filter",
	          "expr": "length(datum.mxgraph_refs)>0 || indata('dots-data-flatten','mxgraph_ref', datum._id)"
	        },
	        {
	          "type": "aggregate",
	          "groupby": ["_id"],
	        },
	        {
	          "type": "lookup",
	          "from": "node-data",
	          "key": "_id",
	          "fields": [
	            "_id"
	          ],
	          "as": [
	            "dotObj"
	          ]
	        },
	        {
	          "type": "formula",
	          "expr": "datum.dotObj.mxgraph_name",
	          "as": "mxgraph_name"
	        },
	        {
	          "type": "formula",
	          "expr": "datum.dotObj.mxgraph_group",
	          "as": "mxgraph_group"
	        },
	        {
	          "type": "formula",
	          "expr": "datum.dotObj.mxgraph_refs",
	          "as": "mxgraph_refs"
	        },
	        {
	          "type": "identifier",
	          "as": "mxgraph_id"
	        },
	      ]
};
		

//Object MetaindexJSAPI
function VegagenRelGraphDocs() 
{
	if (!pako) {
		console.log("[Metaindex] Error: pako.js required but not found. Please include this lib, ex: <script  src=\"mydeps/pako.min.js\"></script>");
		return null;
	}
	var myself=this;
	
	myself._graph_template = {
			  "$schema": "https://vega.github.io/schema/vega/v4.json",
			  "padding": 0,
			  "autosize": "fit",
			  "signals": [
			    {
					"name": "zoomFactor",
					"value": 1,
					"bind": {"input": "range", "min": 0.01, "max": 2, "step": 0.001}
		    	},
		    	{"name": "showLabels", "value": true, "bind": {"input": "checkbox"}},
			    {
			      "name": "nodeRadius",
			      "value": 40,
			      "bind": {"input": "range", "min": 1, "max": 100, "step": 1}
			    },
			    {
			      "name": "nodeCharge",
			      "value": -5,
			      "bind": {"input": "range", "min": -500, "max": 500, "step": 1}
			    },
			    {
			      "name": "linkDistance",
			      "value": 150,
			      "bind": {"input": "range", "min": 5, "max": 1000, "step": 1}
			    },
			    {"name": "static", "value": false, "bind": {"input": "checkbox"}},
			    {
			      "name": "velocity",
			      "value": 0.997,
			      "bind": {"input": "range", "min": 0.9, "max": 1, "step": 0.001}
			    },
			    {"name": "cx", "update": "width / 2"},
			    {"name": "cy", "update": "height / 2"},
			    
			    {
			      "description": "State variable for active node fix status.",
			      "name": "fix",
			      "value": false,
			      "on": [
			        {
			          "events": "symbol:mouseout[!event.buttons], window:mouseup",
			          "update": "false"
			        },
			        {"events": "symbol:mouseover", "update": "fix || true"},
			        {
			          "events": "[symbol:mousedown, window:mouseup] > window:mousemove!",
			          "update": "xy()",
			          "force": true
			        }
			      ]
			    },
			    {
			      "description": "Graph node most recently interacted with.",
			      "name": "node",
			      "value": null,
			      "on": [
			        {
			          "events": "symbol:mouseover",
			          "update": "fix === true ? item() : node"
			        }
			      ]
			    },
			    {
			      "description": "Flag to restart Force simulation upon data changes.",
			      "name": "restart",
			      "value": false,
			      "on": [
			        {"events": {"signal": "fix"}, "update": "fix && fix.length"}
			      ]
			    }
			  ],
			  
			  "data": [
				  
				  {
				      "name": "link-data",
				      "source": "dots-data",
				      "transform": [
				       {
				          "type": "formula",
				          "expr": "split(datum.mxgraph_refs, ',')",
				          "as": "mxgraph_ref"
				        },
				        {
				          "type": "flatten",
				          "fields": [
				            "mxgraph_ref"
				          ]
				        },
				        
				        {
				          "type": "lookup",
				          "from": "dots-data",
				          "key": "_id",
				          "fields": [
				            "_id"
				          ],
				          "as": [
				            "sourceObj"
				          ]
				        },
				        {
				          "type": "filter",
				          "expr": "datum.sourceObj!=null"
				        },
				        {
				          "type": "lookup",
				          "from": "dots-data",
				          "key": "_id",
				          "fields": [
				            "mxgraph_ref"
				          ],
				          "as": [
				            "targetObj"
				          ]
				        },
				        {
				          "type": "filter",
				          "expr": "datum.targetObj!=null"
				        },
				        {
				          "type": "filter",
				          "expr": VEGAGEN_RPL_LINKSFILTER
				        },
				        {
				          "type": "formula",
				          "expr": "datum.sourceObj.mxgraph_id-1",
				          "as": "source"
				        },
				        {
				          "type": "formula",
				          "expr": "datum.targetObj.mxgraph_id-1",
				          "as": "target"
				        },
				        {
				          "type": "formula",
				          "expr": "!datum.linkcolor ? 'grey' : datum.linkcolor",
				          "as": "linkcolor"
				        },
				        {
				          "type": "formula",
				          "expr": "!datum.linkvalue ? 1 : datum.linkvalue",
				          "as": "linkvalue"
				        }
				      ]
				    },
			  ],
			  
			  "scales": [
				  {
				      "name": "nodecolor",
				      "type": "ordinal",
				      "domain": {"data": "node-data", "field": "mxgraph_group"},
				      "range": {"scheme": "set3"}
				   }
			  ],
			  "legends": [
				    {
				    "title": VEGAGEN_RPL_GROUPSTITLE,
			        "padding": 10,
			        "stroke": "nodecolor", 
			        "fill" : "nodecolor",
			        "type" : "symbol",
			        "direction" : "vertical",
			        "orient" : "right",
			        "symbolType": "circle",
			        "encode": {
			          "symbols": {
			            "enter": {
			              "fillOpacity": {"value": 0.5},
			            }
			          }
			        },
			        "fillColor" : "#eef",
			        "strokeColor" : "#ccc"
			      }
			   ],
			  "marks": [
				  {
				      "name": "dots",
				      "type": "symbol",
				      "zindex": 1,
				      "from": {
				        "data": "dots-data"
				      },
				      "on": [
				        {
				          "trigger": "fix",
				          "modify": "node",
				          "values": "fix === true ? {fx: node.x, fy: node.y} : {fx: fix[0], fy: fix[1]}"
				        },
				        {
				          "trigger": "!fix",
				          "modify": "node",
				          "values": "{fx: null, fy: null}"
				        }
				      ],
				      "encode": {
				        "enter": {
				          "fill": {
				            "scale": "nodecolor",
				            "field": "mxgraph_group"
				          },
				          "stroke": {
				            "value": "grey"
				          }
				        },
				        "update": {
				          "size": {
				            "signal": "2 * nodeRadius * nodeRadius * zoomFactor"
				          },
				          "cursor": {
				            "value": "pointer"
				          }
				        }
				      },
				      "transform": [
				        {
				          "type": "force",
				          "signal": "force-nodes",
				          "iterations": 10,
				          "restart": {
				            "signal": "restart"
				          },
				          "static": {
				            "signal": "static"
				          },
				          "velocityDecay": {
				            "signal": "velocity"
				          },
				          "forces": [
				            {
				              "force": "center",
				              "x": {
				                "signal": "cx"
				              },
				              "y": {
				                "signal": "cy"
				              }
				            },
				            {
				              "force": "collide",
				              "radius": {
				                "signal": "nodeRadius*zoomFactor"
				              }
				            },
				            {
				              "force": "nbody",
				              "strength": {
				                "signal": "nodeCharge"
				              }
				            },
				            {
				              "force": "link",
				              "links": "link-data",
				              "distance": {
				                "signal": "linkDistance*zoomFactor"
				              }
				            },

				          ]
				        }
				      ]
				    },
				    {
				      "type": "path",
				      "from": {
				        "data": "link-data"
				      },
				      "interactive": false,
				      "encode": {
				        "update": {
				          "stroke": {
				            "field": "linkcolor"
				          },
				          "strokeWidth": {
				            "field": "linkvalue"
				          }
				        }
				      },
				      "transform": [
				        {
				          "type": "linkpath",
				          "orient": "vertical",
				          "require": {
				            "signal": "force-nodes"
				          },
				          "shape": "diagonal",
				          "sourceX": "datum.source.x",
				          "sourceY": "datum.source.y",
				          "targetX": "datum.target.x",
				          "targetY": "datum.target.y"
				        }
				      ]
				    },
				    {
				      "type": "text",
				      "from": {
				        "data": "dots"
				      },
				      "zindex": 2,
				      "encode": {
				        "update": {
				          "x": {
				            "field": "x",
				            "offset": {
				              "signal": "showLabels ? -15 : 10000"
				            }
				          },
				          "y": {
				            "field": "y",
				            "offset": 5
				          },
				          "text": {
				            "field": "datum.mxgraph_name"
				          },
				          "fill": {
				            "value": "#000"
				          },
				          "fontSize": {
				            "value": 13
				          }
				        }
				      }
				    }
			  ]
			};
		
	myself._elkIndexName="";	
	myself._refsField=null;
	myself._nameField=null;
	myself._groupField=null;
	myself._queryJson=VEGAGEN_QUERY_MATCHALL;
	myself._linksFilterExpr="true";
	
	/*
	 * indexName : ElasticSearch index name
	 * refsField : name of the field containing references to other documents (_id)
	 * nameField : name of the field to be used for nodes labels
	 * groupField : name of the field to be used for assigning a color to the nodes
	 * queryJson : (optional) ElasticSearch json query (without the 'query' part) for selecting the bucket
	 * linksFilter : (optional) Filter links based on source or target nodes fields values, in javascript code. Ex: source.name=='Lolo' && target.value<2
	 */
	
	this.setNodesDefinition=function(indexName, refsField, nameField, groupField, queryStr, linksFilter) {
		myself._elkIndexName=indexName;
		myself._refsField=refsField;
		myself._groupField=groupField;
		
		nameField=nameField.replace(/\w+/g,function(txt) { return "datum._source."+txt; });
		myself._nameField=nameField;
		
	
	// Nodes Query		
		// empty query --> match all
		if (queryStr==null || queryStr.length==0) { myself._queryJson = VEGAGEN_QUERY_MATCHALL; }
		
		// full json query : use it as is
		else if (queryStr.startsWith('{')) {
			 myself._queryJson = JSON.parse(queryStr);
		}
		
		// query string
		else { 			
			let queryStrEvaluated = JSON.stringify(VEGAGEN_QUERY_FIELDSVALUES);
			queryStrEvaluated = queryStrEvaluated.replace(new RegExp(VEGAGEN_RPL_NODESQUERY,'g'),queryStr);
			myself._queryJson = JSON.parse(queryStrEvaluated);
		}
		
	// Links Filter
		if (linksFilter!=null) {
			linksFilter=linksFilter.replace(new RegExp("source\.",'g'),"datum.sourceObj.dotObj._source.");
			linksFilter=linksFilter.replace(new RegExp("target\.",'g'),"datum.targetObj.dotObj._source.");
			myself._linksFilterExpr=linksFilter;			
		}

	}
		
	this.getVegaCode=function() {
		
		let jsonBaseGraphStr = JSON.stringify(myself._graph_template);
		let groupTitle=myself._groupField.charAt(0).toUpperCase() + myself._groupField.substr(1).toLowerCase();
		jsonBaseGraphStr = jsonBaseGraphStr.replace(new RegExp(VEGAGEN_RPL_GROUPSTITLE, 'g'), groupTitle);		
		let jsonBaseGraph = JSON.parse(jsonBaseGraphStr);

		let dotsDefinitionStr = JSON.stringify(VEGAGEN_RELGRAPH_DOTSDATA_ELK_TEMPLATE,null, 2);		
		dotsDefinitionStr = dotsDefinitionStr.replace(new RegExp(VEGAGEN_RPL_INDEXNAME, 'g'), myself._elkIndexName);		
		dotsDefinitionStr = dotsDefinitionStr.replace(new RegExp(VEGAGEN_RPL_REFSFIELD, 'g'), myself._refsField);
		dotsDefinitionStr = dotsDefinitionStr.replace(new RegExp(VEGAGEN_RPL_NAMEFIELD, 'g'), myself._nameField);
		dotsDefinitionStr = dotsDefinitionStr.replace(new RegExp(VEGAGEN_RPL_GROUPFIELD, 'g'), myself._groupField);
		let dotsDefinitionJson = JSON.parse(dotsDefinitionStr);
		dotsDefinitionJson.transform.push(VEGAGEN_CREATE_MXGRAPH_ID);		
		jsonBaseGraph.data.unshift(dotsDefinitionJson); // push front the data
		
		let dotsflatDefinitionStr = JSON.stringify(VEGAGEN_RELGRAPH_DOTSDATAFLATTEN_ELK_TEMPLATE,null, 2);		
		dotsflatDefinitionStr = dotsflatDefinitionStr.replace(new RegExp(VEGAGEN_RPL_INDEXNAME, 'g'), myself._elkIndexName);		
		dotsflatDefinitionStr = dotsflatDefinitionStr.replace(new RegExp(VEGAGEN_RPL_REFSFIELD, 'g'), myself._refsField);
		dotsflatDefinitionStr = dotsflatDefinitionStr.replace(new RegExp(VEGAGEN_RPL_NAMEFIELD, 'g'), myself._nameField);
		dotsflatDefinitionStr = dotsflatDefinitionStr.replace(new RegExp(VEGAGEN_RPL_GROUPFIELD, 'g'), myself._groupField);
		let dotsflatDefinitionJson = JSON.parse(dotsflatDefinitionStr);
		jsonBaseGraph.data.unshift(dotsflatDefinitionJson); // push front the data

		let nodesDefinitionStr = JSON.stringify(VEGAGEN_RELGRAPH_NODEDATA_ELK_TEMPLATE,null, 2);		
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_INDEXNAME, 'g'), myself._elkIndexName);		
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_REFSFIELD, 'g'), myself._refsField);
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_NAMEFIELD, 'g'), myself._nameField);
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_GROUPFIELD, 'g'), myself._groupField);
		let nodesDefinitionJson = JSON.parse(nodesDefinitionStr);
		nodesDefinitionJson.url.body.query=myself._queryJson;		
		jsonBaseGraph.data.unshift(nodesDefinitionJson); // push front the data

		
		// stringify (pretty print)
		let graphStr=JSON.stringify(jsonBaseGraph,null, 2);		
		
		// apply links filter (default is 'true', which means no filter at all, take all of them
		graphStr=graphStr.replace(new RegExp(VEGAGEN_RPL_LINKSFILTER,'g'),myself._linksFilterExpr);
		
		return graphStr;
	}
	
}

