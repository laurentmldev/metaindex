<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<script>

// function to be called in order to programmatically set value on this field
// typically used when receive info from server via websocket
function new_color_field(htmlid,inputname,inputstyle,onchangeFunc) {
	
	var node = document.getElementById("globals.colorFieldTemplate").cloneNode(true);
	node.style.display=true;
	
	node.setValue = function(curVal, isReadOnly) {
		    		
		document.getElementById(htmlid+".color").value=curVal;
		
		if (isReadOnly) { document.getElementById(htmlid+".color").disabled=true; }			
		
		document.getElementById(htmlid+".color").onchange=onchangeFunc;
	};
	
	var colorPicker = node.querySelector("._color_pick_");	
	colorPicker.id=htmlid+".color";
	colorPicker.name=inputname;
	colorPicker.style=inputstyle;
	
	return node;
}
</script>

<span id="globals.colorFieldTemplate" style="display:none">	
	<input type="color" class="_color_pick_" >
</span>
