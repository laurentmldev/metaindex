<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>


function updateElementProperties(elementData) {
	
	if (elementData.readOnly) { document.getElementById("workzone.elementdetails.icon.editModeOn").style.display='none'; }
	else { document.getElementById("workzone.elementdetails.icon.editModeOn").style.display='block'; } 
	document.getElementById("workzone.elementdetails.icon.editModeOff").style.display='none';	
	
	document.getElementById("elementProperties.elementId").innerHTML=elementData.elementId;    	
	
	var treeInsertSpot = document.getElementById('elementProperties_tree_insertspot');
	treeInsertSpot.innerHTML="";
	
		
	// isTemplate
	var insertspotField_isTemplate = document.getElementById("field_elementProperties_isTemplate");
	fieldNode = new_boolean_field(
					/*name*/ "elementProperties_isTemplate", /*input name*/ "selectedElement.template",
					/*onchange Func*/ function(e) { document.getElementById('edit.element.details.form').submit(); }
				);
	insertspotField_isTemplate.innerHTML=fieldNode.innerHTML;		
	fieldNode.setValue(elementData.template,elementData.readOnly);
	
	
	if (elementData.template) {    			
		document.getElementById('field_elementProperties_templateRefElementIdContainer').style.display='none';
		document.getElementById('field_elementProperties_nbreferencingElementsContainer').style.display='table-row';
		document.getElementById('field_elementProperties_nbReferencingElements').innerHTML=elementData.nbReferencingElements;
		
	// templateRefElementId
	} else {
		
		document.getElementById('field_elementProperties_nbreferencingElementsContainer').style.display='none';
		document.getElementById('field_elementProperties_templateRefElementIdContainer').style.display='table-row';
		
		var insertspotField_templateRefElementId = document.getElementById("field_elementProperties_templateRefElementId");
		var options=[];
		options.push({ value:0, text:"--<s:text name="workzone.choice.none"/>--" });
			<s:iterator value="selectedCommunity.templateElements" var="curTemplate" status="itStatus"  >	
				options.push({ value:<s:property value="#curTemplate.elementId"/>, text:"<s:property value="#curTemplate.name"/>" });				 
			</s:iterator>
				
		fieldNode = new_enumerated_field(
						/*name*/ "elementProperties_templateRefElementId", 
						/*input name*/ "selectedElement.templateRefElementIdStr", 
						/*enum values*/ options, 
						/*onchange Func*/ function(e) { document.getElementById('edit.element.details.form').submit(); }
					);
			insertspotField_templateRefElementId.innerHTML=fieldNode.innerHTML;		
			fieldNode.setValue(elementData.templateRefElementId,elementData.readOnly);			
		
	}
	
	
			
}

function elementProperties_addDataset(d) {
		
	var newDatasetTreeEl=document.getElementById('properties_tree__template_dataset_item_').cloneNode(true);
	newDatasetTreeEl.id="properties_tree.dataset."+d.datasetId;
	newDatasetTreeEl.querySelector("._datasetTreeEl_checkbox_").id+="."+d.datasetId;
	
	// tree Label
	treeLabel = newDatasetTreeEl.querySelector("._datasetTreeEl_label_")
	treeLabel.id+="."+d.datasetId;
	//treeLabel.for+="."+d.datasetId;	
	if (d.nbMetadata>0) { treeLabel.style.display+='none'; }
	else { treeLabel.style.display+='block'; }
	
	// selector
	selector = newDatasetTreeEl.querySelector("._datasetTreeEl_selector_");
	selector.id+="."+d.datasetId;
	selector.innerHTML=d.datasetName;
	selector.onclick=function(e) {
		if (editModeActive) { switchSelected_dataset(d.datasetId); }
	}
	selector.onmouseover=function(e) {		
		if (selected_dataset[d.datasetId]==false) { 
				document.getElementById('selector.dataset.'+d.datasetId).classList.add('editModeDatasetMouseOverMenu');
				if (d.readOnly) { document.getElementById('content.dataset.'+d.datasetId).classList.add('editModeDatasetMouseOver'); }
				else { document.getElementById('content.dataset.'+d.datasetId).classList.add('editModeDatasetMouseOverRO'); }
		 }
	}
	selector.onmouseout=function(e) { 
		if (selected_dataset[d.datasetId]==false) { 	
			document.getElementById('selector.dataset.'+d.datasetId).classList.remove('editModeDatasetMouseOverMenu');
			if (d.readOnly) { document.getElementById('content.dataset.'+d.datasetId).classList.remove('editModeDatasetMouseOver'); }
			else { document.getElementById('content.dataset.'+d.datasetId).classList.remove('editModeDatasetMouseOverRO'); }								
		}
	}
	selector.select=function() {
		this.classList.add('editModeDatasetSelectedMenu');					
		this.classList.remove('editModeDatasetMouseOverMenu');
	}
	selector.deselect=function() {
		this.classList.remove('editModeDatasetSelectedMenu');					
		this.classList.remove('editModeDatasetMouseOverMenu');
	}
	
	// metadata tree insertspot
	var metadata_tree_insertspot = newDatasetTreeEl.querySelector("._metadata_tree_insertspot_");
	metadata_tree_insertspot.id="dataset."+d.datasetId+".metadata_insertspot";
	metadata_tree_insertspot.style.display='block';
	
	document.getElementById('elementProperties_tree_insertspot').appendChild(newDatasetTreeEl);	 
	newDatasetTreeEl.style.display='block';
	
	elementProperties_addDetails_Dataset(d);
}

function elementProperties_addMetadata(m) {
	
	var metadata_tree_insertspot = document.getElementById("dataset."+m.datasetId+".metadata_insertspot");
	var newMetadataSelector = metadata_tree_insertspot.querySelector("._template_selector_wrapper_").cloneNode(true);			
	selected_metadata[m.metadataId]=false;
	
	// selector 
	var selector = newMetadataSelector.querySelector('._selector_');
	selector.id+=m.metadataId;
	selector.innerHTML=m.metadataName;
	
	selector.onclick=function(event) { 
		if (editModeActive) { switchSelected_metadata(m.metadataId); }
	}
	
	selector.onmouseover=function(event) { 
		if (selected_metadata[m.metadataId]==false) { 
			document.getElementById('selector.metadata.'+m.metadataId).classList.add('editModeMetadataMouseOverMenu');
			if (m.readOnly) { document.getElementById('content.metadata.'+m.metadataId).classList.add('editModeMetadataMouseOverRO'); }
			else { document.getElementById('content.metadata.'+m.metadataId).classList.add('editModeMetadataMouseOver'); }			
		}
	}
	selector.onmouseout=function(event) { 
		if (selected_metadata[m.metadataId]==false) { 
			document.getElementById('selector.metadata.'+m.metadataId).classList.remove('editModeMetadataMouseOverMenu');
			if (m.readOnly) { document.getElementById('content.metadata.'+m.metadataId).classList.remove('editModeMetadataMouseOverRO'); }
			else { document.getElementById('content.metadata.'+m.metadataId).classList.remove('editModeMetadataMouseOver'); }			
		}
	}
	
	selector.ondragstart=function(event) { 
		handleDragStartMetadata(document.getElementById('selector.metadata.'+m.metadataId),event);
	}
	selector.ondragend=function(event) { 
		handleDragEndMetadata(document.getElementById('selector.metadata.'+m.metadataId),event);
	}
		
	newMetadataSelector.style.display='block';
	metadata_tree_insertspot.append(newMetadataSelector);
	
	elementProperties_addDetails_Metadata(m);
}

</script>

<fieldset  >
<legend >

	<span class="fieldsetTitle" ><s:property value="selectedCommunity.vocabulary.capElementTraduction" /></span>
	
</legend>

	<form 	id="edit.element.details.form" 
			action="<c:url value="/updateElementDataProcess" />" method="post" >
		<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
		
			<div style="width:100%;display:flex;align-items: center;justify-content:center;" >
					<a  
						href="#" id="workzone.elementdetails.icon.editModeOn" 
		 				title="<s:text name="workzone.icon.createData" />" 
		 				class="bigicon icon_modify" 
		 				onmouseover="event.stopPropagation();"
		 				onclick="switchToEditMode(true);
		 					document.getElementById('workzone.elementdetails.icon.editModeOn').style.display='none';
		 					document.getElementById('workzone.elementdetails.icon.editModeOff').style.display='block';">
					</a>
	 				
					<a  href="#" id="workzone.elementdetails.icon.editModeOff" style="display:none;"
		 				title="<s:text name="workzone.icon.createData" />" 
		 				class="bigicon icon_modify_active" 
		 				onmouseover="event.stopPropagation();"
		 				onclick="switchToEditMode(false);
								document.getElementById('workzone.elementdetails.icon.editModeOn').style.display='block';
								document.getElementById('workzone.elementdetails.icon.editModeOff').style.display='none';">
					</a>
			</div>
			
		<table style="padding:5px">
			
				<tr>
					<td><span class="fieldtitle"  >ID</span></td>
					<td><span id="elementProperties.elementId" ></span></td>
				</tr>				
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.element.layout.istemplate"/></span></td>
					<td><span id="field_elementProperties_isTemplate" ></span></td>									
				</tr>				
				<tr id="field_elementProperties_nbreferencingElementsContainer" >
					<td><span class="fieldtitle"><s:text name="workzone.element.nbreferencingelements"/></span></td>					
					<td><span id="field_elementProperties_nbReferencingElements"></span></td>
					
				</tr>
				<tr id="field_elementProperties_templateRefElementIdContainer"  >
					<td><span class="fieldtitle"><s:text name="workzone.element.layout.templateRefElementId"/></span></td>
					<td><span id="field_elementProperties_templateRefElementId"></span></td>					
				</tr>								
 		</table> 
 		</form>
 			
 	<fieldset id="elementdetails_structure" style="display:none;padding:4px;border:none;" class="menushadowcard" >
 	
 		<center><div class="negative" style="padding:2px;"><s:text name="workzone.elementdetails.structure" /></div></center>
 		
 		<span id="elementProperties_tree_insertspot"> </span>
 		
 		
	
	<ul class="menutree" id="properties_tree__template_dataset_item_" style="display:none ">
		<li class="menutree"  >
			<input type="checkbox" class="_datasetTreeEl_checkbox_" id="elementProperties.checkBox.dataset" checked>	
			<label class="_datasetTreeEl_label_" id="label.dataset" for="checkBox.dataset" ></label>
			 
			<span id="selector.dataset" class="_datasetTreeEl_selector_ treechoice_dataset" ></span>
			
			<ul class="_metadata_tree_insertspot_ menutree" style="display:none" >
				
				<li class="_template_selector_wrapper_ menutree" >
					<span id="selector.metadata." class="_selector_ treechoice_metadata"
						draggable="true" >
						
					</span>
				</li>							
			</ul>			
		</li>
	</ul>
	
	
</fieldset>
	
</fieldset>

<fieldset id="elementdetails_subdata" style="display:none;">
	<legend><span id="elementdetails_subdata.legendTitle" class="fieldsetTitle" ></span></legend>

	<s:include value="/secure/workzone/properties/workzone.properties.details.dataset.jsp" />
	<s:include value="/secure/workzone/properties/workzone.properties.details.metadata.jsp" />

</fieldset>   
