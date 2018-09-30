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
<body>


<!-- Main Menu Bar -->
<nav>
	<s:include value="/public/subparts/mainmenu.jsp" />
</nav>

		
<header>
<h1 class="negative" style="font-size:7rem;margin:0;text-align:center"><s:property value='username'/></h1>
</header>

<div id="wrapper">

	<div id="leftside"  class="menushadowcard">
		<h4 class="negative" ><s:text name="profile.additionalInfo" /></h4>
	</div>
	
	<div id="centerpanel" class=""   style="text-align:center">
		<center>
		  <table width="80%" class="">
			<tr><td id="profile.communities" class="cell"  >
			   <s:include value="/secure/subparts/profile.opencommunity.jsp" />
			</td></tr>
			<tr><td id="profile.details" class="cell"   >
			   <s:include value="/secure/subparts/profile.personnaldetails.jsp" />
			</td></tr>
			
		  </table>
		</center>	
	</div>
	
	<div id="rightside"  class="menushadowcard">
		<h4 class="negative" style="text-align:center;"><s:text name="profile.metaindexCommunities" /></h4>
		<div id="profile.choosecommunity" >
				<s:include value="/secure/subparts/profile.joincommunity.jsp" />
		</div>
	</div>
	
	
	<div id="footer">
		<s:include value="/public/subparts/footer.jsp" />
	</div>

</div>
  </body>
</html>  
