<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 
 <script type="text/javascript" >
 
 var bootstrap_confirmations=[];
 
 function bootstrap_confirmation_finishConfirmations() {
	 
	 $('[data-toggle=confirmation]').each(function() {
		 bootstrap_confirmations.push(this);		 
	 });
	 
	 // activate 'bootstrap confirmation' popups
	//let fieldsList = Object.keys(itemCard.descr.data);
	for (var idx=0;idx<bootstrap_confirmations.length;idx++) {
		let confirmationNode = bootstrap_confirmations[idx];	
		let onConfirmAction=confirmationNode.getAttribute('onConfirm');
		let onCancelAction=confirmationNode.getAttribute('onCancel');
		
		$(confirmationNode).confirmation(
				{
					rootSelector: '[data-toggle=confirmation]',
				    container: 'body',
				    onConfirm: new Function(onConfirmAction),
				  	onCancel : new Function(onCancelAction)
				});			
	}
	/*
	  // enable confirmation popups
	  $('[data-toggle=confirmation]').confirmation({
		  rootSelector: '[data-toggle=confirmation]',
		    container: 'body',
		    onConfirm: function(value) { console.log("Confirmed : "+this.onConfirm); },
		  	onCancel : function() { console.log("Canceled"); }
	  });
	  */
 }
 </script>
 
