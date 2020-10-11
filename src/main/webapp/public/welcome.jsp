<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/struts-tags" prefix="s" %>  
<c:url value="/" var="mxurl"/>
<c:url value="/loginprocess" var="loginUrl"/>

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
  <link rel="icon" type="image/svg" href="public/commons/media/img/favicon.png">
  
  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
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

<body class="" style="background-color:#f5f5f5;background-image:none;"
	onload="carousel();" >

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
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'en');"><img src="${mxurl}public/commons/media/img/flags/UK.png" class="mx-lang-flag scale" /></a></td>
            		<td><a onclick="window.location.href=URL_add_parameter(location.href, 'language', 'fr');"><img src="${mxurl}public/commons/media/img/flags/France.png" class="mx-lang-flag scale"/></a></td>
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
     	 	<div class="app-title" style="text-align:center;font-weight:bold;padding-bottom:1rem;font-size:2rem;text-shadow:none;">						
				<s:text name="welcome.generalPresentation" />
				</div>
           
			<div style="text-align:left;font-size:1rem;text-shadow:none;padding:0;margin:0;font-weight:bold">
            	<ul class="app-title" style="text-align:left;padding-left:6rem;font-size:1rem;text-shadow:none;padding-top:0;">
	            	<li style="list-style-type:'';margin-bottom:1rem;padding-bottom:0;text-align:center;">
	            		<a href="signup?origin=welcome"  
					 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" 
					 		style="font-size:1.2rem;background:#6c6;border-color:white;margin-left:0.3rem;text-shadow:none;width:20vw"
					 		><s:text name="signup.createYourAccount" /></a>
	            	</li>
            		<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.features.injectdata" /></li>
					<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.feature.workOffline" /></li>
					<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.features.storeCorpus" /></li>
					<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.features.explore" /></li>
					<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.features.statistics" /></li>			
					<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.features.graphs" /></li>
					<li style="list-style-type:'\2713';padding-left:1rem;"><s:text name="welcome.features.teamWork" /></li>
				</ul>	
				</div>     
			  
			  	
			<div style="margin-bottom:3rem;">       	
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/stats.png">	        	
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/cards1.png">				
        	</div>
        	
	   </center>
	  
	</div>
        	
<%-- 
        <div style="padding-left:6rem;padding-right:6rem;" ><center>
    		 	
  			<div id="features-list"  >  
   				<div class="card mb-3 scale-bgcolor-white" style="border:none" >    					
				    <div class="card-body " >
				      <p class="card-text mx_welcome_card_title text-center scale-color-blue">Organize, Control and Manage your Data</p>
				      <div id="control.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      
					      	<li class="fa-li fa fa-check scale-color-blue">
						      	<span class="mx_welcome_card_li_contents" >Access and edit contents online<br/>				      	
						      		<span class="_details_ mx_welcome_card_details"  >Access your work from anywhere - edit, update, modify ...</span>
						      	</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Exchange contents with third-party tools<br/>
					      				<span class="_details_ mx_welcome_card_details" >Synchronize your work or third-party tools (Excel, <a class="mx_welcome_card_body_link" target="_blank"  href="http://openrefine.org">OpenRefine</a>,...) using advanced CSV injection utilities</span>
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Download contents on your computer, work off-line and re-sync<br/>
					      				<span class="_details_ mx_welcome_card_details" >Retrieve data as standard CSV, store, share and update at will</span>
					      			</span>
					      	</li>					      	
					      	<li class="fa-li fa fa-check scale-color-blue">
					      		<span class="mx_welcome_card_li_contents">
					      			 Customize visualization <br/>
					      			<span class="_details_ mx_welcome_card_details" >Decide what and how you want to display your data</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Store all your files online<br/>
					    				<span class="_details_ mx_welcome_card_details" >Store images, text, or any type of data related to your project and reference it from the documents</span>
					    			</span>
					    	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Link documents with each other<br/>
					      				<span class="_details_ mx_welcome_card_details" >Link documents with each other to build connections</span>
					      			</span>
					      	</li>	
					      	<li class="fa-li fa fa-check scale-color-blue">
						      	<span class="mx_welcome_card_li_contents" >Define data model<br/>				      	
						      		<span class="_details_ mx_welcome_card_details"  >Define the fields your documents contain, including enumerations for easy editing</span>
						      	</span>
					      	</li>				      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			    <div >			  	
			  	 <div class="card mb-3 scale-bgcolor-white"  style="border:none">
				    <div class="card-body " >
				      <p class="card-text mx_welcome_card_title text-center scale-color-blue">Explore and Discover</p>
				      <div id="explore.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					        <li class="fa-li fa fa-check scale-color-blue" >
					      			<span class="mx_welcome_card_li_contents" >Build Powerful Search Queries<br/>
					      				<span class="_details_ mx_welcome_card_details"  >Experience the powerful <a class="mx_welcome_card_body_link" target="_blank"  href="http://lucene.org">Lucene</a> Search Queries, and make very efficient and precise search queries</span>	
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
			      				<span class="mx_welcome_card_li_contents">Generate great statistic charts in few clicks<br/>
			      					<span class="_details_ mx_welcome_card_details"  >Full integration with industry leading <a class="mx_welcome_card_body_link" target="_blank"  href="http://elk.org">Kibana</a> for generation of statistical charts</span>
			      				</span>
			      			</li>			      			
					      	<li class="fa-li fa fa-check scale-color-blue" >
			      				<span class="mx_welcome_card_li_contents">Export data for graph analysis<br/>
			      					<span class="_details_ mx_welcome_card_details"  >Generate GEXF file and load it with <a class="mx_welcome_card_body_link" target="_blank"  href="http://gephi.org">Gephi</a> graph analyzer</span>
			      				</span>
			      			</li>			      			
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      			<span class="mx_welcome_card_li_contents">Amazingly fast search engine<br/>
					      				<span class="_details_ mx_welcome_card_details"  >Lightning-fast search with the <a class="mx_welcome_card_body_link" target="_blank" href="http://elk.org">Elastic-Search</a> engine which is used worldwide</span>
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      			<span class="mx_welcome_card_li_contents">Save and combine queries<br/>
					      				<span class="_details_ mx_welcome_card_details"  >Keep track of useful queries so you can reused or combined them</span>
					      			</span>
					      	</li>

			      			<li class="fa-li fa fa-check scale-color-blue" >
			      				<span class="mx_welcome_card_li_contents">Integrate charts into your workspace<br/>
			      					<span class="_details_ mx_welcome_card_details"  >Access to updated dynamic statistic charts from main application</span>
			      				</span>
			      			</li>			      			
			      		</ul>
				      </div>
				    </div>
				  </div>
			  </div>
  
			  <div >  
   				<div class="card mb-3 scale-bgcolor-white"  style="border:none">    					
				    <div class="card-body ">
				      <p class="card-text mx_welcome_card_title text-center scale-color-blue">Collaborate with Partners and Share Your Work</p>
				      <div id="collab.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      	<li class="fa-li fa fa-check scale-color-blue" >
				      			<span class="mx_welcome_card_li_contents">Work together with your team or partners<br/>
				      				<span class="_details_ mx_welcome_card_details"  >Work directly together on the same contents and avoid painful data merging</span>
				      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      		<span class="mx_welcome_card_li_contents">Control who can see, edit or delete data<br/>
					      			<span class="_details_ mx_welcome_card_details"  >Assign roles and access rights to each of your partners</span>
					      		</span>
					      	</li>					      						      	
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      		<span class="mx_welcome_card_li_contents">Multi-languages customization (English,French, ...)<br/>
					      			<span class="_details_ mx_welcome_card_details"  >Define lexic for a user interface adapted to your specific domain and partners languages</span>
					      		</span>
				      		</li>	
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      		<span class="mx_welcome_card_li_contents">Get notified when data has changed<br/>
					      			<span class="_details_ mx_welcome_card_details"  >Get live updates on the data that has been modified</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      		<span class="mx_welcome_card_li_contents">See who changed the documents<br/>
					      			<span class="_details_ mx_welcome_card_details"  >Find when a document has been changed lastly and by who</span>
					      		</span>
					      	</li>
					      					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			   
	  </div>
	  	
	  	<div style="font-size:0.8rem;padding:1rem;" >
	  	Special thanks to <i>Elastic Stack Team</i> for their great products, and also, for advices and support, to
	  		<i>Jakob H</i>.
	  	</div>
	  </center></div>
	  
	  --%>
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
        	  style="" >
        	  <s:text name="global.cookiesAlert" />
        	  <a href="#"  
        	  	class="btn btn-sm btn-info mx_welcome_btn scale nav-item no-arrow" 
        	  	style="margin-left:5vw;background:green;border:none" 
        	  	onclick="this.parentNode.style.display='none';">
        	  	OK</a>
        </div>
         
  </footer>
   
      
	 
  <!-- Bootstrap core JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${mxurl}public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${mxurl}public/commons/js/sb-admin-2.min.js"></script>

</body>

</html>
