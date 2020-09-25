<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  



<!-- include Paypal payment method if found -->

<c:if test="${mxDevMode == false}" >
<s:include value="./mx_payment_paypal_proprietary.jsp" />
</c:if>

<c:if test="${mxDevMode == true}" >


 <script type="text/javascript" >
 

	function addCheckoutButton(containerId, transactionId, breakdownCheckout, successCallback,failureCallback) {

	 let containerEl = document.getElementById(containerId);
	 
	 let buttonGetIt = document.createElement("BUTTON");
	 buttonGetIt.classList.add("btn");
	 buttonGetIt.classList.add("btn-success");
	 buttonGetIt.style["font-size"]="1.4rem";
	 buttonGetIt.innerHTML="Checkout (test)";
	 buttonGetIt.onclick=function(event) {
		 footer_showAlert(INFO, "Using 'sandbox' payment interface");		 		
		 successCallback("sandbox" /*payment partner*/, breakdownCheckout);
	 }
	 
	 breakdownCheckout.transactionId=transactionId;
	 breakdownCheckout.paymentMethod="sandbox";
	 breakdownCheckout.paymentDetails="transactionId:"+transactionId;
	 containerEl.append(buttonGetIt);    
 }
 </script>
</c:if>