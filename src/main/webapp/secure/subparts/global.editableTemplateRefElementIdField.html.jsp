<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script>

// function to be called in order to programmatically set value on this field
// typically used when receive info from server via websocket
function set_${param.htmlid}(curVal, isReadOnly, isTemplateLoadError) {
	
	if (curVal=='' || curVal=='0') { 
		document.getElementById("${param.htmlid}.readonly").innerHTML="---";
	} else {
		document.getElementById("${param.htmlid}.readonly").innerHTML=curVal;
	}
	
	if (curVal=='' || curVal=='0') {
		document.getElementById("${param.htmlid}.editmode.readonly").innerHTML=
				'<span style="font-style:italic" ><s:text name="workzone.clickToAdd" /> <s:text name="workzone.createnewelement.enterTemplateRef" /></span>';	
	} else {
		var textHtml=curVal;
		if (isTemplateLoadError==true || isTemplateLoadError=='true') {
			textHtml = '<s:property value="selectedCommunity.vocabulary.capElementTraduction" /> ' + textHtml +' <s:text name="workzone.elementnotatemplate"/>';
			document.getElementById("${param.htmlid}.editmode.readonly").innerHTML=textHtml;
		}
		document.getElementById("${param.htmlid}.editmode.readonly").innerHTML=textHtml;
	}
	
	
	if (isReadOnly==true || isReadOnly=='true') { 
		document.getElementById("${param.htmlid}.editmode.edit").onclick='';
	}

	if (isTemplateLoadError==true || isTemplateLoadError=='true') {
		document.getElementById("${param.htmlid}.editmode.readonly").classList.add='errorFieldValue';
	}
}
</script>


<span class="notInEditModeDataField" id="${param.htmlid}.readonly" ></span>


<a href="#" id="${param.htmlid}.editmode.readonly"
	class="modifyable inEditModeDataField"  
		
	style="display:none;"
	onclick="this.style.display='none';
			document.getElementById('${param.htmlid}.editmode.edit').style.display='block';"		
			
	title="<s:text name="workzone.element.templateRef.explanation" />"					
	>
			
</a>

<select id="${param.htmlid}.editmode.edit" 
		name="${param.inputname}"  
		onchange="${param.inputonchange}"
		style="display:none;" >

	<option value=""></option>
	<option value="0">- <s:text name="workzone.choice.none"/> -</option>
	<s:iterator value="selectedCommunity.templateElements" var="curTemplate" status="itStatus"  >		
		<option value="<s:property value="#curTemplate.elementId"/>" >
			<s:property value="#curTemplate.name"/>
		</option> 
	</s:iterator>
</select>

