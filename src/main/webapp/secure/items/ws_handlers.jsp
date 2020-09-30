<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

  
  <!-- callbacks required by metaindex API -->
 <script type="text/javascript" >
 
 var NB_ITEMS_PER_REQUEST=100;
 var _fromIdx=0;
 var _size=NB_ITEMS_PER_REQUEST;
 
 function ws_handlers_refreshItemsGui() {
	MxApi.requestGetCatalogs({'catalogId':<s:property value="currentCatalog.id"/>, 'successCallback':handleMxWsCatalogs});
	ws_handlers_requestItemsSearch(MxGuiHeader.getCurrentSearchQuery());
 }
 
 function onWsConnect(isConnected) {
	if (!isConnected) {
		footer_showAlert(ERROR, "<s:text name="global.unableToConnectToServer" />");
	}
	else {
		footer_showAlert(SUCCESS, "<s:text name="global.connectedToServer" />");
		ws_handlers_refreshItemsGui();
	}
 }
 function handleMxWsSelectedCatalog(msg) {}	 
 function handleMxWsItems(msg) {}
 function handleMxWsCreatedCatalog(msg) {}
 function handleMxWsSelectedItem(msg) {}
 function handleMxWsCreatedTerm(msg) {}
 function handleMxWsDeletedTerm(msg) {}
 
 function handleMxWsCsvUpload(msg) {
	 if (!msg.isSuccess) {
		 footer_showAlert(ERROR, msg.rejectMessage);
		 return;
	 }
	 else { footer_showAlert(INFO, "<s:text name="Items.uploadItems.starting"/>"); }
	
 }
 
 function handleMxWsDeletedFilter(msg)  {	 
	 if (!msg.isSuccess) {
		 footer_showAlert(ERROR, "Sorry, an error occured while deleting filter.");
		 return;
	 }
	// GUI refreshed on 'CatalogContentsChanged' message received appart
	 footer_showAlert(WARNING, "Filter '"+msg.filterName+"' deleted.");
	 filtersInsertSpot=MxGuiLeftBar.getFiltersInsertSpot();
	 for (var curFilter=filtersInsertSpot.firstChild;curFilter!==null;curFilter=curFilter.nextElementSibling) {		
		if (typeof(curFilter)!='object') { continue; }
		if (curFilter.descr.name==msg.filterName) { 
			curFilter.parentNode.removeChild(curFilter);
			break;
		}
	}
 }
 function handleMxWsCatalogContentsChanged(msg)  {	
	
	if (msg.catalogName!="<s:property value='currentCatalog.name'/>") { return; }
 	if (msg.modifType==MxApi.CATALOG_MODIF_TYPE.DOCS_LIST) {
		 if (msg.userNickname!="<s:property value='currentUserProfile.nickname'/>") {
			 footer_showAlert(INFO, 
					 msg.userNickname+" changed catalog with "+msg.nbImpactedDocs+" document(s). <a href='Items'>Click to Refresh</a>",
					 null,
					 5000);
		 } else {
			 // refresh the items list only, not the full GUI
			 // otherwise when creating a new item the popup 
			 // disappear for each new item which is not convenient to the user
			 //ws_handlers_refreshItemsGui();
			 ws_handlers_requestItemsSearch(MxGuiHeader.getCurrentSearchQuery()); 
		 }
	 }
 	
 	 else if (msg.modifType==MxApi.CATALOG_MODIF_TYPE.FIELD_VALUE) {
		 if (msg.userNickname!="<s:property value='currentUserProfile.nickname'/>") {
			 
			 if (msg.nbImpactedDocs==1) {
			 	footer_showAlert(INFO, 
			 				msg.userNickname+" changed contents of document \""+msg.impactedDocName+"\" : "+msg.impactDetails,
			 				null,
			 				5000);
			 } else {
				 footer_showAlert(INFO, msg.userNickname+" changed contents of "+msg.nbImpactedDocs+" document(s)");
			 }
		 } else { /*ws_handlers_refreshItemsGui();*/ }
	 }
 	else if (msg.modifType==MxApi.CATALOG_MODIF_TYPE.FIELDS_LIST) {
 		// nothing special to do
 		return;
 	}
	 else { console.log("ERROR: unknown catalog modif type '"+msg.modifType+"'"); }
	
	 
 }

 
 function handleMxWsUpdatedFilter(msg) {
	 if (!msg.isSuccess) {
		 footer_showAlert(ERROR, "Sorry, an error occured while updating filter.");
		 return;
	 }
	// GUI refreshed on 'CatalogContentsChanged' message received appart
	 footer_showAlert(SUCCESS, "Filter '"+msg.filterName+"' query updated as : \""+msg.query+"\"");
 }
 function handleMxWsCatalogs(msg) {
	 if (msg.length!=1) {
		 console.log('ERROR: expected only one catalog from server, got '+msg.length);
		 return;
	 }
	 
	let curCatalogDescr=msg[0];
	MxGuiDetails.handleCatalogDetails(curCatalogDescr);
	MxGuiLeftBar.handleCatalogDetails(curCatalogDescr);
	MxGuiHeader.handleCatalogDetails(curCatalogDescr);
 }
 
 
 function handleMxWsServerGuiMessage(msg) {
	 //dumpStructure(msg);
	 
	 if (msg.msgType=="PROGRESS") { 
		 MxGuiLeftBar.setProgressBar(msg.processingId,msg.pourcentage,msg.text, msg.processingActive);
	 }
	 else if (msg.msgType=="TEXT") {
		 footer_showAlert(msg.level, msg.text, msg.details); }
	 else { footer_showAlert(INFO, "(Unknown msg type : '"+msg.msgType+"')" + msg.text); }
	 
	 
 }
</script>

<!-- Other functions using Mx API -->
<script type="text/javascript" >
function retrieveItemsError(msg) { footer_showAlert(ERROR, msg); }

function retrieveItemsSuccess(itemsAnswerMsg) {
	 
	 let isCatalogWritable = mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights);
	 
	 if (itemsAnswerMsg.totalItems==0) {
		 MxGuiMain.showTextEmptyCatalog(isCatalogWritable);
	 }
	 else { MxGuiMain.hideTextEmptyCatalog();}
	 
	 if (isCatalogWritable) { MxGuiMain.enableFileDropzone(); }
	 else { MxGuiMain.disableFileDropzone(); }
	 
	 MxGuiCards.deselectAll();	 
	 MxGuiLeftBar.setNbMatchingItems(itemsAnswerMsg.totalHits);
	 MxGuiDetails.setNbMatchingItems(itemsAnswerMsg.totalHits);
	 MxGuiDetails.setNbTotalItems(itemsAnswerMsg.totalItems);
	 if (itemsAnswerMsg.fromIdx==0) { MxGuiCards.clearCards(); }
	 
	 for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
		 var item=itemsAnswerMsg.items[idx];
		 MxGuiCards.addNewCard(item);		 
	 }	 
		 
	 // if only one item in the result, open it directly
	 if (itemsAnswerMsg.items.length==1) {
		 MxGuiCards.selectNext();
	 }
	 
}

$(window).scroll(function() {

	   let isScrollBottom=$(window).scrollTop() + $(window).height() == $(document).height();
	   let needMoreResults=MxGuiCards.getNbCards()<MxGuiDetails.getNbMatchingItems();
	   
	   if(isScrollBottom && needMoreResults) {		  	 
	 		 let query = MxGuiHeader.getCurrentSearchQuery();
	 		 let selectedFiltersNames=MxGuiLeftBar.getSelectedFiltersNames();
	 		 let sortString = MxGuiHeader.getCurrentSearchSortString();
	 		 let reversedOrder = MxGuiHeader.getCurrentSearchReversedOrder();
	 		  
		  	 retrieveItemsError=function(msg) { footer_showAlert(ERROR, msg); }
			 _fromIdx=_fromIdx+NB_ITEMS_PER_REQUEST;
			 
			 MxApi.requestCatalogItems({"fromIdx":_fromIdx,
				 						"size":NB_ITEMS_PER_REQUEST,
				 						"query":query,
				 						"filtersNames":selectedFiltersNames,
				 						"sortByFieldName":sortString,
				 						"reverseSortOrder":reversedOrder,
				 						"successCallback":retrieveItemsSuccess,
				 						"errorCallback":retrieveItemsError});
			
	   }
	   
	   
	});

// items search requested explicitly by user
function ws_handlers_requestItemsSearch(query,selectedFiltersNames,sortByFieldName,reversedSortOrder) {
	
	_fromIdx=0;			 
	 MxApi.requestCatalogItems({"fromIdx":_fromIdx,
		 						"size":_size,
		 						"query":query,
		 						"filtersNames":selectedFiltersNames,
		 						"sortByFieldName":sortByFieldName,
		 						"reverseSortOrder":reversedSortOrder,
		 						"successCallback":retrieveItemsSuccess,
		 						"errorCallback":retrieveItemsError});
} 

	
function ws_handlers_requestCreateFilter(filterName, query) {
		
	let successCallback=function() { 
		footer_showAlert(SUCCESS, "Filter '"+filterName+"' created.");
		filterDescr={ "name":filterName,"query":query};
		let newFilter=MxGuiLeftBar.buildNewFilter(filterDescr);
		filtersInsertSpot=MxGuiLeftBar.getFiltersInsertSpot();
		filtersInsertSpot.appendChild(newFilter);
		newFilter.select();
	}	
	let errorCallback=function(errorMsg) { 
		footer_showAlert(ERROR, "Could not create filter '"+filterName+"' : "+errorMsg);
	}
	
	MxApi.requestCreateFilter({ "filterName":filterName,
								"queryString":query,
								"successCallback":successCallback,
								"errorCallback":errorCallback
								});
}

function ws_handlers_requestDeleteFilter(filterName) {
	MxApi.requestDeleteFilter(filterName);
}

function ws_handlers_requestUpdateFilter(filterName,queryString) {
	MxApi.requestUpdateFilter(filterName,queryString);
}

function ws_handlers_requestUploadCsvFile(csvRows,selectedCsvColsDef) {	
	let finishCallback=function(e) {
		//console.log("requesting catalog update");		
		MxApi.requestGetCatalogs({'catalogId':<s:property value="currentCatalog.id"/>, 'successCallback':handleMxWsCatalogs});
	}
	MxApi.requestUploadItemsFromCsv(csvRows,selectedCsvColsDef,finishCallback);    
}

function ws_handlers_requestDownloadCsvFile(selectedTermsList,query,selectedFiltersNames,sortByFieldName,reversedSortOrder) {
	
	 retrieveCsvSuccess=function(itemsAnswerMsg) {
		footer_showAlert(SUCCESS, "<s:text name="Items.downloadItems.csvFileReady" /> : <a target='_blank' href='"+itemsAnswerMsg.csvFileUrl+"' "
				+" title='<s:text name="Items.downloadItems.csvFileReady.title" />'"
				+">"+itemsAnswerMsg.csvFileName+"</a> ("+Math.round(itemsAnswerMsg.csvFileSizeMB*1000)/1000+"MB)","",999999999);						
	 }
	
	 retrieveCsvError=function(msg) { footer_showAlert(ERROR, msg.rejectMessage); }
	 MxApi.requestDownloadItemsCsv({	"termNamesList":selectedTermsList,
		 						"fromIdx":0,
		 						"size":-1,
		 						"query":query,
		 						"filtersNames":selectedFiltersNames,
		 						"sortByFieldName":sortByFieldName,
		 						"reverseSortOrder":reversedSortOrder,
		 						"successCallback":retrieveCsvSuccess,
		 						"errorCallback":retrieveCsvError});	  
}

function ws_handlers_requestDownloadGraphFile(selectedNodesDataTermIdsList,selectedEdgesTermIdsList,query,selectedFiltersNames,sortByFieldName,reversedSortOrder) {
	
	 retrieveCsvSuccess=function(itemsAnswerMsg) {
		footer_showAlert(SUCCESS, "<s:text name="Items.downloadItems.csvFileReady" /> : <a target='_blank' href='"+itemsAnswerMsg.graphFileUrl+"' "
				+" title='<s:text name="Items.downloadItems.csvFileReady.title" />'"
				+">"+itemsAnswerMsg.graphFileName+"</a> ("+Math.round(itemsAnswerMsg.graphFileSizeMB*1000)/1000+"MB)","",999999999);						
	 }
	
	 retrieveCsvError=function(msg) { footer_showAlert(ERROR, msg.rejectMessage); }
	 MxApi.requestDownloadItemsGraph({	
		 						"nodesDataTermIdsList":selectedNodesDataTermIdsList,
		 						"edgesTermIdsList":selectedEdgesTermIdsList,
		 						"fromIdx":0,
		 						"size":-1,
		 						"query":query,
		 						"filtersNames":selectedFiltersNames,
		 						"sortByFieldName":sortByFieldName,
		 						"reverseSortOrder":reversedSortOrder,
		 						"successCallback":retrieveCsvSuccess,
		 						"errorCallback":retrieveCsvError});	  
}




function ws_handlers_deleteItem(itemsId) {
	itemsIdsArray=[];
	itemsIdsArray.push(itemsId);
	MxApi.requestDeleteItems(itemsIdsArray);    
}

function ws_handlers_deleteAllItems() {
	MxApi.requestDeleteItemsByQuery(MxGuiHeader.getCurrentSearchQuery(),
									MxGuiLeftBar.getSelectedFiltersNames()); 
}

function ws_handlers_uploadFiles(catalogDescr,filesToUpload,successCallback,errorCallback) {
	
	MxApi.requestUploadFiles({
		'catalogId':catalogDescr.id,
		'filesToUpload':filesToUpload,
		'successCallback':successCallback,
		'errorCallback':errorCallback
	});
	
}

function ws_handlers_createItem(catalogDescr,fieldsMap,successCallback,errorCallback) {
	
	MxApi.requestCreateItem({
		'catalogId':catalogDescr.id,
		'fieldsMap':fieldsMap,
		'successCallback':successCallback,
		'errorCallback':errorCallback
	});
	
}
</script>
