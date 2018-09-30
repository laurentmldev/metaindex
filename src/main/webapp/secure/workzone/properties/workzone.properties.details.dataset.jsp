<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function elementProperties_addDetails_Dataset(d) {
	
	var subdataNode = document.getElementById("elementdetails_subdata");
	
	var newDatasetDetails = document.getElementById("_template_details_dataset_").cloneNode(true);
	newDatasetDetails.id="edit.dataset."+d.datasetId+".details.form";
	newDatasetDetails.style.display='block';
	
	var layout = newDatasetDetails.querySelector("._layout_");
	layout.id+=d.datasetId;	
	
	if (d.templated) {
		var templated=newDatasetDetails.querySelector("._templated_dataset_");
		templated.style.display='block';		
	}
	
	subdataNode.append(newDatasetDetails);
	
	// dataset name
	var datasetName = new_editable_field(
			/*name*/ "workzone.elementdetails.datasetname."+d.datasetId, 
			/*input name*/ "selectedElement.datasetsMap['"+d.datasetId+"'].name", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.elementName"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.dataset.'+d.datasetId+'.details.form').submit(); }
		);
	newDatasetDetails.querySelector("._elementProperties_details_dataset_name_").innerHTML=datasetName.innerHTML;
	datasetName.setValue(d.datasetName,d.readOnly || d.templated);	
	
	// dataset comment
	var datasetComment = new_editable_field(
			/*name*/ "workzone.elementdetails.datasetcomment."+d.datasetId, 
			/*input name*/ "selectedElement.datasetsMap['"+d.datasetId+"'].comment", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.elementComment"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.dataset.'+d.datasetId+'.details.form').submit(); }
		);	
	newDatasetDetails.querySelector("._elementProperties_details_dataset_comment_").innerHTML=datasetComment.innerHTML;	
	datasetComment.setValue(d.datasetComment,d.readOnly || d.templated);
	
	// dataset position
	var datasetPosition = new_editable_field(
			/*name*/ "workzone.elementdetails.datasetLayoutPosition."+d.datasetId, 
			/*input name*/ "selectedElement.datasetsMap['"+d.datasetId+"'].layoutPosition", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.position"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.dataset.'+d.datasetId+'.details.form').submit(); }
		);	
	newDatasetDetails.querySelector("._elementProperties_details_dataset_layoutPosition_").innerHTML=datasetPosition.innerHTML;	
	datasetPosition.setValue(d.layoutPosition,d.readOnly || d.templated);
	
	// dataset nbColumns
	var datasetNbColumns = new_editable_field(
			/*name*/ "workzone.elementdetails.datasetLayoutNbColumns."+d.datasetId, 
			/*input name*/ "selectedElement.datasetsMap['"+d.datasetId+"'].layoutNbColumns", 
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.dataset.layout.nbColumns"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.dataset.'+d.datasetId+'.details.form').submit(); }
		);	
	newDatasetDetails.querySelector("._elementProperties_details_dataset_layoutNbColumns_").innerHTML=datasetNbColumns.innerHTML;	
	datasetNbColumns.setValue(d.layoutNbColumns,d.readOnly || d.templated);
	
	// dataset show name (and frame)
	var datasetDisplayName = new_boolean_field(
			/*name*/ "workzone.elementdetails.datasetLayoutDoDisplayName."+d.datasetId, 
			/*input name*/ "selectedElement.datasetsMap['"+d.datasetId+"'].layoutDoDisplayName", 
			/*onchange Func*/ function(e) { document.getElementById('edit.dataset.'+d.datasetId+'.details.form').submit(); }
		);	
	newDatasetDetails.querySelector("._elementProperties_details_dataset_layoutDoDisplayName_").innerHTML=datasetDisplayName.innerHTML;	
	datasetDisplayName.setValue(d.layoutDoDisplayName,d.readOnly || d.templated);
	
	
}


</script>


	 <form id="_template_details_dataset_" 
	 	style="display:none"  
	 	action="<c:url value="/updateDatasetProcess" />" method="post" >
		<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>	
				
		<div id="layout.dataset." style="display:none;" class="_layout_">
		<center><span class="comment _templated_dataset_" style="display:none"><s:text name="element.details.fromTemplate"></s:text></span></center>
		
			<table>
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.elementName"/></span></td>
					<td><span class="_elementProperties_details_dataset_name_"></span></td>	
				</tr>
								
				<tr>
					<td><span class="fieldtitle"><s:text name="workzone.dataset.layout.elementComment"/></span></td>
					<td><span class="_elementProperties_details_dataset_comment_"></span></td>									
                </tr>
                
	            <tr>
                    <td><span class="fieldtitle"><s:text name="workzone.dataset.layout.position"/></span></td>
                    <td><span class="_elementProperties_details_dataset_layoutPosition_"></span></td>
                </tr>
                <tr>
                    <td><span class="fieldtitle"><s:text name="workzone.dataset.layout.nbColumns"/></span></td>
                    <td><span class="_elementProperties_details_dataset_layoutNbColumns_"></span></td>                        
                </tr>
                <tr>
                    <td><span class="fieldtitle"><s:text name="workzone.dataset.layout.displayFrame"/></span></td>
                    <td><span class="_elementProperties_details_dataset_layoutDoDisplayName_"></span></td>                        
                </tr>
                
        </table>
                </div>
                </form>
	
 
