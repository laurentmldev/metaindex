<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function insert_metadata_Image(insertSpotId,m) {
	
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("elementContents.template.metadata.image").cloneNode(true);
	metadataContentsNode.id="edit.metadata."+m.metadataId;
	
	// image
	var image = metadataContentsNode.querySelector("._image_");
	image.src=m.imageUrl;
	image.classList.add("picture_"+m.layoutSize);
	image.title=m.metadataName;
	image.alt=m.metadataName;
	if (m.borderSize>0) {
		image.style="border-radius:4px;border:"+m.borderSize+"px solid "+m.borderColor;
	}
	
	// input
	var input = metadataContentsNode.querySelector("._input_");
	input.name="selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asImage.imageUrl";
	input.value=m.imageUrl;
		
	// sendForm
	var sendFormNode = metadataContentsNode.querySelector("._sendForm_");
	sendFormNode.onclick=function(event) {
		document.getElementById('edit.metadata.'+m.metadataId+'.form').submit();
	}
	
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="elementContents.template.metadata.image"  class="matadataTextEdit"  style="display:none" >


<img src="" class="_image_"
	draggable="false"
	ondragstart="event.preventDefault();event.stopPropagation();"	
/>
<div>
<input  name="" value="" class="_input_" style="margin-top:10px" size="80"		
		ondragstart="event.preventDefault();event.stopPropagation();"
		onclick="event.stopPropagation();"		
	>

<a class="_sendForm_"  href="#" ><s:text name="global.submit" /></a>
		
</div>	
	
</span>
