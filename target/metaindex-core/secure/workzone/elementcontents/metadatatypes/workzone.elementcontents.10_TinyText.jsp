<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

		<c:if test="${curMetadata.layoutDoDisplayName}" >
			<span class="fieldtitle" ><s:property value="#curMetadata.name"/>  :</span>
		</c:if>	
		<span 
			style="
				<c:if test="${curMetadata.asTinyText.fontWeight=='Italic'}">
					font-style:italic;
				</c:if>
				<c:if test="${curMetadata.asTinyText.fontWeight!='Italic'}">
					font-weight:<s:property value="#curMetadata.asTinyText.fontWeight"/>;
				</c:if>
				" 
				>
			<s:property value="#curMetadata.asTinyText.text"/>
		</span>
		
	


	    	
	    			
		
