<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


	    		
	<!-- Drop zone -->
	   	 	<div 	class="dropzone_metadata"
	   	 			id="dropzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}"  
		  	   	 	ondragover="handleDragOverMetadata(document.getElementById('dropzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}'),event);"
					ondragenter="handleDragEnterMetadata(document.getElementById('dropzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}'),event);"
					ondragleave="handleDragLeaveMetadata(document.getElementById('dropzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}'),event);"
					ondrop="event.preventDefault();event.stopPropagation();handleDropMetadata(${param.dropColumn},${param.dropPosition},${param.dropDatasetId},document.getElementById('dropzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}'),event);"
													
				>		 
				<s:text name="workzone.elementcontents.dropheremetadata" />				
			</div>
			
			    
			<!-- Insert zone -->
		<div 	id="insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}" 
				class="insertzone_metadata" 				
				title="Insert new Metadata"
		 		onclick="event.stopPropagation();
							displayNewMetadataForm(${param.dropDatasetId},${param.dropColumn},${param.dropPosition})"
			> 						
			<span class="positive"><s:text name="workzone.createnewmetadata"/> <s:property value="selectedCommunity.vocabulary.metadataTraduction" /></span>
			
			<div id="insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.inputs" 
				onclick="event.stopPropagation();"
				class="insertzoneMetadataInputs" >
	
		 	<table >
		 			
	  				<tr><td>
	  						<input id="insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.metaDataName" 
	  							placeholder="<s:text name="workzone.createnewmetadata.enterName" />"
	  							type="text" onclick="event.stopPropagation();" />
	  				</td></tr>
	 				<tr><td>
	 						<input  id="insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.metaDataComment"
	 								placeholder="<s:text name="workzone.createnewmetadata.enterComment" />" 
	 								type="text" onclick="event.stopPropagation();" />
					</td></tr>	   			
	 				<tr><td>
	 						<select id="insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.metaDataType" 
	 								onclick="event.stopPropagation();" >
	 							<s:iterator value="selectedCommunity.terms" var="curTerm">
	 								<option value="<s:property value="#curTerm.termId"/>" ><s:property value="#curTerm.idName"/></option>
	 							</s:iterator>
	 						</select>						   					
	 				</td></tr>						   					 
	 				<tr><td>
	 					<span class="clickable" onclick="event.stopPropagation();addNewMetadata(${param.dropDatasetId},
	 																		document.getElementById('insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.metaDataName').value,
	 																		document.getElementById('insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.metaDataComment').value,
	 																		document.getElementById('insertzone.metadata.dataset_${param.dropDatasetId}.${param.dropColumn}.${param.dropPosition}.metaDataType').value,
	 																		${param.dropColumn},${param.dropPosition})" ><s:text name="global.submit" /></span>
	 					<span class="clickable" onclick="event.stopPropagation();cancelNewMetadatas();" ><s:text name="global.cancel" /></span>
	 				</td></tr>
			</table>
			</div> 
</div>
			
