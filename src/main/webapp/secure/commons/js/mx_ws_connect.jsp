<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  
<c:url value="/" var="mxurl"/>

<script  src="${mxurl}public/commons/deps/sockjs-0.3.min.js"></script>
<script  src="${mxurl}public/commons/deps/stomp.min.js"></script>
<script src="${mxurl}public/commons/js/metaindex.js"></script>

<!-- Pako for GZip inflate/deflate for big communication with server  -->
<script src="${mxurl}public/commons/deps/pako.min.js"></script>

<script type="text/javascript">

// initialized when calling mx_ws_connect function
var MxApi=null;

// event : HEARTBEAT_TIMEOUT|FAILURE|MAINTENANCE|STOPPED|RUNNING
function handleMxWsHeartbeatEvent(event) {
	if (event=='HEARTBEAT_TIMEOUT') {
		footer_showAlert(ERROR, "<s:text name="metaindex.disconnected" />",null,999999999);
	}
	
	else if (event=='FAILURE') { 
		footer_showAlert(ERROR, "Metaindex Server Failure detected",null,999999999);
	}
	else if (event=='RUNNING') { 
		// do nothing special
	}
} 
function mx_ws_connect(mxHost,mxApiConnectionParams, onConnectFunc) {
	
	// from metaindex.js	
	MxApi = new MetaindexJSAPI(mxHost,mxApiConnectionParams);
	
	MxApi.subscribeToCatalogsList(handleMxWsCatalogs);
	MxApi.subscribeToSelectedCatalog(handleMxWsSelectedCatalog);
	MxApi.subscribeToCreatedCatalog(handleMxWsCreatedCatalog);
	MxApi.subscribeToCreatedTerm(handleMxWsCreatedTerm);
	MxApi.subscribeToDeletedTerm(handleMxWsDeletedTerm);
	MxApi.subscribeToCatalogSelectedItem(handleMxWsSelectedItem);
	MxApi.subscribeToDeletedFilter(handleMxWsDeletedFilter);
	MxApi.subscribeToUpdatedFilter(handleMxWsUpdatedFilter);
	MxApi.subscribeToCsvUpload(handleMxWsCsvUpload);
	MxApi.subscribeToServerGuiMessages(handleMxWsServerGuiMessage);
	MxApi.subscribeToCatalogContentsChanged(handleMxWsCatalogContentsChanged);
	MxApi.subscribeToServerHeartBeatEvent(handleMxWsHeartbeatEvent);
	
	MxApi.connect(onConnectFunc);
}
	
var curMxHostUrl = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');
var mxHost=curMxHostUrl+"/metaindex"
var tokenName = "${_csrf.headerName}";
var tokenVal = "${_csrf.token}";
var mxApiConnectionParams={};
mxApiConnectionParams[tokenName]=tokenVal;
		
</script>
