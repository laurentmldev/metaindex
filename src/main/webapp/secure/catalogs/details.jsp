<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript" >
 
 var _curCatalogDesc=null;
  
 function details_getCatalogDetailsNode() {
	 if (_curCatalogDesc==null) { return null; }
	 return document.getElementById("details_catalog_"+_curCatalogDesc.id);
 }
 // function used by commons/details.jsp
 function details_buildContents(catalogCard) {
	 
	 var newPopulatedCatalogDetails = document.getElementById("MxGui._templates_.catalog_details").cloneNode(true);
	 newPopulatedCatalogDetails.id="details_catalog_"+catalogCard.descr.id;
	 newPopulatedCatalogDetails.style.display='block';
	 	 
	// title
	let title = newPopulatedCatalogDetails.querySelector("._title_");
	title.innerHTML=catalogCard.descr.vocabulary.name;
	
	// button delete
	let buttonDelete = newPopulatedCatalogDetails.querySelector("._button_delete_");		
	if (catalogCard.descr.ownerId==<s:property value="currentUserProfile.id" />) { buttonDelete.delete_dbid=catalogCard.descr.id; }
	else { buttonDelete.style.display='none'; }
	
	// nbdocs
	let nbdocs = newPopulatedCatalogDetails.querySelector("._nbdocs_");
	nbdocs.innerHTML=catalogCard.descr.nbDocuments;
	
	// nbdocs name
	let nbdocsName = newPopulatedCatalogDetails.querySelector("._nbdocs_name_");
	if (catalogCard.descr.nbDocuments<=1) { nbdocsName.innerHTML=capWords(catalogCard.descr.vocabulary.item); }
	else { nbdocsName.innerHTML=capWords(catalogCard.descr.vocabulary.items); }	

	// open / create
	let openButtonArea = newPopulatedCatalogDetails.querySelector("._foundInDb_");
	let generateButtonArea = newPopulatedCatalogDetails.querySelector("._notFoundInDb_");
	
	if (catalogCard.descr.isDbIndexFound==true) {
		// open button
		if (mx_helpers_isCatalogReadable(catalogCard.descr.userAccessRights)) {
			openButtonArea.style.display="table-row";
			generateButtonArea.style.display="none";
			let openBtn = newPopulatedCatalogDetails.querySelector("._openBtn_");
			
			if (catalogCard.descr.enabled==true) {
				openBtn.onclick=function(e) {
					//console.log("selecting catalog '"+catalogCard.descr.id+"'");
					details_enterCatalog();			
				}
			} else {
				openBtn.classList.add("mx-btn-disabled");
				openBtn.classList.remove("btn-info");
				openBtn.onclick=function(event) {
					event.stopPropagation();
					event.preventDefault();
            		MxGuiHeader.showInfoModal('<s:text name="Catalogs.disabled" />',
            									'<s:text name="Catalogs.disabledExplanation" />');
					
				}
				let warningDisabled=document.createElement("span");
				warningDisabled.innerHTML="<s:text name="Catalogs.disabled" />";
				warningDisabled.title="<s:text name="Catalogs.disabledExplanation" />";
				warningDisabled.classList.add("mx-tiny-warning");
				warningDisabled.onclick=function(event) {
					event.stopPropagation();
					event.preventDefault();
            		MxGuiHeader.showInfoModal('<s:text name="Catalogs.disabled" />',
            									'<s:text name="Catalogs.disabledExplanation" />');					
				}
				openButtonArea.append(warningDisabled);
			}
		}
		
		
	} else {
		openButtonArea.style.display="none";
		generateButtonArea.style.display="table-row";
		if (!mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let buttonNode = generateButtonArea.querySelector("._generateBtn_");
			buttonNode.parentNode.removeChild(buttonNode);
		}
	}

	// from details_tab_mapping.jsp
	details_buildContents_overview(newPopulatedCatalogDetails,catalogCard);

	// from details_tab_mapping.jsp
	details_buildContents_mapping(newPopulatedCatalogDetails,catalogCard);
	
	// from details_tab_lexic.jsp
	details_buildContents_lexic(newPopulatedCatalogDetails,catalogCard);
	
	// from details_tab_perspectives.jsp
	details_buildContents_perspectives(newPopulatedCatalogDetails,catalogCard);
	
	// users tab requires extra db requests and is refreshed only if user enters the tab
	//
	
	_curCatalogDesc=catalogCard.descr;
	details_selectCatalog(_curCatalogDesc.id);
		
	return newPopulatedCatalogDetails;			 
 }

 function details_selectCatalog(catalogId) {
	MxApi.requestSelectCatalog(catalogId);	
 } 

 var reCatalogName = /^[a-z][a-z0-9_]{3,}$/;
	
 function details_createCatalog(catalogName) {
	 if (catalogName==null) { catalogName=_curCatalogDesc.name; }
	 if (!reCatalogName.test(catalogName)) {
		 footer_showAlert(WARNING,"<s:text name="Catalogs.create.nameSyntaxNotGood" />");
		 return;
	 }
	 footer_showAlert(INFO,"<s:text name="global.pleasewait"/>",null,5000);
	 MxApi.requestCreateCatalog(catalogName);
 }
 function details_enterCatalog() {
	 footer_showAlert(INFO, "<s:text name="Catalogs.enteringCatalog"/>");
	 redirectToPage("${webAppBaseUrl}/Items");
 }
 
// remember which tab was open when a Gui refreshed is performed 
var _curGuiActiveTabId=null;
function details_memGui() {
	 let guiDetailsTab_overview = document.getElementById("nav-overview-tab");
	 if (guiDetailsTab_overview.classList.contains("active")) {
		 _curGuiActiveTabId="overview";
		 return; 
	}
	 
	 let guiDetailsTab_mapping = document.getElementById("nav-mapping-tab");
	 if (guiDetailsTab_mapping.classList.contains("active")) { 
		 _curGuiActiveTabId="mapping"
		 return; 
	 }
	 
	 let guiDetailsTab_perspectives = document.getElementById("nav-perspectives-tab");
	 if (guiDetailsTab_perspectives.classList.contains("active")) { 
		 _curGuiActiveTabId="perspectives"
		 return; 
	 }
 }
 function details_restoreGui() {
	 
	 if (_curGuiActiveTabId!=null) {
			 
		 //console.log("setting active tab "+_curGuiActiveTabId);
		 
		 let guiDetailsTab_overview = document.getElementById("nav-overview-tab");
		 guiDetailsTab_overview.classList.remove("active");
		 let guiDetailsContents_overview = document.getElementById("nav-overview");
		 guiDetailsContents_overview.classList.remove("active","show");
		 
		 let guiDetailsTab_mapping = document.getElementById("nav-mapping-tab");
		 guiDetailsTab_mapping.classList.remove("active");
		 let guiDetailsContents_mapping = document.getElementById("nav-mapping");
		 guiDetailsContents_mapping.classList.remove("active","show");
		 
		 let guiDetailsTab_perspectives = document.getElementById("nav-perspectives-tab");
		 guiDetailsTab_perspectives.classList.remove("active");
		 let guiDetailsContents_perspectives = document.getElementById("nav-perspectives");
		 guiDetailsContents_perspectives.classList.remove("active","show");
		 
		 _curGuiActiveTab=document.getElementById("nav-"+_curGuiActiveTabId+"-tab");
		 _curGuiActiveTab.classList.add("active");
		 _curGuiActiveContents=document.getElementById("nav-"+_curGuiActiveTabId);
		 _curGuiActiveContents.classList.add("active","show");
		 
		 //console.log("setting active tab "+_curGuiActiveTabId+" done");
	 }
	 
 }


function details_reconfirm_delete_catalog(catId) {
	
	function confirmAction() { ws_handlers_deleteCatalog(catId); }
		
	MxGuiHeader.showInfoModalAlertConfirm(
			"<s:text name="Catalogs.deleteConfirmation.title" />",
			"<s:text name="Catalogs.deleteConfirmation.body" />",
			"<s:text name="Catalogs.deleteConfirmation.yes" />",
			"<s:text name="Catalogs.deleteConfirmation.no" />",
			confirmAction
			);	
}

 MxGuiDetails.HideBulkActionsButton();
 MxGuiDetails.setTitle("<s:text name="Catalogs.configurationPanel" />");
 MxGuiDetails.setCurCatalogDescription=function(curCommDesc) { _curCatalogDesc=curCommDesc; }
 MxGuiDetails.getCurCatalogDescription=function() { return _curCatalogDesc; }
 MxGuiDetails.getDetailsNode=details_getCatalogDetailsNode;
 MxGuiDetails.getPerspectivesInsertSpot=function(detailsNode) { return detailsNode.querySelector('._details_perspectives_insertspot_'); }
 MxGuiDetails.memGui=details_memGui;
 MxGuiDetails.restoreGui=details_restoreGui;
 MxGuiDetails.updateUsersTab=details_updateUsersTab;
 MxItemsView.extractName=function(objDescr) {
	 if (objDescr["vocabulary"] != null) { return objDescr.vocabulary.name; }
	 else { return objDescr.name; }
 }
 
 </script>
 
 
 <div id="MxGui._templates_.catalog_details" style="display:none;margin-top:2rem;text-align:left;">
 	
 	
 		
		<table style="margin:0.5rem;margin-bottom:2rem;">
 		<tr>
 			<td><h2 class="_title_" style="font-weight:bold;margin-right:2rem;"></h2></td>
 		<td  >
	 		<span class="_foundInDb_" style="display:none">
		 		<a href="#"  class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm" style="font-size:1.3rem">
		 			<i class="mx-btn-text-icon fas fa-database fa-sm" ></i><b><s:text name="Catalogs.enter" /></b>
		 		</a>
	 		</span>
	 	</td>
 	
 	
 		<td style="padding-left:1rem;padding-right:1rem;width:min-contents;text-align:center;font-size:1.4rem;">
 			<span class="_nbdocs_"  ></span> <span class="_nbdocs_name_"></span>
 		</td>
 	 		
 		<td>
 			
	 		<span class="_notFoundInDb_" style="display:none">
	 			<div class="alert fade show alert-warning" >
					  <span class="_text_" data-toggle="collapse" data-target="" ><s:text name="Catalogs.notFoundInDb" /></span>		  					  
				 </div>
		 		<a href="#" class="_generateBtn_ d-none d-sm-inline-block btn btn-sm btn-warning shadow-sm"
		 			onclick="details_createCatalog();" >
		 			<i class="mx-btn-text-icon fas fa-pen fa-sm text-white-50" ></i><s:text name="Catalogs.create" />
		 		</a>
	 		</span>
 		</td>
 		<td class="dropdown no-arrow mx-1" style="padding-left:1em;">
              <a class="dropdown-toggle" href="#" id="actionsDropdown" 
              	role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-cog fa-fw" style="font-size:1rem;"></i>
                
              </a>
              <!-- Dropdown - Actions -->
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow" 
              aria-labelledby="actionsDropdown"
              		id="details.itemsactions.insertspot"
              		>
                <h6 class="dropdown-header" >Actions</h6>
               
		 		<button type="button" class="_button_delete_ btn btn-default btn-sm alert alert-danger" 	  		
		 	  		data-toggle="confirmation"
		 	  		delete_dbid=""
		 	  		title="<s:text name="global.areYouSure" />"
		 	  		btnOkLabel="<s:text name="global.yes" />"
		 	  		btnCancelLabel="<s:text name="global.no" />"
		 			onConfirm="details_reconfirm_delete_catalog(this.delete_dbid);" onCancel=""
		 			style="margin-left:1em;">
		 			<i class="fa fa-times" aria-hidden="true"></i> <s:text name="Catalogs.delete" />
		 		</button>
              </div>          
		</td>
		</tr>
		</table>
		<hr/>
 	

				<nav>
					<div class="nav nav-tabs nav-fill" id="nav-tab" role="tablist">
						<a class="nav-item nav-link active mx-tab-tiny-shadow " id="nav-overview-tab" data-toggle="tab" 
								href="#nav-overview" role="tab" aria-controls="nav-overview" aria-selected="true">
								<s:text name="Catalogs.overview"></s:text>
						</a>
						<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-mapping-tab" data-toggle="tab" href="#nav-mapping" role="tab" aria-controls="nav-mapping" aria-selected="false"><s:text name="Catalogs.fields"></s:text>
							<span title="S.O.S" 
				                	onclick="event.stopPropagation();event.preventDefault();
				                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.fields.title" />','<s:text name="help.catalog.fields.body" />')">
				                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
				             </span>
					    </a>
						<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-lexic-tab" data-toggle="tab" href="#nav-lexic" role="tab" aria-controls="nav-lexic" aria-selected="false"><s:text name="Catalogs.lexic"></s:text>
							<span title="S.O.S" 
				                	onclick="event.stopPropagation();event.preventDefault();
				                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.lexic.title" />','<s:text name="help.catalog.lexic.body" />')">
				                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
				             </span>
						</a>
						<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-perspectives-tab" data-toggle="tab" href="#nav-perspectives" role="tab" aria-controls="nav-perspectives" aria-selected="false"><s:text name="Catalogs.perspectives"></s:text>
							<span title="S.O.S" 
				                	onclick="event.stopPropagation();event.preventDefault();
				                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.perspective.title" />','<s:text name="help.catalog.perspective.body" />')">
				                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
				             </span>
						</a>
						<c:if test="${mxRunMode != 'standalone'}" >
						<a class="nav-item nav-link mx-tab-tiny-shadow users-tab" onclick="MxGuiCatalogUsersTab.updateUsersList();" 
								id="nav-users-tab" data-toggle="tab" href="#nav-users" role="tab" aria-controls="nav-users" aria-selected="false"
								style="display:none"><s:text name="Catalogs.users"></s:text>
							<span title="S.O.S" 
				                	onclick="event.stopPropagation();event.preventDefault();
				                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.users.title" />','<s:text name="help.catalog.users.body" />')">
				                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
				             </span>
						</a>
						</c:if>
					</div>
				</nav>
				<div class="tab-content py-3 px-3 px-sm-0" id="nav-tabContent">
					<s:include value="details_tab_overview.jsp" />
					<s:include value="details_tab_mapping.jsp" />
					<s:include value="details_tab_lexic.jsp" />
					<s:include value="details_tab_perspectives.jsp" />
					<c:if test="${mxRunMode != 'standalone'}" >	
					<s:include value="details_tab_users.jsp" />
					</c:if> 					
				</div>
	
 </div>
 
