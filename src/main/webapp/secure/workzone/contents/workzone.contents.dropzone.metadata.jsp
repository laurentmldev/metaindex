<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function makeInsertzoneNode(dropColumn,dropPosition,curDatasetId) {
	
	var idsuffix="dataset_"+curDatasetId+"."+dropColumn+"."+dropPosition;
	
	var metadataInsertZone = document.getElementById("_elementContents.template.metadata.insertzone_").cloneNode(true);	
	metadataInsertZone.id="insertzone.metadata."+idsuffix;
	metadataInsertZone.classList="insertzone_metadata";	
	metadataInsertZone.onclick=function(event) {
			event.stopPropagation();
			displayNewMetadataForm(curDatasetId,dropColumn,dropPosition);			
	}	
	
	
	var typeElId="insertzone.metadata."+idsuffix+".metadataType";
	
	// overall div for metadata inputs
	var inputs = metadataInsertZone.querySelector("._inputs_");
	inputs.id="insertzone.metadata."+idsuffix+".inputs";
	
	// name
	var name = metadataInsertZone.querySelector("._input_name_");
	name.id="insertzone.metadata."+idsuffix+".metadataName";
	name.onkeypress=function(event) { if (event.which==13||event.keycode==13) { document.getElementById(typeElId).focus(); } }
	
	// comment
	var comment = metadataInsertZone.querySelector("._input_comment_");
	comment.id="insertzone.metadata."+idsuffix+".metadataComment";
	
	// type
	var type = metadataInsertZone.querySelector("._input_type_");
	type.id=typeElId;
	
	// add node
	var addNode = metadataInsertZone.querySelector("._addnode_");
	addNode.onclick= function(event) { 
			event.stopPropagation();
			addNewMetadata(curDatasetId, name.value, comment.value, type.value,dropColumn,dropPosition);					
	}
	return metadataInsertZone;
}


function makeDropzoneNode(dropColumn,dropPosition,curDatasetId) {
	
	var idsuffix="dataset_"+curDatasetId+"."+dropColumn+"."+dropPosition;
	
	var metadataDropSpotNode = document.getElementById("_elementContents.template.metadata.dropzone_").cloneNode(true);
	metadataDropSpotNode.id="dropzone.metadata."+idsuffix;
	metadataDropSpotNode.classList="dropzone_metadata"; 
	
	metadataDropSpotNode.ondragover=function(event) { handleDragOverMetadata(metadataDropSpotNode,event); }
	metadataDropSpotNode.ondragenter=function(event) { handleDragEnterMetadata(metadataDropSpotNode,event); }
	metadataDropSpotNode.ondragleave=function(event) { handleDragLeaveMetadata(metadataDropSpotNode,event); }
	metadataDropSpotNode.ondrop=function(event) { 
		event.preventDefault();
		event.stopPropagation();
		handleDropMetadata(dropColumn,dropPosition,curDatasetId,metadataDropSpotNode,event); 
	}		
	
	return metadataDropSpotNode;
}


</script>


	<!-- Drop zone -->
	   	 	<div 	id="_elementContents.template.metadata.dropzone_"
	   	 			style="display:none;">
	   	 			<s:text name="workzone.contents.dropheremetadata" />
 			</div>
			
			    
	<!-- Insert zone -->
		<div 	id="_elementContents.template.metadata.insertzone_"  
				style="display:none;"
				title="Insert new Metadata"
				> 						
			<span class="positive"><s:text name="workzone.createnewmetadata"/> <s:property value="selectedCommunity.vocabulary.metadataTraduction" /></span>
				
			<div onclick="event.stopPropagation();"
				class="insertzoneMetadataInputs _inputs_"
				style="border:1px solid white;">
			
	
		 		<table >		 			
	  				<tr><td>
	  						<input class="_input_name_" 
	  							placeholder="<s:text name="workzone.createnewmetadata.enterName" />"
	  							type="text" onclick="event.stopPropagation();" 
	  							onkeypress="event.stopPropagation();"
								onkeydown="event.stopPropagation();"/>
	  				</td></tr>
	 				<tr><td>
	 						<input  class="_input_comment_"
	 								placeholder="<s:text name="workzone.createnewmetadata.enterComment" />" 
	 								type="text" onclick="event.stopPropagation();" 
	 								onkeypress="event.stopPropagation();"
									onkeydown="event.stopPropagation();"/>
					</td></tr>	   			
	 				<tr><td>
	 						<select  class="_input_type_" onclick="event.stopPropagation();">
	 							<s:iterator value="selectedCommunity.terms" var="curTerm">
	 								<option value="<s:property value="#curTerm.termId"/>" ><s:property value="#curTerm.idName"/></option>
	 							</s:iterator>
							</select>
					</td></tr>						   					 
	 				<tr><td>
	 					<span class="clickable _addnode_" ><s:text name="global.submit" /></span>
	 					<span class="clickable" onclick="event.stopPropagation();cancelNewMetadatas();" ><s:text name="global.cancel" /></span>
	 				</td></tr>  			
				</table>
			</div> 
</div>

