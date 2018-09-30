<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script>


var dropPreviousContentMetadata="";
var droppedContentMetadata="";
var dragInitialItemMetadata=null;
var editModeActive=false;



//Reinit the selected element
function deselectAll() {

	<s:iterator value="selectedCatalog.selectedElement.datasets" var="curDataset">	
		document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetSelectedMenu');
		document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetSelected<c:if test="${curDataset.readOnly}" >RO</c:if>');
		document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDataset<c:if test="${curDataset.readOnly}" >RO</c:if>');
		document.getElementById('layout.dataset.<s:property value="#curDataset.datasetId" />').style.display='none';
		selected_dataset_<s:property value="#curDataset.datasetId" />=false;
		<s:iterator value="#curDataset.metadata" var="curMetadata">
			document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataSelectedMenu');
			document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataSelected<c:if test="${curMetadata.readOnly}" >RO</c:if>');
			document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadata<c:if test="${curMetadata.readOnly}" >RO</c:if>');
			document.getElementById('layout.metadata.<s:property value="#curMetadata.metadataId" />').style.display='none';
			document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='none';
			document.getElementById('readonly.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='block';
			selected_metadata_<s:property value="#curMetadata.metadataId" />=false;
		</s:iterator>
	</s:iterator>
	document.getElementById('elementdetails_subdata').style.display='none';
}



//Selection switcher for each dataset or metadata tree element
<s:iterator value="selectedCatalog.selectedElement.datasets" var="curDataset">
	var selected_dataset_<s:property value="#curDataset.datasetId" />=false;
	
	function switchSelected_dataset_<s:property value="#curDataset.datasetId" />() {
					
		// activate selection for this dataset
		if (selected_dataset_<s:property value="#curDataset.datasetId" />==true) { deselectAll(); } 
		else {
			deselectAll();			
			selected_dataset_<s:property value="#curDataset.datasetId" />=true;			
			document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetSelectedMenu');
			document.getElementById('selector.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOverMenu');
			document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDataset<c:if test="${curDataset.readOnly}" >RO</c:if>');
			document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.add('editModeDatasetSelected<c:if test="${curDataset.readOnly}" >RO</c:if>');
			document.getElementById('content.dataset.<s:property value="#curDataset.datasetId" />').classList.remove('editModeDatasetMouseOver<c:if test="${curDataset.readOnly}" >RO</c:if>');			
			document.getElementById('layout.dataset.<s:property value="#curDataset.datasetId" />').style.display='block';
			document.getElementById('elementdetails_subdata.legendTitle').innerHTML="<s:property value="selectedCommunity.vocabulary.capDatasetTraduction" />";
			document.getElementById('elementdetails_subdata').style.display='block';
		}
	}

	<s:iterator value="#curDataset.metadata" var="curMetadata">
		var selected_metadata_<s:property value="#curMetadata.metadataId" />=false;
		function switchSelected_metadata_<s:property value="#curMetadata.metadataId" />() {
			// desactivate selection for this metadata
			if (selected_metadata_<s:property value="#curMetadata.metadataId" />==true) 
			{ 
				deselectAll();
				<c:if test="${not curMetadata.readOnly}" >
					document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='none';
					document.getElementById('readonly.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='block';
				</c:if>
			}	
			// activate selection for this metadata
			else {
				deselectAll();
				selected_metadata_<s:property value="#curMetadata.metadataId" />=true;
				document.getElementById('checkBox.dataset.<s:property value="#curDataset.datasetId" />').checked=true;
				document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadataSelectedMenu');
				document.getElementById('selector.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataMouseOverMenu');
				
				document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadataMouseOver<c:if test="${curMetadata.readOnly}" >RO</c:if>');
				document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.remove('editModeMetadata<c:if test="${curMetadata.readOnly}" >RO</c:if>');
					document.getElementById('content.metadata.<s:property value="#curMetadata.metadataId" />').classList.add('editModeMetadataSelected<c:if test="${curMetadata.readOnly}" >RO</c:if>');
				<c:if test="${not curMetadata.readOnly}" >
					document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='block';
					document.getElementById('readonly.metadata.<s:property value="#curMetadata.metadataId"/>').style.display='none';
				</c:if>
				document.getElementById('layout.metadata.<s:property value="#curMetadata.metadataId" />').style.display='block';
				document.getElementById('elementdetails_subdata.legendTitle').innerHTML="<s:property value="selectedCommunity.vocabulary.capMetadataTraduction" />"
				document.getElementById('elementdetails_subdata').style.display='block';
			}
		}

	</s:iterator>
</s:iterator>

function showEditModeZones()
{
  // showing drop zones as insert zones
  var insertzonesMetadata=document.querySelectorAll(".insertzone_metadata");
  for  (var i=0;i<insertzonesMetadata.length;i++) { insertzonesMetadata[i].style.display='block'; }
  var insertzonesDataset=document.querySelectorAll(".insertzone_dataset");
  for  (var i=0;i<insertzonesDataset.length;i++) { insertzonesDataset[i].style.display='block'; }

  var datasets=document.querySelectorAll(".datasetcontent");
  for  (var i=0;i<datasets.length;i++) { 
	  datasets[i].classList.add('editModeDataset');
	  datasets[i].draggable=true;
  }
  var datasets=document.querySelectorAll(".datasetcontentRO");
  for  (var i=0;i<datasets.length;i++) { 
	  datasets[i].classList.add('editModeDatasetRO');
	  datasets[i].draggable=true;
  }  

  var metadata=document.querySelectorAll(".metadatacontent");
  for  (var i=0;i<metadata.length;i++) {
	  metadata[i].classList.add('editModeMetadata');
	  metadata[i].draggable=true;
  }
  var metadata=document.querySelectorAll(".metadatacontentRO");
  for  (var i=0;i<metadata.length;i++) {
	  metadata[i].classList.add('editModeMetadataRO');
	  metadata[i].draggable=true;
  }
  var notModifyables=document.querySelectorAll(".notInEditModeDataField");
  for  (var i=0;i<notModifyables.length;i++) { 	  
	  notModifyables[i].style.display='none';	  
  }
  var modifyables=document.querySelectorAll(".inEditModeDataField");
  for  (var i=0;i<modifyables.length;i++) { 
	  modifyables[i].style.display='block';	  
  }    
  document.getElementById('elementdetails_structure').style.display='block';
  
  
}

function hideEditModeZones()
{
  deselectAll();
  
  // showing drop zones as insert zones
  var insertzonesMetadata=document.querySelectorAll(".insertzone_metadata");
  for  (var i=0;i<insertzonesMetadata.length;i++) { insertzonesMetadata[i].style.display='none'; }
  var insertzonesDataset=document.querySelectorAll(".insertzone_dataset");
  for  (var i=0;i<insertzonesDataset.length;i++) { insertzonesDataset[i].style.display='none'; }
  
  var datasets=document.querySelectorAll(".datasetcontent");
  for  (var i=0;i<datasets.length;i++) { 
	  datasets[i].classList.remove('editModeDataset');
	  datasets[i].draggable=false	;
  }
  var datasets=document.querySelectorAll(".datasetcontentRO");
  for  (var i=0;i<datasets.length;i++) { 
	  datasets[i].classList.remove('editModeDatasetRO');
	  datasets[i].draggable=false	;
  }

  var metadatas=document.querySelectorAll(".metadatacontent");
  for  (var i=0;i<metadatas.length;i++) { 
	  metadatas[i].classList.remove('editModeMetadata');
	  metadatas[i].draggable=false;
  }
  var metadatas=document.querySelectorAll(".metadatacontentRO");
  for  (var i=0;i<metadatas.length;i++) { 
	  metadatas[i].classList.remove('editModeMetadataRO');
	  metadatas[i].draggable=false;
  }
  var notModifyables=document.querySelectorAll(".notInEditModeDataField");
  for  (var i=0;i<notModifyables.length;i++) { 
	  notModifyables[i].style.display='block';	  
  }
  var modifyables=document.querySelectorAll(".inEditModeDataField");
  for  (var i=0;i<modifyables.length;i++) { 
	  modifyables[i].style.display='none';	  
  }    
  document.getElementById('elementdetails_structure').style.display='none';
  document.getElementById('elementdetails_subdata').style.display='none';

  
}


function switchToEditMode(setOn) {
	if (setOn) {
		showEditModeZones();	  
		editModeActive=true;	
	} else {
		hideEditModeZones();
		editModeActive=false;
	}
		
}

// Drag and Drop Metadata
function handleDragStartMetadata(metadataId,item,event) {

    event.stopPropagation();
    event.dataTransfer.effectAllowed = 'move';
    item.classList.add('dropping');
    item.classList.remove('editModeMetadataMouseOver');
    droppedContentMetadata=item.innerHTML;
    event.dataTransfer.setData('movedMetadata',droppedContentMetadata);
    event.dataTransfer.setData('metadataId',metadataId);
    event.dataTransfer.setData('width',item.offsetWidth);
    event.dataTransfer.setData('height',item.offsetHeight);
    dragInitialItemMetadata=item;   
    // showing drop zones
    var dropzones=document.querySelectorAll(".dropzone_metadata");
    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='block'; }
 	

}

function handleDragEndMetadata(item,event) {	
	
	if (dragInitialItemMetadata!=null) {
		item.classList.remove('dropping');      
		dropPreviousContentMetadata="";
		dragInitialItemMetadata=null;
		event.dataTransfer.dropEffect = 'none';    	
		// hidding drop zones  
	    var dropzones=document.querySelectorAll(".dropzone_metadata");
	    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='none';  }    	
	}
}

function handleDragOverMetadata(item,event) {
	// Allows us to drop.
    if (event.preventDefault) { event.preventDefault(); }
    event.dataTransfer.dropEffect = 'move';
    item.classList.add('dropzone_metadata_draghover');
    return false;
}

function handleDragEnterMetadata(item,event) {
	if (dropPreviousContentMetadata=="") { dropPreviousContentMetadata = item.innerHTML; }
	item.innerHTML=event.dataTransfer.getData('movedMetadata');
	item.classList.add('dropzone_metadata_draghover');
	item.style.width=event.dataTransfer.getData('width');
    item.style.height=event.dataTransfer.getData('height');
    // hidding drop zone icons
    var dropzones=document.querySelectorAll(".dropzone_icon_metadata");
    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='none'; }
            
}

function handleDragLeaveMetadata(item,event) {
	item.classList.remove('dropzone_metadata_draghover');
	item.innerHTML=dropPreviousContentMetadata;
	dropPreviousContentMetadata="";
	event.dataTransfer.dropEffect = 'none';
	item.style.width="";
	item.style.height="";
    // showing drop zone icons
    var dropzones=document.querySelectorAll(".dropzone_icon_metadata");
    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='block'; }
}

function handleDropMetadata(newColNb,newPosition,newDatasetId,item,event) {

	var dropzones=document.querySelectorAll(".dropzone_metadata");
    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='none';  }
    
    // this/e.target is current target element.
	if (item.id!=dragInitialItemMetadata.id) {

	
		dragInitialItemMetadata.style.display='none';
		item.style.display='block';
		item.innerHTML=droppedContentMetadata;
		item.classList.add('metadatacontent');
		document.getElementById('workzone.move.metadata.form.movedMetadataId').value=event.dataTransfer.getData('metadataId');
		document.getElementById('workzone.move.metadata.form.moveToColumn').value=newColNb;
		document.getElementById('workzone.move.metadata.form.moveToPosition').value=newPosition;
		document.getElementById('workzone.move.metadata.form.moveToDatasetId').value=newDatasetId;
		document.getElementById('workzone.move.metadata.form').submit();
		
		dropPreviousContentMetadata="";
	    dragInitialItemMetadata=null;	    	
	}
    
    return false;
}


// Drag and Drop Dataset 


var dropPreviousContentDataset="";
var droppedContentDataset="";
var dragInitialItemDataset=null;

function handleDragStartDataset(datasetId,item,event) {
	event.stopPropagation();
    event.dataTransfer.effectAllowed = 'move';
    item.classList.add('dropping');
    droppedContentDataset=item.innerHTML;
    event.dataTransfer.setData('movedDataset',droppedContentDataset);
    event.dataTransfer.setData('datasetId',datasetId);
    event.dataTransfer.setData('width',item.offsetWidth);
    event.dataTransfer.setData('height',item.offsetHeight);
    dragInitialItemDataset=item;   
    // showing drop zones
    var dropzones=document.querySelectorAll(".dropzone_dataset");
    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='block'; }
}

function handleDragEndDataset(item,event) {	
		
	if (dragInitialItemDataset!=null) {
		item.classList.remove('dropping');      
		dropPreviousContentDataset="";
		dragInitialItemDataset=null;
		event.dataTransfer.dropEffect = 'none';    	
		// hidding drop zones  
	    var dropzones=document.querySelectorAll(".dropzone_dataset");
	    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='none';  }    	
	}
}

function handleDragOverDataset(item,event) {
	// Allows us to drop.	
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
    item.innerHTML=event.dataTransfer.getData('movedDataset');
	
    item.classList.add('dropzone_dataset_draghover');
    return false;
    
}

function handleDragEnterDataset(item,event) {
	if (dropPreviousContentDataset=="") { dropPreviousContentDataset = item.innerHTML; }
	item.innerHTML=event.dataTransfer.getData('movedDataset');
	item.classList.add('dropzone_dataset_draghover');
	item.style.width=event.dataTransfer.getData('width');
    item.style.height=event.dataTransfer.getData('height');
}

function handleDragLeaveDataset(item,event) {
	item.classList.remove('dropzone_dataset_draghover');
	item.innerHTML=dropPreviousContentDataset;
	dropPreviousContentDataset="";
	event.dataTransfer.dropEffect = 'none';
	item.style.width="";
	item.style.height="";
}

function handleDropDataset(newPosition,item,event) {
	
	var dropzones=document.querySelectorAll(".dropzone_dataset");
    for  (var i=0;i<dropzones.length;i++) { dropzones[i].style.display='none';  }
    
	if (item.id!=dragInitialItemDataset.id) {
	
		dragInitialItemDataset.style.display='none';
		item.style.display='block';
		item.innerHTML=droppedContentDataset;
		
		document.getElementById('workzone.move.dataset.form.movedDatasetId').value=event.dataTransfer.getData('datasetId');
		document.getElementById('workzone.move.dataset.form.moveToPosition').value=newPosition;
		document.getElementById('workzone.move.dataset.form').submit();
		
		dropPreviousContentDataset="";
	    dragInitialItemDataset=null;	    	
	}
    
    return false;
}



// -------------------------------------------------------------------------------

function addNewMetadata(datasetId,name,comment,termId,column,position) {

	document.getElementById('workzone.create.metadata.form.datasetId').value=datasetId;
	document.getElementById('workzone.create.metadata.form.metadataName').value=name;
	document.getElementById('workzone.create.metadata.form.metadataComment').value=comment;
	document.getElementById('workzone.create.metadata.form.termId').value=termId;
	document.getElementById('workzone.create.metadata.form.createInPosition').value=position;
	document.getElementById('workzone.create.metadata.form.createInColumn').value=column;
	document.getElementById('workzone.create.metadata.form').submit();
}

function addNewDataset(elementId,name,comment,position) {
	document.getElementById('workzone.create.dataset.form.elementId').value=elementId;
	document.getElementById('workzone.create.dataset.form.datasetName').value=name;
	document.getElementById('workzone.create.dataset.form.datasetComment').value=comment;
	document.getElementById('workzone.create.dataset.form.createInPosition').value=position;
	document.getElementById('workzone.create.dataset.form').submit();

}


function displayNewMetadataForm(datasetId,column,position) {
	cancelNewMetadatas();// if any other is currently displayed, hide it
	document.getElementById('insertzone.metadata.dataset_'+datasetId+'.'+column+'.'+position+'.inputs').style.display='block';
	document.getElementById('insertzone.metadata.dataset_'+datasetId+'.'+column+'.'+position).style.background='#555555';	
	document.getElementById('insertzone.metadata.dataset_'+datasetId+'.'+column+'.'+position).style.width=document.getElementById('insertzone.metadata.dataset_'+datasetId+'.'+column+'.'+position+'.inputs').offsetWidth;
	document.getElementById('insertzone.metadata.dataset_'+datasetId+'.'+column+'.'+position).style.height=document.getElementById('insertzone.metadata.dataset_'+datasetId+'.'+column+'.'+position+'.inputs').offsetHeight;
}

function displayNewDatasetForm(position) {
	cancelNewMetadatas();// if any other is currently displayed, hide it
	document.getElementById('insertzone.dataset.'+position+'.inputs').style.display='block';
	document.getElementById('insertzone.dataset.'+position).style.background='#555555';	
	document.getElementById('insertzone.dataset.'+position).style.width=document.getElementById('insertzone.dataset.'+position+'.inputs').offsetWidth;
	document.getElementById('insertzone.dataset.'+position).style.height=document.getElementById('insertzone.dataset.'+position+'.inputs').offsetHeight;
}

function cancelNewMetadatas() {
    var insertzones=document.querySelectorAll(".insertzone_metadata");
    for  (var i=0;i<insertzones.length;i++) { 
    		insertzones[i].style.display='block';
    		insertzones[i].style.width=''; 
    		insertzones[i].style.height=''; 
   	}
    var insertzonesInputs=document.querySelectorAll(".insertzoneMetadataInputs");
    for  (var i=0;i<insertzonesInputs.length;i++) { 
    	insertzonesInputs[i].style.display='none';
   	}
    var insertzonesAreas=document.querySelectorAll(".insertzoneMetadataArea");
    for  (var i=0;i<insertzonesAreas.length;i++) { 
    	insertzonesAreas[i].style.display='block';
   	}	
}

function cancelNewDatasets() {
    var insertzones=document.querySelectorAll(".insertzone_dataset");
    for  (var i=0;i<insertzones.length;i++) { 
    		insertzones[i].style.display='block';
    		insertzones[i].style.width=''; 
    		insertzones[i].style.height=''; 
   	}
    var insertzonesInputs=document.querySelectorAll(".insertzoneDatasetInputs");
    for  (var i=0;i<insertzonesInputs.length;i++) { 
    	insertzonesInputs[i].style.display='none';
   	}
    var insertzonesAreas=document.querySelectorAll(".insertzoneDatasetArea");
    for  (var i=0;i<insertzonesAreas.length;i++) { 
    	insertzonesAreas[i].style.display='block';
   	}	
}
</script>

			    
