<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 	

 	  
 <script type="text/javascript" >
 

	// Expects from user pages a node named _perspective_title_ with subnodes with classes:
	//	_perspective_title_
	// _perspective_bodyid_
	// _perspective_body_
	// _remove_button_ (if edit_mode is 'customizable')
	
 var MxGuiPerspective={}
 MxGuiPerspective.MODE_CUSTOMIZE="customize";
 MxGuiPerspective.MODE_EDIT_CONTENTS="edit-contents";
 MxGuiPerspective.MODE_READ_ONLY="read-only";
 
</script>

 
 <s:include value="./perspective_tab.jsp"></s:include>
 
 <script type="text/javascript" >
 

var _deletePerspectiveFuncsById={};
var _curPerspectiveId=0;
var _curPerspectiveTabs=[];
//--------------- PERSPECTIVE --------------

 // function used by commons/details, and items/details
 // editMode: MxGuiPerspective.MODE_CUSTOMIZE|MxGuiPerspective.MODE_EDIT_CONTENTS|MxGuiPerspective.MODE_READ_ONLY
 function _commons_perspective_build(insertSpot,catalogDesc,perspectiveData,editMode,
		 										// only used in EDIT / RO modes
		 										itemId,fieldsValueMap,editSuccessCallback,
		 										getLongFieldFullValueCallback/*for long_text fields only*/) {
	 
	 	let curPerspectiveId="perspective_"+perspectiveData.id;
		let perspectiveNode = document.getElementById("_details_perspective_template_").cloneNode(true);
	 	perspectiveNode.id=curPerspectiveId;
	 	perspectiveNode.style.display='block';	 	
	 	
	 	// title
	 	let perspectiveTitleNode=perspectiveNode.querySelector("._perspective_title_");
	 	perspectiveTitleNode.setAttribute("data-parent","#"+insertSpot.id);
	 	perspectiveTitleNode.setAttribute("href","#"+curPerspectiveId);
	 	perspectiveTitleNode.innerHTML=perspectiveData.name;
	 	
	 	// CUSTOMIZABLE 
	 	if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
	 		
	 		// editable name
	 		let onNameChangeCallback=function(pk,fieldName,fieldValue,successCallback, errorCallback) { 			 
				 let perspectiveDataCopy=JSON.parse(JSON.stringify(perspectiveData));
				 perspectiveDataCopy.name=fieldValue;
			 	 MxApi.requestPerspectiveUpdate({ "catalogId":catalogDesc.id, 
			 		 							  "perspectiveId":perspectiveData.id,
			 		 							  "perspectiveName":perspectiveDataCopy.name,
												  "perspectiveJsonDef":perspectiveDataCopy,
												  "successCallback":successCallback, "errorCallback":errorCallback});		 	 
			 }
			 
			 let onNameChangeSuccessCallback=function(fieldName,fieldValue) { perspectiveData.name=fieldValue; }	 
			 let editableNameNode = xeditable_create_text_field(
					 	"perspective_"+perspectiveData.id+"_name" /* pk */,
						"<s:text name='global.name'/>",false /*show fieldName*/,
						perspectiveData.name,
						onNameChangeCallback, onNameChangeSuccessCallback);
			 perspectiveTitleNode.innerHTML="";
			 perspectiveTitleNode.appendChild(editableNameNode);
			 
			// remove buton
	 		let removeButton=perspectiveNode.querySelector("._remove_button_");
	 		removeButton.style.display='block';
			let onDeletePerspectiveSuccessCallback=function() {
				 delete catalogDesc.perspectives[perspectiveData.name];
				 perspectiveNode.remove();
			 }
			
			_deletePerspectiveFuncsById[perspectiveNode.id]=function(event) {
				 
				 // Async request for value update
			 	 // A message will be sent back to inform success or not of operation,
			 	 // then MxApi will invoke success/error callback depending on returned result
			 	 MxApi.requestPerspectiveDelete({ "catalogId":catalogDesc.id,
										 		  "perspectiveId":perspectiveData.id,
												  "successCallback":onDeletePerspectiveSuccessCallback,
												  "errorCallback":function(msg){footer_showAlert(ERROR, msg);}
			 	 								});	 
			 }
	 	}
	 	
	 	//  body id
	 	let perspectiveBodyIdNode=perspectiveNode.querySelector("._perspective_bodyid_");
	 	perspectiveBodyIdNode.id=curPerspectiveId;
	 	
	 	// show contents (if we click anywhere on the container, not only on the title)	 	
	 	perspectiveNode.open=function() {	 		
	 		if (perspectiveBodyIdNode.className.indexOf("show")<0) { 
	 			perspectiveBodyIdNode.classList.add("show");
	 			_curPerspectiveId=perspectiveData.id;
	 			if (perspectiveNode.activeTabNode!=null) {
	 				perspectiveNode.activeTabNode.activate();
	 			}
	 		}
	 		else { perspectiveBodyIdNode.classList.remove("show"); }
	 		
	 		// close all other opened perspective
	 		let sortedPerspectivesNames = Object.keys(catalogDesc.perspectives).sort();		 
	 		for (pIdx in sortedPerspectivesNames) {
	 			let curPName=sortedPerspectivesNames[pIdx];
	 			let curPerspectiveData=catalogDesc.perspectives[curPName];
	 			if (curPerspectiveData.id!=perspectiveData.id) {	 	
	 				let curPerspectiveNode=document.getElementById("perspective_"+curPerspectiveData.id);
		 			if (curPerspectiveNode==null) { continue; }
		 			let curBodyIdNode=curPerspectiveNode.querySelector("._perspective_bodyid_");
		 			curBodyIdNode.classList.remove("show");
	 			}	 											
	 		}

	 	}
	 	perspectiveNode.onclick=function(event) {
	 		// flag set in tab when user changed it: then event shall be ignored. Couldn't find a better way.
	 		// @see perspective_tab.jsp tabNavNode.onclick
	 		if (event.fromTabChange==true) { return; }
	 		perspectiveNode.open();
	 	}
	 	
	 	// body
	 	let perspectiveBodyNode=perspectiveNode.querySelector("._perspective_body_");
	 	let tabsContainerNode=document.getElementById("_commons_perspectives_tabs_template_").cloneNode(true);
	 	tabsContainerNode.id="";
	 	
	 	let tabNodeToActivate=null;
	 	
	 	for (var tabidx=0;tabidx<perspectiveData.tabs.length;tabidx++) {
	 		//console.log("creating index "+tabidx+" getCurrentTabIndex="+MxGuiPerspective.getCurrentTabIndex());
	 		let curTabDef=perspectiveData.tabs[tabidx];
	 		// some refresh to be improved, don't know why
	 		// pb occures when deleting a tab, closing catalog and opening it again
	 		if (curTabDef==null) { continue; }	 		
	 		let tabNode = _commons_perspective_build_tab(catalogDesc,perspectiveData,tabidx,tabsContainerNode,editMode,itemId,
	 				fieldsValueMap,editSuccessCallback,
	 				getLongFieldFullValueCallback);
	 		 
	 		if (MxGuiPerspective.getCurrentTabIndex()==tabidx) {
	 			tabNodeToActivate=tabNode;
	 			perspectiveNode.activeTabNode=tabNode;	 			
	 		}
	 		_curPerspectiveTabs[tabidx]=tabNode;
	 	}	 	
	 	
	 	// add new tab
	 	if (editMode==MxGuiPerspective.MODE_CUSTOMIZE) {
		 	let tabsNavInsertspot=tabsContainerNode.querySelector("._tabnav_insertspot_");		 	
		 	tabsNavInsertspot.appendChild(MxGuiPerspective.buildTabCreateNewTab(perspectiveData));
	 	}
	 	perspectiveBodyNode.appendChild(tabsContainerNode);
	 	
	 	// new perspectives node
		insertSpot.appendChild(perspectiveNode);
	 	
	 	return perspectiveNode;
	 	
 }

MxGuiPerspective.buildCustomizablePerspective=function(insertSpot,catalogDesc,perspectiveData) {
	let perspectiveNode = _commons_perspective_build(insertSpot,catalogDesc,perspectiveData,MxGuiPerspective.MODE_CUSTOMIZE,
			catalogDesc.id);
	// need to refresh confirmation and xeditable forms once they are defined
	details_postBuildContents();
	return perspectiveNode;
}
MxGuiPerspective.buildEditablePerspective=function(insertSpot,catalogDesc,perspectiveData,itemId,fieldsValueMap,
		editSuccessCallback,getLongFieldFullValueCallback/*for long_text fields only*/) {
	let perspectiveNode = _commons_perspective_build(insertSpot,catalogDesc,perspectiveData,MxGuiPerspective.MODE_EDIT_CONTENTS,
			itemId,fieldsValueMap,editSuccessCallback,
			getLongFieldFullValueCallback/*for long_text fields only*/);
	// need to refresh confirmation and xeditable forms once they are defined
	details_postBuildContents();
	return perspectiveNode;
}
MxGuiPerspective.buildReadOnlyPerspective=function(insertSpot,catalogDesc,perspectiveData,
			itemId,fieldsValueMap,getLongFieldFullValueCallback /*for long_text fields only*/) {
	let perspectiveNode = _commons_perspective_build(insertSpot,catalogDesc,perspectiveData,MxGuiPerspective.MODE_READ_ONLY,
			itemId,fieldsValueMap,null/*no editCallback in RO mode*/,getLongFieldFullValueCallback);
	// need to refresh confirmation and xeditable forms once they are defined
	details_postBuildContents();
	return perspectiveNode;
}
MxGuiPerspective.deletePerspective=function(perspectiveNodeId) {
	_deletePerspectiveFuncsById[perspectiveNodeId]();
}
MxGuiPerspective.getCurrentPerspectiveId=function() { return _curPerspectiveId; }
MxGuiPerspective.activateLastChosenTab=function() {
	// activate tab corresponding to latest user selection
	let curTabIndex=MxGuiPerspective.getCurrentTabIndex();
	let tabAtIndex=_curPerspectiveTabs[curTabIndex];
	if (tabAtIndex!=null) { tabAtIndex.activate(); }
	
}

function _get_new_perspective_json(perspectiveName,catalogId) {
		return {
			'id':0,
			'catalogId':catalogId,
			'name':perspectiveName,
			'tabs':[ 
					{  title:"tab1", 
						sections : [ 
							
							{
							  title: "section1",
							  type: "mozaic",
							  align: "center", 
							  showFrame:"true",
							  fields:[] 
							} 
						 ] 
					 }
			]
		}
}
		
function _sendCreateNewPerspectiveRequest(perspectiveName) {
	 
	 let catalogDesc=MxGuiDetails.getCurCatalogDescription();
	 
	 if (catalogDesc.perspectives==null) { catalogDesc.perspectives=new Array();}
	 perspectiveData=_get_new_perspective_json(perspectiveName,catalogDesc.id);
	 
	 function successCallback(perspectiveId) {
		 perspectiveData.id=perspectiveId;
		 catalogDesc.perspectives[perspectiveName]=perspectiveData;
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
		 							  "perspectiveName":perspectiveData.name,
							 		  "perspectiveId":perspectiveData.id,
									  "perspectiveJsonDef":perspectiveData,
									  "successCallback":successCallback,
									  "errorCallback":function(msg){footer_showAlert(ERROR, msg);}
	 						});	
	 
	 
}

MxGuiPerspective.buildTabCreateNewPerspectiveButton=function() { 
	function onValidCallback(enteredValue) {
		_sendCreateNewPerspectiveRequest(enteredValue);					
	}
	let popupAddPerspective = MxGuiPopups.newTextInputPopup("<s:text name='Catalogs.perspectives.new_name' />"
															,"<s:text name='Catalogs.perspectives.add'/>", onValidCallback);
	 
	let nav_add_new_perspective=document.getElementById("_details_perspectives_add_").cloneNode(true);
	nav_add_new_perspective.id="";
	nav_add_new_perspective.style.display='';
	nav_add_new_perspective.appendChild(popupAddPerspective);
	
	nav_add_new_perspective.onclick=function(event) {
		event.preventDefault();
		event.stopPropagation();
		// comes in the popup
		popupAddPerspective.show();
	} 
	return nav_add_new_perspective;

}

 </script>
 
  
<div  id="_details_perspectives_add_"   
		class="mx-btn-transparent-success mx-square-button-md btn btn-md  shadow-sm text-center "
		style="display:none;width:30%;margin:1rem;"
		title="<s:text name='Catalogs.perspectives.add'/>"	
		onclick="" >
		<i class="fas fa-plus fa-sm text-grey-50"></i>
</div>


 
