<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 <script type="text/javascript" >
 
 /* /!\ pk and fieldName shall not contain '.' nor space character! */
 
 // xeditable_create_text_field
 // xeditable_create_dropdown_field
 // xeditable_create_checklist_field
 
 var _xeditable_fields=[];
 
 function _xeditable_getEditableFieldNodeId(pk,fieldName) { 
	 // ID shall not have any special character
	 fieldNameId=fieldName.replace(" ","_").replace(".","_").replace(",","_").replace(";","_").replace('@','_');
	 pkStr=pk.replace(" ","_").replace(".","_").replace(",","_").replace(";","_").replace('@','_');
	 return fieldXeditableId=pkStr+"_"+fieldNameId+"_"+_xeditable_fields.length; 
}
 
 function _xeditable_prepare_valueNode(fieldValueNode,nodeId,pk,fieldName) {
	// /!\ x-editable does not work if node id contains dots (ex: 'a.b.c'), using '_' instead
	fieldValueNode.id=nodeId+"_valueNode";
	fieldValueNode.fieldName=fieldName;	
	fieldValueNode.pk=pk;
 }
 
// -------------------------------------------------
// -------------------------------------------------
 
//--------- Text ----------

 /*/!\ pk and name shall not contain '.' nor space character! */
 function xeditable_create_text_field(	pk,
										fieldName, 
										showFieldName /*Boolean*/,
		 								fieldValue,
		 								onchangeCallback /* onchangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback) */,
		 								successCallback,/* successCallback(fieldName,newValue) */
		 								errorCallback /* errorCallback(errorMsg) */) 
 {
	
	
	if (onchangeCallback==null) { onchangeCallback=function() {}; }
	if (successCallback==null) { successCallback=function() {}; }
	if (errorCallback==null) { errorCallback=function(msg) {}; }
			 	 
	let newFieldNode = document.getElementById("MxGui._templates_.xeditable_field.text").cloneNode(true);
	newFieldNode.style.display='block';
	// /!\ x-editable does not work if node id contains dots (ex: 'a.b.c'), using '_' instead
	newFieldNode.id=_xeditable_getEditableFieldNodeId(pk,fieldName);
	
	newFieldNode.onchangeCallback=onchangeCallback;
	newFieldNode.successCallback=successCallback;
	newFieldNode.errorCallback=errorCallback;
	
	// name
	if (showFieldName) {
		let fieldNameNode = newFieldNode.querySelector("._name_");
		fieldNameNode.innerHTML=fieldName+": ";
		fieldNameNode.style.display='inline';
	}
	
	// value		
	let fieldValueNode = newFieldNode.querySelector("._value_");	
	fieldValueNode.innerHTML=fieldValue;	
	_xeditable_prepare_valueNode(fieldValueNode,newFieldNode.id,pk,fieldName);
	
	_xeditable_fields.push(fieldValueNode);
	
	return newFieldNode;

 }
 function _xeditable_finish_text_field(fieldValueNode) {
	 	
	 $('#'+fieldValueNode.id).editable(
				{
					// from commons/js/mx_ws_connect
					url:_xeditable_onchange,
					pk:fieldValueNode.id
				});
	  
 }


 // --------- Dropdown & Checklist ----------

 var MX_XEDITABLE_SELECT="select";
 var MX_XEDITABLE_CHECKLIST="checklist";
 /*/!\ pk shall not contain '.' character */
 function _xeditable_create_list_field( pk,
										fieldName,
										showFieldName /*Boolean*/,
		 								fieldValue,
		 								inputType /* MX_XEDITABLE_SELECT|MX_XEDITABLE_CHECKLIST */,
		 								choicesDef,// [ {value:val1, text:"text 1"}, {value:val2, text:"text 2"} , ... ]
		 								onchangeCallback /* onchangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback) */,
		 								successCallback,/* successCallback(fieldName,newValue) */
		 								errorCallback /* errorCallback(errorMsg) */) 
 {
	if (inputType!=MX_XEDITABLE_CHECKLIST && inputType!=MX_XEDITABLE_SELECT) {inputType=MX_XEDITABLE_SELECT; }
	if (onchangeCallback==null) { onchangeCallback=function() {}; }
	if (successCallback==null) { successCallback=function() {}; }
	if (errorCallback==null) { errorCallback=function(msg) {}; }
			 	
	let newFieldNode = document.getElementById("MxGui._templates_.xeditable_field.dropdown").cloneNode(true);
	newFieldNode.style.display='block';
	// /!\ x-editable does not work if node id contains dots (ex: 'a.b.c'), using '_' instead
	newFieldNode.id=_xeditable_getEditableFieldNodeId(pk,fieldName);
	
	newFieldNode.onchangeCallback=onchangeCallback;
	newFieldNode.successCallback=successCallback;
	newFieldNode.errorCallback=errorCallback;
	
	// name
	if (showFieldName) {
		let fieldNameNode = newFieldNode.querySelector("._name_");
		fieldNameNode.innerHTML=fieldName+": ";
		fieldNameNode.style.display='inline';
	}
	// value		
	let fieldValueNode = newFieldNode.querySelector("._value_");	
	_xeditable_prepare_valueNode(fieldValueNode,newFieldNode.id,pk,fieldName);
	fieldValueNode.type=inputType;
	fieldValueNode.choicesDef=choicesDef;
	fieldValueNode.curValue=fieldValue;
	
	_xeditable_fields.push(fieldValueNode);
	
	return newFieldNode;

 }
 
 
 // make available functions :
 // <node>.addOption(value,text) : to dynamically uptate options
 // <node>.setValue(value) : to dynamically uptate current value
 function _xeditable_create_dynamicdropdown_field(pk,
										fieldName,
										showFieldName /*Boolean*/,
		 								fieldValue,
		 								onchangeCallback /* onchangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback) */,
		 								successCallback,/* successCallback(fieldName,newValue) */
		 								errorCallback /* errorCallback(errorMsg) */) 
 {
	if (onchangeCallback==null) { onchangeCallback=function() {}; }
	if (successCallback==null) { successCallback=function() {}; }
	if (errorCallback==null) { errorCallback=function(msg) {}; }
	
	let dynDropDown = document.getElementById("MxGui._templates_.xeditable_field.dyndropdown").cloneNode("true");
	dynDropDown.style.display="block";
	
	// name
	if (showFieldName) {
		let fieldNameNode = dynDropDown.querySelector("._name_");
		fieldNameNode.innerHTML=fieldName+": ";
		fieldNameNode.style.display='inline';
	}
	
	// current value
	let curValue=dynDropDown.querySelector("._current_value_");
	let optionsNode=dynDropDown.querySelector("._options_");
	curValue.onclick=function(event) { optionsNode.style.display="block"; }
	curValue.innerHTML=fieldValue;
	dynDropDown.setValue=function(val) {
		if (typeof(val)==='string') { curValue.innerHTML=val; }
		else { curValue.appendChild(val); }
	}
	
	// dynamic fill-in options available for user-app side	
	dynDropDown.addOption=function(value,text) {
		//console.log("adding option : "+value+" / "+text);
		let newOption = document.createElement("a");
		newOption.title=value;
		newOption.innerHTML=text;
		optionsNode.appendChild(newOption);
		let onFailedChange=function(msg) { 
			footer_showAlert(ERROR,msg);
			errorCallback(msg);
		}
		let onSuccessChange=function(fieldName,value) { 
			footer_showAlert(SUCCESS,fieldName+"="+value);
			dynDropDown.setValue(value);
			successCallback(fieldName,value);
			curValue.style.background="";
			curValue.classList.add("editable-bg-transition");
			optionsNode.style.display="none";
		}
		newOption.onclick=function(event) {
			onchangeCallback(pk,fieldName,value,onSuccessChange,onFailedChange);
			curValue.classList.remove("editable-bg-transition");
			curValue.style.background="yellow";
			
		}
	}	
	
	return dynDropDown;
			 	
 }
 
 
 function xeditable_create_dropdown_field(
		 	pk /*primary key*/,
			fieldName,
			showFieldName /*Boolean*/,
			fieldValue,
			// choicesDef = [ {value:val1, text:"text 1"}, {value:val2, text:"text 2"} , ... ]
			// if null, make available function <node>.addOption(value,text) to dynamically uptate contents
			choicesDef,
			onchangeCallback /* onchangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback) */,
			successCallback,/* successCallback(newValue) */
			errorCallback /* errorCallback(errorMsg) */) 
{
	 
	 if (choicesDef==null)
	 {		 
		 // define a "addOption" function letting user-side defining contents of the node
		 return _xeditable_create_dynamicdropdown_field(
				 pk,fieldName,showFieldName,fieldValue,
				 onchangeCallback,successCallback,errorCallback) 
	 }
	 else {
		return _xeditable_create_list_field(pk,fieldName,showFieldName,fieldValue,
											MX_XEDITABLE_SELECT,choicesDef,
											onchangeCallback,successCallback,errorCallback);
	 }
}
 function xeditable_create_checklist_field(
		 	pk /*primary key*/,
			fieldName,
			showFieldName /*Boolean*/,
			fieldValue,
			choicesDef,// [ {value:val1, text:"text 1"}, {value:val2, text:"text 2"} , ... ]
			onchangeCallback /* onchangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback) */,
			successCallback,/* successCallback(newValue) */
			errorCallback /* errorCallback(errorMsg) */) 
{
	if (! fieldValue instanceof Array) { fieldValue=fieldValue.split(","); }
	return _xeditable_create_list_field(pk,fieldName,showFieldName,fieldValue,
											MX_XEDITABLE_CHECKLIST,choicesDef,
											onchangeCallback,successCallback,errorCallback);
}
 

//--------- Boolean ----------
 
 var xeditable_yes="<s:text name="global.yes"></s:text>";
 var xeditable_no="<s:text name="global.no"></s:text>";
 
 function getTrueFalseChoice() {
	 return [{value:"true", text:xeditable_yes},{value:"false", text:xeditable_no}];
 }
 
function xeditable_create_boolean_field(
		 	pk /*primary key*/,
			fieldName,
			showFieldName /*Boolean*/,
			fieldValue,
			onchangeCallback /* onchangeCallback(pk,fieldName,fieldValue,successCallback, errorCallback) */,
			successCallback,/* successCallback(newValue) */
			errorCallback /* errorCallback(errorMsg) */) 
{
	if (fieldValue==true) { fieldValue='true'; }
	else if (fieldValue==false) { fieldValue='false'; }
	
	return _xeditable_create_list_field(pk,fieldName,showFieldName,fieldValue,
											MX_XEDITABLE_SELECT,getTrueFalseChoice(),
											onchangeCallback,successCallback,errorCallback);
}
 function _xeditable_finish_list_field(fieldValueNode) {
	 
	 //console.log("EDITABLE list pk="+fieldValueNode.id);
	 $('#'+fieldValueNode.id).editable(
		{
			// from commons/js/mx_ws_connect
			url:_xeditable_onchange,
			pk:fieldValueNode.id,
			type:fieldValueNode.type,
			title:'Choose',
			placement:'right',
			value:fieldValueNode.curValue,
			source: fieldValueNode.choicesDef
		});	 
 }
 
 // -------------------------------------------------
 // -------------------------------------------------
 
 function _xeditable_onchange(xeditableParamInfo) {
	  
	 let fieldValueNode=document.getElementById(xeditableParamInfo.pk);
	 let fieldNameNode=document.getElementById(xeditableParamInfo.name);
	 	
	 let fieldName=fieldNameNode.fieldName;
	 let fieldValue=xeditableParamInfo.value;
	 
	 //console.log("CHANGED pk="+xeditableParamInfo.pk+ " fieldName=" +fieldName+" fieldValue="+fieldValue);
 	 //_name_ node shall be direct child of root node
	 let fieldNode=fieldNameNode.parentNode;
	 
	 fieldValueNode.defferedAnswer=new $.Deferred();
	 fieldNode.onchangeCallback(fieldValueNode.pk,fieldName,fieldValue,
			 // success callback
			 function() { 
		 		fieldNode.successCallback(fieldName,fieldValue); // user success callback 
		 		fieldValueNode.defferedAnswer.resolve(); 
		 	 },
		 	 // error callback
			 function(msg) { 
		 		 fieldNode.errorCallback(msg);  // user error callback
		 		 fieldValueNode.defferedAnswer.reject(msg); 
		 	 }
	   ); 
	
	return fieldValueNode.defferedAnswer.promise();
}
 
 
 function xeditable_finishEditableFields() {
	 
		// activate 'x-editable' features
		//let fieldsList = Object.keys(itemCard.descr.data);
		for (var idx=0;idx<_xeditable_fields.length;idx++) {
			let fieldValueNode = _xeditable_fields[idx];	
			if (fieldValueNode.choicesDef!=null) { _xeditable_finish_list_field(fieldValueNode); }
			else { _xeditable_finish_text_field(fieldValueNode); }	
		}		
 }
 
 
 function filterDynamicDropdownFunction(dyndropRootNode) {
	  var input, filter, ul, li, a, i;
	  input = dyndropRootNode.querySelector("._search_input_");
	  filterQuery = input.value.trim().toUpperCase();
	  div =  dyndropRootNode.querySelector("._options_");
	  a = div.getElementsByTagName("a");
	  for (i = 0; i < a.length; i++) {
		  shallDisplay=false; 
	    txtValue = a[i].textContent || a[i].innerText;
	    txtElementId=a[i].href
	    if (txtValue.toUpperCase().indexOf(filterQuery) > -1) { shallDisplay=true; }
	    else if (txtElementId.toUpperCase().indexOf(filterQuery) > -1) { shallDisplay=true; }
	    
	    if (shallDisplay){
	      a[i].style.display = "";
	    } else {
	      a[i].style.display = "none";
	    }
	  }
	} 
 
 </script>
 

 <div id="MxGui._templates_.xeditable_field.text" style="display:none" >
 	<!-- _name_ node shall be direct child of root node -->
 	<span class="_name_ mx-perspective-field-title" style="display:none;" ></span> 	 
 	<a href="#" class="_value_ editable editable-click mx-editable " 
 			 data-type="text" data-placement="right" data-title="<s:text name='global.enter_value'/>" 
 			style="padding:0.2rem;display:inline-block;max-width:100px;overflow:auto;" ></a>
 </div>
 
 
 <div id="MxGui._templates_.xeditable_field.dropdown" style="display:none" >
 	<!-- _name_ node shall be direct child of root node -->
 	<span class="_name_ mx-perspective-field-title" style="display:none;" ></span>	 
 	<a href="#" class="_value_ editable editable-click mx-editable " style="padding:0.2rem;" ></a>
 </div>

 <div id="MxGui._templates_.xeditable_field.dyndropdown" style="display:none" class="mx-dropdown"
 	onkeypress="event.stopPropagation();" onkeydown="event.stopPropagation();">
  <span class="_name_ mx-perspective-field-title" style="display:none;" ></span>
  <button onclick="this.parentNode.querySelector('._options_').classList.toggle('show');" 
  		class="editable editable-click editable-open mx-editable mx_dyndropdown_btn _current_value_"  ></button>
  <div  class="mx_dyndropdown_content _options_">
    <input type="text" class="mx_dyndropdown_searchinput _search_input_" placeholder="<s:text name="global.search" /> ..."  onkeyup="filterDynamicDropdownFunction(parentNode.parentNode)">  
  </div>
</div> 