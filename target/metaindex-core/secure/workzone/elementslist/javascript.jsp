<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script type="text/javascript" >
	// tmp
	// var catalogserverUri="ws://<s:property value="selectedCommunity.catalogServer.serverUri" />/<s:property value="selectedCommunity.catalogServer.port" />";
	var catalogserverUri="ws://localhost:<s:property value="selectedCommunity.catalogServer.port" />";
	console.log("connecting to WS Catalog data "+catalogserverUri);
	// tmp
	//catalogserverUri="ws://localhost:8897";
	catalogContentsContainerId="workzone.catalogcontents.container";
	
	catalogContentsContainerInitialData="<!-- this is a hidden element used for drag and drop visual effect -->"			       
 	 +"<div id=\"dropping_multi_elements_parent\" class=\"dropping_element\" style=\"position:absolute;top:-500px;\" >"
 	+"	<div class=\"dropping_element\" >"
 	+" 		<div id=\"dropping_multi_elements_top\" class=\"dropping_element\" ></div>"
 	+"  </div>"
 	+"</div>";
 	
	var MAX_DISPLAY_ELEMS=80;
	var DISPLAY_ELEMS_TRESHOLD=300;
	var DISPLAY_LOADPERCENT_TRESHOLD=7;
   	var catalogws;
   	var nbelements=0;
   	var catalogSummary=null;
   	var elementsSummary=new Array();
   	var lastDisplayedElementIdx=0;
   	
   	function connectCatalogContentsServer(targeturi) {
   		console.log(targeturi);
   		catalogws = new WebSocket(targeturi);
   		catalogws.onopen = function() {
               	console.log("catalog-server : connected to catalog server");
               	
               }
   		catalogws.onmessage = function(e) {
               	//addChatText("[WebSocket#onmessage] Message: '" + e.data + "'\n");
                catalogcontents_handle_msg(e.data);
               }
   		catalogws.onclose = function() {
               	console.log("Catalogs server connection closed");
               }
      }

   	   // TODO
       // ws.close()
       function closeWS() {
       	catalogws.close();
        }
   	   
   	
   	function catalogcontents_handle_msg(msgStr) {
   		//console.log("Received: "+msgStr);
   		msg=JSON.parse(msgStr);
   		if (msg.msgType == 'catalog-login') { 
   			catalog_send_sessionid(msg);
   			catalog_send_catalogcontentsrequest();
   		}
   		else if (msg.msgType == 'catalog-summary') {
   			//console.log("### Received : "+msgStr);
   			catalogSummary = new CatalogSummary(msg);
   			// TODO?
   			//DISPLAY_ELEMS_TRESHOLD=round(catalogSummary.nbElements/10);
   			
   		}
   		// add elements in the catalog contents list while they are loading via websockets
   		else if (msg.msgType == 'catalog-elementsummary') {
   			nbelements++;
   			el = new ElementSummary(msg);
   			el.index=nbelements;
   			elementsSummary.push(el);
   			
   			sorted_elements_ids.push(el.elementId);
			elementsPosition[el.elementId]=el.index;
			document.getElementById("workzone.catalogcontents.elementsFastSearch").style.display='none';
			document.getElementById("catalogcontents-loadprogress").style.display='block';
	   		// update Html every DISPLAY_ELEMS_TRESHOLD elements, too heavy otherwise
			if (nbelements%DISPLAY_ELEMS_TRESHOLD==0 || nbelements==catalogSummary.nbElements) {
				
				htmlStr="";
   				searchDataHtmlStr="";
   				for (i=lastDisplayedElementIdx;i<nbelements;i++) {
   					curEl=elementsSummary[i];
   					
   					searchDataHtmlStr+=curEl.addInCatalogSearch();
   					// actually update catalog contents only if we did not reach yet max elems treshold   					
   					if (i<MAX_DISPLAY_ELEMS) { htmlStr+=curEl.addInCatalogContents(); }
   					else if (i==MAX_DISPLAY_ELEMS) {
   						htmlStr+=getEndCatalogContentsHtml();
   					}
   				}
   				lastDisplayedElementIdx=nbelements-1;
   				document.getElementById(catalogContentsContainerId).innerHTML+=htmlStr;
			
   				document.getElementById("workzone.catalogcontents.elementsInnerSearch.datalist").innerHTML+=searchDataHtmlStr;
   				
   				if (nbelements==catalogSummary.nbElements) {
   					document.getElementById("catalogcontents-loadprogress").style.display='none';
   			   		document.getElementById("workzone.catalogcontents.elementsFastSearch").style.display='block';
   				}
   			}
   			
   			if (nbelements%DISPLAY_LOADPERCENT_TRESHOLD==0) {
   				updateprogressbar(nbelements,catalogSummary.nbElements)   				
   			}
   			
   				
   		}
        else(console.log('Error: received unhandled chat msg '+msg.msgType))
   	}
   	
   	function updateprogressbar(total, part){
   		var text=Math.round((total*100)/part);
   		document.getElementById("catalogcontents-loadprogress-bar").style.width=text+'%';
   		document.getElementById("catalogcontents-loadprogress-text").innerHTML=text+'%';
   	}

   	function updateCatalogContents(regex) {
   		//console.log("searching for "+regex);
   		document.getElementById("workzone.catalogcontents.elementsFastSearch").style.display='none';
   		document.getElementById("catalogcontents-loadprogress").style.display='block';
   		
   		
   		document.getElementById(catalogContentsContainerId).innerHTML=catalogContentsContainerInitialData;
   		htmlStr="";
   		sorted_elements_ids.clear();
   		elementsPosition.clear();
   		var searchPattern=regex;
   		regexp = new RegExp(searchPattern,"gi");
		
   		for (i=0;i<catalogSummary.nbElements;i++) {
   			if (sorted_elements_ids.length==MAX_DISPLAY_ELEMS) {
   				htmlStr+=getEndCatalogContentsHtml();
   				document.getElementById(catalogContentsContainerId).innerHTML+=htmlStr;
   				document.getElementById("catalogcontents-loadprogress").style.display='none';
   		   		document.getElementById("workzone.catalogcontents.elementsFastSearch").style.display='block';
   				return;
   			} 
   			curEl=elementsSummary[i];
   			//console.log("checking '"+curEl.searchText+"' VS "+regex);
   			if (curEl.searchText.search(regexp)!=-1) {
   				//console.log("	match!");
   				htmlStr+=curEl.addInCatalogContents();
   				sorted_elements_ids.push(this.elementId);
   				elementsPosition[this.elementId]=this.index;
   				
   				if (sorted_elements_ids.length%DISPLAY_ELEMS_TRESHOLD==0) {
   					document.getElementById(catalogContentsContainerId).innerHTML+=htmlStr;
   					htmlStr="";
   				}
   			} 
   			
   			if (i%DISPLAY_LOADPERCENT_TRESHOLD==0) {   				
   				updateprogressbar(nbelements,catalogSummary.nbElements)
	   		}
   			
   		}
   		
   		document.getElementById("catalogcontents-loadprogress").style.display='none';
   		document.getElementById("workzone.catalogcontents.elementsFastSearch").style.display='block';
   		
   		document.getElementById(catalogContentsContainerId).innerHTML+=htmlStr;
   		
   	}
   	
   	function getEndCatalogContentsHtml() {
   		return "<table><tr><td><div>Sorry, too many elements ...</div></td></tr></table>";
   	}
   function getPourcentageElemsHtml(current,total) {
	   	pourcentLoaded= (current / total) * 100;
		return "&nbsp;"+current+"&nbsp;"+"/"+"&nbsp;&nbsp;"+total+"&nbsp;("+pourcentLoaded+" %)";
   }
 	
   	function catalog_send_sessionid(msg) {
   		msg.sessionId="<s:property value='loggedUserProfile.sessionId'/>";
   		catalogws.send(JSON.stringify(msg));   		
   	}
   	

   	function catalog_send_catalogcontentsrequest() {
   		// TODO use a real JSON object
		JSONstr="{ 'msgType':'catalog-getcontents' , 'catalogId' : '<s:property value='loggedUserProfile.selectedCommunity.selectedCatalog.catalogId'/>' }";
		catalogws.send(JSONstr);
   		
   	}
   	
</script>

<script>

//CTRL key pressed for adding a single element to the current selection
var workzone_catalogcontents_multiOn = false;
//SHIFT key pressed for adding a range of elements to the current selection
var workzone_catalogcontents_newMultiRangeOn = false;
//CTRL+SHIFT key pressed for adding a range of elements to the current selection
var workzone_catalogcontents_multiRangeOn = false;

// all ids of selected elements (value==true if selected)
var cur_elements_multiselection = new Array();
cur_elements_multiselection[<s:property value="selectedElement.elementId"/>]=true;
var nbSelectedElems=1;

// the list of all elements IDs, sorted as in the GUI (alaphabetically by title for example)
var sorted_elements_ids = [];

var elementsPosition=new Array();

				

var lastSelectedPos=elementsPosition[<s:property value="selectedCatalog.selectedElement.elementId" />];
if (!lastSelectedPos) {
	lastSelectedPos=1;	
}
// Place the selected element to the middle of the horizontal items scroll zone
location.href="#workzone.catalogcontents.<s:property value="selectedElement.elementId"/>";
var element = document.getElementById("workzone.catalogcontents");
if (element.scrollLeft>0) { element.scrollLeft+=element.offsetWidth/2; }

function getSelectedElementsList()
{	
	var elementsIdsList=new Array();
	for (i=0;i<=sorted_elements_ids.length-1;i++) {
		curElementId=sorted_elements_ids[i];	
		if (cur_elements_multiselection[curElementId]==true) {
			elementsIdsList.push(curElementId);			
		}
	}	
	return elementsIdsList;
}

function clearMultiSelection()
{	
	//  for each element of multi selection : clear it
	for (i=0;i<=sorted_elements_ids.length-1;i++) {
		curElementId=sorted_elements_ids[i];	
		if (cur_elements_multiselection[curElementId]==true) {			
			var element=document.getElementById('workzone.catalogcontents.'+curElementId);
			if (element != null) { element.classList.remove('multiSelectedSlideItem'); }
		}
	}
	cur_elements_multiselection = new Array();
	cur_elements_multiselection[<s:property value="selectedCatalog.selectedElement.elementId"/>]=true;
	lastSelectedPos=elementsPosition[<s:property value="selectedCatalog.selectedElement.elementId" />];
	nbSelectedElems=1;
	hideMultiSelectionCommands();
	
}

// Add or remove given element from the current multi-selection  (invoked when user press SHIFT while clicking)
function switchMultiselection(elementId) {
	if (cur_elements_multiselection[elementId]==true && elementId != <s:property value="selectedCatalog.selectedElement.elementId"/>) { 
		cur_elements_multiselection[elementId]=false
		document.getElementById('workzone.catalogcontents.'+elementId).classList.remove('multiSelectedSlideItem');
		nbSelectedElems--;
	}
	else if (elementId != <s:property value="selectedCatalog.selectedElement.elementId"/>){ 
		cur_elements_multiselection[elementId]=true; 
		document.getElementById('workzone.catalogcontents.'+elementId).classList.add('multiSelectedSlideItem');
		nbSelectedElems++;
	}
	
	// (des)activate multi-selection commands
	if (nbSelectedElems>1) { showMultiSelectionCommands(); }	 	
	else { hideMultiSelectionCommands(); }
}

// Add a range of elements (invoked when user press CTRL+SHIFT while clicking)
function selectRange(firstElementPos, lastElementPos) {
	if (lastElementPos<firstElementPos) {
		var tmp=lastElementPos;
		lastElementPos=firstElementPos;
		firstElementPos=tmp;
	}
	
	for (pos=firstElementPos; pos<=lastElementPos; pos++) {
		
		var curElementId=sorted_elements_ids[pos-1];
		console.log("Multiselecting element "+curElementId);
		if (cur_elements_multiselection[curElementId]!=true) {
			nbSelectedElems++;
			cur_elements_multiselection[curElementId]=true; 
			document.getElementById('workzone.catalogcontents.'+curElementId).classList.add('multiSelectedSlideItem');
		}
	}
	
	// (des)activate multi-selection commands
	if (nbSelectedElems>1) { showMultiSelectionCommands(); }	 	
	else { hideMultiSelectionCommands(); }
}


function showMultiSelectionCommands()
{
	document.getElementById('workzone.catalogcontents.icon.multiselectCmds').style.display='table';
	document.getElementById('workzone.catalogcontents.icon.multiselectCmds.nbSelectedElems').innerHTML=nbSelectedElems;
	
}

function hideMultiSelectionCommands()
{
	document.getElementById('workzone.catalogcontents.icon.multiselectCmds').style.display='none';
	
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
	if (nbSelectedElems==1 && (movedId === 'undefined' || movedId=="")) { movedId=<s:property value="selectedCatalog.selectedElement.elementId" />; }
		
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
