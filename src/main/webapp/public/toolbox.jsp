<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  

<!DOCTYPE html>
<html lang="en">

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="MetaindeX - Opensource Cataloger App">
  <meta name="author" content="Editions du Tilleul - Laurent ML">
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">

  
  <title>MetaindeX</title>
  <link rel="stylesheet" href="https://www.w3schools.com/w3css/4/w3.css">
  <link rel="icon" type="image/svg" href="${webAppBaseUrl}/public/commons/media/img/favicon.png">
 
  <!-- Custom fonts for this template-->
  <link href="${webAppBaseUrl}/public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 
  <s:include value="/public/commons/js/helpers.jsp" />
  
  
  <script>
  
  var origin="welcome";
  if ("${param.origin}"!="") { origin="${param.origin}"; }
  
  function onload_actions() {
		if ("<s:property value="currentUserProfile.name" />".length!=0) {
			document.getElementById("language_buttons").style.display='none';	
		}
	}
  
  function goBack() { window.location.href=origin; }
  </script>
</head>

<body class="mx-public-form" onload="onload_actions()" >

<c:if test="${mxDevMode == true}" >
  	<nav class="navbar navbar-expand topbar static-top"
  			style="background:orange;height:2rem;color:white;font-weight:bold;">
  	Dev-Mode Active
  	</nav>
  </c:if>
  
	 
	 
	 
	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top mx_welcome_navbar" 
	 		style="background:#aaa;height:5rem">
	 			<div id="language_buttons">
            		<table style="margin-left:1rem;width:10vw;"><tr>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
            		</tr></table>
            	</div>
				<div class="app-title" style="font-size:4vw;padding:0;margin:0;width:100%;text-align:center;">
			 		<span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X 
			 		<s:text name="Toolbox.title" /></span>           		            			
            	</div>	
            	<a href="#" class="btn btn-primary btn-user btn-block scale" 
	        		style="max-width:10%;height:3rem;background:#999;border:none;padding:0;padding:0;padding-top:0.8rem;" 
		  	onclick="goBack();">
	                   <s:text name="globals.goback" />
	                 </a>  			 	
		 			
	 </nav>
	 		
  <div class="container" style="max-width:95vw;margin-top:1rem;"  >
  	<center>

		 <table class="table table-striped">						    
		    <tbody>
		     <tr style="text-align:center">
		        <th  style="font-style:italic"><s:text name="Toolbox.toolName" /></td>
		        <th  style="font-style:italic"><s:text name="Toolbox.toolVersion" /></td>
		        <th  style="font-style:italic"><s:text name="Toolbox.toolDesc" /></td>
		        <th  style="font-style:italic"><s:text name="Toolbox.toolFiles" /></td>
		        <th  style="font-style:italic"><s:text name="Toolbox.toolDeps" /></td>
		        						        
		      </tr>
		    <c:forEach items="${toolsDesc}" var="toolDesc">
		      <tr>
		        <td style="font-weight:bold">${toolDesc.name}</td>
		        <td>${toolDesc.version}</td>
		        <td>${toolDesc.description[currentLanguage]}</td>
		        <td>
			        <ul>
			        	<c:forEach items="${toolDesc.filesUrl}" var="fileUrl" varStatus="loop">
			        		
			        		<li><a style="font-size:0.9rem" href="${fileUrl}">${toolDesc.filesName[loop.index]}</a></li>
			        	</c:forEach>
		        	</ul>
		        </td>
		        <td style="font-size:0.8rem">${toolDesc.dependencies}</td>		        
		      </tr>						      		    			
			</c:forEach>
		    
		      </tbody>
		      </table>
		
	</center>
</div>
        <center>
        <h1>${sessionLanguage}</h1>
<footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="mx-copyright text-center my-auto">
            <span><b><s:text name="globals.copyright"/></b> - MetaindeX v<s:property value="mxVersion"/><br/><s:property value="mxFooterInfo"/></span>
          </div>
          <center>
        <div>
		<a class="" href="https://secure.europeanssl.eu" target="_blank" style="">
           			<img src="https://secure.europeanssl.eu/seal/metaindex.fr/150" 
           			style="margin-top:1rem" />
           			
		</a>	
		</div>
		</center>
        </div>
       
       </footer>
   </center>
      
	 
  <!-- Bootstrap core JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${webAppBaseUrl}/public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${webAppBaseUrl}/public/commons/js/sb-admin-2.min.js"></script>



</body>

</html>
