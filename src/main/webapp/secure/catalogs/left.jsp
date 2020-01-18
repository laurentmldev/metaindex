<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %> 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 


 <li id="leftbar_catalog_create" class="nav-item" style="display:none">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo">
          <i class="fas fa-fw fa-cube"></i>
          <span><s:text name="Catalogs.createCatalog"></s:text></span>
        </a>
        <div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
          <div class="mx-collapse py-2 collapse-inner rounded">
            <h6 class="collapse-header"><s:text name="Catalogs.new_catalog" /></h6>
                        
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
					 <!--button class="_button_cancel_ btn "  type="button" 
					 		onmouseover="this.classList.add('btn-secondary');"
							onmouseout="this.classList.remove('btn-secondary');"							
							onclick="event.preventDefault();event.stopPropagation();
									// set class to 
								"
						>
						 <i class="fa fa-times fa-sm"></i>
					 </button -->
				 	
				 </div>
 
          </div>
        </div>
 </li>




<script>

// Title
MxGuiLeftBar.setTitle("<s:text name="Catalogs.title" />");


// Operations

if (mx_helpers_isUser()) {
	var createCatalogOp = document.getElementById("leftbar_catalog_create").cloneNode(true);
	createCatalogOp.style.display="block";
	MxGuiLeftBar.addOperation(createCatalogOp);
}

</script>


