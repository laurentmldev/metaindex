<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
		

<fieldset >
<legend ><span class="fieldsetTitle" ><s:text name="community.details"/></span></legend>

<form id="community.details.form" action="<c:url value="/updateCommunityProcess" />"  method="post">
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
			
		<table style="padding:5px">			
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.idName"/></span></td>
				<td><span><s:property value="selectedCommunity.idName"/></span></td>
			</tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.creatorName"/></span></td>
				<td><span><s:property value="selectedCommunity.creatorName"/></span></td>						
			</tr>
			<tr>
				<td><span class="fieldtitle"  ><s:text name="community.nbOf"/> <s:property value="selectedCommunity.vocabulary.elementsTraduction"/></span></td>
				<td><span><s:property value="selectedCommunity.nbElements"/></span></td>						
			</tr>			
 		</table> 
</form>
 		</fieldset>
