<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>




<script>

//CTRL key pressed for adding a single element to the current selection
var workzone_thumbnails_multiOn = false;
//SHIFT key pressed for adding a range of elements to the current selection
var workzone_thumbnails_newMultiRangeOn = false;
//CTRL+SHIFT key pressed for adding a range of elements to the current selection
var workzone_thumbnails_multiRangeOn = false;

// all ids of selected elements (value==true if selected)
var cur_elements_multiselection = new Array();
var nbSelectedElems=1;
			
var lastSelectedPos=1;
if (curElementData!=null) { lastSelectedPos=elementsPosition[curElementData.elementId]; }


// Place the selected element to the middle of the horizontal items scroll zone
if (curElementData!=null) { location.href="#workzone.thumbnails."+curElementData.elementId; }
var element = document.getElementById("workzone.thumbnails");
if (element.scrollLeft>0) { element.scrollLeft+=element.offsetWidth/2; }


function changeToNextElement() {
	curElPos=elementsPosition[curElementData.elementId];
	nextElPos=curElPos+1;	
	if (nextElPos>displayedThumbnailsIds.length) { nextElPos=1; }
	// array index starts from 0
	nextElId=displayedThumbnailsIds[nextElPos-1];
	changeElement(nextElId);
}
function changeToPrevElement() {
	curElPos=elementsPosition[curElementData.elementId];
	nextElPos=curElPos-1;
	if (nextElPos==0) { nextElPos=displayedThumbnailsIds.length; }
	// array index starts from 0
	nextElId=displayedThumbnailsIds[nextElPos-1];
	changeElement(nextElId);
}

function getSelectedElementsList()
{	
	var elementsIdsList=new Array();
	for (i=0;i<=filteredThumbnailsIds.length-1;i++) {
		curElementId=filteredThumbnailsIds[i];	
		if (cur_elements_multiselection[curElementId]==true) {
			elementsIdsList.push(curElementId);			
		}
	}	
	return elementsIdsList;
}

function clearMultiSelection()
{	
	//  for each element of multi selection : clear it
	for (i=0;i<=filteredThumbnailsIds.length-1;i++) {
		curElementId=filteredThumbnailsIds[i];	
		if (cur_elements_multiselection[curElementId]==true) {			
			var element=document.getElementById('workzone.thumbnails.'+curElementId);
			if (element != null) { element.classList.remove('multiSelectedSlideItem'); }
		}
	}
	cur_elements_multiselection = new Array();
	
	if (curElementData!=null) {  
		lastSelectedPos=elementsPosition[curElementData.elementId];
	}
	
	nbSelectedElems=1;
	hideMultiSelectionCommands();
	
}

// Add or remove given element from the current multi-selection  (invoked when user press CTRL while clicking)
function switchMultiselection(elementId) {
			
	if (cur_elements_multiselection[elementId]==true && (curElementData==null || elementId != curElementData.elementId)) { 
		cur_elements_multiselection[elementId]=false
		document.getElementById('workzone.thumbnails.'+elementId).classList.remove('multiSelectedSlideItem');
		nbSelectedElems--;
	}
	else if (curElementData==null || elementId != curElementData.elementId){ 
		cur_elements_multiselection[elementId]=true; 
		document.getElementById('workzone.thumbnails.'+elementId).classList.add('multiSelectedSlideItem');
		nbSelectedElems++;
	}
	
	// (des)activate multi-selection commands
	if (nbSelectedElems>1) { showMultiSelectionCommands(); }	 	
	else { hideMultiSelectionCommands(); }
}

// Add a range of elements (invoked when user press CTRL+SHIFT while clicking)
function selectRange(firstElementPos, lastElementPos) {
	
	
	console.log("select range "+firstElementPos+".."+lastElementPos);
	if (lastElementPos<firstElementPos) {
		var tmp=lastElementPos;
		lastElementPos=firstElementPos;
		firstElementPos=tmp;
	}
	
	for (pos=firstElementPos; pos<=lastElementPos; pos++) {
		
		var curElementId=filteredThumbnailsIds[pos-1];
		console.log("Multiselecting element "+curElementId);
		if (cur_elements_multiselection[curElementId]!=true) {
			if (curElementId!=curElementData.elementId) { nbSelectedElems++; }
			console.log("	nbSelectedElems "+nbSelectedElems);
			cur_elements_multiselection[curElementId]=true; 
			document.getElementById('workzone.thumbnails.'+curElementId).classList.add('multiSelectedSlideItem');
		}
	}
	
	// (des)activate multi-selection commands
	if (nbSelectedElems>1) { showMultiSelectionCommands(); }	 	
	else { hideMultiSelectionCommands(); }
}


function showMultiSelectionCommands()
{
	document.getElementById('workzone.thumbnails.icon.multiselectCmds').style.display='table';
	document.getElementById('workzone.thumbnails.icon.multiselectCmds.nbSelectedElems').innerHTML=nbSelectedElems;
	
}

function hideMultiSelectionCommands()
{
	document.getElementById('workzone.thumbnails.icon.multiselectCmds').style.display='none';
	
}

// Add a range of elements (invoked when user press SHIFT while clicking)
function selectNewRange(firstElementPos, lastElementPos) {
	clearMultiSelection(); 	
	selectRange(firstElementPos, lastElementPos);
}

//--------------- Multi select actions

function removeMultiSelectedStaticElements(catalogId, catalogName)
{
	 document.getElementById('workzone.removeStaticElements.catalogName').innerHTML=catalogName;
	 document.getElementById('workzone.removeStaticElements.nbElems').innerHTML=nbSelectedElems;
	 document.getElementById('workzone.removeStaticElements.form.catalogId').value=catalogId;	 
	 document.getElementById('workzone.removeStaticElements.form.elementsIds').value=getSelectedElementsList();	 
	 document.getElementById('workzone.removeStaticElements.form.modal').style.display='table';
}

function deleteMultiSelectedElements()
{
	 document.getElementById('workzone.deleteElements.nbElems').innerHTML=nbSelectedElems;
	 document.getElementById('workzone.deleteElements.form.elementsIds').value=getSelectedElementsList();	 
	 document.getElementById('workzone.deleteElements.form.modal').style.display='table';
}

function deleteAllElements()
{
	 document.getElementById('workzone.deleteElements.nbElems').innerHTML="<s:text name="workzone.all" />";
	 document.getElementById('workzone.deleteElements.form.elementsIds').value="-1";	 
	 document.getElementById('workzone.deleteElements.form.modal').style.display='table';
}
//--------------- Drag and Drop Elements into Catalogs (for static add) ----------------


// Drag and Drop Element
function handleDragStartTest(elementId,elementName,item,event) {

    event.dataTransfer.setData('movedElementsIds',cur_elements_multiselection);
}

// Drag and Drop Element
function handleDragStartElement(elementId,elementName,item,event) {

   // draggingCatalogElement=true;
    event.dataTransfer.effectAllowed = 'move';

   // alert("elementId="+elementId+" : "+cur_elements_multiselection[elementId]);
    // if multiselection
    if (nbSelectedElems>1 && cur_elements_multiselection[elementId]==true) { 	
	    event.dataTransfer.setData('movedElementsIds',cur_elements_multiselection);	    
	    document.getElementById('dropping_multi_elements_top').innerHTML=nbSelectedElems; 
	    var crt = document.getElementById('dropping_multi_elements_parent').cloneNode(true);	    
	    crt.classList.add("dropping_multi_elements");
	    document.body.appendChild(crt);
	    event.dataTransfer.setDragImage(crt, -25, 5);
    }
    else {
    	event.dataTransfer.setData('movedElementId',elementId);
    	event.dataTransfer.setData('movedElementName',elementName);
	    var crt = item.cloneNode(true);
	    crt.classList.add("dropping_element");
	    crt.style.position = "absolute"; crt.style.top = "-500px"; 
	    crt.innerHTML=item.innerHTML;
	    document.body.appendChild(crt);
	    event.dataTransfer.setDragImage(crt, -25, 5);
    	
    }
}

function handleDragEndElement(item,event) {	
	// Do nothing special
}


function handleDragOverCatalog(item,event) {
	
	// Allows us to drop.
    if (event.preventDefault) { event.preventDefault(); }
    event.dataTransfer.dropEffect = 'move';
    item.classList.add("dropzone_catalog");
	
    return false;
  
}

function handleDragEnterCatalog(item,event) {
	item.classList.add("dropzone_catalog");

}

function handleDragLeaveCatalog(item,event) {
	item.classList.remove("dropzone_catalog");

}

function handleDropCatalog(catalogId,catalogName,item,event) {
	//alert('plop');
	
	
	var movedIds=event.dataTransfer.getData('movedElementsIds');
	var movedId=event.dataTransfer.getData('movedElementId');
	var movedName=event.dataTransfer.getData('movedElementName');
	if (curElementData!=null) {
		if (nbSelectedElems==1 && (movedId === 'undefined' || movedId=="")) { movedId=curElementData.elementId; }
	}
		
	if (movedId === 'undefined' || movedId=="") {
		document.getElementById('workzone.addStaticElementsToCatalog.form.elementsIds').value=getSelectedElementsList();		
		document.getElementById('workzone.addStaticElementsToCatalog.form.catalogId').value=catalogId;
		document.getElementById('workzone.addStaticElementsToCatalog.catalogName').innerHTML=catalogName;
		document.getElementById('workzone.addStaticElementsToCatalog.nbElements').innerHTML=nbSelectedElems;
		document.getElementById('workzone.addStaticElementsToCatalog.form.modal').style.display='table';
		
	}
	else
	{
		document.getElementById('workzone.addStaticElementToCatalog.elementName').innerHTML=movedName;
		document.getElementById('workzone.addStaticElementToCatalog.form.elementId').value=movedId;		
		document.getElementById('workzone.addStaticElementToCatalog.form.catalogId').value=catalogId;
		document.getElementById('workzone.addStaticElementToCatalog.catalogName').innerHTML=catalogName;
		document.getElementById('workzone.addStaticElementToCatalog.form.modal').style.display='table';
		
	}
	
	
    return false;
}

</script>
