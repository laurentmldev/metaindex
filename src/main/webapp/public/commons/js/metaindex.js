

//
// METATINDEX Javascript API 
// requires SockJS, stomp
//

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/


// Some helpers
function stripStr(str) { return str.replace(/^\s*/,"").replace(/\s*$/,""); }
// ----

// shouldn't be too big otherwise risk of WS deconnexion because max. size limit reached.
var MX_WS_FIELD_VALUE_MAX_CHARS = 5000;
var MX_WS_UPLOAD_FILE_MAX_LINES = 500;
var MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE = 50000;
var MX_WS_UPLOAD_FILE_SEND_PERIOD_MS = 30;

var MX_DOWNSTREAM_MSG="down";
var MX_UPSTREAM_MSG="up";

if (!window.WebSocket) { alert('ERROR: WebSockets are not supported in this browser. This feature is required to run Metaindex Javascript API'); }

if (window.Prototype) {
	console.log("[Metaindex] Warning: Current page seems to be using Prototype.js which defines a wrong redefinition of Array 'toJSON' function. Disabling it.");
	delete Array.prototype.toJSON;
}


// Object MetaindexJSAPI
function MetaindexJSAPI(url, connectionParamsHashTbl) 
{
	if (!pako) {
		console.log("[Metaindex] Error: pako.js required but not found. Please include this lib, ex: <script  src=\"mydeps/pako.min.js\"></script>");
		return null;
	}
	var myself=this;

	// properties
	myself._url=url;
	myself._connectionParamsHashTbl=connectionParamsHashTbl; 
	myself._socket=null;
	myself._stompClient=null;
	myself.MX_WS_ENDPOINT="/wsmx";
	myself.MX_WS_APP_PREFIX="/wsmxapp";	
	myself._callback_onConnect=null;
	
	// invoked each time a msg is sent or received
	// eventType: MX_DOWNSTREAM_MSG|MX_UPSTREAM_MSG
	myself._callback_NetworkEvent=function(eventType) {};
	
	// Set user preferences
	myself._callback_SetUserPreferences=null;	
	myself._callback_SetUserPreferences_debug=false;
		
	// User profiles
	myself._callback_GetUserProfiles=null;	
	myself._callback_GetUserProfiles_debug=false;
	
	// Server Messages
	myself._callback_ServerMessages=null;
	myself._callback_ServerMessages_debug=false;
	
	// Server heartbeat
	myself._callback_ServerHeartbeatEvent=null;
	myself._callback_ServerHeartbeatEvent_debug=false;
		
	// Server session status notif
	myself._callback_ServerSessionStatusEvent=null;
	myself._callback_ServerSessionStatusEvent_debug=false;
		

	// Create Catalog
	myself._callback_CreateCatalog=null;	
	myself._callback_CreateCatalog_debug=false;
	
	// Delete Catalog
	myself._callback_DeleteCatalog=null;
	myself._callback_DeleteCatalog_debug=false;
		
	// Get Catalogs
	myself._callback_Catalogs_debug=false;
	
	// Select Catalog
	myself._callback_SelectedCatalog=null;	
	myself._callback_SelectedCatalog_debug=false;
	
	// Perspective Update
	myself._callback_UpdatedPerspective=null;	
	myself._callback_UpdatedPerspective_debug=false;
	
	// Perspective Delete
	myself._callback_DeletedPerspective=null;	
	myself._callback_DeletedPerspective_debug=false;
	
	// Customize Catalog
	myself._callback_CustomizedCatalog=null;	
	myself._callback_CustomizedCatalog_debug=false;
		
	// Catalog Contents Changed
	myself._callback_CatalogContentsChanged=null;	
	myself._callback_CatalogContentsChanged_debug=false;
	
	// Retrieve catalog items
	myself._callback_CatalogItems=null;
	myself._callback_CatalogItems_debug=false;
	
	// Retrieve catalog selected item
	myself._callback_CatalogSelectedItem=null;
	myself._callback_CatalogSelectedItem_debug=false;
	
	// Progress info bar
	myself._callback_ProgressInfo=null;
	myself._callback_ProgressInfo_debug=false;
	
	// Create filter
	myself._callback_CreatedFilter=null;	
	myself._callback_CreatedFilter_debug=false;
	
	// Delete filter
	myself._callback_DeletedFilter=null;	
	myself._callback_DeletedFilter_debug=false;
	
	// Download CSV file
	myself._callback_DownloadCsv=null;
	myself._callback_DownloadCsv_debug=false;
	
	// Download Graph file
	myself._callback_DownloadGraph=null;
	myself._callback_DownloadGraph_debug=false;
	
	// Upload CSV file
	myself._callback_UploadCsv=null;
	myself._callback_UploadCsv_debug=false;
	
	// Create item
	myself._callback_CreatedItem=null;	
	myself._callback_CreatedItem_debug=false;
		
	// Delete items
	myself._callback_DeleteItems=null;
	myself._callback_DeleteItems_debug=false;
	
	// Delete term
	myself._callback_DeleteTerm=null;
	myself._callback_DeleteTerm_debug=false;
	
	// Create term
	myself._callback_CreateTerm_debug=false;

	// Get Catalog Users
	myself._callback_GetCatalogUsers=null;
	myself._callback_GetCatalogUsers_debug=null;
	
	// Update Catalog Lexic Entry
	myself._callback_SetCatalogLexicEntry=null;
	myself._callback_SetCatalogLexicEntry_debug=null;
		
	// Update Term Lexic Entry
	myself._callback_SetTermLexicEntry=null;
	myself._callback_SetTermLexicEntry_debug=null;
	
	// Set Catalog User Custo
	myself._callback_CustomizeCatalog=null;	
	myself._callback_CustomizeCatalog_debug=false;
	
	// Upload Files
	myself._callback_UploadFiles=null;	
	myself._callback_UploadFiles_debug=false;

	// Request Plan Update, answer is details for payment 
	myself._callback_PlanRequest=null;	
	myself._callback_PlanRequest_debug=false;
	
	// Plan Payment confirmation Request 
	myself._callback_PlanConfirmPayment=null;	
	myself._callback_PlanConfirmPayment_debug=false;
	
	
	// uncompress GZIP and Base64 encoded data
	function uncompressBase64(base64data) {

		let bytesGzip = window.atob(base64data);
		let deflatedData = pako.ungzip(bytesGzip,{ to: 'string' }); 			
		return deflatedData;
	}
	// compress GZIP and Base64 encode data
	function compressBase64(rawdata) {		
		let bytesGzip = pako.gzip(rawdata,{ to: 'string' }); 			
		let base64data = window.atob(bytesGzip);
		return base64data;
	}
	
	// public method connect
	this.connect = function(userConnectCallback, debug) {	
		myself._socket = new SockJS(myself._url+myself.MX_WS_ENDPOINT);
		myself._stompClient = Stomp.over(myself._socket);
		myself._stompClient.debug = debug;
		myself._callback_onConnect = userConnectCallback;

		var header={};
		
		var userKeys=Object.keys(myself._connectionParamsHashTbl);
		for (var i=0; i<userKeys.length;i++) {
			var curKey=userKeys[i];
			var curVal=myself._connectionParamsHashTbl[curKey];
			header[curKey]=curVal;
		}
		
		var connectionCallback = function(frame) {		
						 	
		 	// subscribe to Metaindex user messages streams	
			myself._stompClient.subscribe('/user/queue/register_ack',myself._handleRegisterAckMsg);
			
			// user
			myself._stompClient.subscribe('/user/queue/user_preferences_set',myself._handleSetUserPreferencesMsg);
			myself._stompClient.subscribe('/user/queue/user_catalogcusto_set',myself._handleSetUserCatalogCustoMsg);
			myself._stompClient.subscribe('/user/queue/users_profiles',myself._handleGetUsersProfilesMsg);
			myself._stompClient.subscribe('/user/queue/update_plan_answer',myself._handlePlanUpdateMsg);
			myself._stompClient.subscribe('/user/queue/update_plan_confirm_payment_answer',myself._handlePlanUpdatePaymentConfirmMsg);
			
			// catalog
			myself._stompClient.subscribe('/user/queue/catalogs',myself._handleCatalogsMsg);
			myself._stompClient.subscribe('/user/queue/catalog_selected',myself._handleSelectedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/created_catalog',myself._handleCreatedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/deleted_catalog',myself._handleDeletedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/join_catalog',myself._handleJoinCatalogMsg);
			myself._stompClient.subscribe('/user/queue/catalog_customized',myself._handleCustomizedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/catalog_lexic_updated',myself._handleSetCatalogLexicResponseMsg);
			myself._stompClient.subscribe('/user/queue/catalog_users',myself._handleGetCatalogUsersMsg);
			myself._stompClient.subscribe('/user/queue/catalog_user_access',myself._handleSetCatalogUserAccessMsg);
			myself._stompClient.subscribe('/user/queue/catalog_chat_history',myself._handleCatalogChatHistoryResponseMsg);
			
			// perspectives
			myself._stompClient.subscribe('/user/queue/perspective_updated',myself._handleUpdatedPerspectiveMsg);
			myself._stompClient.subscribe('/user/queue/perspective_deleted',myself._handleDeletedPerspectiveMsg);			
			
			// terms
			myself._stompClient.subscribe('/user/queue/updated_term',myself._handleUpdateTermResponseMsg);
			myself._stompClient.subscribe('/user/queue/deleted_term',myself._handleDeleteTermResponseMsg);
			myself._stompClient.subscribe('/user/queue/created_term',myself._handleCreateTermResponseMsg);
			myself._stompClient.subscribe('/user/queue/term_lexic_updated',myself._handleSetTermLexicResponseMsg);

			// filters
			myself._stompClient.subscribe('/user/queue/created_filter',myself._handleCreatedFilterMsg);
			myself._stompClient.subscribe('/user/queue/deleted_filter',myself._handleDeletedFilterMsg);
			myself._stompClient.subscribe('/user/queue/updated_filter',myself._handleUpdatedFilterMsg);
			
			// items
			myself._stompClient.subscribe('/user/queue/items',myself._handleCatalogItemsMsg);
			myself._stompClient.subscribe('/user/queue/get_catalog_items_allids_response',myself._handleCatalogItemsIdsMsg);
			myself._stompClient.subscribe('/user/queue/selected_item',myself._handleCatalogSelectedItemMsg);
			myself._stompClient.subscribe('/user/queue/upload_items_csv_response',myself._handleUploadItemsFromCsvAnswer);
			myself._stompClient.subscribe('/user/queue/download_items_csv_response',myself._handleDownloadItemsCsvAnswer);
			myself._stompClient.subscribe('/user/queue/download_items_graph_response',myself._handleDownloadItemsGraphAnswer);
			myself._stompClient.subscribe('/user/queue/download_items_graphgroupby_response',myself._handleDownloadItemsGraphGroupByAnswer);
			myself._stompClient.subscribe('/user/queue/created_item',myself._handleCreatedItemResponseMsg);
			myself._stompClient.subscribe('/user/queue/upload_userdata_files_response',myself._handleUploadFilesAnswer);
			myself._stompClient.subscribe('/user/queue/upload_userdata_file_contents_progress',myself._handleUploadFilesContentsAnswer);			
			
			// fields
			myself._stompClient.subscribe('/user/queue/field_value',myself._handleUpdateFieldResponseMsg);
			
			// misc
			myself._stompClient.subscribe('/user/queue/gui_messaging',myself._handleGuiMsgFromServer);
			myself._stompClient.subscribe('/user/queue/gui_messaging_progress',myself._handleGuiMsgFromServer);
			myself._stompClient.subscribe('/user/queue/gui_messaging_chat',myself._handleGuiChatMsg);
			
			// contents change notif (broadcast) 
			myself._stompClient.subscribe('/queue/catalog_contents_changed',myself._handleCatalogContentsChangedMsg);
			// server heartbeat (broadcast)
			myself._stompClient.subscribe('/queue/heartbeat',myself._handleServerHeartbeat);
			// server session status
			myself._stompClient.subscribe('/user/queue/session_status',myself._handleServerSessionStatus);
			
			
			// register user
			myself._sendRegisterRequest();
			
		}
		myself._stompClient.connect(header, connectionCallback);
	}

	//------ network msg callaback
	// function takes one argument : MX_DOWNSTREAM_MSG|MX_UPSTREAM_MSG
	this.subscribeToNetworkEvents=function(callback_func) {
		myself._callback_NetworkEvent=callback_func;
	}
//------- Messages from Server --------	
	this.subscribeToServerGuiMessages=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_ServerMessages_debug=debug;
		myself._callback_ServerMessages=callback_func;
	}
	
	this._handleGuiMsgFromServer= function (mxServerMsg) {
		
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(mxServerMsg.body)
		
		if (myself._callback_ServerMessages_debug==true) {
			console.log("MxAPI Received [ServerMessage]\n"+parsedMsg);
		}
		if (myself._callback_ServerMessages!=null) {			
			myself._callback_ServerMessages(parsedMsg); 
		}		
	}
//------- Receive Chat Messages from Server --------	
	this.subscribeToChatMessages=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_ChatMessages_debug=debug;
		myself._callback_ChatMessages=callback_func;
	}
	
	this._handleGuiChatMsg= function (mxChatMsg) {
		
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(mxChatMsg.body)
		
		if (myself._callback_ChatMessages_debug==true) {
			console.log("MxAPI Received [ChatMessage]\n"+parsedMsg);
		}
		if (myself._callback_ChatMessages!=null) {			
			myself._callback_ChatMessages(parsedMsg); 
		}		
	}
// -------- Post Chat Message				
	
	// dataObj { 
	//  catalogId:xxx,
	//  text:xxx,
	// }
	this.requestPostChatMessage = function(dataObj) {
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/post_chat_message", {}, 
								 JSON.stringify({"catalogId":dataObj.catalogId,
									 			 "chatMessage":dataObj.text
									 			}));			
	}

// -------- Get catalog chat history

	this.requestCatalogChatHistoCallbacks=[];
	
	// dataObj {
	// 	catalogId
	//  successCallback
	//  errorCallback
	// }
	this.requestCatalogChatHistory = function(dataObj) {
		
		var curRequestId=myself.requestCatalogChatHistoCallbacks.length;
		myself.requestCatalogChatHistoCallbacks.push(dataObj);
		if (myself._callback_CatalogChatHisto_debug==true) {
			console.log("MxAPI Requesting Catalog chat history");
		}
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalog_chat_history", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "catalogId":dataObj.catalogId,									 			 
									 			}));			
	}
	
	this._handleCatalogChatHistoryResponseMsg= function (responseMsg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=responseMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_SetUserPreferences_debug==true) {
			console.log("MxAPI Received Catalog chat history\n"+decodedData);
		}
		
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestCatalogChatHistoCallbacks[requestId];
		//console.log("received customization requestId="+requestId+" -> "+requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="get catalog chat history refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}
	
	
	
//------- Server HeartBeat--------	
	// server heartbeat occuring every 3 seconds
	// consider connection lost if no heartbeat after x secs
	var HEARTBEAT_DELAY_TRESHOLD_SEC=30;
	var lastHeartbeatDate = new Date();
	var heartbeatTimerCheck = null;
	var curApplicationStatus = 'STOPPED';
		
	this.subscribeToServerHeartBeatEvent=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_ServerHeartbeatEvent_debug=debug;
		myself._callback_ServerHeartbeatEvent=callback_func;
	}
	
	
	this._handleServerHeartbeat= function (mxServerHeartbeatMsg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		lastHeartbeatDate=new Date();		
		var parsedMsg = JSON.parse(mxServerHeartbeatMsg.body);
		if (parsedMsg.applicationStatus!=curApplicationStatus) {
			curApplicationStatus=parsedMsg.applicationStatus;
			// FAILURE|STOPPED|RUNNING			
			myself._callback_ServerHeartbeatEvent(parsedMsg.applicationStatus);			
		}
		if (myself._callback_ServerHeartbeatEvent_debug==true) {
			console.log("MxAPI Received [Server Heartbeat] : '"+curApplicationStatus+"'\n"+parsedMsg.count);
		}
		
		// check periodicaly that heartbeat arrive often enough
		// wait for 2 consecutive holes, because sometimes the javascript is blocked
		// for example when a "save as" window is open
		if (heartbeatTimerCheck==null) {
			heartbeatTimerCheck=setInterval(function() { 
				var curDate = new Date(); 
				var delay_ms=curDate-lastHeartbeatDate;
				// notify application if heartbeat lost
				if (delay_ms>HEARTBEAT_DELAY_TRESHOLD_SEC*1000) {
					// notify user only once
					if (curApplicationStatus!='HEARTBEAT_TIMEOUT') {
						curApplicationStatus='HEARTBEAT_TIMEOUT';
						if (myself._callback_ServerHeartbeatEvent!=null) {	
							myself._callback_ServerHeartbeatEvent(curApplicationStatus);
						}					
					}
				}
			}, HEARTBEAT_DELAY_TRESHOLD_SEC*1000);
		}
				
	}

//------- Server Session Status notif --------	
	this.subscribeToServerSessionStatusEvent=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_ServerSessionStatusEvent_debug=debug;
		myself._callback_ServerSessionStatusEvent=callback_func;
	}
	
	
	this._handleServerSessionStatus= function (mxServerSessionStatusMsg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(mxServerSessionStatusMsg.body);
			
		if (myself._callback_ServerHeartbeatEvent_debug==true) {
			console.log("MxAPI Received [Server Session Status] : '"+parsedMsg.sessionStatus+"'");
		}
		if (myself._callback_ServerSessionStatusEvent!=null) {
			myself._callback_ServerSessionStatusEvent(parsedMsg.sessionStatus);
		}
		
		
				
	}
//------- Register User --------	
	this._sendRegisterRequest = function() {
		
		console.log("Sending Metaindex user registration request");
		
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/register_request", {}, JSON.stringify({}));
	}
	
	this._handleRegisterAckMsg= function (mxRegisterAckMsg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(mxRegisterAckMsg.body)
		if (parsedMsg.registrationStatus==true) {
			console.log("Metaindex API opened!");
			// call user callback if any defined
			if (myself._callback_onConnect !=null) { myself._callback_onConnect(true); }
		} else {
			console.log("Metaindex API open failed : user registration refused.");
			if (myself._callback_onConnect !=null) { myself._callback_onConnect(false); }
		}
	}
	


//------- Set User Preferences --------	
	
	this.requestUserPreferencesCallbacks=[];
	
	// dataObj {
	// 	userId
	// 	nickName
	// 	languageId
	//	themeId
	//  successCallback
	//  errorCallback
	// }
	this.requestSetUserPreferences = function(dataObj) {
		
		var curRequestId=myself.requestUserPreferencesCallbacks.length;
		myself.requestUserPreferencesCallbacks.push(dataObj);
		if (myself._callback_SetUserPreferences_debug==true) {
			console.log("MxAPI Requesting Set User Preferences");
		}
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/set_user_preferences", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "userId":dataObj.userId,
									 			 "nickName":dataObj.nickName,
									 			 "languageId":dataObj.languageId,
									 			 "themeId":dataObj.themeId
									 			}));			
	}
	
	this._handleSetUserPreferencesMsg= function (responseMsg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=responseMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_SetUserPreferences_debug==true) {
			console.log("MxAPI Received Set User Preferences\n"+decodedData);
		}
		
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestUserPreferencesCallbacks[requestId];
		//console.log("received customization requestId="+requestId+" -> "+requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="set user preferences refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}
	
	
	

//------- Set User Catalog Customizations --------	
	
	this.requestUserCatalogCustoCallbacks=[];
	
	// dataObj {
	// 	userId
	// 	catalogId
	// 	kibanaIFrame
	//  successCallback
	//  errorCallback
	// }
	this.requestSetUserCatalogCustomization = function(dataObj) {
		
		var curRequestId=myself.requestUserCatalogCustoCallbacks.length;
		myself.requestUserCatalogCustoCallbacks.push(dataObj);
		if (myself._callback_CustomizeCatalog_debug==true) {
			console.log("MxAPI Requesting Set User Catalog Customization");
		}
		
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/set_user_catalogcusto", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "userId":dataObj.userId,
									 			 "catalogId":dataObj.catalogId,
									 			 "kibanaIFrame":dataObj.kibanaIFrame
									 			}));			
	}
	
	this._handleSetUserCatalogCustoMsg = function (responseMsg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=responseMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_SetUserPreferences_debug==true) {
			console.log("MxAPI Received Set User Catalog Customisation\n"+decodedData);
		}
		
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestUserCatalogCustoCallbacks[requestId];
		//console.log("received customization requestId="+requestId+" -> "+requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="set user catalog customisation refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}


//------- Get Users Profiles --------	
		
		this.requestUsersProfilesCallbacks=[];
		
		// dataObj {
		// 	usersIds = []
		//  successCallback
		//  errorCallback
		// }
		this.requestGetUsersProfiles = function(dataObj) {
			
			var curRequestId=myself.requestUsersProfilesCallbacks.length;
			myself.requestUsersProfilesCallbacks.push(dataObj);
			if (myself._callback_GetUserProfiles_debug==true) {
				console.log("MxAPI Requesting Get User Profiles ");
			}
			
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/users_profiles", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "usersIds":dataObj.usersIds,
										 			}));			
		}
		
		this._handleGetUsersProfilesMsg = function (responseMsg) {
			myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var decodedData=responseMsg.body;
			var parsedMsg = JSON.parse(decodedData);
			
			if (myself._callback_GetUserProfiles_debug==true) {
				console.log("MxAPI Received Get Users Profiles\n"+decodedData);
			}
			
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestUsersProfilesCallbacks[requestId];
			//console.log("received users profiles requestId="+requestId+" -> "+requestObj);
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="set user catalog customisation refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}			
		}
		
		
//------- Catalog Contents Changed notification --------
			// coherent with Metadindex WsControllerUser.CATALOG_MODIF_TYPE enumeration
			this.CATALOG_MODIF_TYPE={ 'CATALOGS_LIST':'CATALOGS_LIST', 
										'CATALOG_DEFINITION':'CATALOG_DEFINITION',
										'FIELD_VALUE':'FIELD_VALUE', 
										'FIELDS_LIST':'FIELDS_LIST', 
										'FIELD_DEFINITION':'FIELD_DEFINITION',
										'DOCS_LIST':'DOCS_LIST' };
			
			this.subscribeToCatalogContentsChanged=function(callback_func,debug) {		
				debug=debug||false;
				myself._callback_CatalogContentsChanged_debug=debug;
				myself._callback_CatalogContentsChanged=callback_func;
			}	
			
			this._handleCatalogContentsChangedMsg= function (mxCatalogContentsChangedMsg) {
				myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
				var parsedMsg = JSON.parse(mxCatalogContentsChangedMsg.body);
				if (myself._callback_CatalogContentsChanged_debug==true) {
					console.log("MxAPI Received [CatalogContentsChanged]\n"+mxCatalogContentsChangedMsg.body);
				}
				if (myself._callback_CatalogContentsChanged!=null) {
					myself._callback_CatalogContentsChanged(parsedMsg); 
				}
			}

//------- Download CSV file --------
			
	this.requestCsvDownloadsCallbacks=[];
	
	// dataObj {
	//   termNamesList = []
	//   fromIdx=0
	//   size=-1
	//   filtersNames=[]
	//	 query=""
	//   sortByFieldName=""
	//   reverseSortOrder=false
	//   successCallback (func)({items:[],totalHits:<int>,totalItems:<int>})
	//   errorCallback (func)(msg)
	// }
	this.requestDownloadItemsCsv = function(dataObj) {
		if (dataObj.fromIdx==null) { dataObj.fromIdx=0; }
		if (dataObj.size==null) { dataObj.size=-1; }
		if (dataObj.filtersNames==null) { dataObj.filtersNames=[]; }
		if (dataObj.query==null) { dataObj.query=""; }
		if (dataObj.sortByFieldName==null) { dataObj.sortByFieldName=""; }
		if (dataObj.reverseSortOrder==null) { dataObj.reverseSortOrder=false; }
		if (myself._callback_CatalogItems_debug==true) {
			console.log("MxAPI Requesting [Items CSV]");
		}

		var curRequestId=myself.requestCsvDownloadsCallbacks.length;
		myself.requestCsvDownloadsCallbacks.push(dataObj);
		dataObj.requestId=curRequestId;
		
		
    	//console.log('### Sending download request : '+JSON.stringify(jsonData));
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/download_items_csv_request", {},JSON.stringify(dataObj));		

	}
	
	this.subscribeToCsvDownload=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_DownloadCsv_debug=debug;
		myself._callback_DownloadCsv=callback_func;
	}
	
	this._handleDownloadItemsCsvAnswer=function(msg) {
		myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(msg.body);
		
		if (myself._callback_DownloadCsv_debug==true) {
			console.log("[MxApi] Received answer to request for 'Download Items from CSV' accepted : "+parsedMsg.isSuccess);
			if (parsedMsg.isSuccess==false) {
				console.log("Reject msg = "+parsedMsg.rejectMessage);
			}
		}

		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestCsvDownloadsCallbacks[requestId];
		//console.log("received customization requestId="+requestId+" -> "+requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="perspective delete refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}	
	    
	}
	
	
	//------- Upload Items from CSV file --------	
	
	// store handles to files being uploaded
	// pb: not robust of performing several CSV upload at a time ...
	var mx_csv_files_to_be_uploaded=[];
	var mx_csv_files_upload_finish_callback=[];

	
	this.subscribeToCsvUpload=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_UploadCsv_debug=debug;
		myself._callback_UploadCsv=callback_func;
	}
		
	this.requestUploadItemsFromCsv = function(csvRows,chosenFieldsMapping,finishCallback) {
	 	
		let jsonData = {};
		jsonData.clientFileId=mx_csv_files_to_be_uploaded.length;		
		let nbItemsToBeCreated=0;
		
		// instantiate a new FileReader object to count total amount of items 
		// t be created
   
    	let curLineNb=0;
    	
    	// count total nb entries
    	while (curLineNb<csvRows.length) {
    		if (	   csvRows[curLineNb].length>0 
    				&& csvRows[curLineNb][0]!='#' 
    				&& !csvRows[curLineNb].match(/^\s*$/)
    				&& !csvRows[curLineNb].match(/^\s*#/)
    				&& curLineNb!=0 // ignore first line (header)
    				) {
    			nbItemsToBeCreated++;
    		}
    			
    		curLineNb++;	    		
    	}
    	jsonData.totalNbEntries=nbItemsToBeCreated;
    	jsonData.chosenFieldsMapping=chosenFieldsMapping;	    	
    	
    	// parse fields names and types
    	let fieldsDefStr=csvRows[0];
    	fieldsDefStr=stripStr(fieldsDefStr.replace("#",""));	    	

    	let separator=";"
    	let fieldsDefsArray=fieldsDefStr.split(separator);
    	if (fieldsDefsArray.length==1) {
    		separator=",";
    		fieldsDefsArray=fieldsDefStr.split(separator);	    		
    	}
    	if (fieldsDefsArray.length==1) {
    		separator="\t";
    		fieldsDefsArray=fieldsDefStr.split(separator);	    		
    	}

    	jsonData.separator=separator;
    	
    	jsonData.csvColsList=[];	    	
    	for (curFieldIdx in fieldsDefsArray) {
    		let curField=fieldsDefsArray[curFieldIdx];	  
    		jsonData.csvColsList.push(stripStr(curField));
    	}
    	//console.log('### Sending file upload request : '+JSON.stringify(jsonData));
    	myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_items_csv_request", {},JSON.stringify(jsonData));		
 		
    	jsonData.csvRows=csvRows;
    	mx_csv_files_to_be_uploaded[jsonData.clientFileId]=jsonData;
    	mx_csv_files_upload_finish_callback[jsonData.clientFileId]=finishCallback;	    		
	}
	
	this._handleUploadItemsFromCsvAnswer=function(msg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(msg.body);
		
		if (myself._callback_UploadCsv_debug==true) {
			console.log("[MxApi] Received answer to request for 'Upload Items from CSV' accepted : "+parsedMsg.isSuccess);
			if (parsedMsg.isSuccess==false) {
				console.log("Reject msg = "+parsedMsg.rejectMessage);
			}
		}

		let fileIndex=parsedMsg.clientFileId;
	    //console.log("file index = "+fileIndex);
	    let csvRows=mx_csv_files_to_be_uploaded[fileIndex].csvRows;
	    let finishCallback=mx_csv_files_upload_finish_callback[fileIndex];
	    //console.log("fileHandle="+fileHandle);
	    //dumpStructure(mx_csv_files_to_be_uploaded);
	    let serverProcessingTaskId=parsedMsg.processingTaskId;
	    
	    if (csvRows==null) { return; }
	    //console.log("received file upload answer : file = "+fileHandle.value);
	    
    
    	let curLineNb=0;
    	let curLinesWsBuffer=[];
    	
    	if (myself._callback_UploadCsv!=null) {
			myself._callback_UploadCsv(parsedMsg); 
		}
    	
    	while (curLineNb<csvRows.length) {
    		// ignore first line (header)
    		if (curLineNb==0) { curLineNb++; continue; }
    		
    		curLinesWsBuffer.push(csvRows[curLineNb]);
    		
    		if (curLineNb % MX_WS_UPLOAD_FILE_MAX_LINES==0 || curLineNb==csvRows.length-1) {
    			//console.log("sending "+curLinesWsBuffer.length+" lines : "+curLinesWsBuffer);
    			//console.log(curLinesWsBuffer);
    			
    			let jsonData = { 
    				 "csvLines" : curLinesWsBuffer,
					 "processingTaskId" : serverProcessingTaskId,
					 "totalNbLines" : csvRows.length,
    				};
    			
    			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
    			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_filter_file_contents", {},JSON.stringify(jsonData));
    			curLinesWsBuffer=[];
    		}
    		curLineNb++;	    		
    	}
    	
    	if (finishCallback!=null) { finishCallback(); }
    		    
	}
	

//------- Download Graph (GEXF) file --------
				
		this.requestGraghDownloadsCallbacks=[];
		
		// dataObj {
		//   nodesDataTermIdsList = []
		//   edgesTermsIdList = []
		//   fromIdx=0
		//   size=-1
		//   filtersNames=[]
		//	 query=""
		//   sortByFieldName=""
		//   reverseSortOrder=false
		//   successCallback (func)({items:[],totalHits:<int>,totalItems:<int>})
		//   errorCallback (func)(msg)
		// }
		this.requestDownloadItemsGraph = function(dataObj) {
			if (dataObj.fromIdx==null) { dataObj.fromIdx=0; }
			if (dataObj.size==null) { dataObj.size=-1; }
			if (dataObj.filtersNames==null) { dataObj.filtersNames=[]; }
			if (dataObj.query==null) { dataObj.query=""; }
			if (dataObj.sortByFieldName==null) { dataObj.sortByFieldName=""; }
			if (dataObj.reverseSortOrder==null) { dataObj.reverseSortOrder=false; }

			var curRequestId=myself.requestGraghDownloadsCallbacks.length;
			myself.requestGraghDownloadsCallbacks.push(dataObj);
			dataObj.requestId=curRequestId;
			
	    	//console.log('### Sending download request : '+JSON.stringify(jsonData));
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
	    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/download_items_graph_request", {},JSON.stringify(dataObj));		

		}
		
		this._handleDownloadItemsGraphAnswer=function(msg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var parsedMsg = JSON.parse(msg.body);
			
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestGraghDownloadsCallbacks[requestId];
			//console.log("received customization requestId="+requestId+" -> "+requestObj);
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="generation of GEXF file refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}	
		    
		}
		

//------- Download Graph (GEXF) GroupBy file --------
				
		this.requestGraghGroupByDownloadsCallbacks=[];
		
		// dataObj {
		//   groupingTermId = int
		//   edgeTermId = int
		//   fromIdx=0
		//   size=-1
		//   filtersNames=[]
		//	 query=""
		//   sortByFieldName=""
		//   reverseSortOrder=false
		//   successCallback (func)({items:[],totalHits:<int>,totalItems:<int>})
		//   errorCallback (func)(msg)
		// }
		this.requestDownloadItemsGraphGroupBy = function(dataObj) {
			if (dataObj.groupingTermId==null) { dataObj.errorCallback("groupingTermId parameter requested, provided value is empty"); return;}
			if (dataObj.edgeTermId==null) { dataObj.errorCallback("edgeTermId parameter requested, provided value is empty"); return; }
			if (dataObj.fromIdx==null) { dataObj.fromIdx=0; }
			if (dataObj.size==null) { dataObj.size=-1; }
			if (dataObj.filtersNames==null) { dataObj.filtersNames=[]; }
			if (dataObj.query==null) { dataObj.query=""; }
			if (dataObj.sortByFieldName==null) { dataObj.sortByFieldName=""; }
			if (dataObj.reverseSortOrder==null) { dataObj.reverseSortOrder=false; }
			
			var curRequestId=myself.requestGraghGroupByDownloadsCallbacks.length;
			myself.requestGraghGroupByDownloadsCallbacks.push(dataObj);
			dataObj.requestId=curRequestId;
			
	    	//console.log('### Sending download request : '+JSON.stringify(jsonData));
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
	    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/download_items_graphgroupby_request", {},JSON.stringify(dataObj));		

		}		
		
		this._handleDownloadItemsGraphGroupByAnswer=function(msg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var parsedMsg = JSON.parse(msg.body);
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestGraghGroupByDownloadsCallbacks[requestId];
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { 
					errorMsg="generation of GEXF GroupBy file refused by server, sorry." 
				}
				requestObj.errorCallback(errorMsg); 
			}	
		    
		}
		
				
		
//------- Catalogs List --------
		this.requestGetCatalogsCallbacks=[];
		
		// catalogId==0 -> retrieve all catalogs at least accessible 'read-only' for current user  
		// catalogId==-1 -> retrieve all catalogs not accessible for current user
		// catalogId>0 -> retrieve given catalog if accessible at least read-only for current user
		
		// dataObj { 
		//	catalogId:xxx,
		//	successCallback:func(msg[]),
		// }
		this.requestGetCatalogs = function(dataObj) {
			
			var curRequestId=myself.requestGetCatalogsCallbacks.length;
			myself.requestGetCatalogsCallbacks.push(dataObj);
			
			if (myself._callback_Catalogs_debug==true) {
				console.log("MxAPI Requesting Catalogs "+dataObj.catalogId);
			}
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalogs", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "catalogId":dataObj.catalogId								 
										 			}));			
		}

		this._handleCatalogsMsg= function (mxCatalogsMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var decodedData=uncompressBase64(mxCatalogsMsg.body);
			var parsedMsg = JSON.parse(decodedData);
			
			
			if (myself._callback_Catalogs_debug==true) {
				console.log("MxAPI Received Catalogs response\n"+decodedData);
			}
			
			if (parsedMsg.length==0) { return; }
			let requestId=parsedMsg[0].requestId;
			let requestObj=myself.requestGetCatalogsCallbacks[requestId];
			
			if (requestObj==null) { return; }
			requestObj.successCallback(parsedMsg); 
						
		}
	
//------- Select Catalog --------	
	this.subscribeToSelectedCatalog=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_SelectedCatalog_debug=debug;
		myself._callback_SelectedCatalog=callback_func;
	}
	this.requestSelectCatalog = function(catalogId) {
		if (myself._callback_SelectedCatalog_debug==true) {
			console.log("MxAPI Requesting Select Catalog "+catalogId+" [Catalogs]");
		}
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/select_catalog", {}, 
								 JSON.stringify({"catalogId":catalogId }));
	}
	
	this._handleSelectedCatalogMsg= function (mxSelectedCatalogMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=mxSelectedCatalogMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_SelectedCatalog_debug==true) {
			console.log("MxAPI Received Selected Catalog [Catalogs]\n"+decodedData);
		}
		if (myself._callback_SelectedCatalog!=null) {
			myself._callback_SelectedCatalog(parsedMsg); 
		}
	}


//------- Get Catalog Users--------
			
			this.requestGetCatalogUsersCallbacks=[];
			
			// dataObj { 
			//	catalogId:xxx,
			//	successCallback:xxx,
			//	errorCallback:xxx
			// }
			this.requestGetCatalogUsers = function(dataObj) {
				
				var curRequestId=myself.requestGetCatalogUsersCallbacks.length;
				myself.requestGetCatalogUsersCallbacks.push(dataObj);
				
				if (myself._callback_GetCatalogUsers_debug==true) {
					console.log("MxAPI Requesting Catalog Users "+dataObj.catalogId);
				}
				myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
				myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalog_users", {}, 
										 JSON.stringify({"requestId" : curRequestId,
											 			 "catalogId":dataObj.catalogId								 
											 			}));			
			}
			
			this._handleGetCatalogUsersMsg= function (mxCatalogUsersMsg) {
				
		    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
				var decodedData=mxCatalogUsersMsg.body;
				var parsedMsg = JSON.parse(decodedData);
				
				if (myself._callback_GetCatalogUsers_debug==true) {
					console.log("MxAPI Received Catalog Users response\n"+decodedData);
				}
				
				let requestId=parsedMsg.requestId;
				let requestObj=myself.requestGetCatalogUsersCallbacks[requestId];
				if (requestObj==null) { return; }
				if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
				else {
					let errorMsg=parsedMsg.rejectMessage;
					// ensure error message is not empty
					// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
					if (errorMsg==undefined) { errorMsg="get_catalog_users request refused by server, sorry." }
					requestObj.errorCallback(errorMsg); 
				}			
			}


//------- Set Catalog User Access-Rights--------
			
			this.requestSetCatalogUserAccessCallbacks=[];
			
			// dataObj { 
			//  catalogId:xxx,
			//  userId:xxx,
			//  accessRights:xxx CATALOG_ADMIN|CATALOG_EDIT|CATALOG_READ|NONE,
			//	successCallback:xxx,
			//	errorCallback:xxx
			// }
			this.requestSetCatalogUserAccess = function(dataObj) {
				
				var curRequestId=myself.requestSetCatalogUserAccessCallbacks.length;
				myself.requestSetCatalogUserAccessCallbacks.push(dataObj);
				
				if (myself._callback_SetCatalogUserAccess_debug==true) {
					console.log("MxAPI Requesting Set Catalog User Access "+dataObj.catalogId);
				}
				
				myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
				myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/set_catalog_user_access", {}, 
										 JSON.stringify({"requestId" : curRequestId,
											 			 "catalogId":dataObj.catalogId,
											 			 "userId":dataObj.userId,
											 			 "accessRights":dataObj.accessRights
											 			}));			
			}
			
			this._handleSetCatalogUserAccessMsg= function (mxCatalogUsersMsg) {
				
		    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
				var decodedData=mxCatalogUsersMsg.body;
				var parsedMsg = JSON.parse(decodedData);
				
				if (myself._callback_GetCatalogUsers_debug==true) {
					console.log("MxAPI Received Set Catalog User Access response\n"+decodedData);
				}
				
				let requestId=parsedMsg.requestId;
				let requestObj=myself.requestSetCatalogUserAccessCallbacks[requestId];
				if (requestObj==null) { return; }
				if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
				else {
					let errorMsg=parsedMsg.rejectMessage;
					// ensure error message is not empty
					// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
					if (errorMsg==undefined) { errorMsg="set_catalog_user_access request refused by server, sorry." }
					requestObj.errorCallback(errorMsg); 
				}			
			}
			
			
		
//------- Delete Perspective Definition --------
		
		this.requestCatalogPerspectiveDeleteCallbacks=[];
		
		// dataObj { 
		//	catalogId:xxx,
		//	perspectiveId:xxx,
		//	successCallback:xxx,
		//	errorCallback:xxx
		// }
		this.requestPerspectiveDelete = function(dataObj) {
			
			var curRequestId=myself.requestCatalogPerspectiveDeleteCallbacks.length;
			myself.requestCatalogPerspectiveDeleteCallbacks.push(dataObj);
			if (myself._callback_DeletedPerspective_debug==true) {
				console.log("MxAPI Requesting Delete of Perspective "+dataObj.perspectiveId+" [Perspectives]");
			}
			
			//console.log("sending customization requestId="+curRequestId);
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_perspective", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "perspectiveId":dataObj.perspectiveId,
										 			 "catalogId":dataObj.catalogId								 
										 			}));			
		}
		
		this._handleDeletedPerspectiveMsg= function (mxDeletedPerspectiveMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var decodedData=mxDeletedPerspectiveMsg.body;
			var parsedMsg = JSON.parse(decodedData);
			
			if (myself._callback_DeletedPerspective_debug==true) {
				console.log("MxAPI Received Deleted Perspective Response [Perspectives]\n"+decodedData);
			}
			
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestCatalogPerspectiveDeleteCallbacks[requestId];
			//console.log("received customization requestId="+requestId+" -> "+requestObj);
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="perspective delete refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}			
		}
		
		
//------- Update Perspective Definition --------
	
	// try to clean 'null' strings in generated JSON string
	// don't how to remove json contents without having those
	// ghost 'null' strings appearing ...
	function stringifyWithoutNull(jsonObj) {
	
		let strJson=JSON.stringify(jsonObj);
		
		//console.log("	<- "+strJson);
		strJson = strJson.replace(/,?null/g,"");
		
		strJson = strJson.replace( /\[ null,/g , "[ " );
		strJson = strJson.replace( /\[,/g , "[" );
		//console.log("	-> "+strJson); 
		
		return strJson;
	}
	
	this.requestCatalogPerspectiveUpdateCallbacks=[];
	
	// dataObj { 
	//	catalogId:xxx,
	//	perspectiveJsonDef:xxx,
	//	successCallback:xxx,
	//	errorCallback:xxx
	// }
	this.requestPerspectiveUpdate = function(dataObj) {
		
		var curRequestId=myself.requestCatalogPerspectiveUpdateCallbacks.length;
		myself.requestCatalogPerspectiveUpdateCallbacks.push(dataObj);
		if (myself._callback_UpdatedPerspective_debug==true) {
			console.log("MxAPI Requesting Update of Perspective "+dataObj.perspectiveId+" [Perspectives]");
		}		
		
		// copy data so that we don't modify user's array
		jsonCopy=JSON.parse(JSON.stringify(dataObj.perspectiveJsonDef));
		// remov the 'definition' part of the json, which is not expected on server side
		if (jsonCopy.definition!=null) { delete jsonCopy.definition; }
				
		//console.log(jsonCopy);
		//console.log("sending customization requestId="+curRequestId);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_perspective", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "perspectiveId":dataObj.perspectiveId,
									 			 "catalogId":dataObj.catalogId,
												 "jsonDef":stringifyWithoutNull(jsonCopy)									 
									 			}));			
	}
	
	this._handleUpdatedPerspectiveMsg= function (mxUpdatedPerspectiveMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=mxUpdatedPerspectiveMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_UpdatedPerspective_debug==true) {
			console.log("MxAPI Received Updated Perspective [Perspectives]\n"+decodedData);
		}
		
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestCatalogPerspectiveUpdateCallbacks[requestId];
		//console.log("received customization requestId="+requestId+" -> "+requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="perspective update refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}
	
	
//------- Customize Catalog --------	
	
		this.requestCatalogCustomParamsUpdateCallbacks=[];
		
		/*dataObj : {
			  catalogId,
			  thumbnailUrl, // URL of global thumbnail for this catalog
			  itemsUrlPrefix, // prefix to be applied for non absolute URLS
			  itemNameFields, // CSV list of fields names to be used for items cards titles
			  itemThumbnailUrlField, // containing URL for thumbnail of items cards
			  perspectiveMatchField, // containing name of field used for perspective auto-detection
			  timeFieldTermId, // containing termId of the date term to be used as Kibana default time field
			  successCallback,
			  errorCallback
		  }
		*/
		this.requestCustomizeCatalog = function(dataObj) {
											
			var curRequestId=myself.requestCatalogCustomParamsUpdateCallbacks.length;
			myself.requestCatalogCustomParamsUpdateCallbacks.push(dataObj);
			if (myself._callback_CustomizedCatalog_debug==true) {
				console.log("MxAPI Requesting Customize Catalog "+catalogId+" [Catalogs]");
			}
			
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/customize_catalog", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "id":dataObj.catalogId,
													 "thumbnailUrl":dataObj.thumbnailUrl,
													 "itemsUrlPrefix":dataObj.itemsUrlPrefix,
													 "itemNameFields":dataObj.itemNameFields,
													 "itemThumbnailUrlField":dataObj.itemThumbnailUrlField,
													 "perspectiveMatchField":dataObj.perspectiveMatchField,
													 "timeFieldTermId":dataObj.timeFieldTermId
										 			}));			
		}
		
		this._handleCustomizedCatalogMsg= function (mxCustomizedCatalogMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var decodedData=mxCustomizedCatalogMsg.body;
			var parsedMsg = JSON.parse(decodedData);
			
			if (myself._callback_CustomizedCatalog_debug==true) {
				console.log("MxAPI Received Customized Catalog [Catalogs]\n"+decodedData);
			}
			
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestCatalogCustomParamsUpdateCallbacks[requestId];
			//console.log("received customization requestId="+requestId+" -> "+requestObj);
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { 
				requestObj.successCallback(); 
			}
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="catalog-customize refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}			
		}

	
//------- Create Catalog --------	
		this.subscribeToCreatedCatalog=function(callback_func,debug) {
			debug=debug||false;
			myself._callback_CreatedCatalog_debug=debug;
			myself._callback_CreatedCatalog=callback_func;
		}
		this.requestCreateCatalog = function(catalogName) {
			if (myself._callback_CreatedCatalog_debug==true) {
				console.log("MxAPI Requesting Create Catalog "+catalogName+" [Catalogs]");
			}
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_catalog", {}, 
									 JSON.stringify({"catalogName":catalogName }));
		}
		
		this._handleCreatedCatalogMsg= function (mxCreatedCatalogMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var decodedData=mxCreatedCatalogMsg.body;
			var parsedMsg = JSON.parse(decodedData);
			
			if (myself._callback_CreatedCatalog_debug==true) {
				console.log("MxAPI Received Created Catalog [Catalogs]\n"+decodedData);
			}
			if (myself._callback_CreatedCatalog!=null) {
				myself._callback_CreatedCatalog(parsedMsg); 
			}
		}

		
	//------- Delete Catalog --------	
		// contains error/success callback functions of each field update request
		this.requestDeleteCatalogCallbacks=[];
		
		// dataObj {
		//   catalogId
		//   successCallback (func)
		//   errorCallback (func)(msg)
		// }
		this.requestDeleteCatalog= function(dataObj) {
			
			var curRequestId=myself.requestDeleteCatalogCallbacks.length;
			myself.requestDeleteCatalogCallbacks.push(dataObj);
			
			if (dataObj.catalogId==null || dataObj.catalogId=="") { dataObj.errorCallback("MxApi ERROR : field 'catalogId' is empty"); return; }
			
			var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
											"catalogId" : dataObj.catalogId });
			//console.log("sending new value : "+metadataStrValue);
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_catalog", {}, jsonStr);
		}
		
		// each field update request is given with a callback to be executed
		// in case of success or failure.
		this._handleDeletedCatalogMsg= function(deleteCatalogResponseMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var parsedMsg = JSON.parse(deleteCatalogResponseMsg.body);
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestDeleteCatalogCallbacks[requestId];
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="delete-catalog refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}		
		}

		
//------- Join Catalog --------	
	// contains error/success callback functions of each field update request
	this.requestJoinCatalogCallbacks=[];
	
	// dataObj {
	//   catalogId
	//   successCallback (func)
	//   errorCallback (func)(msg)
	// }
	this.requestJoinCatalog= function(dataObj) {
		
		var curRequestId=myself.requestJoinCatalogCallbacks.length;
		myself.requestJoinCatalogCallbacks.push(dataObj);
		
		if (dataObj.catalogId==null || dataObj.catalogId=="") { dataObj.errorCallback("MxApi ERROR : field 'catalogId' is empty"); return; }
		
		var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
										"catalogId" : dataObj.catalogId });
		//console.log("sending new value : "+metadataStrValue);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/join_catalog", {}, jsonStr);
	}
	
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleJoinCatalogMsg= function(joinCatalogResponseMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(joinCatalogResponseMsg.body);
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestJoinCatalogCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="join-catalog refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}		
	}


	//------- CatalogItemSelect --------
		this.subscribeToCatalogSelectedItem=function(callback_func,debug) {	
			debug=debug||false;
			myself._callback_CatalogSelectedItem_debug=debug;
			myself._callback_CatalogSelectedItem=callback_func;
		}	
		
		this.requestCatalogItemSelect = function(itemId) {
			var jsonStr = JSON.stringify({ "itemId" : itemId });
			if (myself._callback_CatalogSelectedItem_debug==true) {
				console.log("MxAPI Sending [SelectItem]\n"+jsonStr);
			}
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/select_communitItemnt", {}, jsonStr);
		}
		
		this._handleCatalogSelectedItemMsg= function (mxCatalogSelectedItemsMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var parsedMsg = JSON.parse(mxCatalogSelectedItemsMsg.body);
			if (myself._callback_CatalogSelectedItem_debug==true) {
				console.log("MxAPI Received [ItemSelected]\n"+mxCatalogSelectedItemsMsg.body);
			}
			if (myself._callback_CatalogSelectedItem!=null) {
				myself._callback_CatalogSelectedItem(parsedMsg); 
			}
		}
		
		
//------- Retrieve Catalog Items --------
// retrieve items contents, only for limited to the currently presented subset
		
	// contains error/success callback functions of each field update request
	this.requestCatalogItemsCallbacks=[];	
	
	// dataObj {
	//   fromIdx=0
	//   size=100
	//   filtersNames=[]
	//	 query=""
	//   sortByFieldName=""
	//   reverseSortOrder=false
	//   successCallback (func)({items:[],totalHits:<int>,totalItems:<int>})
	//   errorCallback (func)(msg)
	// }
	this.requestCatalogItems = function(dataObj) {
		if (dataObj.fromIdx==null) { dataObj.fromIdx=0; }
		if (dataObj.size==null) { dataObj.size=100; }
		if (dataObj.filtersNames==null) { dataObj.filtersNames=[]; }
		if (dataObj.query==null) { dataObj.query=""; }
		if (dataObj.sortByFieldName==null) { dataObj.sortByFieldName=""; }
		if (dataObj.reverseSortOrder==null) { dataObj.reverseSortOrder=false; }
		if (myself._callback_CatalogItems_debug==true) {
			console.log("MxAPI Requesting [Items]");
		}
		
		var curRequestId=myself.requestCatalogItemsCallbacks.length;
		myself.requestCatalogItemsCallbacks.push(dataObj);
		
		let requestObj = {"requestId" : curRequestId,
					"fromIdx":dataObj.fromIdx, 
					"size":dataObj.size,
					"filtersNames":dataObj.filtersNames,
					"query":dataObj.query,
					"sortByFieldName":dataObj.sortByFieldName,
					"reverseSortOrder":dataObj.reverseSortOrder};
		//console.log(requestObj);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalog_items", {}, 
						JSON.stringify(requestObj));
	}	
	
	this._handleCatalogItemsMsg= function (mxCatalogItemsMsg) {
				
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		// Items are GZIP and Base64 encoded
		let base64Msg = mxCatalogItemsMsg.body;
		let bytesGzip = window.atob(base64Msg);
		let inflatedMsg = pako.ungzip(bytesGzip,{ to: 'string' }); 
			
		var parsedMsg = JSON.parse(inflatedMsg);
		
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestCatalogItemsCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="field-update refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}


	
//------- Retrieve all Items ids --------
// retrieve FULL ids list of items corresponding to given query
	
	// contains error/success callback functions of each field update request
	this.requestCatalogItemsIdsCallbacks=[];	
	
	// dataObj {
	//   fromIdx=0
	//   size=100
	//   filtersNames=[]
	//	 query=""
	//   sortByFieldName=""
	//   reverseSortOrder=false
	//   successCallback (func)({items:[],totalHits:<int>,totalItems:<int>})
	//   errorCallback (func)(msg)
	// }
	this.requestCatalogItemsIds = function(dataObj) {
		if (dataObj.fromIdx==null) { dataObj.fromIdx=0; }
		if (dataObj.size==null) { dataObj.size=100; }
		if (dataObj.filtersNames==null) { dataObj.filtersNames=[]; }
		if (dataObj.query==null) { dataObj.query=""; }
		if (dataObj.sortByFieldName==null) { dataObj.sortByFieldName=""; }
		if (dataObj.reverseSortOrder==null) { dataObj.reverseSortOrder=false; }
		if (myself._callback_CatalogItems_debug==true) {
			console.log("MxAPI Requesting [Items Ids]");
		}
		
		var curRequestId=myself.requestCatalogItemsIdsCallbacks.length;
		myself.requestCatalogItemsIdsCallbacks.push(dataObj);
		
		let requestObj = {"requestId" : curRequestId,
					"fromIdx":dataObj.fromIdx, 
					"size":dataObj.size,
					"filtersNames":dataObj.filtersNames,
					"query":dataObj.query,
					"sortByFieldName":dataObj.sortByFieldName,
					"reverseSortOrder":dataObj.reverseSortOrder};
		//console.log(requestObj);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalog_items_allids_request", {}, 
						JSON.stringify(requestObj));
	}	
	
	this._handleCatalogItemsIdsMsg= function (mxCatalogItemsIdsMsg) {
				
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(mxCatalogItemsIdsMsg.body);
		
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestCatalogItemsIdsCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="items-ids refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}


//------- Update Field Value --------
	// contains error/success callback functions of each field update request
	this.requestFieldValueUpdateCallbacks=[];
	
	// dataObj { 
	//   id (item id)
	//   fieldName
	//   fieldValue
	//   successCallback (func)
	//   errorCallback (func)(msg)
	// }
	this.requestFieldValueUpdate= function(dataObj) {
		
		// if value contents is too long, split it in several chunks to send it
		if (dataObj.fieldValue.length>MX_WS_FIELD_VALUE_MAX_CHARS) {
			myself.requestMultipartFieldValueUpdate(dataObj);
			return;
		}

		var curRequestId=myself.requestFieldValueUpdateCallbacks.length;
		myself.requestFieldValueUpdateCallbacks.push(dataObj);
		
		if (dataObj.id==null || dataObj.id=="") { dataObj.errorCallback("MxApi ERROR : field 'id' is empty"); return; }
		if (dataObj.fieldName==null || dataObj.fieldName=="") { dataObj.errorCallback("MxApi ERROR : field 'fieldName' is empty"); return; }
		if (dataObj.fieldValue==null) { dataObj.errorCallback("MxApi ERROR : field 'fieldValue' is null"); return; }
		
		// if it is not a string, we expect a Json value, to be encoded
		if (typeof(dataObj.fieldValue)!="string") {
			dataObj.fieldValue=JSON.stringify(dataObj.fieldValue);
		}
		// removing extra spaces coming sometimes from copy/paste operations
		dataObj.fieldValue=dataObj.fieldValue.trim();
		
		
		var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
										"itemId" : dataObj.id,
										"nbchunks" : 1,
										"fieldName" : dataObj.fieldName,
										"fieldValue" : dataObj.fieldValue});
		//console.log("sending new value : "+jsonStr);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_field_value", {}, jsonStr);
	}
	
	
	// dataObj {
	//   id (item id)
	//   fieldName
	//   fieldValue
	//   successCallback (func)
	//   errorCallback (func)(msg)
	// }
	this.requestMultipartFieldValueUpdate= function(dataObj) {
		
		//console.log(new Date().getTime());
		if (dataObj.requestId==null) {

			var curRequestId=myself.requestFieldValueUpdateCallbacks.length;
			myself.requestFieldValueUpdateCallbacks.push(dataObj);
			dataObj.requestId=curRequestId;
				
			if (dataObj.id==null || dataObj.id=="") { dataObj.errorCallback("MxApi ERROR : field 'id' is empty"); return; }
			if (dataObj.fieldName==null || dataObj.fieldName=="") { dataObj.errorCallback("MxApi ERROR : field 'fieldName' is empty"); return; }
			if (dataObj.fieldValue==null) { dataObj.errorCallback("MxApi ERROR : field 'fieldValue' is null"); return; }
		}		
		// if this is the first sending, prepare the data to be sent chunk by chunk into dataStillToSend variable
		if (dataObj.dataStillToSend==null) {
			// if it is not a string, we expect a Json value, to be encoded
			if (typeof(dataObj.fieldValue)!="string") {
				dataObj.fieldValue=JSON.stringify(dataObj.fieldValue);
			}
			dataObj.fieldValue=dataObj.fieldValue.trim();
			dataObj.dataStillToSend=dataObj.fieldValue;
		}		
		// calculate total nb of chunks
		if (dataObj.nbchunks==null) {
			dataObj.nbchunks = Math.ceil(dataObj.fieldValue.length/MX_WS_FIELD_VALUE_MAX_CHARS);
			dataObj.curChunkNb=0;
			
		}
		dataObj.curChunkNb=dataObj.curChunkNb+1;
		
		// send the next chunk. once response from server will be received will send next chunk
		let curValueChunk=dataObj.dataStillToSend.substr(0,MX_WS_FIELD_VALUE_MAX_CHARS);
		
		// prepare next chunk
		if (curValueChunk.length==0) { return; }
		else { 
			dataObj.dataStillToSend=dataObj.dataStillToSend.substr(MX_WS_FIELD_VALUE_MAX_CHARS);
			if (dataObj.dataStillToSend.length==0) { dataObj.dataStillToSend=null;}
		}
		 
		//console.log("sending chunk "+dataObj.curChunkNb+"/"+dataObj.nbchunks+" : "+"["+curValueChunk+"]");
		
		// actually send current chunk
		let jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
									"itemId" : dataObj.id,
									"nbChunks" : dataObj.nbchunks,
									"curChunkNb": dataObj.curChunkNb,
									"fieldName" : dataObj.fieldName,
									"fieldValue" : curValueChunk});
		//console.log("sending new value : "+jsonStr);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_field_value", {}, jsonStr);
		
	}
	
	
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleUpdateFieldResponseMsg= function(updateFieldResponseMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(updateFieldResponseMsg.body);
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestFieldValueUpdateCallbacks[requestId];
		//console.log("requestId="+requestId+" -> obj="+requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { 
			if (requestObj.dataStillToSend!=null) {
				myself.requestMultipartFieldValueUpdate(requestObj);				
			}
			else {
				// remove entry from callbacks (bug: all gets displayed then in progress bar) 
				myself.requestFieldValueUpdateCallbacks.pop(requestId);
				let newFieldValue=parsedMsg.fieldValue;
				if (requestObj.nbchunks>1) { newFieldValue=requestObj.fieldValue; }
				requestObj.successCallback(parsedMsg.fieldName,newFieldValue);
			}
		}
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="field-update refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}		
	}
	

	
	
//------- Update Term definition --------
	// contains error/success callback functions of each field update request
	this.requestTermUpdateCallbacks=[];
	
	// dataObj {
	//	 catalogId
	//   termName
	//   termType
	//   termEnumsList array[enum1,enum2]
	//   termIsMultiEnum true|false
	//   successCallback (func)
	//   errorCallback (func)(msg)
	// }
	this.requestTermUpdate= function(dataObj) {
		
		var curRequestId=myself.requestTermUpdateCallbacks.length;
		myself.requestTermUpdateCallbacks.push(dataObj);
		
		if (dataObj.termName==null || dataObj.fieldName=="") { dataObj.errorCallback("MxApi ERROR : field 'termName' is empty"); return; }
		if (dataObj.termType==null) { dataObj.errorCallback("MxApi ERROR : field 'termType' is null"); return; }
		
		var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
										"catalogId" : dataObj.catalogId,			
										"termName" : dataObj.termName,
										"termType" : dataObj.termType,
										"termEnumsList" : dataObj.termEnumsList,
										"termIsMultiEnum" : dataObj.termIsMultiEnum,
										});
		
		
		//console.log("requesting term-update "+curRequestId+" : "+dataObj.termName+":"+dataObj.termType);
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_term", {}, jsonStr);
	}
	
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleUpdateTermResponseMsg= function(updateFieldResponseMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(updateFieldResponseMsg.body);
		let requestId=parsedMsg.requestId;
		
		//console.log("handling request term-update answer "+requestId);
		let requestObj=myself.requestTermUpdateCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="term-update refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}		
	}
	

//------- Delete Term --------
	this.subscribeToDeletedTerm=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_DeleteTerm_debug=debug;
		myself._callback_DeleteTerm=callback_func;
	}
	
	this.requestDeleteTerm= function(catalogId,termName) {
		
		if (myself._callback_DeleteTerm_debug==true) {
			console.log("MxAPI Sending Request Deleted Term : "+termName);
		}
		
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_term", {}, 
				JSON.stringify({ 	"catalogId" : catalogId,
									"termName" : termName
								}));
	}

	this._handleDeleteTermResponseMsg= function(deleteTermResponseMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(deleteTermResponseMsg.body);
		
		if (myself._callback_DeleteTerm_debug==true) {
			console.log("MxAPI Received Deleted Term\n"+decodedData);
		}
		if (myself._callback_DeleteTerm!=null) {
			myself._callback_DeleteTerm(parsedMsg); 
		}
	}
		
//------- Create Term --------
	this.requestCreateTermCallbacks=[];
	/*
	this.subscribeToCreatedTerm=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_CreateTerm_debug=debug;
		myself._callback_CreateTerm=callback_func;
	}
	*/
	// dataObj:
	// 		catalogId
	//		termName
	//		termDatatype
	//		complementaryInfoMap: map containing complementary info, might be needed depending on created datatype
	//  	successCallback (func)
	//   	errorCallback (func(msg))
	this.requestCreateTerm= function(dataObj) {
		
		var curRequestId=myself.requestCreateTermCallbacks.length;
		myself.requestCreateTermCallbacks.push(dataObj);
		
		
		if (myself._callback_CreateTerm_debug==true) {
			console.log("MxAPI Sending Request Create Term : "+dataObjtermName);
		}
		
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_term", {}, 
				JSON.stringify({ 	"requestId" : curRequestId,
									"catalogId" : dataObj.catalogId,
									"termName" : dataObj.termName,
									"termDatatype" : dataObj.termDatatype,
									"complementaryInfoMap" : dataObj.complementaryInfoMap
								}));
	}

	this._handleCreateTermResponseMsg= function(createTermResponseMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(createTermResponseMsg.body);
		let requestId=parsedMsg.requestId;
		
		//console.log("handling request create-filter answer "+requestId);
		let requestObj=myself.requestCreateTermCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="create-term refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}	
	}
		
			
		
//------- Create Filter --------	
	
	this.requestCreateFilterCallbacks=[];
	
	// dataObj {
	//		filterName:"xxx",
	//		queryString:"xxx"
	//  	successCallback (func)
	//   	errorCallback (func)(msg)
	//	}
	this.requestCreateFilter = function(dataObj) {
		
		var curRequestId=myself.requestCreateFilterCallbacks.length;
		myself.requestCreateFilterCallbacks.push(dataObj);
		
		
		if (myself._callback_CreatedFilter_debug==true) {
			console.log("MxAPI Requesting Create Filter "+filterName+"='"+queryString+"'");
		}
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_filter", {}, 
								 JSON.stringify({"requestId":curRequestId,
									 			 "filterName":dataObj.filterName,
									 			 "query":dataObj.queryString}));
	}
	
	this._handleCreatedFilterMsg= function (mxCreatedFilterMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(mxCreatedFilterMsg.body);
		let requestId=parsedMsg.requestId;
		
		//console.log("handling request create-filter answer "+requestId);
		let requestObj=myself.requestCreateFilterCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
		else {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="create-filter refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}		
	}
	
//------- Update Filter --------	
	this.subscribeToUpdatedFilter=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_UpdatedFilter_debug=debug;
		myself._callback_UpdatedFilter=callback_func;
	}
	this.requestUpdateFilter = function(filterName,queryString) {
		if (myself._callback_UpdatedFilter_debug==true) {
			console.log("MxAPI Requesting Update Filter "+filterName+"='"+queryString+"'");
		}
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_filter", {}, 
								 JSON.stringify({"filterName":filterName,
									 			 "query":queryString}));
	}
	
	this._handleUpdatedFilterMsg= function (mxUpdatedFilterMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=mxUpdatedFilterMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_UpdatedFilter_debug==true) {
			console.log("MxAPI Received Updated Filter\n"+decodedData);
		}
		if (myself._callback_UpdatedFilter!=null) {
			myself._callback_UpdatedFilter(parsedMsg); 
		}
	}

//------- Delete Filter --------	
	this.subscribeToDeletedFilter=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_DeletedFilter_debug=debug;
		myself._callback_DeletedFilter=callback_func;
	}
	this.requestDeleteFilter = function(filterName) {
		if (myself._callback_CreatedFilter_debug==true) {
			console.log("MxAPI Requesting Delete Filter "+filterName);
		}
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_filter", {}, 
								 JSON.stringify({"filterName":filterName}));
	}
		
	this._handleDeletedFilterMsg= function (mxDeletedFilterMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var decodedData=mxDeletedFilterMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_DeletedFilter_debug==true) {
			console.log("MxAPI Received Deleted Filter\n"+decodedData);
		}
		if (myself._callback_DeletedFilter!=null) {
			myself._callback_DeletedFilter(parsedMsg); 
		}
	}

	
//------- Create Item --------
		// contains error/success callback functions of each field update request
		this.requestCreateItemCallbacks=[];
				
		// dataObj {
		//	 catalogId
		//   fieldsMap
		//   successCallback (func)
		//   errorCallback (func)(msg)
		// }
		this.requestCreateItem= function(dataObj) {
			
			var curRequestId=myself.requestCreateItemCallbacks.length;
			myself.requestCreateItemCallbacks.push(dataObj);
						
			
			// preparing / cleaning fieldsMap
			// removing extra spaces, coming sometimes from copy/paste operations
			// stringify json objects
			for (fieldName in dataObj.fieldsMap) {
				let fieldVal=dataObj.fieldsMap[fieldName];
				if (typeof(fieldVal)!="string") {
					fieldVal=JSON.stringify(fieldVal);
				}
				// removing empty values
				if (fieldVal.length==0 
						|| fieldVal=="\"\"" 
						|| fieldVal=="''"
						|| fieldVal=="{}") {
					dataObj.fieldsMap[fieldName]=null;
				}
				
				dataObj.fieldsMap[fieldName]=fieldVal.trim();
			}
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
											"catalogId" : dataObj.catalogId,			
											"fieldsMap" : dataObj.fieldsMap											
											});
			
			
			//console.log("requesting term-update "+curRequestId+" : "+dataObj.termName+":"+dataObj.termType);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_item", {}, jsonStr);
		}
		
		// each field update request is given with a callback to be executed
		// in case of success or failure.
		this._handleCreatedItemResponseMsg= function(createdItemResponseMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var parsedMsg = JSON.parse(createdItemResponseMsg.body);
			let requestId=parsedMsg.requestId;
			
			//console.log("handling request create item answer "+requestId+" : "+parsedMsg);
			let requestObj=myself.requestCreateItemCallbacks[requestId];
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="create item refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}		
		}
		
	
//------- Delete Items --------
		
		// @param catalogId requested catalog. If NULL, get all catalogs
		this.requestDeleteItems = function(itemsIdsArray) {
			
			if (itemsIdsArray==null) { return; }
			if (myself._callback_DeleteItems_debug==true) {
				console.log("MxAPI Requesting [Delete Items] ids="+itemsIdsArray);
			}
			
			let jsonData = { 
					 "itemsIds" : itemsIdsArray,				 
					};
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_items", {}, JSON.stringify(jsonData));
		}
		
		this.requestDeleteItemsByQuery = function(query,filtersNames) {
			// non-empty search Query required
			if (query==null) { query=""; }
			if (filtersNames==null) { filtersNames=[]; }
			
			if (myself._callback_CatalogItems_debug==true) {
				console.log("MxAPI Requesting [Deleting Items] : query='"+query+"' filters='"+filtersIds+"'");
			}
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_items_by_query", {}, 
							JSON.stringify({"filtersNames":filtersNames,
											"query":query
											}));			
		}
		
// ------ Set Catalog Lexic Entry -------
		
		// contains error/success callback functions of each field update request
		this.requestSetCatalogLexicEntryCallbacks=[];
				
		// dataObj {
		//	 catalogId
		//   langShortName
		//   entryName
		//   entryTranslation 
		//   successCallback (func)
		//   errorCallback (func)(msg)
		// }
		this.requestSetCatalogLexicEntry= function(dataObj) {
			
			var curRequestId=myself.requestSetCatalogLexicEntryCallbacks.length;
			myself.requestSetCatalogLexicEntryCallbacks.push(dataObj);
			
			var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
											"catalogId" : dataObj.catalogId,			
											"langShortName" : dataObj.langShortName,
											"entryName" : dataObj.entryName,
											"entryTranslation" : dataObj.entryTranslation
											});
			
			
			//console.log("requesting term-update "+curRequestId+" : "+dataObj.termName+":"+dataObj.termType);
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_catalog_lexic", {}, jsonStr);
		}
		
		// each field update request is given with a callback to be executed
		// in case of success or failure.
		this._handleSetCatalogLexicResponseMsg= function(responseMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var parsedMsg = JSON.parse(responseMsg.body);
			let requestId=parsedMsg.requestId;
			
			//console.log("handling request create item answer "+requestId+" : "+parsedMsg);
			let requestObj=myself.requestSetCatalogLexicEntryCallbacks[requestId];
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="set catalog lexic refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}		
		}
		
		// ------ Set Term Lexic Entry -------
			
			// contains error/success callback functions of each field update request
			this.requestSetTermLexicEntryCallbacks=[];
					
			// dataObj {
			//	 catalogId
			//   langShortName
			//   termName
			//   entryTranslation 
			//   successCallback (func)
			//   errorCallback (func)(msg)
			// }
			this.requestSetTermLexicEntry= function(dataObj) {
				
				var curRequestId=myself.requestSetTermLexicEntryCallbacks.length;
				myself.requestSetTermLexicEntryCallbacks.push(dataObj);
				
				var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
												"catalogId" : dataObj.catalogId,			
												"langShortName" : dataObj.langShortName,
												"termName" : dataObj.termName,
												"entryTranslation" : dataObj.entryTranslation
												});
				
				
				//console.log("requesting term-update "+curRequestId+" : "+dataObj.termName+":"+dataObj.termType);
				myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
				myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_term_lexic", {}, jsonStr);
			}
			
			// each field update request is given with a callback to be executed
			// in case of success or failure.
			this._handleSetTermLexicResponseMsg= function(responseMsg) {
				
		    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
				var parsedMsg = JSON.parse(responseMsg.body);
				let requestId=parsedMsg.requestId;
				
				//console.log("handling request create item answer "+requestId+" : "+parsedMsg);
				let requestObj=myself.requestSetTermLexicEntryCallbacks[requestId];
				if (requestObj==null) { return; }
				if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
				else {
					let errorMsg=parsedMsg.rejectMessage;
					// ensure error message is not empty
					// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
					if (errorMsg==undefined) { errorMsg="set term lexic refused by server, sorry." }
					requestObj.errorCallback(errorMsg); 
				}		
			}
				
			
//------- Upload Files --------
			
	// contains error/success callback functions of each field update request
	this.requestUploadFilesCallbacks=[];
			
	// dataObj {
	//	 catalogId
	//   filesDescriptions { 
	//   successCallback (func)
	//   errorCallback (func)(msg)
	// }
	this.requestUploadFiles= function(dataObj) {
		
		var curRequestId=myself.requestUploadFilesCallbacks.length;
		myself.requestUploadFilesCallbacks.push(dataObj);
		
		let fileDescriptions=[];
		
		// send request to prepare upload, server will check
		// quotas are ok
		for (let pos=0;pos<dataObj.filesToUpload.length;pos++) {	
			fileObj=dataObj.filesToUpload[pos];
			let fileDesc={ name : fileObj.name,byteSize : fileObj.size, id:fileDescriptions.length};
			fileObj.id=fileDescriptions.length;
			fileDescriptions.push(fileDesc);	
		}
				
		
		myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
		var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
										"catalogId" : dataObj.catalogId,			
										"fileDescriptions" : fileDescriptions											
										});
		
		
		//console.log("requesting files upload "+curRequestId);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_userdata_files", {}, jsonStr);
		
	}
	
	
		
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleUploadFilesAnswer= function(uploadFilesAnswerMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(uploadFilesAnswerMsg.body);
		let requestId=parsedMsg.requestId;
		
		//console.log("handling upload files answer "+requestId+" : "+parsedMsg);
		let requestObj=myself.requestUploadFilesCallbacks[requestId];
		requestObj.processingTaskId=parsedMsg.processingTaskId;
		//console.log(requestObj);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess!=true) {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="uploading files refused by server, sorry." }
			requestObj.errorCallback(errorMsg);
			return;
		}		
		
		// if OK start uploading contents of user-files, they will be stored
		// in catalog drive.
		// upload each file one by one
	    for (let pos=0;pos<requestObj.filesToUpload.length;pos++) {	 
	    	let fileObj=requestObj.filesToUpload[pos];
	    	//console.log("starting uploading file '"+fileObj.name+"'");
		    let reader = new FileReader();
		    reader.onloadend = function (evt) 
		    { 
		    	/*
		    	if (evt.target.readyState == FileReader.DONE) {
		            alert(String(reader.result[0]));
		        }
		        */
		        let buffer = reader.result;
				let rawdata = new Uint8Array(buffer);			
				
				let sendRawData = function(startRawdataPos,curSequenceNumber) {
					//console.log("sendRawData("+curSequenceNumber+"):["+startRawdataPos+"]");
					let sendingBuffer=[];
					let curRawdataPos=startRawdataPos;
					//strDataContents = compressBase64(view);
					while ((curRawdataPos==startRawdataPos || curRawdataPos%MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE!=0)
								&& curRawdataPos<rawdata.length) {
						sendingBuffer.push(rawdata[curRawdataPos]);
						curRawdataPos++;
					}
						
				    jsonData={ clientFileId : fileObj.id, processingTaskId:requestObj.processingTaskId, rawContents: sendingBuffer, sequenceNumber:curSequenceNumber };
			    	//console.log('### Sending file upload contents request : '+JSON.stringify(jsonData));
			    	//console.log("	### Sending file upload contents request  "+curSequenceNumber+" : -> ["+(curRawdataPos-1)+"]");
				    myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_userdata_file_contents", {},JSON.stringify(jsonData));
					
					if (curRawdataPos<rawdata.length && requestObj.abort!=true) {
						let timer = setInterval(function() {
							clearInterval(timer);
							//console.log("	### planning next send in 1s from seq="+(curSequenceNumber+1)+"["+curRawdataPos+"]");
							sendRawData(curRawdataPos,curSequenceNumber+1); 
						}, MX_WS_UPLOAD_FILE_SEND_PERIOD_MS);
					}
					
				}
				let timer = setInterval(function() { 
					clearInterval(timer);
					sendRawData(0,1); }, MX_WS_UPLOAD_FILE_SEND_PERIOD_MS);
							
		    }	
	    	reader.readAsArrayBuffer(fileObj);			
		}
	    
		//requestObj.successCallback();
		 
		 
	}

	this._handleUploadFilesContentsAnswer= function(uploadFilesContentsAnswerMsg) {
		
    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
		var parsedMsg = JSON.parse(uploadFilesContentsAnswerMsg.body);
		let requestId=parsedMsg.requestId;
		
		//console.log("handling upload files contents answer "+requestId+" : "+parsedMsg);
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess!=true) {
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some misbehaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="uploading files contents refused by server, sorry." }
			requestObj.errorCallback(errorMsg);
			// will stop running upload process
			requestObj.abort=true;
			return;
		}		
		
	}
	

//------- Request Plan Update --------	
		
		this.requestPlanUpdateCallbacks=[];
		
		// dataObj {
		// 	userId
		// 	planId
		//  successCallback
		//  errorCallback
		// }
		this.requestPlanUpdate = function(dataObj) {
			
			var curRequestId=myself.requestPlanUpdateCallbacks.length;
			myself.requestPlanUpdateCallbacks.push(dataObj);
			if (myself._callback_PlanRequest_debug==true) {
				console.log("MxAPI Requesting Plan Update");
			}
			
			myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_plan_request", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "userId":dataObj.userId,
										 			 "planId":dataObj.planId
										 			}));			
		}
		
		this._handlePlanUpdateMsg = function (responseMsg) {
			
	    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
			var decodedData=responseMsg.body;
			var parsedMsg = JSON.parse(decodedData);
			
			if (myself._callback_PlanRequest_debug==true) {
				console.log("MxAPI Received Plan Update Response\n"+decodedData);
			}
			
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestPlanUpdateCallbacks[requestId];
			//console.log("received Received Plan Update Response");
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) {
				requestObj.successCallback(parsedMsg); 
			}
			else {
				let errorMsg=parsedMsg.rejectMessage;
				// ensure error message is not empty
				// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
				if (errorMsg==undefined) { errorMsg="plan update request refused by server, sorry." }
				requestObj.errorCallback(errorMsg); 
			}			
		}


	//------- Confirm Plan Update Payment --------	
			
			this.requestPlanUpdatePaymentConfirmCallbacks=[];
			
			// dataObj {
			// 	userId
			// 	planId
			// 	transactionId
			// 	totalCost
			//  paymentMethod: paypal|sandbox
			// 	paymentDetails : string
			//  successCallback
			//  errorCallback
			// }
			this.requestPlanUpdatePaymentConfirm = function(dataObj) {
				
				var curRequestId=myself.requestPlanUpdatePaymentConfirmCallbacks.length;
				myself.requestPlanUpdatePaymentConfirmCallbacks.push(dataObj);
				if (myself._callback_PlanRequestPaymentConfirm_debug==true) {
					console.log("MxAPI Requesting Plan Update Payment Confirmation");
				}
				
				myself._callback_NetworkEvent(MX_UPSTREAM_MSG);
				myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_plan_confirm_payment_request", {}, 
										 JSON.stringify({"requestId" : curRequestId,
											 			 "userId":dataObj.userId,
											 			 "planId":dataObj.planId,
											 			 "transactionId":dataObj.transactionId,
											 			 "totalCost":dataObj.totalCost,
											 			 "paymentMethod":dataObj.paymentMethod,
											 			 "paymentDetails":dataObj.paymentDetails
											 			}));			
			}
			
			this._handlePlanUpdatePaymentConfirmMsg = function (responseMsg) {
				
		    	myself._callback_NetworkEvent(MX_DOWNSTREAM_MSG);
				var decodedData=responseMsg.body;
				var parsedMsg = JSON.parse(decodedData);
				
				if (true || myself._callback_PlanRequestPaymentConfirm_debug==true) {
					console.log("MxAPI Received Plan Update Payment Confirmation Msg\n"+decodedData);
				}
				
				let requestId=parsedMsg.requestId;
				let requestObj=myself.requestPlanUpdatePaymentConfirmCallbacks[requestId];
				
				if (requestObj==null) { return; }
				if (parsedMsg.isSuccess==true) {
					console.log("invoking success callback for payment confirmation");
					requestObj.successCallback(parsedMsg); }
				else {
					let errorMsg=parsedMsg.rejectMessage;
					// ensure error message is not empty
					// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
					if (errorMsg==undefined) { errorMsg="confirm plan update payment refused by server, sorry." }
					requestObj.errorCallback(errorMsg); 
				}			
			}


}

