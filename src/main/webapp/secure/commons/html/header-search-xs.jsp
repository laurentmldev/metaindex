<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
 
  
            <!-- Nav Item - Search Dropdown (Visible Only XS)  -->
            <li class="nav-item dropdown no-arrow d-sm-none" id="searchDropdownXs">
              <a class="nav-link dropdown-toggle" href="#" id="searchDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-search fa-fw"></i>
              </a>
              <!-- Dropdown - Search expression -->
              <div class="dropdown-menu dropdown-menu-right p-3 shadow animated--grow-in" aria-labelledby="searchDropdown">
                <div class="form-inline mr-auto w-100 navbar-search">
                  <div class="input-group">
                    <input id="header.filter.text.xs" type="text" class="form-control bg-light border-0 small" 
                    		style="min-width:200px;"
                    onchange="document.getElementById('header.filter.text').value=this.value;"
                    placeholder="<s:text name="Header.search.placeholder"/>" aria-label="Filter" aria-describedby="basic-addon2"              		
              		onkeypress="event.stopPropagation(); if (event.which==13||event.keycode==13) { MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());}"
              		onkeydown="event.stopPropagation();"
                    aria-label="Search" aria-describedby="basic-addon2">
                    <div class="input-group-append">
                      <button class="btn btn-primary" type="button"
                       	onclick="MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());" >
                        <i class="fas fa-search fa-sm"></i>
                      </button>
                    </div>
                    <div class="input-group-append">
                      <button class="btn btn-primary" type="button"
                       	onclick="event.stopPropagation(); event.preventDefault();
                        		 document.getElementById('header.filter.name.xs').style.display='block';" >
                        <i class="far fa-star fa-sm" ></i>
                      </button>
                    </div>
                  </div>
                </div>
               
               <!-- Nav Item - Search Name -->
		   	<div id="header.filter.name.xs" >
		   		<hr/>
		   		<input type="text" class="form-control bg-light border-0 small"  
		   				style="margin-top:5px;margin-bottom:2px;"
                    
                    placeholder="<s:text name="Header.savesearch.placeholder"/>" aria-label="Filter" aria-describedby="basic-addon2"              		
              		onkeypress="if (event.which==13||event.keycode==13) { MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());}" 
                    aria-label="Search" aria-describedby="basic-addon2">
              <div>
                <button class="btn btn-primary" type="button"
                 	onclick="MxGuiHeader.onFilterClick(MxGuiHeader.getCurrentSearchQuery());" >
                  <i class="fas fa-check fa-sm"></i>
                </button>

                <button class="btn btn-primary" type="button" >
                  <i class="fas fa-times fa-sm"></i>
                </button>
              </div>
		   	</div>         
           
              </div>
            </li>
          
          
  
  <script type="text/javascript" >
  // hide 'save filter' input when opening dropdown 
  $('#searchDropdownXs').on('show.bs.dropdown', function () {
	  document.getElementById('header.filter.name.xs').style.display='none';
	})
	
  </script>
