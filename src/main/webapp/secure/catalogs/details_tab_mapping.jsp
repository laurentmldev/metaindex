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
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)
				// dynamic enums are read-only in the GUI, because loaded from server.
				// Typically used for RELATION fields.
				// In this case, enumeration column is used to show the names
				// of parent/child relations
				 && !mx_helpers_isDatatypeDynamicEnumOk(termDesc.datatype)) {
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
		if (termDesc.datatype=="RELATION") {
			comments.innerHTML="only one such field per catalog."
			details_tab_mapping_relationFieldAlreadyUsed=true;
		}
		else if (termDesc.datatype=="REFERENCE") {
			comments.innerHTML="enums. list shall be a search request (ex: 'type:person')";
		}
					
		termsInsertspot.appendChild(newTermNode);
	}
		 
 }
 
 function details_createTerm(termName,termDatatype) {
	 
	 // term datatype 'relation' need extra-information : name of parent/child roles in the relation
	 if (termDatatype=="RELATION") {
		 let fieldsList = [ { id:"parent",type:"text",title:"Parent Relation Name",defaultValue:"", important:'true', disabled:'false' },
			 				{ id:"child", type:"text",title:"Child Relation Name",defaultValue:"", important:'true', disabled:'false' }
			 				];
		 let popup = MxGuiPopups.newMultiInputsPopup("Relation Details",
				 						fieldsList,
				 						function(resultFields){
			 								MxApi.requestCreateTerm(_curCatalogDesc.id,termName,termDatatype,resultFields); 
			 							});
		 document.getElementById("wrapper").appendChild(popup);
		 
		 popup.show();
	 }
 
 	 // default
	 else {
		 MxApi.requestCreateTerm(_curCatalogDesc.id,termName,termDatatype);
	 }
	 
 }
  
 </script>
 
 
 
