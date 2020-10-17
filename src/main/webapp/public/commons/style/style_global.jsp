<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>

<c:url value="/public/commons/media/img" var="img_path"/>
<c:url value="/public/commons/style/less" var="less_path"/>
<c:url value="/public/commons/deps/less.min.js" var="less_javascript"/>

  <!-- Custom fonts for this template-->
  <link href="${webAppBaseUrl}/public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

<link href="${webAppBaseUrl}/public/commons/style/css/sb-admin-2.css" rel="stylesheet">

  <!--  Set GUI Theme to Silver by default when no GU theme session attribute available -->
  <link href="${less_path}/color_themes/<s:property value='currentUserProfile.guiThemeShortname'/>.less" type="text/css" rel="stylesheet/less"/>
  <c:url value="${img_path}/icons/gui/<s:property value='currentUserProfile.guiThemeShortname'/>" var="guiIconsUrl"/>	
 
  <!-- Custom styles for this template-->
  
  <link href="${webAppBaseUrl}/public/commons/style/css/mx-custom.css" rel="stylesheet">
 
<!-- Apply 'less' processing guitheme style -->
<script src="${less_javascript}" type="text/javascript"></script>

<style type="text/css" >
@charset "UTF-8";
</style>
