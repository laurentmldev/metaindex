<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  


<script  src="${webAppBaseUrl}/public/commons/deps/sockjs-0.3.min.js"></script>
<script  src="${webAppBaseUrl}/public/commons/deps/stomp.min.js"></script>
<script src="${webAppBaseUrl}/public/commons/js/metaindex.js"></script>


<!-- Pako for GZip inflate/deflate for big communication with server  -->
<script src="${webAppBaseUrl}/public/commons/deps/pako.min.js"></script>

<script type="text/javascript">

// initialized when calling mx_ws_connect function
var MxApi=null;

// event : HEARTBEAT_TIMEOUT|FAILURE|MAINTENANCE|STOPPED|RUNNING
function handleMxWsHeartbeatEvent(event) {
	if (event=='HEARTBEAT_TIMEOUT') {
		footer_showAlert(ERROR, "<s:text name="metaindex.disconnected" />",null,999999999);
	}
	
	else if (event=='FAILURE') { 
		footer_showAlert(ERROR, "<s:text name="metaindex.failure" />",null,999999999);
	}
	else if (event=='RUNNING') { 
		// do nothing special
	}
} 
var upstreamLight=document.getElementById("upstream_light");
var upstreamTimer=null;
var downstreamLight=document.getElementById("downstream_light");
var downstreamTimer=null;
var BLINK_PERIOD_MS=100;
var BLACK_PERIOD_MS=10;
function _blink(light,timer) {
	if (timer!=null) { clearInterval(timer); }
	light.style.display='none';
	timer = setInterval(function() { 
		clearInterval(timer);
		light.style.display='block';
		timer= setInterval(function() { 
			clearInterval(timer);
			light.style.display='none';			 
		}, BLINK_PERIOD_MS);		 
	}, BLACK_PERIOD_MS);
}
function handleNetworEvent(direction) {
	if (downstreamLight==null) { downstreamLight=document.getElementById("downstream_light"); }
	if (upstreamLight==null) { upstreamLight=document.getElementById("upstream_light"); }
	
	if (direction==MX_DOWNSTREAM_MSG) {
		_blink(downstreamLight,downstreamTimer);
	} else {
		_blink(upstreamLight,upstreamTimer);
	} 
}
function handleMxSessionStatusEvent(sessionStatus) {
	if (sessionStatus=="EXPIRED") {
		footer_showAlert(WARNING, "<s:text name="session.expired" />",null,999999999);
		
		let redirectToLoginDelayMs=2000;
		let timer = setInterval(function() { 
			clearInterval(timer); 
			document.location.href="${webAppBaseUrl}/loginform?expired" 
		}, redirectToLoginDelayMs);
	}
}

function handleMxWsChatMessage(chatMxMsg) {
	for (var idx in chatMxMsg.chatMessages) {
		let curMsg=chatMxMsg.chatMessages[idx];
		MxChat.handleChatMessage(chatMxMsg.catalogId,curMsg);
	}	
}

function handleHisto(chatMxMsg) {
	for (var idx=0;idx<chatMxMsg.chatHistory.length;idx++) {
		let curMsg=chatMxMsg.chatHistory[idx];
		MxChat.handleChatMessage(chatMxMsg.catalogId,curMsg);
	}
}


function mx_ws_connect(mxHost,mxApiConnectionParams, onConnectFunc) {
	
	// from metaindex.js	
	MxApi = new MetaindexJSAPI(mxHost,mxApiConnectionParams);
	
	MxApi.subscribeToNetworkEvents(handleNetworEvent);
	MxApi.subscribeToSelectedCatalog(handleMxWsSelectedCatalog);
	MxApi.subscribeToCreatedCatalog(handleMxWsCreatedCatalog);
	MxApi.subscribeToDeletedTerm(handleMxWsDeletedTerm);
	MxApi.subscribeToCatalogSelectedItem(handleMxWsSelectedItem);
	MxApi.subscribeToDeletedFilter(handleMxWsDeletedFilter);
	MxApi.subscribeToUpdatedFilter(handleMxWsUpdatedFilter);
	MxApi.subscribeToCsvUpload(handleMxWsCsvUpload);
	MxApi.subscribeToServerGuiMessages(handleMxWsServerGuiMessage);
	MxApi.subscribeToChatMessages(handleMxWsChatMessage);
	MxApi.subscribeToCatalogContentsChanged(handleMxWsCatalogContentsChanged);
	MxApi.subscribeToServerHeartBeatEvent(handleMxWsHeartbeatEvent);
	MxApi.subscribeToServerSessionStatusEvent(handleMxSessionStatusEvent);
	MxApi.connect(onConnectFunc);
}
	
var mxHost="${webAppBaseUrl}";
var tokenName = "${_csrf.headerName}";
var tokenVal = "${_csrf.token}";
var mxApiConnectionParams={};
mxApiConnectionParams[tokenName]=tokenVal;



function ws_handlers_requestPlanUpdate(planId,onResponseCallback) {
	
	function errorCallback(msg) { 
		footer_showAlert(WARNING, "<s:text name="Profile.plans.unableToGetPlansDetails" />");
		console.log("Plans Update error : "+msg);
	}
	
	MxApi.requestPlanUpdate({	"userId":<s:property value="currentUserProfile.id"/>,
								"planId":planId,
								"successCallback":onResponseCallback,
								"errorCallback":errorCallback
									});
	
}


function ws_handlers_requestPlanUpdateConfirmPayment(planId,transactionId,totalCost,paymentMethod,paymentDetailsStr,onResponseCallback) {
	
	function errorCallback(msg) { 
		footer_showAlert(ERROR, "<s:text name="Profile.plans.unableToContactServerForPaymentConfirm" />");
		console.log("Plans Payment Confirmation error : "+msg);
	}
	
	MxApi.requestPlanUpdatePaymentConfirm({	"userId":<s:property value="currentUserProfile.id"/>,
								"planId":planId,
								"transactionId":transactionId,
								"totalCost":totalCost,
								"paymentMethod":paymentMethod,
								"paymentDetails":paymentDetailsStr,
								"successCallback":onResponseCallback,
								"errorCallback":errorCallback
									});
	
}

function ws_handlers_send_chatmsg(catalogId,text) {
	MxApi.requestPostChatMessage({"catalogId":catalogId,"text":text});	
}

function ws_handlers_get_chat_histo(catalogId) {
	function errorCallback(errorMsg) {
		footer_showAlert(ERROR, "<s:text name="Catalogs.chat.unableToGetChatHistory" />");
	}
	MxApi.requestCatalogChatHistory({	"catalogId":catalogId,
										"successCallback":handleHisto,
										"errorCallback":errorCallback});	
}

</script>
