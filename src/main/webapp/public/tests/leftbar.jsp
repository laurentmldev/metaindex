<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="/" var="mxurl"/>
<c:url value="/logoutprocess" var="logoutUrl"/>

<!DOCTYPE html>
<html lang="en">

<head>
  <title>MetaIndex - Catalogs</title>  
   <s:include value="../commons/html/head-meta.jsp" />
     
</head>

<body id="page-top" onload="">

  <!-- Page Wrapper -->
  <div id="wrapper">

<s:include value="../commons/html/left.jsp" />
 
  </div><!-- End of Page Wrapper -->
  
  <!-- Scroll to Top Button-->
  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>

</body>

</html>
