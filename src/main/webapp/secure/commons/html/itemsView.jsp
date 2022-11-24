<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="./itemsView.cards.jsp" />
<s:include value="./itemsView.table.jsp" />

<script type="text/javascript">

var _activeItem = null;
var _selectedItemsMapById = [];
var _itemsViewMode="cards"; // cards|table 

function itemsView_toggleViewMode(ev,mode) {
	if (mode!=null) { _itemsViewMode=mode; }
	
	if (_itemsViewMode=="cards") { 
		_itemsViewMode="table";
		document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID).style.display='';
		document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID).style.display='none';
		
	}
	else if (_itemsViewMode=="table") { 
		_itemsViewMode="cards";
		document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID).style.display='none';
		document.getElementById(ITEMSVIEW_CARDS_INSERTSPOT_ID).style.display='';
	}
	else {
		console.log("unhandle items view mode '"+mode+"'. Authorized modes are table|cards. ")
		return;
	}
	MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());
}
function itemsView_setViewMode(mode) {
	if (mode=="cards")  { _itemsViewMode="cards" }
	else if (mode=="table")  { _itemsViewMode="table" }
	else {
		console.log("unhandle items view mode '"+mode+"'. Authorized modes are table|cards. ")
		return;
	}
}

function itemsView_getCurrentTermsDesc() {
	let termsDescList=[];
	let catalogDesc=MxGuiDetails.getCurCatalogDescription();
	if (catalogDesc==null) { return termsDescList; }
	
	let sortedTermsNames = Object.keys(catalogDesc.terms).sort();		
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		termName=sortedTermsNames[termIdx];
		let termDesc = MxGuiDetails.getCurCatalogDescription().terms[termName];
		termsDescList.push(termDesc);
	}
	
	return termsDescList;	
}


function itemsView_clearItems() {
	if (_itemsViewMode=="cards") { return itemsView_cards_clearItems(); }
	else if (_itemsViewMode=="table") { return itemsView_table_clearItems(); }
	_activeItem = null;
	_selectedItemsMapById = [];
} 
function itemsView_addNewItem(objDescr) {
	let termsDescList=MxItemsView.getCurrentTermsDesc();	 	 
	if (_itemsViewMode=="cards") { return itemsView_cards_addNewItem(objDescr); }
	else if (_itemsViewMode=="table") { return itemsView_table_addNewItem(objDescr,termsDescList); }
}

function itemsView_deselectAll() {
	if (_itemsViewMode=="cards") { return itemsView_cards_deselectAll(); }
	else if (_itemsViewMode=="table") { return itemsView_table_deselectAll(); }
} 

function itemsView_getNbItemsInView() {
	if (_itemsViewMode=="cards") { return itemsView_cards_getNbItemsInView(); }
	else if (_itemsViewMode=="table") { return itemsView_table_getNbItemsInView(); }
}


function itemsView_selectNext() {
	if (_itemsViewMode=="cards") { return itemsView_cards_selectNext(); }
	else if (_itemsViewMode=="table") { return itemsView_table_selectNext(); }
}
function itemsView_selectPrevious() {
	if (_itemsViewMode=="cards") { return itemsView_cards_selectPrevious(); }
	else if (_itemsViewMode=="table") { return itemsView_table_selectPrevious(); }
}



function itemsView_extractId(objDescr) { return objDescr.id; }
function itemsView_extractName(objDescr) { return objDescr.name; }
function itemsView_extractThumbnailUrl(objDescr)  { 
	let urlStr=objDescr.thumbnailUrl;
	if (objDescr.itemsUrlPrefix!=null && objDescr.itemsUrlPrefix!="" && !urlStr.startsWith('http')) {
		urlStr=objDescr.itemsUrlPrefix+"/"+urlStr;
	}
	return urlStr; 
}

function itemsView_getActiveItem() { return _activeItem; }


function itemsView_enableAutoFeedOnScrollDown() {
	let itemsInsertSpots=document.querySelectorAll("._itemsView_autofeed_");
	
	for (var i=0;i<itemsInsertSpots.length;i++) {
	  let insertSpot=itemsInsertSpots[i];
	  
	  insertSpot.onscroll=function() {
		  
		  //console.log(insertSpot);
		   //console.log("height="+insertSpot.height+" pos="+insertSpot.scrollTop);
		   
		   let isScrollBottom=insertSpot.scrollTopMax - insertSpot.scrollTop < 1;
	  	   let needMoreResults=MxItemsView.getNbItemsInView()<MxGuiDetails.getNbMatchingItems();
	  	   	   
	  	   if(isScrollBottom && needMoreResults) {		  	 
	  	 		 let query = MxGuiHeader.getCurrentSearchQuery();
	  	 		 let selectedFiltersNames=MxGuiHeader.getSelectedFiltersNames();
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
	  				 						"successCallback":retrieveItemsSuccess, // from ws_handlers.jsp
	  				 						"errorCallback":retrieveItemsError});
	  			
	  	   }
	  	   
	  	   
	  	};
	}
}

function itemsView_updateFieldValue(itemId,fieldName,newValue) {
	// find corresponding termDesc
	let fieldTermsDesc=null;
	let termsDescList=MxItemsView.getCurrentTermsDesc();
	for (var i=0;i<termsDescList.length;i++) {
		let curTermDesc=termsDescList[i];
		if (curTermDesc.name==fieldName) {
			fieldTermsDesc=curTermDesc;
			break;
		}
	}
	
	if (_itemsViewMode=="cards") { return itemsView_cards_updateFieldValue(itemId,fieldName,newValue,fieldTermsDesc); }
	else if (_itemsViewMode=="table") { return itemsView_table_updateFieldValue(itemId,fieldName,newValue,fieldTermsDesc); }	
}

//Public Interface
var MxItemsView={}
MxItemsView.toggleViewMode=itemsView_toggleViewMode;
MxItemsView.getCurrentTermsDesc=itemsView_getCurrentTermsDesc;
MxItemsView.setViewMode=itemsView_setViewMode;
MxItemsView.deselectAll=itemsView_deselectAll;
//expect structure with at least fields 'id','name','thumbnailUrl', 
MxItemsView.addNewItem=itemsView_addNewItem;
MxItemsView.clearItems=itemsView_clearItems;
MxItemsView.getActiveItem=itemsView_getActiveItem;
MxItemsView.selectNext=itemsView_selectNext;
MxItemsView.selectPrevious=itemsView_selectPrevious;
MxItemsView.extractName=itemsView_extractName;
MxItemsView.extractId=itemsView_extractId;
MxItemsView.getNbItemsInView=itemsView_getNbItemsInView;
MxItemsView.enableAutoFeedOnScrollDown=itemsView_enableAutoFeedOnScrollDown;
MxItemsView.updateFieldValue=itemsView_updateFieldValue;

</script>