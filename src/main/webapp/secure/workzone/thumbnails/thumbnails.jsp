<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>
	function updateElementThumbnail(elementData, previousSelectedElementId) {
		
		if (previousSelectedElementId!=null) {			
			var prevElementThumbnail = document.getElementById("workzone.thumbnails."+previousSelectedElementId);
			if (prevElementThumbnail!=null) {
				prevElementThumbnail.classList.remove("selectedErrorTemplateSlideItem");
				prevElementThumbnail.classList.remove("selectedSlideItem");
				prevElementThumbnail.classList.remove("selectedTemplateSlideItem");
			}
		}
		
		// on first load, thumbnail might not be loaded yet, but then no need to run following commands
		var elementThumbnail = document.getElementById("workzone.thumbnails."+elementData.elementId);
		if (elementThumbnail!=null) {
			if (elementData.templateLoadError) { elementThumbnail.classList.add("selectedErrorTemplateSlideItem"); }
			else { elementThumbnail.classList.add("selectedSlideItem"); }
			if (elementData.template) { elementThumbnail.classList.add("selectedTemplateSlideItem"); }
		}		
	}
</script>
	

<center>


<!-- Current catalog elements list container -->
 <fieldset id="workzone.thumbnails" class="menushadowcard" style="padding-bottom:20px" onclick="clearMultiSelection();" >
 
 		<legend>
 		<table><tr>
 		<td>
 				
 				
		</td>
   		<td>			
   				<!-- Maximize -->
   				<a  href="#" id="workzone.thumbnails.icon.maximize" 
 				title="<s:text name="workzone.icon.maximize" />" 
 				class="icon icon_gridview" 
 				onclick="event.stopPropagation();
 					document.getElementById('workzone.thumbnails.container').maximize();
	 				"></a>
	 				
	 			<!-- Minimize -->
   				<a  href="#" id="workzone.thumbnails.icon.minimize" 
 				title="<s:text name="workzone.icon.minimize" />" 
 				class="icon icon_slideview" 
 				style="display:none"
 				onclick="event.stopPropagation();
 					document.getElementById('workzone.thumbnails.container').minimize();
	 				"></a>	
	 		

			</td>
	 		<td >
	 				  <fieldset id="workzone.thumbnails.icon.multiselectCmds" class="multiselectCmds_fieldset" 
	 				  			style="display:none;">
	 				  	<legend class="multiselectCmds_legend"><span id="workzone.thumbnails.icon.multiselectCmds.nbSelectedElems"></span> 
	 				  										 <s:text name="workzone.icon.selected" />
						</legend>
						
		 						
			 			<a  href="#" id="workzone.thumbnails.icon.removeElements" 
		 						
				 				title="<s:text name="workzone.icon.removeElements" /> <s:property value="selectedCommunity.vocabulary.catalogTraduction" />" 
				 				class="icon icon_removeElement"  ></a>
					   
	 					<a  href="#" id="workzone.thumbnails.icon.deleteElements" 
	 						 
			 				title="<s:text name="workzone.icon.deleteElements" /> <s:property value="selectedCommunity.vocabulary.elementsTraduction" />" 
			 				class="icon icon_deleteElement" 			 				
			 				onclick="event.stopPropagation();deleteMultiSelectedElements();"></a>
			 				
	 				</fieldset>
	 		</td>
	 		<td>
 				<a  href="#" id="workzone.thumbnails.icon.deleteAll" 
 				title="<s:text name="workzone.icon.deleteAll" /> <s:property value="selectedCommunity.vocabulary.elementsTraduction" />" 
 				class="" 
 				onclick="event.stopPropagation();deleteAllElements();">
 				DeleteAll
 				</a>	 				
			</td>
	 		<td>
	 		
	    	<!-- Real Fast Search input 
	    	list="workzone.thumbnails.elementsInnerSearch.datalist" autocomplete="on"
	    	<datalist id="workzone.thumbnails.elementsInnerSearch.datalist">   					   					
   				</datalist>
	    	-->	 		
 			<input type="text" name="elementsFastSearch" id="workzone.thumbnails.elementsFastSearch" style="display:none"
	    			placeholder="<s:text name="workzone.catalog.innerSearch" />" 
	    			
	    			onkeypress="event.stopPropagation();if (event.which==13||event.keycode==13) {
	    				updateThumbnailsContents(document.getElementById('workzone.thumbnails.elementsFastSearch').value);
		    		}"
		    		onkeydown="event.stopPropagation();"
	    	>
	    	<span id="thumbnails-loadprogress" style="width:150px; display:none"  >	    	
					<div id="thumbnails-loadprogress-container" class="progressbar_container">
						<div id="thumbnails-loadprogress-bar" class="progressbar"></div>
						<div id="thumbnails-loadprogress-text" class="progressbar_text"></div>
					</div>
	    	</span>
   					
	 		</td>
	 		<!-- number of elements matching current search -->
	 		<td>
 				<span id="workzone.thumbnails.nbDisplayedThumbnails" class="thumbnailsCountDisplayed"> 
 				</span>	 	
 							
			</td>
	 		<td>
 				<span id="workzone.thumbnails.nbMatchingElements" class="thumbnailsCountTotal"> 
 				0 <s:property value="selectedCommunity.vocabulary.elementTraduction" />
 				</span>	 	
 							
			</td>

			 		</tr></table>	
 		</legend>
 	
			      
	<!-- Items container itself -->
	<div  id="workzone.thumbnails.container" class="slideItemsContainer" 
		oncontextmenu="return false;"  >
	   		      			      
	   			
	</div>	
</fieldset>


</center>

<s:include value="thumbnails.modals.jsp" ></s:include>
<s:include value="javascript.jsp" ></s:include>
