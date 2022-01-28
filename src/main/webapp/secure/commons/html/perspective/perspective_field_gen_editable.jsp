<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- Generic Customizable Field -------------->		  
 <script type="text/javascript" >

 function _commons_perspective_buildEditableTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,
		 	fieldContainerNode,fieldVisuDesc,termDesc,
			itemId,curTermValue,successCallback,onChangeCallback,
			getFullFieldContentsCallback /*for long_text fields only */) {		 		
 	
	 if (termDesc.datatype=="LINK") { 		
			return _commons_perspective_buildEditableReferenceTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,
				 	fieldContainerNode,fieldVisuDesc,termDesc,
					itemId,curTermValue,successCallback,onChangeCallback);
	 }
	 
	 else if (termDesc.datatype=="LONG_TEXT") { 		
			return _commons_perspective_buildEditableLongTextTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,
				 	fieldContainerNode,fieldVisuDesc,termDesc,
					itemId,curTermValue,successCallback,onChangeCallback,
					getFullFieldContentsCallback);
	 }
	
	 // list or multi choice
 	 if (mx_helpers_isDatatypeEnumOk(termDesc.datatype) 
 					&& termDesc.enumsList.length>1  
 					&& termDesc.enumsList[0]!="") {
 		
 		valuesChoice=[];
 		
 		
		for (var i=0;i<termDesc.enumsList.length;i++) {
			 let curValue=termDesc.enumsList[i];
			 valuesChoice.push({'value':curValue,'text':curValue});
		}
	
		 // multi choice
 		if (mx_helpers_isDatatypeMultiEnumOk(termDesc.datatype) && termDesc.isMultiEnum==true){
 			return xeditable_create_checklist_field(
 					itemId /* pk */,
 					termDesc.name,true /*show fieldName*/,
 					curTermValue,
 					valuesChoice,
 					onChangeCallback,
 					successCallback);
 		}
		// else list (dropdown)	
		return xeditable_create_dropdown_field(
			itemId /* pk */,
			termDesc.name,true /*show fieldName*/,
			curTermValue,
			valuesChoice,
			onChangeCallback,
			successCallback);
 	
 	}
 	// else simple text
	return xeditable_create_text_field(
		itemId /* pk */,
		/*keep original field name (termDesc.name) rather than lexic value when editing 
		  so that there is no ambiguity*/
		termDesc.name,true /*show fieldName*/,
		curTermValue,
		onChangeCallback,
		successCallback);

 }


 function _commons_perspective_build_editable_field(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,
 					itemId,fieldValue,successCallback,
 					getFullFieldContentsCallback /*for long_text fields only */) {
 	 
 	 let fieldNode=document.getElementById("_commons_perspectives_field_editable_template_").cloneNode(true);
 	 fieldNode.id="";
 	 fieldNode.style.display="block";
 	 
 	 function onChangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback){	 
 	 	 if (fieldValue instanceof Array) { fieldValue=array2str(fieldValue); }		 
 	  	 MxApi.requestFieldValueUpdate({
 	  		 "id":itemId,
 	  		 "fieldName":termDesc.name, 
 	  		 "fieldValue":fieldValue,
 	  		 "successCallback":successCallback,
 	  		 "errorCallback":errorCallback
 	  	 });
 	 }
 	  
 	 let newEditableFieldNode = _commons_perspective_buildEditableTerm(catalogDesc,tabIdx,sectionIdx,fieldIdx,
 			 	fieldContainerNode,fieldVisuDesc,termDesc,
				itemId,fieldValue,
				successCallback,
				onChangeCallback,
				getFullFieldContentsCallback);		
 	 newEditableFieldNode.classList.add("mx-perspective-field"); 
 	 newEditableFieldNode.onclick=function(event) { event.stopPropagation(); }
 	 fieldNode.appendChild(newEditableFieldNode);
 	 
 	 fieldContainerNode.appendChild(fieldNode);

  }
  
</script>


<div style="display:none;" class="mx-perspective-field" id="_commons_perspectives_field_editable_template_" >              
</div>



