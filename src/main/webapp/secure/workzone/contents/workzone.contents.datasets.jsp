<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<s:include value="/secure/workzone/contents/workzone.contents.dropzone.dataset.jsp" />
<s:include value="/secure/workzone/contents/workzone.contents.metadatas.jsp" />

<script>



// d is 'dataset json stucture' coming from websockets transmission from server
function elementContents_addDataset(d) {
		
	var newDatasetNode=document.getElementById('elementContents__template_dataset_').cloneNode(true);
	newDatasetNode.id="content.dataset."+d.datasetId;	
	newDatasetNode.datasetId=d.datasetId;
	
	var insertSpotNode = document.getElementById('elementContents_datasets_insertspot'); 	 	
 	insertSpotNode.appendChild(newDatasetNode);
		
	if (d.readOnly) { newDatasetNode.classList.add("datasetcontentRO");}
	else { newDatasetNode.classList.add('datasetcontent'); }
	if (!d.layoutDoDisplayName) { newDatasetNode.classList.add("hiddenfieldset");}
	
	newDatasetNode.select=function() {
		selected_dataset[d.datasetId]=true;			
		
		this.classList.remove('editModeDataset');
		this.classList.remove('editModeDatasetRO');
		this.classList.remove('editModeDatasetMouseOver');
		this.classList.remove('editModeDatasetMouseOverRO');
		
		if (d.readOnly) { this.classList.add('editModeDatasetSelectedRO'); }
		else { this.classList.add('editModeDatasetSelected'); }
	}
	
	newDatasetNode.deselect=function() {
		selected_dataset[d.datasetId]=false;			
		
		this.classList.remove('editModeDatasetSelectedRO');
		this.classList.remove('editModeDatasetSelected');
		
		if (d.readOnly) {  this.classList.add('editModeDatasetRO'); }
		else { this.classList.add('editModeDataset'); }
	}
	
	newDatasetNode.enterEditMode=function() {
		
		this.classList.remove('hiddenfieldset');
		if (d.readOnly) { this.classList.add('editModeDatasetRO'); }
		else { this.classList.add('editModeDataset'); }
		this.draggable=true;
	}
	
	newDatasetNode.quitEditMode=function() {
		
		if (!d.layoutDoDisplayName) { this.classList.add('hiddenfieldset'); }
		this.classList.remove('editModeDataset');
		this.classList.remove('editModeDatasetRO');
		this.draggable=false;
	}
	// onclick
	newDatasetNode.onclick=function(event) { if (editModeActive) { event.stopPropagation(); switchSelected_dataset(d.datasetId); } }
	
	// onmouseover
	newDatasetNode.onmouseover=
	function() { 
		if (editModeActive) { 
			if (selected_dataset[d.datasetId]==false) {  
					document.getElementById('selector.dataset.'+d.datasetId).classList.add('editModeDatasetMouseOverMenu'); 					
					if (d.readOnly) { document.getElementById("content.dataset."+d.datasetId).classList.add('editModeDatasetMouseOverRO'); }
					else { document.getElementById("content.dataset."+d.datasetId).classList.add('editModeDatasetMouseOverRO'); }
			}
			
			if (!d.readOnly) { document.getElementById("workzone.contents.icon.delete.dataset."+d.datasetId).style.display='block'; }									
	  	}
	}
	 	
	// onmouseout	
	newDatasetNode.onmouseout=
	function() {
		if (editModeActive) { 	
    		if (selected_dataset[d.datasetId]==false) { 
				document.getElementById("selector.dataset."+d.datasetId).classList.remove('editModeDatasetMouseOverMenu');
				if (d.readOnly) { document.getElementById("content.dataset."+d.datasetId).classList.remove('editModeDatasetMouseOverRO'); }
				else { document.getElementById("content.dataset."+d.datasetId).classList.remove('editModeDatasetMouseOverRO'); }					    					
		 	}
		 	if (!d.readOnly) { document.getElementById("workzone.contents.icon.delete.dataset."+d.datasetId).style.display='none'; } 			 				    			 		   						    			 
	 	}
	}
	
 	// ondragstart
 	newDatasetNode.ondragstart=
 	function(event) { 
 		event.stopPropagation();
 		if (editModeActive && !d.readOnly) { 
 			handleDragStartDataset(d.datasetId,document.getElementById('content.dataset.'+d.datasetId),event); 
 		}
 	}
							
 	// ondragend
 	newDatasetNode.ondragend=
	function(event) {
		event.stopPropagation();
		handleDragEndDataset(document.getElementById('content.dataset.'+d.datasetId),event); 	
 	}
 	
 	// legend title
 	var legendTitle = newDatasetNode.querySelector("._legendtitle_");
 	legendTitle.title=d.datasetComment;
 	legendTitle.innerHTML=d.datasetName;
 	
 	// icon Delete
	var iconDelete = newDatasetNode.querySelector(".icon_deleteElement");
 	if (d.readOnly) { iconDelete.id+="."+d.datasetId+"__disabled"; }
 	else {
		iconDelete.id+="."+d.datasetId;
		iconDelete.onclick=
		function(event) {
			event.stopPropagation();
			document.getElementById('workzone.deleteDataset.form.formDatasetId').value=d.datasetId;
			document.getElementById('workzone.deleteDataset.form.datasetName').innerHTML=d.datasetName;
			document.getElementById('workzone.deleteDataset.form.datasetComment').innerHTML=d.datasetComment;									 							
			document.getElementById('workzone.deleteDataset.form.modal').style.display='table';
		}
 	}
 	
 	var metadataTableRow = newDatasetNode.querySelector("._elementContents__template_metadata_table_row_");
 	metadataTableRow.id="elementContents__dataset_"+d.datasetId+"_metadata_table_row";
 	metadataTableRow.classList="";
 	
 	
 	var metadataTableColTemplate = metadataTableRow.querySelector("._elementContents__template_metadata_table_col_"); 	
 	
 	for (var i=0;i<d.layoutNbColumns;i++) {
 		var colNb=i+1;
 		var colNodeId="elementContents__dataset_"+d.datasetId+"_metadata_col_"+colNb;
 		var colNode = metadataTableColTemplate.cloneNode(true);
 		colNode.classList="";
 		colNode.id=colNodeId;
 		colNode.style.display='table-cell';
 		
 		metadataTableRow.append(colNode);
 		
 		var shouldInsertDropZones = !curElementData.readOnly && !curElementData.templated;
 	 	if (shouldInsertDropZones) {
 	 		
 	 		// add dropzone
	 		var metadataDropSpotNode = makeDropzoneNode(colNb,0, d.datasetId);	
	 		colNode.append(metadataDropSpotNode);	
	 		
	 		// insert zone
	 		var metadataInsertZone = makeInsertzoneNode(colNb,0, d.datasetId); 		
	 		colNode.append(metadataInsertZone); 

 	 	}
 	}
 	if (d.nbMetadata==0) {  newDatasetNode.querySelector(".elementContents__template_emptymsg_").style.display='block'; } 
 	 	 	  	
 	var shouldInsertDropZones = !curElementData.readOnly && !curElementData.templated; 
 	if (shouldInsertDropZones) { insert_dataset_dropzone(insertSpotNode,d.layoutPosition+1, d.elementId); }
 
	newDatasetNode.style.display='block';
			
}


</script>
	
	
	<span id="elementContents_datasets_insertspot" ></span>
					
	
	<fieldset  id="elementContents__template_dataset_" 
		style="margin-top:20px;font-size:1em;display:none;"	  
		draggable="false" >
	
		<legend >    	    				
			<table><tr>
				<td><span class="_legendtitle_ fieldsetTitle" title="" ></span></td>
				<td><a  href="#"  id="workzone.contents.icon.delete.dataset"
						title="<s:text name="workzone.icon.deleteDataset" />" 
						class="smallicon icon_deleteElement" style="display:none;"  >								 				
				</a></td>
			</tr></table>							 													
		</legend>
    	    				
		<h3 class="elementContents__template_emptymsg_" style="display:none" ><span class="negative"><s:text name="workzone.empty" ></s:text></span></h3>
    	
    	<table style="width:100%;">
    		<tr class="_elementContents__template_metadata_table_row_">
    			<td class='_elementContents__template_metadata_table_col_' style="display:none" ></td>
    		</tr>
    	</table>
   
</fieldset>
		       		
		       			       			    			       
			 
			    
