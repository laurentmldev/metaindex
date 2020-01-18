<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<s:if test="#curMetadata.layoutDoDisplayName" >
	<span title="<s:property value="#curMetadata.comment"/>" >
		<s:property value="#curMetadata.name"/>
	</span>
</s:if>

<a href="<s:property value="#curMetadata.asWebLink.url"/>" 
	target="_blank"
	draggable="false"
	ondragstart="event.preventDefault();event.stopPropagation();">
		<c:choose>
			<c:when test="${curMetadata.asWebLink.linkText!=''}"><s:property value="#curMetadata.asWebLink.linkText"/></c:when>
			<c:otherwise><s:property value="#curMetadata.asWebLink.url"/></c:otherwise>
		</c:choose>
</a>
		
