<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  



<!-- include Paypal payment method if found -->
<%-- s:include value="./mx_payment_paypal_proprietary.jsp" /--%>

 <script type="text/javascript" >

if (addCheckoutButton==null) {

	function addCheckoutButton(containerId, transactionId, breakdownCheckout, successCallback,failureCallback) {

	 let containerEl = document.getElementById(containerId);
	 
	 let buttonGetIt = document.createElement("BUTTON");
	 buttonGetIt.classList.add("btn");
	 buttonGetIt.classList.add("btn-success");
	 buttonGetIt.style["font-size"]="2rem";
	 buttonGetIt.innerHTML="Checkout!";
	 buttonGetIt.onclick=function(event) {
		 footer_showAlert(INFO, "Using 'test' payment interface");		 		
		 successCallback("test" /*payment partner*/, breakdownCheckout);
	 }
	 
	 breakdownCheckout.transactionId=transactionId;
	 breakdownCheckout.paymentMethod="sandbox";
	 breakdownCheckout.paymentDetails="transactionId:"+transactionId;
	 containerEl.append(buttonGetIt);
	      
 }
 
}

  </script>
