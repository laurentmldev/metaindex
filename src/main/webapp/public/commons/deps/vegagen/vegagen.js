

// Vega Generator Replacement strings
var VEGAGEN_RPL_INDEXNAME="%IndexName%";
var VEGAGEN_RPL_NAMEFIELD="%NameField%";
var VEGAGEN_RPL_GROUPFIELD="%GroupField%";
var VEGAGEN_RPL_REFSFIELD="%RefsField%";
var VEGAGEN_RPL_NODESQUERY="%NodesQuery%";
var VEGAGEN_RPL_LINKSFILTER="%LinksFilter%";

var VEGAGEN_DEFAULT_ID_OFFSET="-1";

var VEGAGEN_QUERY_MATCHALL={ "match_all": {} };
var VEGAGEN_QUERY_FIELDSVALUES={
    "query_string" : {
        "query" : VEGAGEN_RPL_NODESQUERY,
        "default_field" : "content"
     }
};


