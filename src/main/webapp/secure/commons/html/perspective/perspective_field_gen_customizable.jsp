<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 

<!--------------- Generic Customizable Field -------------->		  
 <script type="text/javascript" >

 
 function _commons_perspective_build_customizable_field(catalogDesc,perspectiveData,tabIdx,sectionIdx,fieldIdx,fieldContainerNode,fieldVisuDesc,termDesc) {
	 
	 let fieldNode=document.getElementById("_commons_perspectives_field_customizable_template_").cloneNode(true);
	 fieldNode.id="field_"+tabIdx+"_"+sectionIdx+"_"+fieldIdx;
	 fieldNode.style.display="block";
	 fieldNode.onclick=function(event) { event.stopPropagation(); }
	 
	 // remove button	 
	 let removeButton = fieldNode.querySelector("._remove_button_");
	 removeButton.style.display='block';
	 let onDeleteFieldSuccessCallback=function(fieldName,fieldValue) {
		 delete perspectiveData.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx];
		 fieldNode.remove();
	 }
	 _deleteFieldFuncsById[fieldNode.id]=function(event) {
		 
		 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
		 delete perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx];
		 
		 //dumpStructure(perspectiveDataCopy);
		 // Async request for value update
	 	 // A message will be sent back to inform success or not of operation,
	 	 // then MxApi will invoke success/error callback depending on returned result
	 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,
								 		  "perspectiveId":perspectiveData.id,
										  "perspectiveJsonDef":perspectiveDataCopy,
										  "successCallback":onDeleteFieldSuccessCallback,
										  "errorCallback":function(msg){footer_showAlert(ERROR, msg);}
	 	 								});	 
	 };
	 
	
	
	 // header
	 let header = fieldNode.querySelector("._header_");
	 header.innerHTML=fieldVisuDesc.term;
	 
	 // size 
	 let sizeNode = fieldNode.querySelector("._size_");
	 let onFieldSizeChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 
		 
		 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
		 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].size=fieldValue;
		 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,"perspectiveId":perspectiveData.id,"perspectiveJsonDef":perspectiveDataCopy,
										  "successCallback":successCallback,"errorCallback":errorCallback});		 	 
	 }
	 let onSizeChangeSuccessCallback=function(fieldName,fieldValue) {
		 perspectiveData.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].size=fieldValue;		 
	 }	 
	 let editableSizeNode = xeditable_create_dropdown_field(
			 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_"+fieldIdx+"_size" /* pk */,
				"<s:text name='Catalogs.perspectives.field.size' />",false /*show fieldName*/, fieldVisuDesc.size,
				[	{ value:"small",text:"<s:text name='Catalogs.perspectives.field.size.small' />"}
				,   { value:"medium",text:"<s:text name='Catalogs.perspectives.field.size.medium' />"}
				,   { value:"big",text:"<s:text name='Catalogs.perspectives.field.size.big' />"} ],
					onFieldSizeChangeCallback, onSizeChangeSuccessCallback);
	 sizeNode.appendChild(editableSizeNode);
	 
	 // weight 
	 let weightNode = fieldNode.querySelector("._weight_");
	 let onFieldWeightChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 
		 
		 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
		 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].weight=fieldValue;
		 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,"perspectiveId":perspectiveData.id,"perspectiveJsonDef":perspectiveDataCopy,
										  "successCallback":successCallback,"errorCallback":errorCallback});		 	 
	 }
	 let onWeightChangeSuccessCallback=function(fieldName,fieldValue) {
		 perspectiveData.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].weight=fieldValue;		 
	 }	 
	 let editableWeightNode = xeditable_create_dropdown_field(
			 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_"+fieldIdx+"_weight" /* pk */,
				"<s:text name='Catalogs.perspectives.field.weight' />",false /*show fieldName*/, fieldVisuDesc.weight,
				[	{ value:"normal",text:"<s:text name='Catalogs.perspectives.field.weight.normal' />"}
				,   { value:"bold",text:"<s:text name='Catalogs.perspectives.field.weight.bold' />"}
				,   { value:"italic",text:"<s:text name='Catalogs.perspectives.field.weight.italic' />"} ],
					onFieldWeightChangeCallback, onWeightChangeSuccessCallback);
	 weightNode.appendChild(editableWeightNode);
	 
	// showTitle 
	 let showTitleNode = fieldNode.querySelector("._showTitle_");
	 let onFieldShowTitleChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 
		 
		 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
		 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].showTitle=fieldValue;
		 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,"perspectiveId":perspectiveData.id,"perspectiveJsonDef":perspectiveDataCopy,
										  "successCallback":successCallback,"errorCallback":errorCallback});		 	 
	 }
	 let onShowTitleChangeSuccessCallback=function(fieldName,fieldValue) {
		 perspectiveData.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].showTitle=fieldValue;		 
	 }	 
	 let editableShowTitleNode = xeditable_create_dropdown_field(
			 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_"+fieldIdx+"_showTitle" /* pk */,
				"ShowTitle",false /*show fieldName*/, ""+fieldVisuDesc.showTitle,
				[	{ value:"true",text:"<s:text name='global.visible' />"}, 
					{ value:"false",text:"<s:text name='global.hidden' />"} ],
					onFieldShowTitleChangeCallback, onShowTitleChangeSuccessCallback);
	 showTitleNode.appendChild(editableShowTitleNode);
	 
	// Color 
	 let colorNode = fieldNode.querySelector("._color_");
	 let onFieldColorChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 
		 
		 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
		 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].color=fieldValue;
		 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,"perspectiveId":perspectiveData.id,"perspectiveJsonDef":perspectiveDataCopy,
										  "successCallback":successCallback,"errorCallback":errorCallback});		 	 
	 }
	 let onColorChangeSuccessCallback=function(fieldName,fieldValue) {
		 perspectiveData.tabs[tabIdx].sections[sectionIdx].fields[fieldIdx].color=fieldValue;		 
	 }	 
	 let editableColorNode = xeditable_create_dropdown_field(
			 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_"+fieldIdx+"_color" /* pk */,
				"<s:text name='Catalogs.perspectives.field.color' />",false /*show fieldName*/, ""+fieldVisuDesc.color,
				[	{ value:"normal",text:"<s:text name='Catalogs.perspectives.field.color.default' />"}, 
					{ value:"black",text:"<s:text name='Catalogs.perspectives.field.color.black' />"},  
					{ value:"red",text:"<s:text name='Catalogs.perspectives.field.color.red' />"},
				 	{ value:"yellow",text:"<s:text name='Catalogs.perspectives.field.color.yellow' />"},  
				 	{ value:"green",text:"<s:text name='Catalogs.perspectives.field.color.green' />"},  
				 	{ value:"orange",text:"<s:text name='Catalogs.perspectives.field.color.orange' />"},
				 	{ value:"blue",text:"<s:text name='Catalogs.perspectives.field.color.blue' />"},  
				 	{ value:"purple",text:"<s:text name='Catalogs.perspectives.field.color.purple' />"}
					],
					onFieldColorChangeCallback, onColorChangeSuccessCallback);
	 colorNode.appendChild(editableColorNode);
	 
	 fieldContainerNode.appendChild(fieldNode);
 }
 
</script>

<div style="display:none;" class="mx_perspective-field-customizable_card card mb-4 _customizable_field_root_" 
	 id="_commons_perspectives_field_customizable_template_"  >
	
	  <div class="d-flex flex-row-reverse mx-perspective-card-header"   >	  
			<div class="p-2 card-header text-center _header_ flex-grow-1" ></div>
		  	<div style="display:none" class="_remove_button_ p-2 mx-btn-transparent-danger mx-square-button-xs d-sm-inline-block btn btn-sm shadow-sm"	 
		  				title="<s:text name='Catalogs.perspectives.field.remove_question' />"
		  				data-toggle="confirmation"
				  		onConfirm="_deleteFieldFuncsById[findAncestorNode(this,'_customizable_field_root_').id]();" 
				  		onCancel=""								  				
			 			onclick="" >
			 			<i class="fas fa-times fa-sm text-grey-50"></i>
			</div>
			   	
	   </div> 
       <div class="py-3 d-flex flex-row align-items-top justify-content-between _body_" 
       				style="margin-left:1rem;" >   
       		<table style="height:100%;width:100%" >		
    		<tr><th class="mx-perspective-field-custom-table"><s:text name='Catalogs.perspectives.field.size' /></th><td class="mx-perspective-field-custom-table _size_"></td></tr>
    		<tr><th class="mx-perspective-field-custom-table"><s:text name='Catalogs.perspectives.field.weight' /></th><td class="mx-perspective-field-custom-table _weight_" ></td></tr>
    		<tr><th class="mx-perspective-field-custom-table"><s:text name='Catalogs.perspectives.field.color' /></th><td class="mx-perspective-field-custom-table _color_" ></td></tr>
    		<tr><th class="mx-perspective-field-custom-table"><s:text name='global.Name' /></th><td class="mx-perspective-field-custom-table _showTitle_" ></td></tr>
    		</table>
       </div>
               
</div>




