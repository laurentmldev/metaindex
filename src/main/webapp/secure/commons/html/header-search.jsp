<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
 
  
  <script type="text/javascript" >
  
  function header_showFilter() {
	  document.getElementById('header.filter').style.display='block';
  }   
  function header_getCurrentSearchQuery() {
	  return document.getElementById('header.filter.text').value; 
  }
  function header_setCurrentSearchQuery(query) {
	  document.getElementById('header.filter.text').value=query; 
  }
  function header_getCurrentSearchSortString() {
	  return document.getElementById('header.filter.sortString').value; 
  }
  function header_getCurrentSearchReversedOrder() {
	  return document.getElementById('header.filter.sortOrderReversed').value; 
  }
  
  var MxGuiHeader={};
  MxGuiHeader.getCurrentSearchQuery=header_getCurrentSearchQuery;
  MxGuiHeader.setCurrentSearchQuery=header_setCurrentSearchQuery;
  
  MxGuiHeader.getCurrentSearchSortString=header_getCurrentSearchSortString;
  MxGuiHeader.getCurrentSearchReversedOrder=header_getCurrentSearchReversedOrder;
  MxGuiHeader.showFilter=header_showFilter;
  MxGuiHeader.onFilterClick=function(queryString,orderString,reversedOrder) {}
  MxGuiHeader.onFilterSave=function(name,queryString) {}
  MxGuiHeader.refreshSearch=function() {
	  let searchText=document.getElementById('header.filter.text').value;
	  let orderByString=document.getElementById('header.filter.sortString').value;
	  let reversedOrder=document.getElementById('header.filter.sortOrderReversed').value;
	  MxGuiHeader.onFilterClick(searchText,orderByString,reversedOrder);
  }
  MxGuiHeader.clearSortingChoice=function() {
	  let sortingNode = document.getElementById('header.filter.sortString');
	  clearNodeChildren(sortingNode);
  }
  MxGuiHeader.addSortingChoice=function(name,value) {
	  let optionNode = document.createElement("option");
	  optionNode.setAttribute("value",value);
	  optionNode.innerHTML=name;
	  
	  let sortingNode = document.getElementById('header.filter.sortString');
	  sortingNode.appendChild(optionNode);
  }
  
  </script>
  
          <!-- Topbar Search -->
          <div id="header.filter" style="display:none;">
          
          	
           <div class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search" style="width:auto";>
           
           
            <div class="input-group" onkeypress="event.stopPropagation(); ">
            
            	     
             <div class="input-group-append">
              	<div title="S.O.S" onclick="MxGuiHeader.showInfoModal('<s:text name="help.search.title" />','<s:text name="help.search.body" />')">
                  <i class="mx-help-icon far fa-question-circle"></i>    
                  </div>                
              </div>
             
              <input id="header.filter.text" type="text" class="mx-search-input form-control bg-light border-0 small" 
              		placeholder="<s:text name="Header.search.placeholder"/>" aria-label="Filter" aria-describedby="basic-addon2"   
              		onchange="document.getElementById('header.filter.text.xs').value=this.value;"           		
              		onkeypress="event.stopPropagation(); if (event.which==13||event.keycode==13) { MxGuiHeader.refreshSearch();}"
              		onkeydown="event.stopPropagation();"
              		title="<s:text name="Header.search.querycontents.title"/>">
              		
              <div class="input-group-append">
              	<!--  call to external (custom) function header_onSearchClick(searchQuery) -->
                <button class="btn btn-secondary" type="button" onclick="MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());" 
                	title="<s:text name="Header.search.runquery.title"/>" >
                  <i class="fas fa-search fa-sm text-grey-50"></i>
                </button>
              </div>
              
              <div class="input-group-append">
              	<!--  call to external (custom) function header_onSearchClick(searchQuery) -->
                <button class="btn btn-secondary" type="button" 
                   onclick="
                	copyToClipBoard(MxGuiHeader.getCurrentSearchQuery());
                	MxGuiHeader.setCurrentSearchQuery('');
                   	MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());"
                   title="<s:text name="Header.search.clearquery.title"/>" >
                  <i class="fas fa-cut fa-sm text-grey-50"></i>
                </button>
              </div>
             
              
              <!-- save current filter expression -->
               <div id="save_current_search_button" class="input-group-append dropdown no-arrow ">
              	<!--  call to external (custom) function header_onSearchClick(searchQuery) -->
                <a class="btn btn-secondary dropdown-toggle" id="saveFilterDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" 
                	title="<s:text name="Header.search.savequery.title"/>" > 
                  <i class="far fa-star fa-sm text-grey-50"></i>
                </a>
               	  <!-- Dropdown - Filter Save Name -->
	              <div class="dropdown-menu dropdown-menu-right p-3 shadow animated--grow-in" aria-labelledby="saveFilterDropdown">
	                <div class="form-inline mr-auto w-auto navbar-search">
	                  <div class="input-group">
	                    <input id="header.filter.name" type="text" class="form-control bg-light border-0 small" 
	                    		style="min-width:200px"
	                    		onkeypress="if (event.which==13||event.keycode==13) {
				                    			MxGuiHeader.onFilterSave(document.getElementById('header.filter.name').value,
         									  								MxGuiHeader.getCurrentSearchQuery());
					                    		document.getElementById('header.filter.text').value='';
				        						this.parentNode.parentNode.parentNode.classList.remove('show');
				        						MxGuiLeftBar.openFiltersArea();
	                    					}
	                    					"
	                    placeholder="<s:text name="Header.savesearch.placeholder"/>" aria-label="Filter" aria-describedby="basic-addon2"              			              		 
	                    aria-label="Search" aria-describedby="basic-addon2">
	                    
	                    <div class="input-group-append">
	                      <button class="btn btn-primary" type="button"
	                       	onclick="MxGuiHeader.onFilterSave(document.getElementById('header.filter.name').value,
	                       									  MxGuiHeader.getCurrentSearchQuery());
    								document.getElementById('header.filter.text').value='';
	        						MxGuiLeftBar.openFiltersArea();
	                      				" >
	                        <i class="fas fa-check fa-sm text-grey-50"></i>
	                      </button>
	                      <button class="btn btn-primary" type="button" >
	                        <i class="fa fa-times fa-sm text-grey-50"></i>
	                      </button>
	                    </div>		                                        
	                  </div>
	                </div>
	              </div>
              </div>
            
             <select id="header.filter.sortString" class="mx-dropdown form-control bg-light border-0 small"             	
             	onchange="MxGuiHeader.refreshSearch();"           		
              	onkeypress="if (event.which==13||event.keycode==13) { MxGuiHeader.refreshSearch();}" 
              	title="<s:text name="Header.sortby.title"/>"
              	style="width:min-content;" >
              	
              </select>
              <select id="header.filter.sortOrderReversed" class="mx-dropdown form-control bg-light border-0 small"  
              	onchange="MxGuiHeader.refreshSearch();"           		
              	onkeypress="if (event.which==13||event.keycode==13) { MxGuiHeader.refreshSearch();}" 
              	title="<s:text name="Header.sortorder.title"/>"
              	style="width:min-content;">
              	<option value="false" Selected><s:text name='Header.sortorder.asc' /></option>
              	<option value="true"><s:text name='Header.sortorder.desc' /></option>
              </select>
              
         
            </div>
            
          </div>
          
</div>
