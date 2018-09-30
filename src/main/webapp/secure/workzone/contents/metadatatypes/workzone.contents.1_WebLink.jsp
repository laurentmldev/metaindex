<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

function insert_metadata_WebLink(insertSpotId,m) {
	var insertSpotNode = document.getElementById(insertSpotId);
	var metadataContentsNode = document.getElementById("_elementContents.template.metadata.weblink_").cloneNode(true);
	metadataContentsNode.id+=m.metadataId;
	
	// input linkText
	var linkTextInputNode = metadataContentsNode.querySelector("._input_linkText_");
	linkTextInputNode.name="selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asWebLink.linkText";	
	linkTextInputNode.value=m.linkText;
	
	// input url
	var urlInputNode = metadataContentsNode.querySelector("._input_url_");
	urlInputNode.name="selectedElement.datasetsMap['"+m.datasetId+"'].metadatasMap['"+m.metadataId+"'].asWebLink.url";	
	urlInputNode.value=m.url;
	
	// sendForm
	var sendFormNode = metadataContentsNode.querySelector("._sendForm_");
	sendFormNode.onclick=function(event) {
		document.getElementById('edit.metadata.'+m.metadataId+'.form').submit();
	}
	
	insertSpotNode.appendChild(metadataContentsNode);
}

</script>

<span id="edit.metadata."  class="_elementContents.template.metadata.weblink_ matadataTextEdit"  style="display:none" >

<br/>

<table >
<tr ><td >
<span class="fieldtitle" style="margin-left:20px;margin-right:10px;"><s:text name="workzone.metadata.layout.urlText" /></span></td> 
<td>
	<input class="_input_linkText_" name="" value="" style="max-width:200px;" size="80" draggable="false"
		placeholder="<s:text name="workzone.metadata.layout.enterUrlText"/>"							
		ondragstart="event.preventDefault();event.stopPropagation();"
		onclick="event.stopPropagation();" >

</td></tr>
<tr >
<td style="padding-top:10px;">
	<span class="fieldtitle" style="margin-left:20px;margin-right:10px;width:200px;"><s:text name="workzone.metadata.layout.urlLink" /></span>
</td> 
<td style="padding-top:10px;">
	<input class="_input_url_" name="" value="" style="max-width:200px;" size="80" draggable="false"
		placeholder="<s:text name="workzone.metadata.layout.enterUrlLink"/>"	
		ondragstart="event.preventDefault();event.stopPropagation();"
		onclick="event.stopPropagation();" >
</td>
</tr>
<tr><td colspan="2" style="padding-top:15px;text-align:center;">


<a class="_sendForm_" href="#" ><s:text name="global.submit" /></a>

</td></tr>
	  </table>  			
		
</span>
