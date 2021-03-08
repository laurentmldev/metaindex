<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 
 
<!--------------- LINK -------------->
<!-- 
LINK field is based on classic 'Text' ElasticSearch field type,
where contents is a coma-separated list of documents IDs.  
 -->		  
 <script type="text/javascript" >
 
// -------- EDITABLE ---------


function _buildLinksOnChangeFunc(newRow,item,multiSelectAllowed,checkbox,checkBoxesById,linksTable,refsDocsTableNode,refDocsListEnabledByIdMap,weightInputNode,onChangeCallBack) {
	
	
	let myOnClickFunc=function(e) {
		 event.stopPropagation();
		 
    	 let newValue="";
    	 // multi select (checkbox)
    	 if (multiSelectAllowed) {
    		 if (checkbox.checked) { 
    			 refDocsListEnabledByIdMap[item.id]=true;
    			 weightInputNode.style.display='block';
    		 }
			 else { 
				 refDocsListEnabledByIdMap[item.id]=false;
				 weightInputNode.style.display='none';
			 } 
			 						 
			 for (var curItemId in refDocsListEnabledByIdMap) {
				 if (refDocsListEnabledByIdMap[curItemId]==true) {
					 if (newValue.length>0) { newValue+=","; }
					 newValue+=curItemId;
					 let weight=_getLinkWeight(linksTable,curItemId);					 
					 if (weight!=1) { newValue+=":"+weight; }
				 }
			 }
		 // single select (radio button)	 
    	 } else {
    		 //console.log(refsDocsTableNode);
    		 
    		 let checkboxesList=refsDocsTableNode.querySelectorAll("._selector_radio_");
    		 let weightInputsList=refsDocsTableNode.querySelectorAll("._perspective_field_link_weight_input_");
    		 
    		 // deselect existing one(s)
    		 for (var i=0;i<checkboxesList.length;i++) {    			 
    			 let curCheckBox=checkboxesList[i];    			 
    			 curCheckBox.checked=false;    			 
			 }
    		 for (var i=0;i<weightInputsList.length;i++) {    			 
    			 let curWeightTextInput=weightInputsList[i];    			 
    			 curWeightTextInput.style.display='none' ;   			 
			 }
    		 for (var curItemId in refDocsListEnabledByIdMap) {
    			 refDocsListEnabledByIdMap[curItemId]=false;
    		 }
    		//console.log(item.id);
    		//console.log(checkBoxesById);
    		
    		checkBoxesById[item.id].checked=true;
    		refDocsListEnabledByIdMap[item.id]=true;	    		
    		newValue=item.id;
    		weightInputNode.style.display='block';
    		let weight=weightInputNode.value;
			 if (weight!=1) { newValue+=":"+weight; }
    		
    	 }
    				    	
		 newRow.classList.remove("editable-bg-transition");
		 newRow.style.background="yellow";

		 let onSuccessCallback=function(fieldname,newvalue) {
			 newRow.classList.add("editable-bg-transition");
			 newRow.style.background="";
		 }

		 onChangeCallBack(newValue,onSuccessCallback);
	}
	return myOnClickFunc;
}



// callback function invoked when receiving new items list
// update list with new items
function _buildHandleSearchedItemsFunc(itemId,linksTable,refsDocsTableNode,termDesc,refDocsListEnabledByIdMap,onChangeCallBack) {
	
		let buildHandleSearchedItemsFunc=function(itemsAnswerMsg) {
			
			if (itemsAnswerMsg==null) { return; }
			
			//console.log("### "+itemsAnswerMsg.items.length+" results");
			
			let checkBoxesById=[];
			for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
				 let item=itemsAnswerMsg.items[idx];
				
				 // add a new row for the new document do be listed
				 let newRow=document.getElementById("_perspective_field_link_refsdocs_fieldset_template_raw_container_editable").querySelector("._raw_").cloneNode(true);
				 newRow.style.display="table-row";
				 
				 // selector checkbox
				 let selector=newRow.querySelector("._selector_");
				 selector.style.display="table-cell";
				 let checkbox=selector.querySelector("._selector_checkbox_");
				 
				 let multiSelectAllowed=mx_helpers_isDatatypeMultiEnumOk(termDesc.datatype) && termDesc.isMultiEnum==true;
				 
				 if (multiSelectAllowed==true){ checkbox=selector.querySelector("._selector_checkbox_"); } 
				 else { checkbox=selector.querySelector("._selector_radio_"); }
				 
				 checkbox.style.display='block';
				 checkBoxesById[item.id]=checkbox;
			     if (refDocsListEnabledByIdMap[item.id]==true) {					 
					 checkbox.checked=true;
				 } 	
			     
				 // doc name
				 let newRowDocTitle=newRow.querySelector("._refdoc_col_");
				 newRowDocTitle.appendChild(_commons_perspective_buildLinkToItem(item.id,item.name,item.id));				 

				 // link weight
				 let newRowWeight=newRow.querySelector("._link_weight_col_");
				 let weightVal=_getLinkWeight(linksTable,item.id);
				 let weightInputNode=document.getElementById("_perspective_field_link_weight_input_").cloneNode(true);
				 if (refDocsListEnabledByIdMap[item.id]==true) { weightInputNode.style.display='block'; }
				 else { weightInputNode.style.display='none';  }
				 weightInputNode.value=weightVal;
				 newRowWeight.append(weightInputNode);	
				 
				// click on checkbox
			     checkbox.onclick=_buildLinksOnChangeFunc(newRow,item,multiSelectAllowed,checkbox,
			    		 	checkBoxesById,linksTable,refsDocsTableNode,refDocsListEnabledByIdMap,weightInputNode,onChangeCallBack);
				 // changing weight
				 let myfuncOnWeightChange=_buildLinksOnChangeFunc(newRow,item,multiSelectAllowed,checkbox,
				 		 	checkBoxesById,linksTable,refsDocsTableNode,refDocsListEnabledByIdMap,weightInputNode,onChangeCallBack);
				 newRowWeight.onchange=function(e) {
					 	_setLinkWeight(linksTable,item.id,weightInputNode.value);
					 	myfuncOnWeightChange(e);
					 	
				 }
						
				 
				 refsDocsTableNode.appendChild(newRow);
				 		
			}	
		
	 }
		
	return buildHandleSearchedItemsFunc;	
}

function _buildSearchInputOnChangeFunc(searchinput,selectedOnlyCheckbox,refsDocsTableNode,
								refDocsListEnabledByIdNode,linksTable,refreshDelayMs) {
	if (refreshDelayMs==null) { refreshDelayMs=0; }
	
	let retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	
	let onSearchInputChangeFunc=function(e) {
		if (event) { /*event.preventDefault();*/ event.stopPropagation(); }	
		 
		 searchinput.style["border-color"]="orange";
		 let refreshAndClear=function(itemsReceivedMsg) {
			 clearNodeChildren(refsDocsTableNode);
		 	 refsDocsTableNode.handleSearchedItems(itemsReceivedMsg);
			 
			 if (searchinput.value.length>0) { searchinput.style["border-color"]="green"; }
			 else { searchinput.style["border-color"]="grey"; }
		 }
		 
		 let customQuery=searchinput.value;
		 // combine default field filter with user custom filter
		 if (refDocsListEnabledByIdNode.mxquery!=null && refDocsListEnabledByIdNode.mxquery.length>0) {
			 customQuery="("+customQuery+") AND ("+refDocsListEnabledByIdNode.mxquery+")";	 
		 }
		 
		// combine current filter with only currently selected documents
		 if (selectedOnlyCheckbox.checked) {
			curSelectDocsRequest=_commons_perspective_buildStrQueryGetRefItems(linksTable);
			if (curSelectDocsRequest.length>0) {
				if (customQuery.length>0) { customQuery=customQuery+" AND ("+curSelectDocsRequest+")"; }
				else { customQuery=curSelectDocsRequest; }
			}
		 }		
		 
		 refDocsListEnabledByIdNode.mxCustomQuery=customQuery;
		 let queryError=function(errorMsg) { refsDocsTableNode.handleSearchedItems(null,true);}
		 
		 //console.log("query="+refDocsListEnabledByIdNode.mxCustomQuery);
		 
		 clearTimeout(searchinput.refineListQueryTimer);
		 searchinput.refineListQueryTimer=setTimeout(
			function() {     				        
				 MxApi.requestCatalogItems({"fromIdx":0,
					"size":NB_ELEMENTS_TO_RETRIEVE,
					"query":refDocsListEnabledByIdNode.mxCustomQuery,
					"successCallback":refreshAndClear,
					"errorCallback":retrieveItemsOptionsError});
			 
		    }, 
		    refreshDelayMs);
	}
	return onSearchInputChangeFunc;
}

function _buildTableOnScrollFunc(refsDocsTableNode,refDocsListEnabledByIdNode) {
	
	let retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	
	let tableOnScrollFunc=function(e) {
		if (e.target.scrollTop >= e.target.scrollTopMax) {
			let query=refDocsListEnabledByIdNode.mxquery;
			if(refDocsListEnabledByIdNode.mxCustomQuery!=null) {
				query=refDocsListEnabledByIdNode.mxCustomQuery;
			}
		    //console.log("### reached bottom, completing with query: '"+query+"'");
			MxApi.requestCatalogItems({"fromIdx":refsDocsTableNode.children.length,
				 						"size":NB_ELEMENTS_TO_RETRIEVE,
				 						"query":query,
				 						"successCallback":refsDocsTableNode.handleSearchedItems,
				 						"errorCallback":retrieveItemsOptionsError});
		}
	}
	return tableOnScrollFunc;
}


function _buildLinksArea(itemId,termDesc,refDocsListEnabledByIdNode,refDocsListEnabledByIdMap,linksTable,onChangeCallBack,successCallback) {
	
	refDocsListEnabledByIdNode.innerHTML="";
	
	let docsListFieldset = document.getElementById("_perspective_field_link_refsdocs_fieldset_template_").cloneNode(true);
	docsListFieldset.id=itemId+"_"+termDesc.name+"_docslist_edit";
	docsListFieldset.style.display='block';
	refDocsListEnabledByIdNode.appendChild(docsListFieldset);
	
	// legend
	let legend= docsListFieldset.querySelector("._legend_");	
	legend.innerHTML="Select Elements";
	
	// handle reach scroll bottom: retrieve and list additional items 
	let refsDocsTableNode=docsListFieldset.querySelector("._refsdocs_table_");
	
	// callback function invoked when receiving new items list
	refsDocsTableNode.handleSearchedItems=_buildHandleSearchedItemsFunc(itemId,linksTable,refsDocsTableNode,termDesc,refDocsListEnabledByIdMap,onChangeCallBack);
	
	let refsDocsTableCont=docsListFieldset.querySelector("._refsdocs_table_container_");
	
	// ask for more items when reaching bottom
	refsDocsTableCont.onscroll=_buildTableOnScrollFunc(refsDocsTableNode,refDocsListEnabledByIdNode);
	
	// inner search filter
	let searchinput = docsListFieldset.querySelector("._search_input_");
	searchinput.style.display="block";
	
	let selectedOnlyCheckbox=docsListFieldset.querySelector("._selected_only_checkbox_");
	selectedOnlyCheckbox.style.display="block";
	
	// each time user changes input contents
	// wait for 1s and then update listed elements
	searchinput.oninput=_buildSearchInputOnChangeFunc(searchinput,selectedOnlyCheckbox,
							refsDocsTableNode,refDocsListEnabledByIdNode,linksTable,
							1000 /*delay after typing before refreshing the list (ms)*/);
	selectedOnlyCheckbox.onchange=_buildSearchInputOnChangeFunc(searchinput,selectedOnlyCheckbox,
			refsDocsTableNode,refDocsListEnabledByIdNode,linksTable);
	searchinput.onkeypress=function(e) {
		if (e) { 
			event.stopPropagation(); 
			if (e.keyCode=='13' || e.key=="Enter") {
				selectedOnlyCheckbox.onchange(e);
			}
		}
	}
	searchinput.onkeydown=function(event) {
		if (event) { event.stopPropagation(); }			 			 
	}
	
	selectedOnlyCheckbox.onchange=_buildSearchInputOnChangeFunc(searchinput,selectedOnlyCheckbox,
							refsDocsTableNode,refDocsListEnabledByIdNode,linksTable);
	
	// build 'text mode' edition
	let editTextModeIcon=docsListFieldset.querySelector("._edit_text_mode_");
	let textModeContainer=docsListFieldset.querySelector("._links_list_text__");
	
	let textModeOnChangeFunc=function(pk,fieldName,fieldValue,editableSuccessCallback, errorCallback) { 
		onChangeCallBack(fieldValue,editableSuccessCallback); 
	}
	
	refDocsListStr="";
	for (i=0;i<linksTable.length;i++) {
		let curDocId=linksTable[i].id;
		let curDocWeight=linksTable[i].weight;
		if (refDocsListStr.length>0) { refDocsListStr+=","; }
		refDocsListStr+=curDocId;
		if (curDocWeight!=1) { refDocsListStr+=":"+curDocWeight; }
	}
	let textEditabledNode=xeditable_create_text_field(
			itemId /* pk */,
			termDesc.name,
			true /*show fieldName*/,
			refDocsListStr,
			textModeOnChangeFunc,
			successCallback
			);
	
	clearNodeChildren(textModeContainer);
	textModeContainer.appendChild(textEditabledNode);	
	xeditable_finishEditableFields();
	editTextModeIcon.style.display="block";
	editTextModeIcon.isActive=false;
	editTextModeIcon.onclick=function(e) {
		if (editTextModeIcon.isActive==true) {
			textModeContainer.style.display='none';
			refsDocsTableCont.style.display='block';
			editTextModeIcon.classList.remove('mx-text-button-active');
			searchinput.disabled=false;
			selectedOnlyCheckbox.disabled=false;
			editTextModeIcon.isActive=false;			
		} else {
			textModeContainer.style.display='block';
			refsDocsTableCont.style.display='none';
			editTextModeIcon.classList.add('mx-text-button-active');
			searchinput.disabled=true;
			selectedOnlyCheckbox.disabled=true;
			editTextModeIcon.isActive=true;
		}
	}
	
	// force one first request to populate the list
 	selectedOnlyCheckbox.onchange(null);
	 	 
	
}

//build list of children connected to a given document
//return div containing datafield with refs
function _commons_perspective_buildRefsDocsEditableList(itemId,docIdsListStr,termDesc,onChangeCallBack,successCallback) {	
		
	let linksTable=_parseLinksList(docIdsListStr);
	let refDocsListEnabledByIdMap=[];
	for (var i=0;i<linksTable.length;i++) {
		let curItemId=linksTable[i].id;
		refDocsListEnabledByIdMap[curItemId]=true;
	}
	let refDocsListEnabledByIdNode = document.createElement("div");
	refDocsListEnabledByIdNode.innerHTML=docIdsListStr;
	
	
	_buildLinksArea(itemId,termDesc,refDocsListEnabledByIdNode,refDocsListEnabledByIdMap,linksTable,onChangeCallBack,successCallback);
	
	
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
	 }
	 let onRefsUpdateErrorCallback=function(msg) {
		 footer_showAlert(WARNING, "<s:text name="Catalogs.field.couldNotUpdate" /> : "+msg); 
	 }
	 let onRefsListChangeCallBack=function(newValue,guiSuccessCallback) {
		 let successCallbackWithGui=function(fieldname,newvalue) {
			 if (guiSuccessCallback!=null) {
				 guiSuccessCallback(fieldname,newvalue); 
			 }
			 onRefsUpdateSuccessCallback(fieldname,newvalue);
		 }
		 onChangeCallback(itemId,termDesc.name,newValue,successCallbackWithGui,onRefsUpdateErrorCallback);
	 }
	 let refsListNode=_commons_perspective_buildRefsDocsEditableList(itemId,fieldValue,termDesc,onRefsListChangeCallBack,onRefsUpdateSuccessCallback);
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


//

</script>

	
<input id="_perspective_field_link_weight_input_" class="_perspective_field_link_weight_input_" 
				type="number" style="width:3em;height:3em;display:none" min="1" value="1"/>
	

<table id="_perspective_field_link_refsdocs_fieldset_template_raw_container_editable" style="display:none;" >
	<tr class="editable-bg-transition _raw_" >
  		<td class="_selector_" style="display:none;width:2em;max-width:2em;padding:0;padding-left:2px;margin:0;vertical-align:middle;">
  			<input  style="display:none"  class="_selector_checkbox_" type="checkbox" >
  			<input style="display:none" class="_selector_radio_" type="radio">
  		</td>
  		<td class="_link_weight_col_" 
  				style="padding:0;font-size:0.6rem;width:20%;font-weight:bold;text-align:center;vertical-align:middle;color:#9a9a9a;"  
  					title="<s:text name="Items.link.weight"/>" >
  		
  		</td>
  		<td style="padding:0;font-size:0.6rem;vertical-align:middle;padding-left:0.3em;" class="_refdoc_col_" >
  		
  		</td>
  	</tr>
</table>
