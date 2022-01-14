<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 


<li id="leftbar_item_create" class="nav-item" style="display:none">
 <!--  Modal contents added by javascript in function "_builCreateNewItemForm" down there -->
        <a class="nav-link collapsed" href="#"
        	onclick="this.parentNode.querySelector('._modal_root_').toggleShowHide();">
          <i class="fas fa-magic fa-copy"></i>
          <span><s:text name="Items.createItem"></s:text> <s:property value='currentUserProfile.catalogVocabulary.itemCap'/></span>
        </a>
       
		<div class="_item_form_insert_spot_" ></div>       
 </li>

 <li id="leftbar_items_statistics" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseStatistics" aria-expanded="true" aria-controls="collapseStatistics">
          <i class="fas fa-fw fa-chart-pie"></i>
          <span><s:text name="Items.statistics"></s:text></span>
        </a>
        <div id="collapseStatistics" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
          <div class="mx-collapse py-2 collapse-inner rounded pt-4" style="overflow:auto;">             			 
	 		  <label  class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" >
			        <a class="collapsed" style="color:inherit;text-decoration:inherit" href="<s:property value='currentUserProfile.statisticsDiscoverUrl'/>" target="_blank" >
			          <i class="fas fa-sm fa-square-root-alt text-white"></i>
			          <span><s:text name="global.kibana"/></span>
			        </a>  
			        
			        <span title="S.O.S" 
		                	onclick="event.stopPropagation();event.preventDefault();
		                			MxGuiHeader.showInfoModal('<s:text name="help.items.kibana.title" />','<s:text name="help.items.kibana.body" />')">
		                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
		          </span>                    		
			  </label>
			  
						   
			 <label  class="_openBtn_ d-none d-sm-inline-block btn-big btn btn-info shadow-sm mx-left-button" >
				 <a href="#" class="collapsed" style="color:inherit;text-decoration:inherit"
			           	  onclick='document.getElementById("details-wrapper").classList.toggle("toggled");'>
			          <i class="fas fa-fw fa-chart-bar text-white"></i>
			          <span><s:text name="global.kibanaPanel"/></span>			                           		
			    </a>  
			    
			    <span title="S.O.S" 
		                	onclick="event.stopPropagation();event.preventDefault();
		                			MxGuiHeader.showInfoModal('<s:text name="help.items.kibana_panel.title" />','<s:text name="help.items.kibana_panel.body" />')">
		                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
		          </span>
			 </label>
			 
			
          </div>
        </div>
 </li>
 

 <li id="leftbar_items_csv_upload" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseCsv" aria-expanded="true" aria-controls="collapseCsv">
          <i class="fas fa-fw fa-file-upload"></i>
          <span><s:text name="Items.uploadItems"></s:text> <s:property value='currentUserProfile.catalogVocabulary.itemsCap'/></span>
        </a>
        <div id="collapseCsv" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
          <div class="mx-collapse py-2 collapse-inner rounded pt-4" style="overflow:auto">
	 		<s:include  value="io/csv_xls_ods.jsp" />
	 		<s:include  value="io/gexf.jsp" />
          </div>
          
            
	 		  
            
        </div>
 </li>
 
 
 <!-- div>
			 	<button id="sendCsvFileButton" onclick="event.stopPropagation();" >from CSV file</button> 
			 </div-->
 
 <li id="leftbar_filters_list" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTwoBis" aria-expanded="true" aria-controls="collapseTwoBis">
          <i class="fas fa-fw fa-search"></i>
          <span><s:text name='Items.filters' /></span>
        </a>
        <div id="collapseTwoBis" class="collapse _list_" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
        	<div class="mx-collapse py-2 collapse-inner rounded">
          		<h6 class="collapse-header"><s:property value='currentUserProfile.catalogVocabulary.filtersCap'/></h6>
          		<div id="leftbar_filters_list_insertSpot" class=" mx_collapse py-2 collapse-inner rounded" style=""></div>                        
          	</div>          
        </div>
 </li>
 
 <div id="leftbar_filter_template" class="mx_left_filter collapse-item small"  style="display:none" >
	<span class="_name_" ></span> 
	<button class="_button_delete_ btn btn-xs float-right" type="button" 
		onmouseover="this.classList.add('btn-danger');"
		onmouseout="this.classList.remove('btn-danger');"
		onclick="ws_handlers_requestDeleteFilter(this.parentNode.querySelector('._name_').innerHTML);"
		>
      <i class="fa fa-times fa-sm"></i>
    </button>
    
 	<div class="_query_" style="display:none;" >
 		<hr style="padding:0;margin:0;margin-top:0.2rem;">
 		<input class="_query_input_" type="text" value="" style="width:70%;margin-top:0.4rem;margin-bottom:0.2rem;"
 		onclick='event.stopPropagation();'
 		onfocus="this.parentNode.querySelector('._button_update_').style.display='inline-block';"
 		onchange="this.changed=true;"
 		onkeypress="if (event.which==13||event.keycode==13) {  			
 			ws_handlers_requestUpdateFilter(this.parentNode.parentNode.querySelector('._name_').innerHTML,
			this.parentNode.parentNode.querySelector('._query_input_').value);
 			}"
 		onblur="if (this.changed!=true) { this.parentNode.querySelector('._button_update_').style.display='none'; }">
 		
 		<button class="_button_update_ btn btn-xs" style="display:none" type="button" 
			onmouseover="this.classList.add('btn-success');"
			onmouseout="this.classList.remove('btn-success');"
			onclick="event.stopPropagation();				
					ws_handlers_requestUpdateFilter(this.parentNode.parentNode.querySelector('._name_').innerHTML,
						        					 this.parentNode.parentNode.querySelector('._query_input_').value);"
		>
		 <i class="fa fa-check fa-sm"></i>
		 </button>
 	</div>
 </div>

<s:include value="../commons/html/form_create_term.jsp"/>

<script type="text/javascript" >

// Title
function onclickCatalogName(event) { redirectToPage("${webAppBaseUrl}/Catalogs"); }
MxGuiLeftBar.setUpTitle("<s:property value='currentUserProfile.catalogVocabulary.name'/> ",onclickCatalogName);
MxGuiLeftBar.setTitle("<s:text name="Catalogs.configurationPanel" /> ",onclickCatalogName);
 
var createItemOp=null;

// call when catalog data has been received, so that we can know which
// menu shall be displayed or not depending on access rights
function _update_menus_list() {
	
	MxGuiLeftBar.clearOperations();
	
	// Operations
	if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)==true) {
		createItemOp = document.getElementById("leftbar_item_create").cloneNode(true);
		createItemOp.style.display="block";
		MxGuiLeftBar.addOperation(createItemOp);
	}
	
	if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)!=true) {
		csvUpload=document.getElementById("datafile_upload_label");
		csvUpload.parentNode.removeChild(csvUpload);		
	}	
	var uploadItemsOp = document.getElementById("leftbar_items_csv_upload").cloneNode(true);
	uploadItemsOp.style.display="block";
	
	MxGuiLeftBar.addOperation(uploadItemsOp);
	
	var statisticsOp = document.getElementById("leftbar_items_statistics").cloneNode(true);
	statisticsOp.style.display="block";
	MxGuiLeftBar.addOperation(statisticsOp);
	
	// Filters
	var filtersOp = document.getElementById("leftbar_filters_list").cloneNode(true);
	filtersOp.style.display="block";
	MxGuiLeftBar.addOperation(filtersOp);
}
function left_buildNewFilter(descr) {
	
	let newFilterNode=document.getElementById("leftbar_filter_template").cloneNode(true);	
	newFilterNode.style.display='block';
	newFilterNode.descr=descr;
	newFilterNode.isSelected=false;
	
	
	// name
	let nameNode=newFilterNode.querySelector("._name_");
	nameNode.innerHTML=descr.name;
	
	// query input
	let queryNode=newFilterNode.querySelector("._query_");
	let queryInputNode=newFilterNode.querySelector("._query_input_");
	queryInputNode.value=descr.query;
	
	newFilterNode.onclick=function(event) {		
		if (newFilterNode.isSelected) { newFilterNode.deselect(); }
		else { newFilterNode.select(); }
	}
	
	newFilterNode.select=function() {
		newFilterNode.isSelected=true;
		queryNode.style.display='block';
		newFilterNode.classList.add('mx_left_filter_selected');
		newFilterNode.classList.add('mx-selected-dropdown');
		MxGuiHeader.refreshSearch();
	}
	newFilterNode.deselect=function() {
		newFilterNode.isSelected=false;
		queryNode.style.display='none';
		newFilterNode.classList.remove('mx_left_filter_selected');
		newFilterNode.classList.remove('mx-selected-dropdown');
		MxGuiHeader.refreshSearch();
	}
	
	return newFilterNode
}
MxGuiLeftBar.getFiltersInsertSpot=function() { 
	return document.getElementById("leftbar_filters_list_insertSpot"); 
}
MxGuiLeftBar.buildNewFilter=left_buildNewFilter;
MxGuiLeftBar.openFiltersArea=function() {
	document.getElementById('leftbar_filters_list').querySelector("._list_").classList.add("show");
}
MxGuiLeftBar.setNbMatchingItems=function(nbMatchingItems) {
	let nbMatchNode=document.getElementById('MxGui.left.csvdownload.nbMatchDocs');	 
	 nbMatchNode.innerHTML=nbMatchingItems;	 
}

function _left_build_newitem_form_field_desc(termId,termDesc,catalogDesc) {
	
	let langName=catalogDesc.vocabulary.guiLanguageShortName;
	let fieldFormDef={ 	id:"form_newitem_"+termId, 
						termId:termId, 
						isMultiEnum:termDesc.isMultiEnum,
						important:"false",
						title:mx_helpers_getTermName(termDesc, catalogDesc)
						 };
	
	if (termDesc.datatype=="LINK_URL" || termDesc.datatype=="IMAGE_URL") {
		fieldFormDef.type="file-url";
		fieldFormDef.defaultValue="";
		fieldFormDef.values=termDesc.enumsList;
		fieldFormDef.values.unshift(" ");
		
	} 
	else if (termDesc.datatype=="LONG_TEXT") {
		fieldFormDef.type="longtext";
		fieldFormDef.defaultValue="";
	}
	else if (mx_helpers_isDatatypeMultiEnumOk(termDesc.datatype) && termDesc.enumsList!="" && termDesc.isMultiEnum==true) {
		fieldFormDef.type="multiselect";
		fieldFormDef.defaultValue="";
		fieldFormDef.values=termDesc.enumsList;
	}
	else if (mx_helpers_isDatatypeEnumOk(termDesc.datatype) && termDesc.enumsList!="") {
		fieldFormDef.type="dropdown";
		fieldFormDef.defaultValue="";
		fieldFormDef.values=termDesc.enumsList;
		fieldFormDef.values.unshift(" ");
		
	}
	else {
		fieldFormDef.type="text";		
		fieldFormDef.defaultValue="";
		if (termDesc.rawDatatype=="Tshort" 
				|| termDesc.rawDatatype=="Tinteger"
				|| termDesc.rawDatatype=="Tfloat") {
			fieldFormDef.datatype="number";	
		}		
	}
	return fieldFormDef;
	
}

function _builCreateNewItemForm(catalogDescr) {
	
	let fieldsList=[];
	
	// add an input form for each term of the catalog
	let sortedTermsNames = Object.keys(catalogDescr.terms).sort();
	for (var i=0;i<sortedTermsNames.length;i++) {
		let curFieldName=sortedTermsNames[i];
		let curFieldTermDescCopy=JSON.parse(JSON.stringify(catalogDescr.terms[curFieldName]));
		// if field is actually not a fieldName but a custom separator, we ignore it
		if (curFieldName[0]=='"') { continue; }		
		curFieldTermDescCopy.addedInForm=true;
		let curFieldFormDef =_left_build_newitem_form_field_desc(curFieldName/*id*/,curFieldTermDescCopy,catalogDescr)
		
		fieldsList.push(curFieldFormDef);		
	}
	// set as 'important' fields used to build the cards title or thumbnail
	// so that in the form they are cleared after each creation
	let fieldsInCatalogCardsTitleArray=catalogDescr.itemNameFields;
	if (fieldsInCatalogCardsTitleArray==null) { fieldsInCatalogCardsTitleArray=[]; }
	let cardThumbnailField=catalogDescr.itemThumbnailUrlField;
	for (var i=0;i<fieldsInCatalogCardsTitleArray.length;i++) {
		let curFieldName=fieldsInCatalogCardsTitleArray[i];
		for (var j=0;j<fieldsList.length;j++) {
			let curFormFieldName=fieldsList[j].termId;
			if (fieldsList[j].termId==curFieldName || fieldsList[j].termId==cardThumbnailField) { 
				fieldsList[j].important=true; 
			}
		}
	}
	
	let onValidFormCallback=function(itemFields,itemFilesToUpload) {
		
		let fieldsMap={};
		let filesList=[];
		for (var fieldFormId in itemFields) {
			// cleaning the id to retrieve the termId
			let termId=fieldFormId.replace("form_newitem_","");
			fieldsMap[termId]=itemFields[fieldFormId];
			
			// if some files have been listed for upload, we prepare the list
			if (itemFilesToUpload[fieldFormId]!=null) {
				for (var i=0;i<itemFilesToUpload[fieldFormId].length;i++) {
					filesList.push(itemFilesToUpload[fieldFormId][i]);	
				}
			}
		}		
		
		let onCreationSuccessCallback=function() {
			//console.log("item created!");
		}
		let onCreationFailureCallback=function(errorMsg) {
			footer_showAlert(ERROR, errorMsg);
		}
		
		if (filesList.length>0) {
			ws_handlers_uploadFiles(catalogDescr,filesList,onCreationSuccessCallback,onCreationFailureCallback);
		}
		ws_handlers_createItem(catalogDescr,fieldsMap,onCreationSuccessCallback,onCreationFailureCallback);		
	}
	
	let popupHeaderNode=document.createElement("div");
	let popupTitleNode=document.createElement("div");
	popupHeaderNode.append(popupTitleNode);
	popupTitleNode.innerHTML="<s:text name="Items.createItem"></s:text> <s:property value='currentUserProfile.catalogVocabulary.itemCap'/>";
	

	// add create-new-term inline form	
	function onSuccessCallback(createTermForm,termName,termType) {
		// refresh and redisplay the 'create item' popup once term created
		let innerSuccess=function(catalogDescr) {
			handleMxWsCatalogs(catalogDescr);
			document.getElementById("leftbar_item_create").querySelector('._modal_root_').toggleShowHide();
		}
		footer_showAlert(SUCCESS, "<s:text name="Catalogs.field.termCreated" />");
		MxApi.requestGetCatalogs({'catalogId':catalogDescr.id, 'successCallback':innerSuccess});
		
	}
	function onErrorCallback(msg) {
		footer_showAlert(ERROR, "<s:text name="Catalogs.field.unableToCreateTerm" /> : "+msg);
	}
	let createNewTermButton= mx_helpers_buildCreateNewTermForm(onSuccessCallback,onErrorCallback);
	popupHeaderNode.append(createNewTermButton);
	
	// create item modal
	let createItemForm=MxGuiPopups.newMultiInputsPopup(popupHeaderNode,
													fieldsList,onValidFormCallback,
													{keepPopupWhenPressOk:'true'});
	createItemForm.id="createItemForm";
	return createItemForm;
}

MxGuiLeftBar.handleCatalogDetails=function(catalogDescr) {
	
	
	_update_menus_list();
	let filtersInsertSpot=MxGuiLeftBar.getFiltersInsertSpot();
	let currentForm=document.getElementById("createItemForm");
	let showCreateItemPopup=false;
	if (currentForm!=null && currentForm.style.display!="none") { showCreateItemPopup=true; }
	
	clearNodeChildren(filtersInsertSpot);
	
	// generate "create new item" form
	if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)) {		
		let createNodeFormInsertSpot=createItemOp.querySelector("._item_form_insert_spot_");	
		let createItemForm=_builCreateNewItemForm(catalogDescr);
		createNodeFormInsertSpot.appendChild(createItemForm);
		
		// tmp 
		//cleanCreateFieldDatatypesList(document.getElementById("createTermForm"),onSuccessCallback,onErrorCallback,onClickCancelCallback);
		if (showCreateItemPopup) { createItemForm.toggleShowHide();}
	}
	
	
// add filters list
	if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)!=true) {
		let filterNodeTemplate=document.getElementById("leftbar_filter_template");
		let filterDeleteButton = filterNodeTemplate.querySelector('._button_delete_');
		filterDeleteButton.style.display='none';
		let filterQueryInput = filterNodeTemplate.querySelector('._query_input_');
		filterQueryInput.disabled=true;
		let filterQueryUpdate = filterNodeTemplate.querySelector('._button_update_');
		filterQueryUpdate.style.display='none';		
	}
	// not applied because after insertion (and clone) in the left menu
	// TODO
	for (i=0;i< catalogDescr.filters.length;i++) {
		let curFilterDescr=catalogDescr.filters[i];
		let newFilterNode = left_buildNewFilter(curFilterDescr);
		filtersInsertSpot.append(newFilterNode);
	}
	
}

// return array of queries corresponding to selected filters
MxGuiLeftBar.getSelectedFiltersNames=function() {
	var result = [];
	let filtersInsertSpot=MxGuiLeftBar.getFiltersInsertSpot();
	for (var curFilter=filtersInsertSpot.firstChild;curFilter!==null;curFilter=curFilter.nextElementSibling) {		
		if (typeof(curFilter)!='object') { continue; }
		if (curFilter.isSelected) { 
			result.push(curFilter.descr.name); 
		}
	}
	return result;
}


</script>
