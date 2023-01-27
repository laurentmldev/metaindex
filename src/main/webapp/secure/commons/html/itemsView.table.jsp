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

var _itemsView_table_fieldsToDisplay=[];
var _itemsView_table_fieldsToDisplayInit=false;
var _itemsView_table_allFieldsStr="";
var _itemsView_table_allFieldsList=[];

function itemsView_table_getItemsInsertSpot() {
	return document.getElementById(ITEMSVIEW_TABLE_INSERTSPOT_ID);	
} 

function itemsView_table_fieldShallBeDisplayed(fieldName) {
	return _itemsView_table_fieldsToDisplay.includes(fieldName);
}
function itemsView_table_getNbFieldsToDisplay(termsDescList) {
	if (_itemsView_table_fieldsToDisplay.length==0) { return termsDescList.length; }
	else { return _itemsView_table_fieldsToDisplay.length; }
}

function itemsView_table_updateColsChoice() {
	let colsChoiceWidget=document.getElementById("itemsView.contextualMenu");
	let colsChoiceItems=colsChoiceWidget.querySelectorAll(".mx-tableview-fieldchoice");
	for (var i=0;i<colsChoiceItems.length;i++) {
		let curChoiceItem=colsChoiceItems[i];
		curChoiceItem.classList.remove("mx-tableview-fieldchoice-selected");
		if (_itemsView_table_fieldsToDisplay.includes(curChoiceItem.termName)) {
			curChoiceItem.classList.add("mx-tableview-fieldchoice-selected");
		}
	}
	MxGuiHeader.refreshSearch();
}

function itemsView_table_updateColsChoiceMenu(termsDescList) {
	
	let colsChoiceWidget=document.getElementById("itemsView.contextualMenu");
	
	let termChoicesInsertSpot=colsChoiceWidget.querySelector(".dropdown-menu");
	clearNodeChildren(termChoicesInsertSpot);
	

	if (_itemsView_table_fieldsToDisplayInit==false) {
		for (var i=0;i<termsDescList.length;i++) {
			let curTerm=termsDescList[i];
			_itemsView_table_fieldsToDisplay.push(curTerm.name);
			_itemsView_table_allFieldsStr=_itemsView_table_allFieldsStr+" "+curTerm.name;
			_itemsView_table_allFieldsList.push(curTerm.name);			
		}
		_itemsView_table_fieldsToDisplayInit=true;		
	}
	
	// add "select All columns" button
	let selectAllColsChoice=document.createElement("a");
	selectAllColsChoice.classList="dropdown-item";
	selectAllColsChoice.classList.add("mx-tableview-fieldgrpchoice");		
	selectAllColsChoice.href="#";
	selectAllColsChoice.innerHTML="<s:text name="Items.table.allColumns"/>";
	selectAllColsChoice.title=_itemsView_table_allFieldsStr;
	selectAllColsChoice.fiedsList=_itemsView_table_allFieldsList;
	
	selectAllColsChoice.onclick=function(e) {
		e.stopPropagation();
		_itemsView_table_fieldsToDisplay=selectAllColsChoice.fiedsList;				
		itemsView_table_updateColsChoice();
	}
	termChoicesInsertSpot.appendChild(selectAllColsChoice);
	
	// add perspectives list
	let perspectives=MxGuiDetails.getCurCatalogDescription().perspectives;
	let perspectivesNames=Object.keys(perspectives);
	
	for (var perspIdx=0;perspIdx<perspectivesNames.length;perspIdx++) {
		let perspFieldsList=[];
		let perspFieldsListStr=""
		let curPerspectiveData=perspectives[perspectivesNames[perspIdx]];
		for (var tabIdx=0;tabIdx<curPerspectiveData.tabs.length;tabIdx++) {
			let curTab=curPerspectiveData.tabs[tabIdx];			
			for (var sectionIdx=0;sectionIdx<curTab.sections.length;sectionIdx++) {
				let curSection=curTab.sections[sectionIdx];
				for (var fieldIdx=0;fieldIdx<curSection.fields.length;fieldIdx++) {
					let curfield=curSection.fields[fieldIdx];
					perspFieldsList.push(curfield.term);
					let curTermDesc=itemsView_getCurrentTermsDesc(curfield.term);
					let curFieldGuiName=mx_helpers_getTermName(curTermDesc, MxGuiDetails.getCurCatalogDescription());
					perspFieldsListStr=perspFieldsListStr+" "+curFieldGuiName;
				}
			}
		}		
		let curChoice=document.createElement("a");
		curChoice.classList="dropdown-item";
		curChoice.classList.add("mx-tableview-fieldgrpchoice");		
		curChoice.href="#";
		curChoice.innerHTML=curPerspectiveData.name;
		curChoice.title=perspFieldsListStr;
		curChoice.fiedsList=perspFieldsList;
		
		curChoice.onclick=function(e) {
			e.stopPropagation();
			_itemsView_table_fieldsToDisplay=curChoice.fiedsList;				
			itemsView_table_updateColsChoice();
		}
		termChoicesInsertSpot.appendChild(curChoice);
	}
	
	
	// add manual columns
	for (var i=0;i<termsDescList.length;i++) {
		let curTerm=termsDescList[i];
		let curChoice=document.createElement("a");
		curChoice.classList="dropdown-item";
		curChoice.classList.add("mx-tableview-fieldchoice");
		if (_itemsView_table_fieldsToDisplay.includes(curTerm.name)) {
			curChoice.classList.add("mx-tableview-fieldchoice-selected");
		}
		curChoice.href="#";
		let curTermGuiName=mx_helpers_getTermName(curTerm, MxGuiDetails.getCurCatalogDescription());
		curChoice.innerHTML=curTermGuiName;
		curChoice.title=curTerm.name;
		curChoice.termName=curTerm.name;
		
		curChoice.onclick=function(e) {
			e.stopPropagation();
			if (_itemsView_table_fieldsToDisplay.includes(curTerm.name)) {				
				_itemsView_table_fieldsToDisplay=_itemsView_table_fieldsToDisplay.filter(function(val) { return val!=curTerm.name} );				
			} else { _itemsView_table_fieldsToDisplay.push(curTerm.name); }
			itemsView_table_updateColsChoice();
		}

		curChoice.ondblclick=function(e) {
			e.stopPropagation();
			_itemsView_table_fieldsToDisplay=[curTerm.name];				
			itemsView_table_updateColsChoice();
		}
		termChoicesInsertSpot.appendChild(curChoice);
	}
	
	
	return colsChoiceWidget;
	
}
function itemsView_table_clearItems() {
	
	
	var insertSpot = itemsView_table_getItemsInsertSpot();
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
		if (!_itemsView_table_fieldsToDisplay.includes(curTermDesc.name)) { continue; }
		let headerFieldColTitle=document.createElement("th");
		headerFieldColTitle.classList.add("mx-tableview-col-header");
		let curTermGuiName=mx_helpers_getTermName(curTermDesc, MxGuiDetails.getCurCatalogDescription());		
		headerFieldColTitle.innerHTML=curTermGuiName;
		headerRow.appendChild(headerFieldColTitle);
	}
	insertSpot.appendChild(headerRow);
	
} 
function itemsView_table_addNewItem(objDescr,termsDescList) {
	var insertSpot = itemsView_table_getItemsInsertSpot();
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
	newItemRow.dbid=MxItemsView.extractId(objDescr);
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
		if (!_itemsView_table_fieldsToDisplay.includes(curTermDesc.name)) { continue; }
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
	newItemRow.getName=function() { return name; };// useful to get name associated to this item
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
		newItemRow.classList.remove("mx-item-visited");
		newItemRow.classList.add("mx-item-selected");
		_activeItem=newItemRow;				
		_lastActiveItem_id=MxItemsView.extractId(objDescr);
		MxGuiDetails.populate(newItemRow);		
		MxGuiPerspective.activateLastChosenTab();
		itemsView_saveSelectionContext();
		
	}
	newItemRow.deselect = function(e) {
		newItemRow.isSelected=false;
		newItemRow.classList.remove("mx-item-selected");
		newItemRow.classList.add("mx-item-visited");
		newColId.classList.add("mx-tableview-visited-id");
		if (_activeItem!=null && _activeItem.dbid==newItemRow.dbid) { 
			MxGuiDetails.clear();
			_activeItem=null;
		}				
	}
	
	newItemRow.onclick = function(e) {

		if (e!=null) {
			e.stopPropagation();
			e.preventDefault();
		}
		if (newItemRow.isSelected) { 
			newItemRow.deselect(e);
			newItemRow.scrollTo();		
			if (_lastActiveItemId==newItemRow.dbid) {
				_lastActiveItemId=null;
			}
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
	let tableInsertSpot = itemsView_table_getItemsInsertSpot(); 
	for (var curItemRow=tableInsertSpot.firstChild;curItemRow!==null;curItemRow=curItemRow.nextElementSibling) {		
		if (typeof(curItemRow)!='object') { continue; }
		if (curItemRow.isSelected) { curItemRow.deselect(); }
	}
} 

function itemsView_table_getNbItemsInView() {
	let count=0;
	var tableInsertSpot = itemsView_table_getItemsInsertSpot(); 
	for (var curItemRow=tableInsertSpot.firstChild;curItemRow!==null;curItemRow=curItemRow.nextElementSibling) {		
		if (typeof(curItemRow)!='object') { continue; }
		count++;
	}
	return count;
}


function itemsView_table_selectNext() {
	let nextRow=null;
	if (itemsView_getActiveItem()==null) { 
		nextRow=itemsView_table_getItemsInsertSpot().getElementsByClassName('mx-tableview-row')[0];
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
