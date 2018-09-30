<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function insert_metadata_properties_Image(insertspotNode,m) {
	
	insert_metadata_properties_GenericSimpleText(insertspotNode,m);
	var imageDetails = document.getElementById("_template_metadata_properties_image_").cloneNode(true);
	imageDetails.id="elementProperties.metadata.image."+m.metadataId;
	imageDetails.style.display='block';
	insertspotNode.append(imageDetails);
	
	// isThumbnail
	var isThumbnail = new_boolean_field(
			/*id*/ "workzone.elementdetails.metadata.image.isthumbnail."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asImage.thumbnail", 			
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	imageDetails.querySelector("._elementProperties_details_metadata_image_isThumbnail_").innerHTML=isThumbnail.innerHTML;
	isThumbnail.setValue(m.thumbnail,m.readOnly || m.templated);
	
	// border size and color
	var borderSize = new_editable_field(
			/*id*/ "workzone.elementdetails.metadata.image.bordersize."+m.metadataId, 
			/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asImage.borderSize",
			/*input style*/ "width:80px", 
			/*input placeholder*/ "<s:text name="workzone.metadata.layout.borderSize"/>",
			/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
		);
	imageDetails.querySelector("._elementProperties_details_metadata_image_borderSize_").innerHTML=borderSize.innerHTML;
	borderSize.setValue(m.borderSize,m.readOnly || m.templated);
	
	if (m.borderSize>0) {
		// border color
		var borderColor = new_color_field(
				/*id*/ "workzone.elementdetails.metadata.image.bordercolor."+m.metadataId, 
				/*input name*/ "selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asImage.borderColor", 			
				/*input style*/ "width:30px",
				/*onchange Func*/ function(e) { document.getElementById('edit.metadata.'+m.metadataId+'.details.form').submit(); }
			);
		imageDetails.querySelector("._elementProperties_details_metadata_image_borderColor_").innerHTML=borderColor.innerHTML;
		imageDetails.querySelector("._elementProperties_details_metadata_image_borderColor_").style.display='block';
		borderColor.setValue(m.borderColor,m.readOnly || m.templated);
	}
	
}
</script>

<table id="_template_metadata_properties_image_" style="display:none">

		<tr>
			<td><span class="fieldtitle"><s:text name="workzone.metadata.layout.isThumbnail"/></span></td>
			<td><span class="_elementProperties_details_metadata_image_isThumbnail_" ></span></td>							
		</tr>
		<tr>
			<td><span class="fieldtitle"><s:text name="workzone.metadata.layout.borderSize"/></span></td>
			<td><span class="_elementProperties_details_metadata_image_borderSize_" ></span></td>
			<td><span class="_elementProperties_details_metadata_image_borderColor_" style="display:none"></span></td>						
		</tr>
</table>	
	


	    	
	    			
		
