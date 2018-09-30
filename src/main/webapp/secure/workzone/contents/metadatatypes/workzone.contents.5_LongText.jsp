
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<c:url value="/public/deps/ckeditor/" var="ckeditorUrl"/>
<script type="text/javascript" src="${ckeditorUrl}/ckeditor.js"></script>

<script>

function insert_metadata_LongText(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("elementContents.template.metadata.longtext").cloneNode(true);
	metadataContentsNode.id="edit.metadata."+m.metadataId;
	
	
	// text area
	var textNode=metadataContentsNode.querySelector("._text_");
	textNode.id+=m.metadataId;
	textNode.name="selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asLongText.text";
	textNode.innerHTML=m.text;
	
	// link
	var link=metadataContentsNode.querySelector("._link_");
	link.onclick=function(event) {
		document.getElementById('edit.metadata.'+m.metadataId+'.form').submit();
	}
	
	insertSpotNode.appendChild(metadataContentsNode);
	CKEDITOR.replace( 'ckeditor.metadata.'+m.metadataId);
	
}

</script>

<span id="elementContents.template.metadata.longtext"  class="metadaText" style="display:none" >
		
  <span onclick="event.stopPropagation();event.preventDefault();">
	<textarea class="_text_" id="ckeditor.metadata." ></textarea>
  </span>
	
	<br/>
	<a href="#" class="_link_" ><s:text name="global.submit" /></a>			


</span>
	    			
		
