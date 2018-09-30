<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script>

function insert_metadata_TinyText(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("elementContents.template.metadata.tinytext").cloneNode(true);
	metadataContentsNode.id="edit.metadata."+m.metadataId;	
	
	// input
	var input = metadataContentsNode.querySelector("._input_");
	input.name="selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asTinyText.text";
	input.value=m.text;
	
	// link
	var link = metadataContentsNode.querySelector("._link_");
	link.onclick=function(event) {
		document.getElementById('edit.metadata.'+m.metadataId+'.form').submit();
	}
	
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="elementContents.template.metadata.tinytext"  class="matadataTextEdit"  style="display:none" >

<input  class="_input_" onclick="event.stopPropagation();" >

<a href="#" class="_link_" ><s:text name="global.submit" /></a>
		
</span>
		
	


	    	
	    			
		
