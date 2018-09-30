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
<s:include value="workzone/thumbnails/javascript.CatalogElementSummary.jsp" />
<s:include value="workzone/thumbnails/javascript.CatalogSummary.jsp" />

</head>
<body 	   	onload="document.addEventListener('dragstart', function (e) { e.preventDefault();}); 
					if (!window.WebSocket) { alert('ERROR: WebSockets are not supported in this browser. This feature is required to run Metadindex, sorry.'); }
					var catalogServerUri='ws://localhost:<s:property value="selectedCommunity.catalogServer.port" />';
					var elementDataServerUri='ws://localhost:<s:property value="selectedCommunity.elementDataServer.port" />';					
					connectCatalogContentsServer(catalogServerUri);	
					connectElementDataServer(elementDataServerUri);
					"
			onkeypress="event.stopPropagation();
						if (event.key=='t') { toggleThumbnailsHide(); }
						else if (event.key=='e') { toggleEditMode(); }"
			onkeydown="event.stopPropagation();
						if (event.key=='ArrowRight' || event.key=='ArrowDown' ) { changeToNextElement(); } 
						else if (event.key=='ArrowLeft' || event.key=='ArrowUp') { changeToPrevElement(); }
			"
			>


<s:include value="/secure/subparts/global.editablefield.html.jsp" />
<s:include value="/secure/subparts/global.booleanfield.html.jsp" />
<s:include value="/secure/subparts/global.enumeratedfield.html.jsp" />
<s:include value="/secure/subparts/global.colorfield.html.jsp" />

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
	     

			
<div id="wrapper"  >

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
		<s:include value="/secure/workzone/catalogs/workzone.catalogslist.jsp" />
				
	</div>
	
	<div id="centerpanel" class="elementpanel" >
		 	<s:include value="/secure/workzone/contents/workzone.contents.jsp" />
		 	<%-- s:include value="/secure/subparts/workzone.contents.jsp" /--%>
		 	<!-- artificial space for being able to scroll down  -->
		    <div style="height:200px"></div>
	</div>
	
	<div id="rightside"  class="flatzone">
		<!-- h4 class="negative" style="text-align:center;" ><s:text name="workzone.currentelement" /></h4-->
		<div id="workzone.elementdetails" >
			<s:include value="/secure/workzone/properties/workzone.properties.jsp" />
			<!--s:include value="/secure/subparts/workzone.elementdetails.jsp" /-->
			
		</div>
	</div>
	
</div>


<center>
	<div id="downside"  class="slideItemsSmallSizeContainer" >
		<s:include value="workzone/thumbnails/thumbnails.jsp" />				
	</div>
</center>



<div id="footer" >


<s:include value="/secure/subparts/chatroom.jsp" />

	<s:include value="/public/subparts/footer.jsp" />
</div>

<br/><br/><br/><br/><br/>
  </body>
</html>  
