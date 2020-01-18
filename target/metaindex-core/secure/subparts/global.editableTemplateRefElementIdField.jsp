<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<span class="notInEditModeDataField" >${param.text}<c:if test="${param.text eq '' or param.text eq '0'}" >---</c:if></span>

<c:if test="${param.readOnly eq 'false'}" >

	<a href="#" id="${param.id}.readonly"
		class="modifyable inEditModeDataField 
			<c:if test="${selectedElement.templateLoadError}" >errorFieldValue</c:if>"  
			
		style="display:none;"
		onclick="this.style.display='none';
				document.getElementById('${param.id}.edit').style.display='block';"		
				
		title="<s:text name="workzone.element.templateRef.explanation" />"					
		>
		<c:if test="${param.text eq '' or param.text eq '0'}" >
			<span style="font-style:italic" ><s:text name="workzone.clickToAdd" /> <s:text name="workzone.createnewelement.enterTemplateRef" /></span>
		</c:if>
		<c:if test="${param.text ne '' and param.text ne '0'}" >
			<c:if test="${selectedElement.templateLoadError}" ><s:property value="selectedCommunity.vocabulary.capElementTraduction" /> '</c:if>${param.text}<c:if test="${selectedElement.templateLoadError}" >' <s:text name="workzone.elementnotatemplate"/></c:if></c:if>		
	</a>

<select id="${param.id}.edit" 
		name="${param.inputname}"  
		onchange="${param.inputonchange}"
		style="display:none;" >

	<option value=""></option>
	<option value="0">- <s:text name="workzone.choice.none"/> -</option>
	<s:iterator value="selectedCommunity.templateElements" var="curTemplate" status="itStatus"  >		
		<option value="<s:property value="#curTemplate.elementId"/>" <c:if test="${param.inputvalue == curTemplate.elementId}" >selected</c:if> >
			<s:property value="#curTemplate.name"/>
		</option> 
	</s:iterator>
</select>

</c:if>
