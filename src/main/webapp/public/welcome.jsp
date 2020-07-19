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
	  setTimeout(carousel, 5000); // Change image every 3 seconds
	}
	
	</script>
</head>

<body class="" style="background-color:#f5f5f5;background-image:none;"
	onload="carousel();" >

	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top mx_welcome_navbar" 
	 		style="background:#aaa;height:5rem">
	 	
	 	
	 	
			 	<div class="app-title" style="font-size:4vw;padding:0;margin:0;width:auto;">
			 		<span class="scale-color-white">M</span><span class="app-title2 scale-color-white" style="color:white;">etainde</span><span class="scale-color-white">X</span>            		            			
            	</div>
            
          
        	 	   			 	
	 	<div style="width:20vw"></div>
	 	
	 	<a href="signup"  
	 		class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" style="background:green;border-color:white;margin-left:3rem;text-shadow:none;width:10vw"
	 		>Join us!</a>
	 		
	 	<div style="width:35vw"></div>	 	
	 	<a href="loginform"  class="btn btn-sm btn-info mx_welcome_btn scale nav-item no-arrow " style="background:#777" >Sign In</a>
	 			
	 </nav>
	 		
  <div class="container" style="width:100%;"  >
    
    <div class="row justify-content-center"  >
     	 	<div style="text-align:left;padding-left:10rem;font-size:1rem;text-shadow:none;padding-top:1rem;">						
				MetaindeX is an <b>OpenSource</b> Cataloging app thought for easy and powerful data management, especially for humanities studies and research
				</div>
           <div class="" style="padding:0;margin-top:2rem;font-size:2.2vw;text-align:center;font-weight:bold;">
			We're very excited to announce the very first official release!<br/>
			<span style="font-size:1.6rem;">
			We try to grow smoothly, so ask for your free account  
			<a href="mailto:laurentmlcontact-metaindex@yahoo.fr?subject=Join Metaindex&body=Hi, I'd like to become a MetaindeX User! Could create an account for me and explain a bit more how to use it ? Thanks! :)"  
	 		 style="color:#77d"
	 		>Here</a></span> 
			</div>
			<div style="text-align:left;padding-left:7rem;font-size:1.3rem;text-shadow:none;padding-top:0;font-weight:bold">
            	<ul class="app-title" style="text-align:left;padding-left:6rem;font-size:1.1rem;text-shadow:none;padding-top:0;">
            		<li style="list-style-type:'\2713';padding-left:1rem;">Inject data from Excel or OpenRefine</li>
					<li style="list-style-type:'\2713';padding-left:1rem;">Work offline thanks to our powerful CSV import/export module</li>
					<li style="list-style-type:'\2713';padding-left:1rem;">Store your corpus associated files (images, documents,...)</li>
					<li style="list-style-type:'\2713';padding-left:1rem;">Explore and filter with a fast and powerful query language</li>
					<li style="list-style-type:'\2713';padding-left:1rem;">Build great statistical charts and find correlations</li>
					<li style="list-style-type:'\2713';padding-left:1rem;">Export to Gephi and explore data connections and graphs</li>
					<li style="list-style-type:'\2713';padding-left:1rem;">Think as a team and work together on the same contents</li>
				</ul>	
				</div>       
			<div style="margin-top:1rem;margin-bottom:3rem;">   <center>     	
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/stats.png">	        	
	        	<img class="mx_welcome_screenshot w3-animate-opacity" src="public/commons/media/img/screenshots/cards1.png">				
        	</center></div>
        	
	
	  
	</div>
        	

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
