<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<s:include value="/secure/workzone/contents/workzone.contents.dropzone.metadata.jsp" />

<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.1_WebLink.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.1_WebLinkRO.jsp" />

<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.4_Image.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.4_ImageRO.jsp" />

<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.10_TinyText.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.10_TinyTextRO.jsp" />

<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.5_LongText.jsp" />
<s:include value="/secure/workzone/contents/metadatatypes/workzone.contents.5_LongTextRO.jsp" />

<script>


function elementContents_addMetadataColumn(datasetId, colId, metadataId,insertdropzoneBefore) {	
	
	var colNode = document.getElementById("elementContents__dataset_"+datasetId+"_metadata_col_"+colId);
	
	var metadataContents = curElementMetadatas[metadataId];
	
	var metadataNode = document.getElementById("elementContents__template_metadata_").cloneNode(true);
	metadataNode.id="content.metadata."+metadataId;
	metadataNode.title=metadataContents.metadataComment;
	
	metadataNode.classList.add("metadataalign"+metadataContents.layoutAlign);
	
	var isReadOnly = metadataContents.readOnly;
	var isDeletable = !metadataContents.readOnly;
	
	if (isReadOnly) {
		metadataNode.classList.add("metadatacontentRO");
	} else {
		metadataNode.classList.add("metadatacontent");
		
	}
	
	metadataNode.onclick=function(event) {
		event.stopPropagation(); 
		if (editModeActive) { switchSelected_metadata(metadataId); }	
	}
	
	metadataNode.onmouseover=function(event) {
		if (editModeActive) { 
				if (selected_metadata[metadataId]==false) { 
					document.getElementById('selector.metadata.'+metadataId).classList.add('editModeMetadataMouseOverMenu');
					
					if (isReadOnly) {
						document.getElementById('content.metadata.'+metadataId).classList.add('editModeMetadataMouseOverRO');
						document.getElementById('content.dataset.'+datasetId).classList.remove('editModeDatasetMouseOverRO');	
					} else {
						document.getElementById('content.metadata.'+metadataId).classList.add('editModeMetadataMouseOver');
						document.getElementById('content.dataset.'+datasetId).classList.remove('editModeDatasetMouseOver');
					}
					
				}
				if (isDeletable) {
					document.getElementById('workzone.contents.icon.delete.metadata.'+metadataId).style.display='block';
				}				
		 }
	}
	
	metadataNode.onmouseout=function(event) {
		if (editModeActive) {
				if (selected_metadata[metadataId]==false) { 					
					
					document.getElementById('selector.metadata.'+metadataId).classList.remove('editModeMetadataMouseOverMenu');
					
					if (metadataContents.readOnly) {
						document.getElementById('content.metadata.'+metadataId).classList.remove('editModeMetadataMouseOverRO');
					} else {
						document.getElementById('content.metadata.'+metadataId).classList.remove('editModeMetadataMouseOver');
					}
					if (selected_dataset[metadataId]==false) {
						if (curElementDatasets[datasetId].readOnly) {
							document.getElementById('content.dataset.'+datasetId).classList.add('editModeDatasetMouseOverRO');	
						} else {
							document.getElementById('content.dataset.'+datasetId).classList.add('editModeDatasetMouseOver');
						}
						 
					}
				}
				if (isDeletable) {
					document.getElementById('workzone.contents.icon.delete.metadata.'+metadataId).style.display='none';
				}								  					
		 }
	}
	
	metadataNode.ondragstart=function(event) {
		event.stopPropagation(); 
		if (editModeActive && !isReadOnly && isDeletable) 
		{ 
			handleDragStartMetadata(metadataId,document.getElementById('content.metadata.'+metadataId),event); 
		}
	}
	
	metadataNode.ondragend=function(event) {
		event.stopPropagation();
		handleDragEndMetadata(document.getElementById('content.metadata.'+metadataId),event);
	}
	
	// delete Icon
	if (isDeletable) {
		var deleteIcon = metadataNode.querySelector("._deleteIcon_");
		deleteIcon.id+="."+metadataId;
		deleteIcon.onclick=function(event) {
			event.stopPropagation();
			document.getElementById('workzone.deleteMetadata.form.formMetadataId').value=metadataId;							
			document.getElementById('workzone.deleteMetadata.form.id').innerHTML=metadataId;
			document.getElementById('workzone.deleteMetadata.form.datasetName').innerHTML=metadataContents.metadataName;
			document.getElementById('workzone.deleteMetadata.form.datasetComment').innerHTML=metadataContents.metadataComment;									 							
			document.getElementById('workzone.deleteMetadata.form.modal').style.display='table';
		}
	}
	
	// Edit form
	var editForm = metadataNode.querySelector("._editForm_");
	editForm.id+=metadataId+".form";
	
	// insertspot
	var insertSpot = metadataNode.querySelector("._contents_insertspot_");
	insertSpot.id="metadata.contents.insertspot."+metadataId;
	insertSpot.classList.add("metadata"+metadataContents.layoutSize+"size");
	
	var shouldInsertDropZones = !curElementData.readOnly && !curElementData.templated;  	
 	
 	colNode.append(metadataNode); 	  	 	
	
	// Insert contents
	if (metadataContents.metadataType=='web-link') { insert_metadata_WebLinkRO(insertSpot.id, metadataContents);insert_metadata_WebLink(insertSpot.id, metadataContents); }
	/*
	else if (metadataContents.metadataType=='audio') { insert_metadata_Audio(insertSpot.id, metadataContents); }
	else if (metadataContents.metadataType=='video') { insert_metadata_Video(insertSpot.id, metadataContents); }
	*/
	else if (metadataContents.metadataType=='image') { insert_metadata_ImageRO(insertSpot.id, metadataContents); insert_metadata_Image(insertSpot.id, metadataContents); }
	else if (metadataContents.metadataType=='long-text') { insert_metadata_LongTextRO(insertSpot.id, metadataContents);insert_metadata_LongText(insertSpot.id, metadataContents); }
	/*
	else if (metadataContents.metadataType=='number') { insert_metadata_Number(insertSpot.id, metadataContents); }
	
	else if (metadataContents.metadataType=='date') { insert_metadata_Date(insertSpot.id, metadataContents); }
	else if (metadataContents.metadataType=='period') { insert_metadata_Period(insertSpot.id, metadataContents); }
	else if (metadataContents.metadataType=='keywords') { insert_metadata_Keywords(insertSpot.id, metadataContents); }
	*/
	else if (metadataContents.metadataType=='tiny-text') { insert_metadata_TinyTextRO(insertSpot.id, metadataContents);insert_metadata_TinyText(insertSpot.id, metadataContents); }
	
	else {		
		insertSpot.innerHTML="<span class=\"comment\">Unsupported datatype "+metadataContents.metadataType+" for Metadata '"+metadataContents.metadataName+"'.</span>";
	}
	
	if (shouldInsertDropZones) {

	 	// add insert and drop zones right after the currently added metadata
	 	var shouldInsertDropZones = !curElementData.readOnly && !curElementData.templated; 	
	 	if (shouldInsertDropZones) {
	 		// add dropzpne
	 		var metadataDropSpotNode = makeDropzoneNode(metadataContents.layoutColumn,metadataContents.layoutPosition+1, metadataContents.datasetId);	
	 		colNode.append(metadataDropSpotNode);	
	 		
	 		// insert zone
	 		var metadataInsertZone = makeInsertzoneNode(metadataContents.layoutColumn,metadataContents.layoutPosition+1, metadataContents.datasetId); 		
	 		colNode.append(metadataInsertZone); 	  	 
	 	}	 			 
	}
	
	
}
</script>
		   		 
		<div id="elementContents__template_metadata_" draggable="false" >

				<a  href="#"  id="workzone.contents.icon.delete.metadata"
		 				title="<s:text name="workzone.icon.deleteMetadata" />" class="_deleteIcon_ smallicon icon_deleteElement" 
		 				style="display:none;" >
		 		</a>
			
			  	<form class="_editForm_" id="edit.metadata." action="<c:url value="/updateMetadataProcess" />" method="post" >
			  	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
					      		
			  	<span class="_contents_insertspot_" ></span>
		   	  </form>
		   	</div>
		  		
			    
