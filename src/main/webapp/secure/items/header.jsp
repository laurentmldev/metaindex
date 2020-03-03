<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<s:include value="../commons/html/header.jsp"></s:include>

<script type="text/javascript" >

var _curCatalogDescr=null;

function header_onFilterClick(searchQuery,orderString,reversedOrder)  {
	var selectedFiltersNames=MxGuiLeftBar.getSelectedFiltersNames();	
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
	_curCatalogDescr=catalogDescr;
	MxGuiHeader.showFilter();
}
MxGuiHeader.onFilterClick=header_onFilterClick;
MxGuiHeader.onFilterSave=header_onFilterSave;
MxGuiHeader.handleCatalogDetails=header_handleCatalogDetails;
MxGuiHeader.getCurCatalogTermsList=function() { return _curCatalogDescr.terms; }
MxGuiHeader.getCurCatalogDescr=function() { return _curCatalogDescr; }


</script>


