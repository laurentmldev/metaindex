<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function insert_dataset_dropzone(insertSpotNode,dropPosition, curElementId) {
	
	// drop zone
	var datasetDropSpotNode = document.getElementById("_elementContents.template.dataset.dropzone_").cloneNode(true);
	datasetDropSpotNode.id="dropzone.dataset."+dropPosition;
	datasetDropSpotNode.classList="dropzone_dataset"; 
	
	datasetDropSpotNode.ondragover=function(event) { handleDragOverDataset(datasetDropSpotNode,event); }
	datasetDropSpotNode.ondragenter=function(event) { handleDragEnterDataset(datasetDropSpotNode,event); }
	datasetDropSpotNode.ondragleave=function(event) { handleDragLeaveDataset(datasetDropSpotNode,event); }
	datasetDropSpotNode.ondrop=function(event) { handleDropDataset(dropPosition,datasetDropSpotNode,event); }
	
	insertSpotNode.append(datasetDropSpotNode);
	
	// insert zone
	var datasetInsertSpotNode = document.getElementById("_elementContents.template.dataset.insertzone_").cloneNode(true);
	datasetInsertSpotNode.id="insertzone.dataset."+dropPosition;
	datasetInsertSpotNode.classList="insertzone_dataset";
	
	datasetInsertSpotNode.onclick=function(event) {  
			event.stopPropagation();
			displayNewDatasetForm(dropPosition);
	}
	
	// inputs
	var inputs = datasetInsertSpotNode.querySelector("._inputs_");
	inputs.id="insertzone.dataset."+dropPosition+".inputs";
	
	// name
	var name = datasetInsertSpotNode.querySelector("._input_name_");
	name.id="insertzone.dataset."+dropPosition+".datasetName";
	
	// comment
	var comment = datasetInsertSpotNode.querySelector("._input_comment_");
	comment.id="insertzone.dataset."+dropPosition+".datasetComment";
	
	// add node
	var addNode = datasetInsertSpotNode.querySelector("._addnode_");
	addNode.onclick= function(event) { 
			event.stopPropagation();
			addNewDataset(curElementId, name.value, comment.value, dropPosition);					
	}
	
	insertSpotNode.append(datasetInsertSpotNode);
	
}

</script>

<!-- Dataset Drop Zone  -->		       			
	<div id="_elementContents.template.dataset.dropzone_" style="display:none"	>
		<s:text name="workzone.contents.dropheredataset" />
	</div>
			
					       			    
<!-- Dataset Insert Zone  -->			       		
		       			
			    
	<!-- Insert zone -->
<div 	id="_elementContents.template.dataset.insertzone_"  
		style="display:none"
		title="Insert new Dataset"		 		
	> 						
		<span class="positive"> <s:text name="workzone.createnewdataset"/> 
			<s:property value="selectedCommunity.vocabulary.datasetTraduction" />
		</span>
  				
		<div onclick="event.stopPropagation();"
			class="insertzoneDatasetInputs _inputs_"
			style="border:1px solid white">
			
		 	<table >
		 			<tr><td>
	  						<input class="_input_name_" 
	  							placeholder="<s:text name="workzone.createnewdataset.enterName" />"
	  							type="text" onclick="event.stopPropagation();" 
	  							onkeypress="event.stopPropagation();"
								onkeydown="event.stopPropagation();"/>
	  				</td></tr>
	 				<tr><td>
	 						<input  class="_input_comment_"
	 								placeholder="<s:text name="workzone.createnewdataset.enterComment" />" 
	 								type="text" onclick="event.stopPropagation();" 
	 								onkeypress="event.stopPropagation();"
									onkeydown="event.stopPropagation();"/>
					</td></tr>	   			
	 										   					 
	 				<tr><td>
	 					<span class="clickable _addnode_" ><s:text name="global.submit" /></span>
	 					<span class="clickable" onclick="event.stopPropagation();cancelNewDatasets();" ><s:text name="global.cancel" /></span>
	 				</td></tr>
			</table>
		</div> 
</div>
