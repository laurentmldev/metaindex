<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="logoutprocess" var="logoutUrl"/> 


<!DOCTYPE html/>
<html>
<head>
	<s:include value="/public/subparts/headcontents.jsp" />
	<s:include value="/style/style_global.jsp" />
	<s:include value="/style/color_theme_%{#session['selected_guitheme']}.jsp" />

</head>
<body	>


<!-- Main Menu Bar -->
<nav>
	<s:include value="/public/subparts/mainmenu.jsp" />
</nav>

		
<header>

</header>

<div id="wrapper" >

	<div id="leftside"  class="menushadowcard">
		<h4 class="negative"  ><s:property value='selectedCommunity.idName'/><br/><s:text name="community.community" /></h4>
		<div>
		<s:include value="/secure/subparts/community.details.jsp" />
		<br/>
		<s:include value="/secure/subparts/community.managemenu.jsp" />		</div>
	</div>
	
	
	<div id="centerpanel" class="elementpanel" >		
	 	<center><s:include value="/secure/subparts/community.lang.termsAndvoc.jsp" /></center>
		 	<!-- artificial space for being able to scroll down  -->
		    <div style="height:200px"></div>
	</div>
	
	<!-- div id="rightside"  class="menushadowcard">
		
	</div-->
	
</div>
	

<div id="footer" >
	<s:include value="/public/subparts/footer.jsp" />
</div>

  </body>
</html>  
