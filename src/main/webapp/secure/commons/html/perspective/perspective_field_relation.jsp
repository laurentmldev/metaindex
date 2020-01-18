<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 
 
<!--------------- RELATION -------------->
<!--
RELATION field is based on 'join' ElasticSearch field type, offering a parent/child relational link.
Limitations are only one single such relation per document.  
 -->		  
 <script type="text/javascript" >
 
 
//--------------- H E L P E R S ------------------

 MX_RELATIONS_FIELD_NAME="mx_relations";


function _commons_perspective_buildStrQueryGetChildItems(parentItemId,parentRoleName) {
	return '{ "bool" : { "must" : [ { "has_parent" : { '
 		+'"parent_type" : "'+parentRoleName+'"'
 		+',"query" : { "match" : { "_id" : "'+parentItemId+'" } }'
 		+' } } ] } }';
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

// build list of children connected to a given parent document
function _commons_perspective_buildChildrenList(itemId,fieldValue,termDesc) {	
	
	let childrenListNode = document.createElement("div");
	childrenListNode.innerHTML=capWords(mx_helpers_getTermParentRelationName(termDesc));
	
	// populate children list 
	retrieveItemsOptionsSuccess=function(itemsAnswerMsg) {
		//console.log("retrieved "+itemsAnswerMsg.items.length+" children blabla");
		childrenListNode.innerHTML="";
		
		
		let childrenDetailedListPopup = document.getElementById("_perspective_field_relation_children_list_popup_template_").cloneNode(true);
		childrenDetailedListPopup.id=itemId+"_"+termDesc.name+"_popup";
		childrenDetailedListPopup.style.display='block';
		childrenListNode.appendChild(childrenDetailedListPopup);
		
		// legend
		let legend= childrenDetailedListPopup.querySelector("._legend_");
		legend.innerHTML=capWords(mx_helpers_getTermChildRelationName(termDesc))+": "+itemsAnswerMsg.items.length;
		legend.onclick=function() {
			MxGuiHeader.setCurrentSearchQuery(_commons_perspective_buildStrQueryGetChildItems(itemId,
															mx_helpers_getTermParentRelationName(termDesc)));
			MxGuiHeader.refreshSearch();		
		}
		
		// children tbale
		let childrenTable=childrenDetailedListPopup.querySelector("._children_table_");
		for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
			 var item=itemsAnswerMsg.items[idx];
			 let newRow=childrenDetailedListPopup.querySelector("._child_row_").cloneNode(true);
			 newRow.classList.remove("_child_row_");
			 newRow.style.display="table-row";
			 let newRowContents=newRow.querySelector("._child_col_");
			 newRowContents.appendChild(_commons_perspective_buildLinkToItem(item.id,item.name,item.id));
			 childrenTable.appendChild(newRow);
		}
		
		
	 }
	
	 // cannot build query with JSON object because one of the keys is dynamic
	 // query = get all children having current document as parent
     let queryJsonStr = _commons_perspective_buildStrQueryGetChildItems(itemId,
    		 							mx_helpers_getTermParentRelationName(termDesc));
	 
	 retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	 MxApi.requestCatalogItems({"fromIdx":0,
		 						"size":10000,
		 						"query":queryJsonStr,
		 						"successCallback":retrieveItemsOptionsSuccess,
		 						"errorCallback":retrieveItemsOptionsError});
	return childrenListNode;
}

// ---------------------------------

function _commons_perspective_build_readonly_field_relation(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,
																				fieldVisuDesc,termDesc,itemId,fieldValue) {
	 let fieldNode=document.getElementById("_commons_perspectives_field_readonly_template_relation").cloneNode(true);
	 fieldNode.id="";
	 fieldNode.style.display="block";
	 // title
	 let title = fieldNode.querySelector("._title_");	 
	 
	 title.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; 
	 if (fieldVisuDesc.showTitle==false) { title.style.display='none'; }
	 
	 // value
	 let valueNode = fieldNode.querySelector("._value_");
	 
	 if (fieldValue.length>0) {		 
		 
		 // contents of the relations field might be as an array or not,
		 // depending on who calls it ... to be clarified.
		 if (!fieldValue.startsWith('[')) { fieldValue="["+fieldValue+"]"; }
		 
		 let valueHtmlText = "";
		 // ElasticSearch store all relations in one single field (called 'mx_relations', one max such field per index),
		 // Here the received value is the value of ElastiSearch field 'mx_relations'
		 // we have to extract from it contents concerning our current term.			 
		 let valueJsonStr="{ \"relations\" : "+fieldValue+"}";		 
		 let valueJsonRelationsList = str2json(valueJsonStr);
		 let valueJsonRelation=null; 
		 
		 // for each values of the mx_relations fields, loacte the one concerning
		 // the current field and extract its contents.
		 // This is not so useful for now because ElasticSearch limits
		 // to only one relation value per document.
		 // So basically this loop is always run once.
		 for (var i=0;i<valueJsonRelationsList.relations.length;i++) {
			 
			 valueJson=valueJsonRelationsList.relations[i];
			 
			 // if it is a parent, list children pointing to him
			 if (valueJson.name==mx_helpers_getTermParentRelationName(termDesc)) {
				 let childrenListNode=_commons_perspective_buildChildrenList(itemId,fieldValue,termDesc);
				 valueNode.appendChild(childrenListNode);			 
			 }
			 
			 // if it is a child, point to (unique) parent
			 else if (valueJson.name==mx_helpers_getTermChildRelationName(termDesc)) {
				 valueHtmlText=mx_helpers_getTermChildRelationName(termDesc);
				 if (valueJson.parent!=null) { 
					 referencedObjId=valueJson.parent;			 
				 }
				 
				 if (referencedObjId!=null) {
					 valueHtmlText=referencedObjId;			 
					 let receivedItemContentsCallback=function(itemDesc) {			 
						 valueNode.innerHTML="";
						 if (itemDesc==null) { 
							 valueNode.appendChild(_commons_perspective_getUnknownItemDisplay(referencedObjId)); 
						 }
						 else { 
						 	let linkToItemContents=_commons_perspective_buildLinkToItem(referencedObjId,itemDesc.name,
														 											json2str(valueJson));
						 	valueNode.appendChild(linkToItemContents);
						 }
					 }
					 mx_helpers_getItemDetailsById(referencedObjId,receivedItemContentsCallback);
				 }
				 valueNode.innerHTML=valueHtmlText;
			 }
			 // else it means that this relation does not concern this specific 'relation' field
		 }
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


function _commons_perspective_buildEditableRelationTerm(catalogDesc, itemId, termDesc, curTermValue, onChangeCallback, successCallback) {

	// success change function shall replace original value with
	// name of corresponding document
	let relationEditData={} // will hold a reference to the dropdown object still to be created
	let relationChangeSuccessCallback=function(fieldName,newValue) {
		// replace raw json value by the relation / document name
		let valueJson=str2json(newValue);
		if (valueJson.name==mx_helpers_getTermParentRelationName(termDesc)) {		
			relationEditData.node.setValue(capWords(mx_helpers_getTermParentRelationName(termDesc)));	
		 }
		else {
			_commons_perspective_setValueWithItemFullName(valueJson.parent,relationEditData.node,
					mx_helpers_getTermChildRelationName(termDesc),
					mx_helpers_getTermParentRelationName(termDesc));
		}
		
		// then apply nominal callback, using the 'raw' field name, where
		// the data is actually stored
		//successCallback(fieldName,newValue);
		successCallback(MX_RELATIONS_FIELD_NAME,newValue);
	}
	
	if (curTermValue.length==0) { curTermValue="[]" };
	
	// contents of the relations field might be as an array or not,
	 // depending on who calls it ... to be clarified.
	 if (!curTermValue.startsWith('[')) { curTermValue="["+curTermValue+"]"; }
	 
	// ElasticSearch store all relations in one single field (called 'mx_relations', one max such field per index),
	// Here the received value is the value of ElastiSearch field 'mx_relations'
	// we have to extract from it contents concerning our current term.	
	 let valueJsonStr="{ \"relations\" : "+curTermValue+"}";	
	 let valueJsonRelationsList = str2json(valueJsonStr);
	 let curTermValueJson={};
	 let fieldValueStr="- none -";
	 let relationIndex=0;
	 
	 // extract corresponding relation value
	 for (var i=0;i<valueJsonRelationsList.relations.length;i++) {	
		 valueJson=valueJsonRelationsList.relations[i];		 
		if (valueJson.name==mx_helpers_getTermParentRelationName(termDesc)
				|| valueJson.name==mx_helpers_getTermChildRelationName(termDesc)) {
			fieldValueStr=json2str(valueJson);	
			curTermValueJson=valueJson;
			relationIndex=i;
			break;
		} 		
	 }
	 
	let relationFieldOnChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) {
		onChangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback);
	}
	// create our dropdown object
	dyndropdown = xeditable_create_dropdown_field(
			itemId /* pk */,
			termDesc.name,true /*show fieldName*/,
			fieldValueStr,
			null,//no choices def, will be populated dynamically using addOption function
			relationFieldOnChangeCallback,
			relationChangeSuccessCallback);
	
	// set the reference to the dropdown object, to be used to replace
	// raw json answer by real corresponding document name
	// when update is successful
	relationEditData.node=dyndropdown;
	
	// populate current value
	if (curTermValueJson.parent!=null) {
		_commons_perspective_setValueWithItemFullName(curTermValueJson.parent,dyndropdown,
				mx_helpers_getTermChildRelationName(termDesc),
				mx_helpers_getTermParentRelationName(termDesc));				
	}
	
	// populate options 
	retrieveItemsOptionsSuccess=function(itemsAnswerMsg) {
		let valueJsonEmpty={ "name":"" };
		dyndropdown.addOption(valueJsonEmpty,"- none -");
		let valueJsonParent={				 
				 "name":mx_helpers_getTermParentRelationName(termDesc)
		 };
		dyndropdown.addOption(valueJsonParent,
				 capWords(mx_helpers_getTermParentRelationName(termDesc)));
		
		
		// for each item from the result, retrieve the name
		// This name has been built by the serve to be compliant
		// with definition of the "Cards Title" def. in catalog's overview panel.
		 for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
			 
			 var item=itemsAnswerMsg.items[idx];
			 /*
			 valueJsonRelationsListCopy=str2json(json2str(valueJsonRelationsList.relations));
			 // the real value as actually expected by the server
			 valueJsonRelationsListCopy[relationIndex]={
					 "parent":item.id,
					 "name":mx_helpers_getTermChildRelationName(termDesc)
			 };
			 */
			 valueJson={
					 "parent":item.id,
					 "name":mx_helpers_getTermChildRelationName(termDesc)
			 };
			 // set option to use the expected valiue (json) and human name of corresponding document
			 dyndropdown.addOption(json2str(valueJson),
					 _commons_perspective_getItemDisplayedNameAndRoles(item.name,
							 mx_helpers_getTermChildRelationName(termDesc),
							 mx_helpers_getTermParentRelationName(termDesc)
							 ));					 	 		 
		 }
		
		
	 }
	
	// query all documents which are a 'parent' of this relation term.
    // cannot build query with JSON object because one of the keys is dynamic
	 let queryJsonStr = '{ "match" : { "mx_relations" : "'+mx_helpers_getTermParentRelationName(termDesc)+'" } }';	
	 
	 retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	 
	 MxApi.requestCatalogItems({"fromIdx":0,
		 						"size":10000,
		 						"query":queryJsonStr,
		 						"successCallback":retrieveItemsOptionsSuccess,
		 						"errorCallback":retrieveItemsOptionsError});
	
	 return dyndropdown;
}

</script>

<div style="display:none;" class="mx-perspective-field card mb-4" id="_commons_perspectives_field_readonly_template_relation"  >
	<table style="height:100%;width:100%"><tr>
		<td class="mx-perspective-field-title"><span class="_title_"></span></td>
		<td class="_value_"></td>
	</tr></table>               
</div>

<fieldset id="_perspective_field_relation_children_list_popup_template_"  class="form-control-group card  modals-form-control" 
			style="display:none;max-height:5rem;overflow:auto;">
   <legend class="mx-perspective-field-legend _legend_"></legend>
   
   <table class="table table-striped" style="margin-bottom:0.2rem">
	   <tbody class="_children_table_">
	   	<tr style="display:none" class="_child_row_" ><td style="padding:0;font-size:0.6rem" class="_child_col_"></td></tr>
	   </tbody>   
   </table>
   
   		
   </fieldset>

