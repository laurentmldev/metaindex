

//Object MetaindexJSAPI
function VegagenRelGraphAggregatedGroups() 
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
		    	{
					"name": "linkFactor",
					"value": 0.2,
					"bind": {"input": "range", "min": 0.001, "max": 1, "step": 0.001}
		    	},
		    	{
			      "name": "nodeRadius",
			      "value": 40,
			      "bind": {"input": "range", "min": 0.01, "max": 100, "step": 0.1}
			    },
			    {
			      "name": "nodeCharge",
			      "value": -300,
			      "bind": {"input": "range", "min": -500, "max": 500, "step": 1}
			    },
			    {
			      "name": "linkDistance",
			      "value": 150,
			      "bind": {"input": "range", "min": 5, "max": 10000, "step": 1}
			    },
			    {"name": "static", "value": false, "bind": {"input": "checkbox"}},
			    {
			      "name": "velocity",
			      "value": 0.997,
			      "bind": {"input": "range", "min": 0.9, "max": 1, "step": 0.001}
			    },
			    
			    
			    
			    {"name": "showLabels", "value": true, "bind": {"input": "checkbox"}},
			    
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
				      "name": "group-nodes-data",
				      "source": "node-data",
				      "transform": [
				        {"type": "aggregate", "groupby": ["mxgraph_group"]},
				        {"type": "identifier", "as": "mxgraph_id"}
				      ]
				    },
				    {
				      "name": "link-data",
				      "source": "node-data",
				      "transform": [
				        {"type": "filter", "expr": "length(datum.mxgraph_refs)>0"},
				        {
				          "type": "formula",
				          "expr": "split(datum.mxgraph_refs, ',')",
				          "as": "mxgraph_ref"
				        },
				        {"type": "flatten", "fields": ["mxgraph_ref"]},				        
				        {
				          "type": "lookup",
				          "from": "group-nodes-data",
				          "key": "mxgraph_group",
				          "fields": ["mxgraph_group"],
				          "as": ["sourceGroupObj"]
				        },
				        {"type": "filter", "expr": "datum.sourceGroupObj!=null"},
				        {
				          "type": "lookup",
				          "from": "node-data",
				          "key": "_id",
				          "fields": ["mxgraph_ref"],
				          "as": ["targetObj"]
				        },
				        { 
				          "type" : "filter",
				          "expr" : "datum.targetObj!=null"
		                },
				        {
				          "type": "lookup",
				          "from": "group-nodes-data",
				          "key": "mxgraph_group",
				          "fields": ["targetObj.mxgraph_group"],
				          "as": ["targetGroupObj"]
				        },
				        {"type": "filter", "expr": "datum.targetGroupObj!=null"},				        
				        {
				          "type": "aggregate",
				          "groupby": ["sourceGroupObj.mxgraph_id", "targetGroupObj.mxgraph_id"]
				        },				        
				        {
				          "type": "formula",
				          "expr": "datum['sourceGroupObj.mxgraph_id']-1",
				          "as": "source"
				        },
				        {
				          "type": "formula",
				          "expr": "datum['targetGroupObj.mxgraph_id']-1",
				          "as": "target"
				        },
				        {
				          "type": "formula",
				          "expr": "!datum.linkcolor ? 'grey' : datum.linkcolor",
				          "as": "linkcolor"
				        },
				        {"type": "formula", "expr": "datum.count*zoomFactor*linkFactor", "as": "linkvalue"}
				      ]
				    }
			  ],
			  
			  "scales": [
				    {
				      "name": "nodecolor",
				      "type": "ordinal",
				      "domain": {"data": "group-nodes-data", "field": "mxgraph_group"},
				      "range": {"scheme": "set3"}
				    }
				  ],
				  
				  "legends": [
				    {
				      "title": VEGAGEN_RPL_GROUPSTITLE,
				      "padding": 10,
				      "stroke": "nodecolor",
				      "fill": "nodecolor",
				      "type": "symbol",
				      "direction": "vertical",
				      "orient": "right",
				      "symbolType": "circle",
				      "encode": {"symbols": {"enter": {"fillOpacity": {"value": 0.5}}}},
				      "fillColor": "#eef",
				      "strokeColor": "#ccc"
				    }
				  ],
				  
				  "marks": [
				    {
				      "name": "dots",
				      "type": "symbol",
				      "zindex": 1,
				      "from": {"data": "group-nodes-data"},
				      "on": [
				        {
				          "trigger": "fix",
				          "modify": "node",
				          "values": "fix === true ? {fx: node.x, fy: node.y} : {fx: fix[0], fy: fix[1]}"
				        },
				        {"trigger": "!fix", "modify": "node", "values": "{fx: null, fy: null}"}
				      ],
				      "encode": {
				        "enter": {
				          "fill": {"scale": "nodecolor", "field": "mxgraph_group"},
				          "stroke": {"value": "grey"}
				        },
				        "update": {
				          "size": {"signal": "5 * datum.count * nodeRadius*zoomFactor"},
				          "cursor": {"value": "pointer"}
				        }
				      },
				      "transform": [
				        {
				          "type": "force",
				          "signal": "force-nodes",
				          "iterations": 300,
				          "restart": {"signal": "restart"},
				          "static": {"signal": "static"},
				          "velocityDecay": {"signal": "velocity"},
				          "forces": [
				            {"force": "center", "x": {"signal": "cx"}, "y": {"signal": "cy"}},
				            {"force": "collide", "radius": {"signal": "nodeRadius*zoomFactor"}},
				            {"force": "nbody", "strength": {"signal": "nodeCharge"}},
				            {
				              "force": "link",
				              "links": "link-data",
				              "distance": {"signal": "linkDistance*zoomFactor"}
				            }
				          ]
				        }
				      ]
				    },
				    {
				      "type": "path",
				      "from": {"data": "link-data"},
				      "interactive": false,
				      "encode": {
				        "update": {
				          "stroke": {"field": "linkcolor"},
				          "strokeWidth": {"field": "linkvalue"}
				        }
				      },
				      "transform": [
				        {
				          "type": "linkpath",
				          "orient": "vertical",
				          "require": {"signal": "force-nodes"},
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
				      "from": {"data": "dots"},
				      "zindex": 2,
				      "encode": {
				        "update": {
				          "x": {"field": "x", "offset": {"signal": "showLabels ? -5 : 1000"}},
				          "y": {"field": "y", "offset": 5},
				          "text": {"field": "datum.mxgraph_group"},
				          "fill": {"value": "#000"},
				          "fontSize": {"value": 13}
				        }
				      }
				    }
				    
				  ]
				};

	myself._elkIndexName="";	
	myself._refsField=null;
	myself._groupField=null;
	myself._queryJson=VEGAGEN_QUERY_MATCHALL;	
	
	/*
	 * indexName : ElasticSearch index name
	 * refsField : name of the field containing references to other documents (_id)
	 * nameField : name of the field to be used for nodes labels
	 * groupField : name of the field to be used for assigning a color to the nodes
	 * queryJson : (optional) ElasticSearch json query (without the 'query' part) for selecting the bucket
	 */
	
	this.setNodesDefinition=function(indexName, refsField, groupField, queryStr) {
		myself._elkIndexName=indexName;
		myself._refsField=refsField;
		myself._groupField=groupField;
	
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
		
	}
		
	this.getVegaCode=function() {
		
		let jsonBaseGraphStr = JSON.stringify(myself._graph_template);
		let groupTitle=myself._groupField.charAt(0).toUpperCase() + myself._groupField.substr(1).toLowerCase();		
		jsonBaseGraphStr = jsonBaseGraphStr.replace(new RegExp(VEGAGEN_RPL_GROUPSTITLE, 'g'), groupTitle);		
		let jsonBaseGraph = JSON.parse(jsonBaseGraphStr);
		
		let nodesDefinitionStr = JSON.stringify(VEGAGEN_RELGRAPH_NODEDATA_ELK_TEMPLATE,null, 2);
		console.log(nodesDefinitionStr);
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_INDEXNAME, 'g'), myself._elkIndexName);		
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_REFSFIELD, 'g'), myself._refsField);
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_NAMEFIELD, 'g'), "'_noname_'");
		nodesDefinitionStr = nodesDefinitionStr.replace(new RegExp(VEGAGEN_RPL_GROUPFIELD, 'g'), myself._groupField);
		let nodesDefinitionJson = JSON.parse(nodesDefinitionStr);
		nodesDefinitionJson.url.body.query=myself._queryJson;
		
		// push front the data
		jsonBaseGraph.data.unshift(nodesDefinitionJson);
		
		// stringify (pretty print)
		let graphStr=JSON.stringify(jsonBaseGraph,null, 2);		
		
		return graphStr;
	}
	
}

