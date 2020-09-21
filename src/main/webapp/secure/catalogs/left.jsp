<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 


 <li id="leftbar_catalog_create" class="nav-item" style="display:none">
 
 <a class="nav-link collapsed" href="#" onclick="document.getElementById(MX_CATALOGS_SEARCH_POPUP_ID).toggleShowHide();updateCatalogsSearchList();" >
          <i class="fas fa-fw fa-search"></i>
          <span><s:text name="Catalogs.searchCatalog"></s:text></span>
        </a>
        
 <c:if test="${mxRole == 'ROLE_ADMIN' || mxRole == 'ROLE_USER'}">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo">
          <i class="fas fa-fw fa-star"></i>
          <span><s:text name="Catalogs.createCatalog"></s:text></span>
        </a>
        <div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
          <div class="mx-collapse py-2 collapse-inner rounded">
          
          	
            <h6 class="collapse-header"><s:text name="Catalogs.new_catalog" /></h6>
                    <center>
                   <a 	id="leftbar_catalog_nbCreatedCatalogs" 
                   		class="btn-big btn btn-sm btn-warning shadow-sm" 
                   		style="font-size:0.8rem;margin-bottom:1rem;color:white"
                   		>
						<s:text name="Catalogs.left.maxNbCatalogsCreatedReached" /> 
										(<span id="leftbar_catalog_curNbCreatedCatalogs" ></span> 
													/ 
										  <span id="leftbar_catalog_maxNbCreatedCatalogs" ></span>)
						<br/><span style="font-weight:bold;font-size:1rem;padding:1rem"><s:text name="Catalogs.left.getMoreCatalogs" /></span>
					</a></center>    
				 <div id="leftbar_create_catalog" class="mx-collapse-item collapse-item small">
					
			 		<input class="_query_input_ form-control bg-light border-0 small" 
			 			type="text" value="" style="width:90%;margin-top:1rem;"
				 		placeholder="<s:text name='global.indexname' />"
				 		onclick='event.stopPropagation();'
				 		onkeypress="if (event.which==13||event.keycode==13) { 
				 			details_createCatalog(this.value);
						 			}"
				 		>
				 		
			 		<button class="_button_update_ btn "  type="button" style="margin-top:0.5rem"
							onmouseover="this.classList.add('btn-success');"
							onmouseout="this.classList.remove('btn-success');"
							onclick="event.stopPropagation();
								details_createCatalog(
										       this.parentNode.parentNode.querySelector('._query_input_').value);"
						>
						 <i class="fa fa-check fa-sm"></i>
					 </button>
									 	
				 </div>
 			
          </div>
        </div>
 </c:if>
 </li>


<div id="catalogs_popups_container" ></div>
<s:include value="catalogs_search_popup.jsp" />

<script>

// Title
MxGuiLeftBar.setTitle("<s:text name="Catalogs.title" />");


// Operations

if (mx_helpers_isUser()) {
	var createCatalogOp = document.getElementById("leftbar_catalog_create").cloneNode(true);
	createCatalogOp.style.display="block";
	MxGuiLeftBar.addOperation(createCatalogOp);
}

MxGuiLeftBar.updateNbCatalogsCreated=function(curNb,maxNb) {
	
	let curNbCatalogsEl = document.getElementById("leftbar_catalog_curNbCreatedCatalogs");
	let maxNbCatalogsEl = document.getElementById("leftbar_catalog_maxNbCreatedCatalogs");
	
	curNbCatalogsEl.innerHTML=curNb;
	maxNbCatalogsEl.innerHTML=maxNb;
	
	document.getElementById("leftbar_catalog_nbCreatedCatalogs").onclick=function() {
		document.getElementById(MX_HEADER_PLANS_POPUP_ID).toggleShowHide();
	}
	
	if (curNb>=maxNb) {
		document.getElementById("leftbar_create_catalog").style.display="none";
		document.getElementById("leftbar_catalog_nbCreatedCatalogs").style.display="block";
	} else {
		document.getElementById("leftbar_create_catalog").style.display="block";
		document.getElementById("leftbar_catalog_nbCreatedCatalogs").style.display="none";
	}
}


var MX_CATALOGS_SEARCH_POPUP_ID="catalogs_search_popup";

buildCatalogsSearchPopup(document.getElementById("catalogs_popups_container"));

</script>


