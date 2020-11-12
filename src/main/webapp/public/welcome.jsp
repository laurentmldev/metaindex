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
	carousel();

	function carousel() {
	  var i;
	  var x = document.getElementsByClassName("mx_welcome_screenshot");
	  for (i = 0; i < x.length; i++) {
	    x[i].style.display = "none";	    
	  }
	  slideIndex++;
	  if (slideIndex > x.length) {slideIndex = 1}
	  x[slideIndex-1].style.display = "block";
	  setTimeout(carousel, 5000); // Change image every 3 seconds
	}

	</script>
	 
</head>

<body class="mx-public-form" onload="carousel();" >

<c:if test="${mxDevMode == true}" >
  	<nav class="navbar navbar-expand topbar static-top"
  			style="background:orange;height:2rem;color:white;font-weight:bold;">
  	Dev-Mode Active
  	</nav>
  </c:if>
  
	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top mx_welcome_navbar" 
	 		style="background:#aaa;height:5rem">
	 	
	 		 	
            	<div>
            		<table style="margin-left:1rem;width:10vw;"><tr>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${webAppBaseUrl}/public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
            		</tr></table>
            	</div>
          
        		<div class="app-title" style="font-size:4vw;padding:0;margin:0;width:60vw;text-align:center;">
			 		<span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X</span>            		            			
            	</div>	 	   			 	
	 	
	 	
	 	
	 	<div><table><tr>
		 	<td><a href="contactform?origin=welcome"  
		 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
		 		style="background:#bbb;border-color:white;text-shadow:none;width:10vw"
		 		><s:text name="contactform.title" /></a>	 
		 		</td><td>			 	 	
		 	<a href="loginform"  class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
		 		style="background:#bbb;border-color:white;margin-left:0.3rem;text-shadow:none;width:10vw" >
		 		<s:text name="signin.signin" /></a>
		 	</td>
	 	</tr></table></div>
	 			
	 </nav>
	 		
  <div class="container" style="width:100%;"  >
    
    <div class="row justify-content-center"  >
    	<center>
     	 	
			<div style="text-align:left;font-size:1rem;text-shadow:none;padding:0;margin:0;margi-top:1rem;font-weight:bold">
            	<ul class="app-title" style="text-align:left;font-size:1rem;text-shadow:none;padding-top:0;">
	            	<li style="list-style-type:'';margin-bottom:1rem;padding-bottom:0;text-align:center;"><center>
	            	<table><tr><td>
	            		<a href="signup?origin=welcome"  
					 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
					 		style="font-size:1.2rem;background:#6c6;border-color:white;margin-left:0.3rem;text-shadow:none;width:20vw"
					 		><s:text name="signup.createYourAccount" /></a>
	            	</td><td>
	            	
	            		<a href="Tutorials"  
					 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
					 		style="font-size:1.2rem;background:#66c;border-color:white;margin-left:0.3rem;text-shadow:none;width:20vw"
					 		><s:text name="tutorials.welcometitle" /></a>
					 	</td></tr></table>
	            	</center></li>
	            	 
	            	<li class="" style="margin-top:2rem;width:90vw;list-style-type:'';" >
	            		<table><tr>
	            			<td style="width:45%">
	            				<div >       	
						        	<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/stats.png">	        	
						        	<img class="mx_welcome_screenshot w3-animate-opacity" src="${webAppBaseUrl}/public/commons/media/img/screenshots/cards1.png">				
					        	</div>
	            			</td>
	            			<td style="width:45%">
	            				<center><div style="font-size:1.3rem;"><s:text name="welcome.generalPresentation" /></div></center>
	            				<ul>
	            				<li class="mx-welcome-li" ><s:text name="welcome.features.teamWork" /></li>
	            				<li class="mx-welcome-li" ><s:text name="welcome.features.injectdata" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.feature.workOffline" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.storeCorpus" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.explore" /></li>
								<li class="mx-welcome-li" ><s:text name="welcome.features.statistics" /></li>			
								<li class="mx-welcome-li" ><s:text name="welcome.features.graphs" /></li>								
	            				</ul>
	            			
	            			</td>
	            		</tr></table>
	            	</li>
	            	
            		
				</ul>	
				</div>     
			  
			  	
			
        	
	   </center>
	  
	</div>
        
 <footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="mx-copyright text-center my-auto">
            <span><b><s:text name="globals.copyright"/></b> - MetaindeX v<s:property value="mxVersion"/></span>
          </div>
          <center><div>
		<a class="" href="https://secure.europeanssl.eu" target="_blank" style="">
           			<img src="https://secure.europeanssl.eu/seal/metaindex.fr/150" 
           			style="margin-top:1rem" />
           			
		</a>	
		</div></center>
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
   
      
	 
  <!-- Bootstrap core JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${webAppBaseUrl}/public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${webAppBaseUrl}/public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${webAppBaseUrl}/public/commons/js/sb-admin-2.min.js"></script>



</body>

</html>
