<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function insert_metadata_WebLinkRO(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("_elementContents.template.metadata.weblinkRO_").cloneNode(true);
	metadataContentsNode.id+=m.metadataId;
	metadataContentsNode.style.display='block';
	
	// metadataName
	if (m.laouytDoDisplayName) {
		var mName = metadataContentsNode.querySelector("._metadataName_");
		mName.title=m.metadataComment;
		mName.innerHTML=m.metadataName;
		mName.style.display='block';
	}
	
	
	// link
	var linkNode = metadataContentsNode.querySelector("._clicklink_");
	linkNode.href=m.url;	
	if (m.linkText!='') { linkNode.innerHTML=m.linkText }
	else { linkNode.innerHTML=m.url }
	
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="readonly.metadata."  class="_elementContents.template.metadata.weblinkRO_ metadaText" style="display:none" >

	<span class="_metadataName_" style="display:none"  ></span>

	
	<a class="_clicklink_" href="" 
		target="_blank"
		draggable="false"
		ondragstart="event.preventDefault();event.stopPropagation();">			
	</a>
		
</span>
