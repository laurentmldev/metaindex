<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<s:include value="/public/commons/js/resizable-table.jsp" /> 

         
<script type="text/javascript">

//
// the page including this file shall declare a node (div) with id=MxGui.cards.insertspot
// marking where to inject new cards
 
var ITEMSVIEW_TABLE_INSERTSPOT_ID = "MxGui.table.insertspot";
var ITEMSVIEW_TABLE_AREA_ID = "MxGui.table.area";

var _fieldsToDisplay=[];
var _fieldsToDisplayInit=false;

function itemsView_table_fieldShallBeDisplayed(fieldName) {
	return _fieldsToDisplay.includes(fieldName);
}
function itemsView_table_getNbFieldsToDisplay(termsDescList) {
	if (_fieldsToDisplay.length==0) { return termsDescList.length; }
	else { return _fieldsToDisplay.length; }
}

function itemsView_table_updateColsChoiceMenu(termsDescList) {
	
	let colsChoiceWidget=document.getElementById("itemsView.contextualMenu");
	
	let termChoicesInsertSpot=colsChoiceWidget.querySelector(".dropdown-menu");
	clearNodeChildren(termChoicesInsertSpot);
	
	if (_fieldsToDisplayInit==false) {
		for (var i=0;i<termsDescList.length;i++) {
			let curTerm=termsDescList[i];
			_fieldsToDisplay.push(curTerm.name);
			_fieldsToDisplayInit=true;
		}
	}
	
	// add perspectives list
	let perspectives=MxGuiDetails.getCurCatalogDescription().perspectives;
	let perspectivesNames=Object.keys(perspectives);	
	for (var i=0;i<perspectivesNames.length;i++) {
		fieldsList=[];
		let curPerspectiveData=perspectives[perspectivesNames[i]];
		for (var tabIdx=0;tabIdx<curPerspectiveData.tabs;tabIdx++) {
			let curTab=curPerspectiveData.tabs[tabIdx];
			for (var sectionIdx=0;sectionIdx<curTab.sections;sectionIdx++) {
				let curSection=curTab.sections[sectionIdx];
				for (var fieldIdx=0;fieldIdx<curSection.fields;fieldIdx++) {
					let curfield=curSection.fields[fieldIdx];
					fieldsList.push(curfield.term);
				}
			}
		}
		console.log(fieldsList);
			
	}
	
	
	// add manual columns
	for (var i=0;i<termsDescList.length;i++) {
		let curTerm=termsDescList[i];
		let curChoice=document.createElement("a");
		curChoice.classList="dropdown-item";
		curChoice.classList.add("mx-tableview-fieldchoice");
		if (_fieldsToDisplay.includes(curTerm.name)) {
			curChoice.classList.add("mx-tableview-fieldchoice-selected");
		}
		curChoice.href="#";
		let curTermGuiName=mx_helpers_getTermName(curTerm, MxGuiDetails.getCurCatalogDescription());
		curChoice.innerHTML=curTermGuiName;
		curChoice.title=curTerm.name;
		
		curChoice.onclick=function(e) {
			e.stopPropagation();
			if (_fieldsToDisplay.includes(curTerm.name)) {				
				_fieldsToDisplay=_fieldsToDisplay.filter(function(val) { return val!=curTerm.name} );
				curChoice.classList.remove("mx-tableview-fieldchoice-selected");								
			} else {
				_fieldsToDisplay.push(curTerm.name);
				curChoice.classList.add("mx-tableview-fieldchoice-selected");
			}
			MxGuiHeader.refreshSearch();
		}
		termChoicesInsertSpot.appendChild(curChoice);
	}
	
	
	return colsChoiceWidget;
	
}
function itemsView_table_clearItems() {
	
	
	var insertSpot = document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID);
	let termsDescList=MxItemsView.getCurrentTermsDesc();
	itemsView_table_updateColsChoiceMenu(termsDescList);
	clearNodeChildren(insertSpot);
	
	let headerRow=document.createElement("tr");
	headerRow.style.position='sticky';
	headerRow.style.top=0;
	
	// id
	let headerFieldColTitle=document.createElement("th");
	headerFieldColTitle.classList.add("mx-tableview-col-header");
	headerFieldColTitle.innerHTML="id";
	headerRow.appendChild(headerFieldColTitle);
	
	// fields
	for (var i=0;i<termsDescList.length;i++) {
		let curTermDesc=termsDescList[i];		
		if (!_fieldsToDisplay.includes(curTermDesc.name)) { continue; }
		let headerFieldColTitle=document.createElement("th");
		headerFieldColTitle.classList.add("mx-tableview-col-header");
		let curTermGuiName=mx_helpers_getTermName(curTermDesc, MxGuiDetails.getCurCatalogDescription());		
		headerFieldColTitle.innerHTML=curTermGuiName;
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


function itemsView_extractColumnContents(colNode,termDesc,fieldValue,itemId) {
	let fieldVisuDesc={
			"showTitle":false,
			"size":"small",
			"color":"normal",
			"weight":"normal"
	}
	let catalogDesc=MxGuiDetails.getCurCatalogDescription();
	
	if (termDesc.datatype=="IMAGE_URL") {
		MxGuiPerspective.buildImgUrl_RO(catalogDesc,colNode,fieldVisuDesc,termDesc,fieldValue);
		
	} else if (termDesc.datatype=="PAGE_URL" 
 			|| termDesc.datatype=="AUDIO_URL"
 			|| termDesc.datatype=="VIDEO_URL") {
		
 		MxGuiPerspective.buildUrl_RO(catalogDesc,colNode,fieldVisuDesc,termDesc,fieldValue);
 	}
 	else if (termDesc.datatype=="LINK") {
 		let showHeader=false;
 		MxGuiPerspective.buildLink_RO(catalogDesc,colNode,fieldVisuDesc,termDesc,itemId,fieldValue,showHeader);
 	}
 	else if (termDesc.datatype=="LONG_TEXT") {
 		MxGuiPerspective.buildLongText_RO(itemId,catalogDesc,colNode,fieldVisuDesc,termDesc,fieldValue,ws_handlers_requestItemLongFieldValue);
 	}
 	else { 		
 		MxGuiPerspective.buildTinyText_RO(catalogDesc,colNode,fieldVisuDesc,termDesc,fieldValue); 	
	}
	
}


function itemsView_table_updateFieldValue(itemId,fieldName,newValue,fieldTermDesc) {
	let guiId="MxGui.tablerow."+itemId+"."+fieldName;
	let valueCell=document.getElementById(guiId);
	if (valueCell==null) { return; }
	valueCell.innerHTML="";
	itemsView_extractColumnContents(valueCell,fieldTermDesc,newValue,itemId);
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
	let newColId=document.createElement("td");		
	newColId.classList.add("mx-tableview-col");
	newColId.innerHTML=objDescr.id;
	newItemRow.appendChild(newColId);	
	
	// fields
	for (var idx=0;idx<termsDescList.length;idx++) {
		let newCol=document.createElement("td");
		newCol.classList.add("mx-tableview-col");
		let curTermDesc=termsDescList[idx];
		if (!_fieldsToDisplay.includes(curTermDesc.name)) { continue; }
		newCol.id=guiId+"."+curTermDesc.name;	
		let value = objDescr.data[curTermDesc.name];
		if (value == null) { 
			newCol.innerHTML="";
			newCol.classList.add("mx-tableview-col-undef");
		} else { 
			let fieldValue=objDescr.data[curTermDesc.name];
			itemsView_extractColumnContents(newCol,curTermDesc,fieldValue,objDescr.id);
		}
				
		newItemRow.appendChild(newCol);
	}
	
	// "summary" name
	let name=MxItemsView.extractName(objDescr);
	newItemRow.getName=function() { return name.innerHTML; };// useful to get name associated to this item
	newItemRow.title=name;
	
	// onmouseover
	newItemRow.onmouseover = function(e) {
		newItemRow.classList.add('mx-item-hover');
	}
	newItemRow.onmouseout = function(e) {
		newItemRow.classList.remove('mx-item-hover');
	}
	
	newItemRow.isSelected=false;	
	newItemRow.select = function(e) {
		newItemRow.isSelected=true;		
		newItemRow.classList.add("mx-item-selected");
		newItemRow.classList.add("mx-item-visited");
		newColId.classList.add("mx-tableview-visited-id");
		
		_selectedItemsMapById[MxItemsView.extractId(newItemRow.descr)]=newItemRow;
		_activeItem=newItemRow;				
		MxGuiDetails.populate(newItemRow);		
		MxGuiPerspective.activateLastChosenTab();
		
	}
	newItemRow.deselect = function(e) {
		newItemRow.isSelected=false;
		newItemRow.classList.remove("mx-item-selected");	
		MxGuiDetails.clear();
		_selectedItemsMapById[MxItemsView.extractId(newItemRow.descr)]=null;
		_activeItem=null;
				 
	}
	
	newItemRow.onclick = function(e) {

		if (e!=null) {
			e.stopPropagation();
			e.preventDefault();
		}
		if (newItemRow.isSelected) { 
			newItemRow.deselect(e);
			newItemRow.scrollTo();			
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
