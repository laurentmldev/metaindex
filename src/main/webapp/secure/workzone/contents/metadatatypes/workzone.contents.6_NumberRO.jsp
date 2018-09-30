<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

		<c:if test="${curMetadata.layoutDoDisplayName}" >
			<span class="fieldtitle" ><s:property value="#curMetadata.name"/>  :</span>
		</c:if>	
		<span>
			<s:property value="#curMetadata.asNumber.value"/> <s:property value="#curMetadata.asNumber.unit"/>				
		</span>
		
	


	    	
	    			
		
