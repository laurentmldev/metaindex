<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<s:include value="../commons/html/header.jsp"></s:include>

<script type="text/javascript" >

var _curCatalogDescr=null;

function header_onFilterClick(searchQuery,orderString,reversedOrder)  {
	var selectedFiltersNames=MxGuiHeader.getSelectedFiltersNames();	
	ws_handlers_requestItemsSearch(searchQuery,selectedFiltersNames,orderString,reversedOrder);	
}

function header_onFilterSave(filterName,searchQuery) {
	ws_handlers_requestCreateFilter(filterName,searchQuery);	
}

function header_handleCatalogDetails(catalogDescr) {
	
	MxGuiHeader.clearSortingChoice();
	MxGuiHeader.addSortingChoice("<s:text name="Header.sortby"/>","");
	
	let sortedTermsNames = Object.keys(catalogDescr.terms).sort();
	for (var termIdx=0;termIdx<sortedTermsNames.length;termIdx++) {
		let termName=sortedTermsNames[termIdx];		
		let termDescr = catalogDescr.terms[termName];
		termTranslation=mx_helpers_getTermName(termDescr, catalogDescr)
		MxGuiHeader.addSortingChoice(termTranslation,termName);
	}
  	if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)!=true) {
	  	let saveCurrentSearch=document.getElementById("save_current_search_button");
	  	saveCurrentSearch.style.display='none';
	}
	_curCatalogDescr=catalogDescr;
	
	let filtersInsertSpot=MxGuiHeader.getFiltersInsertSpot();
	clearNodeChildren(filtersInsertSpot);
	
	
	// add filters list
		if (mx_helpers_isCatalogWritable(MxGuiDetails.getCurCatalogDescription().userAccessRights)!=true) {
			let filterNodeTemplate=document.getElementById("header_filter_template");
			let filterDeleteButton = filterNodeTemplate.querySelector('._button_delete_');
			filterDeleteButton.style.display='none';
			let filterQueryInput = filterNodeTemplate.querySelector('._query_input_');
			filterQueryInput.disabled=true;
			let filterQueryUpdate = filterNodeTemplate.querySelector('._button_update_');
			filterQueryUpdate.style.display='none';		
		}

		// add user custom filters	
		for (i=0;i< catalogDescr.filters.length;i++) {
			let curFilterDescr=catalogDescr.filters[i];
			let newFilterNode = header_buildNewFilter(curFilterDescr);
			filtersInsertSpot.append(newFilterNode);
		}
	
	
	MxGuiHeader.showFilter();
}

//return array of queries corresponding to selected filters
MxGuiHeader.getSelectedFiltersNames=function() {
	var result = [];
	let filtersInsertSpot=MxGuiHeader.getFiltersInsertSpot();
	for (var curFilter=filtersInsertSpot.firstChild;curFilter!==null;curFilter=curFilter.nextElementSibling) {		
		if (typeof(curFilter)!='object') { continue; }
		if (curFilter.isSelected) { 
			result.push(curFilter.descr.name); 
		}
	}
	return result;
}

var nbFiltersActive=0;
function header_buildNewFilter(descr) {
	
	let newFilterNode=document.getElementById("header_filter_template").cloneNode(true);	
	newFilterNode.style.display='block';
	newFilterNode.descr=descr;
	newFilterNode.isSelected=false;
	
	// name
	let nameNode=newFilterNode.querySelector("._name_");
	nameNode.innerHTML=descr.name;
	
	// id
	let idNode=newFilterNode.querySelector("._id_");
	idNode.innerHTML=descr.id;
	
	// query input
	let queryNode=newFilterNode.querySelector("._query_");
	let queryInputNode=newFilterNode.querySelector("._query_input_");
	queryInputNode.value=descr.query;
	
	let filtersButton=document.getElementById('showFilterDropdownButton');
	let nbActiveFiltersCounter=document.getElementById('tiny_nb_active_filters');
	newFilterNode.onclick=function(event) {		
		event.stopPropagation();
		if (newFilterNode.isSelected) {
			nbFiltersActive--;
			newFilterNode.deselect();
			if (nbFiltersActive>0) { 
				filtersButton.classList.add('mx_filters_active');
				nbActiveFiltersCounter.innerHTML=nbFiltersActive;
				nbActiveFiltersCounter.style.display='block';			
			} 
			else { 	
				filtersButton.classList.remove('mx_filters_active');
				nbActiveFiltersCounter.style.display='none';
			}
			
		}
		else { 
			nbFiltersActive++;
			newFilterNode.select(); 
			if (nbFiltersActive>0) { 
				filtersButton.classList.add('mx_filters_active');
				nbActiveFiltersCounter.innerHTML=nbFiltersActive;
				nbActiveFiltersCounter.style.display='block';
			} 
			else { 	
				filtersButton.classList.remove('mx_filters_active');
				nbActiveFiltersCounter.style.display='none';
			}			
		}
	}
	
	newFilterNode.select=function() {
		newFilterNode.isSelected=true;
		queryNode.style.display='block';
		newFilterNode.classList.add('mx_filter_selected');
		newFilterNode.classList.add('mx-selected-dropdown');
		MxGuiHeader.refreshSearch();
	}
	newFilterNode.deselect=function() {
		newFilterNode.isSelected=false;
		queryNode.style.display='none';
		newFilterNode.classList.remove('mx_filter_selected');
		newFilterNode.classList.remove('mx-selected-dropdown');
		MxGuiHeader.refreshSearch();
	}
	
	// button delete
	let deleteButton=newFilterNode.querySelector("._button_delete_");
	deleteButton.onclick=function(event) {
		ws_handlers_requestDeleteFilter(descr.id);
	}
	
	if (descr.isBuiltin==true) {
		queryInputNode.disabled=true;
		newFilterNode.classList.add('mx_filter_builtin');
		deleteButton.style.display='none';
	} else {
		queryInputNode.disabled=false;
		newFilterNode.classList.remove('mx_filter_builtin');
		deleteButton.style.display='block';
	}
	return newFilterNode
}

MxGuiHeader.buildNewFilter=header_buildNewFilter;
MxGuiHeader.openFiltersArea=function() {
	document.getElementById('showFilterDropdown').classList.add('show');
	
}
MxGuiHeader.onFilterClick=header_onFilterClick;
MxGuiHeader.onFilterSave=header_onFilterSave;
MxGuiHeader.handleCatalogDetails=header_handleCatalogDetails;
MxGuiHeader.getCurCatalogTermsList=function() { return _curCatalogDescr.terms; }
MxGuiHeader.getCurCatalogDescr=function() { return _curCatalogDescr; }
MxGuiHeader.getFiltersInsertSpot=function() { 
	return document.getElementById("filters_list_insertSpot"); 
}

</script>



 <div id="header_filter_template" class="mx_filter collapse-item small"  style="display:none" >
	<span class="_name_" ></span>
	<span class="_id_" style="display:none"></span>  
	<button class="_button_delete_ btn btn-xs float-right" type="button" style="border:none"
		onmouseover="this.classList.add('btn-danger');"
		onmouseout="this.classList.remove('btn-danger');"
		>
      <i class="fa fa-times fa-sm"></i>      
    </button>
    
 	<div class="_query_" style="display:none;" >
 		<hr style="padding:0;margin:0;margin-top:0.2rem;">
 		<input class="_query_input_" type="text" value="" style="width:70%;margin-top:0.4rem;margin-bottom:0.2rem;"
 		onclick='event.stopPropagation();'
 		onfocus="this.parentNode.querySelector('._button_update_').style.display='inline-block';"
 		onchange="this.changed=true;"
 		onkeydown="event.stopPropagation();"
 		onkeypress="event.stopPropagation();
 			if (event.which==13||event.keycode==13) {  			
 				ws_handlers_requestUpdateFilter(this.parentNode.parentNode.querySelector('._id_').innerHTML,
				this.parentNode.parentNode.querySelector('._query_input_').value);
 			}"
 		onblur="if (this.changed!=true) { this.parentNode.querySelector('._button_update_').style.display='none'; }">
 		
 		<button class="_button_update_ btn btn-xs" style="display:none" type="button" 
			onmouseover="this.classList.add('btn-success');"
			onmouseout="this.classList.remove('btn-success');"
			onclick="event.stopPropagation();				
					ws_handlers_requestUpdateFilter(this.parentNode.parentNode.querySelector('._id_').innerHTML,
						        					 this.parentNode.parentNode.querySelector('._query_input_').value);"
		>
		 <i class="fa fa-check fa-sm"></i>
		 </button>
 	</div>
 </div>

