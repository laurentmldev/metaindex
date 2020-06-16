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
	  setTimeout(carousel, 3500); // Change image every 2 seconds
	}
	
	</script>
</head>

<body class="bg-gradient-primary" style="background-color:#fff;background-image:none;"
	onload="carousel();" >

	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top " style="margin:0;padding:0;margin-top:2rem;">
	 	
	 	
	 	
		<style type="text/css">a.eussl_seal:hover .eussl_bubble { visibility:visible; } a.eussl_seal .eussl_bubble { visibility:hidden; width:145.5px; height:200px; margin-left:3.75px; border:1px solid #6c81b3; border-radius:0 0 3px 3px; overflow:hidden; border-top:0; }</style>	 		 	
	 	<a class="eussl_seal eussltip" href="https://secure.europeanssl.eu" target="_blank" style="padding-top:10rem;">
           			<img src="https://secure.europeanssl.eu/seal/metaindex.fr/150" alt="Secured by EuropeanSSL.eu" />
           			<br />
           			<iframe class="eussl_bubble" src="https://secure.europeanssl.eu/sealdetails/metaindex.fr/0.75"></iframe>
		</a>
        <div style="width:70vw"></div>	 	   			 	
	 		 	
	 	
	 	<a href="mailto:laurentmlcontact-metaindex@yahoo.fr?subject=Join Metaindex&body=Hi, I'd like to become a MetaindeX Beta-User! Could create an account for me and explain a bit more how to use it ? Thanks! :)"  
	 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" style="background:green;border-color:white;margin-left:3rem;text-shadow:none"
	 		>Join us! <br/>Become a <span style="white-space:nowrap;">Beta-User!</span></a>	 	
	 	<a href="loginform"  class="btn btn-sm btn-info mx_welcome_btn scale nav-item no-arrow " style="background:#777" >Sign In</a>
	 			
	 </nav>
	
		 		
  <div class="container" style="width:100%;"  >
    
    <div class="row justify-content-center"  >
     	 
           	<div class="app-title" style="padding:0;margin:0;width:100%;margin-bottom:1rem;">
           		
           		
           		<img src="public/commons/media/img/mx-title.png" style="max-width:40vw;height: auto;" />
           		<!--span class="scale-color-white">M</span><span class="app-title2 scale-color-white" >etainde</span><span class="scale-color-white">X</span-->
           	
            		<div class="app-title" style="padding:0;margin:0;font-size:1.6vw;text-shadow:none">
            			Opensource Data Cataloger           			
	 				</div>
	 							            			
            </div>

			<div class="" style="padding:0;margin:0;font-size:1.3vw;text-align:center;">            				
				<b style="color:green">Save time and get more from your data.</b>       
				<div style="font-size:1rem">
				Especially suited for researchers on history, history of art, social studies, archeology, 
            			or any field implying work on heavy data corpus.
				</div>       			
	 		</div>
	
	  
	</div>
        	<div style="margin-top:2rem;">   <center>     	
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/stats.png">	        	
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/editmode2.png">
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/cards1.png">				
        	</center></div>

        <div style="padding-left:6rem;padding-right:6rem;" ><center>
        
         	
  			<div id="features-list"  >  
   				<div class="card mb-3 scale-bgcolor-white" style="border:none" >    					
				    <div class="card-body " >
				      <p class="card-text mx_welcome_card_title text-center scale-color-blue">Organize, Control and Manage your Data</p>
				      <div id="control.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      
					      	<li class="fa-li fa fa-check scale-color-blue">
						      	<span class="mx_welcome_card_li_contents" >Access and Edit Contents Online<br/>				      	
						      		<span class="_details_ mx_welcome_card_details"  >Access from anywhere your work, edit, update, modify ...</span>
						      	</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Upload contents from Excel or third-party database<br/>
					      				<span class="_details_ mx_welcome_card_details" >Synchronize your work or third-party DB using advanced CSV injection utility</span>
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
					      			<span class="_details_ mx_welcome_card_details" >Decide what and how to display your data</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Store on-line all associated files<br/>
					    				<span class="_details_ mx_welcome_card_details" >Store images, text, or any type of data related to your project and reference it from the documents</span>
					    			</span>
					    	</li>
					      	<li class="fa-li fa fa-check scale-color-blue">
					      			<span class="mx_welcome_card_li_contents">Links documents with each other<br/>
					      				<span class="_details_ mx_welcome_card_details" >Reference documents from each other to build connections</span>
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
					      				<span class="_details_ mx_welcome_card_details"  >Experience powerful <a class="mx_welcome_card_body_link" href="http://lucene.org">Lucene</a> Search Queries, and make very efficient and precise search queries</span>	
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
			      				<span class="mx_welcome_card_li_contents">Generate great statistic charts in few clicks<br/>
			      					<span class="_details_ mx_welcome_card_details"  >Full integration with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Kibana</a> for generation of statistics charts</span>
			      				</span>
			      			</li>			      			
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      			<span class="mx_welcome_card_li_contents">Amazingly fast search engine<br/>
					      				<span class="_details_ mx_welcome_card_details"  >Lightning-fast search with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Elastic-Search</a> engine</span>
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      			<span class="mx_welcome_card_li_contents">Save and combine queries<br/>
					      				<span class="_details_ mx_welcome_card_details"  >Keep track of useful queries to be reused or combined</span>
					      			</span>
					      	</li>

			      			<li class="fa-li fa fa-check scale-color-blue" >
			      				<span class="mx_welcome_card_li_contents">Integrate charts into your workspace<br/>
			      					<span class="_details_ mx_welcome_card_details"  >Access to updated dynamic statistic charts from main application</span>
			      				</span>
			      			</li>
			      			<li class="fa-li fa fa-check scale-color-blue" >
			      				<span class="mx_welcome_card_li_contents">Connection graphs with auto-generated VEGA graphs (beta)<br/>
			      					<span class="_details_ mx_welcome_card_details"  >Control great power of VEGA-graph with dedicated pre-defined templates</span>
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
					      			<span class="_details_ mx_welcome_card_details"  >Get lively updated with what has actually been modified in your data</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" >
					      		<span class="mx_welcome_card_li_contents">Trace who changed what and when<br/>
					      			<span class="_details_ mx_welcome_card_details"  >Find when a document has been changed lastly and by who</span>
					      		</span>
					      	</li>
					      					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			   
	  </div>
	  	
	  </center></div>
 <footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="mx-copyright text-center my-auto">
            <span><s:text name="globals.copyright"/> - MetaindeX v<s:property value="mxVersion"/></span>
          </div>
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
