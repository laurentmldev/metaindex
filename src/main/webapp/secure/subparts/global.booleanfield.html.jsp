<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script>

// function to be called in order to programmatically set value on this field
// typically used when receive info from server via websocket
function new_boolean_field(htmlid,inputname,onchangeFunc) {
	
	var node = document.getElementById("globals.booleanFieldTemplate").cloneNode(true);
	node.style.display=true;
	
	node.setValue = function(curVal, isReadOnly) {
		
		document.getElementById(htmlid+".input").value=(curVal||curVal=='true');
		document.getElementById(htmlid+".readonly").checked=(curVal||curVal=='true');
		document.getElementById(htmlid+".editmode.checkbox").checked=(curVal||curVal=='true');
		
		
		if (isReadOnly) { 
			document.getElementById(htmlid+".readonly.container").classList='notInEditModeDataField inEditModeDataField';
			document.getElementById(htmlid+".editmode.checkbox").classList='';
			document.getElementById(htmlid+".editmode.checkbox").style.display='none';
		} else {
			
			document.getElementById(htmlid+".editmode.checkbox").classList='inEditModeDataField';
			document.getElementById(htmlid+".readonly.container").classList='notInEditModeDataField';
		}			
		
		document.getElementById(htmlid+".editmode.checkbox").onchange=function(e) {
			document.getElementById(htmlid+'.input').value=this.checked;
			onchangeFunc(e);
		}
	};
	
	var hiddenInput = node.querySelector("._hiddeninput_");
	hiddenInput.id=htmlid+".input";
	hiddenInput.name=inputname;
	
	var ROcontainer = node.querySelector("._ROcontainer_");
	ROcontainer.id=htmlid+".readonly.container";
	
	var ROcheckbox = node.querySelector("._ROcheckbox_");	
	ROcheckbox.id=htmlid+".readonly";
	
	var checkbox = node.querySelector("._checkbox_");	
	checkbox.id=htmlid+".editmode.checkbox";
	
	
	return node;
}
</script>

<span id="globals.booleanFieldTemplate" style="display:none">
	
	<input type="hidden" class="_hiddeninput_"  value="" >
	
	<span class="notInEditModeDataField _ROcontainer_" >
		<input type="checkbox" class="_ROcheckbox_" disabled="disabled"	>
	</span>
	
	<input type="checkbox"  class="inEditModeDataField _checkbox_" style="display:none;" >
</span>



