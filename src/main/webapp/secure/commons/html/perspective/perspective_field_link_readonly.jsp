<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 
 
<!--------------- LINK -------------->
<!-- 
LINK field is based on classic 'Text' ElasticSearch field type,
where contents is a coma-separated list of documents IDs.  
 -->		  
 <script type="text/javascript" >
 

// -------------- READONLY-------------------


// build list of children connected to a given document
// return div containing datafield with refs
function _commons_perspective_buildRefsDocsList(itemId,docIdsListStr,termDesc) {	
	
	let linksTable=_parseLinksList(docIdsListStr);
	let refDocsListEnabledByIdNode = document.createElement("div");
	refDocsListEnabledByIdNode.innerHTML=docIdsListStr;
	
	// populate documents list with all docs found in current value of the field.
	buildReadOnlyListLinks=function(itemsAnswerMsg) {
		//console.log("retrieved "+itemsAnswerMsg.items.length+" children blabla");
		refDocsListEnabledByIdNode.innerHTML="";
		
		let docsListFieldset = document.getElementById("_perspective_field_link_refsdocs_fieldset_template_").cloneNode(true);
		docsListFieldset.id=itemId+"_"+termDesc.name+"_docslist_readonly";
		docsListFieldset.style.display='block';
		refDocsListEnabledByIdNode.appendChild(docsListFieldset);
		
		// legend
		let legend= docsListFieldset.querySelector("._legend_");
		legend.innerHTML="References : "+itemsAnswerMsg.items.length;
		legend.onclick=function() {
			MxGuiHeader.setCurrentSearchQuery(_commons_perspective_buildStrQueryGetRefItems(linksTable));
			MxGuiHeader.refreshSearch();		
		}
		
		// refs docs table
		let refsDocsTableNode=docsListFieldset.querySelector("._refsdocs_table_");
		
		for (var idx=0;idx<itemsAnswerMsg.items.length;idx++) {
			 var item=itemsAnswerMsg.items[idx];
			 
			 
			 let newRow=document.getElementById("_perspective_field_link_refsdocs_fieldset_template_raw_container_RO_").querySelector("._raw_").cloneNode(true);
			 newRow.style.display="table-row";
			
			 // item name (clickable)
			 let newRowLink=newRow.querySelector("._refdoc_col_");
			 newRowLink.appendChild(_commons_perspective_buildLinkToItem(item.id,item.name,item.id));
			 
			 // link weight
			 let newRowWeight=newRow.querySelector("._link_weight_col_");
			 let weightVal=_getLinkWeight(linksTable,item.id);
			 if (weightVal!=1) { newRowWeight.innerHTML=weightVal; }			 
			 
			 refsDocsTableNode.appendChild(newRow);

		}	
		
	 }
	
	 // cannot build query with JSON object because one of the keys is dynamic
	 // query = get all children having current document as parent
     let queryJsonStr = _commons_perspective_buildStrQueryGetRefItems(linksTable);
	 
	 retrieveItemsOptionsError=function(msg) { footer_showAlert(ERROR, msg); }
	 MxApi.requestCatalogItems({"fromIdx":0,
		 						"size":10000,
		 						"query":queryJsonStr,
		 						"successCallback":buildReadOnlyListLinks,
		 						"errorCallback":retrieveItemsOptionsError});
	return refDocsListEnabledByIdNode;
}




function _commons_perspective_build_readonly_field_reference(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,
																				fieldVisuDesc,termDesc,itemId,fieldValue) {
	 let fieldNode=document.getElementById("_commons_perspectives_field_template_reference").cloneNode(true);
	 fieldNode.id="";
	 fieldNode.style.display="block";
	 // title
	 let titleNode = fieldNode.querySelector("._title_");	 
	 
	 titleNode.innerHTML=mx_helpers_getTermName(termDesc, catalogDesc)+": "; 
	 titleNode.title=termDesc.name;
	 if (fieldVisuDesc.showTitle==false) { titleNode.style.display='none'; }
	 
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

</script>

<table id="_perspective_field_link_refsdocs_fieldset_template_raw_container_RO_" style="display:none;" >
	<tr class="editable-bg-transition _raw_" >
  		<td style="padding:0;font-size:0.6rem;vertical-align:middle;" class="_refdoc_col_" >
  		
  		</td>
  		<td class="_link_weight_col_" 
  				style="padding:0;font-size:0.6rem;width:20%;padding-right:0.3em;font-weight:bold;text-align:end;vertical-align:middle;color:#9a9a9a;"  
  					title="<s:text name="Items.link.weight"/>" >
  		
  		</td>
  		
  	</tr>
</table>

