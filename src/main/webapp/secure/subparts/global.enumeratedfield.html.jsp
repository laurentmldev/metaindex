<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<script>

// function to be called in order to programmatically set value on this field
// typically used when receive info from server via websocket
//
// @param : options : array of hash tables [ {value:4, text:"option1"},{value:2, text:"option2"} ]
//
function new_enumerated_field(htmlid,inputname,options,onchangeFunc) {
	
	var node = document.getElementById("globals.enumFieldTemplate").cloneNode(true);
	
	node.setValue = function(curVal, isReadOnly) {
		    				
		curValText=curVal;
		for (var i=0;i<options.length;i++) {
			if (options[i]['value']==curVal) { 
				curValText=options[i]['text'];
				break;
			}
		}
		document.getElementById(htmlid+".readonly").innerHTML=curValText;
		document.getElementById(htmlid+".editmode.readonly").innerHTML=curValText;
		document.getElementById(htmlid+".editmode.readmodifiable.contents").innerHTML=curValText;
		document.getElementById(htmlid+".editmode.edit").value=curVal;
		
		if (isReadOnly) { 
			document.getElementById(htmlid+".editmode.readmodifiable").classList='';			
		} else {
			document.getElementById(htmlid+".editmode.readonly").classList='';
		}
		
		if (curVal=='' || curVal==0) { this.querySelector("._emptytext_").style.display='block'; }
		else { this.querySelector("._emptytext_").style.display='none'; }
		
		document.getElementById(htmlid+".editmode.readmodifiable").onclick=function(e) {
												this.style.display='none';
												document.getElementById(htmlid+'.editmode.edit').style.display='block';
											}	
		
		document.getElementById(htmlid+".editmode.edit").onchange=onchangeFunc;
		document.getElementById(htmlid+".editmode.edit").onblur=function(e) {
			this.style.display='none';
			document.getElementById(htmlid+'.editmode.readmodifiable').style.display='block';
		};
				
	};
	
	var readOnlyNode = node.querySelector("._readonly_");
	readOnlyNode.id=htmlid+".readonly";
	
	var editNodeRO = node.querySelector("._editRO_");
	editNodeRO.id=htmlid+".editmode.readonly";
	
	var editlink = node.querySelector("._link_");
	editlink.id = htmlid+".editmode.readmodifiable";
	
	var editROText = node.querySelector("._editROContents_");
	editROText.id = htmlid+".editmode.readmodifiable.contents";
	
	var select = node.querySelector("._select_");
	select.id=htmlid+".editmode.edit";
	select.name=inputname;
	var optionsHtml="";
	for (var i=0;i<options.length;i++) {
		optionsHtml+="<option value='"+options[i]['value']+"' >"+options[i]['text']+"</option>";
	}
	select.innerHTML=optionsHtml;
	
	
	return node;
}
</script>

<span id="globals.enumFieldTemplate" style="display:none">

	<span class="_readonly_ notInEditModeDataField"  ></span>
	
	<span class="_editRO_ inEditModeDataField" style="display:none;" ></span>
	
	<a href="#" class="modifyable inEditModeDataField _link_"  
		style="display:none;" 
		 >	
		<span class="_emptytext_" style="font-style:italic;display:none" >
			<s:text name="workzone.clickToAdd" />
		</span>
		<span class="_editROContents_">			
		</span>			
	</a>
		
	<select class="_select_" style="display:none;" ></select>
	


</span>
