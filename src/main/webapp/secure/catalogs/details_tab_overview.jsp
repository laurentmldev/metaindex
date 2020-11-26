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
			  "timeFieldTermId":_curCatalogDesc.timeFieldTermId,
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
			  "timeFieldTermId":_curCatalogDesc.timeFieldTermId,
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
			  "timeFieldTermId":_curCatalogDesc.timeFieldTermId,
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
			  "timeFieldTermId":_curCatalogDesc.timeFieldTermId,
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
			  "timeFieldTermId":_curCatalogDesc.timeFieldTermId,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });
}
 
 
 
 function details_customTimeFieldNameChange(pk,fieldName,newValue,successCallback, errorCallback){	 
	
	 // Async request for value update
 	 // A message will be sent back to inform success or not of operation,
 	 // then MxApi will invoke success/error callback depending on returned result	 
	 MxApi.requestCustomizeCatalog({
		 	  "catalogId":_curCatalogDesc.id,
		 	  "thumbnailUrl":_curCatalogDesc.thumbnailUrl,
			  "itemsUrlPrefix":_curCatalogDesc.itemsUrlPrefix,
			  "itemNameFields":_curCatalogDesc.itemNameFields, 
			  "itemThumbnailUrlField":_curCatalogDesc.itemThumbnailUrlField,
			  "perspectiveMatchField":_curCatalogDesc.perspectiveMatchField,
			  "timeFieldTermId":newValue,
			  "successCallback":successCallback,
		 	  "errorCallback":errorCallback
	 });
}
 
function _refreshItemsNames_options(dropdown,curItemsNamesStr,catalogCard) {
	dropdown.innerHTML="";
	let option = document.createElement("option");
	option.value="";
	option.innerHTML="- <s:text name="Catalogs.overview.cardsTitles.addNew" /> -";
	dropdown.appendChild(option);
	let optionClear = document.createElement("option");
	optionClear.value="__CLEAR__";
	optionClear.innerHTML="* <s:text name="Catalogs.overview.cardsTitles.clear" /> *";
	dropdown.appendChild(optionClear);
	
	let sortedTermsNames = Object.keys(catalogCard.descr.terms).sort();			
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];
		let termDescr = catalogCard.descr.terms[termName];
		let termId=termDescr.id;
		let datatype=termDescr.datatype;
		termTranslation=mx_helpers_getTermName(termDescr, catalogCard.descr);
		
		let regex=new RegExp("(^|,)"+termName+"(,|$)","g");		
		if (datatype!="IMAGE_URL" && datatype!="PAGE_URL" && datatype!="LINK"
				&& curItemsNamesStr.match(regex)==null
			) {		
			let option = document.createElement("option");
			option.value=termName;
			option.innerHTML=termTranslation;
			option.id="itemsName.option.termName";
			dropdown.appendChild(option);
		}
	}
	
	let optionSepComma = document.createElement("option");
	optionSepComma.value="\" , \"";
	optionSepComma.innerHTML=" , ";
	dropdown.appendChild(optionSepComma);
	
	let optionSepDash = document.createElement("option");	
	optionSepDash.value="\" - \"";
	optionSepDash.innerHTML=" - ";
	dropdown.appendChild(optionSepDash);
	
	let optionSepSlash = document.createElement("option");	
	optionSepSlash.value="\" \/ \"";
	optionSepSlash.innerHTML=" \/ ";
	dropdown.appendChild(optionSepSlash);
	
}
 
 function _makeUpgradePlanButtton(id) {
	 let buttonPlanUpgrade=document.createElement("a");
		buttonPlanUpgrade.id=id;
		buttonPlanUpgrade.classList="btn-big btn btn-sm btn-warning shadow-sm";
		buttonPlanUpgrade.style="font-size:0.8rem;margin-left:2rem;color:white !important;margin-top:0.3rem;";
		buttonPlanUpgrade.innerHTML="<span style='font-weight:bold;font-size:0.8rem;padding:1rem'><s:text name="Catalogs.left.getMoreCatalogs" /></span>";
		buttonPlanUpgrade.onclick=function(e) { document.getElementById(MX_HEADER_PLANS_POPUP_ID).toggleShowHide(); }
		return buttonPlanUpgrade;
 }
 // function used by details.jsp:details_buildContents
 function details_buildContents_overview(newPopulatedCatalogDetails,catalogCard) {	 

	// index name + owner and plan
		let indexName = newPopulatedCatalogDetails.querySelector("._index_name_");
		let ownerName= catalogCard.descr.ownerName;
		let planName= catalogCard.descr.planName;
		indexName.innerHTML=catalogCard.descr.name
			+"<span style='font-size:0.6rem;padding-left:3rem;' > <s:text name="global.ownedBy" /> <b>"+ownerName+"</b>"
			+" <s:text name="global.withPlan" />"+" <b>"+planName+"</b></span>";
	
	// access rights
		let accessRights = newPopulatedCatalogDetails.querySelector("._access_rights_");
		accessRights.innerHTML=catalogCard.descr.userAccessRightsStr;
	
		if (!mx_helpers_isCatalogWritable(catalogCard.descr.userAccessRights)) {
			let quotaNbDocs=newPopulatedCatalogDetails.querySelector("._quota_nb_docs_row_");
			let quotaDrive=newPopulatedCatalogDetails.querySelector("._quota_drive_row_");
			quotaNbDocs.parentNode.removeChild(quotaNbDocs);
			quotaDrive.parentNode.removeChild(quotaDrive);
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
			
			let quotaNbDocsProgressBarNode=document.getElementById("progressbar-quota-template").cloneNode(true);			
			quotaNbDocs.append(quotaNbDocsProgressBarNode);
			quotaNbDocsProgressBarNode.id="progressbars.nbdocsusage";
			
	    	let pourcentageNode = quotaNbDocsProgressBarNode.querySelector("._pourcentage_");
	    	pourcentageNode.style.width=usagePourcentNbDocs+"%";
	    	
	    	let textNode = quotaNbDocsProgressBarNode.querySelector("._text_");	    	
	    	textNode.innerHTML="<div class=\""+pourcentClass+"\" >"
	    					+"<span style=\"font-size:0.8rem;\">"+curNbDocs
	    							+"<i> / "
									+maxNbDocs+"</i></span> "
									+"<span style=\"font-weight:bold;\">"
									+usagePourcentNbDocs+"%</span></div>";
	    	
			quotaNbDocsProgressBarNode.style.display='block';
	    
	    	// adapt color to usage pourcentage
	    	if (usagePourcentNbDocs>=80 ) {
    			pourcentageNode.classList.add('bg-warning');
    		} else if (usagePourcentNbDocs>=95) {
    			pourcentageNode.classList.add('bg-failure');
	    	}
	    	
			// add a "upgrade plan" button if limit is reached and current user is catalog's owner
			if (usagePourcentNbDocs>=80
					&& catalogCard.descr.ownerId==<s:property value="currentUserProfile.id"/>) {
				quotaNbDocs.appendChild(_makeUpgradePlanButtton("details_overview_upgrade_quotaNbDocs"));
			}
			
	// quotas drive space
			let quotaDrive=newPopulatedCatalogDetails.querySelector("._quota_drive_");
			let currentUseBytes=catalogCard.descr.driveUseBytes;
			let currentUseMBytes=currentUseBytes/1000000;
			if (currentUseBytes==0) { currentUseMBytes=0;} // avoid weird numbers when rounding occurs
			let maxUseSpaceBytes=catalogCard.descr.quotaDriveBytes;
			let maxUseSpaceMBytes=maxUseSpaceBytes/1000000;
			let usagePourcentDriveSpace=100;
			if (maxUseSpaceBytes>0) { usagePourcentDriveSpace=(currentUseMBytes*100)/maxUseSpaceMBytes; }
			
			// rounding values do 2 decimals
			currentUseMBytes=Math.round(currentUseMBytes * 100) / 100;
			maxUseSpaceMBytes=Math.round(maxUseSpaceMBytes * 100) / 100;
			usagePourcentDriveSpace=Math.round(usagePourcentDriveSpace * 100) / 100;
			if (currentUseMBytes==0) {usagePourcentDriveSpace=0; }
			if (usagePourcentDriveSpace>100) { usagePourcentDriveSpace=100; }
			pourcentClass="";
			
			if (usagePourcentDriveSpace>95) { pourcentClass="alert-danger" }		
			else if (usagePourcentDriveSpace>80)  { pourcentClass="alert-warning"; }
			else pourcentClass="";	
			
			let quotaProgressBarNode=document.getElementById("progressbar-quota-template").cloneNode(true);			
			quotaDrive.append(quotaProgressBarNode);
			quotaProgressBarNode.id="progressbars.driveusage";
			
	    	pourcentageNode = quotaProgressBarNode.querySelector("._pourcentage_");
	    	pourcentageNode.style.width=usagePourcentDriveSpace+"%";
	    	
	    	textNode = quotaProgressBarNode.querySelector("._text_");	    	
	    	textNode.innerHTML="<div class=\""+pourcentClass+"\" >"
	    					+"<span style=\"font-size:0.8rem;\">"+currentUseMBytes+ "MB"
	    							+"<i> / "
									+maxUseSpaceMBytes+"MB</i></span> "
									+"<span style=\"font-weight:bold;\">"
									+usagePourcentDriveSpace+"%</span></div>";
	    	
			quotaProgressBarNode.style.display='block';
	    
	    	// adapt color to usage pourcentage
	    	if (usagePourcentDriveSpace>=80 ) {
    			pourcentageNode.classList.add('bg-warning');
    		} else if (usagePourcentDriveSpace>=95) {
    			pourcentageNode.classList.add('bg-failure');
	    	}
			
			// add a "upgrade plan" button if limit is reached and current user is catalog's owner			
			if (usagePourcentDriveSpace>=85 
					&& catalogCard.descr.ownerId==<s:property value="currentUserProfile.id"/>) {
				console.log("ARGGG");
				quotaDrive.appendChild(_makeUpgradePlanButtton("details_overview_upgrade_quotaDrive"));
			}
		}	
		
	// catalog thumbnail url
		let thumbnailUrl = newPopulatedCatalogDetails.querySelector("._thumbnail_url_");
		let successCallbackThumbnailUrlChange=function(fieldName,newValue) {
			catalogCard.descr.thumbnailUrl=newValue;			
		}		
	
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
			//console.log("fieldName="+fieldName+" newValue="+newValue);
			catalogCard.descr.itemNameFields=newValue.split(","); 
		}	
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let editableFieldItemNameFieldsNode = document.createElement("span");
			editableFieldItemNameFieldsNode.innerHTML=catalogCard.descr.itemNameFields;
				/*
				xeditable_create_text_field(
					'itemNameFields',// pk ,
					'Items-Name-Fields',false, //show Name
					catalogCard.descr.itemNameFields, // cur value		
					details_customOnItemNameFieldsChange,
					successCallbackItemNameFieldsChange);
				*/
			itemNameFields.append(editableFieldItemNameFieldsNode);	
			editableFieldItemNameFieldsNode.id="globals.overview.itemsNames.text";
			
			let dropdown = document.createElement("select");
			dropdown.id="globals.overview.itemsNames.dropdown";
			dropdown.classList.add("form-control");
			dropdown.classList.add("form-control-sm");
			
			dropdown.style.width="auto";			
			let textValueNode=editableFieldItemNameFieldsNode;//.querySelector("._value_");
			dropdown.onchange=function(event) {
				let termName=dropdown.options[dropdown.selectedIndex].value;
				let curValue=termName;				
				if (curValue==null || curValue=="") { return; }
				
				if (curValue=="__CLEAR__") {
					curValue="";
				} 
				
				else if (textValueNode.innerHTML!="Empty" && textValueNode.innerHTML.length>0) {
					curValue = textValueNode.innerHTML+","+curValue;
				}
				
				details_customOnItemNameFieldsChange(
								"itemNameFields",
								'Items-Name-Fields',
								curValue,
								function() { 
									let newStrValue=curValue;
									successCallbackItemNameFieldsChange("itemNameFields",curValue); 
									if (curValue=="") { 
										newStrValue="Empty";
										textValueNode.classList.add("editable-empty");
									} else {
										textValueNode.classList.remove("editable-empty");
									}
									textValueNode.innerHTML=newStrValue;									
									bgTransit(textValueNode);
									dropdown.value="";
									_refreshItemsNames_options(dropdown,curValue,catalogCard);																		
								},
								function() { console.log("ERROR in dropdown"); })
								
			}
						
			let itemNameFieldsDropdownNode = newPopulatedCatalogDetails.querySelector("._items_name_fields_dropdown_");
			itemNameFieldsDropdownNode.append(dropdown);
			_refreshItemsNames_options(dropdown,textValueNode.innerHTML,catalogCard);
			
		} else { itemNameFields.innerHTML=catalogCard.descr.itemNameFields; }
		
		
	// items thumbnail URL
		let itemsUrlField = newPopulatedCatalogDetails.querySelector("._items_url_field_");
		let successCallbackItemThumbnailUrlChange=function(fieldName,newValue) {
			catalogCard.descr.itemThumbnailUrlField=newValue;
		}		
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			let choicesDef = [ { value:"", text:""} ];
			let sortedTermsNames = Object.keys(catalogCard.descr.terms).sort();			
			for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
				let termName=sortedTermsNames[termIdx];
				let termDescr = catalogCard.descr.terms[termName];
				let termId=termDescr.id;
				let datatype=termDescr.datatype;
				termTranslation=mx_helpers_getTermName(termDescr, catalogCard.descr);
				if (datatype=="IMAGE_URL") {		
					choicesDef.push({ value:termName, text:termTranslation});
				}
			}
			
			let editableFieldItemThumbnailUrlNode = xeditable_create_dropdown_field(
					'itemThumbnailUrl' /* pk */,
					'Items-Thumbnail-Url',false /*show Name*/,
					catalogCard.descr.itemThumbnailUrlField /* cur value */,	
					choicesDef,
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
			
			let choicesDef = [ { value:"", text:""} ];
			let sortedTermsNames = Object.keys(catalogCard.descr.terms).sort();			
			for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
				let termName=sortedTermsNames[termIdx];
				let termDescr = catalogCard.descr.terms[termName];
				let termId=termDescr.id;
				termTranslation=mx_helpers_getTermName(termDescr, catalogCard.descr);				
				// we use termName rather termId because we might want later
				// use moe complex expression to be interpreted, like
				// 'date>1980' or so
				choicesDef.push({ value:termName, text:termTranslation});				
			}
			
			let editableFieldItemThumbnailUrlNode = xeditable_create_dropdown_field(
					'itemThumbnailUrl' /* pk */,
					'Items-Thumbnail-Url',false /*show Name*/,
					catalogCard.descr.perspectiveMatchField /* cur value */,	
					choicesDef,
					details_customOnPerspectiveMatchFieldChange,
					successCallbackPerspectiveMatchFieldChange);
			perspectiveMatchField.append(editableFieldItemThumbnailUrlNode);
		} else { perspectiveMatchField.innerHTML=catalogCard.descr.perspectiveMatchField; }
		 
	// Kibana TimeField change
		let kibanaTimeField = newPopulatedCatalogDetails.querySelector("._kibana_time_field_");
		let successCallbackKibanaTimeFieldChange=function(fieldName,newValue) {
			catalogCard.descr.timeFieldTermId=newValue;
		}		
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			
			let choicesDef = [ { value:0, text:"<s:text name="Catalogs.overview.lastUpdateTimestamp"/>"} ];
			let sortedTermsNames = Object.keys(catalogCard.descr.terms).sort();			
			for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
				let termName=sortedTermsNames[termIdx];
				let termDescr = catalogCard.descr.terms[termName];
				let termId=termDescr.id;
				let termRawType=termDescr.rawDatatype;
				termTranslation=mx_helpers_getTermName(termDescr, catalogCard.descr);
				if (termRawType=="Tdate") {
					choicesDef.push({ value:termId, text:termTranslation});
				}
			}
			
			let editableFieldTiemFieldNode = xeditable_create_dropdown_field(
					'kibanatimeField' /* pk */,
					'Kibana-TimeField',false /*show Name*/,
					catalogCard.descr.timeFieldTermId /* cur value */,
					choicesDef /* dropdown choices  */,
					details_customTimeFieldNameChange,
					successCallbackKibanaTimeFieldChange);
			kibanaTimeField.append(editableFieldTiemFieldNode);
			
		} else {
			let termTranslation = "<s:text name="Catalogs.overview.lastUpdateTimestamp"/>";
			if (catalogCard.descr.timeFieldTermId!=0) {
				for (var termName in catalogCard.descr.terms) {
					let termDescr = catalogCard.descr.terms[termName];
					if (termDescr.id==catalogCard.descr.timeFieldTermId) {
						termTranslation=mx_helpers_getTermName(termDescr, catalogCard.descr);
						break;
					}
				}
			}
			kibanaTimeField.innerHTML=termTranslation;
		}
		 
 }
   
 </script>
 
<div id="progressbar-quota-template" class="progress mx-progress" 
		style="display:none;margin:0;width:40%;background:white;" >
  <div class="progress-bar bg-success _pourcentage_" style="width:0%;">
  	<span class="_text_ mx-progress-bar-quota-contents" style="padding-left:0.2em;"></span>
  </div>
 </div>
  
 <div class="tab-pane fade show active" id="nav-overview" role="tabpanel" aria-labelledby="nav-overview-tab">
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
						        <td  style="font-style:italic"><s:text name="Catalogs.overview.quotaTitleNbDocs" />
						        	<span title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.quota_nbdocs.title" />','<s:text name="help.catalog.overview.quota_nbdocs.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td  style="font-style:italic" class="_quota_nb_docs_"></td>
						      </tr>
						      <tr class="_quota_drive_row_">
						        <td  style="font-style:italic"><s:text name="Catalogs.overview.quotaTitleDrive" />
						        	<span title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.quota_drive.title" />','<s:text name="help.catalog.overview.quota_drive.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td  style="font-style:italic" class="_quota_drive_"></td>
						       </tr>
						        
						       <tr>
						        <td ><s:text name="Catalogs.overview.thumbnailUrl" />
						        	<span title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.thumbnail_url.title" />','<s:text name="help.catalog.overview.thumbnail_url.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td class="_thumbnail_url_"></td>						        
						      </tr>
						      
						      <tr>
						        <td><s:text name="Catalogs.overview.cardsTitles" />
						        	<span class=""  title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.cards_title.title" />','<s:text name="help.catalog.overview.cards_title.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td><table><tr style="background:none;border:none;">
						        					<td style="border:none;padding-left:0;padding-top:0;padding-bottom:0;" class="_items_name_fields_"></td>
						        					<td style="border:none;padding-top:0;padding-bottom:0;" class="_items_name_fields_dropdown_"></td>
						        			</tr>
						        	</table>
						        </td>						        
						      </tr>
						      <tr>
						        <td ><s:text name="Catalogs.overview.cardsThumbnailField" />
						        	<span class=""  title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.cards_thumbnail.title" />','<s:text name="help.catalog.overview.cards_thumbnail.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td class="_items_url_field_"></td>						        
						      </tr>
						      <tr>
						        <td><s:text name="Catalogs.overview.perspectiveMatchField" />
						        	<span class=""  title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.detect_perspective_on_field.title" />','<s:text name="help.catalog.overview.detect_perspective_on_field.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td class="_perspective_match_field_"></td>						        
						      </tr>
						       <tr>
						        <td><s:text name="Catalogs.overview.timeField" />
						        	<span class=""  title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.kibana_timefield.title" />','<s:text name="help.catalog.overview.kibana_timefield.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td class="_kibana_time_field_"></td>						        
						      </tr>						        						      						      	
						      <tr>
						        <td ><s:text name="Catalogs.overview.urlsPrefix" />
						        	<span class=""  title="S.O.S" 
						                	onclick="event.stopPropagation();event.preventDefault();
						                			MxGuiHeader.showInfoModal('<s:text name="help.catalog.overview.urls_prefix.title" />','<s:text name="help.catalog.overview.urls_prefix.body" />')">
						                   <i class="mx-help-icon far fa-question-circle" style=""></i>    
						             </span>
						        </td>
						        <td class="_url_prefix_"></td>						        
						      </tr>		      
						    </tbody>
						  </table>						  						
					</div>
