<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


	    	 			    

<!-- Dataset Drop Zone  -->		       			
	<div id="dropzone.dataset.${param.dropPosition}" class="dropzone_dataset" 
 		ondragover="handleDragOverDataset(document.getElementById('dropzone.dataset.${param.dropPosition}'),event);"
		ondragenter="handleDragEnterDataset(document.getElementById('dropzone.dataset.${param.dropPosition}'),event);"
		ondragleave="handleDragLeaveDataset(document.getElementById('dropzone.dataset.${param.dropPosition}'),event);"
		ondrop="handleDropDataset(${param.dropPosition},document.getElementById('dropzone.dataset.${param.dropPosition}'),event);"								
	>
	<s:text name="workzone.contents.dropheredataset" />
	</div>
			
					       			    
<!-- Dataset Insert Zone  -->			       		
		       			
			    
			<!-- Insert zone -->
		<div 	id="insertzone.dataset.${param.dropPosition}" class="insertzone_dataset" 
				class="insertzone_dataset" 				
				title="Insert new Dataset"
		 		onclick="event.stopPropagation();
							displayNewDatasetForm(${param.dropPosition})"
			> 						
			<span class="positive"> <s:text name="workzone.createnewdataset"/> <s:property value="selectedCommunity.vocabulary.datasetTraduction" /></span>
	  				
			<div id="insertzone.dataset.${param.dropPosition}.inputs" 
				onclick="event.stopPropagation();"
				class="insertzoneDatasetInputs"
				style="border:1px solid white">
			
		 	<table >
		 			<tr><td>
	  						<input id="insertzone.dataset.${param.dropPosition}.datasetName" 
	  							placeholder="<s:text name="workzone.createnewdataset.enterName" />"
	  							type="text" onclick="event.stopPropagation();" />
	  				</td></tr>
	 				<tr><td>
	 						<input  id="insertzone.dataset.${param.dropPosition}.datasetComment"
	 								placeholder="<s:text name="workzone.createnewdataset.enterComment" />" 
	 								type="text" onclick="event.stopPropagation();" />
					</td></tr>	   			
	 										   					 
	 				<tr><td>
	 					<span class="clickable" onclick="event.stopPropagation();addNewDataset(
	 																		<s:property value="selectedCatalog.selectedElement.elementId" />,
	 																		document.getElementById('insertzone.dataset.${param.dropPosition}.datasetName').value,
	 																		document.getElementById('insertzone.dataset.${param.dropPosition}.datasetComment').value,	 																		
	 																		${param.dropPosition});" ><s:text name="global.submit" /></span>
	 					<span class="clickable" onclick="event.stopPropagation();cancelNewDatasets();" ><s:text name="global.cancel" /></span>
	 				</td></tr>
			</table>
			</div> 
</div>
