

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
var MX_WS_UPLOAD_FILE_MAX_LINES = 500;
var MX_WS_UPLOAD_FILE_MAX_RAW_SIZE_BYTE = 10000;
var MX_WS_UPLOAD_FILE_SEND_PERIOD_MS = 5;

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
	
	// Set user preferences
	myself._callback_SetUserPreferences=null;	
	myself._callback_SetUserPreferences_debug=false;
	
	// Server Messages
	myself._callback_ServerMessages=null;
	myself._callback_ServerMessages_debug=false;
	
	// Server heartbeat
	myself._callback_ServerHeartbeatEvent=null;
	myself._callback_ServerHeartbeatEvent_debug=false;
		
	// Create Catalog
	myself._callback_CreateCatalog=null;	
	myself._callback_CreateCatalog_debug=false;
	
	// Delete Catalog
	myself._callback_DeleteCatalog=null;
	myself._callback_DeleteCatalog_debug=false;
		
	// Get Catalogs
	myself._callback_Catalogs=null;
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
	myself._callback_CreateTerm=null;
	myself._callback_CreateTerm_debug=false;
	
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
			
			// catalog
			myself._stompClient.subscribe('/user/queue/catalogs',myself._handleCatalogsMsg);
			myself._stompClient.subscribe('/user/queue/catalog_selected',myself._handleSelectedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/created_catalog',myself._handleCreatedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/deleted_catalog',myself._handleDeletedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/catalog_customized',myself._handleCustomizedCatalogMsg);
			myself._stompClient.subscribe('/user/queue/catalog_lexic_updated',myself._handleSetCatalogLexicResponseMsg);			
			
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
			myself._stompClient.subscribe('/user/queue/selected_item',myself._handleCatalogSelectedItemMsg);
			myself._stompClient.subscribe('/user/queue/upload_items_csv_response',myself._handleUploadItemsFromCsvAnswer);
			myself._stompClient.subscribe('/user/queue/download_items_csv_response',myself._handleDownloadItemsCsvAnswer);
			myself._stompClient.subscribe('/user/queue/created_item',myself._handleCreatedItemResponseMsg);
			myself._stompClient.subscribe('/user/queue/upload_userdata_files_response',myself._handleUploadFilesAnswer);
			myself._stompClient.subscribe('/user/queue/upload_userdata_file_contents_progress',myself._handleUploadFilesContentsAnswer);
			
			
			// fields
			myself._stompClient.subscribe('/user/queue/field_value',myself._handleUpdateFieldResponseMsg);
			
			// misc
			myself._stompClient.subscribe('/user/queue/gui_messaging',myself._handleGuiMsgFromServer);
			myself._stompClient.subscribe('/user/queue/gui_messaging_progress',myself._handleGuiMsgFromServer);
			
			// contents change notif (broadcast) 
			myself._stompClient.subscribe('/queue/catalog_contents_changed',myself._handleCatalogContentsChangedMsg);
			// server heartbeat (broadcast)
			myself._stompClient.subscribe('/queue/heartbeat',myself._handleServerHeartbeat);
			
			// register user
			myself._sendRegisterRequest();
			
		}
		myself._stompClient.connect(header, connectionCallback);
	}
	
//------- Messages from Server --------	
	this.subscribeToServerGuiMessages=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_ServerMessages_debug=debug;
		myself._callback_ServerMessages=callback_func;
	}
	
	this._handleGuiMsgFromServer= function (mxServerMsg) {
		var parsedMsg = JSON.parse(mxServerMsg.body)
		
		if (myself._callback_ServerMessages_debug==true) {
			console.log("MxAPI Received [ServerMessage]\n"+parsedMsg);
		}
		if (myself._callback_ServerMessages!=null) {			
			myself._callback_ServerMessages(parsedMsg); 
		}		
	}

	
//------- Server HeartBeat--------	
	// server heartbeat occuring every 3 seconds
	var HEARTBEAT_DELAY_TRESHOLD_SEC=7;
	var lastHeartbeatDate = new Date();
	var heartbeatTimerCheck = null;
	var curApplicationStatus = 'STOPPED';
		
	this.subscribeToServerHeartBeatEvent=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_ServerHeartbeatEvent_debug=debug;
		myself._callback_ServerHeartbeatEvent=callback_func;
	}
	
	
	this._handleServerHeartbeat= function (mxServerHeartbeatMsg) {
		lastHeartbeatDate=new Date();		
		var parsedMsg = JSON.parse(mxServerHeartbeatMsg.body);
		if (parsedMsg.applicationStatus!=curApplicationStatus) {
			curApplicationStatus=parsedMsg.applicationStatus;
			// FAILURE|MAINTENANCE|STOPPED|RUNNING			
			myself._callback_ServerHeartbeatEvent(parsedMsg.applicationStatus);			
		}
		if (myself._callback_ServerHeartbeatEvent_debug==true) {
			console.log("MxAPI Received [Server Heartbeat] : '"+curApplicationStatus+"'\n"+parsedMsg.count);
		}
		
		// check periodicaly that heartbeat arrive often enough
		// wait for 2 consecutive holes, bcause sometimes the jaavascript is blocked
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

//------- Register User --------	
	this._sendRegisterRequest = function() {
		
		console.log("Sending Metaindex user registration request");
		
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/register_request", {}, JSON.stringify({}));
	}
	
	this._handleRegisterAckMsg= function (mxRegisterAckMsg) {
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
		
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/set_user_preferences", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "userId":dataObj.userId,
									 			 "nickName":dataObj.nickName,
									 			 "languageId":dataObj.languageId,
									 			 "themeId":dataObj.themeId
									 			}));			
	}
	
	this._handleSetUserPreferencesMsg= function (responseMsg) {
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
		
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/set_user_catalogcusto", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "userId":dataObj.userId,
									 			 "catalogId":dataObj.catalogId,
									 			 "kibanaIFrame":dataObj.kibanaIFrame
									 			}));			
	}
	
	this._handleSetUserCatalogCustoMsg = function (responseMsg) {
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
				
				var parsedMsg = JSON.parse(mxCatalogContentsChangedMsg.body);
				if (myself._callback_CatalogContentsChanged_debug==true) {
					console.log("MxAPI Received [CatalogContentsChanged]\n"+mxCatalogContentsChangedMsg.body);
				}
				if (myself._callback_CatalogContentsChanged!=null) {
					myself._callback_CatalogContentsChanged(parsedMsg); 
				}
			}

//------- Download CSV file --------
			
	this.requestCsvDownloasCallbacks=[];
	
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

		var curRequestId=myself.requestCsvDownloasCallbacks.length;
		myself.requestCsvDownloasCallbacks.push(dataObj);
		dataObj.requestId=curRequestId;
		
    	//console.log('### Sending download request : '+JSON.stringify(jsonData));
    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/download_items_csv_request", {},JSON.stringify(dataObj));		

	}
	
	this.subscribeToCsvDownload=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_DownloadCsv_debug=debug;
		myself._callback_DownloadCsv=callback_func;
	}
	
	this._handleDownloadItemsCsvAnswer=function(msg) {
		
		var parsedMsg = JSON.parse(msg.body);
		
		if (myself._callback_DownloadCsv_debug==true) {
			console.log("[MxApi] Received answer to request for 'Download Items from CSV' accepted : "+parsedMsg.isSuccess);
			if (parsedMsg.isSuccess==false) {
				console.log("Reject msg = "+parsedMsg.rejectMessage);
			}
		}

		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestCsvDownloasCallbacks[requestId];
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

	
	this.subscribeToCsvUpload=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_UploadCsv_debug=debug;
		myself._callback_UploadCsv=callback_func;
	}
		
	this.requestUploadItemsFromCsv = function(fileHandle,chosenFieldsMapping) {
	 	
		let jsonData = {};
		jsonData.clientFileId=mx_csv_files_to_be_uploaded.length;		
		let nbItemsToBeCreated=0;
		
		// instantiate a new FileReader object to count total amount of items 
		// t be created
	    let reader = new FileReader();
	    reader.onload = function (file_contents) 
	    { 
	    	let CSVrows = file_contents.target.result.split("\n");
	    	let curLineNb=0;
	    	
	    	// count total nb entries
	    	while (curLineNb<CSVrows.length) {
	    		if (	   CSVrows[curLineNb].length>0 
	    				&& CSVrows[curLineNb][0]!='#' 
	    				&& !CSVrows[curLineNb].match(/^\s*$/)
	    				&& !CSVrows[curLineNb].match(/^\s*#/)
	    				&& curLineNb!=0 // ignore first line (header)
	    				) {
	    			nbItemsToBeCreated++;
	    		}
	    			
	    		curLineNb++;	    		
	    	}
	    	jsonData.totalNbEntries=nbItemsToBeCreated;
	    	jsonData.chosenFieldsMapping=chosenFieldsMapping;	    	
	    	
	    	// parse fields names and types
	    	let fieldsDefStr=CSVrows[0];
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
	    	myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_items_csv_request", {},JSON.stringify(jsonData));		
	 		
	    	jsonData.fileHandle=fileHandle;
	    	mx_csv_files_to_be_uploaded[jsonData.clientFileId]=jsonData;
	    }
	    
	    // load the file into an array buffer
	    reader.readAsText(fileHandle.files[0]);		
	}
	
	this._handleUploadItemsFromCsvAnswer=function(msg) {
		
		var parsedMsg = JSON.parse(msg.body);
		
		if (myself._callback_UploadCsv_debug==true) {
			console.log("[MxApi] Received answer to request for 'Upload Items from CSV' accepted : "+parsedMsg.isSuccess);
			if (parsedMsg.isSuccess==false) {
				console.log("Reject msg = "+parsedMsg.rejectMessage);
			}
		}

		// instantiate a new FileReader object
	    let reader = new FileReader();
	    let fileIndex=parsedMsg.clientFileId;
	    //console.log("file index = "+fileIndex);
	    let fileHandle=mx_csv_files_to_be_uploaded[fileIndex].fileHandle;
	    //console.log("fileHandle="+fileHandle);
	    //dumpStructure(mx_csv_files_to_be_uploaded);
	    let serverProcessingTaskId=parsedMsg.processingTaskId;
	    
	    if (fileHandle==null) { return; }
	    //console.log("received file upload answer : file = "+fileHandle.value);
	    
	    reader.onload = function (file_contents) 
	    { 
	    	let CSVrows = file_contents.target.result.split("\n");
	    	let curLineNb=0;
	    	let curLinesWsBuffer=[];
	    	
	    	if (myself._callback_UploadCsv!=null) {
				myself._callback_UploadCsv(parsedMsg); 
			}
	    	
	    	while (curLineNb<CSVrows.length) {
	    		// ignore first line (header)
	    		if (curLineNb==0) { curLineNb++; continue; }
	    		
	    		curLinesWsBuffer.push(CSVrows[curLineNb]);
	    		
	    		if (curLineNb % MX_WS_UPLOAD_FILE_MAX_LINES==0 || curLineNb==CSVrows.length-1) {
	    			//console.log("sending "+curLinesWsBuffer.length+" lines : "+curLinesWsBuffer);
	    			//console.log(curLinesWsBuffer);
	    			
	    			let jsonData = { 
	    				 "csvLines" : curLinesWsBuffer,
						 "processingTaskId" : serverProcessingTaskId,
						 "totalNbLines" : CSVrows.length,
	    				};
	    			
	    			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_filter_file_contents", {},JSON.stringify(jsonData));
	    			curLinesWsBuffer=[];
	    		}
	    		curLineNb++;	    		
	    	}
	    	
	    }

	    // load the file into an array buffer
	    reader.readAsText(fileHandle.files[0]);
	    
	}
	
	
//------- Catalogs List --------
	this.subscribeToCatalogsList=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_Catalogs_debug=debug;
		myself._callback_Catalogs=callback_func;
	}		
	// @param catalogId requested catalog. If NULL, get all catalogs
	this.requestCatalogs = function(catalogId) {
		
		if (catalogId==null) { catalogId=0; }
		if (myself._callback_Catalogs_debug==true) {
			console.log("MxAPI Requesting [Catalogs]");
		}
		
		let jsonData = { 
				 "catalogId" : catalogId,				 
				};
			
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalogs", {}, JSON.stringify(jsonData));
	}

	this._handleCatalogsMsg= function (mxCatalogsMsg) {

		//var decodedData=mxCatalogsMsg.body;
		var decodedData=uncompressBase64(mxCatalogsMsg.body);
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_Catalogs_debug==true) {
			console.log("MxAPI Received [Catalogs]\n"+decodedData);
		}
		if (myself._callback_Catalogs!=null) {
			myself._callback_Catalogs(parsedMsg); 
		}
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/select_catalog", {}, 
								 JSON.stringify({"catalogId":catalogId }));
	}
	
	this._handleSelectedCatalogMsg= function (mxSelectedCatalogMsg) {
		var decodedData=mxSelectedCatalogMsg.body;
		var parsedMsg = JSON.parse(decodedData);
		
		if (myself._callback_SelectedCatalog_debug==true) {
			console.log("MxAPI Received Selected Catalog [Catalogs]\n"+decodedData);
		}
		if (myself._callback_SelectedCatalog!=null) {
			myself._callback_SelectedCatalog(parsedMsg); 
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
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_perspective", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "perspectiveId":dataObj.perspectiveId,
										 			 "catalogId":dataObj.catalogId								 
										 			}));			
		}
		
		this._handleDeletedPerspectiveMsg= function (mxDeletedPerspectiveMsg) {
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_perspective", {}, 
								 JSON.stringify({"requestId" : curRequestId,
									 			 "perspectiveId":dataObj.perspectiveId,
									 			 "catalogId":dataObj.catalogId,
												 "jsonDef":stringifyWithoutNull(jsonCopy)									 
									 			}));			
	}
	
	this._handleUpdatedPerspectiveMsg= function (mxUpdatedPerspectiveMsg) {
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
			console.log("sending customization requestId="+curRequestId);
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/customize_catalog", {}, 
									 JSON.stringify({"requestId" : curRequestId,
										 			 "id":dataObj.catalogId,
													 "thumbnailUrl":dataObj.thumbnailUrl,
													 "itemsUrlPrefix":dataObj.itemsUrlPrefix,
													 "itemNameFields":dataObj.itemNameFields,
													 "itemThumbnailUrlField":dataObj.itemThumbnailUrlField,
													 "perspectiveMatchField":dataObj.perspectiveMatchField
										 			}));			
		}
		
		this._handleCustomizedCatalogMsg= function (mxCustomizedCatalogMsg) {
			var decodedData=mxCustomizedCatalogMsg.body;
			var parsedMsg = JSON.parse(decodedData);
			
			if (myself._callback_CustomizedCatalog_debug==true) {
				console.log("MxAPI Received Customized Catalog [Catalogs]\n"+decodedData);
			}
			
			let requestId=parsedMsg.requestId;
			let requestObj=myself.requestCatalogCustomParamsUpdateCallbacks[requestId];
			console.log("received customization requestId="+requestId+" -> "+requestObj);
			if (requestObj==null) { return; }
			if (parsedMsg.isSuccess==true) { requestObj.successCallback(); }
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
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_catalog", {}, 
									 JSON.stringify({"catalogName":catalogName }));
		}
		
		this._handleCreatedCatalogMsg= function (mxCreatedCatalogMsg) {
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
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_catalog", {}, jsonStr);
		}
		
		// each field update request is given with a callback to be executed
		// in case of success or failure.
		this._handleDeletedCatalogMsg= function(deleteCatalogResponseMsg) {
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
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/select_communitItemnt", {}, jsonStr);
		}
		
		this._handleCatalogSelectedItemMsg= function (mxCatalogSelectedItemsMsg) {
			var parsedMsg = JSON.parse(mxCatalogSelectedItemsMsg.body);
			if (myself._callback_CatalogSelectedItem_debug==true) {
				console.log("MxAPI Received [ItemSelected]\n"+mxCatalogSelectedItemsMsg.body);
			}
			if (myself._callback_CatalogSelectedItem!=null) {
				myself._callback_CatalogSelectedItem(parsedMsg); 
			}
		}
		
		
//------- Retrieve Catalog Items --------
		
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/get_catalog_items", {}, 
						JSON.stringify(requestObj));
	}	
	
	this._handleCatalogItemsMsg= function (mxCatalogItemsMsg) {
		
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
			console.log(parsedMsg);
			let errorMsg=parsedMsg.rejectMessage;
			// ensure error message is not empty
			// (otherwise can lead to some mis behaviour in user app (ex: x-editable) )
			if (errorMsg==undefined) { errorMsg="field-update refused by server, sorry." }
			requestObj.errorCallback(errorMsg); 
		}			
	}


//------- Update Field Value --------
	// contains error/success callback functions of each field update request
	this.requestFieldValueUpdateCallbacks=[];
	
	// dataObj {
	//   itemDescr
	//   fieldName
	//   fieldValue
	//   successCallback (func)
	//   errorCallback (func)(msg)
	// }
	this.requestFieldValueUpdate= function(dataObj) {
		
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
										"fieldName" : dataObj.fieldName,
										"fieldValue" : dataObj.fieldValue});
		//console.log("sending new value : "+jsonStr);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_field_value", {}, jsonStr);
	}
	
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleUpdateFieldResponseMsg= function(updateFieldResponseMsg) {
		var parsedMsg = JSON.parse(updateFieldResponseMsg.body);
		let requestId=parsedMsg.requestId;
		let requestObj=myself.requestFieldValueUpdateCallbacks[requestId];
		if (requestObj==null) { return; }
		if (parsedMsg.isSuccess==true) { requestObj.successCallback(parsedMsg.fieldName,
																	parsedMsg.fieldValue); }
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_term", {}, jsonStr);
	}
	
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleUpdateTermResponseMsg= function(updateFieldResponseMsg) {
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
		
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_term", {}, 
				JSON.stringify({ 	"catalogId" : catalogId,
									"termName" : termName
								}));
	}

	this._handleDeleteTermResponseMsg= function(deleteTermResponseMsg) {
		var parsedMsg = JSON.parse(deleteTermResponseMsg.body);
		
		if (myself._callback_DeleteTerm_debug==true) {
			console.log("MxAPI Received Deleted Term\n"+decodedData);
		}
		if (myself._callback_DeleteTerm!=null) {
			myself._callback_DeleteTerm(parsedMsg); 
		}
	}
		
//------- Create Term --------
	this.subscribeToCreatedTerm=function(callback_func,debug) {
		debug=debug||false;
		myself._callback_CreateTerm_debug=debug;
		myself._callback_CreateTerm=callback_func;
	}
	
	// complementaryInfoMap: map containing complementary info, might be needed depending on created datatype
	this.requestCreateTerm= function(catalogId,termName,termDatatype,complementaryInfoMap) {
		
		if (myself._callback_CreateTerm_debug==true) {
			console.log("MxAPI Sending Request Create Term : "+termName);
		}
		
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_term", {}, 
				JSON.stringify({ 	"catalogId" : catalogId,
									"termName" : termName,
									"termDatatype" : termDatatype,
									"complementaryInfoMap" : complementaryInfoMap
								}));
	}

	this._handleCreateTermResponseMsg= function(createTermResponseMsg) {
		var parsedMsg = JSON.parse(createTermResponseMsg.body);
		
		if (myself._callback_CreateTerm_debug==true) {
			console.log("MxAPI Received Create Term\n"+decodedData);
		}
		if (myself._callback_CreateTerm!=null) {
			myself._callback_CreateTerm(parsedMsg); 
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/create_filter", {}, 
								 JSON.stringify({"requestId":curRequestId,
									 			 "filterName":dataObj.filterName,
									 			 "query":dataObj.queryString}));
	}
	
	this._handleCreatedFilterMsg= function (mxCreatedFilterMsg) {
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_filter", {}, 
								 JSON.stringify({"filterName":filterName,
									 			 "query":queryString}));
	}
	
	this._handleUpdatedFilterMsg= function (mxUpdatedFilterMsg) {
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
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_filter", {}, 
								 JSON.stringify({"filterName":filterName}));
	}
		
	this._handleDeletedFilterMsg= function (mxDeletedFilterMsg) {
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
				
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/delete_items", {}, JSON.stringify(jsonData));
		}
		
		this.requestDeleteItemsByQuery = function(query,filtersNames) {
			// non-empty search Query required
			if (query==null) { query=""; }
			if (filtersNames==null) { filtersNames=[]; }
			
			if (myself._callback_CatalogItems_debug==true) {
				console.log("MxAPI Requesting [Deleting Items] : query='"+query+"' filters='"+filtersIds+"'");
			}
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
			myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_catalog_lexic", {}, jsonStr);
		}
		
		// each field update request is given with a callback to be executed
		// in case of success or failure.
		this._handleSetCatalogLexicResponseMsg= function(responseMsg) {
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
				myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/update_term_lexic", {}, jsonStr);
			}
			
			// each field update request is given with a callback to be executed
			// in case of success or failure.
			this._handleSetTermLexicResponseMsg= function(responseMsg) {
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
		
		var jsonStr = JSON.stringify({ 	"requestId" : curRequestId,
										"catalogId" : dataObj.catalogId,			
										"fileDescriptions" : fileDescriptions											
										});
		
		
		//console.log("requesting files upload "+curRequestId);
		myself._stompClient.send(myself.MX_WS_APP_PREFIX+"/upload_userdata_files", {}, jsonStr);
		
	}
	
	
		
	// each field update request is given with a callback to be executed
	// in case of success or failure.
	this._handleUploadFilesAnswer= function(upoloadFilesAnswerMsg) {
		var parsedMsg = JSON.parse(upoloadFilesAnswerMsg.body);
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
		// in catalog userdata space (also accessible via FTP
		// upload each file one by one
	    for (let pos=0;pos<requestObj.filesToUpload.length;pos++) {	 
	    	let fileObj=requestObj.filesToUpload[pos];
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
}

