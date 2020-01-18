<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url value="logoutprocess" var="logoutUrl"/> 
<c:url value="/communityLang" var="communityLangUrl"/>

<!DOCTYPE html/>
<html>
<head>
	<s:include value="/public/subparts/headcontents.jsp" />
	<s:include value="/style/style_global.jsp" />
	<s:include value="/style/color_theme_%{#session['selected_guitheme']}.jsp" />

<script type="text/javascript" src="public/deps/prototype.js"></script>
<s:include value="workzone/elementslist/javascript.CatalogElementSummary.jsp" />
<s:include value="workzone/elementslist/javascript.CatalogSummary.jsp" />

</head>
<body 	   	onload="document.addEventListener('dragstart', function (e) { e.preventDefault();}); 
					if (!window.WebSocket) { alert('ERROR: WebSocket not natively supported!'); }  
					connectCatalogContentsServer(catalogserverUri);					
					"
			onkeypress="event.stopPropagation();"	>



<!-- Main Menu Bar -->
<nav>
	<s:include value="/public/subparts/mainmenu.jsp" />
</nav>

		
<header>

</header>

<s:include value="/secure/workzone/catalogs/uploadcatalog.modal.jsp" />

		<form id="workzone.downloadCatalog.form" action="<c:url value="/downloadCatalogProcess" />" method="post" >
	    			<input type="hidden"  name="${_csrf.parameterName}" value="${_csrf.token}"/>
	     </form>
	     

			
<div id="wrapper" >

	<div id="leftside"  class="menushadowcard">
		
		<fieldset><legend><span class="fieldsetTitle" ><s:property value='selectedCommunity.idName'/></span></legend>
			<table>
				<tr><td><a href="${communityLangUrl}" class="listitem"><s:text name="global.managecommunity"/></a></td></tr>
				<tr><td>
					<a href="#" class="listitem" onclick="document.getElementById('workzone.downloadCatalog.form').submit()" >
						<span class="bigchoice"><s:text name="workzone.importcsv"/></span>
					</a>
				</td></tr>
				<tr><td>
				  <a id="workzone.uploadCatalog.link" href="#" class="listitem" 
				  		onclick="document.getElementById('fileLoad').clear();document.getElementById('workzone.uploadCatalog.modal').style.display='table';" >
				  		<span class="bigchoice"><s:text name="workzone.uploadcatalog"/></span>
				  </a>
				  
				</td></tr>
			</table>
		</fieldset>
		<s:include value="/secure/subparts/workzone.catalogslist.jsp" />
				
	</div>
	
	<div id="centerpanel" class="elementpanel">
		 	<s:include value="/secure/subparts/workzone.elementcontents.jsp" />
		 	<!-- artificial space for being able to scroll down  -->
		    <div style="height:200px"></div>
	</div>
	
	<div id="rightside"  class="flatzone">
		<!-- h4 class="negative" style="text-align:center;" ><s:text name="workzone.currentelement" /></h4-->
		<div id="workzone.elementdetails" >
			<s:include value="/secure/subparts/workzone.elementdetails.jsp" />
		</div>
	</div>
	
</div>


<center>
	<div id="downside"  class="slideItemsSmallSizeContainer" >
		<s:include value="workzone/elementslist/catalogcontents.jsp" />				
	</div>
</center>



<div id="footer" >


<s:include value="/secure/subparts/chatroom.jsp" />

	<s:include value="/public/subparts/footer.jsp" />
</div>

<br/><br/><br/><br/><br/>
  </body>
</html>  
