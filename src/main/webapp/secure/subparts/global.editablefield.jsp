<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<span class="notInEditModeDataField" >${param.text}</span>

<c:if test="${param.readOnly eq 'true'}" >
	<span class="inEditModeDataField" >${param.text}</span>
</c:if>
<c:if test="${param.readOnly eq 'false' }" >
	<a href="#" id="${param.id}.readonly"
		class="modifyable inEditModeDataField"  
		style="display:none;"
		onclick="this.style.display='none';
				document.getElementById('${param.id}.edit').style.display='block';"							
		>
		<c:if test="${param.text eq ''}" >
			<span style="font-style:italic" ><s:text name="workzone.clickToAdd" /> <s:text name="workzone.createnewelement.enterComment" /></span>
		</c:if>
		
		${param.text}
			
	</a>
	

<input id="${param.id}.edit" type="text"
	style="display:none;${param.inputstyle}"  
	name="${param.inputname}" 
	value="${param.inputvalue}" 							
	placeholder="${param.inputplaceholder}"							
	onchange="${param.inputonchange}"
	onfocus="this.select();" 
	onblur="this.style.display='none';
			document.getElementById('${param.id}.readonly').style.display='block';"/>


	</c:if>
