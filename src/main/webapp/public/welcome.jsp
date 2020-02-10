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
  <meta name="description" content="">
  <meta name="author" content="">

  
  <title>MetaindeX - Welcome</title>
  <link rel="icon" type="image/svg" href="public/commons/media/img/favicon.png">
  
  <!-- Custom fonts for this template-->
  <link href="${mxurl}public/commons/deps/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">

  <!-- Custom styles for this template-->
  <s:include value="/public/commons/style/style_global.jsp" />  							 

	<script type="text/javascript">

		function clearDetails() {
			let details = document.querySelectorAll(".mx_welcome_card_details");
			for (var i = 0; i < details.length; i++) {
				let detailNode=details[i];
				detailNode.style.display='none';
			}
		}
	</script>
</head>

<body class="bg-gradient-primary" style="background-color:#fff;background-image:none;" >

	 <nav class="navbar navbar-expand navbar-light bg-white topbar static-top " style="margin:0;padding:0;margin-top:1rem;">
	 	<div class="app-title mx-copyright" style="text-align:left;width:25vw; border:1px solid grey;padding-left:1rem;padding-top:0.5rem;padding-bottom:0.5rem;"  ><s:text name="globals.copyright"/></div>
	 	<div style="width:60vw"></div>	 	
	 	<a href="/demo"  class="nav-item no-arrow btn btn-sm btn-info mx_welcome_btn scale" style="background:green;border-color:white;margin-left:3rem;">Try Live Demo Now!</a>
	 	<a href="loginform"  class="btn btn-sm btn-info mx_welcome_btn scale nav-item no-arrow " style="background:#aaa" >Sign Up</a>
	 	<a href="loginform"  class="btn btn-sm btn-info mx_welcome_btn scale nav-item no-arrow " style="background:#777" >Sign In</a>
	 			
	 </nav>
	<!--img src="public/commons/media/img/mx-gui-capture.jpg" class="mx_welcome_capture_img" /-->
		 		
  <div class="container" style="width:100%;"  >
    
    <div class="row justify-content-center"  >
     	 
           	<div class="app-title" style="font-size:7vw;padding:0;margin:0;width:100%"><span class="scale-color-white">M</span><span class="app-title2 scale-color-white" >etainde</span><span class="scale-color-white">X</span>
            		<div class="app-title" style="padding:0;margin:0;margin-bottom:2rem;font-size:2vw;text-shadow:none">
            			The OpenSource Data Toolbox             			
	 				</div>
	 				            			
            </div>
        

        <div style="padding-left:6rem;padding-right:6rem;" ><center>
         <div class="row" style="width:100vw;" >
         	
  			<div id="features-list" class="col-sm-3" onmouseleave="clearDetails();" >  
   				<div class="card mb-3 mx_welcome_card scale-bgcolor-white" >    					
				    <div class="card-body " >
				      <p class="card-text mx_welcome_card_title text-center">Control</p>
				      <div id="control.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      
					      	<li class="fa-li fa fa-check scale-color-blue" 
					      	onmouseover="clearDetails();this.querySelector('._details_').style.display='block';" >
						      	<span class="mx_welcome_card_li_contents" >Define data model					      	
						      		<span class="_details_ mx_welcome_card_details" style="display:none;" >Define what fields your documents contain</span>
						      	</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue"
					      				onmouseover="clearDetails();this.querySelector('._details_').style.display='block';" >
					      		<span class="mx_welcome_card_li_contents">
					      			 Customize visualization 
					      			<span class="_details_ mx_welcome_card_details" style="display:none;">Set what and how to display the fields for each type of your documents</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue"
					      			onmouseover="clearDetails();this.querySelector('._details_').style.display='block';" >
					      			<span class="mx_welcome_card_li_contents">Import contents CSV file
					      				<span class="_details_ mx_welcome_card_details" style="display:none;">Import data from standard CSV file</span>
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue"
					      			onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      			<span class="mx_welcome_card_li_contents">Data storage drive
					    				<span class="_details_ mx_welcome_card_details" style="display:none;">Store images, text, or any type of data related to your project and reference it from the documents</span>
					    			</span>
					    	</li>
					      	<li class="fa-li fa fa-check scale-color-blue"
					      			onmouseover="clearDetails();this.querySelector('._details_').style.display='block';" >
					      			<span class="mx_welcome_card_li_contents">Link data items with each other
					      				<span class="_details_ mx_welcome_card_details" style="display:none;">Reference documents from each other to build a connection tree</span>
					      			</span>
					      	</li>					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			  <div class="col-sm-3" onmouseleave="clearDetails();">  
   				<div class="card mb-3 mx_welcome_card scale-bgcolor-white">    					
				    <div class="card-body ">
				      <p class="card-text mx_welcome_card_title text-center">Collaborate</p>
				      <div id="collab.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
				      			<span class="mx_welcome_card_li_contents">All the team can create and use data
				      				<span class="_details_ mx_welcome_card_details" style="display:none;" >Share your work with team or project partners</span>
				      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      		<span class="mx_welcome_card_li_contents">Control who can see, edit or delete data
					      			<span class="_details_ mx_welcome_card_details" style="display:none;" >Assign roles and access rights to each user of your project</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      		<span class="mx_welcome_card_li_contents">Multi-language (French,English,Spanish, more to come ...)
					      			<span class="_details_ mx_welcome_card_details" style="display:none;" >Define lexic for a user interface adapted to your specific domain</span>
					      		</span>
				      		</li>	
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      		<span class="mx_welcome_card_li_contents">Get notified when data has changed
					      			<span class="_details_ mx_welcome_card_details" style="display:none;" >Get lively updated with what is actually modified in the data</span>
					      		</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      		<span class="mx_welcome_card_li_contents">Trace who changed what and when
					      			<span class="_details_ mx_welcome_card_details" style="display:none;" >Find when a document has been changed lastly and by who</span>
					      		</span>
					      	</li>
					      					      	
					      </ul>
				      </div>
				    </div>
				  </div>								 
			  </div>
			  <div class="col-sm-3" onmouseleave="clearDetails();">			  	
			  	 <div class="card mb-3 mx_welcome_card scale-bgcolor-white" >
				    <div class="card-body " >
				      <p class="card-text mx_welcome_card_title text-center">Explore</p>
				      <div id="explore.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      			<span class="mx_welcome_card_li_contents" >Powerful <a class="mx_welcome_card_body_link" href="http://lucene.org">Lucene</a> Search Queries
					      				<span class="_details_ mx_welcome_card_details" style="display:none;" >Rich semantical query language</span>	
					      			</span></li>
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      			<span class="mx_welcome_card_li_contents">Lightning-fast search with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Elastic-Search</a> engine
					      				<span class="_details_ mx_welcome_card_details" style="display:none;" >Efficient and fast search engine</span>
					      			</span>
					      	</li>
					      	<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
					      			<span class="mx_welcome_card_li_contents">Save and combine queries
					      				<span class="_details_ mx_welcome_card_details" style="display:none;" >Keep track of useful queries to be reused or combined</span>
					      			</span>
					      	</li>
					      </ul>
				      </div>
				    </div>
				  </div>
				  
				 
			</div>
			  <div class="col-sm-3" onmouseleave="clearDetails();">			  	
			   	 <div class="card mb-3 mx_welcome_card scale-bgcolor-white" >
				    <div class="card-body " >
				      <p class="card-text mx_welcome_card_title text-center">Compute</p>
				      <div id="compute.body" class="mx_welcome_card_body" >
					      <ul class="mx_welcome_card_ul fa-ul">
				      			<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
				      				<span class="mx_welcome_card_li_contents">Statistics graphs with world-famous <a class="mx_welcome_card_body_link" href="http://elk.org">Kibana</a>
				      					<span class="_details_ mx_welcome_card_details" style="display:none;" >Generate meaningful statistic graphs in few clics</span>
				      				</span>
				      			</li>
				      			<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
				      				<span class="mx_welcome_card_li_contents">Connection graphs with auto-generated VEGA graphs
				      					<span class="_details_ mx_welcome_card_details" style="display:none;" >Control great power of VEGA-graph with dedicated pre-defined templates</span>
				      				</span>
				      			</li>
				      			<li class="fa-li fa fa-check scale-color-blue" onmouseover="clearDetails();this.querySelector('._details_').style.display='block';">
				      				<span class="mx_welcome_card_li_contents">Integrated Kibana panels into your main visualization
				      					<span class="_details_ mx_welcome_card_details" style="display:none;" >Access to updated statistic panels from with the application</span>
				      				</span>
				      			</li> 
				      		</ul>
				      </div>
				    </div>
				  </div>
			  </div>
  
		</div> 
        
         				 
				
				   
	  </div>
	  </center></div>
	  
	</div>


  <!-- Bootstrap core JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery/jquery.min.js"></script>
  <script src="${mxurl}public/commons/deps/bootstrap/js/bootstrap.bundle.min.js"></script>

  <!-- Core plugin JavaScript-->
  <script src="${mxurl}public/commons/deps/jquery-easing/jquery.easing.min.js"></script>

  <!-- Custom scripts for all pages-->
  <script src="${mxurl}public/commons/js/sb-admin-2.min.js"></script>

</body>

</html>
