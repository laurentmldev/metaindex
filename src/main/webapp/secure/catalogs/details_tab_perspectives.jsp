<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 		  
<s:include value="../commons/html/perspective/perspective.jsp" />

 <script type="text/javascript" >

 
 // function used by details.jsp:details_buildContents
 function details_buildContents_perspectives(newPopulatedCatalogDetails,catalogCard) {
	 
	 let insertSpot=newPopulatedCatalogDetails.querySelector("._details_perspectives_insertspot_");
	 let sortedPerspectivesNames = Object.keys(catalogCard.descr.perspectives).sort();		 
	 for (pIdx in sortedPerspectivesNames) {
		let curPName=sortedPerspectivesNames[pIdx];
		let perspectiveData=catalogCard.descr.perspectives[curPName];
		let perspectiveNode=null;
		if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
			perspectiveNode = MxGuiPerspective.buildCustomizablePerspective(insertSpot,catalogCard.descr,perspectiveData);
		} else {
			perspectiveNode = MxGuiPerspective.buildReadOnlyPerspective(insertSpot,catalogCard.descr,perspectiveData);			
			let removeButton=perspectiveNode.querySelector("._remove_button_");
			removeButton.parentNode.removeChild(removeButton);
		}
		if (MxGuiPerspective.getCurrentPerspectiveId()==perspectiveData.id) { perspectiveNode.open(); }
		
		
		
	 }
	 
	 // add-new-perspective trailing card
	 if (mx_helpers_isCatalogAdmin(catalogCard.descr.userAccessRights)) {
	 	let addNewPerspectiveButton=MxGuiPerspective.buildTabCreateNewPerspectiveButton();
	 	insertSpot.appendChild(addNewPerspectiveButton);
	 }

 }		
	
 </script>

<!--  --------Templates--------- -->
 
<!-- Perspective Template -->  
<!-- expected by commons/perspectives.jsp -->
<div id="_details_perspective_template_" class="card _customizable_perspective_root_" style="display:none; margin:1rem;" onclick="event.stopPropagation();">
	
	<div class="card-header" >
		<table ><tr>
		<td onclick="event.stopPropagation();">
			<div class="p-2 mx-btn-transparent-danger mx-square-button-xs d-sm-inline-block btn btn-sm shadow-sm _remove_button_"
						title="<s:text name='Catalogs.perspectives.remove_question' />"	 	
						data-toggle="confirmation"
						btnOkLabel="<s:text name="global.yes" />"
		 	  			btnCancelLabel="<s:text name="global.no" />"
				  		onConfirm="MxGuiPerspective.deletePerspective(findAncestorNode(this,'_customizable_perspective_root_').id)" 
				  		onCancel=""	  				
			 			 >
			 			<i class="fas fa-times fa-sm text-grey-50"></i>
			</div>
		</td>
		<td onclick="event.stopPropagation();">
		  <h6 style="margin:1rem"><a data-toggle="collapse" 
		  	data-parent="#perspectivesInsertSpotId" href="#perspectiveBodyId" 
		    class="card-link _perspective_title_" ></a></h6>
        </td>
        </tr></table>
	</div>	
	<div id="perspectiveBodyId" class="collapse _perspective_bodyid_" >	
   		<div class="card-body mx-perspective-body-editing mx-perspective-body _perspective_body_" >
		</div>
	</div>
</div>
<!--  --------end of Templates--------- -->

<div class="tab-pane fade _details_perspectives_insertspot_" id="nav-perspectives" role="tabpanel" aria-labelledby="nav-perspectives-tab">
					  						  						
</div>
					
 	