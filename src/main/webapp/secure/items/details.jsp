<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

  <style>
    .popover {
      max-width: none;
    }
  </style>
  
 <s:include value="./details_editable_fields.jsp"></s:include>
 <s:include value="../commons/html/perspective/perspective.jsp" />
 <script type="text/javascript" >

 var _curCatalogDesc=null;
 var _curItemCard=null;
 var _details_DEFAULT_PERSPECTIVE_NAME="default";
 var _details_current_default_perspective=_details_DEFAULT_PERSPECTIVE_NAME;
 var _currentPerspectiveName=null;
 var _curEditMode="readonly"; //edit|readonly
 
 
 function details_switchEditMode() {
	 if (_curEditMode=="readonly") { _curEditMode="edit"; }
	 else { _curEditMode="readonly"; }
	 
	 MxGuiDetails.redraw();
 }

 function details_switchPerspective() {
	 let detailsNode=document.getElementById("current_item_details");
	 let perspectiveSelector=detailsNode.querySelector("._perspectives_select_");
	 let newIndex=perspectiveSelector.selectedIndex+1;
	 if (newIndex>=perspectiveSelector.getElementsByTagName('option').length) { newIndex=0; }
	 
	 perspectiveSelector.value=perspectiveSelector.getElementsByTagName('option')[newIndex].value;
	 _details_current_default_perspective=perspectiveSelector.value;
	 perspectiveSelector.onchange();	 
	 
	// show alert with ne perspective name
	MxGuiDetails.showAlert("<table><tr><td>Perspective : </td><td>"
				+perspectiveSelector.getElementsByTagName('option')[newIndex].innerHTML
				+"</td></tr></table>");
 }
 
 function _details_buildBasicPerspective(fieldsValuesMap) {
	 
	 let basicPerspective={ 
			 	name : _details_DEFAULT_PERSPECTIVE_NAME,
			 	id : "0",
			 	tabs : [ 
			 		{ title:"Tab", 
			 			sections : [ 
			 				{ title :"Section", 
			 				  type : "mozaic", 
			 				  align : "center", 
			 				  fields : [] 
			 				} // section one
			 			] // sections
			 		} //tab one
			 	] // tabs 
			}; // perspective
      			
	 for (var fieldName in fieldsValuesMap) {		 
		 basicPerspective.tabs[0].sections[0].fields.push(
				 	{ term : fieldName,
				 	   showTitle:true,
				 	   weight:"normal",
				 	   size : "medium" });
	 }
	 return basicPerspective;
 }
 
//called from commons/details/details_populate() function
 function details_buildContents(itemCard) {
		
	 _curItemCard=itemCard;
	 var newItemDetails = document.getElementById("MxGuiDetails._templates_.item_details").cloneNode(true);
	 newItemDetails.id="current_item_details";
	 newItemDetails.style.display='block';
	
	// button delete
	let buttonDelete = newItemDetails.querySelector("._button_delete_");
	buttonDelete.delete_dbid=itemCard.descr.id;
	if (!mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)) { buttonDelete.style.display='none'; }
	
	// button editMode
	let buttonEditMode = newItemDetails.querySelector("._button_switch_mode_");
	if (!mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)) { buttonEditMode.style.display='none'; }
	
	// perspective selector
	let perspectiveSelector = newItemDetails.querySelector("._perspectives_select_");
	let sortedPerspectivesNames = Object.keys(MxGuiDetails.getCurCatalogDescription().perspectives).sort();		 
	
	let perspectiveDetectionField=MxGuiDetails.getCurCatalogDescription().perspectiveMatchField;
	_currentPerspectiveName=null;
	for (pIdx in sortedPerspectivesNames) {
		let pName=sortedPerspectivesNames[pIdx];
		let curTestedVal=itemCard.descr.data[perspectiveDetectionField];
		// if item matches current perspective, we use it 
		if (pName==curTestedVal) { 
			_currentPerspectiveName=pName; 
		}
		
		let newOption=document.createElement("option");
		newOption.setAttribute("value",pName);
		newOption.innerHTML=pName;
		perspectiveSelector.appendChild(newOption);
	}
	
	let newOption=document.createElement("option");
	newOption.setAttribute("value",_details_DEFAULT_PERSPECTIVE_NAME);
	newOption.innerHTML=_details_DEFAULT_PERSPECTIVE_NAME;
	perspectiveSelector.appendChild(newOption);
	
	// if current item does not contain the perpective-detection-field, then we use default perspective
	if (_currentPerspectiveName==null) { _currentPerspectiveName=_details_current_default_perspective; }
	
	// id
	let idNode = newItemDetails.querySelector("._dbid_");
	idNode.innerHTML=itemCard.descr.id;
	
	// lastchange info
	let lastchangeTimestampNode = newItemDetails.querySelector("._lastchangetimestamp_");
	lastchangeTimestampNode.innerHTML=itemCard.descr.lastModifTimestampStr;
	let lastchangeUser = newItemDetails.querySelector("._lastchangeuser_");
	lastchangeUser.innerHTML=itemCard.descr.lastModifUserNickname;
		
	let fieldsInsertSpot = newItemDetails.querySelector("._fields_insertspot_");
	
	let fieldsValuesMap=itemCard.descr.data;
	let editSuccessCallback=function(fieldName,newValue) {
		if (newValue instanceof Array) { newValue=array2str(newValue); }
		//console.log("changed "+fieldName+"="+newValue);
		itemCard.descr.data[fieldName]=newValue;
		lastchangeTimestampNode.innerHTML="<s:text name='global.now'/>";
		lastchangeUser.innerHTML="<s:text name='global.you'/>";
		
	}
	//dumpStructure(_curCatalogDesc);
	
	// configure (and activate default) perspective
	perspectiveSelector.value=_currentPerspectiveName;
	perspectiveSelector.onchange=function(event) {		
		fieldsInsertSpot.innerHTML="";
		_currentPerspectiveName=this.value;
		let perspectiveDefinition=null;
		if (this.value==_details_DEFAULT_PERSPECTIVE_NAME) { 
			perspectiveDefinition=_details_buildBasicPerspective(fieldsValuesMap); 
		}
		else { perspectiveDefinition=MxGuiDetails.getCurCatalogDescription().perspectives[this.value]; }
		
		if (_curEditMode=="edit" && mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)) { 
			MxGuiPerspective.buildEditablePerspective(fieldsInsertSpot,
					MxGuiDetails.getCurCatalogDescription(),
					perspectiveDefinition,
					itemCard.descr.id,fieldsValuesMap,editSuccessCallback);
		} else {
			
			MxGuiPerspective.buildReadOnlyPerspective(fieldsInsertSpot,
				MxGuiDetails.getCurCatalogDescription(),
				perspectiveDefinition,
				itemCard.descr.id,fieldsValuesMap);
			
		}
	}
	perspectiveSelector.onchange();
	
	return newItemDetails;			 
 }
 
 


 MxGuiDetails.setTitle("<s:property value='currentUserProfile.catalogVocabulary.itemsCap'/>");
 MxGuiDetails.getCurCatalogDescription=function() { return _curCatalogDesc; }
 MxGuiDetails.getCurItemCard=function() { return _curItemCard; }
 MxGuiDetails.redraw=function() {
	 let itemsDetails = document.getElementById("current_item_details");
	 let perspectiveSelector = itemsDetails.querySelector("._perspectives_select_");
	 perspectiveSelector.onchange();
 }
 MxGuiDetails.switchEditMode=details_switchEditMode;
 MxGuiDetails.switchPerspective=details_switchPerspective;
 MxGuiDetails.handleCatalogDetails=function(curCatalogDescr) {
	 
	_curCatalogDesc=curCatalogDescr; 
	 
	var bulkActionsInsertSpot = MxGuiDetails.getBulkActionsInsertSpot();	
	// delete-all button
	if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)) {
		var deleteAllItemsButton=document.getElementById("details.deleteAll.button");
		bulkActionsInsertSpot.appendChild(deleteAllItemsButton);
		deleteAllItemsButton.style.display='block';
	}
	
 }

 MxGuiCards.extractName=function(objDescr) {
	 if (objDescr.name == "") { return "<s:property value='currentUserProfile.catalogVocabulary.itemCap'/>"; }
	 else { return objDescr.name; }
 }
 </script>
 
 <!-- expected by commons/perspectives.jsp -->
 <div id="_details_perspective_template_" style="display:none;">
	<span style="display:none" class="_perspective_title_ _perspective_bodyid_"></span>
	<div class="mx-perspective-body card-body  _perspective_body_" ></div>	
</div>

 <div id="MxGuiDetails._templates_.item_details" style="display:none" >
 
          
 	 <nav class="navbar navbar-expand navbar-light topbar static-top" >
		
 			<button type="button" class="btn btn-default btn-sm editable-cancel alert alert-info"
 			style="margin:0.2em;"
 			onclick="_curItemCard.click();_curItemCard=null;" >
 				<i class="fa fa-times" aria-hidden="true"></i>
 				 
 			</button>
 		
 			<div class="dropdown no-arrow mx-1" style="padding-left:1em;vertical-align:center;">
              <a class="dropdown-toggle" href="#" id="actionsDropdown" 
              	role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-cog fa-fw" style="font-size:1rem;"></i>
                
              </a>
              <!-- Dropdown - Actions -->
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow  animated--grow-in" 
              		aria-labelledby="actionsDropdown"
              		id="details.itemsactions.insertspot"
              		style="position:relative;margin-top:10rem;margin-left:2rem;">
                <h6 class="dropdown-header" ><s:text name='Items.actions'/></h6>
               
                <select  style="margin:0.5rem;width:80%;" class="form-control bg-light border-0 small _perspectives_select_" ></select>
                
                <button type="button" class="_button_switch_mode_ btn btn-default btn-sm alert alert-info" 	  		
		 	  		onclick="details_switchEditMode();"
		 			style="margin-left:1em;margin-bottom:0;"> 
		 			<s:text name='Items.editmode' />
		 		</button>
		 		<hr style="margin:0.5rem;"/>
		 		<button type="button" class="_button_delete_ btn btn-default btn-sm alert alert-danger" 	  		
		 	  		data-toggle="confirmation"
		 	  		delete_dbid=""
		 			onConfirm="ws_handlers_deleteItem(this.delete_dbid);" onCancel=""
		 			style="margin-left:1em;"><i class="fa fa-times" aria-hidden="true"></i> 
		 			<s:text name="Items.delete" />
		 		</button>
              </div>
          </div>
		
	
	<ul class="navbar-nav ml-auto" >
	  <div>
 	 	<div>
 	 		<span style="font-weight:bold;padding:0.2rem;">ID:</span>
 	 		<span class="_dbid_" style="padding:0.2rem;"></span>
 	 	</div> 	
 		<div>
 	 		<table><tr>
 				<td style="font-weight:bold;padding:0.2rem;"><s:text name='Items.lastchange'/>:</td>
 				<td style="padding:0.2rem;">
						<span class="_lastchangetimestamp_" style="padding:0.2rem;"></span>
	 					<s:text name='global.by'/> <span class="_lastchangeuser_" style="padding:0.2rem;"></span>
				</td>				
			</tr></table>
 	 	</div>
 	 </div> 	
	 </ul>
	 
 	</nav><!-- end of header cards deck -->
 	
 	<div class="_fields_insertspot_" ></div>
 			
 </div><!-- end of details container -->
 
 
 
 <button id="details.deleteAll.button" type="button" class="btn btn-default btn-sm alert alert-danger" 	  
 		style="display:none;margin-left:1rem;margin-right:1rem;"		
  		data-toggle="confirmation"
  		onConfirm="ws_handlers_deleteAllItems();" 
  		onCancel="" ><i class="fa fa-times" aria-hidden="true"></i> 
		<s:text name="Items.deleteAll" />
	</button>
	
<script type="text/javascript" >
	
	bootstrap_confirmation_finishConfirmations();
	
</script>
