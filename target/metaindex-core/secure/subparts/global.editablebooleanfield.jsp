<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<span class="notInEditModeDataField">
	<input type="checkbox" <c:if test="${param.inputvalue == 'true' }" >checked</c:if>  disabled="disabled"	/>
</span>
<c:if test="${param.readOnly eq 'false'}" >

	<input type="hidden" id="${param.id}.readonly" name="${param.inputname}" value="${param.inputvalue}" /> 
	
	<input type="checkbox"  class="inEditModeDataField"
			style="display:none;"
		<c:if test="${param.inputvalue == 'true' }" >checked</c:if> 								 
		onchange="document.getElementById('${param.id}.readonly').value=this.checked;${param.inputonchange}"		
	/>
</c:if>
