<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script>

function insert_metadata_ImageRO(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("elementContents.template.metadata.imageRO").cloneNode(true);
	metadataContentsNode.id="readonly.metadata."+m.metadataId;
	metadataContentsNode.style.display='block';
	
	// image
	var image = metadataContentsNode.querySelector("._image_");
	image.src=m.imageUrl;
	image.classList.add("picture_"+m.layoutSize);
	image.title=m.metadataName;
	image.alt=m.metadataName;
	if (m.borderSize>0) {
		image.style="border-radius:4px;border:"+m.borderSize+"px solid "+m.borderColor;
	}
	
	// imgName
	if (m.layoutDoDisplayName) {
		var imgName = metadataContentsNode.querySelector("._imgName_");
		imgName.style.display='block';
		var imgNameLink = imgName.querySelector("._link_");
		imgNameLink.href=m.imageUrl;
		imgNameLink.innerHTML=m.imageUrl;
	}
	
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="elementContents.template.metadata.imageRO"  class="metadaText" style="display:none" >


	<img src="" class="_image_"
		title="<s:property value="#curMetadata.name"/>"
		alt="<s:property value="#curMetadata.name"/>"
		onclick="event.preventDefault();"
		draggable="false"
		ondragstart="event.preventDefault();event.stopPropagation();"
		
		/>

	<div class="_imgName_ fieldtitle" style="margin-top:10px" style="display:none" >
		<a  href="" class="_link_" target="_blank" draggable="false"
			ondragstart="event.preventDefault();event.stopPropagation();"
			>
			
		</a>
	</div>


</span>


	    	
	    			
		
