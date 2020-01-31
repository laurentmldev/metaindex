<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="/Items" var="itemsUrl"/>

<script type="text/javascript" >
 // due to ElasticSearch limitations, we only limit one single RELATION field per Catalog
 var details_tab_mapping_relationFieldAlreadyUsed=false;
 
 var _curCatalogDesc=null;

</script>

 <s:include value="details_tab_overview.jsp" />
 <s:include value="details_tab_mapping.jsp" />
 <s:include value="details_tab_lexic.jsp" />
 <s:include value="details_tab_perspectives.jsp" />
 
 <script type="text/javascript" >
  
 function details_getCatalogDetailsNode() {
	 if (_curCatalogDesc==null) { return null; }
	 return document.getElementById("details_catalog_"+_curCatalogDesc.id);
 }
 // function used by commons/details.jsp
 function details_buildContents(catalogCard) {
	 
	 details_tab_mapping_relationFieldAlreadyUsed=false;
	 var newPopulatedCatalogDetails = document.getElementById("MxGui._templates_.catalog_details").cloneNode(true);
	 newPopulatedCatalogDetails.id="details_catalog_"+catalogCard.descr.id;
	 newPopulatedCatalogDetails.style.display='block';
	 	 
	// title
	let title = newPopulatedCatalogDetails.querySelector("._title_");
	title.innerHTML=catalogCard.descr.vocabulary.name;
	
	// button delete
	let buttonDelete = newPopulatedCatalogDetails.querySelector("._button_delete_");		
	if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) { buttonDelete.delete_dbid=catalogCard.descr.id; }
	else { buttonDelete.style.display='none'; }
	
	// nbdocs
	let nbdocs = newPopulatedCatalogDetails.querySelector("._nbdocs_");
	nbdocs.innerHTML=catalogCard.descr.nbDocuments;
	
	// nbdocs name
	let nbdocsName = newPopulatedCatalogDetails.querySelector("._nbdocs_name_");
	nbdocsName.innerHTML=catalogCard.descr.vocabulary.items;
	

	// open / create
	let openButtonArea = newPopulatedCatalogDetails.querySelector("._foundInDb_");
	let generateButtonArea = newPopulatedCatalogDetails.querySelector("._notFoundInDb_");
	
	if (catalogCard.descr.isDbIndexFound==true) {
		// open button
		if (mx_helpers_isCatalogReadable(catalogCard.descr.userAccessRights)) {
			openButtonArea.style.display="table-row";
			generateButtonArea.style.display="none";
			let openBtn = newPopulatedCatalogDetails.querySelector("._openBtn_");		
			openBtn.onclick=function(e) {
				//console.log("selecting catalog '"+catalogCard.descr.id+"'");
				details_enterCatalog();			
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
	
	_curCatalogDesc=catalogCard.descr;
	details_selectCatalog(_curCatalogDesc.id);
	
	return newPopulatedCatalogDetails;			 
 }

 function details_selectCatalog(catalogId) {
	 MxApi.requestSelectCatalog(catalogId);
 } 

 function details_createCatalog(catalogName) {
	 if (catalogName==null) { catalogName=_curCatalogDesc.name; }
	 if (catalogName.toLowerCase()!=catalogName
			 || catalogName.replace(/[_a-z0-9]/g,'').length>0) {
		 footer_showAlert(WARNING,"The catalog id shall only contain lower case letters (no accent), numbers or '_' (no space).<br/>"
				 		+"Following characters are refused '"+catalogName.replace(/[_a-z0-9]/g,'')+"', please try again!");
		 return;
	 }
		 
	 MxApi.requestCreateCatalog(catalogName);
 }
 function details_enterCatalog() {
	 footer_showAlert(INFO, "Entering catalog ...");
	 redirectToPage("${mxUrl}/metaindex/Items");
 }
 
// remember which tab was open when a Gui refreshed is performed 
var _curGuiActiveTabId=null;
function details_memGui() {
	 let guiDetailsTab_custompres = document.getElementById("nav-custompres-tab");
	 if (guiDetailsTab_custompres.classList.contains("active")) {
		 _curGuiActiveTabId="custompres";
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
		 
		 let guiDetailsTab_custompres = document.getElementById("nav-custompres-tab");
		 guiDetailsTab_custompres.classList.remove("active");
		 let guiDetailsContents_custompres = document.getElementById("nav-custompres");
		 guiDetailsContents_custompres.classList.remove("active","show");
		 
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

 MxGuiDetails.setTitle("<s:text name="Catalogs.title" />");
 MxGuiDetails.setCurCatalogDescription=function(curCommDesc) { _curCatalogDesc=curCommDesc; }
 MxGuiDetails.getCurCatalogDescription=function() { return _curCatalogDesc; }
 MxGuiDetails.getDetailsNode=details_getCatalogDetailsNode;
 MxGuiDetails.getPerspectivesInsertSpot=function(detailsNode) { return detailsNode.querySelector('._details_perspectives_insertspot_'); }
 MxGuiDetails.memGui=details_memGui;
 MxGuiDetails.restoreGui=details_restoreGui;
 
 MxGuiCards.extractName=function(objDescr) { return objDescr.vocabulary.name; }
 </script>
 
 
 <div id="MxGui._templates_.catalog_details" style="display:none;margin-top:2rem;">
 	
 	
 	<table><tr>
 		<td>
 			<h2 class="_title_" ></h2></td>
 		<td style="padding-left:1rem;padding-right:1rem;">
 			<span class="_nbdocs_"  ></span> <span class="_nbdocs_name_"></span>
 		</td>
 		<td>
 			<span class="_foundInDb_" style="display:none">
		 		<a href="#"  class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm">
		 			<i class="mx-btn-text-icon fas fa-pen fa-sm text-white-50"></i><s:text name="Catalogs.enter" />
		 		</a>
	 		</span>
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
              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow" aria-labelledby="actionsDropdown"
              		id="details.itemsactions.insertspot">
                <h6 class="dropdown-header" >
                  Actions
                </h6>
               
		 		<button type="button" class="_button_delete_ btn btn-default btn-sm alert alert-danger" 	  		
		 	  		data-toggle="confirmation"
		 	  		delete_dbid=""
		 			onConfirm="ws_handlers_deleteCatalog(this.delete_dbid);" onCancel=""
		 			style="margin-left:1em;"><i class="fa fa-times" aria-hidden="true"></i> 
		 			<s:text name="Catalogs.delete" />
		 		</button>
              </div>
          
		</td>
 	</tr></table>
 	<hr/> 

				<nav>
					<div class="nav nav-tabs nav-fill" id="nav-tab" role="tablist">
						<a class="nav-item nav-link active mx-tab-tiny-shadow " id="nav-custompres-tab" data-toggle="tab" href="#nav-custompres" role="tab" aria-controls="nav-custompres" aria-selected="true"><s:text name="Catalogs.overview"></s:text></a>
						<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-mapping-tab" data-toggle="tab" href="#nav-mapping" role="tab" aria-controls="nav-mapping" aria-selected="false"><s:text name="Catalogs.fields"></s:text></a>
						<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-lexic-tab" data-toggle="tab" href="#nav-lexic" role="tab" aria-controls="nav-lexic" aria-selected="false"><s:text name="Catalogs.lexic"></s:text></a>
						<a class="nav-item nav-link mx-tab-tiny-shadow " id="nav-perspectives-tab" data-toggle="tab" href="#nav-perspectives" role="tab" aria-controls="nav-perspectives" aria-selected="false"><s:text name="Catalogs.perspectives"></s:text></a>
					</div>
				</nav>
				<div class="tab-content py-3 px-3 px-sm-0" id="nav-tabContent">
					<div class="tab-pane fade show active" id="nav-custompres" role="tabpanel" aria-labelledby="nav-custompres-tab">
					   <table class="table table-striped">						    
						    <tbody>
						      <tr>
						        <td  style="font-style:italic"><s:text name="Catalogs.overview.indexName" /></td>
						        <td  style="font-style:italic" class="_index_name_"></td>						        
						      </tr>
						      <tr>
						        <td  style="font-style:italic"><s:text name="Catalogs.overview.userAccessRights" /></td>
						        <td  style="font-style:italic" class="_access_rights_"></td>						        
						      </tr>
						      <tr class="_quota_nb_docs_row_" >
						        <td  style="font-style:italic"><s:text name="Catalogs.overview.quotaTitleNbDocs" /></td>
						        <td  style="font-style:italic" class="_quota_nb_docs_"></td>
						      </tr>
						      <tr class="_quota_disc_space_row_">
						        <td  style="font-style:italic"><s:text name="Catalogs.overview.quotaTitleDiscSpace" /></td>
						        <td  style="font-style:italic" class="_quota_disc_space_"></td>
						       </tr>
						        
						        						        
						      
						      <tr title="<s:text name="globals.clickToCopyToClipboard"/>" class="_ftp_row_"
						      	  onclick="copyToClipBoard(this.querySelector('._ftp_port_').innerHTML);
						      		footer_showAlert(INFO, '<s:text name="globals.ftpPortCopiedToClipboard"/>');
						      			" >
						        <td style="font-style:italic" ><s:text name="Catalogs.overview.ftpPort" /></td>
						        <td style="font-style:italic" class="_ftp_port_" ></td>						        
						      </tr>
						      <tr>
						        <td ><s:text name="Catalogs.overview.thumbnailUrl" /></td>
						        <td class="_thumbnail_url_"></td>						        
						      </tr>
						      <tr>
						        <td ><s:text name="Catalogs.overview.urlsPrefix" /></td>
						        <td class="_url_prefix_"></td>						        
						      </tr>
						      <tr>
						        <td><s:text name="Catalogs.overview.cardsTitles" /></td>
						        <td class="_items_name_fields_"></td>						        
						      </tr>
						      <tr>
						        <td ><s:text name="Catalogs.overview.cardsThumbnailField" /></td>
						        <td class="_items_url_field_"></td>						        
						      </tr>
						      <tr>
						        <td><s:text name="Catalogs.overview.perspectiveMatchField" /></td>
						        <td class="_perspective_match_field_"></td>						        
						      </tr>
						      						      
						    </tbody>
						  </table>						  						
					</div>
					<div class="tab-pane fade" id="nav-mapping" role="tabpanel" aria-labelledby="nav-mapping-tab">
						
						<table class="table table-striped" >
						    <thead>
						      <tr>
						        <th style="min-width:5rem;"><s:text name="Catalogs.field"></s:text>
						        	 <span class="dropdown no-arrow mx-1" style="padding:1rem;">
							              <a class="dropdown-toggle" href="#" id="createTermDropdown" 
							              	role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
							                <i class="fas fa-plus fa-fw" onclick="cleanCreateFieldDatatypesList();"></i>
							                
							              </a>
							              <!-- Create Term form -->
							              <div class="dropdown-list dropdown-menu dropdown-menu-right shadow" 
							              		aria-labelledby="createTermDropdown"
							              		id="MxGui.details.createTerm">
							               
								              <div class="form-inline mr-auto w-auto navbar-search" >
								                  <div class="input-group">
								                    <input id="details.createTerm.name" type="text" class="form-control bg-light border-0 small" 
								                    		style="min-width:200px;margin:0.2rem;"		
								                    		onkeypress="if (event.which==13||event.keycode==13) {
								                    			details_createTerm(
										                       			document.getElementById('details.createTerm.name').value,
										                       			document.getElementById('details.createTerm.datatype').value);
								                    		}"
								                    		placeholder="Term Name ..." aria-label="Filter" aria-describedby="basic-addon2"              			              		 
								                    		aria-label="Create Term Name" aria-describedby="basic-addon2">
								                    
								                    <select id="details.createTerm.datatype"  class="form-control bg-light border-0 small" 
								                    		style="min-width:200px;margin:0.2rem;"	
								                    		onclick="event.stopPropagation();"	
								                    		onkeypress="if (event.which==13||event.keycode==13) {
								                    			details_createTerm(
										                       			document.getElementById('details.createTerm.name').value,
										                       			document.getElementById('details.createTerm.datatype').value);
								                    		}"						                    		
								                    		aria-label="Filter" aria-describedby="basic-addon2"              			              		 
								                    		aria-label="Create Term Type" aria-describedby="basic-addon2">
								                    		
								                    		<!-- Options field by javascript (down this page) -->
															
								                    </select>	
								                    <div class="input-group-append" style="margin:0.2rem">
								                      <button class="btn btn-primary" type="button"
								                       	onclick="details_createTerm(
								                       			document.getElementById('details.createTerm.name').value,
								                       			document.getElementById('details.createTerm.datatype').value);" >
								                        <i class="fas fa-check fa-sm"></i>
								                      </button>
								                      <button class="btn btn-primary" type="button" >
								                        <i class="fa fa-times fa-sm"></i>
								                      </button>
								                    </div>	                    
								                  </div>
								                </div>
							           </div>
							              
							        </span>
						        </th>
						        <th><s:text name="Catalogs.field.type"></s:text></th>
						        <th><s:text name="Catalogs.field.enumeration"></s:text></th>
						        <th><s:text name="Catalogs.field.multi"></s:text></th>
						        <th style="width:200rem"><s:text name="Catalogs.field.comments"></s:text></th>
						        <!-- not functional yet <th>Delete</th>  -->						        
						      </tr>
						    </thead>
						    <tbody class="_terms_insertspot_" >
						      <tr class="_term_template_" style="display:none" >
						        <td class="_term_name_" ></td>
						        <td><span class="_term_type_"></span></td>						        
						        <td><span class="_term_enum_"></span></td>
						        <td><span class="_term_multi_"></span></td>
						        <td><span class="_term_comments_" style="font-size:0.7rem"></span></td>
						      </tr>						      
						    </tbody>
						  </table>
						
					</div>		
					
					
					<div class="tab-pane fade _details_lexic_rootnode_" id="nav-lexic" role="tabpanel" aria-labelledby="nav-lexic-tab">

					  						
					</div>
						
					<div class="tab-pane fade _details_perspectives_insertspot_" id="nav-perspectives" role="tabpanel" aria-labelledby="nav-perspectives-tab">
					  						  						
					</div>
					
					
				</div>
	
 </div>
<script type="text/javascript">

function cleanCreateFieldDatatypesList() {
	let createTermTypeButton = document.getElementById("details.createTerm.datatype");
	clearNodeChildren(createTermTypeButton);
	for (datatypeIdx in mx_helpers_FIELDS_DATATYPES) {
		let datatype=mx_helpers_FIELDS_DATATYPES[datatypeIdx];
		if (details_tab_mapping_relationFieldAlreadyUsed==true 
				&& datatype=="RELATION") { continue; }
			
		let option = document.createElement("option");
		option.id="details.createTerm.datatype."+datatype;
		option.value=datatype;
		option.innerHTML=datatype;	
		createTermTypeButton.appendChild(option);
	}
}
</script>
 
