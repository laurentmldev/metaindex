<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 
 
<!--------------- LINK -------------->
<!-- 
LINK field is based on classic 'Text' ElasticSearch field type,
where contents is a coma-separated list of documents IDs.  
 -->		  
 <script type="text/javascript" >
 
 
//--------------- H E L P E R S ------------------


function _commons_perspective_buildStrQueryGetRefItems(linksTable) {
	let query="";
	
	for (var i=0;i<linksTable.length;i++) {
		if (query.length>0) { query+=" OR "; }
		if (linksTable[i].id.length>0) { query+="_id:\""+linksTable[i].id+"\""; }	
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
	
	if (itemName=="") { itemName=itemId; }
	anchorNode.innerHTML=itemName;
	anchorNode.href="#";
	anchorNode.title=title;
	anchorNode.onclick=function() {
		MxGuiHeader.setCurrentSearchQuery("_id:\""+itemId+"\"");
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

function _parseLinksList(docIdsListStr) {
	
	let idsList=docIdsListStr.replace(/([^:,]+)(\:\d+)?($|,)/g,"$1$3").split(",");
	let weightsList=docIdsListStr.replace(/[^:,]+(\:(\d+))?($|,)/g,"$2$3").split(",");
	
	// list of links and weights
	let linksTable=[];
	for (var i=0;i<idsList.length;i++) {
		let curId=idsList[i];
		let curWeight=weightsList[i];
		if (curWeight.length==0) { curWeight=1; }
		linksTable.push({"id":curId,"weight":curWeight});
	}
	
	return linksTable;
}

function _getLinkWeight(linksTable,targetId) {
	for (var i=0;i<linksTable.length;i++) {
		if (linksTable[i].id==targetId) { return linksTable[i].weight; }
	}
	return 1;
}
function _setLinkWeight(linksTable,targetId,weightVal) {
	for (var i=0;i<linksTable.length;i++) {
		if (linksTable[i].id==targetId) { 
			linksTable[i].weight=weightVal;
			return;
		}
	}
	// if we arrive here, we did not find the item in current list so we add it
	linksTable.push({"id":targetId,"weight":weightVal});
	
}


</script>
<s:include value="./perspective_field_link_readonly.jsp"></s:include>
<s:include value="./perspective_field_link_editable.jsp"></s:include>

<div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_field_template_reference"  >
	<table style="height:100%;width:100%">
	 <tr>
		<td class="mx-perspective-field-title"><span class="_title_"></span></td></tr>
		<tr><td class="_value_"></td>
	</tr></table>               
</div>

<fieldset id="_perspective_field_link_refsdocs_fieldset_template_"  class="form-control-group modals-form-control" 
			style="display:none;max-height:8rem;overflow:auto;">
   <legend class="mx-perspective-field-legend">
   		<table class="_links_list_table_">
   		<tr><td colspan="3" class="_legend_" ></td></tr>   		
   		<tr>
   			<td><input type="checkbox" style="display:none" class="_selected_only_checkbox_" 
   							title="<s:text name='Items.filterListSelectedOnly'/>"></td>
   				<td>
   				<input style="display:none;border:2px solid grey" type="text" placeholder="<s:text name="Items.filterThat" /> ..."  
   										title="<s:text name='Items.filterList'/>" class="small _search_input_" >
			</td>
			<td><div style="display:none;" class="mx-text-button _edit_text_mode_" 
									title="<s:text name='Items.editLinksTextMode'/>">T</div></td>
			
   		</tr>
   		</table>   		
   	</legend>
   <div style="display:none;" class="_links_list_text__"></div>
   <div class="_refsdocs_table_container_" style="overflow:auto;max-height:10vh"">
	   <table class="table table-striped" style="margin-bottom:0.2rem display:none"  >
		   <tbody class="_refsdocs_table_"  style="overflow:auto" >
		   	
		   </tbody>   
	   </table>
    </div>
   
</fieldset>






