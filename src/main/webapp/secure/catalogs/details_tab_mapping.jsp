<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 		  
 <script type="text/javascript" >
 

 function details_termOnTypeChange(pk,termName,newTermType,successCallback, errorCallback){	 
 	 MxApi.requestTermUpdate({
 		 "catalogId":_curCatalogDesc.id,
 		 "termName":termName,
 		 "termType":newTermType,
 		 "termEnumsList":_curCatalogDesc.terms[termName].enumsList,
 		 "termIsMultiEnum":_curCatalogDesc.terms[termName].isMultiEnum,
 		 "successCallback":successCallback,
 		 "errorCallback":errorCallback
 	 });	 
}

 function details_termOnEnumsListChange(pk,termName,newEnumsList,successCallback, errorCallback){	
	 
 	 MxApi.requestTermUpdate({
 		 "catalogId":_curCatalogDesc.id,
 		 "termName":termName,
 		 "termType":_curCatalogDesc.terms[termName].datatype,
 		 "termEnumsList":newEnumsList.split(','),
 		 "termIsMultiEnum":_curCatalogDesc.terms[termName].isMultiEnum,
 		 "successCallback":successCallback,
 		 "errorCallback":errorCallback
 	 });	 
}
 
 function details_termOnMultiEnumChange(pk,termName,newIsMultiEnum,successCallback, errorCallback){	 
 	 MxApi.requestTermUpdate({
 		 "catalogId":_curCatalogDesc.id,
 		 "termName":termName,
 		 "termType":_curCatalogDesc.terms[termName].datatype,
 		 "termEnumsList":_curCatalogDesc.terms[termName].enumsList,
 		 "termIsMultiEnum":newIsMultiEnum,
 		 "successCallback":successCallback,
 		 "errorCallback":errorCallback
 	 });	 
}
 
 
 // function used by details.jsp:details_buildContents
 function details_buildContents_mapping(newPopulatedCatalogDetails,catalogCard) {
	 
	// Terms definition
	let termsInsertspot = newPopulatedCatalogDetails.querySelector("._terms_insertspot_");
	let termNodeTemplate = newPopulatedCatalogDetails.querySelector("._term_template_");
	let sortedTermsNames = Object.keys(catalogCard.descr.terms).sort();		
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		termName=sortedTermsNames[termIdx];
		let termDesc = catalogCard.descr.terms[termName];
		let newTermNode = termNodeTemplate.cloneNode(true);
		newTermNode.style.display="table-row";
		
		// term name
		let termNameNode=newTermNode.querySelector("._term_name_");
		termNameNode.innerHTML=termName;
		
		// term type
		let termTypeNode=newTermNode.querySelector("._term_type_");		
		let fieldEnumNode=newTermNode.querySelector("._term_enum_");	
		let fieldMultiEnumNode=newTermNode.querySelector("._term_multi_");		
		
		let successCallbackType=function(fieldName,newValue) { 
			termDesc.datatype=newValue;
			if (mx_helpers_isDatatypeEnumOk(termDesc.datatype)) { 
				fieldEnumNode.style.display='block'; 
				fieldMultiEnumNode.style.display='block';
			}
			else { 
				fieldEnumNode.style.display='none'; 
				fieldMultiEnumNode.style.display='none'; 
			}
			//console.log("type modified properly : "+fieldName+"="+newValue); 			
		}		
		
		// we cannot easily change the type of underlying ElasticSearch mapping for a given field 
		// (for this we need a re-index operation, not implemented yet)
		// So we offer the user only the possibility to change the interpretatoion of the data by metaindex
		// ex: a text can be interpreted as a web link or a rich text ...
		let compatibleTypesChoice=mx_helpers_getDataTypesChoice(termDesc.datatype);
		if (compatibleTypesChoice.length>1) {
			if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
				let editabletermTypeNode = xeditable_create_dropdown_field(
						termName /* pk */,
						termName,false /*show termName*/,
						termDesc.datatype, /* current value */
						mx_helpers_getDataTypesChoice(termDesc.datatype),
						details_termOnTypeChange,
						successCallbackType);
				termTypeNode.append(editabletermTypeNode);
			} else { termTypeNode.innerHTML=termDesc.datatype;}
		} else {
			termTypeNode.innerHTML=termDesc.datatype;
		}
		

		// field enumeration list (when datatype allows it)				
		let successCallbackEnumChange=function(fieldName,newValue) { 
			termDesc.enumsList=newValue.split(",");
		}		
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldEnumNode = xeditable_create_text_field(
						termName /* pk */,
						termName,false /*show termName*/,
						termDesc.enumsList /* cur value */,		
						details_termOnEnumsListChange,
						successCallbackEnumChange);
			fieldEnumNode.append(editableFieldEnumNode);
		} else { fieldEnumNode.innerHTML=termDesc.enumsList; }
		//console.log(termName+":"+termDesc.datatype);
		if (mx_helpers_isDatatypeEnumOk(termDesc.datatype)) {
			fieldEnumNode.style.display='block'; 
		}
		else { fieldEnumNode.style.display='none'; }
		
		// term multi-enum 
		let successCallbackMultiEnumChange=function(fieldName,newValue) { 
			//console.log("multi modified properly :"+fieldName+"="+newValue); 
		}		
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldMultiEnumNode = xeditable_create_boolean_field(
					termName /* pk */,
					termName,false /*show termName*/,
					termDesc.isMultiEnum /* cur value */,	
					details_termOnMultiEnumChange,
					successCallbackMultiEnumChange);
			fieldMultiEnumNode.append(editableFieldMultiEnumNode);
		} else {}
		if (mx_helpers_isDatatypeMultiEnumOk(termDesc.datatype)) { fieldMultiEnumNode.style.display='block'; }
		else { fieldMultiEnumNode.style.display='none'; }
		
		// comments
		let comments = newTermNode.querySelector("._term_comments_");
		if (termDesc.datatype=="LINK") {
			comments.innerHTML="enums. list shall be a search request (ex: 'type:person')";
		}
					
		termsInsertspot.appendChild(newTermNode);
	}
		 
 }
 
 function details_createTerm(termName,termDatatype) {
	 
 	MxApi.requestCreateTerm(_curCatalogDesc.id,termName,termDatatype);
	  
 }
  
 </script>
 
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
 
