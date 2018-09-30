<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
	
<script>

function insert_metadata_LongTextRO(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("elementContents.template.metadata.longtextRO").cloneNode(true);
	metadataContentsNode.id="readonly.metadata."+m.metadataId;
	metadataContentsNode.style.display='block';
	
	// text
	metadataContentsNode.innerHTML=m.text;
	
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="elementContents.template.metadata.longtextRO"  class="metadaText" style="display:none" >
	
</span>
