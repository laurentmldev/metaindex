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

	var slideIndex = 0;
	function carousel() {
	  var i;
	  var x = document.getElementsByClassName("_slideshow_");
	  for (i = 0; i < x.length; i++) {
	    x[i].style.display = "none";	    
	  }
	  slideIndex++;
	  if (slideIndex > x.length) {slideIndex = 1}
	  x[slideIndex-1].style.display = "block";
	  setTimeout(carousel, 10000); 
	}

	function onload_actions() {
		if ("<s:property value="currentUserProfile.name" />".length!=0) {
			document.getElementById("language_buttons").style.display='none';	
		}
		carousel();
	}
	</script>
	 
</head>

<body class="mx-public-form" onload="onload_actions();" >

<c:if test="${mxDevMode == true}" >
  	<nav class="navbar navbar-expand topbar static-top"
  			style="background:orange;height:2rem;color:white;font-weight:bold;">
  	Dev-Mode Active
  	</nav>
  </c:if>
  
	 <nav class="navbar navbar-expand navbar-light topbar static-top mx_welcome_navbar" 
	 		style="">
	 	
	 		 	
            	<div id="language_buttons" style="position:fixed;margin-left:1rem;">
            		<table style="margin-left:1rem;width:10vw;"><tr>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
            		</tr></table>
            	</div>
          
        		<div style="padding:0;margin:0;width:60vw;text-align:center;line-height:1;position:fixed;margin-left:19%;">
			 		<span style="font-size:5vw;" class="app-title"  ><span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X</span></span>
			 		<div class="mx-welcome-globaltext"><s:text name="welcome.generalPresentation"/></div>            		            			
            	</div>	 	   			 	
	 			
	 	
	 	
	 	<div style="position:fixed;margin-left:85%;"><table>
		 	<tr><td><a href="contactform?origin=welcome"  
		 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale mx-welcome-navbar-btn " >
		 		<s:text name="contactform.title" /></a>	 
		 		</td></tr>
		 	<tr><td style="margin-top:5rem;">			 	 	
		 	<a href="loginform"  
		 	    class="mx-welcome-navbar-btn nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale " >
		 		<s:text name="signin.signin" /></a>
		 	</td></tr>
	 	</table></div>
	 			
	 </nav>
	 		
  <div class="container" style="width:100%;"  >
    
    <div class="row justify-content-center"  >
    	<center>
     	 	
			<div style="text-align:left;font-size:1rem;text-shadow:none;padding:0;margin:0;margin-top:3rem;font-weight:bold">
            	<ul class="app-title" style="text-align:left;font-size:1rem;text-shadow:none;padding-top:0;">
	            	<li style="list-style-type:'';margin-bottom:3rem;padding-bottom:0;text-align:center;"><center>
	            	<table style="width:90%"><tr>
	            	
	            	<td><center>
	            	
	            		<a href="Tutorials"  
					 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
					 		style="font-size:1.2rem;background:#99e;border-color:#ccc;color:white;margin-left:0.3rem;text-shadow:none;width:20vw"
					 		><s:text name="tutorials.welcometitle" /></a>
					 	</center></td>
					<td><center>
	            		<a href="signup?origin=welcome"  
					 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
					 		style="font-size:1.2rem;background:#8c8;border-color:#ccc;color:white;margin-left:0.3rem;text-shadow:none;width:20vw"
					 		><s:text name="signup.createYourAccount" /></a>
	            	</center></td>
	            	 	
					 	</tr></table>
	            	</center></li>
	            	 
	            	<li class="" style="width:90vw;list-style-type:'';" >
	            		<table><tr>
	            			<td style="width:45%">
	            				<center>
	            				<div >       	
       					        	<div class="_slideshow_" >
						        		<div class="mx-welcome-li mx-slideshow-title"><s:text name="welcome.features.injectdata.short" /></div>
						        		<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/cards1.png">
						        	</div>
						        	<div class="_slideshow_" >
						        		<div class="mx-welcome-li mx-slideshow-title"><s:text name="welcome.features.statistics.short" /></div>
						        		<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/stats.png">
						        	</div>
						        	<div class="_slideshow_" >
						        		<div class="mx-welcome-li mx-slideshow-title"><s:text name="welcome.features.statistics.mapsshort" /></div>
						        		<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/stats-map.png">
						        	</div>	        	
						        	<div class="_slideshow_" >
						        		<div class="mx-welcome-li mx-slideshow-title"><s:text name="welcome.features.teamWork.short" /></div>
						        		<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/collaborate.png">
						        	</div>	
						        	<div class="_slideshow_" >
						        		<div class="mx-welcome-li mx-slideshow-title"><s:text name="welcome.features.explore.short" /></div>
						        		<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/cards2.png">
						        	</div>
			
					        	</div>
					        	</center>
	            			</td>
	            			<td style="width:45%">
	            				<!-- center><div style="font-size:1.3rem;"><s:text name="welcome.exploreYourData" /></div></center-->
	            				<ul>
	            				<li class="mx-welcome-li" ><s:text name="welcome.features.injectdata" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.storeCorpus" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.explore" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.statistics" /></li>			
								<li class="mx-welcome-li" ><s:text name="welcome.features.graphs" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.teamWork" /></li>
	            				<li class="mx-welcome-li" ><s:text name="welcome.feature.workOffline" /></li>																
	            				</ul>
	            			
	            			</td>
	            		</tr></table>
	            	</li>
	            	
            		
				</ul>	
				</div>     
			  
			  	
			
        	
	   </center>
	  
	</div>
        <center>
 <footer class="sticky-footer" style="background:#eee;margin:0;padding:0.2rem;width:40vw;">
        <div class="container my-auto">
          <div class="mx-copyright text-center my-auto">
          <center>
          <table><tr><td><center>
            <span><b><s:text name="globals.copyright"/></b><br/>MetaindeX v<s:property value="mxVersion"/><br/><s:property value="mxFooterInfo"/></span>
          
          <center>
          </td><td>
		<a class="" href="https://secure.europeanssl.eu" target="_blank" style="">
           			<img src="https://secure.europeanssl.eu/seal/metaindex.fr/150" 
           			style="margin-left:3rem;" />
           			
		</a>	
		</td>
		<td style="text-align:center;min-width:10rem;">
		<span ><s:text name="welcome.requiredConfig" /></span>
		</td></tr></table>
		</center>
		</div>
        </div>
        <div  id="cookies-alert-div"
        	  class="container fixed-bottom mx-cookies-alert" 
        	   >
        	  <s:text name="global.cookiesAlert" /><br/>
        	  <a href="#"  
        	  	class="btn btn-sm btn-info mx_welcome_btn scale nav-item no-arrow" 
        	  	style="margin-left:5vw;background:green;border:none;margin-top:2rem;" 
        	  	onclick="this.parentNode.style.display='none';">
        	  	<s:text name="global.cookiesAlert.accept" /></a>
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
