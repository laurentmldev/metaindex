<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript" >

function details_createEditableTerm(itemId, termDesc, curTermValue, onChangeCallback, successCallback) {
	
	if (termDesc.datatype=='TINY_TEXT' && termDesc.enumsList.length>0
			// when list is empty, should be considered as a pure string  not an 'empty' list
			 && !(termDesc.enumsList.length==1 && termDesc.enumsList[0]=="")) { 
	 	
		valuesChoice=[];
		for (var i=0;i<termDesc.enumsList.length;i++) {
			 let curValue=termDesc.enumsList[i];
			 valuesChoice.push({'value':curValue,'text':curValue});
		 }
		
		if (termDesc.isMultiEnum==true){
			return xeditable_create_checklist_field(
					itemId /* pk */,
					termDesc.name,true /*show fieldName*/,
					curTermValue,
					valuesChoice,
					onChangeCallback,
					successCallback);
		} else {
			return xeditable_create_dropdown_field(
				itemId /* pk */,
				termDesc.name,true /*show fieldName*/,
				curTermValue,
				valuesChoice,
				onChangeCallback,
				successCallback);
		}
	}
	else { 
		return xeditable_create_text_field(
			itemId /* pk */,
			termDesc.name,true /*show fieldName*/,
			curTermValue,
			onChangeCallback,
			successCallback);
	}
}
</script>
