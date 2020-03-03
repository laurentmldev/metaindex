<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<c:url value="/" var="mxurl"/>

<s:include  value="vegagen_forms/vegagen_graphs.jsp" />



 <li id="leftbar_items_statistics" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseStatistics" aria-expanded="true" aria-controls="collapseStatistics">
          <i class="fas fa-fw fa-chart-pie"></i>
          <span><s:text name="Items.statistics"></s:text></span>
        </a>
        <div id="collapseStatistics" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
          <div class="mx-collapse py-2 collapse-inner rounded pt-4" style="overflow:auto;">             			 
	 		  <label  class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button" >
			        <a class="collapsed" href="<s:property value='currentUserProfile.statisticsDiscoverUrl'/>" target="_blank" >
			          <i class="fas fa-sm fa-square-root-alt text-white"></i>
			          <span><s:text name="global.kibana"/></span>
			        </a>                      		
			  </label>
			  
						   
			 <label  class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button" >
				 <a href="#" class="collapsed" 
			           	  onclick='document.getElementById("details-wrapper").classList.toggle("toggled");'>
			          <i class="fas fa-fw fa-chart-bar text-white"></i>
			          <span><s:text name="global.kibanaPanel"/></span>			                           		
			    </a>  
			 </label>
			 
			  <label  class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button" >
				 <a href="#" class="collapsed" 
			           	  onclick="let popupForm=document.getElementById('relations_graph_form_insertspot').querySelector('._modal_root_').toggleShowHide();">
			          <i class="fas fa-fw fa-project-diagram text-white"></i>
			          <span><s:text name="Items.graphsGenerator"/></span>			                           		
			    </a>  
			    <span title="S.O.S" 
		                	onclick="event.stopPropagation();event.preventDefault();
		                			MxGuiHeader.showInfoModal('<s:text name="help.items.connection_graph.title" />','<s:text name="help.items.connection_graph.body" />')">
		                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
		          </span>
			    
			 </label>
			 
			 <label  class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button" >
				 <a href="#" class="collapsed" 
			           	  onclick="let popupForm=document.getElementById('aggr_relations_graph_form_insertspot').querySelector('._modal_root_').toggleShowHide();">
			          <i class="fas fa-fw fa-project-diagram text-white"></i>
			          <span><s:text name="Items.groupGraphsGenerator"/></span>			                           		
			    </a>  
			    <span title="S.O.S" 
		                	onclick="event.stopPropagation();event.preventDefault();
		                			MxGuiHeader.showInfoModal('<s:text name="help.items.connection_graph_group.title" />','<s:text name="help.items.connection_graph_group.body" />')">
		                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
		          </span>
			    
			 </label>
	 		
	 		  <div id="relations_graph_form_insertspot"  ></div>
	 		  <div id="aggr_relations_graph_form_insertspot"  ></div>
          </div>
        </div>
 </li>
 
 


 
 
<c:url value="Files" var="filesUrl"/> 
 <li id="leftbar_goto_files" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="${filesUrl}" target="_blank" >
          <i class="fas fa-fw fa-chart-bar"></i>
          <span><s:text name="global.filesView"/></span>
        </a>                
 </li>
 
 <li id="leftbar_item_create" class="nav-item" style="display:none">
 <!--  Modal contents added by javascript in function "handleCatalogDetails" down there -->
        <a class="nav-link collapsed" href="#"
        	onclick="let popupForm=this.parentNode.querySelector('._modal_root_').toggleShowHide();">
          <i class="fas fa-fw fa-copy"></i>
          <span><s:text name="Items.createItem"></s:text> <s:property value='currentUserProfile.catalogVocabulary.itemCap'/></span>
        </a>
       
		<div class="_item_form_insert_spot_" ></div>       
 </li>
 
 <li id="leftbar_items_csv_upload" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseCsv" aria-expanded="true" aria-controls="collapseCsv">
          <i class="fas fa-fw fa-file-upload"></i>
          <span><s:text name="Items.uploadItems"></s:text> <s:property value='currentUserProfile.catalogVocabulary.itemsCap'/></span>
        </a>
        <div id="collapseCsv" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
          <div class="mx-collapse py-2 collapse-inner rounded pt-4" style="overflow:auto">                         			 
			  <!-- Custom CSV FileUpload button -->
			 
	 		  <label for="fileUpload" 
	 		  	class="_openBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button"  >
	 		  	<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="Items.uploadItems.fromCsv"></s:text>
	 		  	 <span title="S.O.S" 
		                	onclick="event.stopPropagation();event.preventDefault();
		                			MxGuiHeader.showInfoModal('<s:text name="help.items.csv_upload.title" />','<s:text name="help.items.csv_upload.body" />')">
		                   <i class="mx-help-icon far fa-question-circle" style="color:white"></i>    
		          </span>
	 		  </label>
	 		  
	 		  <div id="csv_contents_previsu_body" style="display:none">
	 		  		 <div><span class="_filename_"></span> : <span class="_nbEntries_"></span> entries</div>
	 		  		 <table style="margin-top:1rem;" class="_csv_columns_tbl_">
	 		  		 	<tr><th style="padding-right:1rem;"><input type="checkbox"/></th><th>Csv Column</th><th>Catalog Field</th></tr>
	 		  		 </table>
				</div>
				<div id="csv_contents_previsu_footer" style="display:none">
	 		  		 <label class="_uploadBtn_ d-none d-sm-inline-block btn btn-sm btn-info shadow-sm mx-left-button"  >
	 		  				<i class="fas fa-upload fa-sm text-white" style="margin-right:1em"></i><s:text name="global.go"></s:text>
	 		  		</label>
				</div>
	 		  <!-- not displayed but used for the file input -->
	 		 <span style="display:none">	 		 	
			 		 <input id="fileUpload" type="file"
			 		  	accept=".csv,.txt"
			 		  	name="formFile"  
			 		  	onChange="MxGuiLeftBar.showCsvPrevisu(this);" />				
	 		  </span>
	 		  
	 		  
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
          		<div id="leftbar_filters_list_insertSpot" class="mx_collapse py-2 collapse-inner rounded" style=""></div>                        
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


<script type="text/javascript" >

// Title
function onclickCatalogName(event) { redirectToPage("${mxUrl}/metaindex/Catalogs"); }
MxGuiLeftBar.setTitle("<s:property value='currentUserProfile.catalogVocabulary.name'/>",onclickCatalogName);

// Operations
var statisticsOp = document.getElementById("leftbar_items_statistics").cloneNode(true);
statisticsOp.style.display="block";
MxGuiLeftBar.addOperation(statisticsOp);

var createItemOp = document.getElementById("leftbar_item_create").cloneNode(true);
createItemOp.style.display="block";
MxGuiLeftBar.addOperation(createItemOp);

var uploadItemsOp = document.getElementById("leftbar_items_csv_upload").cloneNode(true);
uploadItemsOp.style.display="block";
MxGuiLeftBar.addOperation(uploadItemsOp);

// Filters
var filtersOp = document.getElementById("leftbar_filters_list").cloneNode(true);
filtersOp.style.display="block";
MxGuiLeftBar.addOperation(filtersOp);

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

function _left_build_newitem_form_field_desc(termId,fieldName,termDesc,catalogDesc) {
	
	let langName=catalogDesc.vocabulary.guiLanguageShortName;
	let fieldFormDef={ 	id:"form_newitem_"+termId, 
						termId:termId, 
						important:"false",
						title:mx_helpers_getTermName(termDesc, catalogDesc)
						 };
	
	if (mx_helpers_isDatatypeMultiEnumOk(termDesc.datatype) && termDesc.enumsList!="" && termDesc.isMultiEnum==true) {
		fieldFormDef.type="multiselect";
		fieldFormDef.defaultValue="";
		fieldFormDef.values=termDesc.enumsList;		
	}
	else if (mx_helpers_isDatatypeEnumOk(termDesc.datatype) && termDesc.enumsList!="") {
		fieldFormDef.type="dropdown";
		fieldFormDef.defaultValue="";
		fieldFormDef.values=termDesc.enumsList;
		fieldFormDef.values.unshift(" ");
		
	} else {
		fieldFormDef.type="text";		
		fieldFormDef.defaultValue="";
	}
	return fieldFormDef;
	
}

MxGuiLeftBar.handleCatalogDetails=function(catalogDescr) {
	
	let filtersInsertSpot=MxGuiLeftBar.getFiltersInsertSpot();
	clearNodeChildren(filtersInsertSpot);

	
// generate "create new item" form
	let createNodeFormInsertSpot=createItemOp.querySelector("._item_form_insert_spot_");
	let fieldsList=[];
	
	// add an input form for each term of the catalog
	let sortedTermsNames = Object.keys(catalogDescr.terms).sort();
	for (var i=0;i<sortedTermsNames.length;i++) {
		let curFieldName=sortedTermsNames[i];
		let curFieldTermDescCopy=JSON.parse(JSON.stringify(catalogDescr.terms[curFieldName]));
		// if field is actually not a fieldName but a custom separator, we ignore it
		if (curFieldName[0]=='"') { continue; }		
		curFieldTermDescCopy.addedInForm=true;		
		let curFieldFormDef =_left_build_newitem_form_field_desc(curFieldName/*id*/,curFieldName/*user text*/,curFieldTermDescCopy,catalogDescr)
		
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
	
	let onValidFormCallback=function(itemFields) {
		let fieldsMap={};
		for (var fieldFormId in itemFields) {
			// cleaning the id to retrieve the termId
			let termId=fieldFormId.replace("form_newitem_","");
			fieldsMap[termId]=itemFields[fieldFormId];
		}
		
		let onCreationSuccessCallback=function() {
			//console.log("item created!");
		}
		let onCreationFailureCallback=function(errorMsg) {
			//console.log("item could not be created : "+errorMsg);
		}
		ws_handlers_createItem(catalogDescr,fieldsMap,onCreationSuccessCallback,onCreationFailureCallback);		
	}
	
	// create item modal
	let popupForm=MxGuiPopups.newMultiInputsPopup("<s:text name="Items.createItem"></s:text> <s:property value='currentUserProfile.catalogVocabulary.itemCap'/>",
													fieldsList,onValidFormCallback);
	createNodeFormInsertSpot.appendChild(popupForm);

	
// generate "Vega Relation graph" form
	let relationsGraphFormPopupInsertSpot = document.getElementById("relations_graph_form_insertspot");
	let createRelationsGraphForm=left_buildNewRelationsGraphForm(catalogDescr);
	relationsGraphFormPopupInsertSpot.appendChild(createRelationsGraphForm);	
// generate "Vega Relation graph" form
	let aggrRelationsGraphFormPopupInsertSpot = document.getElementById("aggr_relations_graph_form_insertspot");
	let createAggregatedRelationsGraphForm=left_buildNewAggregatedRelationsGraphForm(catalogDescr);
	aggrRelationsGraphFormPopupInsertSpot.appendChild(createAggregatedRelationsGraphForm);
	
// add filters list
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


//showCsvPrevisu : build a line of the 'CSV-columns table'
function _getColTypeNode(csvColName,checkBox) {
	let colTermNodeSelect=document.createElement("select");
	colTermNodeSelect.onchange=function() { checkBox.checked=true; }
	let found=false;
	for (termName in MxGuiHeader.getCurCatalogTermsList()) {
		let choiceNode=document.createElement("option");
		choiceNode.value=termName;
		choiceNode.innerHTML=mx_helpers_getTermName(MxGuiHeader.getCurCatalogDescr().terms[termName],MxGuiHeader.getCurCatalogDescr());
		if (termName==csvColName) { 
			choiceNode.selected=true;
			found=true;
		}		
		colTermNodeSelect.appendChild(choiceNode);
	}
	
	let idChoiceNode=document.createElement("option");
	idChoiceNode.value="_id";
	idChoiceNode.innerHTML="<id>";
	colTermNodeSelect.appendChild(idChoiceNode);
	idChoiceNode.onclick=function() { checkBox.checked=true; }
	
	let choiceIgnoreNode=document.createElement("option");
	choiceIgnoreNode.value="ignore";
	choiceIgnoreNode.innerHTML="- ignore -";
	choiceIgnoreNode.onclick=function() { checkBox.checked=false; }
	if (found==false) { choiceIgnoreNode.selected=true; }
	colTermNodeSelect.appendChild(choiceIgnoreNode);
	
	
	for (datatypeIdx in mx_helpers_FIELDS_DATATYPES) {
		let datatypeStr=mx_helpers_FIELDS_DATATYPES[datatypeIdx];
		let choiceNode=document.createElement("option");
		choiceNode.value="__new__"+datatypeStr;
		choiceNode.innerHTML=datatypeStr+" (new)";
		colTermNodeSelect.appendChild(choiceNode);
		choiceNode.onclick=function() { checkBox.checked=true; }
		
		// use TINY_TEXT as default new data type
		if (datatypeStr=="TINY_TEXT") {
			checkBox.onclick=function() {
				if (checkBox.checked==true) { choiceNode.selected=true; }
				else { choiceIgnoreNode.selected=true; }
			}
		}
	}
	
	if (found==true) { checkBox.checked=true; }
	
	checkBox.getCsvColName=function() { return csvColName; }; 
	checkBox.getTermName=function() { return colTermNodeSelect.value; }
	checkBox.classList.add("_csvColCheck_");	
	
	return colTermNodeSelect;
}

// showCsvPrevisu : build CSV columns table
function _getCsvColumnsList(fileHandle,nbEntriesNode,csvColsTable) {
	
	let reader = new FileReader();
    reader.onload = function (file_contents) 
    { 
    	let CSVrows = file_contents.target.result.split("\n");
    	let curLineNb=0;
    	let nbEntries=0;
    	
    	// count total nb entries
    	while (curLineNb<CSVrows.length) {
    		if (	   CSVrows[curLineNb].length>0 
    				&& CSVrows[curLineNb][0]!='#' 
    				&& !CSVrows[curLineNb].match(/^\s*$/)
    				&& !CSVrows[curLineNb].match(/^\s*#/)) {
    			nbEntries++;
    		}    			
    		curLineNb++;	    		
    	}
    	nbEntriesNode.innerHTML=nbEntries;    	
 	    
    	// parse fields names and types
    	let csvFieldsList=[];
    	let fieldsDefStr=CSVrows[0];
    	if (fieldsDefStr[0]!='#') {
    		alert("CsvUpload error : first line of CSV file shall be a commented line (starting with a '#') describing fields names,"
    						+"for example '# name,age'. Given line was : "+fieldsDefStr);
    		return;
    	}
    	let fieldsDefsArray=fieldsDefStr.split(';');
    	if (fieldsDefsArray.length==1) { fieldsDefsArray=fieldsDefStr.split(','); }
    	for (curFieldIdx in fieldsDefsArray) {
    		curField=fieldsDefsArray[curFieldIdx];	  
    		if (curFieldIdx==0) { curField=curField.replace(/^#/,""); }
    		let curFieldStr=stripStr(curField)
    		csvFieldsList.push(curFieldStr);
    		
    		let newRow=document.createElement("tr");
    		
    		// checkbox selected
    		let colSelected=document.createElement("td");
    		newRow.appendChild(colSelected);
    		let checkBox=document.createElement("input");
    		checkBox.type='checkbox';
    		colSelected.appendChild(checkBox);
    		
    		// field Name
    		let colName=document.createElement("td");
    		newRow.appendChild(colName);
    		colName.innerHTML=curFieldStr;
    		
    		// field type
    		let colType=document.createElement("td");
    		newRow.appendChild(colType);    		
    		colTypeNode=_getColTypeNode(curFieldStr,checkBox);
    		colType.appendChild(colTypeNode);
    		    		
    		csvColsTable.appendChild(newRow);
    	}    	
    }
    reader.readAsText(fileHandle);
}

//showCsvPrevisu : get list and type/name of selected CSV fields for upload
function _getSelectedCsvColumnsDef(csvColsTable) {
	let csvColsTermsMap={};
	let checkboxes = csvColsTable.querySelectorAll("._csvColCheck_");
	for (idx in checkboxes) {
		checkbox=checkboxes[idx];	
		if (typeof(checkbox)!='object') { continue; }
		//console.log("--- "+checkbox.getCsvColName()+" -> "+checkbox.getTermName());
		let curCsvColName=checkbox.getCsvColName();
		let curTermName=checkbox.getTermName();
		let isChecked=checkbox.checked;
		if (isChecked==true) { csvColsTermsMap[curCsvColName]=curTermName; }
	}
	return csvColsTermsMap;
}

MxGuiLeftBar.showCsvPrevisu=function(fileHandle) {
	// body
	let previsuNode=document.getElementById('csv_contents_previsu_body').cloneNode(true);
	previsuNode.style.display='block';
	
	let fileName=previsuNode.querySelector("._filename_");
	fileName.innerHTML=fileHandle.files[0].name;
	let nbEntries=previsuNode.querySelector("._nbEntries_");
	let csvColsTable=previsuNode.querySelector("._csv_columns_tbl_");
	
	let csvColsList = _getCsvColumnsList(fileHandle.files[0],nbEntries,csvColsTable)
	
	// footer
	let previsuNodeFooter=document.getElementById('csv_contents_previsu_footer').cloneNode(true);
	previsuNodeFooter.style.display='block';
	let uploadBtn=previsuNodeFooter.querySelector('._uploadBtn_');
	uploadBtn.onclick=function() {
		let selectedCsvColsDef = _getSelectedCsvColumnsDef(csvColsTable);
		ws_handlers_requestUploadCsvFile(fileHandle,selectedCsvColsDef); 
	}
	
	// show
	MxGuiHeader.showInfoModal('CSV Upload',previsuNode,previsuNodeFooter);
	
}
</script>


