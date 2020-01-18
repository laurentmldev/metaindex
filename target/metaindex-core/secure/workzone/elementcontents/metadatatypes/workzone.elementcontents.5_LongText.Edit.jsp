
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	
	<c:url value="/public/deps/ckeditor/" var="ckeditorUrl"/>
	<script type="text/javascript" src="${ckeditorUrl}/ckeditor.js"></script>
		
	<span onclick="event.stopPropagation();event.preventDefault();">
	<textarea id="ckeditor.metadata.<s:property value="#curMetadata.metadataId"/>" 												
					name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId"/>'].metadatasMap['<s:property value="#curMetadata.metadataId"/>'].asLongText.text" >							
					<s:property value="#curMetadata.asLongText.text"/>
	</textarea>
	</span>
	<script type="text/javascript">
		CKEDITOR.replace( 'ckeditor.metadata.<s:property value="#curMetadata.metadataId"/>');
	</script>
	<br/>
	<a href="#" onclick="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.form').submit()"><s:text name="global.submit" /></a>			


	    	
	    			
		
