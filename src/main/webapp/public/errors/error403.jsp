<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>   
<c:url value="root" var="rootUrl"/> 
<!DOCTYPE html/>
<html>
<head>
	<s:include value="/public/subparts/headcontents.jsp" />
	<s:include value="/style/color_theme_silver.jsp" />
	<s:include value="/style/style_global.jsp" />
</head>
<body onLoad="mainmenu_switch('');">
<!-- Main Menu Bar -->
<s:include value="/public/subparts/publicmainmenu.jsp" />
<br/><br/>
<center>
<h1 class="negative" style="font-size:7rem;margin:0">MetaIndex</h1>
<br/><br/>
<div class="shadowcard">
<h2 class="positive" style="margin:0"><s:text name="error.sessionexpired" /></h2>
<a href="${rootUrl}" ><s:text name="error.backtologin"/></a> 
<br/>


	
</div>
</center>
<s:include value="/public/subparts/footer.jsp" />
</body>
</html>
