<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<s:include value="./perspective_field.jsp"></s:include>
 
 <script type="text/javascript" >
 
 
//--------------- SECTION --------------
var _deleteSectionFuncsById={};

function _commons_perspective_build_section(catalogDesc,perspectiveData,tabIdx,sectionIdx,sectionContainerNode,editMode,
		 																			itemId,fieldsValueMap,editSuccessCallback) {
		
		 let sectionDefinition=perspectiveData.tabs[tabIdx].sections[sectionIdx];
		 	
		 if (sectionDefinition==null) {
			 footer_showAlert(WARNING,"<s:text name="Catalogs.perspectives.section.inconsistent" /> "+sectionIdx);
			 return;
		 }
		 let sectionTemplate=document.getElementById("_commons_perspectives_tabsection_template_").cloneNode(true);
		 sectionTemplate.id="section_"+tabIdx+"_"+sectionIdx;
		 sectionTemplate.style.display="block";
		 
		 // hide section legend if only one section or if set to false
		 if (editMode!=MxGuiPerspective.MODE_CUSTOMIZE 
				 && sectionDefinition.showFrame=="false"
			) {
			 let fieldsetNode=sectionTemplate.querySelector("._fieldset_");
			 let legendNode=sectionTemplate.querySelector("._legend_");		 
			 
			 legendNode.style.display='none';
			 fieldsetNode.classList.remove("mx-perspective-section-fieldset-border");
			 fieldsetNode.style.margin=0;
			 fieldsetNode.style.padding=0;			 
		 }
		 
		 // title
		 let titleNode=sectionTemplate.querySelector("._title_");
		 titleNode.innerHTML=sectionDefinition.title;
		 		 
		 // CUSTOMIZATION 
		 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
			 
			 // editable title
			 let onSectionTitleChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 			 
				 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
				 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].title=fieldValue;
			 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id, "perspectiveId":perspectiveData.id,
												  "perspectiveJsonDef":perspectiveDataCopy,
												  "successCallback":successCallback, "errorCallback":errorCallback});		 	 
			 }
			 
			 let onSectionTitleChangeSuccessCallback=function(fieldName,fieldValue) { perspectiveData.tabs[tabIdx].sections[sectionIdx].title=fieldValue; }	 
			 let editableTitleNode = xeditable_create_text_field(
					 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_title" /* pk */,
						"<s:text name="Catalogs.perspectives.field.title" />",false /*show fieldName*/,
						sectionDefinition.title,
						onSectionTitleChangeCallback, onSectionTitleChangeSuccessCallback);
			 titleNode.innerHTML="";
			 titleNode.appendChild(editableTitleNode);
			 
			// 'remove' button
			 let removeButton = sectionTemplate.querySelector("._remove_button_");
			 removeButton.style.display='block';
			 let onDeleteSectionSuccessCallback=function(fieldName,fieldValue) {
				 delete perspectiveData.tabs[tabIdx].sections[sectionIdx];
				 sectionTemplate.remove();
			 }
			 _deleteSectionFuncsById[sectionTemplate.id]=function(event) {
				 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
				 delete perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx];
				 
				 //dumpStructure(perspectiveDataCopy);
				 // Async request for value update
			 	 // A message will be sent back to inform success or not of operation,
			 	 // then MxApi will invoke success/error callback depending on returned result
			 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,
										 		  "perspectiveId":perspectiveData.id,
												  "perspectiveJsonDef":perspectiveDataCopy,
												  "successCallback":onDeleteSectionSuccessCallback,
												  "errorCallback":function(msg){footer_showAlert(ERROR, msg);}
			 	 								});	 
			 }
			 
			 // type disabled for now, only 'mozaic' type implemented
			 let typeNode=sectionTemplate.querySelector("._type_");
			 //typeNode.style.display='block';
			 let onSectionTypeChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 
				 
				 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
				 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].type=fieldValue;
			 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id, "perspectiveId":perspectiveData.id,
												  "perspectiveJsonDef":perspectiveDataCopy,
												  "successCallback":successCallback, "errorCallback":errorCallback});		 	 
			 }
			 
			 let onSectionTypeChangeSuccessCallback=function(fieldName,fieldValue) { perspectiveData.tabs[tabIdx].sections[sectionIdx].type=fieldValue; }	 
			 let editableTypeNode = xeditable_create_dropdown_field(
					 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_type" /* pk */,
						"<s:text name='Catalogs.perspectives.section.type' />",true /*show fieldName*/,
						sectionDefinition.type,
						[	{ value:"mozaic",text:"<s:text name='Catalogs.perspectives.section.type.mozaic'/>"},
							{ value:"table",text:"<s:text name='Catalogs.perspectives.section.type.table'/>"}
						],
							onSectionTypeChangeCallback, onSectionTypeChangeSuccessCallback);
			 typeNode.appendChild(editableTypeNode);
			 
			 // align
			 let alignNode=sectionTemplate.querySelector("._align_");
			 alignNode.style.display='block';
			 let onSectionAlignChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 			 
				 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
				 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].align=fieldValue;
				 //console.log("sending perspective def :\n" +JSON.stringify(perspectiveDataCopy));
				 
				 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id, "perspectiveId":perspectiveData.id,
												  "perspectiveJsonDef":perspectiveDataCopy,
												  "successCallback":successCallback, "errorCallback":errorCallback});		 	 
			 }
			 
			 let onSectionAlignChangeSuccessCallback=function(fieldName,fieldValue) { perspectiveData.tabs[tabIdx].sections[sectionIdx].align=fieldValue; }	 
			 let editableAlignNode = xeditable_create_dropdown_field(
					 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_align" /* pk */,
						"<s:text name='Catalogs.perspectives.section.align' />",true /*show fieldName*/,
						sectionDefinition.align,
						[	{ value:"left",text:"<s:text name='global.left' />"},
							{ value:"center",text:"<s:text name='global.center' />"},
							{ value:"right",text:"<s:text name='global.right' />"}
						],
						onSectionAlignChangeCallback, onSectionAlignChangeSuccessCallback);
			 alignNode.appendChild(editableAlignNode);
			 
			 // show frame option
			 let showFrameNode=sectionTemplate.querySelector("._showFrame_");
			 showFrameNode.style.display='block';
			 let onSectionShowFrameChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 			 
				 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
				 perspectiveDataCopy.tabs[tabIdx].sections[sectionIdx].showFrame=fieldValue;
				 //console.log("sending perspective def :\n" +JSON.stringify(perspectiveDataCopy));
				 
				 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id, "perspectiveId":perspectiveData.id,
												  "perspectiveJsonDef":perspectiveDataCopy,
												  "successCallback":successCallback, "errorCallback":errorCallback});		 	 
			 }
			 
			 let onSectionShowFrameChangeSuccessCallback=function(fieldName,fieldValue) { perspectiveData.tabs[tabIdx].sections[sectionIdx].showFrame=fieldValue; }			 
			 let editableShowFrameNode = xeditable_create_dropdown_field(
					 	"perspective_"+perspectiveData.id+"_"+tabIdx+"_"+sectionIdx+"_showFrame" /* pk */,
						"<s:text name='Catalogs.perspectives.section.showFrame' />",true /*show fieldName*/,
						""+sectionDefinition.showFrame,
						[	{ value:"true",text:"<s:text name='global.yes' />"},
							{ value:"false",text:"<s:text name='global.no' />"}
						],
						onSectionShowFrameChangeCallback, onSectionShowFrameChangeSuccessCallback);
			 showFrameNode.appendChild(editableShowFrameNode);
		 } // end of 'if CUSTOMIZE-MODE'
			 
		 // body
		 let bodyNode=sectionTemplate.querySelector("._body_");
		 
		 let listOfMissingTermsInCatalog="";
		 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) { bodyNode.classList.add("mx-perspective-section-align-left"); } 
		 else { bodyNode.classList.add("mx-perspective-section-align-"+sectionDefinition.align); }
		 for (var fieldIdx=0;fieldIdx<sectionDefinition.fields.length;fieldIdx++) {
	 		let curFieldVisuDef=sectionDefinition.fields[fieldIdx];
	 		if (curFieldVisuDef==null) { continue; }
	 		
	 		let curFieldTermDesc=catalogDesc.terms[curFieldVisuDef.term];
	 		
	 		if (curFieldTermDesc==null) {
	 			if (listOfMissingTermsInCatalog!="") { listOfMissingTermsInCatalog+=", "; }
	 			listOfMissingTermsInCatalog+=curFieldVisuDef.term;
	 			// use 'TINY_TEXT' type for unknown fields
	 			curFieldTermDesc={ "catalogId":0, 
	 								"name":curFieldVisuDef.term,
	 								"datatype" : "TINY_TEXT",
	 								enumsList : [],
	 								isMultiEnum : false
	 								};	 			
	 		}	 		
 		 	 			
 			let curFieldValue=null;
 			if (fieldsValueMap!=null) {
 				curFieldValue=fieldsValueMap[curFieldVisuDef.term];
 				// if field not defined in current element, suppose value is empty string
 				if (curFieldValue==null) { curFieldValue=""; }
 				// if it's an object we suppose it is a Json to stringify
 				if (typeof(curFieldValue)==='object') {curFieldValue=JSON.stringify(curFieldValue); }		 
 				 	 				
 			}
 			_commons_perspective_build_field(catalogDesc,perspectiveData,tabIdx,sectionIdx,fieldIdx,bodyNode,curFieldTermDesc,editMode,
 																											itemId,curFieldValue,
 																											editSuccessCallback);
 			 		
	 	 }	
		 if (listOfMissingTermsInCatalog!="") {
		 	footer_showAlert(WARNING, "<s:text name="Catalogs.perspectives.missingFields.1" /> '"+sectionDefinition.title
		 					+"' <s:text name="Catalogs.perspectives.missingFields.2" /> : "+listOfMissingTermsInCatalog);
		 }
		 
		 // add-new-field trailing card
		 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
			 bodyNode.appendChild(MxGuiPerspective.buildButtonCreateNewField(catalogDesc,perspectiveData,sectionIdx));
		 }
		 
		 sectionContainerNode.appendChild(sectionTemplate);
 }
 
 

 function _get_new_section_json(sectionName) {
		return {
			'title':sectionName,
			'type':"mozaic",
			'align':"center",
			'showFrame':"true",
			'fields':[]
		}
 }
		
 function _sendCreateNewSectionRequest(sectionName, perspectiveData) {
	 
	 let catalogDesc=MxGuiDetails.getCurCatalogDescription();
	 let curTabIndex=MxGuiPerspective.getCurrentTabIndex();
	 
	 //dumpStructure(perspectiveDataCopy);
	 
	 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
	 let curTabData=perspectiveDataCopy.tabs[curTabIndex];	 
	 if (curTabData.sections==null) { curTabData.sections=new Array();}
	 curTabData.sections.push(_get_new_section_json(sectionName));
	 
	 function successCallback() {
		 let curTabData=perspectiveData.tabs[curTabIndex];	 
		 if (curTabData.sections==null) { curTabData.sections=new Array();}
		 curTabData.sections.push(_get_new_section_json(sectionName));
		 		 
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
 
 
 MxGuiPerspective.buildButtonCreateNewSection=function(perspectiveData) { 
	function onValidCallback(enteredValue) {
		_sendCreateNewSectionRequest(enteredValue,perspectiveData);					
	}
	let popupAddSection = MxGuiPopups.newTextInputPopup("<s:text name='Catalogs.perspectives.section.new_name' />"
														,"<s:text name='Catalogs.perspectives.section.add' />", onValidCallback);
	 
	
	let addNewSectionButton=document.getElementById("_commons_perspectives_tabsection_add_").cloneNode(true);
	 addNewSectionButton.id="";
	 addNewSectionButton.style.display="block";			 
	 addNewSectionButton.appendChild(popupAddSection);
	
	 addNewSectionButton.onclick=function(event) {
		event.preventDefault();
		event.stopPropagation();
		popupAddSection.show();
	} 
	return addNewSectionButton;

 }
 
 </script>
 
<!-- Sections template -->
<div style="display:none" class="_customizable_section_root_" id="_commons_perspectives_tabsection_template_"  >
	<div class="card-deck col-sm-2 " style="max-width:100%">
	<fieldset class="mx-perspective-section-fieldset mx-perspective-section-fieldset-border p-2 _fieldset_" >
	   <legend  class="w-auto _legend_" >
		   <table onclick="event.stopPropagation();"><tr>
			<td class="_remove_button_" style="display:none" >
				<div class="p-2 mx-btn-transparent-danger mx-square-button-xs d-sm-inline-block btn btn-sm shadow-sm"
							title="<s:text name='Catalogs.perspectives.section.remove_question' />"	 
							data-toggle="confirmation"
					  		onConfirm="_deleteSectionFuncsById[findAncestorNode(this,'_customizable_section_root_').id]();" 
					  		onCancel=""		  				
				 			onclick="" >
				 			<i class="fas fa-times fa-sm text-grey-50"></i>
				</div>
			</td>
			<td>
				<h6 class="_title_" style="margin:1rem;"></h6>
			</td>
			<td><span class="_type_ mx-perspective-section-option" style="display:none"></span></td>
			<!-- section alignement not functional -->
			<td><span class="_align_ mx-perspective-section-option" style="display:none"></span></td>		    
			
			<td><span class="_showFrame_ mx-perspective-section-option" style="display:none"></span></td>
		  </tr></table>
		  
	  </legend>
	  
	  <div class="_body_ mx-details-container" ></div>
	</fieldset>
	  
	</div>
	
	
</div>


<div  style="display:none" class="mx-btn-transparent-success mx-square-button-md btn btn-md shadow-sm text-center _new_section_" 
	id="_commons_perspectives_tabsection_add_"
	style="display:none;width:30%;"
	title="<s:text name='Catalogs.perspectives.section.add' />"
	 >
		
		<i class="fas fa-plus fa-sm text-grey-50"></i>
		
</div>	

 
