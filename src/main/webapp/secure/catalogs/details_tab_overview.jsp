<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 		  
 <script type="text/javascript" >
 


 function details_customOnThumbnailUrlChange(pk,fieldName,newValue,successCallback, errorCallback){	 
	
	 // Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result	 
	 MxApi.requestCustomizeCatalog({
		 	  "catalogId":_curCatalogDesc.id,
		 	  "thumbnailUrl":newValue,
			  "itemsUrlPrefix":_curCatalogDesc.itemsUrlPrefix,
			  "itemNameFields":_curCatalogDesc.itemNameFields, 
			  "itemThumbnailUrlField":_curCatalogDesc.itemThumbnailUrlField,
			  "perspectiveMatchField":_curCatalogDesc.perspectiveMatchField,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });	
}
 
 function details_customOnUrlPrefixChange(pk,fieldName,newValue,successCallback, errorCallback){	 
	
	 // Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result	 
	 MxApi.requestCustomizeCatalog({
		 	  "catalogId":_curCatalogDesc.id,
		 	  "thumbnailUrl":_curCatalogDesc.thumbnailUrl,
			  "itemsUrlPrefix":newValue,
			  "itemNameFields":_curCatalogDesc.itemNameFields, 
			  "itemThumbnailUrlField":_curCatalogDesc.itemThumbnailUrlField,
			  "perspectiveMatchField":_curCatalogDesc.perspectiveMatchField,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });
}
 
 function details_customOnItemNameFieldsChange(pk,fieldName,newValue,successCallback, errorCallback){	 

	 // Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result	 
	 MxApi.requestCustomizeCatalog({
		 	  "catalogId":_curCatalogDesc.id,
		 	  "thumbnailUrl":_curCatalogDesc.thumbnailUrl,
			  "itemsUrlPrefix":_curCatalogDesc.itemsUrlPrefix,
			  "itemNameFields":newValue.split(","), 
			  "itemThumbnailUrlField":_curCatalogDesc.itemThumbnailUrlField,
			  "perspectiveMatchField":_curCatalogDesc.perspectiveMatchField,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });
	 
	
}
 
 function details_customOnItemThumbnailUrlChange(pk,fieldName,newValue,successCallback, errorCallback){	 
	// Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result	 
	 MxApi.requestCustomizeCatalog({
		 	  "catalogId":_curCatalogDesc.id,
		 	  "thumbnailUrl":_curCatalogDesc.thumbnailUrl,
			  "itemsUrlPrefix":_curCatalogDesc.itemsUrlPrefix,
			  "itemNameFields":_curCatalogDesc.itemNameFields, 
			  "itemThumbnailUrlField":newValue,
			  "perspectiveMatchField":_curCatalogDesc.perspectiveMatchField,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });
}
 
 function details_customOnPerspectiveMatchFieldChange(pk,fieldName,newValue,successCallback, errorCallback){	 
	// Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result	 
	 MxApi.requestCustomizeCatalog({
		 	  "catalogId":_curCatalogDesc.id,
		 	  "thumbnailUrl":_curCatalogDesc.thumbnailUrl,
			  "itemsUrlPrefix":_curCatalogDesc.itemsUrlPrefix,
			  "itemNameFields":_curCatalogDesc.itemNameFields, 
			  "itemThumbnailUrlField":_curCatalogDesc.itemThumbnailUrlField,
			  "perspectiveMatchField":newValue,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });
}
 
 // function used by details.jsp:details_buildContents
 function details_buildContents_overview(newPopulatedCatalogDetails,catalogCard) {	 

		// index name
		let indexName = newPopulatedCatalogDetails.querySelector("._index_name_");
		indexName.innerHTML=catalogCard.descr.name;
	
		// access rights
		let accessRights = newPopulatedCatalogDetails.querySelector("._access_rights_");
		accessRights.innerHTML=catalogCard.descr.userAccessRightsStr;
	
		if (!mx_helpers_isCatalogWritable(catalogCard.descr.userAccessRights)) {
			let quotaNbDocs=newPopulatedCatalogDetails.querySelector("._quota_nb_docs_row_");
			let quotaDiscSpace=newPopulatedCatalogDetails.querySelector("._quota_disc_space_row_");
			quotaNbDocs.parentNode.removeChild(quotaNbDocs);
			quotaDiscSpace.parentNode.removeChild(quotaDiscSpace);
		}
		else {
			// quotas nb docs
			let quotaNbDocs=newPopulatedCatalogDetails.querySelector("._quota_nb_docs_");
			let maxNbDocs=catalogCard.descr.quotaNbDocs;
			let curNbDocs=catalogCard.descr.nbDocuments;
			let usagePourcentNbDocs=(curNbDocs*100)/maxNbDocs;
			usagePourcentNbDocs=Math.round(usagePourcentNbDocs * 100) / 100;
			let pourcentClass="";
			if (usagePourcentNbDocs>95) { pourcentClass="alert-danger" }		
			else if (usagePourcentNbDocs>85)  { pourcentClass="alert-warning"; }
			else pourcentClass="";		
			quotaNbDocs.innerHTML="<span class=\""+pourcentClass+"\" ><b>"+curNbDocs+ "</b> <i>/ "+maxNbDocs+" ("+usagePourcentNbDocs+"%)</i></span>";
			
			// quotas disc space
			let quotaDiscSpace=newPopulatedCatalogDetails.querySelector("._quota_disc_space_");
			let currentUseMBytes=catalogCard.descr.discSpaceUseBytes/1000000;
			let maxUseSpaceMBytes=catalogCard.descr.quotaFtpDiscSpaceBytes/1000000;
			let usagePourcentDiscSpace=100;
			if (maxUseSpaceMBytes>0) { usagePourcentDiscSpace=(currentUseMBytes*100)/maxUseSpaceMBytes; }
			
			// rounding values do 2 decimals
			currentUseMBytes=Math.round(currentUseMBytes * 100) / 100;
			maxUseSpaceMBytes=Math.round(maxUseSpaceMBytes * 100) / 100;
			usagePourcentDiscSpace=Math.round(usagePourcentDiscSpace * 100) / 100;
			pourcentClass="";
			
			if (usagePourcentDiscSpace>95) { pourcentClass="alert-danger" }		
			else if (usagePourcentDiscSpace>85)  { pourcentClass="alert-warning"; }
			else pourcentClass="";		
			quotaDiscSpace.innerHTML="<span class=\""+pourcentClass+"\" ><b>"+currentUseMBytes+ "MB</b> <i> / "+maxUseSpaceMBytes+"MB ("+usagePourcentDiscSpace+"%</i>)</span>";
		}
		
		// ftp port : no FTP access for Read-Only users
		if (!mx_helpers_isCatalogWritable(catalogCard.descr.userAccessRights)) {
			let ftpPortRow = newPopulatedCatalogDetails.querySelector("._ftp_row_");
			ftpPortRow.parentNode.removeChild(ftpPortRow);
		} else {
			let ftpPort = newPopulatedCatalogDetails.querySelector("._ftp_port_");
			if (catalogCard.descr.ftpPort==-1) { ftpPort.innerHTML="-"; }
			else { ftpPort.innerHTML=catalogCard.descr.ftpPort; }
		}
		
		// thumbnail url
		let thumbnailUrl = newPopulatedCatalogDetails.querySelector("._thumbnail_url_");
		let successCallbackThumbnailUrlChange=function(fieldName,newValue) {
			catalogCard.descr.thumbnailUrl=newValue;			
		}		
		
		// thumbnail url
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldThumbnailUrlNode = xeditable_create_text_field(
					'thumbnailUrl' /* pk */,
					'Thumbnail-URL',false /*show Name*/,
					catalogCard.descr.thumbnailUrl /* cur value */,		
					details_customOnThumbnailUrlChange,
					successCallbackThumbnailUrlChange);
			thumbnailUrl.append(editableFieldThumbnailUrlNode);
		} else { thumbnailUrl.innerHTML=catalogCard.descr.thumbnailUrl; }
		
		// url prefix
		let urlPrefix = newPopulatedCatalogDetails.querySelector("._url_prefix_");
		let successCallbackUrlPrefixChange=function(fieldName,newValue) {
			catalogCard.descr.itemsUrlPrefix=newValue;			
		}	
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldUrlPrefixNode = xeditable_create_text_field(
					'itemsUrlPrefix' /* pk */,
					'Prefix-URL',false /*show Name*/,
					catalogCard.descr.itemsUrlPrefix /* cur value */,		
					details_customOnUrlPrefixChange,
					successCallbackUrlPrefixChange);
			urlPrefix.append(editableFieldUrlPrefixNode);			
		} else { urlPrefix.innerHTML=catalogCard.descr.itemsUrlPrefix; }
		
		// items name
		let itemNameFields = newPopulatedCatalogDetails.querySelector("._items_name_fields_");
		let successCallbackItemNameFieldsChange=function(fieldName,newValue) {
			catalogCard.descr.itemNameFields=newValue.split(","); 
		}	
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldItemNameFieldsNode = xeditable_create_text_field(
					'itemNameFields' /* pk */,
					'Items-Name-Fields',false /*show Name*/,
					catalogCard.descr.itemNameFields /* cur value */,		
					details_customOnItemNameFieldsChange,
					successCallbackItemNameFieldsChange);
			itemNameFields.append(editableFieldItemNameFieldsNode);	
		} else { itemNameFields.innerHTML=catalogCard.descr.itemNameFields; }
		
		// items thumbnail URL
		let itemsUrlField = newPopulatedCatalogDetails.querySelector("._items_url_field_");
		let successCallbackItemThumbnailUrlChange=function(fieldName,newValue) {
			catalogCard.descr.itemThumbnailUrlField=newValue;
		}		
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldItemThumbnailUrlNode = xeditable_create_text_field(
					'itemThumbnailUrl' /* pk */,
					'Items-Thumbnail-Url',false /*show Name*/,
					catalogCard.descr.itemThumbnailUrlField /* cur value */,		
					details_customOnItemThumbnailUrlChange,
					successCallbackItemThumbnailUrlChange);
			itemsUrlField.append(editableFieldItemThumbnailUrlNode);
		} else { itemsUrlField.innerHTML=catalogCard.descr.itemThumbnailUrlField; }
		
		// perspetive Match field : used to detect automatically which perspective to activate
		// on which document : used if perspective name matches contents of field given here
		let perspectiveMatchField = newPopulatedCatalogDetails.querySelector("._perspective_match_field_");
		let successCallbackPerspectiveMatchFieldChange=function(fieldName,newValue) {
			catalogCard.descr.perspectiveMatchField=newValue;
		}		
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldItemThumbnailUrlNode = xeditable_create_text_field(
					'itemThumbnailUrl' /* pk */,
					'Items-Thumbnail-Url',false /*show Name*/,
					catalogCard.descr.perspectiveMatchField /* cur value */,		
					details_customOnPerspectiveMatchFieldChange,
					successCallbackPerspectiveMatchFieldChange);
			perspectiveMatchField.append(editableFieldItemThumbnailUrlNode);
		} else { perspectiveMatchField.innerHTML=catalogCard.descr.perspectiveMatchField; }
		 
 }
   
 </script>
 
 
