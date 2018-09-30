<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

// function to be called in order to programmatically set value on this field
// typically used when receive info from server via websocket
function new_editable_field(htmlid,inputname,inputstyle,inputplaceholder,onchangeFunc) {
	
	if (typeof inputstyle=== 'undefined') { inputstyle=""; }
	
 	var node = document.getElementById("globals.ediatableFieldTemplate").cloneNode(true);
	node.id=htmlid;
	
	node.setValue = function(curVal, isReadOnly) {
		    				
		document.getElementById(htmlid+".readonly").innerHTML=curVal;
		document.getElementById(htmlid+".editmode.readonly").innerHTML=curVal;
		document.getElementById(htmlid+".editmode.readmodifiable.contents").innerHTML=curVal;
		document.getElementById(htmlid+".editmode.edit").value=curVal;
		
		if (isReadOnly) { 
			document.getElementById(htmlid+".editmode.readmodifiable").classList='';			
		} else {
			document.getElementById(htmlid+".editmode.readonly").classList='';
		}
				
		if (curVal=='') { document.getElementById(htmlid+".emptytext").style.display='block'; }
		else { document.getElementById(htmlid+".emptytext").style.display='none'; }
		
		document.getElementById(htmlid+".editmode.readmodifiable").onclick=function(e) {
												this.style.display='none';
												document.getElementById(htmlid+'.editmode.edit').style.display='block';
											}	
		
		document.getElementById(htmlid+".editmode.edit").onchange=onchangeFunc;
		document.getElementById(htmlid+".editmode.edit").onblur=function(e) {
									node.style.display='none';
									document.getElementById(htmlid+'.editmode.readmodifiable').style.display='block';
								}
				
	}
	
	
	var readOnlyNode = node.querySelector("._readonly_");
	readOnlyNode.id=htmlid+".readonly";
	
	var editNodeRO = node.querySelector("._editRO_");
	editNodeRO.id=htmlid+".editmode.readonly";
	
	var editlink = node.querySelector("._link_");
	editlink.id = htmlid+".editmode.readmodifiable";	
	
	var emptyTextNode = node.querySelector("._emptytext_");
	emptyTextNode.id = htmlid+".emptytext";
	emptyTextNode.innerHTML+=" "+inputplaceholder;
	
	var editROText = node.querySelector("._editROContents_");
	editROText.id = htmlid+".editmode.readmodifiable.contents";
	
	var input = node.querySelector("._input_");
	input.id=htmlid+".editmode.edit";
	input.classList.add(inputstyle);
	input.name=inputname;
	input.placeholder=inputplaceholder;
	
	return node;
}
</script>

<span id="globals.ediatableFieldTemplate">

	<span class="_readonly_ notInEditModeDataField"  ></span>
	
	<span class="_editRO_ inEditModeDataField" style="display:none;" ></span>
	
	<a href="#" class="modifyable inEditModeDataField _link_"  
		style="display:none;" >
		
		<span class="_emptytext_" style="font-style:italic;display:none" >
			<s:text name="workzone.clickToAdd" />
		</span>
		<span class="_editROContents_">			
		</span>			
	</a>
		
	
	<input type="text" class="_input_"
		style="display:none;"  
		onfocus="this.select();"
		onkeypress="event.stopPropagation();"
		onkeydown="event.stopPropagation();"
		/>

</span>
