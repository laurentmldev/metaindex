<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 	
 	 
 <s:include value="./perspective_section.jsp"></s:include>
 
 <script type="text/javascript" >
 
//--------------- TAB --------------

var _deleteTabFuncsById={};
var _curTabIndex=0;

 // itemId= document id in items, or perspective title in catalog customization
 function _commons_perspective_build_tab(catalogDesc,perspectiveData,tabIdx,tabsContainerNode,editMode,itemId,fieldsValueMap,editSuccessCallback) {
			 
	 let tabDefinition=perspectiveData.tabs[tabIdx];
	 if (tabDefinition==null) {
		 footer_showAlert(WARNING,"inconsistent perspective definition at index "+tabIdx);
		 return;
	 }
	 let tabsNavInsertspot=tabsContainerNode.querySelector("._tabnav_insertspot_");
	 let tabsContentsInsertspot=tabsContainerNode.querySelector("._tabcontents_insertspot_");
	 
	 let tabNavId="perspective_"+perspectiveData.id+"_tab_"+tabIdx+"_nav";
	 let tabContentsId="perspective_"+perspectiveData.id+"_tab_"+tabIdx+"_contents";
	 
	 // nav
	 let tabNavNode = document.getElementById("_commons_perspectives_tabnav_template_").cloneNode(true);
	 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE || perspectiveData.tabs.length>1) { tabNavNode.style.display=''; }
	 tabNavNode.id=tabNavId;
	 tabNavNode.setAttribute("href","#"+tabContentsId);
	 tabNavNode.setAttribute("aria-controls",tabContentsId);
	 tabNavNode.onclick=function(event) { 
		 _curTabIndex=tabIdx;
		 // could not find a simple way to prevent propagation without breaking the tabs switch
		 // so adding information that onclick event with this flag shall be ignored
		 // @see perspective.jsp perspectiveNode.onclick
		 event.fromTabChange=true; 
	 }
	
	 
	 // nav 'remove' button
	 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
		 let removeButton = tabNavNode.querySelector("._remove_button_");
		 removeButton.style.display='block';
		 let onDeleteTabSuccessCallback=function(fieldName,fieldValue) {
			 delete perspectiveData.tabs[tabIdx];	
			 tabNavNode.contentsNode.remove();
			 tabNavNode.remove();
			 _curTabIndex=0;
		 }
		 
		 _deleteTabFuncsById[tabNavId]=function(event) {
			 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
			 delete perspectiveDataCopy.tabs[tabIdx];
			 
			 //dumpStructure(perspectiveDataCopy);
			 // Async request for value update
		 	 // A message will be sent back to inform success or not of operation,
		 	 // then MxApi will invoke success/error callback depending on returned result
		 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,
									 		  "perspectiveId":perspectiveData.id,
											  "perspectiveJsonDef":perspectiveDataCopy,
											  "successCallback":onDeleteTabSuccessCallback,
											  "errorCallback":function(msg){footer_showAlert(ERROR, msg);}
		 	 								});	 
		 }
	 }
	 
	 
	 // nav text 
	 tabText=tabNavNode.querySelector("._nav_text_");
	 
	 let onTitleChangeSuccessCallback=function(fieldName,fieldValue) {
		 //console.log("success "+fieldName+" title set to "+fieldValue);
		 perspectiveData.tabs[tabIdx].title=fieldValue;
	 }	 
	 let onTitleChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback){
		
		 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
		 perspectiveDataCopy.tabs[tabIdx].title=fieldValue;
		 
		 // Async request for value update
	 	 // A message will be sent back to inform success or not of operation,
	 	 // then MxApi will invoke success/error callback depending on returned result
	 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id,
								 		  "perspectiveId":perspectiveData.id,
										  "perspectiveJsonDef":perspectiveDataCopy,
										  "successCallback":successCallback,
										  "errorCallback":errorCallback});		 
	   }
	 
	 
	 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
		 let editableTabNameNode=xeditable_create_text_field(
				 	"perspective_"+itemId /* pk : perspective name  */,
					"tab_"+tabIdx, false /*show fieldName*/,
					tabDefinition.title,
					onTitleChangeCallback,
					onTitleChangeSuccessCallback);
		editableTabNameNode.querySelector("._value_").classList.add("mx-perspective-tab-active");
		
		 
		tabText.appendChild(editableTabNameNode);
	 } else { tabText.innerHTML=tabDefinition.title; }
	 
	 tabsNavInsertspot.appendChild(tabNavNode);
	 if (tabIdx==0) { tabNavNode.classList.add("show","active"); }
	 
	 // contents
	 let tabContentsNode = document.getElementById("_commons_perspectives_tabcontent_template_").cloneNode(true);
	 tabContentsNode.style.display='';
	 tabContentsNode.id=tabContentsId;
	 tabContentsNode.setAttribute("aria-labelledby",tabNavId);		 
	 if (tabIdx==0) { tabContentsNode.classList.add("show","active"); }
	 for (var sectionIdx=0;sectionIdx<tabDefinition.sections.length;sectionIdx++) {
		 let curSectionDef=perspectiveData.tabs[tabIdx].sections[sectionIdx];
 		 // some refresh to be improved, don't know why
 		 // pb occures when deleting a section
 		 if (curSectionDef==null) { continue; }
 		_commons_perspective_build_section(catalogDesc,perspectiveData,tabIdx,sectionIdx,tabContentsNode,editMode,itemId,fieldsValueMap,editSuccessCallback);
 	 }
	 
	 // add-new-section trailing card
	 if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
		 tabContentsNode.appendChild(MxGuiPerspective.buildButtonCreateNewSection(perspectiveData));
	 }
	 tabNavNode.contentsNode=tabContentsNode;
	 tabNavNode.activate=function() {
		 let tabsList=tabNavNode.parentNode.getElementsByClassName('_customizable_tab_root_');		 
		 // activate nav
		 for (var navNodeIdx=0;navNodeIdx<tabsList.length;navNodeIdx++) {
			 let navNode=tabsList[navNodeIdx];
			 navNode.deactivate(); 
		 }
		 tabNavNode.classList.add('active')		 	
		 tabContentsNode.classList.add('active');
		 tabContentsNode.classList.add('show');
	 }
	 tabNavNode.deactivate=function() {		 		
		 tabNavNode.classList.remove('active')
		 tabContentsNode.classList.remove('active');
		 tabContentsNode.classList.remove('show');
	 }
	 
	 tabsContentsInsertspot.appendChild(tabContentsNode);
	 
	 return tabNavNode;
	 
 }
 
 function _get_new_tab_json(tabName) {
		return {
			'title':tabName,
			'sections':[]
		}
 }
		
 function _sendCreateNewTabRequest(newTabName, perspectiveData) {
	 
	 let catalogDesc=MxGuiDetails.getCurCatalogDescription();
	 
	 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
	 if (perspectiveDataCopy.tabs==null) { perspectiveDataCopy.tabs=new Array();}
	 perspectiveDataCopy.tabs.push(_get_new_tab_json(newTabName));
	 
	 function successCallback() {
		 if (perspectiveData.tabs==null) { perspectiveData.tabs=new Array();}
		 perspectiveData.tabs.push(_get_new_tab_json(newTabName));
		 
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
 
 MxGuiPerspective.buildTabCreateNewTab=function(perspectiveData) { 
	function onValidCallback(enteredValue) {
		_sendCreateNewTabRequest(enteredValue,perspectiveData);					
	}
	let popupAddTab = MxGuiPopups.newTextInputPopup("<s:text name='Catalogs.perspectives.tab.new_name' />"
													,"<s:text name='Catalogs.perspectives.tab.add' />", onValidCallback);
	 
	let nav_add_new_tab=document.getElementById("_commons_perspectives_tabnav_new_").cloneNode(true);
	nav_add_new_tab.id="";
	nav_add_new_tab.style.display='';
	nav_add_new_tab.appendChild(popupAddTab);
	
	nav_add_new_tab.onclick=function(event) {
		event.preventDefault();
		event.stopPropagation();
		// comes in the popup
		popupAddTab.show();
	} 
	return nav_add_new_tab;

 }
 
 MxGuiPerspective.getCurrentTabIndex=function() { return _curTabIndex; }

 </script>
 

 <!-- Tabs template -->
 <div id="_commons_perspectives_tabs_template_" >
	 <nav>
		<div class="nav nav-tabs nav-fill _tabnav_insertspot_" id="nav-tab" role="tablist">		
		</div>
	</nav>
	<div class="tab-content py-3 px-3 px-sm-0 _tabcontents_insertspot_" >
		
	</div>
</div>	

<!-- Tab nav -->
<a  style="display:none" 
	class="nav-item nav-link mx-tab-tiny-shadow _customizable_tab_root_" 
	id="_commons_perspectives_tabnav_template_" 
	data-toggle="tab" href="" role="tab" aria-controls="" aria-selected="true"
	>
	
	<table onclick="event.stopPropagation();" ><tr>
		<td style="display:none" class="_remove_button_">
			<span class="p-2 mx-btn-transparent-danger mx-square-button-xs d-sm-inline-block btn btn-sm shadow-sm"	 
		  				title="<s:text name='Catalogs.perspectives.tab.remove_question' />"		
		  				data-toggle="confirmation"
				  		onConfirm="_deleteTabFuncsById[findAncestorNode(this,'_customizable_tab_root_').id]();" 
				  		onCancel="" >
			 			<i class="fas fa-times fa-sm text-grey-50"></i>
			</span>
		</td>
		<td class="_nav_text_" style="margin:2rem;padding-left:1rem;font-weight:bold;" ></td>
	</tr></table>
</a>		

<!-- Tab contents -->
<div style="display:none;" class="tab-pane fade " id="_commons_perspectives_tabcontent_template_" 
	role="tabpanel" aria-labelledby="" >
</div>


<!-- New-Tab nav -->
<div  style="display:none;max-width:4rem;" class="nav-item nav-link mx-tab-tiny-shadow _newtab_ " 
	id="_commons_perspectives_tabnav_new_"	
	>
			 <i class="fas fa-plus fa-sm text-grey-50"></i>		
</div>	


  
