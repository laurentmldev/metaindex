<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/loginprocess" var="loginUrl"/> 
<c:url value="/logoutprocess" var="logoutUrl"/> 
 
<!DOCTYPE html/>
<html>
<head>

	<s:include value="/public/subparts/headcontents.jsp" />
	<s:include value="/style/style_global.jsp" />
	<s:include value="/style/color_theme_silver.jsp" />
	


</head>
<body>
<!-- Main Menu Bar -->
<s:include value="/public/subparts/publicmainmenu.jsp" />

<br/><br/>
<center>
<h1 class="negative" style="font-size:7rem;margin:0">MetaIndex</h1>
<center>
<table class="" width="30%">
<tr><td  class="" >
<div id="loginpanel" class="editshadowcard" >
<br/>
<center><h3 class="positive" style="margin:0"><s:text name="session.welcome" /></h3>

<form action="${loginUrl}" method="post">
<c:if test="${param.error != null}"> <p>
   <s:text name="session.loginerror" />
  </p>
</c:if>
<c:if test="${param.logout != null}">
  <p>
   <s:text name="session.logoutmessage" />
</p>
</c:if>

<p>
<table>
	<tr><td><span class="fieldtitle"><s:text name="session.username" /></span></td><td><input type='text' name='username' value=''></td></tr>
	<tr><td><span class="fieldtitle"><s:text name="session.password" /></span></td><td><input type='password' name='password'/></td></tr>
	<tr><td colspan='2'><center><br/><input name="submit" type="submit" value="<s:text name="global.submit" />" 
		onclick="document.getElementById('loginpanel').className ='shadowcard fadeout'"/> 
	<a href="<c:url value="/createProfile" />"><s:text name="profile.create" /></a></center></td></tr>
	<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
</table>


</form>
</center>
</td></tr></table></center>
</div>
</center>
<s:include value="/public/subparts/footer.jsp" />
</body>
</html>
