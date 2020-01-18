<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<img src="<s:property value="#curMetadata.asImage.imageUrl"/>" class="picture_<s:property value="#curMetadata.layoutSize"/>"
	title="<s:property value="#curMetadata.name"/>"
	alt="<s:property value="#curMetadata.name"/>"
	onclick="event.preventDefaukt();"
	draggable="false"
	ondragstart="event.preventDefault();event.stopPropagation();"
	<c:if test="${curMetadata.asImage.borderSize > 0}" >
		style="border-radius:4px;border:<s:property value="#curMetadata.asImage.borderSize"/>px solid <s:property value="#curMetadata.asImage.borderColor"/>"
	</c:if>
	/>

<c:if test="${curMetadata.layoutDoDisplayName}" >
	<div class="fieldtitle" style="margin-top:10px" >
		<a  href="<s:property value="#curMetadata.asImage.imageUrl"/>" 
			target="_blank"
			draggable="false"
			ondragstart="event.preventDefault();event.stopPropagation();"
			>
				<s:property value="#curMetadata.asImage.imageUrl"
			/>
		</a></div>
</c:if>	

	


	    	
	    			
		
