<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
  
  
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="MetaindeX - Opensource Cataloger App">
  <meta name="author" content="Editions du Tilleul - Laurent ML">
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta name="robots" content="noindex, nofollow">
  <meta name="googlebot" content="noindex, nofollow">
  
  <link rel="icon" type="image/svg" href="public/commons/media/img/favicon.png">
  	
  <script type="text/javascript" src="${mxurl}public/commons/deps/jquery/jquery.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery-easing/jquery.easing.min.js"></script>
  <script src="${mxurl}public/commons/deps/popper/popper.min.js"></script>
    
   
  <!-- Bootstrap core JavaScript-->  
  <script src="${mxurl}public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Bootstrap Editable -->
  <link rel="stylesheet" type="text/css" href="${mxurl}public/commons/deps/bootstrap-editable/css/bootstrap-editable.css">
  <script type="text/javascript" src="${mxurl}public/commons/deps/bootstrap-editable/js/bootstrap-editable.js"></script>
  
  <!-- Bootstrap Confirmation -->  
  <script src="${mxurl}public/commons/deps/bootstrap-confirmation/bootstrap-confirmation.js"></script>
  
  <!-- Paypal API -->
   <script src="https://www.paypal.com/sdk/js?client-id=sb&currency=EUR">/*${mxPaypalId}*/</script>
  
  <!-- Fontawsome icons -->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />
  
  <!-- some generic helpers -->
  <s:include value="../js/mx_helpers.jsp" />
  <s:include value="./xeditable.jsp" />
  <s:include value="./bootstrap-confirmation.jsp" />
  <s:include value="./popups.jsp" />
  
  
 <!-- Declare cards to be manipulated in the GUI -->
 <s:include value="card.jsp" />
  
  <!-- API to connect to Metaindex server --> 
  <s:include value="../js/mx_ws_connect.jsp" />
  

  
