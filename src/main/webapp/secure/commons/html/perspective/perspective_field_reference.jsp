<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 
 
<!--------------- REFERENCE -------------->
<!--
REFERENCE field is based on classic 'Text' ElasticSearch field type.
It intends to replace 'RELATION' field because less restrictive.
The referencing resolution is performed by the application, but 
then several references can be defined within documents.  
 -->		  
 <script type="text/javascript" >
 
 
//--------------- H E L P E R S ------------------


function _commons_perspective_buildStrQueryGetRefItems(itemIdsArray) {
	let query="";
	for (var i=0;i<itemIdsArray.length;i++) {
		if (query.length>0) { query+=" or "; }
		query+="_id:"+itemIdsArray[i];	
	}
	return query;
}
function  _commons_perspective_getItemDisplayedNameAndRoles(itemName,thisRelRole,itemRelRole) {
	return capWords(thisRelRole)+" / "+capWords(itemRelRole)+": "+itemName;
}
function  _commons_perspective_getUnknownItemDisplay(itemId) {
	let notFoundItem=document.createElement("div");
	notFoundItem.innerHTML=itemId;
	notFoundItem.classList.add("alert-danger");
	notFoundItem.title="Item '"+itemId+"' not reachable";	
	return notFoundItem;
}

function _commons_perspective_buildLinkToItem(itemId,itemName,title) {
	let anchorNode = document.createElement("a");
	
	anchorNode.innerHTML=itemName;
	anchorNode.href="#";
	anchorNode.title=title;
	anchorNode.onclick=function() {
		MxGuiHeader.setCurrentSearchQuery("_id:"+itemId);
		MxGuiHeader.refreshSearch();		
	}
	
	return anchorNode;
}


// need to be async because websocket reply is sent asynchronously
// expect given object to expose a setValue() method
function  _commons_perspective_setValueWithItemFullName(itemId,objectToSet,thisRelRole,itemRelRole) {
	let receivedCurItemCallback=function(itemDesc) {
		// if item details could not be retrieved, display it as a warning
		if (itemDesc==null) { objectToSet.setValue(_commons_perspective_getUnknownItemDisplay(itemId)); }
		else { objectToSet.setValue(_commons_perspective_getItemDisplayedNameAndRoles(itemDesc.name,thisRelRole,itemRelRole)); }
	}
	mx_helpers_getItemDetailsById(itemId,receivedCurItemCallback);	
}

// -------------- READONLY-------------------


// build list of children connected to a given document
// return div containing datafield with refs
function _commons_perspective_buildRefsDocsList(itemId,docIdsListStr,termDesc) {	
	
	let refDocsList=docIdsListStr.split(",");
	let refDocsListEnabledByIdNode = document.createElement("div");
	refDocsListEnabledByIdNode.innerHTML=docIdsListStr;
	
	// populate documents list with all docs found in current value of the field.
	retrieveItemsOptionsSuccess=function(itemsAnswerMsg) {
		//console.log("retrieved "+itemsAnswerMsg.items.length+" children blabla");
		refDocsListEnabledByIdNode.innerHTML="";
		
		let docsListFieldset = document.getElementById("_perspective_field_reference_refsdocs_fieldset_template_").cloneNode(true);
		docsListFieldset.id=itemId+"_"+termDesc.name+"_docslist_readonly";
		docsListFieldset.style.display='block';
		refDocsListEnabledByIdNode.appendChild(docsListFieldset);
		
		// legend
		let legend= docsListFieldset.querySelector("._legend_");
		legend.innerHTML="References : "+itemsAnswerMsg.items.length;
		legend.onclick=function() {
			MxGuiHeader.setCurrentSearchQuery(_commons_perspective_buildStrQueryGetRefItems(refDocsList));
			MxGuiHeader.refreshSearch();		
		}
		
		// refs docs table
		let refsDocsTable=docsListFieldset.querySelector("._refsdocs_table_");
		for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
			 var item=itemsAnswerMsg.items[idx];
			 let newRow=document.getElementById("_perspective_field_reference_refsdocs_fieldset_template_raw_container_").querySelector("._raw_").cloneNode(true);
			 newRow.style.display="table-row";
			
			 // contents
			 let newRowContents=newRow.querySelector("._refdoc_col_");
			 newRowContents.appendChild(_commons_perspective_buildLinkToItem(item.id,item.name,item.id));
			 refsDocsTable.appendChild(newRow);
		}				
	 }
	
	 // cannot build query with JSON object because one of the keys is dynamic
	 // query = get all children having current document as parent
     let queryJsonStr = _commons_perspective_buildStrQueryGetRefItems(refDocsList);
	 
	 retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	 MxApi.requestCatalogItems({"fromIdx":0,
		 						"size":10000,
		 						"query":queryJsonStr,
		 						"successCallback":retrieveItemsOptionsSuccess,
		 						"errorCallback":retrieveItemsOptionsError});
	return refDocsListEnabledByIdNode;
}




function _commons_perspective_build_readonly_field_reference(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,
																				fieldVisuDesc,termDesc,itemId,fieldValue) {
	 let fieldNode=document.getElementById("_commons_perspectives_field_template_reference").cloneNode(true);
	 fieldNode.id="";
	 fieldNode.style.display="block";
	 // title
	 let title = fieldNode.querySelector("._title_");	 
	 
	 title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; 
	 if (fieldVisuDesc.showTitle==false) { title.style.display='none'; }
	 
	 // value
	 let valueNode = fieldNode.querySelector("._value_");
	 
	 if (fieldValue.length>0) {		 		 
		 // list documents listed
		let refsListNode=_commons_perspective_buildRefsDocsList(itemId,fieldValue,termDesc);
		valueNode.appendChild(refsListNode);		 		 
	 }
	 
	 // base text class
	 valueNode.classList.add("mx-perspective-field-text-value");
	 
	// size 
	 let textSizeClass="mx-perspective-field-text-size-"+fieldVisuDesc.size;
	 fieldNode.classList.add(textSizeClass);	 
	 
	 // color 
	 let textColorClass="mx-perspective-field-text-color-"+fieldVisuDesc.color;
	 valueNode.classList.add(textColorClass);	 
	 
	// weight 
	 let textWeightClass="mx-perspective-field-text-weight-"+fieldVisuDesc.weight;
	 valueNode.classList.add(textWeightClass);	 
	 
	 fieldContainerNode.appendChild(fieldNode);
 }


// -------- EDITABLE ---------


//build list of children connected to a given document
//return div containing datafield with refs
function _commons_perspective_buildRefsDocsEditableList(itemId,docIdsListStr,termDesc,onChangeCallBack) {	
		
	let refDocsList=docIdsListStr.split(",");
	let refDocsListEnabledByIdMap=[];
	for (var i=0;i<refDocsList.length;i++) {
		refDocsListEnabledByIdMap[refDocsList[i]]=true;
	}
	let refDocsListEnabledByIdNode = document.createElement("div");
	refDocsListEnabledByIdNode.innerHTML=docIdsListStr;
	
	// populate documents list with all docs matching the query defined 
	// in the "enumsList" field of the term, setting to true the ones
	// found in current value of the field.
	retrieveItemsOptionsSuccess=function(itemsAnswerMsg) {
		//console.log("retrieved "+itemsAnswerMsg.items.length+" children blabla");
		refDocsListEnabledByIdNode.innerHTML="";
		
		let docsListFieldset = document.getElementById("_perspective_field_reference_refsdocs_fieldset_template_").cloneNode(true);
		docsListFieldset.id=itemId+"_"+termDesc.name+"_docslist_edit";
		docsListFieldset.style.display='block';
		refDocsListEnabledByIdNode.appendChild(docsListFieldset);
		
		// legend
		let legend= docsListFieldset.querySelector("._legend_");
		// maybe add here a "search" input field
		legend.innerHTML="Select Elements";
		
		// refs docs table
		let refsDocsTable=docsListFieldset.querySelector("._refsdocs_table_");
		refsDocsTable.refresh=function(searchTxt) {
			clearNodeChildren(refsDocsTable);
			for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
				 let item=itemsAnswerMsg.items[idx];
				 
				 // do not link to itself
				 //if (item.id==itemId) { continue; }
				 
				 // keep only entries matching current 'fast' search
				 if (searchTxt!=null && searchTxt.length>0) { 
					let nameMatch=item.name.toLowerCase().match(stripStr(searchTxt).toLowerCase());
					let idMatch=item.id==stripStr(searchTxt);
					if (nameMatch==null && !idMatch) { continue; }
				 }
									 
				 // add a new row for the new document do be listed
				 let newRow=document.getElementById("_perspective_field_reference_refsdocs_fieldset_template_raw_container_").querySelector("._raw_").cloneNode(true);
				 newRow.style.display="table-row";
				 
				 // selector checkbox
				 let selector=newRow.querySelector("._selector_");
				 selector.style.display="block";
				 let checkbox=selector.querySelector("._selector_checkbox_");
			     if (refDocsListEnabledByIdMap[item.id]==true) {					 
					 checkbox.checked=true;
				 } 	
			     checkbox.onclick=function(event) {
			    	 event.stopPropagation();
					 if (checkbox.checked) { refDocsListEnabledByIdMap[item.id]=true; }
					 else { refDocsListEnabledByIdMap[item.id]=false; } 
					 
					 event.stopPropagation();
					 let newValue="";
					 for (var curItemId in refDocsListEnabledByIdMap) {
						 if (refDocsListEnabledByIdMap[curItemId]==true) {
							 if (newValue.length>0) { newValue+=","; }
							 newValue+=curItemId;
						 }
					 }
					 
					 newRow.classList.remove("editable-bg-transition");
					 newRow.style.background="yellow";
					 
					 let onSuccessCallback=function(fieldname,newvalue) {
						 newRow.classList.add("editable-bg-transition");
						 newRow.style.background="";
					}
					 onChangeCallBack(newValue,onSuccessCallback);
				 }	
				 
				 // contents
				 let newRowContents=newRow.querySelector("._refdoc_col_");
				 newRowContents.appendChild(_commons_perspective_buildLinkToItem(item.id,item.name,item.id));
				 refsDocsTable.appendChild(newRow);
				 		
			}	
		}
		refsDocsTable.refresh();
		
		 // fastsearch
		 let searchinput = docsListFieldset.querySelector("._fastsearch_");
		 searchinput.style.display="block";
		 searchinput.onkeypress=function(event) {
			 //event.preventDefault();
			 event.stopPropagation();			 			 
		 }
		 searchinput.onkeydown=function(event) {
			 //event.preventDefault();
			 event.stopPropagation();			 			 
		 }
		 searchinput.oninput=function(event) {
			 event.preventDefault();
			 event.stopPropagation();			 
			 refsDocsTable.refresh(searchinput.value); 
		 }
		 	 
		 
	 }
	
	// query = get all children having current document as parent
	// if several defined, we merge them with a 'or' operator
  	let queryStr = "";
  	for (var j=0;j<termDesc.enumsList.length;j++) {
  		if (queryStr.length>0) { queryStr+=" or "; }
  		queryStr+=termDesc.enumsList[j];
  	};  	
	 
	 retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	 MxApi.requestCatalogItems({"fromIdx":0,
		 						"size":10000,
		 						"query":queryStr,
		 						"successCallback":retrieveItemsOptionsSuccess,
		 						"errorCallback":retrieveItemsOptionsError});
	return refDocsListEnabledByIdNode;
}


function _commons_perspective_buildEditableReferenceTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,
		fieldVisuDesc,termDesc,itemId,fieldValue,successCallback,onChangeCallback) {
	

	 let fieldNode=document.getElementById("_commons_perspectives_field_template_reference").cloneNode(true);
	 fieldNode.id="";
	 fieldNode.style.display="block";
	 // title
	 let title = fieldNode.querySelector("._title_");	 
	 
	 title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; 
	 if (fieldVisuDesc.showTitle==false) { title.style.display='none'; }
	 
	 // value
	 let valueNode = fieldNode.querySelector("._value_");
	 let onRefsUpdateSuccessCallback=function(fieldname,newvalue) {
		 successCallback(fieldname,newvalue);
		 footer_showAlert(SUCCESS, "List updated for "+fieldname);		 		 
	 }
	 let onRefsUpdateErrorCallback=function(msg) {
		 footer_showAlert(WARNING, "Sorry list could not be updated : "+msg); 
	 }
	 let onRefsListChangeCallBack=function(newValue,guiSuccessCallback) {
		 let successCallbackWithGui=function(fieldname,newvalue) {
			 guiSuccessCallback(fieldname,newvalue);
			 onRefsUpdateSuccessCallback(fieldname,newvalue);
		 }
		 onChangeCallback(itemId,termDesc.name,newValue,successCallbackWithGui,onRefsUpdateErrorCallback);
	 }
	 let refsListNode=_commons_perspective_buildRefsDocsEditableList(itemId,fieldValue,termDesc,onRefsListChangeCallBack);
	 valueNode.appendChild(refsListNode);		 		 
	 
	 
	 // base text class
	 valueNode.classList.add("mx-perspective-field-text-value");
	 
	// size 
	 let textSizeClass="mx-perspective-field-text-size-"+fieldVisuDesc.size;
	 fieldNode.classList.add(textSizeClass);	 
	 
	 // color 
	 let textColorClass="mx-perspective-field-text-color-"+fieldVisuDesc.color;
	 valueNode.classList.add(textColorClass);	 
	 
	// weight 
	 let textWeightClass="mx-perspective-field-text-weight-"+fieldVisuDesc.weight;
	 valueNode.classList.add(textWeightClass);	 
	 
	 fieldContainerNode.appendChild(fieldNode);
	 
	 return fieldNode;
	 
}

</script>

<div style="display:none;" class="mx-perspective-field card mb-4" id="_commons_perspectives_field_template_reference"  >
	<table style="height:100%;width:100%">
	 <tr>
		<td class="mx-perspective-field-title"><span class="_title_"></span></td>
		<td class="_value_"></td>
	</tr></table>               
</div>

<fieldset id="_perspective_field_reference_refsdocs_fieldset_template_"  class="form-control-group card  modals-form-control" 
			style="display:none;max-height:8rem;overflow:auto;">
   <legend class="mx-perspective-field-legend">
   		<table>
   		<tr><td class="_legend_" ></td></tr>   		
   		<tr><td><input style="display:none" type="text" placeholder="Filter ..."  title="Quick Filter Displayed List" class="small _fastsearch_" ></tr>
   		</table>
   	</legend>
   
   <table class="table table-striped" style="margin-bottom:0.2rem">
	   <tbody class="_refsdocs_table_">
	   	
	   </tbody>   
   </table>
   
</fieldset>
<table id="_perspective_field_reference_refsdocs_fieldset_template_raw_container_" style="display:none" >
	<tr class="editable-bg-transition _raw_" >
  		<td style="padding:0;font-size:0.6rem" class="_refdoc_col_"></td>
  		<td class="_selector_" style="display:none;width:2rem;padding:0;margin:0;"><input class="_selector_checkbox_" type="checkbox" ></td>
  	</tr>
</table>

