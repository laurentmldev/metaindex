<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

//
// the page including this file shall declare a node (div) with id=MxGui.cards.insertspot
// marking where to inject new cards
 
var ITEMSVIEW_TABLE_INSERTSPOT_ID = "MxGui.table.insertspot"

var tmpFieldsList=["id","nom","date"];

function itemsView_table_clearItems() {
	var insertSpot = document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID);

	clearNodeChildren(insertSpot);
	
	let headerRow=document.createElement("tr");
	let termsDescList=MxItemsView.getCurrentTermsDesc();
	
	// id
	let headerFieldColTitle=document.createElement("th");
	headerFieldColTitle.classList.add("mx-tableview-col-header");
	headerFieldColTitle.innerHTML="id";
	headerRow.appendChild(headerFieldColTitle);
	
	// fields
	for (var i=0;i<termsDescList.length;i++) {
		let curTermDesc=termsDescList[i];
		let curTermName=mx_helpers_getTermName(curTermDesc, MxGuiDetails.getCurCatalogDescription());		
		let headerFieldColTitle=document.createElement("th");
		headerFieldColTitle.classList.add("mx-tableview-col-header");
		headerFieldColTitle.innerHTML=curTermName;
		headerRow.appendChild(headerFieldColTitle);
	}
	insertSpot.appendChild(headerRow);
} 
function itemsView_table_addNewItem(objDescr,termsDescList) {
	var insertSpot = document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID);
	let newRow=itemsView_buildNewTableEntry(objDescr,termsDescList);
	insertSpot.appendChild(newRow);
	return newRow;
}


// objDescr : shall containing following data :
//	objDescr.id
//	objDescr.name
//	objDescr.thumbnailUrl (optional)
function itemsView_buildNewTableEntry(objDescr,termsDescList) {
	
	var guiId="MxGui.tablerow."+MxItemsView.extractId(objDescr);
	var newItemRow=document.createElement("tr");
	newItemRow.descr=objDescr;
	newItemRow.id=guiId;
	newItemRow.classList.add("mx-tableview-row")
	
	// id
	let newCol=document.createElement("td");		
	newCol.classList.add("mx-tableview-col");
	newCol.innerHTML=objDescr.id;
	newItemRow.appendChild(newCol);
	newCol.name="anchor-"+MxItemsView.extractId(objDescr);
	
	// fields
	for (var idx=0;idx<termsDescList.length;idx++) {
		let newCol=document.createElement("td");
		newCol.classList.add("mx-tableview-col");
		let curTermDesc=termsDescList[idx];				
		newCol.innerHTML=objDescr.data[curTermDesc.name]; 		
		newItemRow.appendChild(newCol);
	}
	
	// "summary" name
	let name=MxItemsView.extractName(objDescr);
	newItemRow.getName=function() { return name.innerHTML; };// useful to get name associated to this item
	
	// onmouseover
	newItemRow.onmouseover = function(e) {
		newItemRow.classList.add('mx-card-lighter-bg');
	}
	newItemRow.onmouseout = function(e) {
		newItemRow.classList.remove('mx-card-lighter-bg');
	}
	
	newItemRow.isSelected=false;	
	newItemRow.select = function(e) {
		newItemRow.isSelected=true;		
		newItemRow.classList.add("mx-card-selected");
		newItemRow.classList.remove("mx-card-darker-bg");
		newItemRow.classList.add("mx-card-lighter-bg");		
		_selectedItemsMapById[MxItemsView.extractId(newItemRow.descr)]=newItemRow;
		_activeItem=newItemRow;				
		MxGuiDetails.populate(newItemRow);		
		MxGuiPerspective.activateLastChosenTab();
		
	}
	newItemRow.deselect = function(e) {
		newItemRow.isSelected=false;
		newItemRow.classList.remove("mx-card-selected");
		newItemRow.classList.add("mx-card-darker-bg");	
		MxGuiDetails.clear();
		_selectedItemsMapById[MxItemsView.extractId(newItemRow.descr)]=null;
		_activeItem=null;
		
		// mark cark as lighter
		newItemRow.classList.add("mx-card-lighter-bg");
		 
	}
	
	newItemRow.onclick = function(e) {

		if (e!=null) {
			e.stopPropagation();
			e.preventDefault();
		}
		if (newItemRow.isSelected) { 
			newItemRow.deselect(e);
			scrollTo(anchor.name);			
		}
		else {
			MxGuiDetails.clear();
			itemsView_deselectAll();
			newItemRow.select(e); 	
			scrollTo("page-top");
		}
	}

	return newItemRow;	
};

function itemsView_table_deselectAll() {
	let tableInsertSpot = document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID); 
	for (var curItemRow=tableInsertSpot.firstChild;curItemRow!==null;curItemRow=curItemRow.nextElementSibling) {		
		if (typeof(curItemRow)!='object') { continue; }
		if (curItemRow.isSelected) { curItemRow.deselect(); }
	}
} 

function itemsView_table_getNbItemsInView() {
	let count=0;
	var tableInsertSpot = document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID); 
	for (var curItemRow=tableInsertSpot.firstChild;curItemRow!==null;curItemRow=curItemRow.nextElementSibling) {		
		if (typeof(curItemRow)!='object') { continue; }
		count++;
	}
	return count;
}


function itemsView_table_selectNext() {
	let nextRow=null;
	if (itemsView_getActiveItem()==null) { 
		nextRow=document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID).getElementsByClassName('mx-tableview-row')[0];
	} else {
		nextRow=itemsView_getActiveItem().nextElementSibling;
		if (nextRow==null) { 
			nextRow=itemsView_getActiveItem().parentNode.getElementsByClassName('mx-tableview-row')[0]; 
		}
	}
	itemsView_deselectAll();
	nextRow.select();
}
function itemsView_table_selectPrevious() {
	if (itemsView_getActiveItem()==null) { return; }
	let prevRow=itemsView_getActiveItem().previousElementSibling;
	if (prevRow==null) { 
		prevRow=itemsView_getActiveItem().parentNode.getElementsByClassName('mx-tableview-row')[itemsView_getActiveItem().parentNode.getElementsByClassName('mx-tableview-row').length-1]; 
	}
	itemsView_deselectAll();
	prevRow.select(); 
}


</script>
   

<!-- ###### -->      
