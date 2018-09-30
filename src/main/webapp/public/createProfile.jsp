<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>   
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
<div class="editshadowcard">
<h2 class="positive" style="margin:0"><s:text name="createProfile.title" /></h2>
<br/>
<form action="<c:url value="/CreateProfileProcess" />" method="post" >
<c:if test="${param.error != null}"> <p>
   <s:text name="createProfile.error" />
  
  </p>
</c:if>
<table>
 <s:iterator value="guiErrorMsgs">
   	 <tr><td>Msg : <s:property /></td></tr>
	</s:iterator>
	</table>
<s:fielderror fieldName="username" />
<s:fielderror fieldName="email" />
<s:fielderror fieldName="password" />
<s:fielderror fieldName="acceptConditions" />
<table>
	<tr><td><s:text name="profile.username" /></td><td>
			<input type='text' name='username' value="${username}" 
				<s:if test="fieldErrors.containsKey('username')">class="errorInput blink"</s:if>/>
	</td></tr>
		
	<tr><td>
		<s:text name="profile.email" /></td><td>
		<input type='email' name='email' value="${email}"
				<s:if test="fieldErrors.containsKey('username')">class="errorInput blink"</s:if>/>
	</td></tr>
	
	<tr><td>
		<s:text name="profile.password" /></td><td>
		<input type='password' name='password'
			<s:if test="fieldErrors.containsKey('password')">class="errorInput blink"</s:if>/>
	</td></tr>
	
	<tr><td>
		<s:text name="profile.language" /></td><td>
		<s:select name="guiLanguageId" list="guiLanguages" listKey="id" listValue="name" headerKey="1" value="%{guiLanguageId}" />
	</td></tr>
	<tr><td>
		<s:text name="profile.theme" /></td><td>
		<s:select name="guiThemeId" list="guiThemes" listKey="id" listValue="name" headerKey="1" value="%{guiThemeId}"/>
	</td></tr>
	
	<tr><td>
		<s:text name="profile.acceptConditions" /></td><td>
		<s:if test="fieldErrors.containsKey('acceptConditions')"><span class="errorInput blink"></s:if> 
			<input type='checkbox' name="acceptConditions" />
		<s:if test="fieldErrors.containsKey('acceptConditions')"></span></s:if>
		
	</td></tr>
	
	<tr><td colspan='2'>
		<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<center><br/><input name="submit" type="submit" value="<s:text name="global.submit" />"/></center>
	</td></tr>
	
</table>

</form>
	
</div>
</center>
<s:include value="/public/subparts/footer.jsp" />
</body>
</html>
