<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

  
  <!-- callbacks required by metaindex API -->
 <script type="text/javascript" >
 
 var NB_ITEMS_PER_REQUEST=100;
 var _fromIdx=0;
 var _size=NB_ITEMS_PER_REQUEST;
 
 // time to display a help msg if items don't get loaded after a while
 var refreshPageMsgTimer=null;
 var refreshPageAlertMsg=null;
 
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
		
		// this timer is cleared if items received in between
		refreshPageMsgTimer = setInterval(function() { 
			clearInterval(refreshPageMsgTimer); 
			refreshPageMsgTimer=null;
			refreshPageAlertMsg=footer_showAlert(INFO, "<s:text name="global.refreshPageIfPageIsStillBlankWithinFewSeconds" />",null,10000);
		}, 5000);
		
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
	 footer_showAlert(WARNING, "<s:text name="Items.filters.deleted" />");
	 filtersInsertSpot=MxGuiHeader.getFiltersInsertSpot();
	 for (var curFilter=filtersInsertSpot.firstChild;curFilter!==null;curFilter=curFilter.nextElementSibling) {		
		if (typeof(curFilter)!='object') { continue; }
		if (curFilter.descr.id==msg.filterId) { 
			curFilter.parentNode.removeChild(curFilter);
			let filterShortcutsContainer=document.getElementById('header-filters-shortcuts-container');
			let filterShortcut=document.getElementById('filter_shortcut_'+msg.filterId);
			if (filterShortcut!=null) {
				filterShortcutsContainer.removeChild(filterShortcut);
			}
			if (curFilter.isSelected) {
				nbFiltersActive--;
				if (nbFiltersActive==0) { 
					let filtersButton=document.getElementById('showFilterDropdownButton');
					let nbActiveFiltersCounter=document.getElementById('tiny_nb_active_filters');
					filtersButton.classList.remove('mx_filters_active');
					nbActiveFiltersCounter.style.display='none';
				}
			}
			break;
		}
	}
 }
 function handleMxWsCatalogContentsChanged(msg)  {	
	
	if (msg.catalogName!="<s:property value='currentCatalog.name'/>") { return; }
 	if (msg.modifType==MxApi.CATALOG_MODIF_TYPE.DOCS_LIST) {
		 if (msg.userNickname!="<s:property value='currentUserProfile.nickname'/>") {
			 footer_showAlert(INFO, 
					  msg.userNickname+" <s:text name="Items.CatalogContentsChangedNotif.part1" /> "
					 		+msg.nbImpactedDocs+" <s:text name="Items.CatalogContentsChangedNotif.part2" />",
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
			 				msg.userNickname+" <s:text name="Items.ItemContentsChangedNotif" /> \""+msg.impactedDocName+"\" : "+msg.impactDetails,
			 				null,
			 				5000);
			 } else {
				 footer_showAlert(INFO, msg.userNickname+" <s:text name="Items.ItemsContentsChangedNotif.part1" /> "
						 			 +msg.nbImpactedDocs+" <s:text name="Items.ItemsContentsChangedNotif.part2" />");
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
	 document.getElementById('filter_shortcut_'+msg.filterId).title=msg.query;
	 footer_showAlert(SUCCESS, "<s:text name="Items.filters.updated" /> : "+msg.filterName+"=\""+msg.query+"\"");
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
	 //console.log("simple progress msg");
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
	
	// clear timer displaying amsg to refresh page if no items displayed
	// we've just received them so everything is ok now
	// no need to display this msg
	if (refreshPageMsgTimer!=null) { 
		clearInterval(refreshPageMsgTimer);
		refreshPageMsgTimer=null;
	}
	if (refreshPageAlertMsg!=null) {
		refreshPageAlertMsg.clear();
		refreshPageAlertMsg=null;
	}
	
	// ensure we have received catalog info before displaying item
	if (MxGuiDetails.getCurCatalogDescription()==null) {
		let timer=setInterval(function() { 
						clearInterval(timer);
						retrieveItemsSuccess(itemsAnswerMsg);
					}, 200);
	}
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


// requesting full value of a long field (truncated by default otherwise)
function ws_handlers_requestItemLongFieldValue(itemId,fieldName,handleFullValueCallback) {
	
	let errorCallback=function(errormsg) { footer_showAlert(ERROR, msg.rejectMessage); }			 
	 MxApi.requestItemFieldFullValue({
		 						"itemId":itemId,
		 						"fieldName":fieldName,
		 						"successCallback":handleFullValueCallback,
		 						"errorCallback":errorCallback});
} 

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

function ws_handlers_requestItemsIdsSearch(query,selectedFiltersNames,sortByFieldName,reversedSortOrder,
			successCallback,errorCallback) {
	
	let fromIdx=0;
	let size=-1;
	 MxApi.requestCatalogItemsIds({"fromIdx":fromIdx,
		 						"size":size,
		 						"query":query,
		 						"filtersNames":selectedFiltersNames,
		 						"sortByFieldName":sortByFieldName,
		 						"reverseSortOrder":reversedSortOrder,
		 						"successCallback":successCallback,
		 						"errorCallback":errorCallback});
} 


	
function ws_handlers_requestCreateFilter(filterName, query) {
		
	let successCallback=function(msgResp) {
		footer_showAlert(SUCCESS, "<s:text name="Items.filters.added" /> : "+filterName);
		filterDescr={ "id":msgResp.filterId,"name":filterName,"query":query};
		let newFilter=MxGuiHeader.buildNewFilter(filterDescr);
		filtersInsertSpot=MxGuiHeader.getFiltersInsertSpot();
		filtersInsertSpot.appendChild(newFilter);
		newFilter.select();
		
		
	}	
	let errorCallback=function(errorMsg) { 
		footer_showAlert(ERROR, "<s:text name="Items.filters.notAdded" /> '"+filterName+"' : "+errorMsg);
	}
	
	MxApi.requestCreateFilter({ "filterName":filterName,
								"queryString":query,
								"successCallback":successCallback,
								"errorCallback":errorCallback
								});
}

function ws_handlers_requestDeleteFilter(filterId) {
	MxApi.requestDeleteFilter(filterId);
}

function ws_handlers_requestUpdateFilter(filterId,queryString) {
	MxApi.requestUpdateFilter(filterId,queryString);
}

function ws_handlers_requestUploadCsvFile(nbEntries,fieldsMapping,fileHandle) {	
	let successCallback=function(e) {
		// reloading catalog description to get potential new
		// fields created from CSV
		MxApi.requestGetCatalogs({'catalogId':<s:property value="currentCatalog.id"/>, 'successCallback':handleMxWsCatalogs});
	}
	let errorCallback=function(msg) { footer_showAlert(ERROR, msg); }
	
	let dataObj={
		'catalogId':<s:property value="currentCatalog.id"/>,
		'filesToUpload':fileHandle.files,
		'totalNbEntries':nbEntries,
		'fieldsMapping':fieldsMapping,
		'successCallback':successCallback,
		'errorCallback':errorCallback,
	}
	MxApi.requestUploadItemsFromCsv(dataObj);    
}

function ws_handlers_itemsUploadBuildNewTermRequest(termName,datatypeStr) {
	return MxApi.buildNewTermValue(termName,datatypeStr);
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
		footer_showAlert(SUCCESS, "<s:text name="Items.downloadItems.gexfFileReady" /> : <a target='_blank' href='"+itemsAnswerMsg.graphFileUrl+"' "
				+" title='<s:text name="Items.downloadItems.gexfFileReady.title" />'"
				+">"+itemsAnswerMsg.graphFileName+"</a> ("+Math.round(itemsAnswerMsg.graphFileSizeMB*1000)/1000+"MB)","",999999999);						
	 }
	
	 retrieveCsvError=function(msg) { footer_showAlert(ERROR, msg); }
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

function ws_handlers_requestDownloadGraphGroupByFile(groupTermId,selectedEdgesTermIdsList,query,selectedFiltersNames,sortByFieldName,reversedSortOrder) {
	
	 retrieveCsvSuccess=function(itemsAnswerMsg) {
		footer_showAlert(SUCCESS, "<s:text name="Items.downloadItems.gexfFileReady" /> : <a target='_blank' href='"+itemsAnswerMsg.graphFileUrl+"' "
				+" title='<s:text name="Items.downloadItems.gexfFileReady.title" />'"
				+">"+itemsAnswerMsg.graphFileName+"</a> ("+Math.round(itemsAnswerMsg.graphFileSizeMB*1000)/1000+"MB)","",999999999);						
	 }
	
	 retrieveCsvError=function(msg) { footer_showAlert(ERROR, msg); }
	 MxApi.requestDownloadItemsGraphGroupBy({	
		 						"groupingTermId":groupTermId,
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
									MxGuiHeader.getSelectedFiltersNames()); 
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
