<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<img src="<s:property value="#curMetadata.asImage.imageUrl"/>" class="picture_<s:property value="#curMetadata.layoutSize"/>"
	draggable="false"
	ondragstart="event.preventDefault();event.stopPropagation();"
	<c:if test="${curMetadata.asImage.borderSize > 0}" >
		style="border-radius:4px;border:<s:property value="#curMetadata.asImage.borderSize"/>px solid <s:property value="#curMetadata.asImage.borderColor"/>"
	</c:if>
/>
<div>
<input  name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId"/>'].metadatasMap['<s:property value="#curMetadata.metadataId"/>'].asImage.imageUrl" 
		value="<s:property value="#curMetadata.asImage.imageUrl"/>"
		style="margin-top:10px"		
		size="80"		
		ondragstart="event.preventDefault();event.stopPropagation();"
		onclick="event.stopPropagation();"		
	>

<a href="#"  onclick="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.form').submit()"><s:text name="global.submit" /></a>
		
</div>	
	


	    	
	    			
		
