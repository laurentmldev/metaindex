<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="./itemsView.cards.jsp" />
<s:include value="./itemsView.table.jsp" />

<script type="text/javascript">

var _activeItem = null;
var _selectedItemsMapById = [];
var _itemsViewMode="cards"; // cards|table 

function itemsView_setViewMode(mode) {
	if (mode=="cards")  { _itemsViewMode="cards" }
	else if (mode=="table")  { _itemsViewMode="table" }
	else {
		console.log("unhandle items view mode '"+mode+"'. Authorized modes are table|cards. ")
		return;
	}
}



function itemsView_clearItems() {
	if (_itemsViewMode=="cards") { return itemsView_cards_clearItems(); }
	else if (_itemsViewMode=="table") { return itemsView_table_clearItems(); }
	_activeItem = null;
	_selectedItemsMapById = [];
} 
function itemsView_addNewItem(objDescr) {
	if (_itemsViewMode=="cards") { return itemsView_cards_addNewItem(objDescr); }
	else if (_itemsViewMode=="table") { return itemsView_table_addNewItem(objDescr); }
}

function itemsView_deselectAll() {
	if (_itemsViewMode=="cards") { return itemsView_cards_deselectAll(); }
} 

function itemsView_getNbItemsInView() {
	if (_itemsViewMode=="cards") { return itemsView_cards_getNbItemsInView(); }
}


function itemsView_selectNext() {
	if (_itemsViewMode=="cards") { return itemsView_cards_selectNext(); }
}
function itemsView_selectPrevious() {
	if (_itemsViewMode=="cards") { return itemsView_cards_selectPrevious(); }
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

//Public Interface
var MxItemsView={}
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

</script>