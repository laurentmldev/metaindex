<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<br/>

<table >
<tr ><td >
<span class="fieldtitle" style="margin-left:20px;margin-right:10px;"><s:text name="workzone.metadata.layout.urlText" /></span></td> 
<td><input  name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId"/>'].metadatasMap['<s:property value="#curMetadata.metadataId"/>'].asWebLink.linkText" 
		value="<s:property value="#curMetadata.asWebLink.linkText"/>"
		style="max-width:200px;"
		placeholder="<s:text name="workzone.metadata.layout.enterUrlText"/>"					
		size="80"
		draggable="false"
		ondragstart="event.preventDefault();event.stopPropagation();"
		onclick="event.stopPropagation();"
	>

</td></tr>
<tr >
<td style="padding-top:10px;">
	<span class="fieldtitle" style="margin-left:20px;margin-right:10px;width:200px;"><s:text name="workzone.metadata.layout.urlLink" /></span>
</td> 
<td style="padding-top:10px;">
	<input  name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId"/>'].metadatasMap['<s:property value="#curMetadata.metadataId"/>'].asWebLink.url" 
		value="<s:property value="#curMetadata.asWebLink.url"/>"
		style="max-width:200px;"	
		placeholder="<s:text name="workzone.metadata.layout.enterUrlLink"/>"	
		size="80"
		draggable="false"
		ondragstart="event.preventDefault();event.stopPropagation();"
		onclick="event.stopPropagation();"
	>
</td>
</tr>
<tr><td colspan="2" style="padding-top:15px;text-align:center;">


<a href="#"  onclick="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.form').submit()"><s:text name="global.submit" /></a>

</td></tr>
	  </table>  			
		
