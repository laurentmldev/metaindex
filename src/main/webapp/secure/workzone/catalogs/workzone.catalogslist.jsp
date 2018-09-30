<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>


var curCatalogId=0;
var TEMPLATES_CATALOG_ID=-2;
var ALLELEMENTS_CATALOG_ID=0;
var catalogSummaries={};

function catalogs_handle_cataloglist(catalog) {
	
	var insertSpot = document.getElementById("_catalogsList_insertspot_");
	insertSpot.innerHTML="";	
	
	for (var i=0;i<catalog.catalogsList.length;i++) {
		var curCatId = catalog.catalogsList[i];
		send_request_cataloginfo(curCatId);
	}
}

function catalogs_handle_catalogsummary(catalog) {
	
	if (catalog.selected) { 
		curCatalogId=catalog.catalogId;
		removeElementsGroupFromCatalogIcon = document.getElementById("workzone.thumbnails.icon.removeElements");
		if (!catalog.virtual) { 
			removeElementsGroupFromCatalogIcon.style.display="block";
			removeElementsGroupFromCatalogIcon.onclick=function(event) {
				event.stopPropagation(); 
				removeMultiSelectedStaticElements(catalog.catalogId,catalog.catalogName);
			}
		}
		else { removeElementsGroupFromCatalogIcon.style.display="none"; }		
	}
	
	addCatalogInList(catalog.catalogId, catalog.catalogName, catalog.catalogComment, catalog.catalogNbElements, catalog.virtual, catalog.dynamic);
	catalogSummaries[catalog.catalogId]=new CatalogSummary(catalog);
	
	if (!catalog.selected) { return; }
	
// input catalog name
	document.getElementById("workzone.modifycatalog.form.formCatalogName").value=catalog.catalogName;

// catalog comment RO
	if (!catalog.virtual) { 
		var commentNode = document.getElementById("workzone.catalogdetails.catalogcomment.readonly");
		var commentText=catalog.catalogComment;
		if (commentText=="") {
			commentText="<span class=\"emptyModifyable\"><s:text name="workzone.clickToAdd" /> <s:text name="workzone.createnewelement.enterComment" /></span>";
		}
		commentNode.innerHTML=commentText;
		commentNode.classList="modifyable";
		commentNode.onclick=function(event) {
			this.style.display='none';
			document.getElementById('workzone.catalogdetails.catalogcomment.edit').style.display='block';
		} 
	}
	
// catalog comment edit
	var editCommentNode = document.getElementById("workzone.catalogdetails.catalogcomment.edit");
	editCommentNode.value=catalog.catalogComment;
	
// catalog filter RO
	var filterNode = document.getElementById("workzone.catalogdetails.catalogsearchquery.readonly");
	
	if (!catalog.virtual) {
		filterNode.style.display='block';
		filterNode.classList="modifyable";
		var filterTxt = catalog.dynamicElementsFilter;
		if (filterTxt=="") {
			document.getElementById("workzone.catalogdetails.catalogsearchquery.title").style.display='none';			
			document.getElementById("workzone.catalogdetails.catalogsearchquery.empty").style.display='block';
			filterTxt=" : "+filterTxt;
		} else {
			document.getElementById("workzone.catalogdetails.catalogsearchquery.title").style.display='block';
			document.getElementById("workzone.catalogdetails.catalogsearchquery.empty").style.display='none';
		}
		filterNode.innerHTML=filterTxt;
		filterNode.onclick=function(event) {
			this.style.display='none';
			document.getElementById('workzone.catalogdetails.catalogsearchquery.edit').style.display='block';
		}
	} else {
		filterNode.style.display='none';
		document.getElementById("workzone.catalogdetails.catalogsearchquery.title").style.display='none';			
		document.getElementById("workzone.catalogdetails.catalogsearchquery.empty").style.display='none';
		document.getElementById('workzone.catalogdetails.catalogsearchquery.edit').style.display='none';		
	}

// catalog filter edit
	var filterNodeEdit = document.getElementById("workzone.catalogdetails.catalogsearchquery.edit");
	filterNodeEdit.value=catalog.dynamicElementsFilter;

// nb static elements
	var nbStaticElemsNode =document.getElementById("workzone.catalogdetails.nbStaticElements");
	nbStaticElemsNode.innerHTML=catalog.staticElementsIds.length;
	
// nb dynamic elements
	var nbDynamicElemsNode =document.getElementById("workzone.catalogdetails.nbDynamicElements");
	nbDynamicElemsNode.innerHTML="-";
	
// update thumbnails list matching the selected
// if curCatalog is selected one and if all elements form community are loaded
	if (catalog.selected 
			&& communityNbLoadedElements==communityNbElements) {
		
		updateThumbnailsContents(document.getElementById('workzone.thumbnails.elementsFastSearch').value);
		// select currently selected element
		changeElement(0); 
	}
}

function addCatalogInList(catId, catName, catComment, catNbElements, isVirtual, isDynamic) {
	
	var insertSpot = document.getElementById("_catalogsList_insertspot_");
	var newCatNode = document.getElementById("_template_catalog_summary_").cloneNode(true);
	newCatNode.style.display='table-row';
	newCatNode.id="catalog.summary."+catId;	
	
// table column node
	var colNode = newCatNode.querySelector("._colnode_");
	
	if (catId==curCatalogId) { colNode.classList="selectedItem"; }	
	
	// Virtual catalogs don't have static elements by definition, so no drop allowed on them 	
	if (!isVirtual) {
		colNode.onmouseover=function(event) {
			document.getElementById("workzone.catalogsList.icon.deleteElement."+catId).style.display='block';
		}
		colNode.onmouseout=function(event) {
			document.getElementById("workzone.catalogsList.icon.deleteElement."+catId).style.display='none';
		}		
		colNode.ondragover=function(event) { handleDragOverCatalog(this,event); }
		colNode.ondragenter=function(event) { handleDragEnterCatalog(this,event); }
		colNode.ondragleave=function(event) { handleDragLeaveCatalog(this,event); }
		colNode.ondrop=function(event) { 
			event.stopPropagation();
			event.preventDefault();
			handleDropCatalog(catId,catName,this,event);	
		}
	}

	
// contents node
	var contentsNode = newCatNode.querySelector("._contents_");
	if (catId==curCatalogId) {
		contentsNode.id="workzone.catalogdetails.catalogname.readonly";
	}
	if (!isVirtual && catId==curCatalogId) {
		contentsNode.onclick=function(event) {
			event.stopPropagation();
			document.getElementById('workzone.catalogdetails.catalogname.selector.'+catId).style.display='none';
			document.getElementById('workzone.catalogdetails.catalogname.nbelems.'+catId).style.display='none';
			document.getElementById('workzone.catalogdetails.catalogname.edit.'+catId).style.display='block';														
		}
	}

// link
	var linkNode = newCatNode.querySelector("._link_");
	linkNode.innerHTML=catName;
	if (catId==curCatalogId) { linkNode.id+=catId; }
	else {
		linkNode.onclick=function(event) {				
			event.stopPropagation();
			catalogs_send_selectcatalogRequest(catId);
		}	
	}
	if (isVirtual) { linkNode.classList.add("modifyable"); }
	if (isDynamic) { linkNode.classList.add("dynamicCatalog"); }
	if (catId==TEMPLATES_CATALOG_ID) { linkNode.classList.add("templateCatalogInCatalogsList"); }
	
	
// catalog name input
	var catNameInput = newCatNode.querySelector("._catalogNameInput_");
	if (catId==curCatalogId) { 
		catNameInput.id+=catId;
		catNameInput.value=catName;
		catNameInput.onchange=function(event) {
			event.stopPropagation();
			document.getElementById('workzone.modifycatalog.form.formCatalogName').value=this.value;
			catNameInput.style.display='none';
			linkNode.innerHTML=this.value;
			document.getElementById('workzone.catalogdetails.catalogname.selector.'+catId).style.display='block';
			document.getElementById('workzone.modifycatalog.form').submit();
		}
	}

// nb elements
	var nbElemsNode = newCatNode.querySelector("._catNbElements_");
	nbElemsNode.id="workzone.catalogdetails.catalogname.nbelems."+catId;
	nbElemsNode.innerHTML="("+catNbElements+")";	
										   
										   
// delete icon
	var deleteCatalogIcon = newCatNode.querySelector("._deletecatalogIcon_");
	deleteCatalogIcon.id+=catId;
	deleteCatalogIcon.onclick=function(event) {
		event.stopPropagation();		
		document.getElementById('workzone.deleteCatalog.form.formCatalogId').value=catId;							
		document.getElementById('workzone.deleteCatalog.form.catalogName').innerHTML=catName;
		document.getElementById('workzone.deleteCatalog.form.catalogComment').innerHTML=catComment;									 							
		document.getElementById('workzone.deleteCatalog.form.modal').style.display='table';
	}

	
	insertSpot.append(newCatNode);
}
</script>
<!-- Modal window for Delete catalog -->
		<form id="workzone.deleteCatalog.form" action="<c:url value="/deleteCatalogProcess" />" method="post" >
			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			<input type="hidden"  id="workzone.deleteCatalog.form.formCatalogId" name="formCatalogId" value=""/>
				<!-- Create Element Modal window -->
			<div class="modal_shadow" id="workzone.deleteCatalog.form.modal" ><div class="modal_back">
			<fieldset class="modal" style="width:400px">
				<legend>			
		       		<a href="#close" title="Close" class="modalclose" 
		       		onclick="document.getElementById('workzone.deleteCatalog.form.modal').style.display='none';">X</a>
		       	</legend>	
		   		<h4 class="negative" >
		   			<s:text name="workzone.deletecatalog" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" /> 
		   		</h4>
		   		<center>
		   		<table>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.name" /></span></td><td><span id="workzone.deleteCatalog.form.catalogName"></span></td></tr>
			   		<tr><td><span class="fieldtitle"><s:text name="workzone.element.comment" /></span></td><td><span id="workzone.deleteCatalog.form.catalogComment"></span></td></tr>
		   		</table>
		   		</center>
		   		<br/>
		   		<a href="#" onclick="document.getElementById('workzone.deleteCatalog.form').submit()" ><span class="bigchoice"><s:text name="global.submit" /></span></a>
	    	
	    	</fieldset></div></div>
		</form>	
		
<div id="workzone.catalogslist" >

		<!-- Catalogs List -->
	    <fieldset class="catalogslist" >
	    	<legend><span class="fieldsetTitle" ><s:text name="workzone.catalogsList" /></span></legend>
	    	
			    <form id="workzone.addCatalog.form" action="<c:url value="/addCatalogProcess" />" method="post" >    
	    	<span class="dynamicCatalog modifyable">
	    		
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	        		
	    		<span  onclick="event.stopPropagation();this.style.display='none'; 
	    						document.getElementById('workzone.addCatalog.form.catalogName.edit').style.display='block';">
	    			<s:text name="workzone.icon.createElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />
	    		</span>
	    		
	    		<input id="workzone.addCatalog.form.catalogName.edit" type="text"
	    										name="newCatalogName"
												style="display:none;"  
												placeholder="<s:text name="workzone.createnewelement.enterName"/>"
												onchange="event.stopPropagation();
															document.getElementById('workzone.addCatalog.form').submit();"
												onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();" 
											/>
				
	    	</span>
	    	</form>
	    	
	        			    	
       		<table>	    		    	
	    	    
			        <tr id="_template_catalog_summary_" style="display:none" >
			         <td class="_colnode_" >
			        	<table class="_table_">	<tr>	
						<td>
			        	<span  class="_contents_" >
									<a href="#selectCatalog" class="_link_" id="workzone.catalogdetails.catalogname.selector." ></a>
									
											<input type="text" style="display:none;"
												id="workzone.catalogdetails.catalogname.edit."
												class="_catalogNameInput_"  
												onfocus="this.select();" 
												onblur="this.style.display='none';														
														document.getElementById('workzone.catalogdetails.catalogname.selector').style.display='block';"
												onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();"
											/>
									<span class="_catNbElements_ comment"></span>
						
						
						</span></td>
						<td>
						<a  href="#"  
							id="workzone.catalogsList.icon.deleteElement."
							title="<s:text name="workzone.icon.deleteElement" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
			 				class="_deletecatalogIcon_ tinyicon icon_deleteElement" style="display:none;"  >
			 				
		 				</a>						
							
						</td>
						</tr></table>
					
						       		
		       		</td></tr>			    	
					<span id="_catalogsList_insertspot_"></span>			    
	   		</table>
	   		
		</fieldset>
		
<!-- Current catalog details -->
		<fieldset id="workzone.catalogdetails"><legend><span class="fieldsetTitle" ><s:text name="workzone.catalogDetails" /></span></legend>
			<form id="workzone.modifycatalog.form" action="<c:url value="/updateCatalogProcess" />" method="post" >
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	    			<input type="hidden" id="workzone.modifycatalog.form.formCatalogName"  name="formCatalogName" value="" />
			<table>

				<tr><td colspan="2">
							<span id="workzone.catalogdetails.catalogcomment.readonly" ></span>
					
						<input id="workzone.catalogdetails.catalogcomment.edit" type="text"
							style="display:none;"  
							 name="formCatalogComment" 
							 value="" 					
							 placeholder="<s:text name="catalog.comment"/>"							
							 onchange="document.getElementById('workzone.modifycatalog.form').submit();" 
							 onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();"/>
										
				</td></tr>
				<tr><td colspan="2">
					<span class="emptyModifyable" id="workzone.catalogdetails.catalogsearchquery.empty" style="display:none" >
						<s:text name="workzone.clickToAdd" /> <s:text name="workzone.catalog.searchQuery" />
					</span>
					<span id="workzone.catalogdetails.catalogsearchquery.title" style="display:none" class="comment"><s:text name="workzone.catalog.searchQuery"/></span>
					<span id="workzone.catalogdetails.catalogsearchquery.readonly" > </span>
					
						<input id="workzone.catalogdetails.catalogsearchquery.edit" type="text"  name="formCatalogSearchQuery" value="" 
								style="display:none;"
								onchange="document.getElementById('workzone.modifycatalog.form').submit();" 
								onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();"/>
														
				</td></tr>
				<tr><td colspan="2"><hr/></td></tr>
				<tr><td colspan="2">
					<table>
						<tr><td><s:text name="workzone.catalog.static"/>:</td><td><span id="workzone.catalogdetails.nbStaticElements"></span></td></tr>
						<tr><td><s:text name="workzone.catalog.dynamic"/>:</td><td><span id="workzone.catalogdetails.nbDynamicElements"></span></td></tr>
					</table>
				</td></tr>		
				<tr>
					<td>
					<a  href="#" id="workzone.thumbnails.icon.toggleHide" 
			 				title="<s:text name="workzone.icon.toggleHideThumbnails" />" 
			 				class="icon icon_gridview" 
			 				onclick="event.stopPropagation();
			 					toggleThumbnailsHide();
				 				"></a>
	 				
					</td>
					<td>
						<a  href="#" id="workzone.thumbnails.icon.createNewElement" 
			 				title="<s:text name="workzone.icon.createElement" /> <s:property value="selectedCommunity.vocabulary.elementTraduction" />" 
			 				class="icon icon_createElement" 
			 				onclick="event.stopPropagation();document.getElementById('workzone.createElement.form.modal').style.display='table';"></a>
			 			
					</td>
				</tr>								
			</table>
			</form>
		</fieldset>
		
		
	</div>
		
