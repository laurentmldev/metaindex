<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<input  name="selectedElement.datasetsMap['<s:property value="#curDataset.datasetId"/>'].metadatasMap['<s:property value="#curMetadata.metadataId"/>'].asNumber.value" 
		value="<s:property value="#curMetadata.asNumber.value"/>"
		onclick="event.stopPropagation();" >

<a href="#" onclick="document.getElementById('edit.metadata.<s:property value="#curMetadata.metadataId"/>.form').submit()"><s:text name="global.submit" /></a>
		
		
	


	    	
	    			
		
