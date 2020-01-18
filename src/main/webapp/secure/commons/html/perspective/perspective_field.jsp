<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="./perspective_field_tinytext.jsp"></s:include>
<s:include value="./perspective_field_linkurl.jsp"></s:include>
<s:include value="./perspective_field_imageurl.jsp"></s:include>
<s:include value="./perspective_field_relation.jsp"></s:include>
<s:include value="./perspective_field_reference.jsp"></s:include>
 
<s:include value="./perspective_field_gen_customizable.jsp"></s:include>
<s:include value="./perspective_field_gen_editable.jsp"></s:include>
<s:include value="./perspective_field_gen_readonly.jsp"></s:include>
		  
 <script type="text/javascript" >
 
 // --------------- FIELD --------------
var _deleteFieldFuncsById={};

 function _commons_perspective_build_field(catalogDesc,perspectiveData,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,termDesc,
		 											editMode,
		 											itemId,fieldValue, /* only for edit and for read-only modes*/
		 											successCallback /* only for edit mode */) {
	 
	 let fieldVisuDesc=perspectiveData.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx];
	 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
		 return _commons_perspective_build_customizable_field(catalogDesc,perspectiveData,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc);
	 }
	 else if (editMode==MxGuiPerspective.MODE_EDIT_CONTENTS) {
		 if (fieldValue==null) { console.log("perspective_field : missing field value"); return ; }
		 if (successCallback==null) { console.log("perspective_field : missing successCallback value"); return ; }
		 return _commons_perspective_build_editable_field(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,
				 					itemId,fieldValue,successCallback);
	 }
	else if (editMode==MxGuiPerspective.MODE_READ_ONLY) {
		return _commons_perspective_build_readonly_field(catalogDesc,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc,
									itemId,fieldValue);
	 }
 }

 function _get_new_field_json(termName) {
		return {
			'term':termName,
			'size':"medium",
			'showTitle':"true",
			'color':"normal",
			'weight':"normal"
		}
 }
		
 function _sendCreateNewPerspectiveFieldRequest(termName,sectionIdx,perspectiveData) {
	 
	 let catalogDesc=MxGuiDetails.getCurCatalogDescription();
	 let curTabIndex=MxGuiPerspective.getCurrentTabIndex();
	 
	 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
	 let curSectionData=perspectiveDataCopy.tabs[curTabIndex].sections[sectionIdx];	 
	 if (curSectionData.fields==null) { curSectionData.fields=new Array();}
	 curSectionData.fields.push(_get_new_field_json(termName));
	 
	 function successCallback() {
		 let curSectionData=perspectiveData.tabs[curTabIndex].sections[sectionIdx];	 
		 if (curSectionData.fields==null) { curSectionData.fields=new Array();}
		 curSectionData.fields.push(_get_new_field_json(termName));
		 		 
		 MxGuiDetails.memGui();
		 let detailsNode=MxGuiDetails.getDetailsNode();
		 MxGuiDetails.getPerspectivesInsertSpot(detailsNode).innerHTML="";
		 details_buildContents_perspectives(detailsNode,{descr:catalogDesc});
		 MxGuiDetails.restoreGui();
	 }
	 
	 // Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result
 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,
							 		  "perspectiveId":perspectiveData.id,
									  "perspectiveJsonDef":perspectiveDataCopy,
									  "successCallback":successCallback,
									  "errorCallback":function(msg){footer_showAlert(ERROR, msg);}
 	 						});	
	 
	 
 }

 
 MxGuiPerspective.buildButtonCreateNewField=function(catalogDesc,perspectiveData,sectionIdx) { 
	function onValidCallback(enteredValue) {
		_sendCreateNewPerspectiveFieldRequest(enteredValue,sectionIdx,perspectiveData);					
	}
	let choices= [];	
	let sortedTermsNames = Object.keys(catalogDesc.terms).sort();	
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		termName=sortedTermsNames[termIdx];
		let termDesc = catalogDesc.terms[termName];
		choices.push({ value:termName, text:termName});
	}
	let popupAddField = MxGuiPopups.newDropdownInputPopup(choices,"<s:text name='Catalogs.perspectives.field.add' />", onValidCallback);	 
	
	let addNewFieldButton=document.getElementById("_commons_perspectives_field_add_").cloneNode(true);
	 addNewFieldButton.id="";
	 addNewFieldButton.style.display="block";			 
	 addNewFieldButton.appendChild(popupAddField);
	
	 addNewFieldButton.onclick=function(event) {
		event.preventDefault();
		event.stopPropagation();
		popupAddField.show();
	} 
	return addNewFieldButton;

 }

 </script>

<!-- Fields template -->


<div  id="_commons_perspectives_field_add_"   
		class="mx-btn-transparent-success mx-square-button-md btn btn-md shadow-sm text-center "
		style="display:none;"
		title="<s:text name='Catalogs.perspectives.field.add' />" >
		<i class="fas fa-plus fa-sm text-grey-50"></i>
</div>

 
